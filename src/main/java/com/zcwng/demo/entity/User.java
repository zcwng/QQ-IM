package com.zcwng.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="im_user")
public class User {
    @Id@GeneratedValue
    private Long id;
    private String username;
    private String sign;
    private String status;
    private String avatar;
    private Date created;
    private String password;
}
