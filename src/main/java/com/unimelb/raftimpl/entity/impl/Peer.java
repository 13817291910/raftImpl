package com.unimelb.raftimpl.entity.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Peer {

    private String host;
    private int port;

}
