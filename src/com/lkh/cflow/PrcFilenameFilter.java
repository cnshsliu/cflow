package com.lkh.cflow;

import java.io.File;
import java.io.FilenameFilter;

public class PrcFilenameFilter  implements FilenameFilter{
	private boolean isPrcFile(String file){
		if(file.charAt(0) == 'P' && file.toLowerCase().endsWith(".xml")){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean accept(File dir, String name) {
		return isPrcFile(name);
	}
}
