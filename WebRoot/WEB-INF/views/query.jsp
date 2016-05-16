<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String contextPath=request.getContextPath();
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<script type="text/javascript" src="<%=contextPath%>/resources/js/jquery-1.4.3.js">
</script>
<script type="text/javascript">
	$(function() {
		$("#query").click(function() {
			var libcode = $("#libcode").val();
			var dwCode = $("#dwCode").val();
			var ok = true;//表单是否通过检查
				if (ok) {
					$("#info").text("归档进行中...");
					var u = "<%=contextPath%>/runArchive?libcode=" + libcode+ "&dwCodes=" + dwCode;
					$.ajax( {
						url : u,
						type : "get",
						dataType : "html",
						success : function(msg) {
							$("#result").text(msg.msg);
							document.getElementById("result").scrollTop = document.getElementById("result").scrollHeight
							window.setTimeout("queryMsg(\""+libcode+"\",\""+dwCode+"\")", 2000);
						},
						error : function() {
							$("#info").text("归档出现错误");
						}
					});
				}
			})
	});

	function queryMsg(libcode,dwCode){
		var u = "<%=contextPath%>/getRunMsg?libcode=" + libcode+ "&dwCodes=" + dwCode;
			$.ajax( {
				url : u,
				type : "get",
				dataType : "json",
				success : function(msg) {
					$("#result").text($("#result").text()+msg.msg);
					document.getElementById("result").scrollTop = document.getElementById("result").scrollHeight
					if(msg.isStop==true || msg.isStop=="true"){
						$("#info").text("归档结束.");
					}else{
						window.setTimeout("queryMsg(\""+libcode+"\",\""+dwCode+"\")", 3000);
					}
				},
				error : function(json) {
					$("#info").text("归档出现错误");
				}
			});
	}

</script>
		<style type="text/css">
body {
	TEXT-ALIGN: center;
}
</style>
	</head>
	<body>
		档案类型
		<select id="libcode" name="libcode">
			<c:if test="${libcodeList!=null}">
				<c:forEach items="${libcodeList}" var="libcode" varStatus="n">
						<option value='${libcode.LIBCODE}'>${libcode.CHNAME}</option>
				</c:forEach>
			</c:if>
			<c:if test="${libcodeList==null}">
				<option value=''>libcode</option>
			</c:if>
   		</select>
		单位编号:
   		<select id="dwCode" name="dwCode">
   			<option value='all'>全部</option>
			<c:if test="${unitList!=null}">
				<c:forEach items="${unitList}" var="unit" varStatus="n">
						<option value='${unit.SYSCODE}'>${unit.UNITNAME}</option>
				</c:forEach>
			</c:if>
   		</select>
		<input type="button" id="query" value="开始归档">
	<!--  <input type="button" id="query" value="测试" onclick="demostart()"> -->	
		<p id="info" style="color: red; margin: 0 0"></p>
		<textarea id="result" rows="25" cols="100"></textarea>
	</body>
</html>
<script>
	function demorun(){
		var myDate = new Date();
		console.log(myDate);
		window.setTimeout("demorun()",3000);
	}
	function demostart(){
		console.log("start...");
		window.setTimeout("demorun()",3000);
	}
</script>
