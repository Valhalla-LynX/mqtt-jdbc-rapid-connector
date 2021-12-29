package com.bjdv.dbconnector.dynamic.datasource;

import com.bjdv.dbconnector.utils.DataSourceGetter;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-20 10:28
 **/
@Slf4j
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private static final String MASTER = "master";

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
    /**
     * @description: 存储注册的数据源
     */
    private DataSource defaultDataSource;
    private Map<String, DataSource> customDataSources = new HashMap<>();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取所有数据源配置
        DynamicDataSourceStringMap defaultDataSourceProperties;
        // 默认数据源
        defaultDataSourceProperties = binder.bind("spring.datasource." + MASTER, DynamicDataSourceStringMap.class).get();
        // 获取默认数据源类型
        Class<? extends DataSource> type = DataSourceGetter.getDataSourceType(environment.getProperty("spring.datasource." + MASTER + ".type"));
        // 绑定默认数据源参数
        defaultDataSource = bind(type, defaultDataSourceProperties);
        // 添加名称
        DynamicDataSourceContextHolder.dataSourceIds.add(MASTER);
        log.info("注册默认数据源成功");
        // 获取其他数据源配置
        try {
            BindResult<List<DynamicDataSourceStringMap>> result = binder.bind("spring.datasource.cluster", Bindable.listOf(DynamicDataSourceStringMap.class));
            List<DynamicDataSourceStringMap> configs = result.get();
            for (DynamicDataSourceStringMap config : configs) {
                DataSource consumerDatasource = bind(DataSourceGetter.getDataSourceType(config.get("type")), config);
                String key = config.get("key");
                customDataSources.put(config.get("key"), consumerDatasource);
                DynamicDataSourceContextHolder.dataSourceIds.add(key);
                log.info("注册数据源{}成功", key);
            }
        } catch (NoSuchElementException exception) {
            log.info("未解析到cluster数据源");
        }
        // bean定义类
        GenericBeanDefinition define = new GenericBeanDefinition();
        // 设置bean的类型，此处DynamicRoutingDataSource是继承AbstractRoutingDataSource的实现类
        define.setBeanClass(DynamicRoutingDataSource.class);
        // 需要注入的参数
        MutablePropertyValues mpv = define.getPropertyValues();
        // 添加默认数据源，避免key不存在的情况没有数据源可用
        mpv.add("defaultTargetDataSource", defaultDataSource);
        // 添加其他数据源
        mpv.add("targetDataSources", customDataSources);
        // 将该bean注册为datasource，不使用springboot自动生成的datasource
        registry.registerBeanDefinition("datasource", define);
        log.info("注册数据源成功，一共注册{}个数据源", customDataSources.keySet().size() + 1);
    }

    @Override
    public void setEnvironment(Environment environment) {
        log.info("开始注册数据源");
        this.environment = environment;
        // 绑定配置器
        binder = Binder.get(environment);
    }

    private <T extends DataSource> T bind(Class<T> clazz, DynamicDataSourceStringMap properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source.withAliases(aliases));
        // 通过类型绑定参数并获得实例对象
        return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz)).get();
    }

    private void bind(DataSource result, DynamicDataSourceStringMap properties) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source.withAliases(aliases));
        // 将参数绑定到对象
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(result));
    }

    private <T extends DataSource> T bind(Class<T> clazz, String sourcePath) {
        DynamicDataSourceStringMap properties = binder.bind(sourcePath, DynamicDataSourceStringMap.class).get();
        return bind(clazz, properties);
    }

}
