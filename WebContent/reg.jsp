<%@ include file="includeHeader2.jsp"%>
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
          <td valign="top" align="left"><a href="/cflow" border="0"><img src="images/logo3764.png" alt="My World Flow" border="0"></a></td>
    	<td align="right">
		    <div id='devConsole'>Developer Console</div>
    	</td>
        </tr>
    </table>
    <table style="width:100%; background-color: #9FC;"><tr><td align="center">
	    <table style="width:800px; background-color: #9FC;"><tr><td>
	  		<h1>Developer Access Creation</h1>
			<form id="form1" action="/cflow/DoRegister" method="post">
				<table>
					<tr><td>Access ID</td><td><input type="text" name="devid"></td></tr>
					<tr><td>Display Name</td><td><input type="text" name="devname"></td></tr>
					<tr><td>Access Key</td><td><input type="password" name="accesskey1"></td></tr>
					<tr><td>Repeat</td><td><input type="password" name="accesskey2"></td></tr>
					<tr><td>Email</td><td><input type="text" name="email"></td></tr>
					<tr><td>Timezone</td><td><%=com.lkh.cflow.HtmlHelper.pickTimezone(request, "GMT+08:00")%> </td></tr>
					<tr><td>Language</td><td><%=com.lkh.cflow.HtmlHelper.pickLang(request)%> </td></tr>
					<tr><td> </td><td><input type="Submit" value="Create Access" id='create'></td></tr>
			        </table>
			</form>
			<div id="result"><%String msg = (String)request.getAttribute("register.message"); msg=(msg==null?"":msg); %><%=msg %></div>
			<BR><BR>
			<a href="login.jsp">Sing In if you already have an account</a>
			<BR>
	  	</td></tr></table>
  </td></tr></table>
  <table id='support'>
  <tr><td>
<p><a href="https://twitter.com/intent/tweet?screen_name=MWFSupport" class="twitter-mention-button" data-lang="zh-cn" data-related="MWFSupport"><img src="/cflow/images/twitter.png" border="0"></a>
<p>   You may also post your technical support request to <a href="mailto:myworldflow@yahoogroups.com">myworldflow@yahoogroups.com</a></p>
<p>  For MyWorldFlow technical discussion,  <a href="http://groups.yahoo.com/group/myworldflow">please go to MWF Yahoo! Groups myworldflow</a></p>

</td></tr>
  </table>

<div id="footer"><BR><BR>
      Copyright &#169; 2012 The MyWorldFlow.
    </div> 
 </center>
</body>
</html>
