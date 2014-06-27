<%@ page session="false" %>
<%request.setCharacterEncoding("UTF8"); response.setHeader("Pragma","No-cache"); response.setHeader("Cache-Control","no-cache"); response.setDateHeader("Expires", 0);%>
<%String token = request.getParameter("token"); if(token==null || token.equalsIgnoreCase("null")) token=null;%>
<html> <head>
	<title>Workflow Engine as a Service</title>
<meta content="Workflow, BPM, PaaS Service, Cloud Service, Process Management, Business Process, MyWorldFlow" name="keywords">
<meta content="MyWordflow is a PaaS (Platform as a Service) cloud service, provides Workflow Engine as a Service to developers, MyWorldFlow can be used in BPM" name="description">
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<LINK href="/cflow/rsstyle.css" rel="stylesheet" type="text/css">
  <link rel="stylesheet" href="javascript/jquery-ui/jquery-ui.css">
  <script src="javascript/jquery-1.11.1.min.js"></script>
  <script src="javascript/jquery-ui/jquery-ui.js"></script>
  <script>
  $(function() {
    $( "a[class=a2button], button" )
      .button()
      .click(function( event ) {
        //event.preventDefault();
      });
  });
  </script>
</head>
<body>
<div class="site_container clearfix develops">
	<div class="nav">
	<div class="row">
		<table class="topbar clearLink">
			<tr><td>
			<a href="/cflow" border="0"><img src="images/logo3764.png" alt="My World Flow Alpha" border="0"></a>
			</td><td class="onright">
			<a href="reg.jsp">Sign up</a> | <a href="login.jsp">Sign in </a>
		</td></tr></table>
	</div>
</div>
	<div class="row top20"><center>
	<h1>Business Process as a Service</h1>Design, run and monitor your business processes.
	</center>	
	</div>
	<div class="row top20">
	<center>
		<table><tr><td>
			<a  class="a2button" style="width:194px; height:120px; display:table-cell; vertical-align:middle;"  href="/cflow/Navigate?to=/cflow/wft.jsp&token=<%=token%>">Design workflow</a>
		</td><td>
			<a  class="a2button" style="width:194px; height:120px; display:table-cell; vertical-align:middle;"   href="/cflow/Navigate?to=/cflow/prc.jsp&token=<%=token%>">Monitor process</a>
		</td></tr>
		<tr><td>
			<a  class="a2button"  style="width:194px; height:120px; display:table-cell; vertical-align:middle;" href="/cflow/Navigate?to=/cflow/ctx.jsp&token=<%=token%>">Configure your business context</a>
		</td><td>
			<a  class="a2button" style="width:194px; height:120px; display:table-cell; vertical-align:middle;" href="/cflow/Navigate?to=/cflow/api.jsp&token=<%=token%>">API and Demos</a>
		</td></tr>
		</table>
		</center>
	</div>
	<!-- div class="row top20">
			<a class="platform_choice_link" href="tutorial/quickstart.html">
						  <p><span class="link bb10">云流程介绍文档</span> </p>
			<a class="platform_choice_link" href="tutorial/api.html">
						  <p><span class="link bb10">查看API及SDK</span> </p>
			<a class="platform_choice_link" href="tutorial/examples.html">
						  <p><span class="link bb10">查看流程开发案例</span> </p>
	</div  -->
   
	<div class="row">
<div id="footer"><center><BR><BR>
      Copyright &#169; 2012 The MyWorldFlow.</center>
    </div> 
</div>

  
</body>

</html>
