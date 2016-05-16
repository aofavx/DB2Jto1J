package com.twoToOneJi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * 系统配置代码表
 * @author jacob
 */
public class ServiceCode {
	
	//电子文档添加到一级系统的webservices调用地址
	private static String requestAddress ="http://127.0.0.1:7001/services/IFileService";
	private static String username = "sgcc022";//用户名
	private static String password = "sgcc!99022";//密码
	private static String check_no = "74D631A4DF157D87B5B123369ADE61B9";//系统验证编码
	//key方式认证的公钥信息
	public static String public_key_info="NEZPbzhXRlY2WDh3RjdrWXc4a2l3dHhzY0ZPVnk0K1AxczcrTGtGSnkxQmliNEZuRDlVcnFocThrV05NVm9DSDI2cUwxcHE1MVB0OA0KNU9ueDNlVlVVMjMrMnlYOUpqbXVyanFaZUJUQjZwT3FUdUY0b1VqL3ZrdVQzSHNwRVRZN3RoSUltL1JudFpqTG9DVCtyek9LcXk5Rw0KaTU0ZytmRTNHNmJYTFdsZndpTT0NCkAmQEFRQUINCg=="; 
	// 其中requestAddress,USERNAME，PASSWORD,CKECK_NO,PUBLIC_KEY_INFO为非结构化平台为业务系统提供
	
	//一级部署的webservice文件归档信息
	public static String yjAMSAddress=null;
	public static String yjNameSpaceUrl=null;
	public static String yjMethod=null;
	
	//二级系统的ftp信息
	public static String ftpIP=null;
	public static String ftpPort=null;
	public static String ftpUserName=null;
	public static String ftpPassWord=null;
	public static String ftpRootPath="";
	public static String localFilePath=null;
	
	public static Map<String,Object> codeMap=null;
	
	public static Map<String,String> libcodeMap=new HashMap<String, String>();
	
	public static Map<String,String> unitCodeMap=new HashMap<String, String>();
	
	static{
		System.out.println("初始化配置信息..............");
		initTsspByString();
		initCodeProperfies(libcodeMap,"libcode.properties");
		initCodeProperfies(unitCodeMap,"unitCode.properties");
	}
	
	/**
	 * 获取文档管理webservices调用地址
	 */
	public static String getRequestAddress(){
		if(requestAddress==null){
			initTsspByString();
		}
		return requestAddress;
	}

	/**
	 * 用户名
	 */
	public static String getUsername() {
		if(username==null){
			initTsspByString();
		}
		return username;
	}
	/**
	 * 用户密码
	 */
	public static String getPassword() {
		if(password==null){
			initTsspByString();
		}
		return password;
	}
	/**
	 * 系统验证编码
	 */
	
	public static String getCheck_no() {
		if(check_no==null){
			initTsspByString();
		}
		return check_no;
	}

	public static String getPublic_key_info() {
		if(public_key_info==null){
			initTsspByString();
		}
		return public_key_info;
	}

	/**
	 * key方式认证的公钥信息
	 */
	public static Map<String, Object> getCodeMap() {
		if(codeMap==null){
			initTsspByString();
		}
		return codeMap;
	}
	
	public static int getRandomInt50(){
		Random r=new Random();
		return r.nextInt(50);
	}

	
	private static void initCodeProperfies(Map codeMap,String proFile) {
		Properties properties=new Properties();
		InputStream is=ServiceCode.class.getClassLoader().getResourceAsStream(proFile);
		try {
			properties.load(is);
			is.close();
			Enumeration en=properties.propertyNames();
			String key=null;
			while(en.hasMoreElements()){
				key=(String) en.nextElement();
				codeMap.put(key,(String)properties.get(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
				if("requestAddress".equals(key)){
					requestAddress=properties.getProperty(key);
				}else if("username".equals(key)){
					username=properties.getProperty(key);
				}else if("password".equals(key)){
					password=properties.getProperty(key);
				}else if("check_no".equals(key)){
					check_no=properties.getProperty(key);
				}else if ("public_key_info".equals(key)) {
					public_key_info=properties.getProperty(key);
				}else if("ftpIP".equals(key)){
					ftpIP=properties.getProperty(key);
				}else if("ftpPort".equals(key)){
					ftpPort=properties.getProperty(key);
				}else if("ftpUserName".equals(key)){
					ftpUserName=properties.getProperty(key);
				}else if("ftpPassWord".equals(key)){
					ftpPassWord=properties.getProperty(key);
				}else if("ftpRootPath".equals(key)){
					ftpRootPath=properties.getProperty(key);
				}else if("yjAMSAddress".equals(key)){
					yjAMSAddress=properties.getProperty(key);
				}else if("yjNameSpaceUrl".equals(key)){
					yjNameSpaceUrl=properties.getProperty(key);
				}else if("yjMethod".equals(key)){
					yjMethod=properties.getProperty(key);
				}else if("localFilePath".equals(key)){
					localFilePath=properties.getProperty(key);
				}
				codeMap.put(key, properties.getProperty(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Object getLibAndDWToKey(String libcode, String dwCode) {
		return libcode+"-"+dwCode;
	}
}
