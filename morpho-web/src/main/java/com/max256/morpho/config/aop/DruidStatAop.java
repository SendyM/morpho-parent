package com.max256.morpho.config.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;

/**
 * DruidStatAop aop
 *
 * @author fbf
 */
@Configuration
public class DruidStatAop implements Ordered {
	@Value("${spring.aop.proxy-target-class:false}")
	private boolean proxyTargetClass;
	@Bean
	public Advice advice() {
		return new DruidStatInterceptor();
	}
	@Bean
	public Advisor advisor() {
		return new RegexpMethodPointcutAdvisor(new String[]{
				"com.*.*.*.dao.*",
				"com.*.*.*.service.impl.*"
				}, advice());
	}
	@Bean
	public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		advisorAutoProxyCreator.setProxyTargetClass(proxyTargetClass);
		return advisorAutoProxyCreator;
	}
	@Override
	public int getOrder() {
		return 3;
	}
}
