package com.max256.morpho.config.web;

import javax.servlet.DispatcherType;

import org.apache.logging.log4j.core.lookup.Log4jLookup;
import org.apache.logging.log4j.web.Log4jServletContextListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.util.IntrospectorCleanupListener;

import com.alibaba.druid.support.http.WebStatFilter;
import com.bstek.ureport.console.UReportServlet;
import com.max256.morpho.common.config.Platforms;
import com.max256.morpho.common.util.SpringUtils;
import com.max256.morpho.common.web.listener.MyContextListener;
import com.max256.morpho.common.web.listener.PathVarServlet;
import com.max256.morpho.config.properties.DruidProperties;
import com.max256.morpho.config.properties.MorphoProperties;

import net.bull.javamelody.SessionListener;

/**
 * web 配置类 代替web.xml
 *
 * @author fbf
 */
@Configuration
public class WebConfig {
	
	//------------------------------Servlet-----------------------------------------
	// druid
	@Bean
	@ConditionalOnProperty(prefix = "spring.datasource", name = "enable-web-admin", havingValue = "true")
	public ServletRegistrationBean druidServletRegistration() {
		DruidProperties druidProperties=SpringUtils.getBean("druidProperties");
		ServletRegistrationBean registration = new ServletRegistrationBean();
		registration.setServlet(new com.alibaba.druid.support.http.StatViewServlet());
		registration.addUrlMappings("/druid/*");
		registration.addInitParameter("allow", "");
		registration.addInitParameter("deny", "");
		/*
		 * 在StatViewSerlvet输出的html页面中，有一个功能是Reset All，
		 * 执行这个操作之后，会导致所有计数器清零，重新计数。你可以通过配置参数关闭它。
		 */
		registration.addInitParameter("resetEnable", "true");
		registration.addInitParameter("loginUsername", druidProperties.getWebAdminUsername());
		registration.addInitParameter("loginPassword", druidProperties.getWebAdminPassword());
		return registration;
	}

