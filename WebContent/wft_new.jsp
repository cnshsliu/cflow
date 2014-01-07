<%@ include file="includeHeader.jsp"%>
<div class="maindiv_100p"><div class="maindiv_800c tbpadding">
<%
String wftId = request.getParameter("wftid");
String mode="workflow";
%>
<%
	String devid = null;
Developer dev = CflowHelper.getDevByToken(token);
if(dev != null) devid = dev.id;
%>
<script src="javascript/designer/AC_OETags.js" language="javascript"></script>
<script language="javascript">
	var designerIsReady = false;
	var designerShown = false;
	var useWizard = true;
	var requiredMajorVersion = 9;
	var requiredMinorVersion = 0;
	var requiredRevision = 124;
	var designerWidth = 800;
	var designerHeight = 540;
	var designerContent = "";
	var count = 0;
	<!--
	// Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
	var hasProductInstall = DetectFlashVer(6, 0, 65);
	
	// Version check based upon the values defined in globals
	var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
	
	if ( hasProductInstall && !hasRequestedVersion ) {
		// DO NOT MODIFY THE FOLLOWING FOUR LINES
		// Location visited after installation is complete if installation is required
		var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
		var MMredirectURL = window.location;
		document.title = document.title.slice(0, 47) + " - Flash Player Installation";
		var MMdoctitle = document.title;
	
		designerContent=AC_FL_RunContent(
			"src", "playerProductInstall",
			"FlashVars", "token=<%=token%>&devid=<%=devid%>&wizard=true&mode=<%=mode%>&usemode=2&subAction=editWftn&MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&MMdoctitle='+MMdoctitle+"",
			"width", "800",
			"height", "540",
			"align", "middle",
			"id", "CFlowWftDesigner",
			"quality", "high",
			"bgcolor", "#869ca7",
			"name", "CFlowWftDesigner",
			"allowScriptAccess","sameDomain",
			"type", "application/x-shockwave-flash",
			"pluginspage", "http://www.adobe.com/go/getflashplayer"
		);
	} else if (hasRequestedVersion) {
		// if we've detected an acceptable version
		// embed the Flash Content SWF when all tests are passed
		designerContent=AC_FL_RunContent(
				"src", "CFlowWftDesigner",
				"FlashVars", "token=<%=token%>&devid=<%=devid%>&wizard=true&mode=<%=mode%>&usemode=2&subAction=editWft&usrname=Steve%20Liu",
				"width", "800",
				"height", "540",
				"align", "middle",
				"id", "CFlowWftDesigner",
				"quality", "high",
				"bgcolor", "#869ca7",
				"name", "CFlowWftDesigner",
				"allowScriptAccess","sameDomain",
				"type", "application/x-shockwave-flash",
				"pluginspage", "http://www.adobe.com/go/getflashplayer"
		);
	  } else {
		designerContent = 'Alternate HTML content should be placed here. This content requires the Adobe Flash Player. <a href=http://www.adobe.com/go/getflash/>Get Flash</a>';
	  }
	// -->
</script>
<script language="JavaScript" type="text/javascript">
	<!--
		function editWft(wftId){
			if(designerShown == false)
				showDesigner();
			if(designerIsReady == false){
				setTimeout(function(){editWft(wftId)}, 500);
			}else{
				CFlowWftDesigner.ExtEditWft(wftId);
			}
		}
		function showDesigner(){
			document.getElementById("DESIGNER").innerHTML = designerContent;
			designerShown = true;
		}
		function OnDesignerReady(msg){  designerIsReady = true;	}	

		function getWindowSize(){
			var w=window,d=document,e=d.documentElement,g=d.getElementsByTagName('body')[0],x=w.innerWidth||e.clientWidth||g.clientWidth,y=w.innerHeight||e.clientHeight||g.clientHeight;
			return { width : x-30 , height : y-30 };
		}

		function toggleSize(flag){
			var s = getWindowSize();
			if(flag == 1){
				document.getElementById("CFlowWftDesigner").style.width=s.width;
				document.getElementById("CFlowWftDesigner").style.height=s.height;
			}else{
				document.getElementById("CFlowWftDesigner").style.width=800;
				document.getElementById("CFlowWftDesigner").style.height=540;
			}
			return s;
		}
		// -->


</script>
<noscript>
			Javascript support is required.!
</noscript>
<DIV ID="DESIGNER"></DIV> 
<DIV ID="monitorData" style="width:800px;">&nbsp;</DIV>
<DIV ID="helpDiv" style="display:none;  opacity: 0.7; width: 180px;  height: 500px;  top: 20px;  left: 0px;  border: 1px dashed #990000;  background-color: #ffdddd;  text-align: center; ">Designer Help<BR><a href="http://php.pass8t.com/" target="_blank">Click here to see more</a></DIV>
	<Script language="JavaScript">	
		showDesigner();
	</Script>

</div></div>
<%@ include file="includeFooter.jsp"%>
</body></html>
