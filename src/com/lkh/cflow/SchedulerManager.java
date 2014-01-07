package com.lkh.cflow;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class SchedulerManager {
	public static Scheduler scheduler = null;
	static Logger logger = Logger.getLogger(SchedulerManager.class);

	public void addJob(JobDetail job, boolean replace) throws SchedulerException {
		scheduler.addJob(job, replace);
	}

	/**
	 * 清除与job 该方法的调用时机包括：<BR>
	 * <ul>
	 * <li>调用DbAdmin.removePrc时</li>
	 * <li>流程运行到结束，系统执行createTask_END时</li>
	 * <li>在每次执行ProcessManager.doRepeatedJob时，自动检查进程是否依旧存在，如果进程不存在
	 * 或者进程文件找不到，也调用该方法，清除与进程相关联的scheduler</li>
	 * <li>通过管理控制台调用</li>
	 * </ul>
	 * 
	 * @param prcId
	 * @throws SQLException
	 */
	public void clearProcessCronJob(String prcId) throws SchedulerException {
		try {
			logger.debug("scheduler:" + scheduler);
			logger.debug("prcId:" + prcId);
			String[] jobs = scheduler.getJobNames(prcId);
			for (int j = 0; j < jobs.length; j++) {
				scheduler.deleteJob(jobs[j], prcId);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

	public void clearProcessCronJob(String prcId, String jobName) throws SchedulerException {
		scheduler.deleteJob(jobName, prcId);
	}

	public void suspendProcessCronJob(String prcId) throws SchedulerException {
		scheduler.pauseTriggerGroup(prcId);
	}

	public void resumeProcessCronJob(String prcId) throws SchedulerException {
		scheduler.resumeTriggerGroup(prcId);
	}
}
