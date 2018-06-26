package com.max256.morpho.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.ValidatingSessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.session.mgt.quartz.QuartzSessionValidationScheduler;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.max256.morpho.common.security.shiro.RetryLimitHashedCredentialsMatcher;
import com.max256.morpho.common.security.shiro.ShiroDbRealm;
import com.max256.morpho.common.security.shiro.ShiroSessionListener;
import com.max256.morpho.common.security.shiro.cache.ehcache.EhCacheShiroCacheManager;
import com.max256.morpho.common.security.shiro.cache.redis.RedisShiroCacheManager;
import com.max256.morpho.common.security.shiro.filter.CaptchaValidateFilter;
import com.max256.morpho.common.security.shiro.filter.EncryptFilter;
import com.max256.morpho.common.security.shiro.filter.ForceLogoutFilter;
import com.max256.morpho.common.security.shiro.filter.MyFormAuthenticationFilter;
import com.max256.morpho.common.security.shiro.filter.SysUserFilter;
import com.max256.morpho.common.security.shiro.service.ChainDefinitionService;
import com.max256.morpho.common.security.shiro.service.impl.ChainDefinitionServiceImpl;
import com.max256.morpho.common.security.shiro.session.ShiroSessionDAO;
import com.max256.morpho.common.security.shiro.session.ehcache.EhCacheShiroSessionRepository;
import com.max256.morpho.common.security.shiro.session.redis.RedisShiroSessionRepository;
import com.max256.morpho.common.util.SpringUtils;
import com.max256.morpho.config.properties.MorphoProperties;
import com.max256.morpho.config.properties.ShiroProperties;

/**
 * shiro配置
 *
 * @author fbf
 */
@Configuration
public class ShiroConfig {
	
	/**
	 * Shiro生命周期处理器: 用于在实现了Initializable接口的Shiro
	 * bean初始化时调用Initializable接口回调(例如:UserRealm) 在实现了Destroyable接口的Shiro
	 * bean销毁时调用 Destroyable接口回调(例如:DefaultSecurityManager)
	 */
	@Bean("lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 自定义的Realm
	 * 
	 * @param credentialsMatcher
	 * @return
	 */
	@Bean("shiroDbRealm")
	public ShiroDbRealm shiroDbRealm() {
		ShiroDbRealm shiroDbRealm = new ShiroDbRealm();
		/* <!-- 注入凭证管理器 --> */
		
		shiroDbRealm.setCredentialsMatcher(SpringUtils.getBean("credentialsMatcher"));
		/*
		 * <!-- 以下是认证和授权时是否使用缓存的设置 --> <!-- 对realm shiro默认不启用缓存 ，在这里显式地写出来 -->
		 * <property name="cachingEnabled" value="true" />
		 */
		shiroDbRealm.setCachingEnabled(true);
		/*
		 * <!-- 启用身份验证缓存，即缓存AuthenticationInfo信息，默认false --> <property
		 * name="authenticationCachingEnabled" value="false" />
		 */
		shiroDbRealm.setAuthenticationCachingEnabled(false);
		/*
		 * <!-- 缓存AuthenticationInfo信息的缓存名称 --> <property
		 * name="authenticationCacheName" value="authenticationCache" />
		 */
		shiroDbRealm.setAuthenticationCacheName("authenticationCache");
		/*
		 * <!-- 启用授权缓存，即缓存AuthorizationInfo信息，默认false --> <property
		 * name="authorizationCachingEnabled" value="true" />
		 */
		shiroDbRealm.setAuthorizationCachingEnabled(true);
		/*
		 * <!-- 缓存AuthorizationInfo信息的缓存名称 --> <property
		 * name="authorizationCacheName" value="authorizationCache" />
		 */
		shiroDbRealm.setAuthorizationCacheName("authorizationCache");
		return shiroDbRealm;
	}

	/*
	 * <!--安全管理器（shiro的核心） --> <bean id="securityManager"
	 * class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
	 * <!--设置自定义Realm --> <!-- 这里主要是设置自定义的单Realm应用,若有多个Realm,可使用'realms'属性代替 -->
	 * <property name="realm" ref="shiroDbRealm" /> <!--将session管理器注入给安全管理器 -->
	 * <property name="sessionManager" ref="sessionManager" /> <!-- 使用下面配置的缓存管理器
	 * --> <!-- 设置SecurityManager的cacheManager，
	 * 会自动设置实现了CacheManagerAware接口的相应对象，这里使用redis作为分布式缓存的缓存管理器 --> <property
	 * name="cacheManager" ref="cacheManager" /> <!--
	 * 设置securityManager安全管理器的rememberMeManager； --> <!-- <property
	 * name="rememberMeManager" ref="rememberMeManager"/> --> </bean>
	 */

	/**
	 * 安全管理器
	 */
	@Bean("securityManager")
	public DefaultWebSecurityManager securityManager(
			/* CookieRememberMeManager rememberMeManager, */
			 ) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(SpringUtils.getBean("shiroDbRealm"));
		ShiroProperties shiroProperties = SpringUtils.getBean("shiroProperties");
		if(shiroProperties.getEnableRedisSession()){
			securityManager.setCacheManager(SpringUtils.getBean("redisShiroCacheManager"));
		}else{
			securityManager.setCacheManager(SpringUtils.getBean("cacheManager"));
		}
		/* securityManager.setRememberMeManager(rememberMeManager); */
		securityManager.setSessionManager(SpringUtils.getBean("sessionManager"));
		return securityManager;
	}


