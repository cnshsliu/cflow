package com.lkh
{
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.geom.Point;
	import flash.text.TextLineMetrics;
	
	import mx.collections.ArrayCollection;
	import mx.containers.Canvas;
	import mx.controls.Alert;
	import mx.controls.Button;
	import mx.controls.Image;
	import mx.controls.Label;
	import mx.core.Application;
	import mx.styles.CSSStyleDeclaration;



	public class MyCanvas extends Canvas
	{
		
		public var selTool:int= -1;
		[Bindable]
        public var selNode:Node=null;
        public var linkFrom:Node=null;
        public var linkTo:Node=null;
        public var dragging:Boolean = false;
		public var jointDragging:Boolean = false;
        public var moving:Boolean = false;
	private var draggingLink:Boolean = false;
        public var selBtn:Button=null;
		public var selJoint:Joint = null;
        public var fromBtn:Button = null;
        public var toBtn:Button = null;
        public var linking:Boolean = false;
        public var ofx:int = -1;
        public var ofy:int = -1;
        public var otx:int = -1;
        public var oty:int = -1;
        [Bindable]
	public var dataModel:DataModel = new DataModel();
        public var justInitialized:Boolean = true;
        public var startNode:Node = null;
        public var endNode:Node = null;
	private var drawLineFromNode:Node = null;
	private var drawLineToPoint:Point = null;
        [Bindable]
        public var selLink:Link = null;
        public var hoverLink:Link = null;
        public var tobeDeletedLink:Link = null;
        private var problemNodes:ArrayCollection = new ArrayCollection();
        private var problemLinks:ArrayCollection = new ArrayCollection();
        private var tempJoints:ArrayCollection = new ArrayCollection();

        public var lastTempJoint:Joint = null;
	public var mouseOverNode:Node = null;
	public var mouseOverJoint:Joint = null;

	public var linkToMove:Link = null;
	private var linkMoving:Boolean = false;
	private var statusLabel:Label = new Label();
	private var errorLabel:Label = new Label();
	public var touched:Boolean = false;
	public var maxx:int = 0;
	public var maxy:int = 0;

        
		public function MyCanvas()
		{
			super();
			addEventListener(KeyboardEvent.KEY_DOWN, reportKeyDown); 
			
		}
		public function setDataModel(dm:DataModel):void{
			dataModel = dm;
		}
		public function loadWftFromXML(xml:XML):void{
			
		}
		public function reset():void{
			touched = false;
			this.removeAllChildren();
			fromBtn = null;
			toBtn = null;
			linkFrom = null;
			linkTo = null;
			selNode =null;
			linkFrom = null;
			linkTo = null;
			dragging = false;
			selBtn = null;
			linking = false;
			startNode = null;
			endNode = null;
			selLink = null;
			hoverLink = null;
			tobeDeletedLink = null;
			problemNodes.removeAll();
			problemLinks.removeAll();
			drawLineFromNode = null;
			drawLineToPoint = null;

			statusLabel.x = 0;
			statusLabel.y = 0;
			statusLabel.width = 700;
			statusLabel.visible = false;
			errorLabel.x = 0;
			errorLabel.y = 16;
			errorLabel.width = 700;
			errorLabel.visible = false;
			var newStyle:CSSStyleDeclaration = new CSSStyleDeclaration(".myStyle");
			newStyle.setStyle("color", "#FF0000");
			errorLabel.styleName = "myStyle";
			this.removeAllChildren();
			addChild(statusLabel);
			addChild(errorLabel);
			updateCanvas();
		}

		public function placeTempJoint(linkFrom:Node,  pt:Point){
			var aJoint:Joint = new Joint();
			aJoint.linkFromNode = linkFrom;
			aJoint.lastJoint = lastTempJoint;
			aJoint.cx = pt.x;
			aJoint.cy = pt.y;
			aJoint.resetPosition();
			placeJoint(aJoint);
			tempJoints.addItem(aJoint);
			lastTempJoint = aJoint;
		}

		private function overLine(p1:Point, p2:Point, p3:Point, Threshold:Number): Boolean{
			var ret:Boolean = false;
			var xa:int = p1.x, xb:int = p2.x;
			var ya:int = p1.y, yb:int = p2.y;
			var xc:int = p3.x, yc:int=p3.y;
			var d = 12;
			
			Threshold = 0.05;

			var ac = Math.sqrt(((xa - xc) * (xa - xc) + (ya - yc) * (ya - yc))*1.0);
			var bc = Math.sqrt(((xb - xc) * (xb - xc) + (yb - yc) * (yb - yc))*1.0);
			var ab = Math.sqrt(((xa - xb) * (xa - xb) + (ya - yb) * (ya - yb))*1.0);
			var delta = Math.abs(ab-(ac+bc));
			if(delta < Threshold){
				ret = (ac>d && bc>d);
			}else{
				ret = false;
			}
			
			return ret;	
			
		}

		
		private function overLink(aLink:Link, p3:Point, Threshold:Number): Boolean{
			var lfp:Point, ltp:Point;
			lfp = new Point(aLink.lfrom.x + aLink.lfrom.width/2, aLink.lfrom.y + 12);
			for(var i:int=0; i<aLink.joints.length; i++){
				ltp = new Point(aLink.joints[i].cx, aLink.joints[i].cy);
				if(overLine(lfp, ltp, p3, 1)){
					return true;
				}
				lfp = new Point(aLink.joints[i].cx, aLink.joints[i].cy);
			}
			ltp = new Point(aLink.lto.x + aLink.lto.width/2, aLink.lto.y + 12);
			return overLine(lfp, ltp, p3, 1);
		}

//		Private Function Pt2LineDis(x0 As Double, y0 As Double, x1 As Double, y1 As Double, x2 As Double, y2 As Double) As Double
//     	Pt2LineDis = (Abs((y1 - y2) * x0 + (x2 - x1) * y0 + x1 * y2 - x2 * y1)) / Sqr((y1 - y2) * (y1 - y2) + (x2 - x1) * (x2 - x1))
//		End Function
		
		public function trytoSelectLink(pt:Point, hoverOnly:Boolean = false):Link{
			var aLink:Link = null, retLink:Link = null;
			for(var i:int =0; i<dataModel.links.length; i++){
				aLink = dataModel.links.getItemAt(i) as Link;
				if(overLink(aLink, pt, 1)){
					retLink = aLink;
					if(hoverOnly){
						if(hoverLink != null){
							hoverLink.hovered = false;
						}
						hoverLink = aLink;
						hoverLink.hovered = true;
					}else{
						if(selLink != null){
							selLink.selected = false;
						}
						selLink = aLink;
						selLink.selected = true;
					}
					break;
				}
				
			}
			if(retLink == null){
				if(hoverOnly){
					if(hoverLink != null){
						hoverLink.hovered = false;
						hoverLink = null;
					}
				}else{
					if(selLink != null){
						selLink.selected = false;
						selLink = null;
					}
				}
			}
			updateCanvas();
			return retLink;
		}
		public function trytoSelectLinkTobeDeleted(pt:Point):Link{
			var aLink:Link = null, retLink:Link = null;
			for(var i:int =0; i<dataModel.links.length; i++){
				aLink = dataModel.links.getItemAt(i) as Link;
				if(overLink(aLink, pt, 1)){
					retLink = aLink;
					tobeDeletedLink = aLink;
					break;
				}
				
			}
			if(retLink == null){
					tobeDeletedLink = null;
			}
			return retLink;
		}
		
		public function deleteLink(link:Link):void{
			var i:int = -1;
			//Rmeove link's label
			if(selLink != null)
				selLink = null;
				
			link.label.text = "";
			if(this.contains(link.label))
				this.removeChild(link.label);
			for(var j:int = 0; j<link.joints.length; j++){
				removeChild((Joint)(link.joints.getItemAt(j)));
			}
			i = dataModel.links.getItemIndex(link);
			if(i>=0){
				dataModel.links.removeItemAt(i);
			}
			_validateWft();
			updateDisplayList(unscaledWidth, unscaledHeight);
			touched = true;
		}
		
		public function deleteNode(node:Node):void{
			var i:int = -1;
			for(i=0; i<dataModel.links.length;){
				var link:Link = dataModel.links.getItemAt(i) as Link;
				if(link.lfrom == node || link.lto == node){
					if(this.contains(link.label))
						this.removeChild(link.label);
					for(var j:int = 0; j<link.joints.length; j++){
						removeChild((Joint)(link.joints.getItemAt(j)));
					}
					dataModel.links.removeItemAt(i);
					continue;
				}
				i++;
			}
			i = dataModel.nodes.getItemIndex(node);
			if(i>=0){
				dataModel.nodes.removeItemAt(i);
			}

			removeChild(node);
			updateDisplayList(unscaledWidth, unscaledHeight);
			touched = true;

		}
		
		private function setNodeStyle(aNode:Node):void{
			switch(aNode.type){
			case Node.NODE_SUB:
				aNode.button.styleName = "taskSub";
				aNode.type = Node.NODE_SUB;
				break;
    			case Node.NODE_TASK:
    				aNode.button.styleName = "taskNode";
    				aNode.type = Node.NODE_TASK;
    				break;
    			case Node.NODE_NOTIFY:
    				aNode.button.styleName = "notifyNode";
    				aNode.type = Node.NODE_NOTIFY;
    				break;
    			case Node.NODE_TIMER:
    				aNode.button.styleName = "timerNode";
    				aNode.type = Node.NODE_TIMER;
    				break;
    			case Node.NODE_START:
    				aNode.button.styleName = "startNode";
    				aNode.type = Node.NODE_START;
    				startNode = aNode;
    				break;
    			case Node.NODE_END:
    				aNode.button.styleName = "endNode";
    				aNode.type = Node.NODE_END;
    				endNode = aNode;
    				break;
    			case Node.NODE_SCRIPT:
    				aNode.button.styleName = "scriptNode";
    				aNode.type = Node.NODE_SCRIPT;
    				break;
    			case Node.NODE_AND:
    				aNode.button.styleName = "andNode";
    				aNode.type = Node.NODE_AND;
    				break;
    			case Node.NODE_OR:
    				aNode.button.styleName = "orNode";
    				aNode.type = Node.NODE_OR;
    				break;
    			case Node.NODE_ROUND:
    				aNode.button.styleName = "roundNode";
    				aNode.type = Node.NODE_ROUND;
    				break;
    			case Node.NODE_GROUND:
    				aNode.button.styleName = "groundNode";
    				aNode.type = Node.NODE_GROUND;
    				break;
    			}
			touched = true;
		}
		
		public function placeNewNode(pt:Point, tip:String, title:String, type:String):Node{
   			var aNode:Node = new Node();
			aNode.type = type;
			aNode.button.toolTip = tip;
			aNode.dlabel.text = title;
			aNode.title = title;
			
			setNodeStyle(aNode);

			addChild(aNode);
			dataModel.nodes.addItem(aNode);
			
			aNode.x = pt.x-aNode.width/2;
			aNode.y = pt.y-12;
			aNode.resetSize();

			_validateWft();
			touched = true;
			
			return aNode;

		}
		
		public function placeChildNodes():void{
			for(var i:int = 0; i<dataModel.nodes.length; i++){
				var node:Node = dataModel.nodes.getItemAt(i) as Node;
				setNodeStyle(node);
				addChild(node);
			}
		}

		public function placeNode(aNode:Node):void{
			setNodeStyle(aNode);

			addChild(aNode);
			dataModel.nodes.addItem(aNode);
		}
		public function placeJoint(aJoint:Joint):void{
			addChild(aJoint);
		}
		
		public function cancelLinking():void{
			linking=false;
			fromBtn= null;
			toBtn = null;
			linkFrom = null;
			linkTo = null;
			drawLineFromNode = null;
			drawLineToPoint = null;
			lastTempJoint = null;
			for(var i:int = 0; i<tempJoints.length; i++){
				removeChild((Joint)(tempJoints.getItemAt(i)));
			}
			tempJoints = new ArrayCollection();
			updateDisplayList(unscaledWidth, unscaledHeight);
			
		}
		
		public function completeLinking():void{
			var isExisting:Boolean = false;
			var oldLinkOption= null;
			for(var i:int = 0; i<dataModel.links.length;){
				var link:Link = dataModel.links.getItemAt(i) as Link;
				//Remove reversed link
				if(link.lto == fromBtn.parent && link.lfrom == toBtn.parent){
					if(this.contains(link.label)){
						this.removeChild(link.label);
					}
					for(var j:int = 0; j<link.joints.length; j++){
						removeChild((Joint)(link.joints.getItemAt(j)));
					}
				   	dataModel.links.removeItemAt(i);
				}
				if((link.lfrom == fromBtn.parent && link.lto == toBtn.parent)){
					if(this.contains(link.label)){
						this.removeChild(link.label);
					}
					oldLinkOption = link.option;
					for(var j:int = 0; j<link.joints.length; j++){
						removeChild((Joint)(link.joints.getItemAt(j)));
					}
					dataModel.links.removeItemAt(i);
				} 
				i++;
			}
			
				var newLink:Link = new Link(fromBtn.parent as Node, toBtn.parent as Node);
				newLink.option = oldLinkOption;
				for(var j:int = 0; j<tempJoints.length; j++){
					var tmp:Joint = (Joint)(tempJoints.getItemAt(j));
					tmp.link = newLink;
					newLink.joints.addItem(tmp);
				}
				tempJoints = new ArrayCollection();
				lastTempJoint = null;
				dataModel.links.addItem(newLink);
				
				
			fromBtn = null;
			toBtn = null;
			linkFrom = null;
			linkTo = null;
			_validateWft();
			touched = true;
			drawLineFromNode = null;
			drawLineToPoint = null;
			updateDisplayList(unscaledWidth, unscaledHeight); 
	   		
		}
		
		public function placeLink(fromNode:Node, toNode:Node):void{
			dataModel.links.addItem(new Link(fromNode, toNode));
			touched = true;
		}
		
		
		public function trytoStartLinkMove(link:Link):Link{
			if(link != null) 
			{
				linkToMove = link;
				linkMoving = true;
				fromBtn = linkToMove.lfrom.button;
				return linkToMove;
			}else{
				linkToMove = null;
				linkMoving = false;
				return linkToMove;
			}
		}
		public function isLinkMoving():Boolean{
			return linkMoving;
		}
		public function cancelLinkMove():void{
			linkToMove = null;
			linkMoving = false;
			drawLineFromNode = null;
			drawLineToPoint = null;
		}
		public function cancelAll():void{
			cancelLinking();
			cancelDragging();
		}
		public function finishDragging():void{
			cancelDragging();
		}
		public function finishJointDragging():void{
			cancelJointDragging();
		}
		public function cancelDragging():void{
			dragging = false;
			selNode = null;
			selBtn = null;
		}
		public function cancelJointDragging():void{
			jointDragging = false;
			selJoint = null;
		}

		
	   public function drawLinkToPoint(aNode:Node, tx:int, ty:int):void{
		drawLineFromNode = aNode;
		drawLineToPoint = new Point(tx, ty);
		updateCanvas();
	    }
	    
		public function drawLine(fx:int, fy:int, tx:int, ty:int, thickness:Number=1, color:uint=0x0000FF, alpha:Number=1):void{
			graphics.lineStyle(thickness, color, alpha);
			graphics.moveTo(fx, fy);
			graphics.lineTo(tx, ty);
		}
	    public function drawLink(link:Link,  thickness:Number=1, color:uint=0x0000FF, alpha:Number=1.0):void{
	    	link.setLabel();
	    	drawLinkBetweenNodes(link, link.lfrom, link.lto, link.option, thickness, color, alpha);
	    }
	    
		private function mapX(x:int):int{
			return x-this.horizontalScrollPosition;
		}
		private function mapY(y:int):int{
			return y-this.verticalScrollPosition;
		}
		private function drawTempJointLine(joint:Joint, thickness:Number=1, color:uint=0x0000FF, alpha:Number=1.0):void{
			var fx:int, fy:int, tx:int, ty:int;
			if(joint.lastJoint == null){
				//from linkFromNode
				fx = mapX(joint.linkFromNode.x) + joint.linkFromNode.width/2;
				fy = mapY(joint.linkFromNode.y) + 12;
			}else{
				fx = joint.lastJoint.cx;
				fy = joint.lastJoint.cy;
			}
			tx = joint.cx;
			ty = joint.cy;
			graphics.lineStyle(thickness, color, alpha);
			graphics.moveTo(fx, fy);
			graphics.lineTo(tx, ty);
		}
	   private function drawLinkBetweenNodes(link:Link, aNode:Node, bNode:Node, theOption:String, thickness:Number=1, color:uint=0x0000FF, alpha:Number=1.0):void{
	    	var fx:int = mapX(aNode.x) + aNode.width/2;
	    	var fy:int = mapY(aNode.y) + 12;
	    	var tx:int = mapX(bNode.x) + bNode.width/2;
	    	var ty:int = mapY(bNode.y) + 12;

			//确定线条颜色
			//If link.lfrom.task_decision was set. link.lfrom must have been finished.
			//if link.lto.task_status was set, the process have go through this 
			if( (theOption == link.lfrom.task_decision &&
				link.lfrom.task_status == "finished") || 
				(link.lfrom.task_status == "finished" && 
					(link.lto.task_status == "running" || 
					 link.lto.task_status == "finished"
					)
				)
			){
				graphics.lineStyle(3, 0x00FF00, 1);
			}else{
				graphics.lineStyle(thickness, color, alpha);
			}

			//Draw joints
	    	graphics.moveTo(fx, fy);
			for(var j:int=0; j<link.joints.length; j++){
				graphics.lineTo(mapX(link.joints[j].cx), mapY(link.joints[j].cy));
			}
			graphics.lineTo(tx, ty);

			//确定Label的位置
			var labelOnFrom:Point = null;
			var labelOnTo:Point = null;
			labelOnFrom = new Point(fx, fy);
			if(link.joints.length>0){
				//如果有Joints, 则应放在from节点到第一个Joint上
				labelOnTo = new Point(link.joints[0].cx, link.joints[0].cy);
			}else{
				//否则，应放在from节点到to节点上
				labelOnTo = new Point(tx, ty);
			}
			var ang:Number = 0; var ltx:int = 0, lty:int = 0; 
			var distance:int = Math.sqrt(Math.abs(labelOnTo.x-labelOnFrom.x)*Math.abs(labelOnTo.x-labelOnFrom.x) + 
				Math.abs(labelOnTo.y-labelOnFrom.y)*Math.abs(labelOnTo.y-labelOnFrom.y));
			if(labelOnTo.x >= labelOnFrom.x){
				ang = Math.atan(-(labelOnTo.y-labelOnFrom.y)/(labelOnTo.x-labelOnFrom.x));
				ltx = labelOnTo.x+this.horizontalScrollPosition - distance/2.0*Math.cos(ang);
				lty = labelOnTo.y+this.verticalScrollPosition + distance/2.0*Math.sin(ang);
			}else{
				ang = Math.atan((labelOnTo.y-labelOnFrom.y)/(labelOnTo.x-labelOnFrom.x));
				ltx = labelOnTo.x+this.horizontalScrollPosition + distance/2.0*Math.cos(ang);
				lty = labelOnTo.y+this.verticalScrollPosition + distance/2.0*Math.sin(ang);
			}
			
			link.label.x = ltx;
			link.label.y = lty;
			if(link.label.text != ""){
				var lineMetrics:TextLineMetrics = measureText(link.label.text);
				link.label.x -= lineMetrics.width/2;
				this.addChild(link.label);
			}

	    	var deltax:int =0; var deltay:int =0; var mx:int =0; var my:int =0; var p1x:int =0;
	    	var p1y:int =0; var p2x:int = 0; var p2y:int = 0;
	    	var vtx:int = 0, vty:int =0;
			var endX:int=0, endY:int=0, fromX:int=0, fromY:int=0;
			if(link.joints.length>0){
				fromX = link.joints[link.joints.length-1].cx;
				fromY = link.joints[link.joints.length-1].cy;
			}else{
				fromX = fx;
				fromY = fy;
			}
			endX = tx;
			endY = ty;
	    				
			//开始画三角箭头
			var distance:int = Math.sqrt(Math.abs(endX-fromX)*Math.abs(endX-fromX) + 
				Math.abs(endY-fromY)*Math.abs(endY-fromY));
			if(endX>=fromX){
				ang = Math.atan(-(endY-fromY)/(endX-fromX));
				deltax = 4 * Math.cos(Math.PI/2-ang);
				deltay = 4 * Math.sin(Math.PI/2-ang);
				mx = endX - 20 * Math.cos(ang);
				my = endY + 20 * Math.sin(ang);
				vtx = endX - 8*Math.cos(ang);
				vty = endY + 8*Math.sin(ang);
				
				p1x = mx - deltax; p1y = my - deltay; p2x = mx + deltax; p2y = my + deltay;
			}else{
				ang = Math.atan((endY-fromY)/(endX-fromX));
				deltax = 4 * Math.cos(Math.PI/2-ang);
				deltay = 4 * Math.sin(Math.PI/2-ang);
				mx = endX + 20 * Math.cos(ang);
				my = endY + 20 * Math.sin(ang);
				vtx = endX + 8*Math.cos(ang);
				vty = endY + 8*Math.sin(ang);
				
				p1x = mx - deltax; p1y = my + deltay; p2x = mx + deltax; p2y = my - deltay;
			}
			
			graphics.beginFill(0x0000FF, 1);
			graphics.moveTo(fromX, fromY);
			graphics.lineTo(endX, endY);
			graphics.moveTo(vtx, vty);
			graphics.lineTo(p1x, p1y);
			graphics.lineTo(p2x, p2y);
			graphics.lineTo(vtx, vty);
			graphics.endFill();

	    	
	    }
	    
	    public function setProcStatus(status:String):void{
		    showInfo(status);
	    }
		public function showMemo(memo:String):void{
			showInfo(memo);
		}
	    public function showInfo(msg:String):void{
	    	statusLabel.text = msg;
			statusLabel.visible = true;
	    }
		public function showError(msg:String):void{
			errorLabel.text = msg;
			errorLabel.visible = true;
		}
	    public function drawGhostLinkBetweenNodes(aNode:Node, bNode:Node, clear:Boolean=true):void{
	   		if(clear) {
	   			graphics.clear();
	   		
	   			graphics.beginFill(0xFFFFFF); 
        	     graphics.drawRect(0, 0, unscaledWidth, unscaledHeight); 
            	 graphics.endFill();
      		} 
	    	graphics.lineStyle(1, 0xFF0000, 0.8);
	    	graphics.moveTo(aNode.x-this.horizontalScrollPosition + aNode.width/2, aNode.y-this.verticalScrollPosition+12);
	    	graphics.lineTo(bNode.x-this.horizontalScrollPosition + bNode.width/2, bNode.y-this.verticalScrollPosition+12);
	    }	    
	    
	    public function updateCanvas():void{
	    	updateDisplayList(unscaledWidth, unscaledHeight);
	    }
		
	    /**
	      	* Draw links, circles around nodes only.
		* the nodes (button actually) was not added by this function
	      **/

		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void 
         { 
             super.updateDisplayList(unscaledWidth, unscaledHeight); 

             graphics.clear(); 

             graphics.beginFill(0xFFFFFF); 
             graphics.drawRect(0, 0, unscaledWidth, unscaledHeight); 
             graphics.endFill(); 

			if(dataModel == null)
				return;
             
             var i:int =0;
			 
			 //Draw temp Joints lines
			 for(var i:int=0; i<tempJoints.length; i++){
				 var joint:Joint = tempJoints.getItemAt(i) as Joint;
				 drawTempJointLine(joint, 1, 0x0000FF, 0.8);
			 }
			 //Draw links
			for(i=0; i<dataModel.links.length; i++){
				var link:Link = dataModel.links.getItemAt(i) as Link;
				if(link.selected){
					drawLink(link, 3, 0xFF0000, 0.8);
				}else if(link.hovered){
					drawLink(link, 2, 0xFF0000, 0.8);
				}else{
					drawLink(link, 1, 0x0000FF, 0.8);
				}
				if(link.lfrom.task_status != null){
					if(link.lfrom.task_status == "finished"){
						graphics.lineStyle(3, 0x00FF00, 1);
						graphics.drawCircle(link.lfrom.x-this.horizontalScrollPosition +link.lfrom.width/2, link.lfrom.y-this.verticalScrollPosition + 12, 24);
					}else if(link.lfrom.task_status == "running"){
						graphics.lineStyle(3, 0x0000FF, 1);
						graphics.drawCircle(link.lfrom.x-this.horizontalScrollPosition +link.lfrom.width/2, link.lfrom.y-this.verticalScrollPosition + 12, 24);
					}else if(link.lfrom.task_status == "suspended"){
						graphics.beginFill(0x0000FF, 0.2);
						graphics.lineStyle(3, 0x0000FF, 1);
						graphics.drawCircle(link.lfrom.x-this.horizontalScrollPosition +link.lfrom.width/2, link.lfrom.y-this.verticalScrollPosition + 12, 24);
						graphics.endFill();
					}
					if(link.lto.type == Node.NODE_END && link.lto.task_status == "finished"){
						graphics.lineStyle(3, 0x00FF00, 1);
						graphics.drawCircle(link.lto.x-this.horizontalScrollPosition +link.lto.width/2, link.lto.y-this.verticalScrollPosition + 12, 24);
					}
					
				}
			}

			//Draw line to point
			if(drawLineFromNode != null && drawLineToPoint != null){
				var tmpFromX:int = drawLineFromNode.x-this.horizontalScrollPosition + drawLineFromNode.width/2;
				var tmpFromY:int = drawLineFromNode.y-this.verticalScrollPosition + 12;
				var tmpToX:int = drawLineToPoint.x;
				var tmpToY:int = drawLineToPoint.y;
				if(lastTempJoint != null){
					tmpFromX = lastTempJoint.cx;
					tmpFromY = lastTempJoint.cy;
				}
				drawLine(tmpFromX, tmpFromY, tmpToX, tmpToY, 1, 0xAAAAAA, 0.5);
				
			}
			for(i=0; i<dataModel.nodes.length; i++){
				var node:Node = dataModel.nodes.getItemAt(i) as Node;
				if(node == selNode){ //Selected node
					graphics.lineStyle(3, 0x0000FF, 0.5);
					graphics.drawCircle(node.x -this.horizontalScrollPosition+ node.width/2, node.y-this.verticalScrollPosition + 12, 16);
				}
				if(node.dirty){ //Dirty node
					graphics.lineStyle(5, 0xFF0000, 0.2);
					graphics.drawCircle(node.x-this.horizontalScrollPosition + node.width/2, node.y-this.verticalScrollPosition + 12, 24);
					//node.dirty = false;
				}
			}
		}

		
		public function validateWft(event:Event=null):Boolean{
         	if(!_validateWft()){
         		((Application.application) as CFlowWftDesigner).msg = "Problem(s) found, please correct.";
         		updateDisplayList(unscaledWidth, unscaledHeight);
         		return false;
         	}else{
         		((Application.application) as CFlowWftDesigner).msg = "No problem. good work";
         		return true;
         	}
         }

         private function _validateWft():Boolean{
         	problemNodes.removeAll();
         	problemLinks.removeAll();
         	for(var i:int=0; i<dataModel.nodes.length; i++){
         		(dataModel.nodes.getItemAt(i) as Node).dirty = false;
         		if(((dataModel.nodes.getItemAt(i)) as Node).type == Node.NODE_START){
         			if(getRightLinks(dataModel.nodes.getItemAt(i) as Node).length==0){
         				(dataModel.nodes.getItemAt(i) as Node).dirty = true;
         				problemNodes.addItem(dataModel.nodes.getItemAt(i));
         			}
         		}
         		else if((dataModel.nodes.getItemAt(i) as Node).type == Node.NODE_END){
         			if(getLeftLinks(dataModel.nodes.getItemAt(i) as Node).length==0){
         				(dataModel.nodes.getItemAt(i) as Node).dirty = true;
         				problemNodes.addItem(dataModel.nodes.getItemAt(i));
         			}
         		}
         		else if((dataModel.nodes.getItemAt(i) as Node).type == Node.NODE_GROUND){
         			if(getLeftLinks(dataModel.nodes.getItemAt(i) as Node).length==0){
         				(dataModel.nodes.getItemAt(i) as Node).dirty = true;
         				problemNodes.addItem(dataModel.nodes.getItemAt(i));
         			}
         		}else if(getRightLinks(dataModel.nodes.getItemAt(i) as Node).length==0 || getLeftLinks(dataModel.nodes.getItemAt(i) as Node).length==0){
         			(dataModel.nodes.getItemAt(i) as Node).dirty = true;
         			problemNodes.addItem(dataModel.nodes.getItemAt(i));
         		}else{
         			(dataModel.nodes.getItemAt(i) as Node).dirty = false;
         		}
         	}
         	if(problemNodes.length>0)
         		return false;
         	else
         		return true;
         }
         
         
         private function getRightLinks(node:Node):ArrayCollection{
         	var ret:ArrayCollection = new ArrayCollection();
         	for(var i:int =0; i<dataModel.links.length; i++){
         		if((dataModel.links[i] as Link).lfrom == node){
         			ret.addItem(dataModel.links[i]);
         		}
         	}
         	return ret;
         }
         private function getLeftLinks(node:Node):ArrayCollection{
         	var ret:ArrayCollection = new ArrayCollection();
         	for(var i:int=0; i<dataModel.links.length; i++){
         		if((dataModel.links[i] as Link).lto == node){
         			ret.addItem(dataModel.links[i]);
         		}
         	}
         	return ret;
         }
		 
		 private function reportKeyDown(event:KeyboardEvent):void
		 {
			 if(event.charCode == 27){
				 if(linking) cancelLinking();
			 }
			 
		 }
		 
	}
}
