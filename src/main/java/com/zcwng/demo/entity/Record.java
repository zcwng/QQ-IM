package com.zcwng.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="im_record")
public class Record {
    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Date created;
    @Column(name = "from_user_id")
    private Long from;
    @Column(name = "to_user_id")
    private Long to;
    private Boolean used; //是否已经读取过，true代表读取过，false代表未读

}
