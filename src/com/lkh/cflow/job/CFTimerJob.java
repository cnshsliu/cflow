package com.lkh.cflow.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

/**
 * <p>
 * This is just a simple job that gets fired off many times by example 1
 * </p>
 * 
 * @author Bill Kratzer
 */
public class CFTimerJob implements Job {

	/**
	 * Empty constructor for job initilization
	 */
	public CFTimerJob() {
	}

	/**
	 * <p>
	 * Called by the <code>{@link org.quartz.Scheduler}</code> when a
	 * <code>{@link org.quartz.Trigger}</code> fires that is associated with the
	 * <code>Job</code>.
	 * </p>
	 * 
	 * @throws JobExecutionException
	 *             if there is an exception while executing the job.
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {

		// This job simply prints out its job name and the
		// date and time that it is running
		String jobName = context.getJobDetail().getFullName();

		DbAdmin dbadmin = DbAdminPool.get();
		boolean kc = dbadmin.keepConnection(true);
		synchronized (CflowHelper.TIMER) {
			try {
				Connection conn = dbadmin.getConnection();
				String sql = "DELETE FROM cf_timer WHERE PRCID=? AND SESSID=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				Statement stat = conn.createStatement();

				ResultSet rs = stat.executeQuery("SELECT DEV, DOER, PRCID, SESSID, NODEID, DUE FROM cf_timer WHERE DUE < CURRENT_TIMESTAMP();");
				for (; rs.next();) {
					String doTaskRet = CflowHelper.prcManager.doTask(rs.getString("DEV"), dbadmin, rs.getString("DOER"), rs.getString("PRCID"), rs.getString("NODEID"), rs.getString("SESSID"), CflowHelper.DEFAULT_OPTION, null);
					ps.setString(1, rs.getString("PRCID"));
					ps.setString(2, rs.getString("SESSID"));
					ps.execute();
				}
				rs.close();
				ps.close();
				stat.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				DbAdminPool.ret(dbadmin);
			}
		}
	}
}
