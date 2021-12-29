package com.bjdv.dbconnector.dynamic.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-20 13:58
 **/
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = DynamicDataSourceContextHolder.getDataSourceRouterKey();
        log.debug("当前数据源是：{}", dataSourceName);
        return dataSourceName;
    }
}
