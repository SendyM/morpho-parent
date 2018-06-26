package com.max256.morpho.common.web.listener;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.max256.morpho.common.util.PasswordUtils;
import com.max256.morpho.config.properties.MorphoProperties;
import com.max256.morpho.config.properties.ShiroProperties;

/**
 * 自定义spring容器启动监听器 功能:当spring 核心容器加载完成时打印系统相关环境信息
 * 
 * @author fbf
 * 
 */
@Component("springContextListener")
public class SpringContextListener implements ApplicationListener<ContextRefreshedEvent> {


	@Resource
	private MorphoProperties morphoProperties;

	@Resource
	private ShiroProperties shiroProperties;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			//覆盖常量

			// 读取配置文件 设置系统名字
			com.max256.morpho.common.config.Constants.SYS_NAME = morphoProperties.getSystemSysname();

			// 读取配置文件 设置系统版权信息
			com.max256.morpho.common.config.Constants.SYS_COPYRIGHT = morphoProperties.getSystemCopyright();

			// 读取配置文件 设置密码解密工具类的配置和配置文件中的一致
			PasswordUtils.algorithmName = shiroProperties.getCredentialsMatcherHashAlgorithmName();

			PasswordUtils.hashIterations = shiroProperties.getCredentialsMatcherHashIterations();

			System.out.println(
					"-------------------以下是系统环境信息：-----------------------------------------------------------------------------------------------");
			Map<String, String> map = System.getenv();
			for (Iterator<String> itr = map.keySet().iterator(); itr.hasNext();) {
				String key = itr.next();
				System.out.println(key + "=" + map.get(key));
			}
			System.out.println(
					"-------------------以下是系统属性信息：-----------------------------------------------------------------------------------------------");
			Properties props = System.getProperties();
			props.list(System.out);

		}

	}

}
