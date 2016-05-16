package com.twoToOneJi.service.impl;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.twoToOneJi.client.FileEJguidang2YJUtil;
import com.twoToOneJi.service.IAMSService;
import com.twoToOneJi.util.Dom4JReadXML;
import com.twoToOneJi.util.ServiceCode;
import com.twoToOneJi.util.XMLDom4jUtil;
/**
 * 一级部署系统功能处理实现类
 * @author jacob
 *
 */
@Service("amsService")
public class AMSServiceImpl implements IAMSService {
	Logger logger=Logger.getLogger(this.getClass().getName());

	@Override
	public String archiveToYJ(Map<String, Object> validateMap,Map<String, Object> file,List<Map<String, Object>> efileList) throws Exception {
		String dataJson=null;
		try {
			//---根据xml转换成json------
			Document doc = DocumentHelper.createDocument();
			try {
                Element root = doc.addElement("root");
				InputStream is=AMSServiceImpl.class.getClassLoader().getResourceAsStream("2jiguidangTo1ji.xml");
				Document document=Dom4JReadXML.readXML(is);
				Element validateEl=Dom4JReadXML.getElementByName(document.getRootElement(), "validate");
				fillValueToElement(validateMap, validateEl,root);
				Element fileEl=Dom4JReadXML.getElementByName(document.getRootElement(), "file");
				fillValueToElement(file, fileEl,root);
				if(efileList!=null && efileList.size()>0){
					Element efileEl=Dom4JReadXML.getElementByName(document.getRootElement(), "efiles");
					for (Map<String, Object> map : efileList) {
						fillValueToElement(map, efileEl,root);
					}
					root.addElement("efiles");
				}
			} catch (DocumentException e1) {
				logger.error("--------xml转json出现错误---------");
				logger.error(e1);
				throw e1;
			}
			//-------------------------
			System.out.println("xml格式数据："+doc.asXML());
			dataJson=XMLDom4jUtil.Xml2Json(doc.asXML());
			dataJson=StringUtils.replace(dataJson,",[]]}","]}");
			dataJson=StringUtils.replace(dataJson,":null",":\"\"");
			String resultString=FileEJguidang2YJUtil.callYJWebService(dataJson,ServiceCode.yjAMSAddress,ServiceCode.yjMethod);
			try {
				JSONObject jo = new JSONObject(String.valueOf(resultString));
				if("true".equalsIgnoreCase(jo.getString("success"))){
					dataJson=null;
				}else{
					dataJson+="   JSON："+jo.getString("message");
					logger.error("file信息归档到一级部署失败："+file.get("SYSCODE")+".数据：   "+dataJson);
				}
			} catch (JSONException e) {
				dataJson="返回的数据，JSON解析失败.";
				logger.error(dataJson+e);
				throw e;
			}
		} catch (AxisFault e) {
			dataJson="file信息归档到一级部署失败。";
			logger.error(dataJson);
			logger.error(e);
			throw e;
		}
		return dataJson;
	}
	

	private void fillValueToElement(Map<String, Object> validateMap,Element element1,Element root) {
		Element element=(Element)element1.clone();
		for( Iterator it=element.elementIterator();it.hasNext();){
			Element node=(Element) it.next();
			String name=node.getName();
			String value=node.getText();
			if("manutype".equals(name)){
				continue;
			}
			if(StringUtils.isNotEmpty(value)){
				String text=validateMap.get(value)+"";
				//设置默认字符为特殊的一个字符。在转换成json时
				if(StringUtils.isNotEmpty(text)){
					node.setText(text);
				}
				if("null".equals(text)){
					node.getParent().remove(node);
				}
			}
		}
		root.add(element);
	}

	public String getGBDM(String libcode, String unitSys, String nd) {
		String dataJson="{\"unitid\":\""+unitSys+"\",\"libcode\":\""+libcode+"\",\"prjcode\":\""+nd+"\"}";
		try {
			return FileEJguidang2YJUtil.callYJWebService(dataJson,ServiceCode.yjAMSAddress,ServiceCode.yjMethod);
		} catch (AxisFault e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static void main(String[] args) throws AxisFault {
		String dataJson="";
		FileEJguidang2YJUtil.callYJWebService(dataJson,"http://192.168.0.125:7001/ams-web/service/IZlkWebserivice","inputZLK");
	}

}
