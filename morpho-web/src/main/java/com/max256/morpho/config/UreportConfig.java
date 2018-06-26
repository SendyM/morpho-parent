package com.max256.morpho.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * ureport2报表整合配置
 * 
 * @author fbf
 * 
 */
@Configuration
@ImportResource(locations = { "classpath:ureport-console-context.xml" })
public class UreportConfig {

}
