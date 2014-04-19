package com.lkh.cflow.servlet;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.ha.ServerAdmin;
import com.lkh.cflow.ha.ServerRemoteRegister;
import com.lkh.cflow.job.CFTimerJob;

public class QuartzInitializerListener implements ServletContextListener {
	Logger logger = Logger.getLogger(QuartzInitializerListener.class);

	public static final String QUARTZ_FACTORY_KEY = "org.quartz.impl.StdSchedulerFactory.KEY";

	private boolean performShutdown = true;

	private Scheduler scheduler = null;

	/*
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * 
	 * Interface.
	 * 
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

	public void contextInitialized(ServletContextEvent sce) {

		logger.info("Initializing QuartzInitializerListener...");

		ServletContext servletContext = sce.getServletContext();
		StdSchedulerFactory factory;
		String path = servletContext.getRealPath("/");
		String log4jConfigFilePath = path + "WEB-INF";
		if (File.separator.equals("/")) {
			log4jConfigFilePath = log4jConfigFilePath + "/log4j_unix.xml";
		} else {
			log4jConfigFilePath = log4jConfigFilePath + "\\log4j_win.xml";
		}
		DOMConfigurator.configure(log4jConfigFilePath);
		try {
			String configPath = path + "WEB-INF";
			String cflowConfigFilePath;
			String quartzConfigFilePath = null;
			if(org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS){
				cflowConfigFilePath = configPath + "\\configuration_win.xml";
				quartzConfigFilePath = configPath + "\\cflow_quartz.properties";
			}else if(org.apache.commons.lang.SystemUtils.IS_OS_MAC_OSX){
				cflowConfigFilePath = configPath + "/configuration_mac_osx.xml";
				quartzConfigFilePath = configPath + "/cflow_quartz.properties";
			}else if(org.apache.commons.lang.SystemUtils.IS_OS_LINUX ||
					org.apache.commons.lang.SystemUtils.IS_OS_UNIX){
				cflowConfigFilePath = configPath + "/configuration_unix.xml";
				quartzConfigFilePath = configPath + "/cflow_quartz.properties";
			}else{
				logger.error(org.apache.commons.lang.SystemUtils.OS_NAME + " is not supported!");
				throw new Exception(org.apache.commons.lang.SystemUtils.OS_NAME + " is not supported!");
			}

			try {
				CflowHelper.setConfigFile(cflowConfigFilePath);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}

			String shutdownPref = CflowHelper.cfg.getString("quartz.shutdownOnUnload");

			CflowHelper.initialize(path);

			if (shutdownPref != null) {
				performShutdown = Boolean.valueOf(shutdownPref).booleanValue();
			}

			// get Properties
			if (quartzConfigFilePath != null) {
				logger.debug("ConfigPath: " + quartzConfigFilePath);
				factory = new StdSchedulerFactory(quartzConfigFilePath);
			} else {
				logger.debug("ConfigPath is null, use StdSchedulerFactory");
				factory = new StdSchedulerFactory();
			}

			// Always want to get the scheduler, even if it isn't starting,
			// to make sure it is both initialized and registered.
			scheduler = factory.getScheduler();
			logger.debug("scheduler=" + scheduler);
			CflowHelper.schManager.scheduler = scheduler;

			// Should the Scheduler being started now or later
			String startOnLoad = CflowHelper.cfg.getString("startSchedulerOnLoad");

			int startDelay = 0;
			String startDelayS = CflowHelper.cfg.getString("startDelaySeconds");
			try {
				if (startDelayS != null && startDelayS.trim().length() > 0)
					startDelay = Integer.parseInt(startDelayS);
			} catch (Exception e) {
				logger.warn("Cannot parse value of 'start-delay-seconds' to an integer: " + startDelayS + ", defaulting to 5 seconds.");
				startDelay = 5;
			}

			/*
			 * If the "start-scheduler-on-load" init-parameter is not specified,
			 * the scheduler will be started. This is to maintain backwards
			 * compatability.
			 */
			if (startOnLoad == null || (Boolean.valueOf(startOnLoad).booleanValue())) {
				if (startDelay <= 0) {
					// Start now
					scheduler.start();
					logger.info("Scheduler has been started...");
				} else {
					// Start delayed
					scheduler.startDelayed(startDelay);
					logger.info("Scheduler will start in " + startDelay + " seconds.");
				}
			} else {
				logger.error("Scheduler has not been started. Use scheduler.start()");
			}

			String factoryKey = CflowHelper.cfg.getString("servletContextFactoryKey");
			if (factoryKey == null) {
				factoryKey = QUARTZ_FACTORY_KEY;
			}

			servletContext.setAttribute(factoryKey, factory);

			SimpleTrigger trigger = new SimpleTrigger("trigger1", "CFTimer");
			trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			trigger.setRepeatInterval(60000L);
			JobDetail job = new JobDetail("CFTimerJob", "CFTimer", CFTimerJob.class);
			CflowHelper.schManager.scheduler.deleteJob("CFTimerJob", "CFTimer");
			try {
				CflowHelper.schManager.scheduler.scheduleJob(job, trigger);
				logger.debug("Job Scheduled: " + job);
			} catch (ObjectAlreadyExistsException oaee) {
				logger.debug("Job already exists.");
			} catch (Exception ex) {
				logger.debug("Job Schedule failed.");
				logger.error("Exception " + ex.getMessage());
				ex.printStackTrace();
			}

			/*
			 * JobDetail job1 = new JobDetail("USER_sessid", "PRCID",
			 * TaskRepeater.class); job1.setDurability(true);
			 * CflowHelper.schManager.scheduler.addJob(job1, false); CronTrigger
			 * trigger1 = new CronTrigger("USER_sessid", "PRCID", "USER_sessid",
			 * "PRCID", "0/2 * * * * ? *");
			 * CflowHelper.schManager.scheduler.scheduleJob(trigger1);
			 * Thread.sleep(10000L); System.out.println("Pause 10 seconds");
			 * CflowHelper.schManager.scheduler.pauseTriggerGroup("PRCID");
			 * Thread.sleep(10000L); System.out.println("Resume now");
			 * CflowHelper.schManager.scheduler.resumeTriggerGroup("PRCID");
			 */

			if (CflowHelper.useHA) {
				if (CflowHelper.isMaster) {
					new Thread(new ServerAdmin(CflowHelper.cfg.getInt("ha.checkInterval", 3000))).start();
					logger.info("Server admin started...");
				}
				new Thread(new ServerRemoteRegister(CflowHelper.cfg.getString("host"))).start();
			}

		} catch (Exception e) {
			logger.fatal("Scheduler failed to initialize: " + e.toString());
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {

		if (!performShutdown) {
			return;
		}
		try {
			CflowHelper.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (scheduler != null) {
				scheduler.shutdown();
			}
		} catch (Exception e) {
			logger.warn("Scheduler failed to shutdown cleanly: " + e.toString());
			e.printStackTrace();
		}

		logger.info("Scheduler successful shutdown.");
	}
}
