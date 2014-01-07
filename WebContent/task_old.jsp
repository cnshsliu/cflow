<%String token = request.getParameter("token"); if(token==null)response.sendRedirect("/cflow/login.jsp?target=task.jsp");%><%@ include file="includeHeader.jsp"%>
<style>
	#inwft{border-top:1px solid red;}
	#startat{border-top:1px solid red;}
	#options{border-top:1px solid red;}
	#buttons{border-bottom:1px solid red;}
	#history_table {border-collapse:collapse; border:1px solid #CCCCCC;}
	#history_table thead{background-color: blue; color:white;}
	#history_table tbody td {border: 1px solid #CCCCCC;}
</style>
<%String tid = request.getParameter("tid"); %>
<div class='title'>Task</div>
<div id='result'></div>
<form id="form1">
<div id='name'></div>
<div id='acqmenu'></div>
<div id='inwft'>In: <span id='wftname'></span></div>
<div id='startat'>Start at: <span id='createdat'></span></div>
<div id='instruction'></div>
<div id='delegation'></div>
<div id='attachments'></div>
<div id='options'></div>
<div id='buttons'>
<input type='hidden' id='token' value='<%=token%>'>
<input type='hidden' id='prcId'>
<input type='hidden' id='nodeid'>
<input type='hidden' id='sessid'>
<input type='hidden' id='option'>
<input type='hidden' id='atts'>
<input type='Button' id='btn_submit' value='Done'>
</div>
</form>
<div class='title'>History:</div>
<div id='history'></div>
</body>

