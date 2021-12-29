package com.bjdv.dbconnector.dynamic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjdv.dbconnector.model.DBCTest;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-17 16:28
 **/
@Repository
public interface CheckMapper extends BaseMapper<DBCTest> {
    String checkTable = "Select COUNT(*) From ${name} Limit 1";

    @Select(checkTable)
    int checkTable(String name);
}
