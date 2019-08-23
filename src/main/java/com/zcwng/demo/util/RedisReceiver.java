package com.zcwng.demo.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zcwng.demo.dao.RecordDao;
import com.zcwng.demo.dao.UserDao;
import com.zcwng.demo.entity.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Component
public class RedisReceiver {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    UserDao userDao;
    @Autowired
    RecordDao recordDao;

    public static class SessionInfo {
        public Session session;
        public long userid;
        public SessionInfo(Session session, long userid) {
            this.session = session;
            this.userid = userid;
        }
    }

    ObjectMapper objectMapper = new ObjectMapper();

    public List<SessionInfo> list = new LinkedList();

    public void onOpen(Session session, long userid){
        if(isUserOnline(userid)){
            try {
                System.out.println("已经在线");
                send(session,"已经在线");
                session.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        synchronized (list) {
            list.add(new SessionInfo(session,userid));
            System.out.println("有人上线了："+list.size());
            stringRedisTemplate.opsForValue().setBit("ONLINE", userid, true);
            sendOnlineOrOffline(userid,"online");
        }

        //推送离线消息
        List<Record> records=recordDao.findUnread(userid);
        records.forEach(record -> {
            try {
                sendToUserid(userid,record.getContent());
                record.setUsed(true);
                recordDao.save(record);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void onClose(Session session) {
        synchronized (list){
            for (int i = 0; i < list.size(); i++) {
                SessionInfo info = list.get(i);
                if(info.session.getId().equals(session.getId())){
                    stringRedisTemplate.opsForValue().setBit("ONLINE", info.userid, false); //标记为不在线
                    sendOnlineOrOffline(info.userid,"offline");
                    list.remove(info);
                    break;
                }
            }
            System.out.println("有人下线了："+list.size());
        }
    }

    public void sendOnlineOrOffline(long userid, String status) {
        //通知大家
        userDao.findByWhoFollwMe(userid).forEach(user -> {
            if(!isUserOnline(user.getId()))return;
            for(int n=0;n<list.size();n++){
                SessionInfo si = list.get(n);
                if(si.userid==user.getId()){
                    send(si.session,Result.of()
                            .put("type",status)
                            .put("userid",userid)
                            .toString());
                }
            }
        });
    }
    /** 发消息给关注我的人，或者说是好友列表中包含我的人 */
    public void sendToFollwMe(long userid,Result result) {
        //通知大家
        userDao.findByWhoFollwMe(userid).forEach(user -> {
            if(!isUserOnline(user.getId()))return;
            for(int n=0;n<list.size();n++){
                SessionInfo si = list.get(n);
                if(si.userid==user.getId()){
                    send(si.session,result.toString());

                }
            }
        });
    }

    public void onMessage(String message, Session session) {
        System.out.println(message);
        try {
            JsonNode root = objectMapper.readTree(message);
            ObjectNode data= (ObjectNode) root.get("data");
            data.put("timestamp", new Date().getTime());
            String str = objectMapper.writeValueAsString(root);
            stringRedisTemplate.convertAndSend("chat",str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Session session, String text) {
        try {
            session.getAsyncRemote().sendText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isUserOnline(long userid) {
        return stringRedisTemplate.opsForValue().getBit("ONLINE", userid);
    }

    /**接收消息的方法*/
    public void receiveMessage(String message){
        System.out.println("收到一条消息："+message);
        try {
            JsonNode root = objectMapper.readTree(message);
            long to=root.get("to").asLong();

            sendToUserid(to,message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToUserid(long userid,String message) throws Exception{
        JsonNode root = objectMapper.readTree(message);
        long to=root.get("to").asLong();
        long from=root.get("from").asLong();
        Date date = new Date();
        //设置消息时间
        ObjectNode data= (ObjectNode) root.get("data");
        data.put("timestamp",date.getTime());

        String str = objectMapper.writeValueAsString(root);

        Record r = new Record();
        r.setFrom(from);
        r.setTo(to);
        r.setCreated(date);
        r.setContent(str);

        if(isUserOnline(userid)){ //在线
            synchronized (list){
                list.forEach(info->{
                    if(info.userid==to){
                        send(info.session,str);
                        r.setUsed(true);
                        recordDao.save(r);
                    }
                });
            }
        }else{ //离线
            r.setUsed(false);
            recordDao.save(r);
        }
    }

    //@Scheduled(fixedRate = 100000)
    public void refreshRedisOnlineInfo(){
        System.out.println("刷新redis在线数据(仅限于单机服务器，集群环境下会出错)");
        synchronized (list){
            stringRedisTemplate.delete("ONLINE");
            for (int i = 0; i < list.size(); i++) {
                SessionInfo info = list.get(i);
                stringRedisTemplate.opsForValue().setBit("ONLINE", info.userid, true);
            }
        }
    }

}
