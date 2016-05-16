package com.twoToOneJi.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {
	private static BeanFactory factory=null;
	public static Object getBean(String beanName){
		if(factory==null){
			factory = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
		return factory.getBean(beanName);
	}
}
