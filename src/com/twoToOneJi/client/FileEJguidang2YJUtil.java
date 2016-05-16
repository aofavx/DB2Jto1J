package com.twoToOneJi.client;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import net.sf.json.xml.XMLSerializer;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.twoToOneJi.util.ServiceCode;

public class FileEJguidang2YJUtil {
	public static String URL="http://192.168.0.223:7001/ams-web/service/IDataWebService";
	public static String METHOD="inputAMS";
	public static String DATE="{\"validate\":{\"libcode\":\"674\",\"libname\":\"文书档案（盒）\",\"companyid\":\"GWBB\",\"companyname\":\"国网公司总部\"," +
			"\"orgnationid\":\"GWBB_010600\",\"orgnationname\":\"文档处\",\"prjcode\":\"2013\",\"sjly\":\"OA\"},\"file\":{\"oaid\":\"vxid001\",\"bgqx\":" +
			"\"C_长期\",\"flh\":\"11\",\"flmc\":\"党政工作\",\"mj\":\"M_秘密\",\"wenhao\":\"�?29号\",\"title\":\"关于加强安全防护的�?知\",\"ztc\":\"安全防护\"" +
			",\"zrz\":\"办公室文档处\",\"lry\":\"刘静\",\"lrrq\":\"2013-01-23\",\"wjlx\":\"1_收文\",\"swlsh\":\"0001\",\"cwrq\":\"2013-01-25\"," +
			"\"wz\":\"D_正文\",\"jjcd\":\"1_收文\",\"remark\":\"备注\",\"attrex\":\"3\",\"pagenum\":\"2\",\"usersys\":[\"xiao-zhou\",\"liujing\",\"libing\",\"zhangxia\"]}," +
			"\"efiles\":[{\"documentid\":\"090f1b318001604d\",\"filename\":\"部署手册副本.doc\",\"filetype\":\"正文\",\"filesize\":\"1024\"," +
			"\"manutype\":\"正式电子文件\"},{\"documentid\":\"090f1b318001604c\",\"filename\":\"招聘.txt\",\"filetype\":\"附件\",\"filesize\":\"100\"," +
			"\"manutype\":\"正式电子文件\"}]}";
	public static void main(String[] args) throws AxisFault{
/*		RPCServiceClient serviceClient=new RPCServiceClient();
		Options options=serviceClient.getOptions();
		//  指定调用WebService的URL  
		EndpointReference targetEPR=new EndpointReference(URL);
		options.setTo(targetEPR);
		//  指定testWebsServiceOne方法的参数
//		String DATE=get2JGuiDangT1JXml();
//		DATE=Xml2Json(DATE);
		System.out.println(DATE);
		Object[] values=new Object[]{DATE};
		//  指定testWebsServiceOne方法返回值的数据类型的Class对象
		Class[] classes=new Class[]{String.class};
		//  指定要调用的testWebsServiceOne方法及WSDL文件的命名空
		QName entry=new QName("http://service.archive.sgcc.com",METHOD);
		//  调用sayHelloToPerson方法并输出该方法的返回值
		Object[] result=serviceClient.invokeBlocking(entry, values, classes);
		System.out.println(result[0]);
		try {
			JSONObject jo=new JSONObject(String.valueOf(result[0]));
			System.out.println("success:"+jo.getString("success"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
*/
		String json="{\"id\":\"10030001\"}";
		String s="";
		s=callYJWebService(json,"http://192.168.0.223:7001/ams-web/service/ISystemInfoService","getUnit");
		System.out.println(s);
		json="{\"unitid\":\"10030001\",\"libcode\":\"72\",\"prjcode\":\"2015\"}";
		System.out.println("----------");
		s=callYJWebService(json,"http://192.168.0.223:7001/ams-web/service/ISystemInfoService","getJGDM");
		System.out.println(s);
//		String DATE=get2JGuiDangT1JXml();
//		DATE=Xml2Json(DATE);
//		System.out.println(DATE);
	}

	/**
	 * 根据webservice地址盒接口名称调用一级部署的webservice服务
	 * @param jsonString 请求的json字符串
	 * @param webServiceAddress webservice接口地址
	 * @param method 方法名
	 * @return String
	 * @throws AxisFault
	 */
	public static String callYJWebService(String jsonString,String webServiceAddress,String method) throws AxisFault{
		RPCServiceClient serviceClient=new RPCServiceClient();
		Options options=serviceClient.getOptions();
		//  指定调用WebService的URL  
		EndpointReference targetEPR=new EndpointReference(webServiceAddress);
		options.setTo(targetEPR);
		//  指定testWebsServiceOne方法的参数
		Object[] values=new Object[]{jsonString};
		//  指定testWebsServiceOne方法返回值的数据类型的Class对象
		Class[] classes=new Class[]{String.class};
		//  指定要调用的testWebsServiceOne方法及WSDL文件的命名空
		QName entry=new QName("http://service.archive.sgcc.com",method);
		//  调用sayHelloToPerson方法并输出该方法的返回值
		Object[] result=serviceClient.invokeBlocking(entry, values, classes);
		return String.valueOf(result[0]);
		
	}

	/**
	 * 获取归档表结构对应关系xml文件
	 * @return
	 */
	private static String get2JGuiDangT1JXml(){
		StringBuilder xmlStr=new StringBuilder();
		InputStream is=FileEJguidang2YJUtil.class.getClassLoader().getResourceAsStream("2jiguidangTo1ji.txt");
		try {
			byte[] bt=new byte[1024];
			int readInt=-1;
			int t=1;
			while ((readInt=is.read(bt))!=-1) {
//				xmlStr.append(new String(bt, t==1 ? 0+3:0, t==1 ? readInt-3 : readInt));
				xmlStr.append(new String(bt, 0,readInt));
				t++;
			}
			is.close();
			System.out.println("xmlStr--->  "+xmlStr.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xmlStr.toString();
	}
}