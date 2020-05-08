package com.unimelb.raftimpl.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Getter
@Setter
@Configuration
public class PeerConfig {

    @Value("${self.ip}")
    private String selfIp;

    @Value("${self.port}")
    private int selfPort;

    @Value("${peers.ip}")
    private String[] peersIp;

    @Value("${peers.port}")
    private int[] peersPort;

}
