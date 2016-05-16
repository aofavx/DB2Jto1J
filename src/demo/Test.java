package demo;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.vx.xmlModelToData.convertor.ModelXML;

public class Test {
	public static void main(String[] args) {
		try {
			ModelXML model=new ModelXML("map.xml");
			Map map=new HashMap();
			map.put("DOCUMENTID", "1234");
			map.put("FILENAME", "安");
			map.put("FILETYPE", "");
			map.put("FILESIZE", 1234);
			String xmlStr=model.getDataXML(map);
			System.out.println(xmlStr);
			System.out.println(ModelXML.Xml2Json(xmlStr));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		System.out.println("结束");
	}
}