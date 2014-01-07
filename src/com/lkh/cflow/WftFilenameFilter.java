package com.lkh.cflow;

import java.io.File;
import java.io.FilenameFilter;

public class WftFilenameFilter  implements FilenameFilter{
	private boolean isWftFile(String file){
		if(file.toLowerCase().endsWith(".xml")){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean accept(File dir, String name) {
		return isWftFile(name);
	}
}
