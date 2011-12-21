/**
 * 2011-10-27 ÉÏÎç9:26:19
 */
package com.jugnoo.videos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jbns.util.sql.DBConnection;
import com.jugnoo.videos.data.Stats;

/**
 * @author Fang
 * 
 */
public class StatsDao {

	Logger logger = LoggerManager.getLogger(StatsDao.class);

	public void saveStats(Stats stats) throws SQLException {
		int i = 1;
		String sql = "insert into stats(published_item_id, view_count,comment_count,like_count,dislike_count,created_at,updated_at) "
				+ " select id, ?, ?, ?, ?, now(), now() from published_item where remote_id = ? and pub_channel = ? "
				+ " on duplicate key update view_count=?, comment_count=?, like_count=?, dislike_count=?, updated_at=now()";
		PreparedStatement ps;
		Connection conn = DBConnectionManager.getConnection();
		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(i++, stats.getViewCount());
			ps.setLong(i++, stats.getCommentCount());
			ps.setLong(i++, stats.getLikeCount());
			ps.setLong(i++, stats.getDislikeCount());
			ps.setString(i++, stats.getRemoteId());
			ps.setString(i++, stats.getChannel());
			ps.setLong(i++, stats.getViewCount());
			ps.setLong(i++, stats.getCommentCount());
			ps.setLong(i++, stats.getLikeCount());
			ps.setLong(i++, stats.getDislikeCount());
			ps.executeUpdate();
			ps.close();
		} finally {
			DBConnectionManager.returnConnection((DBConnection) conn);
			logger.debug("saveStats stats=" + stats);
		}
	}

}