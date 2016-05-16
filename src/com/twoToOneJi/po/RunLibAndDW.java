package com.twoToOneJi.po;

import java.util.concurrent.atomic.AtomicInteger;

public class RunLibAndDW{
	private boolean isStop=false;
	private StringBuffer resultStr=new StringBuffer();
	private  AtomicInteger ai=new AtomicInteger(0);
	
	
	public void setStop() {
		this.isStop = true;
	}
	
	public boolean isStop() {
		return isStop;
	}
	public StringBuffer getResultStr() {
		return resultStr;
	}
	public void setResultStr(String str) {
		synchronized (resultStr) {
			this.resultStr.append(str);
		}
	}
	
	public void deleteResultStr(int length) {
		synchronized (resultStr) {
			this.resultStr.delete(0, length);
		}
	}
	
	public int getAiNum() {
		return ai.get();
	}
	
	/**
	 * 设置ai计算器加一
	 */
	
	public void addAi() {
		ai.getAndIncrement();
	}
	
	/**
	 * 设置ai计算器减一
	 */
	public void decAi() {
		ai.getAndDecrement();
	}
}