package com.zcwng.demo.controller;

import com.zcwng.demo.dao.GroupDao;
import com.zcwng.demo.dao.UserDao;
import com.zcwng.demo.entity.Group;
import com.zcwng.demo.entity.User;
import com.zcwng.demo.util.RedisReceiver;
import com.zcwng.demo.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserDao userDao;
    @Autowired
    GroupDao groupDao;
    @Autowired
    RedisReceiver redisReceiver;

    @GetMapping("/user/home")
    public Object home(ModelAndView mav, HttpSession session){

        User info = (User) session.getAttribute("UserInfo");
        if(info==null){
            return "redirect:/login.html";
        }

        mav.setViewName("home");
        return mav;
    }
    @ResponseBody
    @PostMapping("/user/update/sign")
    public Object updateSign(HttpSession session,String sign){

        User info = (User) session.getAttribute("UserInfo");
        if(info==null){
            return Result.of(0, "未登录");
        }
        User user = userDao.findOne(info.getId());
        user.setSign(sign);
        userDao.save(user);
        redisReceiver.sendToFollwMe(user.getId(),Result.of()
                .put("type","updateSign")
                .put("sign",sign)
                .put("userid",user.getId()));
        return Result.of(200,"签名修改成功");
    }

    @ResponseBody
    @PostMapping("/user/friend/add/all")
    public Object addAllFriend(HttpSession session){

        User info = (User) session.getAttribute("UserInfo");
        if(info==null){
            return Result.of(0, "未登录");
        }

        User user = userDao.findOne(info.getId());

        Group group=null;
        List<Group> groups = groupDao.findGroupsByUser(info.getId());
        if(groups==null||groups.size()==0){
            group = new Group();
            group.setCreated(new Date());
            group.setUser(user);
            group.setName("我的好友");
            groupDao.save(group);
        }else{
            group = groups.get(0);
        }

        List<User> list = userDao.findAll();
        group.setList(list);
        groupDao.save(group);

        return Result.of(200,"success");
    }

}
