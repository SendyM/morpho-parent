package com.max256.morpho.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.max256.morpho.common.cache.redis.RedisManager;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisConfig配置 
 * 包括jedis原生配置和spring data redis配置spring data redis使用springboot自己提供的自动配置
 * 使用springboot原生配置文件
 * @author fbf
 * 
 */
@Configuration
public class RedisConfig {

	@Autowired
	RedisProperties redisProperties;
	
	
/*	<!-- 为了保险起见，加上name和type，防止根据index对应会出问题 -->
	<bean id="jedisPool" class="redis.clients.jedis.JedisPool">
		<constructor-arg index="0" ref="jedisPoolConfig" name="poolConfig" />
		<constructor-arg index="1" value="${redis.host}" name="host"
			type="java.lang.String" />
		<constructor-arg index="2" value="${redis.port}" name="port"
			type="int" />
		<constructor-arg index="3" value="${redis.timeout}"
			name="timeout" type="int" />
		<!-- 如果有密码和 database选择的话放开后边的配置 -->
		<!-- <constructor-arg index="4" value="${redis.password}" name="password" 
			type="java.lang.String"/> <constructor-arg index="5" value="${redis.database}" 
			name="database" type="int"/> -->
	</bean>*/
	//TODO jedis构造注入参数特别多 以后完善
	@Bean(name = "jedisPool")
	@Autowired
	public JedisPool jedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig config) {
		return new JedisPool(config,
				redisProperties.getHost(), 
				redisProperties.getPort(),
				redisProperties.getTimeout()
				);
	}
	@Bean(name = "jedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(redisProperties.getPool().getMaxActive());
		config.setMaxIdle(redisProperties.getPool().getMaxIdle());
		config.setMaxWaitMillis(redisProperties.getPool().getMaxWait());
		config.setMinIdle(redisProperties.getPool().getMinIdle());
		return config;
	}
	
	@Bean(name = "redisManager")
	@Autowired
	public RedisManager redisManager(@Qualifier("jedisPool") JedisPool jedisPool) {
		RedisManager redisManager = new RedisManager();
		redisManager.setJedisPool(jedisPool);
		return redisManager;
	}

}