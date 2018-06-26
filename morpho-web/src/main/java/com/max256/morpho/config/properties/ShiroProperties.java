package com.max256.morpho.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * shiro配置
 * @author fbf
 *
 */
@Configuration
@ConfigurationProperties(prefix = ShiroProperties.PREFIX)
public class ShiroProperties {

	public static final String PREFIX = "shiro";

	private String successUrl = "/index";
	private String loginUrl = "/login";
	private String unauthorizedUrl = "/login";
	private String credentialsMatcherHashAlgorithmName = "md5";
	private Integer credentialsMatcherHashIterations = 2;
	private Boolean credentialsMatcherStoredCredentialsHexEncoded = true;
	private String formAuthcUsernameParam = "usrn";
	private String formAuthcPasswordParam = "usrp";
	private Boolean enableRedisSession =false;//是否开启redis存储session 集群环境必须使用 默认关闭
	private Boolean enableRememberMe =false;//是否开启记住功能 默认关闭
	private String rememberMeParam= "rememberMe";
	private Integer globalSessionTimeout=1800000;//30min
	private Boolean deleteInvalidSessions=true;//是否删除无效session
	private Boolean sessionValidationSchedulerEnabled=true;
	private Integer sessionValidationInterval=60000;//多久检测一次session有效性
	
	

	
	
	public Integer getGlobalSessionTimeout() {
		return globalSessionTimeout;
	}
	public void setGlobalSessionTimeout(Integer globalSessionTimeout) {
		this.globalSessionTimeout = globalSessionTimeout;
	}
	public Boolean getDeleteInvalidSessions() {
		return deleteInvalidSessions;
	}
	public void setDeleteInvalidSessions(Boolean deleteInvalidSessions) {
		this.deleteInvalidSessions = deleteInvalidSessions;
	}
	public Boolean getSessionValidationSchedulerEnabled() {
		return sessionValidationSchedulerEnabled;
	}
	public void setSessionValidationSchedulerEnabled(Boolean sessionValidationSchedulerEnabled) {
		this.sessionValidationSchedulerEnabled = sessionValidationSchedulerEnabled;
	}
	public Integer getSessionValidationInterval() {
		return sessionValidationInterval;
	}
	public void setSessionValidationInterval(Integer sessionValidationInterval) {
		this.sessionValidationInterval = sessionValidationInterval;
	}
	public Boolean getEnableRedisSession() {
		return enableRedisSession;
	}
	public void setEnableRedisSession(Boolean enableRedisSession) {
		this.enableRedisSession = enableRedisSession;
	}
	public Boolean getEnableRememberMe() {
		return enableRememberMe;
	}
	public void setEnableRememberMe(Boolean enableRememberMe) {
		this.enableRememberMe = enableRememberMe;
	}
	public String getRememberMeParam() {
		return rememberMeParam;
	}
	public void setRememberMeParam(String rememberMeParam) {
		this.rememberMeParam = rememberMeParam;
	}
	public String getSuccessUrl() {
		return successUrl;
	}
	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}
	public String getLoginUrl() {
		return loginUrl;
	}
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}
	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}
	public String getCredentialsMatcherHashAlgorithmName() {
		return credentialsMatcherHashAlgorithmName;
	}
	public void setCredentialsMatcherHashAlgorithmName(String credentialsMatcherHashAlgorithmName) {
		this.credentialsMatcherHashAlgorithmName = credentialsMatcherHashAlgorithmName;
	}
	public Integer getCredentialsMatcherHashIterations() {
		return credentialsMatcherHashIterations;
	}
	public void setCredentialsMatcherHashIterations(Integer credentialsMatcherHashIterations) {
		this.credentialsMatcherHashIterations = credentialsMatcherHashIterations;
	}
	public Boolean getCredentialsMatcherStoredCredentialsHexEncoded() {
		return credentialsMatcherStoredCredentialsHexEncoded;
	}
	public void setCredentialsMatcherStoredCredentialsHexEncoded(Boolean credentialsMatcherStoredCredentialsHexEncoded) {
		this.credentialsMatcherStoredCredentialsHexEncoded = credentialsMatcherStoredCredentialsHexEncoded;
	}
	public String getFormAuthcUsernameParam() {
		return formAuthcUsernameParam;
	}
	public void setFormAuthcUsernameParam(String formAuthcUsernameParam) {
		this.formAuthcUsernameParam = formAuthcUsernameParam;
	}
	public String getFormAuthcPasswordParam() {
		return formAuthcPasswordParam;
	}
	public void setFormAuthcPasswordParam(String formAuthcPasswordParam) {
		this.formAuthcPasswordParam = formAuthcPasswordParam;
	}
	
	

}
