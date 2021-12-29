package com.bjdv.dbconnector.direct;

import com.bjdv.dbconnector.dynamic.datasource.DynamicDataSourceStringMap;
import com.bjdv.dbconnector.utils.DataSourceGetter;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-02 10:36
 **/
@Slf4j
@Component
public class JDBCDataSourceFactory implements EnvironmentAware {
    /**
     * @description: 别名
     */
    private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();

    static {
        aliases.addAliases("url", "jdbc-url");

    }

    /**
     * @description: 参数绑定工具
     */
    private Binder binder;
    /**
     * @description: 上下文
     */
    private Environment environment;

    @Autowired
    public void initJDBCFactory() {
    }

    public DataSource createJDBCDataSource(String name) throws SQLException {
        DirectDataSourceStringMap directDataSourceStringMap = binder.bind("spring.datasource." + name, DirectDataSourceStringMap.class).get();
        String typeStr = environment.getProperty("spring.datasource." + name + ".type");
        return bind(DataSourceGetter.getDataSourceType(typeStr), directDataSourceStringMap);
    }

    public Map<String,DataSource> createJDBCDataSources() throws SQLException {
        Map<String,DataSource> map = new HashMap<>();
        BindResult<List<DirectDataSourceStringMap>> result = binder.bind("spring.datasource.cluster", Bindable.listOf(DirectDataSourceStringMap.class));
        if (result.isBound()) {
            List<DirectDataSourceStringMap> configs = result.get();
            for (DirectDataSourceStringMap config : configs) {
                DataSource dataSource = bind(DataSourceGetter.getDataSourceType(config.get("type")), config);
                map.put(config.get("key"),dataSource);
            }
        }
        return map;
    }

    private static <T extends DataSource> T bind(Class<T> clazz, DirectDataSourceStringMap properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source.withAliases(aliases));
        // 通过类型绑定参数并获得实例对象
        return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz)).get();
    }

    @Override
    public void setEnvironment(Environment environment) {
        log.info("开始注册数据源");
        this.environment = environment;
        // 绑定配置器
        binder = Binder.get(environment);
    }
}