	/**
	 * session管理器(单机环境) ehcache中存储
	 */
	@Bean("sessionManager")
	@ConditionalOnProperty(prefix = "shiro", name = "enable-redis-session", havingValue = "false")
	public DefaultWebSessionManager defaultWebSessionManager() {
		ShiroProperties shiroProperties = SpringUtils.getBean("shiroProperties");
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionValidationInterval(shiroProperties.getSessionValidationInterval() );
		sessionManager.setGlobalSessionTimeout(shiroProperties.getGlobalSessionTimeout());
		sessionManager.setDeleteInvalidSessions(shiroProperties.getDeleteInvalidSessions());
		sessionManager.setSessionValidationSchedulerEnabled(shiroProperties.getSessionValidationSchedulerEnabled());
		sessionManager.setSessionDAO(SpringUtils.getBean("ehcacheShiroSessionDAO"));
		sessionManager.setSessionIdCookieEnabled(true);
		sessionManager.setSessionIdCookie(SpringUtils.getBean("sessionIdCookie"));
		Collection<SessionListener> listeners=new ArrayList<SessionListener>();
		listeners.add(SpringUtils.getBean("shiroSessionlistener"));
		sessionManager.setSessionListeners(listeners);
		return sessionManager;
	}

	/**
	 * session管理器(集群环境) redis中存储
	 */
	@Bean("sessionManager")
	@ConditionalOnProperty(prefix = "shiro", name = "enable-redis-session", havingValue = "true")
	public DefaultWebSessionManager defaultWebSessionManagerRedis() {
		ShiroProperties shiroProperties = SpringUtils.getBean("shiroProperties");
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionValidationInterval(shiroProperties.getSessionValidationInterval() );
		sessionManager.setGlobalSessionTimeout(shiroProperties.getGlobalSessionTimeout());
		sessionManager.setDeleteInvalidSessions(shiroProperties.getDeleteInvalidSessions());
		sessionManager.setSessionValidationSchedulerEnabled(shiroProperties.getSessionValidationSchedulerEnabled());
		sessionManager.setSessionDAO(SpringUtils.getBean("redisShiroSessionDAO"));
		sessionManager.setSessionIdCookieEnabled(true);
		sessionManager.setSessionIdCookie(SpringUtils.getBean("sessionIdCookie"));
		Collection<SessionListener> listeners=new ArrayList<SessionListener>();
		listeners.add(SpringUtils.getBean("shiroSessionlistener"));
		sessionManager.setSessionListeners(listeners);
		return sessionManager;
	}


	/**
	 * rememberMe管理器 rememberme cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度（128 256 512
	 * 位） rememberMe管理器，cipherKey是加密rememberMe Cookie的密钥；默认AES算法；
	 * <property name="cipherKey" value=
	 * "#{T(org.apache.shiro.codec.Base64).decode('4AvVhmFLUs0TTA3Kprsdag==')}"/>
	 * decode里面可以写${shiro.uid.rememeberMe.cookie.base64.cipherKey}从config.properties来读取
	 */
	@Bean("rememberMeManager")
	public CookieRememberMeManager rememberMeManager(SimpleCookie rememberMeCookie) {
		CookieRememberMeManager manager = new CookieRememberMeManager();
		manager.setCipherKey(Base64.decode("4AvVhmFLUs0TTA3Kprsdag=="));
		manager.setCookie(rememberMeCookie);
		return manager;
	}

