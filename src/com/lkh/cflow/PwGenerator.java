package com.lkh.cflow;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PwGenerator {
	
	public static void main(String[] args){
		PwGenerator PWG = new PwGenerator();
		String pw = PWG.generate(8);
		System.out.println(pw);
	}
	
	public String generate(int len){
		char [] pw = new char[len];
		SecureRandom wheel = null;
		try {
			wheel = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		/*
		 * char[] printableAscii = new char[]{'!', '\"', '#', '$', '%', '(', ')', '*', '+', '-', '.', '/', '\'',
	            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ':', '<', '=', '>', '?', '@',
	            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	            '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~',
	            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	    */
		char[] printableAscii = new char[]{
				'.',  ':',  '=', '@','_', 
	            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
	            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		
		
		 for (int i = 0; i < len; i++) {
             int random = wheel.nextInt(printableAscii.length);
             pw[i] = printableAscii[random];
         }
		 
		 return new String(pw);
	}

}
