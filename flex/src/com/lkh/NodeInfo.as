package com.lkh
{
	import mx.containers.Canvas;
	
	public class NodeInfo extends Object
	{
		[Bindable]
        public var label:String;
        
        [Bindable]
        public var nodeid:String;
 
        [Bindable]
        public var isSelected:Boolean;

		
		public function NodeInfo(lab:String, nid:String, selected:Boolean){
			label = lab;
			nodeid = nid;
			isSelected = selected;
			super();
		}

	}
}