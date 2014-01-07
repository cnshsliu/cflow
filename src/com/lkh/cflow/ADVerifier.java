package com.lkh.cflow;
import javax.naming.*;
import javax.naming.Context;
import javax.naming.directory.*;
import java.util.Hashtable;
class DomainCon
{
	    public static void main(String[] args) {
		       Hashtable <String, String>env = new Hashtable<String, String>();
		       env.put(Context.INITIAL_CONTEXT_FACTORY,
		              "com.sun.jndi.ldap.LdapCtxFactory");
		       env.put(Context.PROVIDER_URL, "ldap://ldap.aspire-tech.com:389/");
		       env.put(Context.SECURITY_AUTHENTICATION, "simple");
		       env.put(Context.SECURITY_PRINCIPAL, "yltest");
		       env.put(Context.SECURITY_CREDENTIALS, "2009@aspire");

		       try {
		           DirContext ctx = new InitialDirContext(env);     
		            SearchControls searchCtls = new SearchControls();
		            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		            String searchFilter = "mail=*";
		            String searchBase = "DC=aspire,DC=aspire-tech,DC=com";
		            int totalResults = 0;
		            NamingEnumeration answer = ctx.search(searchBase, searchFilter,
		                                        searchCtls);
		            while (answer.hasMoreElements()) {
		                          SearchResult sr = (SearchResult) answer.next();
		                          totalResults++;
		                          System.out.println(sr.getName());
		            }
		            System.out.println("Total results: " + totalResults);
		            ctx.close();
		            ctx.close();
		       } catch (NamingException e) {
		           System.err.println("Problem getting attribute: " + e);
		       }
		    }


}
