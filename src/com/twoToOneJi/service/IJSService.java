package com.twoToOneJi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.twoToOneJi.po.RunLibAndDW;


/**
 * 江苏二级系统功能处理接口
 * @author jacob
 *
 */
public interface IJSService {
	
	public static Map<String,RunLibAndDW> runLibAndDWMap=new HashMap<String, RunLibAndDW>();
	/**
	 * 读取所有文件
	 * @param libcode 档案类型
	 * @param dwCode 单位编号
	 * @return
	 */
	public List<Map<String,Object>> queryFileAll(String libcode,String dwCode);

	/**
	 * 根据文件id读取文件
	 * @param libcode 档案类型
	 * @param dwCode 单位编号
	 * @param fileSyscode 文件id
	 * @return
	 */
	public Map<String, Object> queryFileById(String libcode,String dwCode,String fileSyscode);
	/**
	 * 根据文件id,读取电子原文记录
	 * @param libcode  档案类型
	 * @param dwCode 单位编号
	 * @param fileId 文件id
	 * @return
	 */
	public List<Map<String,Object>> queryEFileByFileId(String libcode,String dwCode,String fileSyscode);
	

	public List queryLibCode();
	
	public List queryUnit();

	/**
	 * 获取执行的消息信息
	 * @param libcode
	 * @param dwCodes
	 * @return
	 */
	public Map getRunMsg(String libcode, String dwCodes);

	/**
	 * 二级系统到一级系统归档推送：验证档案和单位对应关系
	 */
	public String ejArchiveVerify(String libcode, String dwCode);
	
	/**
	 * 二级系统数据归档到一级系统业务处理执行方法
	 * @param libcode 档案类型
	 * @param dwCodes 单位编号 可能有多个，以“,”分割
	 */
	public void ejArchiveToYJ(String libcode, String dwCodes);
	
	/**
	 * 回调函数传入的字符串
	 * @param resultStr
	 */
//	public void setResult(String resultStr);
}
