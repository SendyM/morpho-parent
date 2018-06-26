package com.max256.morpho.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.max256.morpho.common.cache.MethodSignatureCacheKeyGenerator;
import com.max256.morpho.common.util.SpringUtils;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

/**
 * 开启spring缓存支持 和ehcache整合
 * 
 * @author fbf
 * 
 */
@Configuration
@ConditionalOnClass(value = Ehcache.class) // calsspath中存在时配置
public class EhCacheConfig extends CachingConfigurerSupport {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public KeyGenerator keyGenerator() {
		// 指定默认的cache key策略
		return SpringUtils.getBean("methodSignatureCacheKeyGenerator");

	}

	/* Key生成策略配置 */
	@Bean(name = "methodSignatureCacheKeyGenerator")
	public MethodSignatureCacheKeyGenerator methodSignatureCacheKeyGenerator() {
		return new MethodSignatureCacheKeyGenerator();
	}

	@Bean(name = "ehCacheManagerFactory")
	public EhCacheManagerFactoryBean ehCacheManagerFactory() {
		EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		ehCacheManagerFactoryBean.setShared(true);
		/* 加载classpath:ehcache.xml配置文件 */
		ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
		return ehCacheManagerFactoryBean;
	}

	/* ehCacheManager */
	@Bean(name = "ehCacheManager")
	public EhCacheCacheManager ehCacheManager(CacheManager ehCacheManagerFactory) {
		EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
		ehCacheCacheManager.setCacheManager(ehCacheManagerFactory);
		return ehCacheCacheManager;
	}

}
