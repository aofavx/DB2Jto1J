package com.twoToOneJi.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.xml.XMLSerializer;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLDom4jUtil {
	public static Map<String,Map<Object,Object>> modelMap=new HashMap<String,Map<Object,Object>>() ;
	
	public static Map<Object,Object> getModelMap(String xmlFileName){
		Map<Object,Object> map=modelMap.get(xmlFileName);
		if(map==null){
			map=initModelMap(xmlFileName);
			modelMap.put(xmlFileName, map);
		}
		return map;
	}
	/**
	 * 根据数据集合 和根节点名称,对象节点名称生成xml文件
	 * @param valueList 数据集合
	 * @param rootName 集合根节点名称
	 * @param chileRootName 对象根节点名称
	 * @param modelMap  xml模版
	 * @return xml文件
	 */
    public static String ListToXMLString(List valueList,String rootName,String chileRootName,Map<Object,Object> modelMap,String xsdName){
    	Document doc=DocumentHelper.createDocument();  
        doc.setXMLEncoding("UTF-8");  
        Element root=null;
        root=doc.addElement(rootName); 
//        root=doc.addElement(rootName,"http://www.example.org/TodosXMLSchema");  //命名空间
//        root.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema-instance");
//        root.addAttribute("xsd:noNamespaceSchemaLocation", xsdName);
        
        for (int i = 0; i < valueList.size(); i++) {
        	Map map=(Map) valueList.get(i);
        	Element childRoot=root.addElement(chileRootName);
        	addChildElement(map, childRoot,modelMap);
		}        
        String xmlStr=doc.asXML();
        System.out.println("-----xmlList-------: "+xmlStr);
		return xmlStr;  
    }
    
	/**
	 * 根据数据集合 和根节点名称生成xml文件
	 * @param map 数据
	 * @param rootName 根节点名称
	 * @param modelMap  xml模版
	 * @return xml文件字符串
	 */
    public static String MapToXMLString(Map map,String rootName,Map<Object,Object> modelMap){
    	Document doc=DocumentHelper.createDocument();  
        doc.setXMLEncoding("UTF-8");  
        Element root=doc.addElement(rootName);        
        addChildElement(map, root,modelMap);
        String xmlStr=doc.asXML();
        System.out.println("-----xml-------: "+xmlStr);
		return xmlStr;  
    }
    
    /**
     * 将数据对象根据xml模版加入到指定节点中
     * @param map
     * @param node 节点名称
     * @param modelMap  xml模版
     */
	public static void addChildElement(Map map, Element node,Map<Object,Object> modelMap) {
		for(Entry entry:modelMap.entrySet()){
			String key=(String) entry.getKey();
			Object modelValue=entry.getValue();
			if(modelValue==null||StringUtils.isNumeric(modelValue.toString())){
				//node.addElement(key).setText("");
			}else{
				String dataValue=map.get(modelValue)==null?"":map.get(modelValue).toString();
//				if("todo-sendpersonguid".equalsIgnoreCase(key)||"todo-receivepersonguid".equalsIgnoreCase(key)||"operator-guid".equalsIgnoreCase(key)){
//					dataValue=""+dataValue;
//				}
				if(!"".equals(dataValue)){
					node.addElement(key).setText(dataValue);
				}
			}
		}
	}
	
	/**
	 * 根据xml文件名初始化xml模版对象
	 * @param fileName
	 * @return map
	 * @exception null
	 */
	public static Map<Object,Object> initModelMap(String fileName){
    	InputStream is=XMLDom4jUtil.class.getClassLoader().getResourceAsStream(fileName);
    	try {
			Map map=Dom4JReadXML.getElementsByInputStream(is);
			return map;
		} catch (DocumentException e) {
			System.out.println("----------代办模版对应关系文件daibanModel.xml读取失败------------");
			e.printStackTrace();
			return null;
		}
    } 
    
	public static String Xml2Json(String xmlData) {
		return new XMLSerializer().read(xmlData).toString();
	}
}