<script  language="javascript">
	window.addEvent('domready', function(){
		getTask();

		var result = $('result');
		var req_doTask = new Request({
			url: '/cflow/rest/task',
			onRequest:function(){result.set('text', 'Submitting..');},
			onSuccess:function(txt){ result.set('text', txt); 
				if(txt.indexOf('ERROR') < 0)
					window.location.href="/cflow/wl.jsp?token=<%=token%>"; 
			},
			onFailure:function(){result.set('text', 'The request failed.');},
			method: 'post',
			emulation: false
			});

		Array.implement({
		    getFirst: function() {
			if(this.length == 0) {
			    return null;
			}
			return this[0];
		    }
		});

		$('btn_submit').addEvent('click', function(event){
			event.stop();
			var firstChecked = $$('input[name=option_1]:checked').getFirst();
			if(firstChecked != null)
				$('option').value =  firstChecked.value;
			var tmp = "{";
			var atts_num = $('numberOfAttachments').value;
			var watt = $('attachments');
			watt.getChildren().each(function(el, i){
				tmp += "\"" + el.get("name") + "\":\"" + el.get("value") + "\",";
			});
			tmp += "}";
			$('atts').value = tmp;
			req_doTask.send({data:{ prcId: $('prcId').value, nodeid:$('nodeid').value, sessid:$('sessid').value, option:$('option').value, attachments:$('atts').value, token:"<%=token%>" }});
			});

	});

	function getTask(){
		  var req = new Request.JSON({
		  url: '/cflow/rest/task?tid=<%=tid%>&token=<%=token%>',
			method: 'get',
			onSuccess: function(json){ showTask(json); },
			onFailure: function(){ $('result').set('text', 'The request failed.'); }
		  });
		  req.send();
	}

	var showTask = function(task){
		var acqmenu_html = "";
		if(task.ACQS != "NOT_ACQUIRABLE"){
			if(task.ACQS == "ALREADY ACQUIRED"){
				var url = "/cflow/Acq" + "?prcid=" + task.WII.PRCID + "&nodeid=" + task.WORK.NODEID + "&sessid=" + task.WORK.SESSID + "&action=unacq";
				if (task.WII.delegaterid != null)
					url += "&dlg=" + task.WII.delegaterid;
				url += "&token=<%=token%>";
				acqmenu_html =  "<a href=\"" + url + "\">Unacquire</a>";
			} else {
				if (task.ACQS=="ACQ_CAN_ACQUIRE") {
					var url = "/cflow/Acq?prcid=" + task.WII.PRCID + "&nodeid=" + task.WORK.NODEID + "&sessid=" + task.WORK.SESSID + "&action=acq";
					if (task.WII.delegaterid != null)
						url += "&dlg=" + task.WII.delegaterid;
					url += "&token=<%=token%>";
					acqmenu_html =  "<a href=\"" + url + "\">Acquire</a>";
				} else {
					acqmenu_html = "<!-- todo: hide dotask form-->";
				}
			}
		}


		//form delegation
		var delegation_html = "";
		if (task.WORK.ALLOWDELEGATE == "true" && (task.ACQS == "ACQ_NOT_ACQUIRABLE" || task.ACQS=="ACQ_CAN_ACQUIRE")) {
			delegation_html = "<input type=\"Button\" name=\"delegate\" value=\"Delegate to\" onClick=\"javascript:startDelegate();\">";
			delegation_html += "<input type=\"hidden\" name=\"delegateflag\" id=\"delegateflag\" value=\"0\">";
			delegation_html += "<select name=\"delegatee\">";
				// task.MATES
			var mates = task.MATES;
			for (var u = 0; u < mates.length; u++) {
				delegation_html += "<option value=\"" + mates[i].ID  + "\">" + mates[i].NAME + "</option>";
			}
			delegation_html += "</select>";
		} else {
			delegation_html = "<input type=\"hidden\" name=\"delegateflag\" id=\"delegateflag\" value=\"0\">";
		}



		//form attachments
		
		var atts_html = "<table>";
		var atts = task.ATTACHMENTS;
		for(var i=0; i<atts.length; i++){
			atts_html += "<tr>";
				atts_html += "<td>" + atts[i].LABEL + "</td><td>";
			atts_html += "<input type='hidden' id='attname_" + i + "' value='" + atts[i].NAME + "'>";
			if(atts[i].TYPE == "url"){
				atts_html += "<a href='" + atts[i].VALUE + "' target='_blank'>" + atts[i].VALUE + "</a>";
				atts_html += "<input type='text' name='" + atts[i].NAME + "' value='" + atts[i].VALUE + "'>";
			}else{
				atts_html += "<input type='text' name='" + atts[i].NAME + "' value='" + atts[i].VALUE + "'>";
			}
			atts_html += "</td></tr>";
		}
		atts_html += "</table>";
		atts_html += "<input type='hidden' id='numberOfAttachments' value='" + atts.length + "'>";


		//form options
		var options_html = "";
		for(var i = 0; i<task.OPTIONS.length; i++){
			options_html += "<input type='radio' name='option_1' value='" + task.OPTIONS[i].VALUE + "'";
			if(task.OPTIONS[i].ISDEFAULT == "true")
				options_html += " checked";
			options_html += ">" + task.OPTIONS[i].CLEANTEXT;
		}

		//history
		var history_html = "<table id='history_table'><thead><tr><th>TASK</th><th>BY</th><th>TIME</th><th>DECISION</th></tr></thead><tbody>";
		for(var i=0; i<task.HISTORY.length; i++){
			history_html += "<tr>";
			history_html +=	"<td>" + task.HISTORY[i].NAME + "</td>";
			history_html +=	"<td>" + task.HISTORY[i].FINISHEDBY + "</td>";
			history_html +=	"<td>" + task.HISTORY[i].FINISHEDAT + "</td>";
			history_html +=	"<td>" + task.HISTORY[i].DECISION + "</td>";
			history_html +=	"</tr>";
			history_html +=	"<tr><td colspan=3>Context</td></tr>";
			history_html +=	"<tr><td colspan=3><table><tbody>";
			for(var j=0; j<task.HISTORY[i].ATTACHMENTS.length; j++){
				history_html +=	"<tr>";
				history_html +=	"<td>" + task.HISTORY[i].ATTACHMENTS[j].LABEL + "</td>";
				history_html +=	"<td>" + task.HISTORY[i].ATTACHMENTS[j].TYPE + "</td>";
				history_html +=	"<td>" + task.HISTORY[i].ATTACHMENTS[j].VALUE + "</td>";
				history_html +=	"</tr>";
			}
			history_html += "</tbody></table></td></tr>";
		}
		history_html += "</tbody></table>";

		$('history').set("html", history_html);

		$('name').set("text", task.WORK.NAME);
		$('acqmenu').set("html", acqmenu_html);
		$('wftname').set("html", task.WFTNAME);
		$('createdat').set("html", task.WORK.CREATEDAT);
		$('instruction').set("text", task.INSTRUCTION);
		$('delegation').set("html", delegation_html);
		$('attachments').set("html", atts_html);
		$('options').set("html", options_html);
		$('prcId').set("value", task.WII.PRCID);
		$('nodeid').set("value", task.WII.NODEID);
		$('sessid').set("value", task.WII.SESSID);
	};



</script>


