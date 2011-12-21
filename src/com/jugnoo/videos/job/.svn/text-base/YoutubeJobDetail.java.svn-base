/**
 * 2011-11-28 ÉÏÎç11:02:47
 */
package com.jugnoo.videos.job;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.util.ResourceNotFoundException;
import com.google.gdata.util.ServiceException;
import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jugnoo.videos.data.Stats;
import com.jugnoo.videos.data.Status;
import com.jugnoo.videos.data.VideoAccess;
import com.jugnoo.videos.db.HistoryDao;
import com.jugnoo.videos.db.TaskDao;

public class YoutubeJobDetail implements Job {

	Logger logger = LoggerManager.getLogger(HistoryTimer.class);

	private void queryAll(String consumer_key, String consumer_secret) {
		HistoryDao hdao = new HistoryDao();
		TaskDao tdao = new TaskDao();
		List<VideoAccess> videoList;
		try {
			videoList = hdao.getVideoAccess("youtube");
			List<Stats> statsList = new ArrayList<Stats>();
			Map<String, Status> lostList = new HashMap<String, Status>();
			Stats s;
			for (VideoAccess no : videoList) {
				try {
					s = query(no, consumer_key, consumer_secret);
					statsList.add(s);
				} catch (ResourceNotFoundException e) {
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
			throws OAuthException, MalformedURLException, IOException,
			ServiceException {

		String clientId = "gdataSample-YouTubeAuth-1";
		String devKey = "AI39si4-CN0DOXTNFlsvYmmyEF4c7agys-_zgDWff7nghS-qeRr2LOcvIppwtlcKHnAqOYaFaFBYSPpcoGBYCcDoDaoPq2Q70g";
		String feedUrl = "http://gdata.youtube.com/feeds/api/videos/";

		Stats stats = new Stats();
		stats.setConsumerKey(consumer_key);
		stats.setConsumerSecret(consumer_secret);
		stats.setAccessToken(no.getAccessToken());
		stats.setTokenSecret(no.getTokenSecret());
		stats.setRemoteId(no.getRemoteid());
		stats.setChannel("youtube");

		YouTubeService service = new YouTubeService(clientId, devKey);
		GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
		oauthParameters.setOAuthConsumerKey(stats.getConsumerKey());
		oauthParameters.setOAuthConsumerSecret(stats.getConsumerSecret());
		oauthParameters.setOAuthToken(stats.getAccessToken());
		oauthParameters.setOAuthTokenSecret(stats.getTokenSecret());
		service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
		VideoEntry ve = service.getEntry(
				new URL(feedUrl + stats.getRemoteId()), VideoEntry.class);
		if (ve.getStatistics() != null) {
			stats.setViewCount(ve.getStatistics().getViewCount());
		}
		if (ve.getYtRating() != null) {
			stats.setLikeCount(ve.getYtRating().getNumLikes());
			stats.setDislikeCount(ve.getYtRating().getNumDislikes());
		}
		stats.setCommentCount(ve.getComments().getFeedLink().getCountHint());
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