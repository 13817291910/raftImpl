package com.unimelb.raftimpl.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Log {
    private long idex;
    private int term;
    private String text;

    public String toString() {
        return "index is " + idex
                + " term is " + term
                + " log is " + text;
    }
}
