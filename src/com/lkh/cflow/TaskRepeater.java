package com.lkh.cflow;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TaskRepeater implements Job {
	static Logger logger = Logger.getLogger(TaskRepeater.class);

	/**
	 * Empty constructor for job initilization
	 */
	public TaskRepeater() {
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			CflowHelper.prcManager.doCronJob(data, CflowHelper.REPEAT_OPTION_DEFUALT);
		} catch (Exception ex) {
			logger.error("Exception", ex);
		}
		// System.out.println(new java.util.Date());
		/*
		 * DbAdmin dbadmin = DbAdminPool.get(); try {
		 * dbadmin.clearProcessCronJob(dataMap.getString("PRCID")); } catch
		 * (SQLException e) { e.printStackTrace(); } DbAdminPool.ret(dbadmin);
		 */

	}

}
