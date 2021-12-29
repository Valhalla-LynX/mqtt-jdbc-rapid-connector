package com.bjdv.dbconnector.dynamic.service;

import com.bjdv.dbconnector.dynamic.mapper.CheckMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-17 16:32
 **/
//@Service
public class CheckService {
    private CheckMapper checkMapper;

    @Autowired
    public void initCheckService(CheckMapper checkMapper) {
        this.checkMapper = checkMapper;
    }

    public boolean checkTable(String name) {
        try {
            checkMapper.checkTable(name);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
