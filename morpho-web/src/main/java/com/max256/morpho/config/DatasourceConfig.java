package com.max256.morpho.config;

import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.max256.morpho.common.mutidatasource.Constants;
import com.max256.morpho.common.mutidatasource.DynamicDataSource;
import com.max256.morpho.common.mutidatasource.config.MutiDataSourceProperties;
import com.max256.morpho.common.util.SpringUtils;
import com.max256.morpho.config.properties.DruidProperties;

/**
 * druid数据源配置 配置aop事务管理
 * 
 * @author fbf
 * 
 */
@Configuration
@EnableTransactionManagement(order = 2) // 由于引入多数据源，所以让spring事务的aop要在多数据源切换aop的后面
@MapperScan(basePackages = { "*.*.*.**.dao" }) // mapper接口扫描
public class DatasourceConfig {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	// durid配置文件
	@Autowired
	DruidProperties druidProperties;
	// 多数据源配置文件
	@Autowired
	MutiDataSourceProperties mutiDataSourceProperties;

	/**
	 * 另一个数据源
	 */
	private DruidDataSource otherDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		druidProperties.config(dataSource);
		mutiDataSourceProperties.config(dataSource);
		return dataSource;
	}

	/**
	 * 主数据源
	 */
	private DruidDataSource masterDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		druidProperties.config(dataSource);
		return dataSource;
	}

	/**
	 * 单数据源连接池配置
	 */
	@Bean(name = "druidDataSource")
	@ConditionalOnProperty(prefix = "morpho", name = "muti-datasource-open", havingValue = "false")
	public DruidDataSource singleDatasource() {
		return masterDataSource();
	}

	/**
	 * 多数据源连接池配置
	 */
	@Bean(name = "druidDataSource")
	@ConditionalOnProperty(prefix = "morpho", name = "muti-datasource-open", havingValue = "true")
	public DynamicDataSource mutiDataSource() {

		DruidDataSource dataSourceMaster = masterDataSource();
		DruidDataSource otherDataSource = otherDataSource();

		try {
			dataSourceMaster.init();
			otherDataSource.init();
		} catch (SQLException sql) {
			sql.printStackTrace();
			throw new Error("初始化多数据源时失败");
		}
		// 动态数据源
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		hashMap.put(Constants.DATA_SOURCE_MASTER, dataSourceMaster);
		hashMap.put(Constants.DATA_SOURCE_OTHER, otherDataSource);
		dynamicDataSource.setTargetDataSources(hashMap);
		dynamicDataSource.setDefaultTargetDataSource(dataSourceMaster);
		return dynamicDataSource;
	}

	// 事务配置数据源事务管理器
	@Bean(name = "transactionManager")
	@ConditionalOnBean(DataSource.class)
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource druidDataSource) {
		return new DataSourceTransactionManager(druidDataSource);
	}

	// druid数据源监控配置
	@Bean(name = "stat-filter")
	public StatFilter statFilter() {
		StatFilter statFilter = new StatFilter();
		statFilter.setSlowSqlMillis(druidProperties.getSlowSqlMillis());
		statFilter.setLogSlowSql(druidProperties.getLogSlowSql());
		statFilter.setMergeSql(druidProperties.getMergeSql());
		return statFilter;
	}

	@Bean(name = "wall-filter")
	public WallFilter wallFilter() {
		WallFilter wallFilter = new WallFilter();
		wallFilter.setConfig(SpringUtils.getBean("wallConfig"));
		return wallFilter;
	}

	@Bean(name = "wallConfig")
	public WallConfig wallConfig() {
		WallConfig wallConfig = new WallConfig();
		wallConfig.setMultiStatementAllow(true);
		return wallConfig;
	}

	@Bean(name = "druid-stat-interceptor")
	public DruidStatInterceptor druidStatInterceptor() {
		DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();
		return druidStatInterceptor;
	}

}
