package com.unimelb.raftimpl.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PeerConfig {

    private int selfPort;

    private Set<String> peers;

}
