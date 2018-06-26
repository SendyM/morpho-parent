package com.max256.morpho.config.web;

import java.util.List;
import java.util.Properties;

import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import com.max256.morpho.common.exception.MyExceptionResolver;
import com.max256.morpho.common.security.shiro.bind.method.CurrentUserMethodArgumentResolver;
import com.max256.morpho.common.security.shiro.bind.method.RequestMessageMethodArgumentResolver;
import com.max256.morpho.common.util.ApplicationContextUtils;

/**
 * SpringMvcConfig配置类
 *
 * @author fbf
 */
@Configuration
@ComponentScans({//只扫描springMVC层容器 不扫描spring core容器
		@ComponentScan( value="com.max256", useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(org.springframework.stereotype.Controller.class),
				@ComponentScan.Filter(ControllerAdvice.class) }, excludeFilters = {
						@ComponentScan.Filter(Service.class), @ComponentScan.Filter(Repository.class), @ComponentScan.Filter(Configuration.class) }),
		@ComponentScan("cn.afterturn.easypoi.view") })
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class SpringMvcConfig extends WebMvcConfigurerAdapter {

	/* beanNameViewResolver设置为优先级最高*/
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
		beanNameViewResolver.setOrder(0);
		registry.viewResolver(beanNameViewResolver);
		super.configureViewResolvers(registry);
	}

	// TODO
	// <!-- 跨域设置spring4.2.0以上版本支持 默认开启 如果您不需要跨域比如如果不是前后端分离并分开部署的话 您没必要打开跨域
	// 请根据您的实际情况和安全考虑合理控制跨域设置 -->
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		CorsRegistration addMapping = registry.addMapping("/**");
		addMapping.allowedOrigins("*");
		addMapping.allowCredentials(true);
		addMapping.maxAge(1800);
		addMapping.allowedMethods(new String[] { "GET", "POST", "OPTIONS" });
		super.addCorsMappings(registry);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		ResourceHandlerRegistration addResourceHandler = registry.addResourceHandler("/api/**");
		addResourceHandler.addResourceLocations("/WEB-INF/springfox/");
		super.addResourceHandlers(registry);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new CurrentUserMethodArgumentResolver());
		argumentResolvers.add(new RequestMessageMethodArgumentResolver());
		super.addArgumentResolvers(argumentResolvers);
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
		super.configureDefaultServletHandling(configurer);
	}

	/* <!-- SpringMVC上传文件时，需要配置MultipartResolver处理器 --> */
	// TODO 做成可配置
	@Bean("multipartResolver")
	public CommonsMultipartResolver ureportServletRegistration() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setDefaultEncoding("utf-8");
		commonsMultipartResolver.setMaxInMemorySize(40960);
		commonsMultipartResolver.setMaxUploadSize(2000000000000L);
		commonsMultipartResolver.setMaxUploadSizePerFile(20000000000L);
		return commonsMultipartResolver;
	}

	/*
	 * <!-- SpringMVC在超出上传文件限制时，
	 * 会抛出org.springframework.web.multipart.MaxUploadSizeExceededException -->
	 * <!-- 该异常是SpringMVC在检查上传的文件信息时抛出来的，而且此时还没有进入到Controller方法中 -->
	 */
	@Bean("exceptionResolver")
	public org.springframework.web.servlet.handler.SimpleMappingExceptionResolver SimpleMappingExceptionResolver() {
		org.springframework.web.servlet.handler.SimpleMappingExceptionResolver simpleMappingExceptionResolver = new org.springframework.web.servlet.handler.SimpleMappingExceptionResolver();
		/*
		 * <!-- 遇到MaxUploadSizeExceededException异常时，自动跳转到/WEB-INF/jsp/
		 * error_fileupload.jsp页面 -->
		 */
		Properties property = new Properties();
		property.setProperty("org.springframework.web.multipart.MaxUploadSizeExceededException", "error_fileupload");
		simpleMappingExceptionResolver.setExceptionMappings(property);
		return simpleMappingExceptionResolver;
	}
	
	//shiro整合springMVC
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(org.apache.shiro.mgt.SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);;
		return authorizationAttributeSourceAdvisor;
	}
	//aop
	@Bean
	public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		advisorAutoProxyCreator.setProxyTargetClass(true);
		return advisorAutoProxyCreator;
	}
	
	//applicationContextUtils
	@Bean("applicationContextUtils")
	public ApplicationContextUtils applicationContextUtils() {
		ApplicationContextUtils applicationContextUtils = new ApplicationContextUtils();
		return applicationContextUtils;
	}
	
	@Bean("myExceptionResolver")
	public MyExceptionResolver myExceptionResolver() {
		MyExceptionResolver myExceptionResolver = new MyExceptionResolver();
		return myExceptionResolver;
	}
}
