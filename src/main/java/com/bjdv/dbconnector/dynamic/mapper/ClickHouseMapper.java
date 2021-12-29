package com.bjdv.dbconnector.dynamic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjdv.dbconnector.model.DBCTest;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-20 15:43
 **/
@Repository
public interface ClickHouseMapper extends BaseMapper<DBCTest> {
    String Insert_Test = " INSERT INTO dbc_test  ( id,name )  VALUES  ( 1,'hello' )";

    @Select(Insert_Test)
    int insertTest();

}
