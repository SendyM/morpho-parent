package com.max256.morpho.common.mutidatasource.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.max256.morpho.common.mutidatasource.DataSourceContextHolder;
import com.max256.morpho.common.mutidatasource.annotion.DataSource;
import com.max256.morpho.common.mutidatasource.config.MutiDataSourceProperties;

/**
 * 多数据源切换的aop
 *
 * @author fbf
 */
@Aspect
@Component
@ConditionalOnProperty(prefix = "morpho", name = "muti-datasource-open", havingValue = "true")
public class MultiSourceExAop implements Ordered {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MutiDataSourceProperties mutiDataSourceProperties;

    @Pointcut(value = "@annotation(com.max256.morpho.common.mutidatasource.annotion.DataSource)")
    private void cut() {
    	//切入点标记多数据源的注解
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        Signature signature = point.getSignature();
        MethodSignature methodSignature = null;
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        methodSignature = (MethodSignature) signature;

        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        DataSource datasource = currentMethod.getAnnotation(DataSource.class);
        if (datasource != null) {
            DataSourceContextHolder.setDataSourceType(datasource.name());
            if(log.isDebugEnabled()){
            	log.debug("设置数据源为：" + datasource.name());	
            }
        } else {
            DataSourceContextHolder.setDataSourceType(mutiDataSourceProperties.getDefaultDataSourceName());
            if(log.isDebugEnabled()){
            	log.debug("设置数据源为：dataSourceCurrent");	
            }
        }

        try {
            return point.proceed();
        } finally {
        	if(log.isDebugEnabled()){
        		log.debug("清空数据源信息！");
        	}
            DataSourceContextHolder.clearDataSourceType();
        }
    }


    /**
     * aop的顺序要早于spring的事务
     */
    @Override
    public int getOrder() {
        return 1;
    }

}
