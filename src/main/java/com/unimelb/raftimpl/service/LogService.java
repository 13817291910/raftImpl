package com.unimelb.raftimpl.service;

import com.unimelb.raftimpl.dao.LogDao;
import com.unimelb.raftimpl.entity.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogDao logDao;

    public List<Log> selectAllLog() {
        return logDao.findAllLog();
    }

    public void insertService() {
        logDao.insertLog(1, 1, "jstql");
    }
}
