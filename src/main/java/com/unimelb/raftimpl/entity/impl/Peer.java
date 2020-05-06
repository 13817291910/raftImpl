package com.unimelb.raftimpl.entity.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Peer {

    private String host;
    private int port;

}
