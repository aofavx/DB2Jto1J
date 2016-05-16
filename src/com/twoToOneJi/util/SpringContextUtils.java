package com.twoToOneJi.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring的工具类，用来获得配置文件中的bean
 * @author hermes
 *
 */
public class SpringContextUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext = null;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
		SpringContextUtils.applicationContext = applicationContext;
	}
	
	/**
	 * 获取spring配置的对象
	 * @param name 类名
	 * @return 对象
	 * @throws BeansException
	 */
	public static Object getBean(String name) throws BeansException {  
		return applicationContext.getBean(name);  
	}  

}
