package com.zcwng.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface HelloService {
    Object init(long userid);

    Object createUser(String username, String password, String sign, MultipartFile avatar);
}
