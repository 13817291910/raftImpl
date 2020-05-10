package com.unimelb.raftimpl.controller;

import com.unimelb.raftimpl.entity.CommonMsg;
import com.unimelb.raftimpl.entity.Log;
import com.unimelb.raftimpl.entity.LogModule;
import com.unimelb.raftimpl.entity.impl.Node;
import com.unimelb.raftimpl.rpc.LogEntry;
import com.unimelb.raftimpl.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private Node node;

    @Autowired
    private LogModule logModule;

    @Autowired
    private LogService logService;

    @RequestMapping("/send")
    CommonMsg send(@RequestParam String text){
        log.info("the front end send the text {}",text);
            try {
                LogEntry curEntry = new LogEntry();
                curEntry.setText(text);
                CommonMsg msg = node.handleRequest(curEntry);
                return msg;
            } catch (Exception e) {
                log.error("the send request fails");
                e.printStackTrace();
                return CommonMsg.builder().success(false)
                        .build();
            }
    }

    @RequestMapping("/read")
    CommonMsg read(){
        String text = LogModule.logEntryList.toString();
        CommonMsg msg = CommonMsg.builder().msg(text).build();
        return msg;
    }



    @RequestMapping("/find")
    public String testFind() {
        List<Log> logList =  logService.selectAllLog();
        String s = null;
        for (Log log : logList) {
            s = s + log.toString();
        }
        return s;
    }

    @RequestMapping("/insert")
    public String insertIt() {
        try {
            logService.insertService();
            return "true";
        } catch (Exception e) {
            return "false";
        }
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
}
