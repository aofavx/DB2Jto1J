package com.twoToOneJi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class ArchiveCode {
	private static String[] fileLibcode=null;
	
	public static Map<String,Object> codeMap=null;
	
	static{
		System.out.println("初始化配置信息..............");
		initTsspByString();
	}
	
	private static void initTsspByString(){
		codeMap=new HashMap<String, Object>();
		Properties properties=new Properties();
		try {
			InputStream is=ServiceCode.class.getClassLoader().getResourceAsStream("Service.properties");
			properties.load(is);
			is.close();
			Enumeration en=properties.propertyNames();
			String key=null;
			while(en.hasMoreElements()){
				key=(String) en.nextElement();
				if("fileLibcode".equals(key)){
					fileLibcode=StringUtils.split(String.valueOf(properties.getProperty(key)),",");
				}
				codeMap.put(key, properties.getProperty(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
