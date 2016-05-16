package com.twoToOneJi.service.impl;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.twoToOneJi.client.YJUploadUtil;
import com.twoToOneJi.po.RunLibAndDW;
import com.twoToOneJi.service.IFTPService;
import com.twoToOneJi.service.IJSService;
import com.twoToOneJi.service.IMyCallInterface;
import com.twoToOneJi.util.ServiceCode;

import demo.Ftp;
@Service("ftpService")
@Scope("prototype")
public class FTPServiceImpl implements IFTPService {
	Logger logger =Logger.getLogger(this.getClass().getName());
	
	private int efileTotalNum=0;
	private int efileFinishNum=0;
	private boolean efileErrorBool=false;
	private Map<String,Object> file;
	private List<Map<String, Object>> efileList;
	private IMyCallInterface myCallInterface;
	
	@Override
	public void downLoadFile(List<Map<String, Object>> _efileList,Map<String, Object> _file, IMyCallInterface myCallInterface) {
		this.myCallInterface=myCallInterface;
		this.file=_file;
		this.efileList=_efileList;
		if(this.efileList!=null&& this.efileList.size()>0){
			efileTotalNum=efileList.size();
			for (Iterator<Map<String, Object>> iterator = efileList.iterator(); iterator.hasNext();) {
				new Thread(new FtpDownThread((Map<String, Object>) iterator.next(),this)).start();
			}
		}else{
			try {
				this.myCallInterface.callback(file,efileList);
			} catch (Exception e) {
				logger.error("无电子文件归档失败");
			}finally{
				JSServiceImpl.decRunLib(file);
			}
		}
	}
	
	public synchronized void  efileFinishNumAdd(){
		efileFinishNum++;
	}
	
	/**
	 * 设置file下的efile执行出现错误的状态值  true
	 */
	public void efileErrorBoolSetTrue(){
		efileErrorBool=true;
	}

	@SuppressWarnings("unchecked")
	public void upLoadToYJ(String localPath, String fileName, String fileType, Map<String, Object> efileMap) {
		//文档属性json串
		String msg="";
		try {
			String fileProperty=String.format("{'file_name':['%s'],'file_type':['%s'],'object_type':'ecm_document'}",fileName, fileType);
			Map param=YJUploadUtil.getUtil().add(fileProperty,localPath);//调用添加方法
			if("false".equals(param.get("success"))){
				msg=String.format("file %s 下的 %s 文件上传出现错误 \r",file.get("SYSCODE"),efileMap.get("SYSCODE"));
				IJSService.runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(String.valueOf(file.get("libcode")),String.valueOf(file.get("dwCode")))).setResultStr(msg);
				efileErrorBoolSetTrue();
			}
			efileMap.put("DOCUMENTID", param.get("fileid"));
		} catch (Exception e) {
			efileErrorBoolSetTrue();
		}finally{
			efileFinishNumAdd();
			checkFinishBool();
		}
	}

	/**
	 * 验证file下的efile是否都下载并上传到一级系统完成
	 */
	private void checkFinishBool() {
		String msg="";
		RunLibAndDW runLibDW=IJSService.runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(String.valueOf(file.get("libcode")),String.valueOf(file.get("dwCode"))));
		if(efileTotalNum==efileFinishNum){
			try {
				msg=String.format("file %s 下的efile全部下载完成:%d \r",file.get("SYSCODE"), efileFinishNum);
				runLibDW.setResultStr(msg);
				logger.info(msg);
				if(!efileErrorBool){
					myCallInterface.callback(file,efileList);
				}else{
					msg=String.format("file %s 下的某个efile文件从二级系统ftp下载或上传到一级系统的过程中出现错误，\r删掉efile到一级系统的所有上传文件\r", file.get("SYSCODE"));
					runLibDW.setResultStr(msg);
					logger.info(msg);
					deleteEfileArchive(YJUploadUtil.getUtil());
				}
			} catch (Exception e) {
			}finally{
				JSServiceImpl.decRunLib(file);
			}
		}else{
			msg=String.format("file %s 下的efile已下载 %d ,剩余 %d \r", file.get("SYSCODE"),efileFinishNum,efileTotalNum-efileFinishNum);
			runLibDW.setResultStr(msg);
			logger.info(msg);
		}
	}

	/**
	 * 调用一级系统的电子文件归档接口  删除之前上传的电子文件
	 */
	private void deleteEfileArchive(YJUploadUtil axis) {
		for (Iterator<Map<String, Object>> iterator = efileList.iterator(); iterator.hasNext();) {
			Map<String, Object> efile=(Map<String, Object>) iterator.next();
			String documentId=(String) efile.get("DOCUMENTID");
			if(StringUtils.isNotEmpty(documentId)){
				String fileProperty="{documentid:'"+documentId+"'}";
				axis.delete(fileProperty);
			}
		}
	}
	
	class FtpDownThread implements Runnable{
		private Map<String, Object> efileMap;
		private String fileName;
		private String filePath;
		private String fileType;
		private FTPServiceImpl ftpService;	
		
		public FtpDownThread(Map<String, Object> map, FTPServiceImpl _ftpService) {
			this.efileMap=map;
			this.ftpService=_ftpService;
			this.fileName=(String) map.get("FILENAME");
			this.filePath=(String) map.get("FILEPATH");
			this.fileType=StringUtils.substring(filePath,StringUtils.lastIndexOf(filePath,".")+1);
		}
		
		@Override
		public void run() {
			logger.info("开始下载文件："+filePath+"--"+fileName);
			Ftp ftp=new Ftp();
			try {
				ftp.connectServer(ServiceCode.ftpIP, Integer.valueOf(ServiceCode.ftpPort), ServiceCode.ftpUserName, ServiceCode.ftpPassWord, ServiceCode.ftpRootPath);
				String localPath=ServiceCode.localFilePath+File.separator+filePath;
//				filePath="data/wygd/sql.sql";
				ftp.download(filePath, localPath);
				logger.info("文件已下载到本地："+localPath);
				this.ftpService.upLoadToYJ(localPath,fileName,fileType,efileMap);
			} catch (Exception e) {
				logger.error(e);
				logger.error("-----"+fileName+"--"+efileMap.get("SYSCODE")+" 二级系统ftp下载失败...");
				this.ftpService.efileErrorBoolSetTrue();
				this.ftpService.efileFinishNumAdd();
				this.ftpService.checkFinishBool();
			}finally{
				ftp.closeConnect();
			}
		}
	}
}
