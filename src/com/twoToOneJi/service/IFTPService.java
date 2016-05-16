package com.twoToOneJi.service;

import java.util.List;
import java.util.Map;


public interface IFTPService {
	
	/**
	 * 根据List集合中的电子文件记录信息，从ftp下载对应的文件
	 * @param efileList
	 * @param file
	 * @param IMyCallInterface
	 */
	public void downLoadFile(List<Map<String, Object>> efileList , Map<String, Object> file, IMyCallInterface myCallInterface);

}
