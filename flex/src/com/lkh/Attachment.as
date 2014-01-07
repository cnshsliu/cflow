package com.lkh
{
	public class Attachment
	{
		public var type:String;
		public var attname:String;
		public var label:String;
		public var value:String;
		public function Attachment(type:String, attname:String, label:String, value:String)
		{
			this.type = type;
			this.attname = attname;
			this.label = label;
			this.value = value;
		}
	}
}