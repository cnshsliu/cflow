<%@page import="com.lkh.cflow.*" %>
<%@ page session="false" %>
<%request.setCharacterEncoding("UTF8"); response.setHeader("Pragma","No-cache"); response.setHeader("Cache-Control","no-cache"); response.setDateHeader("Expires", 0);%>
<%String token = request.getParameter("token"); if(token==null || token.equalsIgnoreCase("null"))response.sendRedirect("/cflow/login.jsp?target=wl.jsp");%><%@ include file="includeHeader2.jsp"%>
<html> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Workflow Engine as a Service</title>
<LINK href="/cflow/rsstyle.css" rel="stylesheet" type="text/css">
<script src="javascript/mootools.js" language="javascript"></script>
</head> <body>
<%@ include file="console_menu.jsp"%>
