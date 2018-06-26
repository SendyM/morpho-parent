package com.max256.morpho;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.max256.morpho.config.web.SpringfoxConfig;

/**
 * SpringBoot方式启动类
 *
 * @author fbf
 */
@SpringBootApplication(exclude = FreeMarkerAutoConfiguration.class)
@ComponentScan(basePackages = {"com.max256"},
    excludeFilters = {
        @ComponentScan.Filter(Controller.class),//只扫描核心spring容器不扫描springmvc层容器
        @ComponentScan.Filter(ControllerAdvice.class)
    })
@Import(value = SpringfoxConfig.class)
public class MorphoWebApplication {

  protected final static Logger logger = LoggerFactory.getLogger(MorphoWebApplication.class);

  public static void main(String[] args) {
    try {
      SpringApplication.run(MorphoWebApplication.class, args);
      logger.info("WebApplication start success!");
    } catch (Exception e) {
      logger.info("WebApplication start failure!");
      throw e;
    }

  }

}
