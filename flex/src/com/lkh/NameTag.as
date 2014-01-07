package com.lkh
{
	import flash.events.MouseEvent;
	
	import mx.controls.Button;

	public class NameTag extends Button
	{
		[Bindable]
				public var tag_id:String;
		[Bindable]
				public var tag_name:String;
		
		public function NameTag(id:String, name:String)
		{
			super();
			this.tag_id = id;
			this.tag_name = name;
			this.setStyle("paddingLeft", 0);
			this.setStyle("paddingRight", 0);
			this.setStyle("paddingTop", 0);
			this.setStyle("paddingBottom", 0);
			this.setStyle("horizontalGap", 0);
			this.label = tag_name;
			
		}
		
	}
}