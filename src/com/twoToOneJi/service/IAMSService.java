package com.twoToOneJi.service;

import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.dom4j.DocumentException;

/**
 * 一级部署系统功能处理接口
 * @author jacob
 *
 */
public interface IAMSService {

	/**
	 * 将二级系统的数据归档到一级系统
	 * @param validateMap 基本信息
	 * @param file 二级系统的file记录
	 * @param list 二级系统file文件对应的efile记录集合
	 * @throws Exception 
	 */
	String archiveToYJ(Map<String,Object> file, Map<String, Object> validateMap, List<Map<String, Object>> efileList) throws Exception;

}
