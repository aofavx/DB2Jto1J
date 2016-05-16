package com.twoToOneJi.dao;

import java.util.List;
import java.util.Map;

/**
 * 江苏二级系统数据库读写接口
 * @author jacob
 */
public interface IJSDBDao {

	/**
	 * 每次查询的记录数
	 */
	
	public int ROWS=5;
	
	/**
	 * 查询表里的总记录数
	 * @return
	 */
	int queryTotalByFile(String tableName);
	
	/**
	 * 根据页号查询记录
	 * @param tableName
	 * @param pageNum
	 * @return
	 */
	List<Map<String,Object>> query(String tableName,int pageNum);

	/**
	 * @param libcodeId
	 */
	Map<String,Object> queryArcByLibcodeId(String libcodeId);
	

	
	/**
	 * 根据id查询文件表记录
	 * @param libcode
	 * @param dwCode
	 * @param fileSyscode
	 * @return
	 */
	
	Map<String,Object> queryFileById(String libcode, String dwCode, String fileSyscode);
	
	/**
	 * 根据fileid查询电子文件表记录
	 * @param libcode
	 * @param dwCode
	 * @param fileSyscode
	 * @return
	 */
	
	List<Map<String,Object>> queryEFileByFileId(String libcode, String dwCode, String fileSyscode);


	/**
	 * 记录文件归档到一级系统的归档日志
	 * @param file
	 * @param efileList
	 * @param guidangResult 归档到一级系统是的处理结果，如果推送到一级系统成功，则guidangResult为null
	 */
	void addArchiveLog(Map<String, Object> file,List<Map<String, Object>> efileList, String jsonStr);
	
	/**
	 * 根据filesysocde查询归档日志表记录
	 * @return
	 */
	public Map<String, Object> queryArchiveLogByfileSyscode(String fileSyscode);
	
	/**
	 * 根据filesysocde修改记录
	 * @param fileSyscode
	 * @param efileSyscodes
	 * @param doucmentIds
	 * @param jsonStr
	 */
	public void updateArchiveLogByfileSyscode(String fileSyscode, String efileSyscodes,String doucmentIds,String jsonStr);

	/**
	 * 根据表名和列名 获取某一列的值
	 * @param fieldName
	 * @param tableName
	 * @return
	 */
	public List<Map<String, Object>> queryFieldByFile(String fieldName,String tableName);
	
	/**
	 * 根据libcode和unitId合成file表的表名
	 * @param libcode
	 * @param unitId
	 */
	public String getFileTableName(String libcode,String unitId);
	
}
