<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
<%String tid = request.getParameter("tid");%>
<div id='task_form'></div>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body>
<script  language="javascript">
	window.addEvent('domready', function(){
		getHTML ('<%=token%>', '<%=tid%>', 'ABC_LeaveApplication', 'task_form');

	});
	function getHTML(token, tid, vtname, divID){
		  var req = new Request({
			url: '/cflow/rest/form?token=' + token + '&tid=' + tid + '&vtname=' + vtname,
			method: 'get',
			noCache: true,
			onSuccess: function(html){ $(divID).set('html', html); },
			onError: function(text, error){ $('err').set('text', error); }
		  });
		  req.send();
	}
</script>
