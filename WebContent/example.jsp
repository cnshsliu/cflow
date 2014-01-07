<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
	<div class='title'>Workflow Examples</div>
	Click to copy these example to your own template library, then go to template library to use them.
	<DIV class='dotborder'>
	<ul>
	<li><a href="javascript:copyExample('PutGiraffe');">How to put a giraffe into refrigerator</a></li>
	<li><a href="javascript:copyExample('Demo_AND');">A AND node requires all of it's preceding tasks must have been done.</a></li>
	<li><a href="javascript:copyExample('Demo_OR');">An OR node requires any one of it's preceding tasks should have been done.</a></li>
	<li><a href="javascript:copyExample('Demo_DoneByAnyone');">A task is done when any one of it's undertakers has done it.</a></li>
	<li><a href="javascript:copyExample('Demo_DoneByAll');">A task is done only when all of it's undertakers have done it</a></li>
	<li><a href="javascript:copyExample('Demo_DoneByCondition');">A task is done when certain number of it's 'undertakers have done it.</a></li>
	<li><a href="javascript:copyExample('Demo_JAVA');">Integrate with a local Java class which can read or write process contextual value and guide workflow routing.</a></li>
	<li><a href="javascript:copyExample('Demo_WEB');">Integrate with a remote Web service by it's URL to read or write process contextual value and guide workflow routing.</a></li>
	<li><a href="javascript:copyExample('Demo_JAVASCRIPT');">Use Javascript read or write process contextual value and guide workflow routing.</a></li>
	<li><a href="javascript:copyExample('Demo_RoleReference');">How to determine task undertakers by referencing.</a></li>
	<li><a href="javascript:copyExample('LeaveApplication');">A simple Leaving Approval Process</a></li>
	<li><a href="javascript:copyExample('Demo_SJYJ');">A promotion workflow use Javascript codes to emulate real business service</a></li>
	</ul>
	</DIV>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body>

<script language="Javascript">
	function copyExample(modelid){
		  var req = new Request.JSON({
		  	url: '/cflow/rest/wft/example?modelid=' + modelid + '&token=<%=token%>',
			method: 'get',
			onComplete: function(json){  window.location = "/cflow/wft.jsp?token=<%=token%>"; },
			onFailure: function(){   },
			emulation: false,
		  });
		 req.send();
	}
</script>
