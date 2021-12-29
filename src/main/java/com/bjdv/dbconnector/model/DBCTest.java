package com.bjdv.dbconnector.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-21 13:53
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("dbc_test")
public class DBCTest {
    private Integer id;
    private String name;
}
