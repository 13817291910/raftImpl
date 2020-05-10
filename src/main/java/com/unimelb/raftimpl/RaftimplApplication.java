package com.unimelb.raftimpl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = {"com.unimelb.raftimpl.dao"})
public class RaftimplApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaftimplApplication.class, args);
    }

}
