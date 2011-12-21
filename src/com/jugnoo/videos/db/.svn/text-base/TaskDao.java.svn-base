/**
 * 2011-10-27 ÉÏÎç9:10:48
 */
package com.jugnoo.videos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.jbns.util.logging.Logger;
import com.jbns.util.logging.LoggerManager;
import com.jbns.util.sql.DBConnection;
import com.jugnoo.videos.data.Status;
import com.jugnoo.videos.data.Task;

/**
 * @author Fang
 * 
 */
public class TaskDao {

	Logger logger = LoggerManager.getLogger(TaskDao.class);

	public long addTask(Task task) throws SQLException {
		String sql = "insert into published_item "
				+ "(pub_channel, pub_type, status, remote_id, remote_space_id, "
				+ "play_url, user_social_account_id, file_token, pub_file_name, description, "
				+ " category, keywords, tags, cb_url, created_at, updated_at) values (?,?,?,?,?"
				+ ",?,?,?,?,?" + ",?,?,?,?, now(), now())";
		PreparedStatement ps;
		Connection conn = DBConnectionManager.getConnection();
		int i = 1;
		long id = -1;
		try {
			ps = conn.prepareStatement(sql,
					java.sql.Statement.RETURN_GENERATED_KEYS);

			ps.setString(i++, task.getChannel());
			ps.setString(i++, task.getType());
			ps.setString(i++, task.getStatus().getStatus());
			ps.setString(i++, task.getRemoteId());
			ps.setString(i++, task.getSpaceId());

			ps.setString(i++, task.getPlayUrl());
			ps.setLong(i++, task.getSocialId());
			ps.setString(i++, task.getToken());
			ps.setString(i++, task.getTitle());
			ps.setString(i++, task.getDescription());

			ps.setString(i++, task.getCategory());
			ps.setString(i++, task.getKeywords());
			ps.setString(i++, task.getTags());
			ps.setString(i++, task.getCallback());

			ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if ((rs != null) && rs.next()) {
				id = rs.getLong(1);
			}
			ps.close();
		} finally {
			DBConnectionManager.returnConnection((DBConnection) conn);
			logger.debug("addTask taskid=" + id + " task=" + task);
		}
		return id;
	}

	public String getVideoIdByToken(String token) throws SQLException {
		String vid = null;
		String sql = "select remote_id from published_item where id=?";
		Connection conn = DBConnectionManager.getConnection();
		try {
			PreparedStatement stm = conn.prepareStatement(sql);
			stm.setString(1, token);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				vid = rs.getString("remote_id");
			}
			stm.close();
		} finally {
			DBConnectionManager.returnConnection((DBConnection) conn);
			logger.debug("getVideoIdByToken token=" + token + " videoid=" + vid);
		}
		return vid;
	}

	public void updateTask(long id, Task task) throws SQLException {
		Connection conn = DBConnectionManager.getConnection();
		String sql = "update published_item set pub_channel = ?, pub_type = ?, status = ?, remote_id = ?, remote_space_id = ?, "
				+ " play_url = ?, user_social_account_id = ?, file_token = ?, pub_file_name = ?, description = ?, "
				+ " category = ?, keywords = ?, tags = ?, cb_url = ? where id = ?";
		int i = 1;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(i++, task.getChannel());
			ps.setString(i++, task.getType());
			ps.setString(i++, task.getStatus().getStatus());
			ps.setString(i++, task.getRemoteId());
			ps.setString(i++, task.getSpaceId());

			ps.setString(i++, task.getPlayUrl());
			ps.setLong(i++, task.getSocialId());
			ps.setString(i++, task.getToken());
			ps.setString(i++, task.getTitle());
			ps.setString(i++, task.getDescription());

			ps.setString(i++, task.getCategory());
			ps.setString(i++, task.getKeywords());
			ps.setString(i++, task.getTags());
			ps.setString(i++, task.getCallback());

			ps.setLong(i++, id);

			ps.executeUpdate();
			ps.close();
		} finally {
			DBConnectionManager.returnConnection((DBConnection) conn);
			logger.debug("updateTask taskid=" + id + " task=" + task);
		}
	}

	public void updateTaskStatus(Map<String, Status> map) throws SQLException {
		Connection conn = DBConnectionManager.getConnection();
		try {
			PreparedStatement stmt = conn
					.prepareStatement("update published_item set status = ?, updated_at = now() where remote_id = ?");
			for (Map.Entry<String, Status> entry : map.entrySet()) {
				stmt.setString(1, entry.getValue().getStatus());
				stmt.setString(2, entry.getKey());
				stmt.addBatch();
			}
			stmt.executeBatch();
			stmt.close();
		} finally {
			DBConnectionManager.returnConnection((DBConnection) conn);
			logger.debug("updateTaskStatus tasks.size()="
					+ (map == null ? -1 : map.size()));
		}
	}

}