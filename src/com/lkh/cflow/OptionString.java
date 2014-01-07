package com.lkh.cflow;

public class OptionString{
	private String option = "";
	private boolean isDefault = false;
	private String cleanOption = "";
	
	public  OptionString(String option){
		this.option = option;
		this.isDefault = _isDefaultOption();
		this.cleanOption = _getCleanOption();
		
	}
	
	public void setOption(String option){
		this.option = option;
		this.isDefault = _isDefaultOption();
		this.cleanOption = _getCleanOption();
	}
	
	public boolean getIsDefault(){
		return isDefault;
	}
	public String getCleanOption(){
		return cleanOption;
	}
	public String getOption(){
		return option;
	}


	private String _getCleanOption(){
		int firstPos = -1;
		int secondPos = -1;
		
		firstPos = option.indexOf('.');
		secondPos = option.indexOf("(d)", firstPos+1);
		
		if(secondPos == -1)
			return option.substring(firstPos + 1);
		else
			return option.substring(firstPos +1, secondPos);
		
	}
	
	private boolean _isDefaultOption(){
		return option.endsWith("(d)");
	}
	
	public static void main(String[] args) throws Exception{
		OptionString os = new OptionString("1.hello");
		System.out.println(os.getCleanOption() + " " + os.getIsDefault());
		
	}
}