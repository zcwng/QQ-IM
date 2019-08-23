package com.zcwng.demo.controller;

import com.zcwng.demo.dao.UserDao;
import com.zcwng.demo.entity.User;
import com.zcwng.demo.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class PublicController {

    @Autowired
    UserDao userDao;

    @Autowired
    HelloService helloService;

    @PostMapping("/public/login")
    public Object login(String username,String password, HttpSession session){

        User user = userDao.findByUsername(username);
        if(user==null){
            return "redirect:/login.html";
        }
        if(!user.getPassword().equals(password)){
            return "redirect:/login.html";
        }
        session.setAttribute("UserInfo",user);
        return "redirect:/user/home";
    }

    @PostMapping("/public/register")
    public Object createUser(String username,String password, String sign, MultipartFile avatar) {
        helloService.createUser(username,password, sign, avatar);
        return "redirect:/login.html";
    }

}
