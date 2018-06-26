package com.max256.morpho.adminserver;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.config.EnableAdminServer;

/**
 * SpringBoot方式启动类
 * 
 * @author fbf
 */
@EnableAdminServer
@SpringBootApplication
public class AdminServerWebApplication  {
	public static void main(String[] args) {	
			SpringApplication.run(AdminServerWebApplication.class, args);
	}

}
