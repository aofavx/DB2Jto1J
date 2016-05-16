package com.twoToOneJi.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twoToOneJi.dao.IJSDBDao;
import com.twoToOneJi.po.RunLibAndDW;
import com.twoToOneJi.service.IAMSService;
import com.twoToOneJi.service.IFTPService;
import com.twoToOneJi.service.IJSService;
import com.twoToOneJi.service.IMyCallInterface;
import com.twoToOneJi.util.ServiceCode;
import com.twoToOneJi.util.SpringContextUtils;
import com.twoToOneJi.util.SpringUtil;
/**
 * 江苏二级系统功能处理实现类
 * @author jacob
 *
 */
@Service("jsService")
public class JSServiceImpl implements IJSService {
	static Logger logger=Logger.getLogger(JSServiceImpl.class.getName());

	@Autowired
	private IJSDBDao jsDBDao;
	
	@Autowired
	private IFTPService ftpService;
	
	@Autowired
	private IAMSService amsService;

	@Override
	public List<Map<String,Object>> queryLibCode() {
		List<Map<String,Object>> list=jsDBDao.queryFieldByFile("LIBCODE,CHNAME", "s_arc");
		return list;
	}
	
	@Override
	public List<Map<String,Object>> queryUnit() {
		List<Map<String,Object>> list=jsDBDao.queryFieldByFile("SYSCODE,UNITNAME", "S_UNIT");
		return list;
	}
	
	@Override
	public List<Map<String,Object>> queryEFileByFileId(String libcode, String dwCode,String fileSyscode) {
		List<Map<String,Object>> list=jsDBDao.queryEFileByFileId(libcode,dwCode,fileSyscode);
		return list;
	}

	@Override
	public List<Map<String,Object>> queryFileAll(String libcode, String dwCode) {
		List<Map<String,Object>> list=jsDBDao.query("D_File"+libcode+"_"+dwCode,1);
		return list;
	}
	

	@Override
	public Map<String,Object> queryFileById(String libcode, String dwCode, String fileSyscode) {
		Map<String,Object> map=jsDBDao.queryFileById(libcode, dwCode, fileSyscode);
		return map;
	}

	@Override
	public String ejArchiveVerify(String libcode,String dwCode) {		
		if(runLibAndDWMap.containsKey(ServiceCode.getLibAndDWToKey(libcode,dwCode))&& !runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(libcode,dwCode)).isStop()){
			StringBuffer resultStr=runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(libcode,dwCode)).getResultStr();
			return resultStr.append("档案类型：").append(libcode).append("，单位id:").append(dwCode).append(" 归档已在归档执行中...\r").toString();
		}
		String yjLibcode=ServiceCode.libcodeMap.get(libcode);
		if(StringUtils.isEmpty(yjLibcode)){
			return String.format("libcode.properties配置文件中未配置与libcode=%s 对应的一级系统档案类型及名称\r",libcode);
		}
		String yjUnitId=ServiceCode.unitCodeMap.get(dwCode);
		if(StringUtils.isEmpty(yjUnitId)){
			return String.format("unitCode.properties配置文件中未配置与二级系统unitid=%s 对应的一级系统unitId及名称\r", dwCode);
		}
		return null;
	}
	
	@Override
	public void ejArchiveToYJ(String libcode, String dwCode) {
		runLibAndDWMap.put(String.valueOf(ServiceCode.getLibAndDWToKey(libcode,dwCode)), new RunLibAndDW());
		ArchiveMainRunnable archiveRun=new ArchiveMainRunnable(libcode, dwCode);
		runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(libcode,dwCode)).setResultStr(String.format("档案类型：%s，单位id:%s 归档开始...\r", libcode,dwCode));
		new Thread(archiveRun).start();
	}

	/**
	 * 运行每一个文件和旗下的电子文件的下载及归档到一级系统
	 * @param validateMap
	 * @param file
	 * @param efileList
	 */
	private void runEveryFileArchive(final Map<String, Object> validateMap, Map<String, Object> file,List<Map<String, Object>> efileList) {
		runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(String.valueOf(file.get("libcode")),String.valueOf(file.get("dwCode")))).addAi();
		ftpService=(IFTPService) SpringContextUtils.getBean("ftpService");
		ftpService.downLoadFile(efileList,file,new IMyCallInterface(){
			@SuppressWarnings("unchecked")
			@Override
			public void callback(Object... object) throws Exception {
				Map<String,Object> file=(Map<String,Object>)object[0];
				List<Map<String,Object>> efileList=(List<Map<String,Object>>)object[1];
				String guidangResult = amsService.archiveToYJ(validateMap,file,efileList);
				jsDBDao.addArchiveLog(file,efileList,guidangResult);
				if(guidangResult==null){
					runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(String.valueOf(file.get("libcode")),String.valueOf(file.get("dwCode")))).setResultStr("文件："+file.get("SYSCODE")+" 归档完成.\r");
				}else{
					String json=StringUtils.substring(guidangResult,StringUtils.indexOf(guidangResult,"JSON:")==-1 ? 0 : StringUtils.indexOf(guidangResult,"JSON:"));
					runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(String.valueOf(file.get("libcode")),String.valueOf(file.get("dwCode")))).setResultStr("文件："+file.get("SYSCODE")+" 归档失败:"+json);
				}
			}
		});
	}
	
	/**
	 * 检查档案类型下的文件是否都归档完成
	 * @param file
	 */
	public static void decRunLib(Map<String, Object> file) {
		RunLibAndDW runLib= runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(String.valueOf(file.get("libcode")),String.valueOf(file.get("dwCode"))));
		runLib.decAi();
		synchronized (runLib) {
			runLib.notifyAll();
			logger.info(String.format("dwCode: %s 唤醒其他等待任务。。。", String.valueOf(file.get("dwCode"))));
		}
		String str=String.format("档案类型：%s，单位id:%s 归档剩余 %d 个文件\r",String.valueOf(file.get("libcode")),String.valueOf(file.get("dwCode")),runLib.getAiNum());
		logger.info(str);
		runLib.setResultStr(str);
	}
	

	/**
	 * 查询基本数据信息
	 * @param libcode
	 * @param dwCodes
	 * @return
	 */
	private Map<String, Object> getValidateMap(String yjLibcode, String yjCode) {
		Map<String, Object> validateMap =new HashMap<String, Object>();
		validateMap.put("libcode",yjLibcode);
		validateMap.put("unitid",yjCode);
		validateMap.put("sjly", "OA");
		return validateMap;
	}

	public static void main(String[] args) {
		IJSService jsService=(IJSService) SpringUtil.getBean("jsService");
		List<Map<String, Object>> list=jsService.queryFileAll("M2", "0005");
		System.out.println(list);
	}

	@Override
	public Map<String,Object> getRunMsg(String libcode, String dwCode) {
		boolean isStop=true;
		StringBuilder result=new StringBuilder();
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String msg=null;
		RunLibAndDW  runLibDW=null;
		if(StringUtils.equals(dwCode,"all")){
			Set<String> keys=ServiceCode.unitCodeMap.keySet();
			for (String key : keys) {
				runLibDW=IJSService.runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(libcode, key));
				if(runLibDW!=null){
					msg=runLibDW.getResultStr().toString();
					if(StringUtils.isNotEmpty(msg)){
						runLibDW.deleteResultStr(msg.length());
						result.append(msg);
					}
					if(isStop){
						isStop=runLibDW.isStop();
					}
				}else{
					isStop=false;
				}
			}
		}else{
			runLibDW=IJSService.runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(libcode, dwCode));
			msg=runLibDW.getResultStr().toString();
			if(StringUtils.isNotEmpty(msg)){
				runLibDW.deleteResultStr(msg.length());
				result.append(msg);
			}
			if(isStop){
				isStop=runLibDW.isStop();
			}
		}
		
