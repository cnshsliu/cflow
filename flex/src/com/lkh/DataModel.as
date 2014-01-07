package com.lkh
{
	import mx.collections.ArrayCollection;
	
	public class DataModel
	{
		[Bindable]
		public var wftname:String = "";
		public var wftcustomzer:String = "";
		public var nodes:ArrayCollection = new ArrayCollection();
		public var links:ArrayCollection = new ArrayCollection();
		[Bindable]
		public var editable:Boolean = true;
		public function DataModel()
		{
		}

	}
}
