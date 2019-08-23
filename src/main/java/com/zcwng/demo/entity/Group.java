package com.zcwng.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="im_group")
public class Group {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Date created;

    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;  //此分组的主人

    @ManyToMany()
    @JoinTable(name="im_group_user",
            joinColumns = {@JoinColumn(name="group_id")}, //连接本类
            inverseJoinColumns = {@JoinColumn(name="user_id")})//连接此属性的类
    private List<User> list; //分组中的好友
}
