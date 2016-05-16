package com.twoToOneJi.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ProcessingInstruction;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

public class Dom4JReadXML {
	/**
	 * 根据文件流读取xml文件，返回document对象
	 * @param in 文件读取流
	 * @return Document对象
	 * @throws DocumentException  文件读取失败
	 */
	public static Document readXML(InputStream in) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(in);
		return document;
	}
	
	/**
	 * 读取Docment的根节点
	 * @param document
	 * @return 根节点Element对象
	 */
	public static Element getRoot(Document document){
		Element root = document.getRootElement();
		return root;
	}
	
	/**
	 * 读取根节点下的所有子节点集合
	 * @param root 节点对象
	 * @return 子节点集合
	 */
	public static List getElements(Element root){
		List<Element> childList = root.elements();
        return childList;
	}
	
	/**
	 * 读取节点名称相同的第一个节点对象
	 * @param root 父节点对象
	 * @param elementName 节点名称
	 * @return 第一个名称相同的子节点对象
	 */
	public static Element getElementByName(Element root,String elementName){
		 Element firstElement = root.element(elementName);
		 return firstElement;
	}
	
	/**
	 * 根据节点名称，读取所有同名的子节点
	 * @param root 父节点对象
	 * @param elementName 节点名称
	 * @return 同名的子节点集合
	 */
	public static List getElementsByName(Element root,String elementName){
		List<Element> childList = root.elements(elementName);
		return childList;
	}
	
	/**
	 * 读取节点包含的所有属性对象
	 * @param el 节点名称
	 * @return
	 */
	public static List getElementByName(Element el){
		List attributes = el.attributes();
		 return attributes;
	}
	/**
	 * 根据文件流读取xml中的所有节点(父节点除外)
	 * @param in 文件流
	 * @return
	 * @throws DocumentException 
	 */
	public static Map getElementsByInputStream(InputStream in) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(in);
		MyVistor vistor=new MyVistor();
		document.accept(vistor);
		return vistor.getElementsMap();
	}

}

class MyVistor extends VisitorSupport {  
	private Map<Object,Object> elementsMap=new HashMap();
	
	//输出属性调用方法
    public void visit(Attribute node) {  
        System.out.println("Attibute: " + node.getName() + "="  + node.getValue());  
    }  
   //输出节点调用方法
    public void visit(Element node) {  
        if (node.isTextOnly()) {  
            System.out.println("Element: " + node.getName() + "="  + node.getText());  
            this.elementsMap.put(node.getName(), node.getText());
        } else {  
            System.out.println(node.getName());  
        }  
    }  
    @Override  
    public void visit(ProcessingInstruction node) {  
        System.out.println("PI:" + node.getTarget() + " " + node.getText());  
    }
    
	public Map getElementsMap() {
		return elementsMap;
	}
	public void setElementsMap(Map elementsMap) {
		this.elementsMap = elementsMap;
	}
	
}