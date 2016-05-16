package com.twoToOneJi.task;


import java.util.List;

import com.twoToOneJi.service.IJSService;
import com.twoToOneJi.util.SpringUtil;

/**
 * 归档任务类
 * @author jacob
 *
 */
public class ArchiveTask {
	public void runArchiveTask(){
		IJSService jsService=(IJSService) SpringUtil.getBean("jsService");
		List list=jsService.queryFileAll("M2", "0005");
		System.out.println(list.size());
	}
	
	
	public static void main(String[] args) {
		new ArchiveTask().runArchiveTask();
	}
}
