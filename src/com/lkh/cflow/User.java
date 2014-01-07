package com.lkh.cflow;

public class User {
	public String dev;
	public String id;
	public String identity;
	public String name;
	public String email;
	public String lang;
	public String notify;

	public static User newUser(String devId, String id, String identity, String name, String email, String lang, String notify) {
		User aUser = new User();
		aUser.dev = devId;
		aUser.id = id;
		aUser.identity = identity;
		aUser.name = name;
		aUser.email = email;
		aUser.lang = lang;
		aUser.notify = notify;

		return aUser;
	}

	@Override
	public boolean equals(Object obj) {
		User book = (User) obj;
		if (this.id.equals(book.id) && this.dev.equals(book.dev))
			return true;
		else
			return false;
	}
}
