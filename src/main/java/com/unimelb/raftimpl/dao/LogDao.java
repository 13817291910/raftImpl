package com.unimelb.raftimpl.dao;

import com.unimelb.raftimpl.entity.Log;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
//@Repository
public interface LogDao {

    @Select("SELECT * FROM log")
    List<Log> findAllLog();

    @Insert("INSERT INTO log VALUES(#{index}, #{term}, #{log})")
    void insertLog(@Param("index") int index, @Param("term") Integer age, @Param("log") String log);
}
