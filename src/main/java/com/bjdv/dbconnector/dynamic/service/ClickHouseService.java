package com.bjdv.dbconnector.dynamic.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjdv.dbconnector.dynamic.datasource.DataSource;
import com.bjdv.dbconnector.dynamic.mapper.ClickHouseMapper;
import com.bjdv.dbconnector.model.DBCTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-21 13:57
 **/
//@Service
public class ClickHouseService extends ServiceImpl<ClickHouseMapper, DBCTest> {
    @DataSource
    public void addOne() {
        save(new DBCTest(1, "hello"));
    }

    @DataSource
    @Transactional
    public void add(int n) {
        List<DBCTest> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(new DBCTest(1, "hello"));
        }
        saveBatch(list);
    }

    @DataSource
    @Transactional
    public void add(List<DBCTest> list) {
        saveBatch(list);
    }

    @DataSource
    public long countTest() {
        return count();
    }
}
