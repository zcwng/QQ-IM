package com.zcwng.demo.controller;

import com.zcwng.demo.dao.GroupDao;
import com.zcwng.demo.dao.UserDao;
import com.zcwng.demo.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.applet.Main;

@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    @Autowired
    UserDao userDao;

    @GetMapping("/user/{userid}/init.json")
    public Object init(@PathVariable Long userid){
        return helloService.init(userid);
    }

    @GetMapping("/test/{userid}")
    public Object test(@PathVariable Long userid){
        return userDao.findByWhoFollwMe(userid);
    }



}
