<%@page import="com.lkh.cflow.*" %>
<%@ page session="false" %>
<%request.setCharacterEncoding("UTF8"); response.setHeader("Pragma","No-cache"); response.setHeader("Cache-Control","no-cache"); response.setDateHeader("Expires", 0);%>
<%String token = request.getParameter("token"); if(token==null || token.equalsIgnoreCase("null"))response.sendRedirect("/cflow/login.jsp?target=wl.jsp");%><%@ include file="includeHeader2.jsp"%>
<html> <head> <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Workflow Engine as a Service</title>
<LINK href="/cflow/rsstyle.css" rel="stylesheet" type="text/css">
<script src="javascript/mootools.js" language="javascript"></script>
</head> <body>
<CENTER>
     <table style="width:800px; align=center" ID="top_menu">
        <tr>
		<td valign="top" align="left"><a href='/cflow/index.html'><img src="images/logo3764.png" alt="My World Flow" border="0"></a>
		</td>
		<td class="menu">
			<a href="/cflow/wft.jsp?token=<%=token%>">
				<img src='images/menu1.png' border=0 onMouseOver="this.src='images/menu1_s.png'" onMouseOut="this.src='images/menu1.png'"></a>
			<a href="/cflow/prc.jsp?token=<%=token%>">
				<img src='images/menu2.png' border=0 onMouseOver="this.src='images/menu2_s.png'" onMouseOut="this.src='images/menu2.png'"></a>
			<a href="/cflow/team.jsp?token=<%=token%>">
				<img src='images/menu3.png' border=0 onMouseOver="this.src='images/menu3_s.png'" onMouseOut="this.src='images/menu3.png'"></a>
			<a href="/cflow/vt.jsp?token=<%=token%>">
				<img src='images/menu4.png' border=0 onMouseOver="this.src='images/menu4_s.png'" onMouseOut="this.src='images/menu4.png'"></a>
			<a href="/cflow/testbed.jsp?token=<%=token%>">
				<img src='images/menu5.png' border=0 onMouseOver="this.src='images/menu5_s.png'" onMouseOut="this.src='images/menu5.png'"></a>
		</td>
    <td align="right">
		<div id="devConsole">Developer Console</div>
    </td>
        </tr>
    </table>
</CENTER>
