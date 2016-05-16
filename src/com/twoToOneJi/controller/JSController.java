package com.twoToOneJi.controller;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.twoToOneJi.po.RunLibAndDW;
import com.twoToOneJi.service.IJSService;
import com.twoToOneJi.util.ServiceCode;

@Controller
@RequestMapping("/")
public class JSController {
	static Logger logger=Logger.getLogger(JSController.class.getName());
	@Autowired
	private IJSService jsService;
	
//	@RequestMapping("/new")
//	public String newList(Model model,HttpSession session){
//		return "collect/list";  进入jsp文件夹下面的collect文件夹中的list.jsp文件
//	}
	
	@RequestMapping("/")
	public String query(Model model){
		List libcodeList=jsService.queryLibCode();
		List unitList=jsService.queryUnit();
		model.addAttribute("libcodeList", libcodeList!=null ? libcodeList : null);
		model.addAttribute("unitList", unitList!=null?unitList:null);
		return "query";  //进入jsp文件夹下面的collect文件夹中的list.jsp文件
	}
	
	
	@RequestMapping(value="/runArchive",method=RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String runArchiveTask(String libcode,String dwCodes){
		logger.info(libcode+"---"+dwCodes);
		String msg=null;
		List<String> dwList=new ArrayList<String>();
		if(StringUtils.equals(dwCodes,"all")){
			Set<String> keys=ServiceCode.unitCodeMap.keySet();
			for (String key : keys) {
				msg=jsService.ejArchiveVerify(libcode, key);
				if(StringUtils.isNotEmpty(msg)){
					return msg;
				}
				dwList.add(key);
			}
		}else{
			msg=jsService.ejArchiveVerify(libcode, dwCodes);
			if(StringUtils.isNotEmpty(msg)){
				return msg;
			}
			dwList.add(dwCodes);
		}
		new Thread(new ArchiveRunnableController(libcode, dwList, jsService)).start();
		return "归档开始...";
	}
	
	@RequestMapping(value="/getRunMsg",method=RequestMethod.GET,produces = "application/json; charset=utf-8")
	@ResponseBody
	public Map getRunMsg(String libcode,String dwCodes){
		Map resultJson=jsService.getRunMsg(libcode, dwCodes);
		return resultJson;
	}
	
	
	class ArchiveRunnableController implements Runnable{
		private String libcode;
		private  List<String> dwList;
		private IJSService jsService;
		
		public ArchiveRunnableController(String libcode, List<String> dwList, IJSService jsService) {
			super();
			this.libcode = libcode;
			this.dwList = dwList;
			this.jsService=jsService;
		}
		
		@Override
		public void run() {
			Iterator dwCodeIt=dwList.iterator();
			while (dwCodeIt.hasNext()) {
				String dwCode = (String) dwCodeIt.next();
				jsService.ejArchiveToYJ(libcode, dwCode);
				RunLibAndDW runLib=IJSService.runLibAndDWMap.get(ServiceCode.getLibAndDWToKey(libcode,dwCode));
				synchronized (runLib) {
					while(!runLib.isStop()){
						try {
							runLib.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}

