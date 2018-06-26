package com.max256.morpho.common.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * PathVarServlet 初始化相关的一些常用路径 便于引用
 * @author fbf
 * @since 2016年10月25日 下午12:50:53
 * @version V1.0
 * 
 */
public class PathVarServlet implements ServletContextListener {
		
	  protected Logger logger = LoggerFactory.getLogger(this.getClass());

	  private static String CONTEXT;
	  private static String WEB;
	  private static String TEMP;
	  private static String PHOTO;
	  private static String CLASS;

	  public void init(ServletContext context)throws ServletException
	  {
	    CONTEXT = context.getContextPath();
	    if (!CONTEXT.endsWith("/")) {
	      CONTEXT += "/";
	    }
	    WEB = context.getRealPath("/").replaceAll("\\\\", "/");
	    if (!WEB.endsWith("/")) {
	      WEB += "/";
	    }
	    TEMP = WEB + "temp/";
	    PHOTO = WEB + "photo/";
	    CLASS = WEB + "WEB-INF/classes/";
	    logger.info("路径CONTEXT:"+CONTEXT);
	    logger.info("路径WEB:"+WEB);
	    logger.info("路径TEMP:"+TEMP);
	    logger.info("路径PHOTO:"+PHOTO);
	    logger.info("路径CLASS:"+CLASS);
	  }

	  public static String CONTEXT() {
	    return CONTEXT;
	  }

	  public static String WEB() {
	    return WEB;
	  }

	  public static String TEMP() {
	    return TEMP;
	  }

	  public static String CLASS() {
	    return CLASS;
	  }

	  public static String PHOTO() {
	    return PHOTO;
	  }

	  public static String fromAbsolutePathToWebPath(String absolutePath)
	  {
	    if (absolutePath.startsWith(WEB)) {
	      return "/" + absolutePath.substring(WEB.length());
	    }
	    throw new RuntimeException("获取文件相对路径失败!");
	  }

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			init(sce.getServletContext());
		} catch (ServletException e) {
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
