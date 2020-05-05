package com.unimelb.raftimpl.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/**
*@Description: This class is a unified encapsulation of the results
                returned to the front end
*@Author: di kan
*@Date: 2020/4/25
 */
public class CommonMsg {

    public int code;
    public boolean success;
    public String msg;
    public Object data;

}
