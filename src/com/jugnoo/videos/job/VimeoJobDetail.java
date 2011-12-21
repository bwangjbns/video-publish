/**
 * 2011-11-28 ÉÏÎç11:02:47
 */
package com.jugnoo.videos.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jugnoo.videos.data.Stats;
import com.jugnoo.videos.data.Status;
import com.jugnoo.videos.data.VideoAccess;
import com.jugnoo.videos.db.HistoryDao;
import com.jugnoo.videos.db.TaskDao;
import com.jugnoo.videos.util.oauth.v1.VimeoUtil;

public class VimeoJobDetail implements Job {

	Logger logger = LoggerManager.getLogger(HistoryTimer.class);

	private void queryAll(String consumer_key, String consumer_secret) {
		HistoryDao hdao = new HistoryDao();
		TaskDao tdao = new TaskDao();
		List<VideoAccess> videoList;
		try {
			videoList = hdao.getVideoAccess("vimeo");
			List<Stats> statsList = new ArrayList<Stats>();
			Map<String, Status> lostList = new HashMap<String, Status>();
			Stats s;
			for (VideoAccess no : videoList) {
				try {
					s = query(no, consumer_key, consumer_secret);
					statsList.add(s);
				} catch (com.jugnoo.videos.ex.ResourceNotFoundException e) {
					logger.error("query[" + no + "] exception=" + e);
					lostList.put(no.getRemoteid(), Status.LOST);
				} catch (Exception e) {
					logger.error("query[" + no + "] exception=" + e);
				}
			}
			saveHistory(hdao, statsList);
			updateLost(tdao, lostList);
		} catch (Exception ex) {
			logger.error("queryAll", ex);
		}
	}

	private void saveHistory(HistoryDao hdao, List<Stats> statsList) {
		try {
			hdao.saveHistory(statsList);
		} catch (Exception e) {
			logger.error("saveHistory[statsList.size()=" + statsList.size()
					+ "] exception=" + e);
		}
	}

	private void updateLost(TaskDao tdao, Map<String, Status> lostList) {
		try {
			if (lostList.size() > 0) {
				tdao.updateTaskStatus(lostList);
			}
		} catch (Exception e) {
			logger.error("updateTaskStatus[lostList.size()=" + lostList.size()
					+ "] exception=" + e);
		}
	}

	Stats query(VideoAccess no, String consumer_key, String consumer_secret)
			throws Exception {
		Stats stats = new Stats();
		stats.setConsumerKey(consumer_key);
		stats.setConsumerSecret(consumer_secret);
		stats.setAccessToken(no.getAccessToken());
		stats.setTokenSecret(no.getTokenSecret());
		stats.setRemoteId(no.getRemoteid());
		stats.setChannel("vimeo");

		VimeoUtil vu = new VimeoUtil(consumer_key, consumer_secret,
				no.getAccessToken(), no.getTokenSecret());
		Stats s = vu.getVideoStat(no.getRemoteid());
		stats.setCommentCount(s.getCommentCount());
		stats.setLikeCount(s.getLikeCount());
		stats.setDislikeCount(s.getDislikeCount());
		stats.setViewCount(s.getViewCount());
		stats.setUpdateTime(new java.util.Date());
		return stats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		String consumer_key = ctx.getJobDetail().getJobDataMap()
				.getString("consumer_key");
		String consumer_secret = ctx.getJobDetail().getJobDataMap()
				.getString("consumer_secret");
		queryAll(consumer_key, consumer_secret);
	}

}