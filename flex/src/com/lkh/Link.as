package com.lkh
{
	import mx.controls.Label;
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	public class Link extends Object
	{
		public var lfrom:Node = null;
		public var lto:Node=null;
		public var selected:Boolean = false;
		public var hovered:Boolean = false;
		public static var OPTION_DEFAULT:String = "DEFAULT";
		[Bindable]
		public var option:String = OPTION_DEFAULT;
		private var tmpOption:String = OPTION_DEFAULT;
		public var label:Label = new Label();
		public var joints:ArrayCollection = new ArrayCollection();
		
		public function Link(lfrom:Node, lto:Node, jointString:String = "", option:String = "DEFAULT"){
			super();
			this.lfrom = lfrom;
			this.lto = lto;
			this.selected = false;
			this.hovered = false;
			this.option = option;
			this.label.text = "";
			this.label.mouseEnabled = false;
			if(jointString != ""){
				var arr:Array = jointString.split(";");
				for(var i=0; i<arr.length; i++){
					var joint:Joint = new Joint();
					var xy:Array = arr[i].split(":");
					joint.cx = xy[0];
					joint.cy = xy[1];
					joint.x = joint.cx - 6;
					joint.y = joint.cy - 6;
					joint.link = this;
					joints.addItem(joint);
				}
			}
		}
		
		public function backupOption():void{
			tmpOption = option;
		} 
		public function restoreOption():void{
			option = tmpOption;
		}
		public function setLabel():void{
			if(option == OPTION_DEFAULT)
				label.text = "";
			else
				label.text = option;
		}
		public function placeJoints(canvas:MyCanvas){
			for(var j:int=0; j<joints.length; j++){
				canvas.placeJoint(joints[j]);
			}
		}
		
	}
}
