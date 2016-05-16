package com.twoToOneJi.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import com.twoToOneJi.dao.IJSDBDao;
import com.twoToOneJi.util.ServiceCode;
import com.twoToOneJi.util.SpringUtil;

/**
 * 江苏二级系统数据库读写实现类
 * @author jacob
 *
 */
@Repository("jsDBDao")
public class JSDBDaoImpl implements IJSDBDao {
	Logger logger=Logger.getLogger(this.getClass().getName());
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public List<Map<String,Object>> query(String tablename, int pageNum) {
		List<Map<String,Object>> list=jdbcTemplate.queryForList("select * from (select * from (select a.*,rownum rn from "+tablename+" a where status=0 ) order by rownum) where rn between ? and ?",new Object[]{(pageNum-1)*ROWS+1,ROWS*pageNum});
		return list;
	}
	@Override
	public List<Map<String,Object>> queryEFileByFileId(String libcode, String dwCode,
			String fileSyscode) {
		String tname="D_EFILE"+libcode+"_"+dwCode;
		String sql="select * from "+tname+" where psyscode=?";
		List<Map<String,Object>> list=jdbcTemplate.queryForList(sql,new Object[]{fileSyscode});
		return list;
	}
	@Override
	public Map<String, Object> queryFileById(String libcode, String dwCode, String fileSyscode) {
		String sql="select * from D_FILE"+libcode+"_"+dwCode+" where syscode=? and status=0 ";
		System.out.println(sql);
		Map<String, Object> map=jdbcTemplate.queryForMap(sql,fileSyscode);
		 return map;
	}
	@Override
	public void addArchiveLog(Map<String, Object> file,List<Map<String, Object>> efileList, String guidangResult) {
		String sysCode=(String) file.get("SYSCODE");
		String eSysCode="";
		String documentId="";
		if(efileList!=null && efileList.size()>0){
			for(Map<String,Object> efile:efileList ){
				String e = (String) efile.get("SYSCODE");
				String d=(String) efile.get(e);
				eSysCode+=(e+",");
				documentId+=(d+",");
			}
			eSysCode = eSysCode.substring(0, eSysCode.length()-1);
			documentId =documentId.substring(0, documentId.length()-1);
		}
		jdbcTemplate.update("insert into archivelog values(?,?,?,?)",new String[]{sysCode,eSysCode,documentId,guidangResult});
	}
	
	@Override
	public Map<String, Object> queryArchiveLogByfileSyscode(String fileSyscode) {
		Map<String, Object> map=null;
		try {
			map=jdbcTemplate.queryForMap("select * from archiveLog where syscode=?",fileSyscode);
		} catch (Exception e) {
		}
		return map;
	}
	
	@Override
	public void updateArchiveLogByfileSyscode(String fileSyscode,String efileSyscodes, String documentIds, String jsonStr) {
		jdbcTemplate.update("update archivelog set esyscode=?, documentId=?, jsonstr=? where syscode=?",new String[]{efileSyscodes,documentIds,jsonStr,fileSyscode});
	}
	
	@Override
	public Map<String, Object> queryArcByLibcodeId(String libcodeId) {
		Map<String, Object> map=null;
		try {
			map=jdbcTemplate.queryForMap("select * from S_ARC where libcode=?", libcodeId);
		} catch (Exception e) {
		}
		return map;
	}
	@Override
	public int queryTotalByFile(String tableName) {
		return jdbcTemplate.queryForObject("select count(*) from "+tableName +" where status=0" ,Integer.class );
	}
	
	@Override
	public List<Map<String,Object>> queryFieldByFile(String fieldName,String tableName) {
		 List<Map<String,Object>> list=jdbcTemplate.queryForList("select distinct "+fieldName+" from "+tableName);
		 return list;
	}
	
	
	public static void main(String[]args){
		JSDBDaoImpl jsdb=new JSDBDaoImpl();
		jsdb.jdbcTemplate=(JdbcTemplate) SpringUtil.getBean("jdbcTemplate");
//		Map<String, Object> file=new HashMap<String, Object>();
//		List<Map<String, Object>> efileList=new ArrayList<Map<String, Object>>();
//		String guidangResult="s";
//		file.put("SYSCODE", "2222222222222222");
//		Map<String, Object> e1=new HashMap<String, Object>();
//		e1.put("SYSCODE","333333333333");
//		e1.put("333333333333", "ddddddddd");
//		Map<String, Object> e2=new HashMap<String, Object>();
//		e2.put("SYSCODE","4444444444444");
//		e2.put("4444444444444", "fffffffffff");
//		efileList.add(e1);
//		efileList.add(e2);
//		jsdb.addArchiveLog(file, efileList, guidangResult);
		jsdb.updateArchiveLogByfileSyscode("2222222222222222", "dfdlfdl,fddsfsd,fsdsfd", "fdfsdf,dfdsff,dfsfds", "FDFSDF");
	}
	
	@Override
	public String getFileTableName(String libcode, String unitId) {
		return "D_File"+libcode+"_"+unitId;
	}

}
