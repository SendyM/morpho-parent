package com.max256.morpho.config;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.util.StringUtils;

import com.max256.morpho.common.util.SpringUtils;
import com.max256.morpho.config.properties.MorphoProperties;

import net.bull.javamelody.MonitoringSpringAdvisor;

/**
 * JavamelodyConfig整合spring
 * @author fbf
 * 
 */
@Configuration
@ImportResource(locations = { "classpath:net/bull/javamelody/monitoring-spring-aspectj.xml" })
@ConditionalOnProperty(prefix = "morpho", name = "enable-javamelody", havingValue = "true")
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class JavamelodyConfig {
	@Bean(name = "facadeMonitoringAdvisor")
	public MonitoringSpringAdvisor facadeMonitoringAdvisor() {
		MorphoProperties morphoProperties=(MorphoProperties)SpringUtils.getBean("morphoProperties");
		MonitoringSpringAdvisor monitoringSpringAdvisor = new MonitoringSpringAdvisor();
		Pointcut pointcut = new JdkRegexpMethodPointcut() {
			private static final long serialVersionUID = 1L;
			@Override
			public void setPattern(String pattern) {
				if(StringUtils.isEmpty(morphoProperties.getJavamelodyAopRegExp())){
					/* <!--正则表达式 com.max256.*.* 意思是对应com.max256.*.*包下的所有类的所有方法--> */
					setPatterns("com.max256.*.*");
				}else{
					String[] str={"com.max256.*.*",morphoProperties.getJavamelodyAopRegExp()};
					setPatterns(str);
				}
			}
		};
		monitoringSpringAdvisor.setPointcut(pointcut);
		return monitoringSpringAdvisor;
	}
}
