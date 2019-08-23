package com.zcwng.demo.config;

import com.itshidu.common.ftp.config.FtpPoolConfig;
import com.itshidu.common.ftp.core.FTPClientFactory;
import com.itshidu.common.ftp.core.FTPClientPool;
import com.zcwng.demo.util.FtpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServerConfig {
    @Value("${ftp.host}")
    String ftpHost;
    @Value("${ftp.username}")
    String ftpUsername;
    @Value("${ftp.password}")
    String ftpPassword;

    @Bean
    public FtpUtil ftpUtil(){
        FtpPoolConfig cfg = new FtpPoolConfig();
        cfg.setHost(ftpHost);
        cfg.setPort(21);
        cfg.setUsername(ftpUsername);
        cfg.setPassword(ftpPassword);

        FTPClientFactory factory = new FTPClientFactory(cfg);//对象工厂
        FTPClientPool pool = new FTPClientPool(factory);//连接池对象
        //FtpClientUtils util = new FtpClientUtils(pool); //工具对象
        FtpUtil util = new FtpUtil(pool);
        return util;
    }
}
