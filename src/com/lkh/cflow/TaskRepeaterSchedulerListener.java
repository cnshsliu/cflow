package com.lkh.cflow;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;

public class TaskRepeaterSchedulerListener implements SchedulerListener {
	static Logger logger = Logger.getLogger(TaskRepeaterSchedulerListener.class);
	@Override
	public void jobScheduled(Trigger arg0) {

	}

	@Override
	public void jobUnscheduled(String arg0, String arg1) {

	}

	@Override
	public void jobsPaused(String arg0, String arg1) {

	}

	@Override
	public void jobsResumed(String arg0, String arg1) {

	}

	@Override
	public void schedulerError(String arg0, SchedulerException arg1) {

	}

	@Override
	public void schedulerShutdown() {

	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		try{
		JobDataMap dataMap = trigger.getJobDataMap();
		CflowHelper.prcManager.doCronJob(dataMap, CflowHelper.REPEAT_OPTION_FINISH);
		}catch(Exception ex){
			logger.error("Exception", ex);
		}
	}

	@Override
	public void triggersPaused(String arg0, String arg1) {

	}

	@Override
	public void triggersResumed(String arg0, String arg1) {

	}


}
