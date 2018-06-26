package com.max256.morpho.config;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.max256.morpho.common.util.ip2region.DbConfig;
import com.max256.morpho.common.util.ip2region.DbMakerConfigException;
import com.max256.morpho.common.util.ip2region.DbSearcher;

/**
 * Ip2regionConfig
 * 
 * @author fbf
 * 
 */
@Configuration
public class Ip2regionConfig {

	@Bean(name = "ip2regionDbConfig")
	public DbConfig ip2regionDbConfig() throws DbMakerConfigException {
		return new DbConfig();
	}

	@Bean(name = "ipSearcher")
	public DbSearcher ipSearcher(DbConfig ip2regionDbConfig) throws FileNotFoundException, URISyntaxException {
		DbSearcher ipSearcher = new DbSearcher(ip2regionDbConfig, "/ip2regiondata/ip2region.db");
		return ipSearcher;
	}

}
