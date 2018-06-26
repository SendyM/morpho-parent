package com.max256.morpho.common.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import com.max256.morpho.common.config.Constants;

/**
 * 自定义增强servlet listener监听器 可 以在这里根据需求做扩展 由于本系统的session已经由shiro控制，
 * 所以session的监听部分已经失效 如果需要请配置shiro的session监听器
 * (默认以开启com.max256.morpho.common.security.shiro.ShiroSessionListener)
 * 
 * @author fbf
 * 
 */
public class MyContextListener implements ServletContextListener, InitializingBean, ServletContextAware{
	// 日志
	private static final Logger logger = LoggerFactory.getLogger(MyContextListener.class);

	/* 通过实现ServletContextAware可获得servletContext */
	private ServletContext servletContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("afterPropertiesSet");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (logger.isInfoEnabled()) {
			logger.info("ServletContextEvent事件:系统已经关闭");
		}
	}

	/*
	 * do sth
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		servletContext = sce.getServletContext();
		if (logger.isInfoEnabled()) {
			logger.info("ServletContextEvent事件:系统已经启动");
		}
		// 系统常量加入到servletContext
		servletContext.setAttribute("sysname", Constants.SYS_NAME);// 系统名称
		servletContext.setAttribute("copyright", Constants.SYS_COPYRIGHT);// 版权信息
	}

	public ServletContext getServletContext() {
		return servletContext;
	}
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
