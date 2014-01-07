<%@ include file="includeHeader2.jsp"%>
<%String target = request.getParameter("target"); 
if(target == null) target="wft.jsp";%>
<html> <head>
    <title>Welcome to My World Flow</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<LINK href="/cflow/rsstyle.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" href="css/slideshow/style.css" />
	<script src="javascript/mootools.js" language="javascript"></script>
</head> <body>
 <center>
     <table style="width:800px;">
         <tr>
          <td valign="top" align="left"><a href="/cflow"><img src="images/logo3764.png" alt="My World Flow" border="0"></a></td>
    	<td align="right">
		    <div id='devConsole'>Developer Console</div>
    	</td>
        </tr>
    </table>

    <table style="width:100%; background-color: #9FC;"><tr><td align="center">
	    <table style="width:800px; background-color: #9FC;"><tr><td>
			  	<h1>Developer Authentication</h1>
				<form action="/cflow/DoLogin" method="post">
					<div class='container_16'><div class='grid_5'>
					<table><tr><Td>Developer ID</Td><td>
					  <input name='id' type="text"></td></tr>
					  <tr><td>Access Key</td><td>
						<input name='acsk' type="password"></td></tr>
					<tr><td colspan=2>	<input id='login' type="submit" value="Developer Access"></td></tr></table>
					<input type='hidden' name='target' value="<%=target%>">
					<%String msg = (String)request.getAttribute("register.message"); if(msg == null) msg=""; %>
					<%=msg %>
					<BR><BR>
					<a href='/cflow/reg.jsp'>Create new Developer Access ID and Access Key</a>
					</div></div>
				</form>
	  	</td></tr></table>
  </td></tr></table>
  <table id='support'>
  <tr><td>
<p><a href="https://twitter.com/intent/tweet?screen_name=MWFSupport" class="twitter-mention-button" data-lang="zh-cn" data-related="MWFSupport"><img src="/cflow/images/twitter.png"></a>
<p>  You may also post your technical support request to <a href="mailto:myworldflow@yahoogroups.com">myworldflow@yahoogroups.com</a></p>
<p>  For MyWorldFlow technical discussion,  <a href="http://groups.yahoo.com/group/myworldflow">please go to MWF Yahoo! Groups myworldflow</a></p>

</td></tr>
  </table>

<div id="footer"><BR><BR>
      Copyright &#169; 2012 The MyWorldFlow.
    </div> 
 </center>
</body>

</script>
</html>
