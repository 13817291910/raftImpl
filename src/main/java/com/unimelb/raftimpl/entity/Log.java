package com.unimelb.raftimpl.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Log {
    private long index;
    private int term;
    private String log;
}