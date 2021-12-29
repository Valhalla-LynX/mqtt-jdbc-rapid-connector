package com.bjdv.dbconnector.dynamic.datasource;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-20 13:44
 **/
@Slf4j
public class DynamicDataSourceContextHolder {
    /**
     * @description: 线程级别的私有变量
     * @param:
     * @return:
     * @author: LX
     */
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();
    /**
     * @description: 存储已经注册的数据源的key
     * @param:
     * @return:
     * @author: LX
     */
    public static List<String> dataSourceIds = new ArrayList<>();

    public static String getDataSourceRouterKey() {
        return HOLDER.get();
    }

    /**
     * @description:
     * @param: [dataSourceRouterKey]
     * @return: [java.lang.String]
     * @author: LX
     */
    public static void setDataSourceRouterKey(String dataSourceRouterKey) {
        log.debug("切换至{}数据源", dataSourceRouterKey);
        HOLDER.set(dataSourceRouterKey);
    }

    /**
     * @description: 设置数据源之后释放
     * @param: []
     * @return: []
     * @author: LX
     */
    public static void removeDataSourceRouterKey() {
        HOLDER.remove();
    }

    /**
     * @description: 判断指定DataSource当前是否存在
     * @param: [dataSourceId]
     * @return: [java.lang.String]
     * @author: LX
     */
    public static boolean containsDataSource(String dataSourceId) {
        return dataSourceIds.contains(dataSourceId);
    }

}