//		while(StringUtils.isEmpty(msg)){
//			try {
//				Thread.sleep(1000);
//				msg=runLibDW.getResultStr().toString();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		if(StringUtils.isNotEmpty(msg)){
//			runLibDW.deleteResultStr(msg.length());
//		}
		resultMap.put("msg", result.toString());
		resultMap.put("isStop", isStop);
		return resultMap;
	}

	
	class ArchiveMainRunnable implements Runnable{
		private String libcode;
		private String dwCode;
		
		public ArchiveMainRunnable(String libcode, String dwCode) {
			this.libcode=libcode;
			this.dwCode=dwCode;
		}
		
		@Override
		public void run() {
			final Map<String,Object> validateMap=getValidateMap(ServiceCode.libcodeMap.get(libcode),ServiceCode.unitCodeMap.get(dwCode));
			try {
				int total=jsDBDao.queryTotalByFile(jsDBDao.getFileTableName(libcode, dwCode));
				int pages=total % IJSDBDao.ROWS==0 ? total/IJSDBDao.ROWS : total/IJSDBDao.ROWS+1;
				List<Map<String,Object>> fileList=null;
				logger.info("总页数："+pages);
				for (int k = 1; k <= pages; k++) {
					fileList=jsDBDao.query(jsDBDao.getFileTableName(libcode, dwCode),k);
					for (int j=0;fileList!=null && j<fileList.size();j++) {
						Map<String, Object> file=fileList.get(j);
						Map<String,Object> archiveLog=jsDBDao.queryArchiveLogByfileSyscode(file.get("SYSCODE")+"");
						if(archiveLog!=null &&  archiveLog.get("JSONSTR")==null){
							continue;
						}
						List<Map<String,Object>> efileList=null;
						efileList= jsDBDao.queryEFileByFileId(libcode, dwCode, String.valueOf(file.get("SYSCODE")));
						file.put("libcode", libcode);
						file.put("dwCode", dwCode);
						file.put("ZTC",StringUtils.right(StringUtils.trim(String.valueOf(file.get("WENHAO"))),5));
						runEveryFileArchive(validateMap, file,efileList);
					}
					//防止file线程过多
					RunLibAndDW runLibDW=IJSService.runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(libcode, dwCode));
					while(runLibDW.getAiNum()>7){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e1) {
				logger.error("查询数据库报错");
				logger.error(e1);
			}
			RunLibAndDW runLib;
			do{
				runLib=IJSService.runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(libcode, dwCode));
				logger.info("剩余待归档文件数："+runLib.getAiNum());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}while(runLib.getAiNum()>0);
			runLib.setStop();
			synchronized (runLib) {
				runLib.notifyAll();
				logger.info(String.format("dwCode: %s 唤醒其他等待任务。。。", String.valueOf(dwCode)));
			}
			String str=String.format("档案类型：%s，单位id:%s 归档结束!!!\r",libcode,dwCode);
			logger.info(str);
			runLib.setResultStr(str);
		}
	}

}