	// ureport
	@Bean
	@ConditionalOnClass(value=UReportServlet.class)
	public ServletRegistrationBean ureportServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean();
		registration.setServlet(new com.bstek.ureport.console.UReportServlet());
		registration.addUrlMappings("/ureport/*");
		return registration;
	}

	

	// -------------------------------------filter--------------------------------------
	// gzipFilter
	@Bean
	public FilterRegistrationBean GzipFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
				new net.sf.ehcache.constructs.web.filter.GzipFilter());
		filterRegistrationBean.addUrlPatterns("*.json");
		filterRegistrationBean.addUrlPatterns("*.xml");
		filterRegistrationBean.addUrlPatterns("*.html");
		filterRegistrationBean.addUrlPatterns("*.htm");
		filterRegistrationBean.addUrlPatterns("*.shtml");
		filterRegistrationBean.addUrlPatterns("*.js");
		filterRegistrationBean.addUrlPatterns("*.jsp");
		filterRegistrationBean.addUrlPatterns("*.css");
		/* <!-- 图片不适应gzip压缩反而会变大 --> */
		return filterRegistrationBean;
	}

	// SpringCharacterEncodingFilter
	@Bean
	public FilterRegistrationBean SpringCharacterEncodingFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
				new org.springframework.web.filter.CharacterEncodingFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.addInitParameter("encoding", "UTF-8");
		filterRegistrationBean.addInitParameter("forceEncoding", "true");
		filterRegistrationBean.setAsyncSupported(true);
		return filterRegistrationBean;
	}

	// XSSFilter
	@Bean
	public FilterRegistrationBean XSSFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
				new com.max256.morpho.common.web.filter.XSSFilter());
		MorphoProperties morphoProperties=SpringUtils.getBean("morphoProperties");
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.addInitParameter("encoding", "UTF-8");
		filterRegistrationBean.addInitParameter("forceEncoding", "true");
		filterRegistrationBean.addInitParameter("filterPaths", morphoProperties.getXssFilterPaths());// 需要过滤的url路径
		filterRegistrationBean.setAsyncSupported(true);
		return filterRegistrationBean;
	}

	// log4jServletFilter
	@Bean
	@ConditionalOnProperty(prefix = MorphoProperties.PREFIX, name = "enable-http-request-log", havingValue = "true")
	public FilterRegistrationBean Log4jServletFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
				new org.apache.logging.log4j.web.Log4jServletFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD,
				DispatcherType.INCLUDE, DispatcherType.ERROR);
		return filterRegistrationBean;
	}
	
	// DelegatingFilterProxy
	@Bean
	public FilterRegistrationBean shiroFilterDelegatingFilterProxy() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
				new DelegatingFilterProxy());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setName("shiroFilter");
		filterRegistrationBean.addInitParameter("targetFilterLifecycle", "true");
		filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD,
				DispatcherType.INCLUDE, DispatcherType.ERROR);
		return filterRegistrationBean;
	}
	
	// LogThreadContextFilter
	@Bean
	@ConditionalOnClass(value=Log4jLookup.class)//log4j2专用
	public FilterRegistrationBean LogThreadContextFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(
				new com.max256.morpho.common.web.filter.LogThreadContextFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.addInitParameter("appKey", "系统");
		return filterRegistrationBean;
	}

	// DruidWebStatFilter
	@Bean
	@ConditionalOnProperty(prefix = "spring.datasource", name = "enable-web-admin", havingValue = "true")
	public FilterRegistrationBean druidStatFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
		// 添加过滤规则.
		filterRegistrationBean.addUrlPatterns("/*");
		// 添加不需要忽略的格式信息.
		filterRegistrationBean.addInitParameter("exclusions",
				"/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid,/druid/*");
		filterRegistrationBean.addInitParameter("sessionStatMaxCount", "10000");
		filterRegistrationBean.addInitParameter("sessionStatEnable", "true");
		filterRegistrationBean.addInitParameter("profileEnable", "true");
		// 用于session监控页面的用户名显示 需要登录后主动将username注入到session里
		filterRegistrationBean.addInitParameter("principalSessionName", "sysUserName");
		return filterRegistrationBean;
	}
	// javamelody monitoring
	@Bean
	@ConditionalOnProperty(prefix = MorphoProperties.PREFIX, name = "enable-javamelody", havingValue = "true")
	public FilterRegistrationBean MonitoringFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new net.bull.javamelody.MonitoringFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.addInitParameter("no-database","true");
		filterRegistrationBean.addInitParameter("monitoring-path", "/monitoring");
		filterRegistrationBean.addInitParameter("log", "true");
		filterRegistrationBean.addInitParameter("disabled", "false");
		filterRegistrationBean.addInitParameter("storage-directory", Platforms.TMP_DIR+"javamelody");
		return filterRegistrationBean;
	}
	// ajaxSessionTimeout
	@Bean
	public FilterRegistrationBean ajaxSessionTimeoutFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new com.max256.morpho.common.web.filter.AjaxSessionTimeoutFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
		return filterRegistrationBean;
	}
	// CaptchaFilter产生验证码 在开启验证码开关的情况下产生
	@Bean
	@ConditionalOnProperty(prefix = MorphoProperties.PREFIX, name = "login-enable-captch", havingValue = "true")
	public FilterRegistrationBean CaptchaFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new com.max256.morpho.common.web.filter.CaptchaFilter());
		filterRegistrationBean.addUrlPatterns("/captchacode");
		return filterRegistrationBean;
	}
	// ------------------------------------------监听器--------------------------------------------
	// Log4jServletContextListener
    @Bean
    public ServletListenerRegistrationBean<Log4jServletContextListener> Log4jServletContextListenerRegistration() {
        return new ServletListenerRegistrationBean<>(new Log4jServletContextListener());
    }

	// IntrospectorCleanupListener
    @Bean
    public ServletListenerRegistrationBean<IntrospectorCleanupListener> IntrospectorCleanupListenerRegistration() {
        return new ServletListenerRegistrationBean<>(new IntrospectorCleanupListener());
    }

	// RequestContextListener
    @Bean
    public ServletListenerRegistrationBean<RequestContextListener> RequestContextListenerRegistration() {
        return new ServletListenerRegistrationBean<>(new RequestContextListener());
    }
	//javamelody SessionListener
    @Bean
	@ConditionalOnProperty(prefix = MorphoProperties.PREFIX, name = "enable-javamelody", havingValue = "true")
    public ServletListenerRegistrationBean<SessionListener> SessionListenerRegistration() {
        return new ServletListenerRegistrationBean<>(new SessionListener());
    }
	// MyContextListener
    @Bean
    public ServletListenerRegistrationBean<MyContextListener> MyContextListenerRegistration() {
        return new ServletListenerRegistrationBean<>(new MyContextListener());
    }
    // PathVarServlet 
 	@Bean
 	public ServletListenerRegistrationBean<PathVarServlet> PathVarServletRegistration() {
 		return new ServletListenerRegistrationBean<>(new PathVarServlet());
 	}

	// --------错误页-----------

	// ---------tag-----------
}
