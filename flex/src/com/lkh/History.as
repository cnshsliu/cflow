package com.lkh
{
	import mx.collections.ArrayCollection;
	public class History
	{
		public var idx:int = 0;
		public var history:ArrayCollection = new ArrayCollection();
		[Bindable]
			public var forwardable:Boolean = false;
		[Bindable]
			public var backwardable:Boolean = false;
		public function History()
		{
			clear();
		}

		public function backward():DataModel{
			var ret:DataModel;
			if(idx < 1){
				ret = null;
			}else{
				idx --;
				ret = history.getItemAt(idx) as DataModel;
			}
			if(history.length <2){
				forwardable = false;
				backwardable = false;
			}else{
				if(idx > 0){
					backwardable = true;
				}else{
					backwardable = false;
				}
				if(idx <history.length-1){
					forwardable = true;
				}else{
					forwardable = false;
				}
			}
			return(ret);
		}
		public function forward():DataModel{
			var ret:DataModel;
			if(idx+1 > history.length -1){
				ret = null;
			}else{
				idx ++;
				ret = history.getItemAt(idx) as DataModel;
			}
			if(history.length <2){
				forwardable = false;
				backwardable = false;
			}else{
				if(idx > 0){
					backwardable = true;
				}else{
					backwardable = false;
				}
				if(idx <history.length-1){
					forwardable = true;
				}else{
					forwardable = false;
				}
			}
			return (ret);
		}
		public function current():DataModel{
			if(history.length > 0)
				return history.getItemAt(idx) as DataModel;
			else 
				return null;
		}

		public function clear():void{
			history.removeAll();
			idx = -1;
			forwardable = false;
			backwardable = false;
		}
		public function newDataModel():DataModel{
			var dm:DataModel = new DataModel();
			history.addItem(dm);
			idx = history.length-1;

			if(history.length <2){
				forwardable = false;
				backwardable = false;
			}else{
				if(idx > 0){
					backwardable = true;
				}
				if(idx <history.length-1){
					forwardable = true;
				}
			}

			return dm;
		}
		
		public function add(dm:DataModel):int{
			history.addItem(dm);
			idx = history.length-1;

			if(history.length <2){
				forwardable = false;
				backwardable = false;
			}else{
				if(idx > 0){
					backwardable = true;
				}
				if(idx <history.length-1){
					forwardable = true;
				}
			}
			return history.length;
		}
		public function set(dm:DataModel):int{
			if(idx < 0){
				return add(dm);
			}else{
				history.setItemAt(dm, idx);
				return history.length;
			}
		}
	}
}
