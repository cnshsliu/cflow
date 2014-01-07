package com.lkh.cflow;

public class Developer {
	public String id;
	public String name;
	public String email;
	public String lang;

	public static Developer newDeveloper(String id, String name, String email, String lang) {
		Developer aDeveloper = new Developer();
		aDeveloper.id = id;
		aDeveloper.name = name;
		aDeveloper.email = email;
		aDeveloper.lang = lang;

		return aDeveloper;
	}

	@Override
	public boolean equals(Object obj) {
		Developer book = (Developer) obj;
		if (this.id.equals(book.id))
			return true;
		else
			return false;
	}
}
