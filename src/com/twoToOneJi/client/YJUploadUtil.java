package com.twoToOneJi.client;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sgcc.uds.rsa.client.UdsEncrypt;
import com.twoToOneJi.util.ServiceCode;

public class YJUploadUtil{
	protected final Logger logger=Logger.getLogger(getClass());
	
	private static YJUploadUtil yjUtil=null;
	/**
	* 上传接口 -添加文档
	* @param fileProperty  文档属性json串
	* @param filePath  文件路径
	* @return
	 * @throws Exception 
	*/
	public Map add(String fileProperty, String filePath) throws Exception {
		logger.info("上传文件到一级系统非结构化：fileProperty:"+fileProperty+" ,filePath:"+filePath);
		Map param=new HashMap();
		// 设置传输协议方式
		EndpointReference targetEPR = new EndpointReference(ServiceCode.getRequestAddress());
		ServiceClient serviceClient = new ServiceClient();
		addOptions(targetEPR, serviceClient);
		OMFactory fac = OMAbstractFactory.getOMFactory();
		// 区域名称可以为空
		OMNamespace omNs = fac.createOMNamespace("", "");
		addHeader(serviceClient, fac, omNs);
		// 增加文档
		OMElement data = fac.createOMElement("add", omNs);
		// 增加元数据
		OMElement metaData = fac.createOMElement("in0", omNs);
		metaData.setText(fileProperty);
		data.addChild(metaData);
		// 增加附件
		OMElement fileData = fac.createOMElement("in1", omNs);
		File file = new File(filePath);
		FileDataSource fs = new FileDataSource(file);
		DataHandler fileHandle = new DataHandler(fs);
		OMText textData = fac.createOMText(fileHandle, true);
		fileData.addChild(textData);
		data.addChild(fileData);
		// 发送请求
		OMElement results = serviceClient.sendReceive(data);
		Iterator it = results.getChildElements();
		while (it.hasNext()) {
			OMElement omElement = (OMElement) it.next();
			Iterator<OMElement> childElements = omElement.getChildElements();
			while (childElements.hasNext()) {
				OMElement element = childElements.next();
				// fileid:文件id,如果失败为null
				// message：是否成功的信息，如果失败返回异常信息
				// success：是否成功，true为是，false为否
				// versionfileid:是版本id
				// messageCode:错误编码
				param.put(element.getLocalName(),element.getText());
			}
		}
		return param;
	}
	
	/**
	* 删除文档
	* @param fileProperty   文档属性json串
	* @return
	*/
	public Map delete(String fileProperty) {
		Map param=new HashMap();
		try {
			EndpointReference targetEPR = new EndpointReference(ServiceCode.getRequestAddress());
			ServiceClient serviceClient = new ServiceClient();
			addOptions(targetEPR, serviceClient);
			OMFactory fac = OMAbstractFactory.getOMFactory();
			// 区域名称可以为空
			OMNamespace omNs = fac.createOMNamespace("", "");
			addHeader(serviceClient, fac, omNs);
			// 删除文档
			OMElement data = fac.createOMElement("delete", omNs);
			// 删除元数据
			OMElement metaData = fac.createOMElement("in0", omNs);
			metaData.setText(fileProperty);
			data.addChild(metaData);
			// 发送请求
			OMElement results = serviceClient.sendReceive(data);
			Iterator it = results.getChildElements();
			while (it.hasNext()) {
				OMElement el = (OMElement) it.next();
				Iterator<OMElement> childElements = el.getChildElements();
				while (childElements.hasNext()) {
					OMElement element = childElements.next();
					// message：是否成功的信息，如果失败返回异常信息
					// success：是否成功，true为是，false为否
					// messageCode:错误编码
					param.put(element.getLocalName(),element.getText());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return param;
	}
	

	/**
	 * 为SOAP Header构造验证信息
	 * @param serviceClient
	 * @param fac
	 * @param omNs
	 * @throws Exception
	 */
	private void addHeader(ServiceClient serviceClient, OMFactory fac,OMNamespace omNs) throws Exception {
		OMElement header = fac.createOMElement("AuthenticationToken", omNs);
		OMElement ome_user = fac.createOMElement("Username", omNs);
		ome_user.setText(ServiceCode.getUsername());//用户名
		header.addChild(ome_user);
		OMElement ome_pass = fac.createOMElement("Password", omNs);
		ome_pass.setText(ServiceCode.getPassword());//密码
		header.addChild(ome_pass);
		OMElement ome_checkNo = fac.createOMElement("SysCheckNo", omNs);
		ome_checkNo.setText(ServiceCode.getCheck_no());//系统验证码
		header.addChild(ome_checkNo);
		
		//若使用key方式认证，则需要增加EncryptData节点
		if(StringUtils.isNotBlank(ServiceCode.getPublic_key_info())){
			addEncryptDate(fac, omNs, header);				
		}
		serviceClient.addHeader(header);
	}

	/**
	 * 添加设置选项
	 * @param targetEPR
	 * @param serviceClient
	 */
	private void addOptions(EndpointReference targetEPR,ServiceClient serviceClient) {
		Options options = new Options();
		options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		options.setTo(targetEPR);
		options.setTimeOutInMilliSeconds(600000);//设置超时时间
		options.setProperty(Constants.Configuration.ENABLE_MTOM,Constants.VALUE_TRUE);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		serviceClient.setOptions(options);
	}

	/**
	 * 使用SG-UDS提供的加密工具API生成加密信息
	 * @param fac
	 * @param omNs
	 * @param header
	 * @throws Exception
	 */
	private void addEncryptDate(OMFactory fac, OMNamespace omNs,OMElement header) throws Exception {
		String encryptData="";				
		try{
			//登录用户ID,必须为统一权限/统一目录的用户
			String ldapUserLoginName="test01";
			//目录登录用户密码,必须为统一权限/统一目录的用户对应的密码
			String ldapUserPassword="test01";
			Date date=new Date();
			encryptData=UdsEncrypt.encrypt(ServiceCode.getPublic_key_info(), ServiceCode.getCheck_no(), ldapUserLoginName, ldapUserPassword, date);
		}catch(Exception e){
			throw e;
		}
		//设置加密后的字符串参数
		OMElement ome_EncryptData = fac.createOMElement("EncryptData", omNs);				
		ome_EncryptData.setText(encryptData);
		header.addChild(ome_EncryptData);
	}
	
	public static YJUploadUtil getUtil(){
		if(yjUtil==null){
			yjUtil=new YJUploadUtil();
		}
		return yjUtil;
	}
	
	public static void main(String[] args) {
		try {
			YJUploadUtil axis = YJUploadUtil.getUtil();
			//文档属性json串
			String fileProperty="{'file_name':['部署手册副本'],'file_type':['doc'],'object_type':'ecm_document'}";
			String filePath="E:\\iot\\文档\\部署手册副本.doc";//文件路径
			Map param=axis.add(fileProperty,filePath);//调用添加方法
			System.out.println("是否成功:"+param.get("success"));//是否成功
			System.out.println("消息:"+param.get("message"));//消息
			System.out.println("文档ID:"+param.get("fileid"));//文档ID  090f1b318001604c,090f1b318001604d,090f1b3180016050
			System.out.println(param.get("versionfileid"));//版本ID
			System.out.println(param.get("messageCode"));//错误编码
			
			if("true".equals(param.get("success"))||"false".equals(param.get("success"))){
				System.out.println("isString");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
