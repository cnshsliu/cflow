
package com.lkh
{
	import flash.utils.getQualifiedClassName;
	
	import mx.collections.ArrayCollection;
	import mx.core.Application;
	import mx.rpc.events.ResultEvent;
	import mx.utils.UIDUtil;
	
	public class Datahandler
	{
		public function Datahandler()
		{
		}
		
		 private function getXMLDate(nn:Date):String{
        	var ss:String = nn.toString();
				var rm:int = nn.month + 1;
				var sm:String = "" + rm;
				var sd:String = "" + nn.date;
				var sh:String = "" + nn.hours;
				var smin:String = "" + nn.minutes;
				var ssec:String = "" + nn.seconds;
				
				if(sm.length <2) sm = "0" + sm;
				if(sd.length <2) sd = "0" + sd;
				if(sh.length <2) sh = "0" + sh;
				if(smin.length<2) smin = "0" + smin;
				if(ssec.length<2) ssec = "0" + ssec;
				
				ss = nn.fullYear + "-" + sm + "-" + sd + "T" 
					+ sh + ":" + smin + ":" + ssec; 
				return ss;
        }
        private function dashedDate(slashedDate:String):String{
        	var arr:Array = slashedDate.split("/");
        	return arr[2] + "-" + arr[0] + "-" + arr[1];
        }
        private function slashedDate(dashedDate:String):String{
        	var arr:Array = dashedDate.split("-");
        	return arr[1]+"/"+arr[2] + "/" + arr[0];
        }
        
        /**
        * 将工作流模板设计器中所设计的工作流模板转换为XML表达
        **/
		public function getWftXML(canvas:MyCanvas):XML{
			var rAction:RobotAction, xSendMail:XML, xMailto:XML, xMessage:XML;
			var xSendSMS:XML, xSMSto:XML;
			var sdate:String = getXMLDate(new Date());
			var ret:XML = <workflow/>
			var ns1:Namespace = new Namespace("cf", "http://lkh.com/cflow");
			var xsi:Namespace = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			ret.setNamespace(ns1);
			ret.addNamespace(xsi);
			ret.@xsi::schemaLocation = "http://lkh.com/cflow ../schemas/wft.xsd";
			trace(ret.toXMLString());
			
			var designer:CFlowWftDesigner = (Application.application) as CFlowWftDesigner;
			ret.@name= designer.wftName.text;
			ret.@owner = designer.devid;
			ret.@acl = "private"
			ret.@lastModified = sdate;
			ret.@created=sdate;
			
			trace(ret.toXMLString());
			
			var i:int =0, j:int =0;
			var node:Node = null;
			
			var uniqueCollection:ArrayCollection = new ArrayCollection();
			
			//Collect roles.
			for(i=0; i<canvas.dataModel.nodes.length; i++){
				node = canvas.dataModel.nodes[i] as Node;
				if(node.tasktoType == Node.TASKTO_TYPE_ROLE && node.taskto != Node.TASKTO_STARTER){
					if(!uniqueCollection.contains(node.taskto)){
						uniqueCollection.addItem(node.taskto);
					}
				}
			}
			for(i=0; i<uniqueCollection.length; i++){
				var xRole:XML = <role/>
				xRole.@name = uniqueCollection.getItemAt(i);
				ret.appendChild(xRole);
			}
			
			//append node.
			for(j=0; j<canvas.dataModel.nodes.length; j++){
				node = canvas.dataModel.nodes[j] as Node;
				var xNode:XML = <node/>
				xNode.@id = node.nodeid;
				xNode.@type = node.type;
				xNode.@title = node.title;
				xNode.@name = node.dlabel.text;
				xNode.@x = Math.round(node.x);
				xNode.@y = Math.round(node.y);
				if(node.type == Node.NODE_TASK){
					xNode.@acquirable = node.acquirable;
					xNode.@acqThreshold = node.acqThreshold;
					xNode.@allowRoleChange = node.allowRoleChange;
					xNode.@allowDelegate = node.allowDelegate;
					xNode.@allowAdhoc = node.allowAdhoc;
					xNode.@roleToChange = node.roleToChange;
					xNode.@form = node.form;
					var xTaskto:XML = <taskto/>
					xTaskto.@type = node.tasktoType;
					xTaskto.@whom = node.taskto;
					for(var m:int=0; m<node.exceptme.length; m++){
							var xExceptme:XML = <exceptme/>
							xExceptme.@nodeid = node.exceptme.getItemAt(m);
							xTaskto.appendChild(xExceptme);
					}
					xNode.appendChild(xTaskto);
					
					
					if(node.isTimeingSet()){
						xNode.@yy = node.timerData.yy;
						xNode.@mm = node.timerData.mm;
						xNode.@dd = node.timerData.dd;
						xNode.@hh = node.timerData.hh;
						xNode.@mi = node.timerData.mi;
						xNode.@timeingType = node.timeingType;
					}
					
					xNode.@mpsm = node.mpsm;
					var xMpcdc:XML = <mpcdc>{node.mpcdc}</mpcdc>
					xNode.appendChild(xMpcdc);
					var xOec:XML = <oec>{node.oec}</oec>
					xNode.appendChild(xOec);
				}
				if(node.type == Node.NODE_SUB){
					xNode.@subWftUID = node.subWftUID;
					xNode.@subWftWG = node.subWftWG;
				}
				if(node.type == Node.NODE_TIMER){
					xNode.@yy = node.timerData.yy;
					xNode.@mm = node.timerData.mm;
					xNode.@dd = node.timerData.dd;
					xNode.@hh = node.timerData.hh;
					xNode.@mi = node.timerData.mi;
				}
				if(node.type == Node.NODE_ROUND){
					xNode.@repeatthresholdtype = node.repeatThresholdType;
					if(node.repeatThresholdType != "NOREPEAT"){
						xNode.@repeatthreshold = node.repeatThreshold;
						if(node.repeatThresholdType == "DATE"){
							xNode.@repeatthreshold = dashedDate(node.repeatThreshold);
						}
						xNode.@repeatcron = node.repeatCron;
					}
				}
				if(node.type == Node.NODE_NOTIFY){
					rAction = node.notifyAction;
					if(rAction.name == Node.ACTION_SENDMAIL){
						xSendMail = <sendmail/>
						xSendMail.@subject = rAction.subject;
						xMailto = <mailto/>
						xMailto.@type = rAction.mailtoType;
						xMailto.@whom = rAction.mailto;
						xSendMail.appendChild(xMailto);
						xMessage = <message>{rAction.message}</message>
						xSendMail.appendChild(xMessage);
						xNode.appendChild(xSendMail);
					}else if(rAction.name == Node.ACTION_SENDSMS){
						xSendSMS = <sendsms/>
						xSendSMS.@subject = rAction.subject;
						xSMSto = <smsto/>
						xSMSto.@type = rAction.mailtoType;
						xSMSto.@whom = rAction.mailto;
						xSendSMS.appendChild(xSMSto);
						xMessage = <message>{rAction.message}</message>
						xSendSMS.appendChild(xMessage);
						xNode.appendChild(xSendSMS);
					}
				}
				
				if(node.type == Node.NODE_SCRIPT){
					var xScript:XML = <script>{node.script}</script>
					xNode.appendChild(xScript);
				}
				

				
				/*if(node.preActions.length>0){
					var xPreActions:XML = <preactions/>
					for(i=0; i<node.preActions.length; i++){
						rAction = node.preActions.getItemAt(i) as RobotAction;
						if(rAction.name == Node.ACTION_SENDMAIL){
							xSendMail = <sendmail/>
							xSendMail.@subject = rAction.subject;
							xMailto = <mailto/>
							xMailto.@type = rAction.mailtoType;
							xMailto.@whom = rAction.mailto;
							xSendMail.appendChild(xMailto);
							xMessage = <message>{rAction.message}</message>
							xSendMail.appendChild(xMessage);
							xPreActions.appendChild(xSendMail);
						}else if(rAction.name == Node.ACTION_SENDSMS){
							xSendSMS = <sendsms/>
							xSendSMS.@subject = rAction.subject;
							xSMSto = <smsto/>
							xSMSto.@type = rAction.mailtoType;
							xSMSto.@whom = rAction.mailto;
							xSendSMS.appendChild(xSMSto);
							xMessage = <message>{rAction.message}</message>
							xSendSMS.appendChild(xMessage);
							xPreActions.appendChild(xSendSMS);
						}
					}
					xNode.appendChild(xPreActions);
				}*/
				
				
				//add Attachments
				if(node.attachments.length > 0){
					for(i=0; i<node.attachments.length; i++){
						var xAttachment:XML = <attachment/>
						var att:Attachment = node.attachments[i] as Attachment;
						xAttachment.@type = att.type;
						xAttachment.@label = att.label;
						xAttachment.@attname = att.attname;
						if(att.value == null)
							att.value = "";
						xAttachment.@value = att.value;
						xNode.appendChild(xAttachment);
					}				
				}
				//add next
				for(i=0; i<canvas.dataModel.links.length; i++){
					var aLink:Link = canvas.dataModel.links[i] as Link;
					if(aLink.lfrom == node){
						var xNext:XML = <next/>
						if(aLink.option == null){
							aLink.option = Link.OPTION_DEFAULT;
						}
						if(aLink.option != Link.OPTION_DEFAULT){
							xNext.@option = aLink.option;
						}
						xNext.@targetID = aLink.lto.nodeid;
						var tmp:String = "";
						for(var jidx:int = 0; jidx<aLink.joints.length; jidx++){
							if(jidx>0)
								tmp += ";";
							var joint:Joint = (Joint)(aLink.joints.getItemAt(jidx));
							tmp += joint.cx + ":" + joint.cy;
						}
						xNext.@joints = tmp;
						xNode.appendChild(xNext);
					}
				}
				
				//End node's next node is itself. 
				//It's next node must be assigned to pass XSD checking.
				if(node.type == Node.NODE_END || node.type == Node.NODE_GROUND){
					var eNext:XML = <next/>
					eNext.@targetID = node.nodeid;
					xNode.appendChild(eNext);
				}

				/*if(node.postActions.length>0){
					var xPostActions:XML = <postactions/>
					for(i=0; i<node.postActions.length; i++){
						rAction = node.postActions.getItemAt(i) as RobotAction;
						if(rAction.name == Node.ACTION_SENDMAIL){
							xSendMail = <sendmail/>
							xSendMail.@subject = rAction.subject;
							xMailto = <mailto/>
							xMailto.@type = rAction.mailtoType;
							xMailto.@whom = rAction.mailto;
							xSendMail.appendChild(xMailto);
							xMessage = <message>{rAction.message}</message>
							xSendMail.appendChild(xMessage);
							xPostActions.appendChild(xSendMail);
						}else if(rAction.name == Node.ACTION_SENDSMS){
							xSendSMS = <sendsms/>
							xSendSMS.@subject = rAction.subject;
							xMailto = <smsto/>
							xMailto.@type = rAction.mailtoType;
							xMailto.@whom = rAction.mailto;
							xSendSMS.appendChild(xMailto);
							xMessage = <message>{rAction.message}</message>
							xSendSMS.appendChild(xMessage);
							xPostActions.appendChild(xSendSMS);
						}
					}
					xNode.appendChild(xPostActions);
				}*/
				
				ret.appendChild(xNode);
			}
			
			return ret;
		}
		
		public function populateGui(canvas:MyCanvas, xnodes:ArrayCollection, xroles:ArrayCollection):void{
			var i:int = 0, j:int =0;
			var xNode:Object;
			var node:Node;
			var rAction:RobotAction;
			var att:Attachment;
			//Application.application.myRoles = xroles;
			//Application.application.myRoles.addItemAt(  {  name:"starter"  }, 0  );
			for(i = 0; i<xnodes.length; i++){
				xNode = xnodes[i];
				node = new Node();
				node.nodeid = xNode.id;
				node.type = xNode.type;
				node.title = xNode.title;
				node.dlabel.text = xNode.name;

				node.x = xNode.x;
				node.y = xNode.y;
				if(node.x > canvas.maxx) canvas.maxx = node.x;
				if(node.y > canvas.maxy) canvas.maxy = node.y;
				if(xNode.type == Node.NODE_TASK){
					node.tasktoType = xNode.taskto.type;
					node.taskto = xNode.taskto.whom;
					node.acquirable = xNode.acquirable;
					node.acqThreshold = xNode.acqThreshold;
					node.allowRoleChange = xNode.allowRoleChange;
					node.allowDelegate = xNode.allowDelegate;
					node.allowAdhoc = xNode.allowAdhoc;
					node.roleToChange = xNode.roleToChange;
					if(node.tasktoType == Node.TASKTO_TYPE_PERSON){
						if(com.lkh.StringUtil.trim(node.taskto) == "")
						{
							node.taskto = "starter";
							node.tasktoType = Node.TASKTO_TYPE_ROLE;
						}else{
							node.plabel.text = "U:" + node.taskto;
						}
					}else if (node.tasktoType == Node.TASKTO_TYPE_ROLE){
						node.taskto = com.lkh.StringUtil.trim(node.taskto);
						if(node.taskto == ""){
							node.taskto = "starter";
						}
						if(node.taskto != "starter"){
							node.plabel.text = "R:" + node.taskto;
						}
					}
					node.form = xNode.form;
						if(xNode.taskto.exceptme != null){
							if(flash.utils.getQualifiedClassName(xNode.taskto.exceptme) == "mx.collections::ArrayCollection"){
								for(j=0; j<xNode.taskto.exceptme.length; j++){
									node.exceptme.addItem(xNode.taskto.exceptme[j].nodeid);
								}
							}else {
								node.exceptme.addItem(xNode.taskto.exceptme.nodeid);
								
							}
						}
				}
				if(xNode.type == Node.NODE_SUB){
					node.subWftUID = xNode.subWftUID;
				}
				if(xNode.type == Node.NODE_TIMER){
					node.timerData.yy = xNode.yy;
					node.timerData.mm = xNode.mm;
					node.timerData.dd = xNode.dd;
					node.timerData.hh = xNode.hh;
					node.timerData.mi = xNode.mi;
				}
				if(xNode.type == Node.NODE_ROUND){
					node.repeatThresholdType = xNode.repeatthresholdtype;
					if(node.repeatThresholdType != "NOREPEAT"){
						node.repeatThreshold = xNode.repeatthreshold;
						
						if(node.repeatThresholdType == "DATE")
							node.repeatThreshold = slashedDate(xNode.repeatthreshold);
						
						node.repeatCron = xNode.repeatcron;
					}
				}
				if(xNode.type == Node.NODE_NOTIFY){
					node.notifyAction = new RobotAction();
					if(xNode.sendmail != null){
						node.notifyAction.name = Node.ACTION_SENDMAIL;
						node.notifyAction.subject = xNode.sendmail.subject;
						node.notifyAction.mailtoType = xNode.sendmail.mailto.type;
						node.notifyAction.mailto = xNode.sendmail.mailto.whom;
						node.notifyAction.message = xNode.sendmail.message;
					}else if(xNode.sendsms != null){
						node.notifyAction.name = Node.ACTION_SENDSMS;
						node.notifyAction.subject = xNode.sendsms.subject;
						node.notifyAction.mailtoType = xNode.sendsms.smsto.type;
						node.notifyAction.mailto = xNode.sendsms.smsto.whom;
						node.notifyAction.message = xNode.sendsms.message;
					}
				}
				if(xNode.type == Node.NODE_SCRIPT){
					node.script = xNode.script;
				}
				
				node.mpsm = xNode.mpsm;
				node.oec = xNode.oec;
				node.mpcdc = xNode.mpcdc;
				
				/*
				if(xNode.preactions != null){
					if(flash.utils.getQualifiedClassName(xNode.preactions) == "mx.collections::ArrayCollection"){
						for(j=0; j<xNode.preactions.length; j++){
							rAction = new RobotAction();
							if(xNode.preactions[j].sendmail != null){
								rAction.name = Node.ACTION_SENDMAIL;
								rAction.subject = xNode.preactions[j].sendmail.subject;
								rAction.mailtoType = xNode.preactions[j].sendmail.mailto.type;
								rAction.mailto = xNode.preactions[j].sendmail.mailto.whom;
								rAction.message = xNode.preactions[j].sendmail.message;
							}else if(xNode.preactions[j].sendsms != null){
								rAction.name = Node.ACTION_SENDSMS;
								rAction.subject = xNode.preactions[j].sendsms.subject;
								rAction.mailtoType = xNode.preactions[j].sendsms.smsto.type;
								rAction.mailto = xNode.preactions[j].sendsms.smsto.whom;
								rAction.message = xNode.preactions[j].sendsms.message;
							}
							node.preActions.addItem(rAction);
						}
					}else{
							rAction = new RobotAction();
							if(xNode.preactions.sendmail != null){
								rAction.name = Node.ACTION_SENDMAIL;
								rAction.subject = xNode.preactions.sendmail.subject;
								rAction.mailtoType = xNode.preactions.sendmail.mailto.type;
								rAction.mailto = xNode.preactions.sendmail.mailto.whom;
								rAction.message = xNode.preactions.sendmail.message;
							}else if(xNode.preactions.sendsms != null){
								rAction.name = Node.ACTION_SENDSMS;
								rAction.subject = xNode.preactions.sendsms.subject;
								rAction.mailtoType = xNode.preactions.sendsms.smsto.type;
								rAction.mailto = xNode.preactions.sendsms.smsto.whom;
								rAction.message = xNode.preactions.sendsms.message;
							}
							node.preActions.addItem(rAction);
					}

				}
				
				if(xNode.postactions != null){
					if(flash.utils.getQualifiedClassName(xNode.preactions) == "mx.collections::ArrayCollection"){
						for(j=0; j<xNode.postactions.length; j++){
							rAction = new RobotAction();
							if(xNode.postactions[j].sendmail != null){
								rAction.name = Node.ACTION_SENDMAIL;
								rAction.subject = xNode.postactions[j].sendmail.subject;
								rAction.mailtoType = xNode.postactions[j].sendmail.mailto.type;
								rAction.mailto = xNode.postactions[j].sendmail.mailto.whom;
								rAction.message = xNode.postactions[j].sendmail.message;
							}else if(xNode.postactions[j].sendsms != null){
								rAction.name = Node.ACTION_SENDSMS;
								rAction.subject = xNode.postactions[j].sendsms.subject;
								rAction.mailtoType = xNode.postactions[j].sendsms.smsto.type;
								rAction.mailto = xNode.postactions[j].sendsms.smsto.whom;
								rAction.message = xNode.postactions[j].sendsms.message;
							}
							node.postActions.addItem(rAction);
						}

					}else{
							rAction = new RobotAction();
							if(xNode.postactions.sendmail != null){
								rAction.name = Node.ACTION_SENDMAIL;
								rAction.subject = xNode.postactions.sendmail.subject;
								rAction.mailtoType = xNode.postactions.sendmail.mailto.type;
								rAction.mailto = xNode.postactions.sendmail.mailto.whom;
								rAction.message = xNode.postactions.sendmail.message;
							}else if(xNode.postactions.sendsms != null){
								rAction.name = Node.ACTION_SENDSMS;
								rAction.subject = xNode.postactions.sendsms.subject;
								rAction.mailtoType = xNode.postactions.sendsms.smsto.type;
								rAction.mailto = xNode.postactions.sendsms.smsto.whom;
								rAction.message = xNode.postactions.sendsms.message;
							}
							node.postActions.addItem(rAction);
					}
				}*/
				if(xNode.attachment != null){
					var atts:ArrayCollection = null;
					
					if(flash.utils.getQualifiedClassName(xNode.attachment) == "mx.collections::ArrayCollection"){
						atts = xNode.attachment;
					}else{
						atts = new ArrayCollection();
						atts.addItem(xNode.attachment);
					}
					for(j=0; j<atts.length; j++){
						att = new Attachment(atts[j].type,
							atts[j].attname,
							atts[j].label,
							atts[j].value);
						node.attachments.addItem(att);
					}
				}
				
				canvas.placeNode(node);
				node.resetSize();
				trace(node.nodeid);
			}
			
			
			//Add links
			for(i=0; i<xnodes.length; i++){
				xNode = xnodes[i];
				var fromNode:Node = null;
				for(var m:int=0; m<canvas.dataModel.nodes.length; m++){
					trace("Node.nodeid=" + ((canvas.dataModel.nodes[m]) as Node).nodeid);
					trace("xNode.id=" + xNode.id);
					if(((canvas.dataModel.nodes[m]) as Node).nodeid == xNode.id){
						fromNode = canvas.dataModel.nodes[m] as Node;
						break;
					}
				}
				if(fromNode == null)
					continue;
				if(fromNode.type == Node.NODE_END)
					continue;
					
				var toNode:Node = null;
				var toOption:String = null;
				var jointString:String = "";
				var nextArr:ArrayCollection = null;
				if(flash.utils.getQualifiedClassName(xNode.next) == "mx.collections::ArrayCollection"){
					nextArr = xNode.next;
				}else{
					nextArr = new ArrayCollection();
					nextArr.addItem(xNode.next);
				}
				
				for(j=0; j<nextArr.length; j++){
					toNode = getNodebyUID(canvas, nextArr[j].targetID);
					toOption = nextArr[j].option;
					if(toOption == null){
						toOption = Link.OPTION_DEFAULT;
					}
					try{
						jointString =nextArr[j].joints;
						if(jointString == null){
							jointString = "";
						}
					}catch(error:TypeError){
						//the tag probably doesn't exist
					}catch(error:Error){
						trace("Error " + error.errorID + ": " + error.name + " - " + error.message);
					}finally{
						
					}
					if(toNode != null){
						var aLink:Link =  new Link(fromNode, toNode, jointString, toOption);
						canvas.dataModel.links.addItem(aLink);
						aLink.placeJoints(canvas);
					}
				}
				
			}
			
			//canvas.updateCanvas();
		}
		
		public function getNodebyUID(canvas:MyCanvas, nid:String):Node{
			var node:Node = null;
			for(var n:int=0; n<canvas.dataModel.nodes.length; n++){
				if((canvas.dataModel.nodes[n] as Node).nodeid == nid){
					node = canvas.dataModel.nodes[n] as Node;
					break;
				}
			}
			return node;
		}

		public function inbedProcessData(canvas:MyCanvas, event:ResultEvent):void{
			var i:int = 0, j:int=0;;
			var inbeded:int = 0;
			var  xWorks:ArrayCollection = null;
			if(flash.utils.getQualifiedClassName(event.result.process.work) == "mx.collections::ArrayCollection"){
				xWorks = event.result.process.work;
			}else{
				xWorks = new ArrayCollection();
				xWorks.addItem(event.result.process.work);
			}
			for(i=0; i<canvas.dataModel.nodes.length; i++){
				if(canvas.dataModel.nodes[i].type == Node.NODE_START){
					canvas.dataModel.nodes[i].task_status = "finished";
					canvas.dataModel.nodes[i].task_decision = "DEFAULT";
					
				}else{
					inbeded = 0;
					for(j=0; j<xWorks.length; j++){
						if(xWorks[j].nodeid == canvas.dataModel.nodes[i].nodeid){
							inbeded = 1;
							canvas.dataModel.nodes[i].task_status = xWorks[j].status;
							canvas.dataModel.nodes[i].task_decision = xWorks[j].decision;
						}
					}
					/*
					if(inbeded == 0){
						canvas.dataModel.nodes[i].task_status = "NOT_YET";
					}
					*/
				}
			}
			
		}

		public function newIid(usrid:String):String
		{
			return "newiid";
		}
	}
}
