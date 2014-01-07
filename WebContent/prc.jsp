<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
	<div class='title'>Workflow Processe Management</div>
	<form>Status:<select id='status'>
			<option value='running'>Running</option>
			<option value='suspended'>Suspended</option>
			<option value='finished'>Finished</option>
			<option value='canceled'>Canceled</option>
		</select>
		<input type=button id='refresh' value='Refresh'>
	</form>
	<div id='prcs' class='dotborder'></div>
	<div id='err'></div>
</div></div>
<%@ include file="includeFooter.jsp"%>
</body>

<script  language="javascript">
	window.addEvent('domready', function(){
		getPrcs("running");
	});

	var showPrcs = function(prcs){
		var prchtml = "<table>";
			prchtml += "<thead><tr><th>NAME</th><th>STARTBY</th><th>VIEW</th><th>EDIT</th><th>Workflow Template</th></thead></tr><tbody>";
		prcs.each(function(prc){
			prchtml += "<tr><td>" + prc.NAME + "</td>"
				+ "<td>" + prc.STARTBY + "</td>"
				+ "<td>" 
					+ "<a href='/cflow/prc_view.jsp?prcid=" + prc.PRCID + "&startby=" + prc.STARTBY + "&token=<%=token%>'>View</a>"
				+ "</td>"
				+ "<td>"
					+ "<a href='/cflow/prc_edit.jsp?prcid=" + prc.PRCID + "&startby=" + prc.STARTBY + "&token=<%=token%>'>Edit</a>"
				+ "</td>"
				+ "<td align=right>"
				+ "<a href='/cflow/wft_view.jsp?wftid=" + prc.WFTID +  "&token=<%=token%>'>View</a>"
				+ "</td>"
				+ "</tr>";

		});
		prchtml += "</tbody>";
		prchtml += "</table>";
		$('prcs').set("html", prchtml);
	};


	function getPrcs(stat){
		  var req = new Request.JSON({
		  url: '/cflow/rest/process/all?status=' + stat + '&token=<%=token%>',
			method: 'get',
			onSuccess: function(json){ showPrcs(json); },
			onFailure: function(){ 
				if(this.status == 401) 
					$('err').set('html', "Session expired: <a href='/cflow/login.jsp'>Relogin</a>");
				else if(this.status == 500)
					$('err').set('html', this.text);
				}
		  });
		  req.send();
	}

	$('refresh').addEvent('click', function (){
		getPrcs($('status').value);
	});
</script>
