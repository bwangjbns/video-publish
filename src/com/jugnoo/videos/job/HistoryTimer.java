package com.jugnoo.videos.job;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Properties;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;

public class HistoryTimer {

	Logger logger = LoggerManager.getLogger(HistoryTimer.class);
	public Scheduler sched = null;

	public HistoryTimer() throws Exception {
		Properties p = new Properties();
		p.put("org.quartz.scheduler.skipUpdateCheck", "true");
		p.put("org.quartz.scheduler.instanceName", "HistoryTimerScheduler");
		p.put("org.quartz.threadPool.threadCount", "10");
		p.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
		SchedulerFactory sf = new StdSchedulerFactory(p);
		sched = sf.getScheduler();
		sched.startDelayed(60);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized void addJob(String jobclass, String consumer_key,
			String consumer_secret, String schedule) {
		logger.info("jobclass=" + jobclass + ", consumer_key=" + consumer_key
				+ ", consumer_secret=" + consumer_secret + ", schedule="
				+ schedule);
		try {
			Class c = Class.forName(jobclass);
			JobDetail job = newJob(c).withIdentity(
					consumer_key + "$" + consumer_secret, schedule).build();
			job.getJobDataMap().put("consumer_key", consumer_key);
			job.getJobDataMap().put("consumer_secret", consumer_secret);
			CronTrigger trigger = newTrigger()
					.withIdentity(consumer_key + "#" + consumer_secret,
							schedule).withSchedule(cronSchedule(schedule))
					.build();
			sched.scheduleJob(job, trigger);
		} catch (Exception e) {
			logger.error("addJob exception=" + e);
		}
	}

	public void destory() throws Exception {
		if(sched != null) {
			sched.shutdown();
		}
	}

}
