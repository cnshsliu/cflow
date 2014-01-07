<%@page import="com.lkh.cflow.*" %>
<%@ page session="false" %>
<%request.setCharacterEncoding("UTF8"); response.setHeader("Pragma","No-cache"); response.setHeader("Cache-Control","no-cache"); response.setDateHeader("Expires", 0);%>
<html> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Workflow Engine as a Service</title>
<LINK href="/cflow/rsstyle.css" rel="stylesheet" type="text/css">
<script src="javascript/mootools.js" language="javascript"></script>
</head> <body>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
<%
DbAdmin dbadmin = DbAdminPool.get();

	String wftid = "6ced6bfac8c74e3d821fb65cff435f73";
		String nodeid = "14D169BCF935982EAD17EAF96CB21650";
		dbadmin.newScriptTask(wftid, nodeid, "{\"days\":5, \"reason\":\"回家\"}");
%>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body></html>

