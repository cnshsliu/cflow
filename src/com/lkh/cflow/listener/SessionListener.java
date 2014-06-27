package com.lkh.cflow.listener;

import java.text.DateFormat;

import javax.servlet.http.HttpSessionEvent;  
import javax.servlet.http.HttpSessionListener;  

import org.apache.log4j.Logger;

import com.lkh.cflow.CflowHelper;

public class SessionListener implements HttpSessionListener {  
   static Logger logger = Logger.getLogger(SessionListener.class);
    public void sessionCreated(HttpSessionEvent event) {  
        synchronized (this) {  
            CflowHelper.sessionCount++;  
        }  
   
       logger.debug("Session Created: " + event.getSession().getId() + " at " + new java.util.Date());  
        logger.debug("Total Sessions: " + CflowHelper.sessionCount);  
    }  
    
     public void sessionDestroyed(HttpSessionEvent event) {  
         synchronized (this) {  
             CflowHelper.sessionCount--;  
         }  
        logger.debug("Session Destroyed: " + event.getSession().getId() + "at " + new java.util.Date());  
        logger.debug("Total Sessions: " + CflowHelper.sessionCount);  
    }  
     
}
