package com.jugnoo.videos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jbns.util.sql.DBConnection;
import com.jugnoo.videos.data.Stats;
import com.jugnoo.videos.data.Status;
import com.jugnoo.videos.data.VideoAccess;

public class HistoryDao {

	Logger logger = LoggerManager.getLogger(HistoryDao.class);

	public List<VideoAccess> getVideoAccess(String channel) throws SQLException {

		String SQL = "select published_item.remote_id,user_social_accounts.user_token,user_social_accounts.secret"
				+ " from published_item,user_social_accounts where published_item.user_social_account_id = user_social_accounts.id "
				+ " and published_item.pub_channel = ? and published_item.status = ? ";

		List<VideoAccess> list = new ArrayList<VideoAccess>();
		Connection conn = DBConnectionManager.getConnection();
		try {
			PreparedStatement stm = conn.prepareStatement(SQL);
			stm.setString(1, channel);
			stm.setString(2, Status.FINISH.getStatus());
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				list.add(new VideoAccess(rs.getString("remote_id"), rs
						.getString("user_token"), rs.getString("secret")));
			}
			stm.close();
		} finally {
			DBConnectionManager.returnConnection((DBConnection) conn);
			logger.debug("GetVideoAccess[" + channel + "] list.size()=" + list.size());
		}
		return list;
	}

	public void saveHistory(List<Stats> statsList) throws SQLException {
		int i = 0;
		String sql = "insert into stats_history (published_item_id, view_count,comment_count,like_count,dislike_count,created_at,updated_at) "
				+ " select id, ?, ?, ?, ?, date(?), ? from published_item where remote_id = ? and pub_channel = ? "
				+ " on duplicate key update view_count=?, comment_count=?, like_count=?, dislike_count=?, updated_at = ? ";
		PreparedStatement ps;
		Connection conn = DBConnectionManager.getConnection();
		try {
			ps = conn.prepareStatement(sql);
			for (Stats s : statsList) {
				i = 1;
				ps.setLong(i++, s.getViewCount());
				ps.setLong(i++, s.getCommentCount());
				ps.setLong(i++, s.getLikeCount());
				ps.setLong(i++, s.getDislikeCount());
				ps.setTimestamp(i++, new Timestamp(s.getUpdateTime().getTime()));
				ps.setTimestamp(i++, new Timestamp(s.getUpdateTime().getTime()));
				ps.setString(i++, s.getRemoteId());
				ps.setString(i++, s.getChannel());
				ps.setLong(i++, s.getViewCount());
				ps.setLong(i++, s.getCommentCount());
				ps.setLong(i++, s.getLikeCount());
				ps.setLong(i++, s.getDislikeCount());
				ps.setTimestamp(i++, new Timestamp(s.getUpdateTime().getTime()));
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
		} finally {
			DBConnectionManager.returnConnection((DBConnection) conn);
			logger.debug("saveHistory statsList.size()="
					+ (statsList == null ? -1 : statsList.size()));
		}
	}

}
