package com.max256.morpho.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MorphoProperties配置
 *
 * @author fbf
 */
@Configuration
@ConfigurationProperties(prefix = MorphoProperties.PREFIX)
public class MorphoProperties {

	public static final String PREFIX = "morpho";

	private Boolean loginEnableRetryLimit = false;

	private Integer loginAllowAttemptNum = 3;

	private Integer loginLockTime = 60;

	private Boolean loginEnableCaptch = false;

	private Boolean sysResourceGenerateFlag = false;

	private String systemSysname = "morpho system";
	
	private String systemCopyright = "www.max256.com";
	
	private Boolean mutiDatasourceOpen = false;
	
	private Boolean enableJavamelody = true;//是否开启javamelody监控
	
	private String javamelodyAopRegExp="";//被监控的bean aop正则表达式 系统核心
	
	private String xssFilterPaths="";//需要xss过滤的path
	
	private Boolean enableHttpRequestLog = true;//是否开启http请求日志
	
	

	public Boolean getEnableHttpRequestLog() {
		return enableHttpRequestLog;
	}

	public void setEnableHttpRequestLog(Boolean enableHttpRequestLog) {
		this.enableHttpRequestLog = enableHttpRequestLog;
	}

	public String getXssFilterPaths() {
		return xssFilterPaths;
	}

	public void setXssFilterPaths(String xssFilterPaths) {
		this.xssFilterPaths = xssFilterPaths;
	}

	public String getJavamelodyAopRegExp() {
		return javamelodyAopRegExp;
	}

	public void setJavamelodyAopRegExp(String javamelodyAopRegExp) {
		this.javamelodyAopRegExp = javamelodyAopRegExp;
	}

	public Boolean getEnableJavamelody() {
		return enableJavamelody;
	}

	public void setEnableJavamelody(Boolean enableJavamelody) {
		this.enableJavamelody = enableJavamelody;
	}

	public Boolean getMutiDatasourceOpen() {
		return mutiDatasourceOpen;
	}

	public void setMutiDatasourceOpen(Boolean mutiDatasourceOpen) {
		this.mutiDatasourceOpen = mutiDatasourceOpen;
	}

	public Boolean getLoginEnableRetryLimit() {
		return loginEnableRetryLimit;
	}

	public void setLoginEnableRetryLimit(Boolean loginEnableRetryLimit) {
		this.loginEnableRetryLimit = loginEnableRetryLimit;
	}

	public Integer getLoginAllowAttemptNum() {
		return loginAllowAttemptNum;
	}

	public void setLoginAllowAttemptNum(Integer loginAllowAttemptNum) {
		this.loginAllowAttemptNum = loginAllowAttemptNum;
	}

	public Integer getLoginLockTime() {
		return loginLockTime;
	}

	public void setLoginLockTime(Integer loginLockTime) {
		this.loginLockTime = loginLockTime;
	}

	public Boolean getLoginEnableCaptch() {
		return loginEnableCaptch;
	}

	public void setLoginEnableCaptch(Boolean loginEnableCaptch) {
		this.loginEnableCaptch = loginEnableCaptch;
	}

	public Boolean getSysResourceGenerateFlag() {
		return sysResourceGenerateFlag;
	}

	public void setSysResourceGenerateFlag(Boolean sysResourceGenerateFlag) {
		this.sysResourceGenerateFlag = sysResourceGenerateFlag;
	}

	public String getSystemSysname() {
		return systemSysname;
	}

	public void setSystemSysname(String systemSysname) {
		this.systemSysname = systemSysname;
	}

	public String getSystemCopyright() {
		return systemCopyright;
	}

	public void setSystemCopyright(String systemCopyright) {
		this.systemCopyright = systemCopyright;
	}
	
	
}