	/**
	 * rememberMeCookie
	 */
	@Bean("rememberMeCookie")
	public SimpleCookie rememberMeCookie() {
		SimpleCookie simpleCookie = new SimpleCookie("remember_me_id");
		simpleCookie.setHttpOnly(true);
		simpleCookie.setMaxAge(2592000);// 30天
		return simpleCookie;
	}

	/**
	 * Shiro Filter过滤器配置 该bean与web.xml里的shiroFilter对应
	 */
	

	

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(org.apache.shiro.mgt.SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		ShiroProperties shiroProperties = SpringUtils.getBean("shiroProperties");
		shiroFilter.setSecurityManager(securityManager);//<!-- Shiro的核心安全接口,这个属性是必须的 -->
		/**
		 * 默认的登陆访问url
		 */
		shiroFilter.setLoginUrl(shiroProperties.getLoginUrl());
		/**
		 * 登陆成功后跳转的url
		 */
		shiroFilter.setSuccessUrl(shiroProperties.getSuccessUrl());
		/**
		 * 没有权限跳转的url
		 */
		shiroFilter.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());
		Map<String, Filter> filters =new LinkedHashMap<>();
		filters.put("authc", SpringUtils.getBean("authcFilter"));
		filters.put("captchaValidate", SpringUtils.getBean("captchaValidateFilter"));
		filters.put("sysUser", SpringUtils.getBean("sysUserFilter"));//SysUser放入到request域中 
		filters.put("forceLogout", SpringUtils.getBean("forceLogoutFilter"));//访问时判断有没有被强制退出 
		filters.put("encrypt", SpringUtils.getBean("encryptFilter"));//解密传输 
		shiroFilter.setFilters(filters);
		//动态加载shiro拦截器链
		ChainDefinitionService chainDefinitionService=new ChainDefinitionServiceImpl();//SpringUtils.getBean("chainDefinitionService");
		shiroFilter.setFilterChainDefinitions(chainDefinitionService.initFilterChainDefinitions());
		return shiroFilter;
	}

	/**
	 * 在方法中 注入 securityManager,进行代理控制
	 * 相当于调用SecurityUtils.setSecurityManager(securityManager)
	 * Shiro是从根对象SecurityManager进行身份验证和授权的；也就是所有操作都是自它开始的，
	 * 这个对象是线程安全且真个应用只需要一个即可，因此Shiro提供了SecurityUtils让我们绑定它为全局的，方便后续操作。
	 */
	@Bean
	public MethodInvokingFactoryBean methodInvokingFactoryBean(DefaultWebSecurityManager securityManager) {
		MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
		bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
		bean.setArguments(new Object[] { securityManager });
		return bean;
	}

	

	/*
	 * <!-- sessionId生成器 ，不配置也行也行默认的，这里明确地配置出来 --> <!-- 默认的 SessionIdGenerator
	 * 是一个 JavaUuidSessionIdGenerator，它能产生基于 Java UUIDs 的 String IDs。该实现能够
	 * 支持所有的生产环境。 -->
	 */
	@Bean("sessionIdGenerator")
	public JavaUuidSessionIdGenerator sessionIdGenerator() {
		return new JavaUuidSessionIdGenerator();
	}

	/* <!-- shiro session监听器注册 --> */
	@Bean("shiroSessionlistener")
	public ShiroSessionListener shiroSessionlistener() {
		return new ShiroSessionListener();
	}

	/*
	 * <!-- sessionIdCookie是用于生产Session ID Cookie的模板 --> <bean
	 * id="sessionIdCookie" class="org.apache.shiro.web.servlet.SimpleCookie">
	 * <!-- 指定本系统SESSIONID, 默认为: JSESSIONID 问题: 与SERVLET容器名冲突, 如JETTY, TOMCAT
	 * 等默认JSESSIONID, 当跳出SHIRO SERVLET时如ERROR-PAGE容器会为JSESSIONID重新分配值导致登录会话丢失!
	 * --> <constructor-arg name="name" value="sid" /> <!--
	 * 如果设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly
	 * cookie有助于减少某些类型的跨站点脚本攻击；此特性需要实现了Servlet 2.5 MR6及以上版本的规范的Servlet容器支持； -->
	 * <property name="httpOnly" value="true" /> <!--
	 * 设置Cookie的过期时间，秒为单位，默认-1表示关闭浏览器时过期Cookie --> <property name="maxAge"
	 * value="-1" /> </bean>
	 */

	@Bean("sessionIdCookie")
	public SimpleCookie sessionIdCookie() {
		SimpleCookie simpleCookie = new SimpleCookie("sid");
		simpleCookie.setHttpOnly(true);
		simpleCookie.setMaxAge(-1);
		return simpleCookie;
	}
	/*
	 * <!-- 会话验证quartz实现，升级shiro到1.2.6以上之后，SimpleTrigger变成了接口， 解决办法有两种：
	 * 1、自己重新实现SessionValidationScheduler类，QuartzSessionValidationScheduler
	 * implements SessionValidationScheduler 2、
	 * 使用ExecutorServiceSessionValidationScheduler代替QuartzSessionValidationScheduler
	 * --> <bean id="sessionValidationScheduler" class=
	 * "org.apache.shiro.session.mgt.quartz.QuartzSessionValidationScheduler">
	 * <property name="sessionValidationInterval" value="60000" /> <property
	 * name="sessionManager" ref="sessionManager" /> </bean>
	 */
	@Bean("sessionValidationScheduler")
	public QuartzSessionValidationScheduler sessionValidationScheduler() {
		QuartzSessionValidationScheduler quartzSessionValidationScheduler = new QuartzSessionValidationScheduler();
		quartzSessionValidationScheduler.setSessionValidationInterval(60000);
		ValidatingSessionManager sessionManager=SpringUtils.getBean("sessionManager");
		quartzSessionValidationScheduler.setSessionManager(sessionManager);
		return quartzSessionValidationScheduler;
	}
			
	
	/*<!-- 凭证匹配器 -->*/
	@Bean("credentialsMatcher")
	public RetryLimitHashedCredentialsMatcher credentialsMatcher() {
		RetryLimitHashedCredentialsMatcher credentialsMatcher = null;
		MorphoProperties morphoProperties = SpringUtils.getBean("morphoProperties");
		ShiroProperties shiroProperties = SpringUtils.getBean("shiroProperties");
		if(shiroProperties.getEnableRedisSession()){
			credentialsMatcher=new RetryLimitHashedCredentialsMatcher(SpringUtils.getBean("redisShiroCacheManager"),SpringUtils.getBean("redisManager"));
		}else{
			credentialsMatcher=new RetryLimitHashedCredentialsMatcher(SpringUtils.getBean("cacheManager"));
		}
		/*hashAlgorithmName必须的，没有默认值。可以有MD5或者SHA-1，如果对密码安全有更高要求可以用SHA-256或者更高。 
		这里使用MD5 storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码 
		hashIterations迭代次数，默认值是1。 */
		credentialsMatcher.setHashAlgorithmName(shiroProperties.getCredentialsMatcherHashAlgorithmName());
		credentialsMatcher.setHashIterations(shiroProperties.getCredentialsMatcherHashIterations());
		/*<!-- 是否存储散列后的密码为16进,制这里需要和注册时使用的加密方式一样，都使用MD5加密，此时用的是密码加密用的是Hex编码,密码匹配也需和注册时的加密方式一致 -->*/
		credentialsMatcher.setStoredCredentialsHexEncoded(shiroProperties.getCredentialsMatcherStoredCredentialsHexEncoded());
		credentialsMatcher.setEnableRetryLimit(morphoProperties.getLoginEnableRetryLimit());//<!-- 是否开启登录次数重试限制 -->
		credentialsMatcher.setAllowAttemptNum(morphoProperties.getLoginAllowAttemptNum());//<!-- 允许的最大失败次数 -->
		credentialsMatcher.setLockTime(morphoProperties.getLoginLockTime());//<!-- 锁定时间 单位秒 -->
		return credentialsMatcher;
	}

	/*··················································· 自定义的shiro过滤器 ····················································*/
	/*提取系统用户（SysUser） 验证用户是否合法的过滤器（如是否删除了 是否锁定了）*/
	@Bean("sysUserFilter")
	public SysUserFilter sysUserFilter() {
		return new SysUserFilter();
	}
	/*验证强制踢人filter*/
	@Bean("forceLogoutFilter")
	public ForceLogoutFilter forceLogoutFilter() {
		return new ForceLogoutFilter();
	}
	/*纯验证码校验*/
	@Bean("captchaValidateFilter")
	public CaptchaValidateFilter captchaValidateFilter() {
		MorphoProperties morphoProperties = SpringUtils.getBean("morphoProperties");
		CaptchaValidateFilter captchaValidateFilter = new CaptchaValidateFilter();
		captchaValidateFilter.setCaptchaEbabled(morphoProperties.getLoginEnableCaptch());
		return captchaValidateFilter;
	}
	/*解密请求数据过滤器 */
	@Bean("encryptFilter")
	public EncryptFilter encryptFilter() {
		EncryptFilter encryptFilter = new EncryptFilter();
		return encryptFilter;
	}
	/*
	
	/*基于自定义Form表单的身份验证过滤器 不带验证码判断 验证码单独出来 */
	@Bean("authcFilter")
	public MyFormAuthenticationFilter authcFilter() {
		ShiroProperties shiroProperties = SpringUtils.getBean("shiroProperties");
		MyFormAuthenticationFilter authcFilter = new MyFormAuthenticationFilter();
		authcFilter.setUsernameParam(shiroProperties.getFormAuthcUsernameParam());//<!-- 请求参数中用户名的参数key -->
		authcFilter.setPasswordParam(shiroProperties.getFormAuthcPasswordParam());//<!-- 请求参数中密码的参数key -->
		if(shiroProperties.getEnableRememberMe()){
			authcFilter.setRememberMeParam(shiroProperties.getRememberMeParam());//请求参数中是否记住我的参数key
		}
		authcFilter.setLoginUrl(shiroProperties.getLoginUrl());
		authcFilter.setSuccessUrl(shiroProperties.getSuccessUrl());
		return authcFilter;
	}

	
	//===================================单机环境ehcache=================================
	@Bean("ehcacheShiroSessionDAO")
	@ConditionalOnProperty(prefix = "shiro", name = "enable-redis-session", havingValue = "false")
	public ShiroSessionDAO ehcacheShiroSessionDAO(EhCacheShiroSessionRepository ehcacheShiroSessionRepository,SessionIdGenerator sessionIdGenerator) {
		ShiroSessionDAO ehcacheShiroSessionDAO = new ShiroSessionDAO();
		ehcacheShiroSessionDAO.setShiroSessionRepository(ehcacheShiroSessionRepository);
		ehcacheShiroSessionDAO.setSessionIdGenerator(sessionIdGenerator);
		return ehcacheShiroSessionDAO;
	}
	/**实现ShiroSessionRepository接口调用springCahce实现
	 * @return
	 */
	@Bean("ehcacheShiroSessionRepository")
	@ConditionalOnProperty(prefix = "shiro", name = "enable-redis-session", havingValue = "false")
	public EhCacheShiroSessionRepository ehcacheShiroSessionRepository() {
		EhCacheShiroSessionRepository ehcacheShiroSessionRepository = new EhCacheShiroSessionRepository();
		ehcacheShiroSessionRepository.setCacheManager(SpringUtils.getBean("ehCacheManager"));
		return ehcacheShiroSessionRepository;
	}
	@Bean("cacheManager")
	@ConditionalOnProperty(prefix = "shiro", name = "enable-redis-session", havingValue = "false")
	public EhCacheShiroCacheManager cacheManager() {
		EhCacheShiroCacheManager cacheManager = new EhCacheShiroCacheManager();
		cacheManager.setCacheManager(SpringUtils.getBean("ehCacheManager"));
		return cacheManager;
	}
	
	//===================================集群环境redis====================================
	@Bean("redisShiroSessionDAO")
	@ConditionalOnProperty(prefix = "shiro", name = "enable-redis-session", havingValue = "true")
	public ShiroSessionDAO redisShiroSessionDAO(RedisShiroSessionRepository redisShiroSessionRepository,SessionIdGenerator sessionIdGenerator) {
		ShiroSessionDAO redisShiroSessionDAO = new ShiroSessionDAO();
		redisShiroSessionDAO.setShiroSessionRepository(redisShiroSessionRepository);
		redisShiroSessionDAO.setSessionIdGenerator(sessionIdGenerator);
		return redisShiroSessionDAO;
	}
	/**
	 * @return
	 */
	@Bean("redisShiroSessionRepository")
	@ConditionalOnProperty(prefix = "shiro", name = "enable-redis-session", havingValue = "true")
	public RedisShiroSessionRepository redisShiroSessionRepository() {
		RedisShiroSessionRepository redisShiroSessionRepository = new RedisShiroSessionRepository();
		redisShiroSessionRepository.setRedisManager(SpringUtils.getBean("redisManager"));
		return redisShiroSessionRepository;
	}
	@Bean("redisShiroCacheManager")
	@ConditionalOnProperty(prefix = "shiro", name = "enable-redis-session", havingValue = "true")
	public RedisShiroCacheManager redisShiroCacheManager() {
		RedisShiroCacheManager redisShiroCacheManager = new RedisShiroCacheManager();
		redisShiroCacheManager.setCacheManager(SpringUtils.getBean("redisManager"));
		return redisShiroCacheManager;
	}
	
	
}
