package com.lkh.cflow.test;

public class TestGetOS {
	public static void main(final String... args) throws Exception {
		if(org.apache.commons.lang.SystemUtils.IS_OS_MAC)
			System.out.println(org.apache.commons.lang.SystemUtils.OS_NAME + "1");
		if(org.apache.commons.lang.SystemUtils.IS_OS_MAC_OSX)
			System.out.println(org.apache.commons.lang.SystemUtils.OS_NAME + "2");
		if(org.apache.commons.lang.SystemUtils.IS_OS_LINUX)
			System.out.println(org.apache.commons.lang.SystemUtils.OS_NAME + "3");
		if(org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS)
			System.out.println(org.apache.commons.lang.SystemUtils.OS_NAME + "4");
		

		System.out.println();
	}

}
