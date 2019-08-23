package com.zcwng.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itshidu.common.ftp.config.FtpPoolConfig;
import com.itshidu.common.ftp.core.FTPClientFactory;
import com.itshidu.common.ftp.core.FTPClientPool;
import com.itshidu.common.ftp.core.FtpClientUtils;
import com.zcwng.demo.dao.GroupDao;
import com.zcwng.demo.dao.UserDao;
import com.zcwng.demo.entity.Group;
import com.zcwng.demo.entity.User;
import com.zcwng.demo.service.HelloService;
import com.zcwng.demo.util.FtpUtil;
import com.zcwng.demo.util.RedisReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.*;

@Service
public class HelloServiceImpl implements HelloService {

    @Autowired
    UserDao userDao;
    @Autowired
    GroupDao groupDao;

    @Autowired FtpUtil ftpUtil;

    @Autowired
    RedisReceiver redisReceiver;

    @Value("${ftp.host}")
    String ftpHost;

    @Override
    public Object init(long userid) {

        File file = new File("d:/001.jpg");
        try {
            ftpUtil.store(file, "/avatar", "hello.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = userDao.findOne(userid);
        if(user==null){
            return "error";
        }

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("code", 0);
        root.put("msg", "");
        ObjectNode data = mapper.createObjectNode();
        root.set("data", data);
        ObjectNode mine = mapper.createObjectNode();
        ArrayNode friend = mapper.createArrayNode();
        ArrayNode group = mapper.createArrayNode();
        data.set("mine", mine);
        data.set("friend", friend);
        data.set("group", group);

        //个人信息
        mine.put("username", user.getUsername());
        mine.put("id", user.getId());
        mine.put("avatar", user.getAvatar());
        mine.put("sign", user.getSign());
        System.out.println(redisReceiver.isUserOnline(user.getId()));
        mine.put("status",redisReceiver.isUserOnline(user.getId())? "online":"offline");

        //friend分组信息
        List<Group> groupList = groupDao.findGroupsByUser(userid);
        groupList.forEach(g->{
            ObjectNode gnode = mapper.createObjectNode();
            gnode.put("id", g.getId());
            gnode.put("groupname", g.getName());
            //friend分组中的好友
            ArrayNode listNode = mapper.createArrayNode();
            gnode.set("list", listNode);
            g.getList().forEach(u -> {
                ObjectNode unode = mapper.createObjectNode();
                unode.put("username", u.getUsername());
                unode.put("id", u.getId());
                unode.put("avatar", u.getAvatar());
                unode.put("sign", u.getSign());
                unode.put("status", redisReceiver.isUserOnline(u.getId())? "online":"offline");
                listNode.add(unode);
            });
            friend.add(gnode);
        });

        try {
            return mapper.writeValueAsString(root);
        }catch (Exception e){

        }

        return null;
    }

    @Override
    public Object createUser(String username,String password, String sign, MultipartFile avatar) {

        User a=userDao.findByUsername(username);
        if(a!=null){
            return "用户名已经存在";
        }

        User user = new User();
        user.setStatus("online");
        user.setCreated(new Date());
        user.setSign(sign);
        user.setUsername(username);
        user.setPassword(password);

        //文件上传到ftp服务器
        String name=avatar.getOriginalFilename();
        String houzhui=name.substring(name.lastIndexOf("."));
        String filename = UUID.randomUUID().toString().replaceAll("-", "")+houzhui;
        try {
            ftpUtil.store(avatar.getInputStream(), "/avatar", filename);
            user.setAvatar("http://"+ftpHost+"/avatar/"+filename);
            userDao.save(user);

            Map<String, Object> data = new HashMap();
            data.put("code", 1);
            data.put("msg", "注册成功");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

}
