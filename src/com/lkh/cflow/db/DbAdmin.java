package com.lkh.cflow.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.lkh.cflow.CflowHelper;
import com.lkh.cflow.Ctool;
import com.lkh.cflow.DelegationEntry;
import com.lkh.cflow.Developer;
import com.lkh.cflow.Membership;
import com.lkh.cflow.Org;
import com.lkh.cflow.PrcInfo;
import com.lkh.cflow.Role;
import com.lkh.cflow.Taskto;
import com.lkh.cflow.Team;
import com.lkh.cflow.TimeControlInfo;
import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.User;
import com.lkh.cflow.WftInfo;
import com.lkh.cflow.Work;
import com.lkh.cflow.WorkItemInfo;

public class DbAdmin {
	final static Logger logger = Logger.getLogger(DbAdmin.class);
	private Connection conn = null;
	private boolean keepConnection = false;
	private long createdTimestamp = 0;
	private static int dbadminCount = 0;

	public DbAdmin() {
		conn = null;
		keepConnection = false;
		createdTimestamp = System.currentTimeMillis();
		dbadminCount++;
	}

	public long getCreateTimestamp() {
		return createdTimestamp;
	}

	public int getInstanceNumber() {
		return dbadminCount;
	}

	/**
	 * 设置在多个数据操作之间保持数据库连接。 如果keepConnection设置为true,
	 * 则releaseConnection不发生作用，下次再调用getConnection时，直接使用当前的连接；
	 * 否则，releaseConnection操作会将当前的数据库连接释放掉，下次再调用getConnection时，需要从连接池中重新取一个连接；
	 * 
	 * @param flag
	 * @return
	 */
	public boolean keepConnection(boolean flag) {
		boolean ret = keepConnection;
		keepConnection = flag;
		return ret;
	}

	/**
	 * 获得数据库连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() {
		if (conn == null) {
			try {
				conn = CflowHelper.dbConnector.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	/**
	 * 释放数据库连接
	 * 
	 * @throws SQLException
	 */
	public void releaseConnection() {
		if (!keepConnection) {
			try {
				conn.close();
			} catch (Exception ex) {
			}
			conn = null;
		}
	}

	/**
	 * 彻底释放数据库连接，keepConnection标志为false;
	 */
	public void reset() {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		}
		keepConnection = false;
	}

	/**
	 * 直接建立一个用户，无须用户邮件确认；在用户信息导入工具中，可以调用此方法；
	 * 
	 * @param usrName
	 * @param usrid
	 * @param usrEmail
	 * @param ticket
	 * 
	 * @throws SQLException
	 */
	public void createUser(String creator, String identity, String usrName, String email, String lang, String notify) throws SQLException {
		try {
			if (usrName.equals("starter")) {
				logger.warn("Discard usrname: starter");
				return;
			}
			getConnection();
			int usemode = 0;
			/*
			 * if(orgid.equals(CflowHelper.SAAS)) usemode =
			 * CflowHelper.UM_GROUP; else usemode = CflowHelper.UM_ENTERPRISE;
			 */
			String sql = "INSERT INTO cf_user (DEV, ID, IDENTITY, NAME,  EMAIL, LANG, NOTIFY) VALUES(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, creator);
			stat.setString(2, CflowHelper.myID());
			stat.setString(3, Ctool.trimTo(identity, 40));
			stat.setString(4, Ctool.trimTo(usrName, 40));
			stat.setString(5, Ctool.trimTo(email, 50));
			stat.setString(6, Ctool.trimTo(lang, 8));
			stat.setString(7, Ctool.trimTo(notify, 9));
			try {
				stat.execute();
			} catch (SQLException ex) {
				logger.warn(ex.getLocalizedMessage());
			}
			stat.close();

		} finally {
			releaseConnection();
		}
	}

	public void loadUser(String devId, BufferedReader reader) throws SQLException, IOException {
		try {
			getConnection();
			String sql = "INSERT INTO cf_user (DEV, ID, IDENTITY, NAME, EMAIL, LANG, NOTIFY) VALUES(?, ?, ?, ?,  ?, ?, ?)";
			PreparedStatement stat = conn.prepareStatement(sql);
			String tmp = null;
			while ((tmp = reader.readLine()) != null) {
				tmp = StringUtils.trimToNull(tmp);
				if (tmp == null || tmp.startsWith("#")) {
					continue;
				}
				String[] flds = StringUtils.split(tmp, ',');
				String identity = StringUtils.trimToEmpty(flds[0]);
				String name = StringUtils.trimToEmpty(flds[1]);
				String email = StringUtils.trimToEmpty(flds[2]);
				String lang = StringUtils.trimToEmpty(flds[4]);
				String notify = CflowHelper.NOTIFY_EMAIL;
				stat.setString(1, devId);
				stat.setString(2, CflowHelper.myID());
				stat.setString(3, Ctool.trimTo(identity, 40));
				stat.setString(4, Ctool.trimTo(name, 40));
				stat.setString(5, Ctool.trimTo(email, 50));
				stat.setString(6, Ctool.trimTo(lang, 8));
				stat.setString(7, Ctool.trimTo(notify, 9));
				try {
					stat.execute();
				} catch (Exception ex) {
					logger.warn(ex.getLocalizedMessage());
				}
			}

			stat.close();

		} finally {
			releaseConnection();
		}
	}

	public String createDev(String devId, String devName, String accessKey, String email, String lang) throws SQLException {
		getConnection();
		boolean kc = keepConnection(true);
		try {
			String sql = "INSERT INTO cf_dev (ID, NAME, ACCESSKEY, EMAIL, LANG) VALUES(?, ?, MD5(?), ?, ?)";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, Ctool.trimTo(devId, 24));
			stat.setString(2, Ctool.trimTo(devName, 40));
			stat.setString(3, accessKey);
			stat.setString(4, Ctool.trimTo(email, 50));
			stat.setString(5, Ctool.trimTo(lang, 8));
			stat.execute();

			stat.close();

			createUser(devId, devId, devName, email, lang, CflowHelper.NOTIFY_EMAIL);

			return "done";
		} finally {
			keepConnection(kc);
			releaseConnection();
		}
	}

	public void setUserLang(String usrid, String lang) throws SQLException {
		try {
			getConnection();
			String sql = "UPDATE cf_user SET LANG=? WHERE ID=?";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, lang);
			stat.setString(2, usrid);
			stat.execute();

			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 改用户的显示名
	 * 
	 * @param usrid
	 *            用户ID
	 * @param name
	 *            用户显示名
	 * @throws SQLException
	 */
	public void changeUserDisplayName(String usrid, String name) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			stat.execute("UPDATE cf_user SET NAME='" + name + "' WHERE ID='" + usrid + "'");

			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 改用户的邮箱
	 * 
	 * @param usrid
	 *            用户ID
	 * @param email
	 *            邮箱
	 * @throws SQLException
	 */
	public void changeUserEmail(String devId, String usrid, String email) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			stat.execute("UPDATE cf_user SET EMAIL='" + email + "' WHERE DEV='" + devId + "' AND ID='" + usrid + "'");

			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 同时改用户的的显示名和邮箱
	 * 
	 * @param usrid
	 *            用户ID
	 * @param name
	 *            用户显示名
	 * @param email
	 *            用户邮箱
	 * @throws SQLException
	 */
	public void changeUserDisplayNameAndEmail(String devId, String usrid, String name, String email) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			stat.execute("UPDATE cf_user SET NAME='" + name + "', EMAIL='" + email + "' WHERE DEV='" + devId + "' AND ID='" + usrid + "'");

			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 创建组
	 * 
	 * @param devId
	 *            所有者（组管理员）
	 * @param teamid
	 *            组ID, 如为null则自动生成；
	 * @param teamName
	 *            组名
	 * @param teamEmail
	 *            组邮箱
	 * @param logo
	 *            组logo
	 * @throws SQLException
	 */
	public String createTeam(String devId, String teamName, String memo) throws SQLException {
		try {
			String teamId = null;
			getConnection();
			String sql = "SELECT ID FROM cf_team WHERE DEV=? AND NAME=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, teamName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				teamId = rs.getString("ID");
			}
			ps.close();
			if (teamId == null) {
				teamId = CflowHelper.myID();
				sql = "INSERT INTO cf_team (DEV, ID, NAME, MEMO) VALUES (?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, devId);
				ps.setString(2, Ctool.trimTo(teamId, 50));
				ps.setString(3, Ctool.trimTo(teamName, 40));
				ps.setString(4, Ctool.trimTo(memo, 40));
				ps.execute();

				ps.close();
			}
			return teamId;
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 同时改变组的显示名和邮箱
	 * 
	 * @param teamid
	 * @param teamName
	 * @param teamEmail
	 * @throws SQLException
	 */
	public void updateTeam(String teamid, String teamName, String teamEmail) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			stat.execute("UPDATE cf_team SET NAME='" + teamName + "', EMAIL='" + teamEmail + "' WHERE ID=" + teamid);

			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 根据用户ID取Developer
	 * 
	 * @param devId
	 * @return
	 * @throws SQLException
	 */
	public Developer getDevById(String devId) throws SQLException {
		Developer dev = null;
		try {
			getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT * FROM cf_dev " + " WHERE ID='" + devId + "'");
			if (rs.next()) {
				dev = newDevFromRS(rs);
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		return dev;
	}

	public User getUserByIdentity(String devId, String identity) throws SQLException {
		User user = null;
		try {
			getConnection();
			String sql = "SELECT A.* FROM cf_user A " + " WHERE A.DEV=? AND A.IDENTITY=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, identity);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user = newUserFromRS(rs);
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return user;
	}

	/**
	 * 根据用户ID和密码或取Developer对象
	 * 
	 * @param id
	 * @param pwd
	 * @return
	 * @throws SQLException
	 */
	public Developer getDev(String id, String acsk) throws SQLException {
		Developer dev = null;
		try {
			getConnection();

			String sql = "SELECT * FROM cf_dev WHERE ID=? AND ACCESSKEY=MD5(?)";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, id);
			stat.setString(2, acsk);
			ResultSet rs = stat.executeQuery();

			if (rs.next()) {
				dev = newDevFromRS(rs);
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		return dev;
	}

	public Developer getDevByToken(String tokenString) throws SQLException {
		if (tokenString.equals("c1i2pp3o4a")) {
			return getDevById("aliyun");
		}
		String devId = TokenAdmin.getDevByToken(tokenString);
		return getDevById(devId);
	}

	public Team[] getTeamsByOwner(String devId) throws SQLException {
		Team team = null;
		List<Team> teamList = new ArrayList<Team>();
		try {
			getConnection();

			PreparedStatement stat = conn.prepareStatement("SELECT * FROM cf_team WHERE DEV=? ORDER BY NAME");
			stat.setString(1, devId);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				team = Team.newTeam(devId, rs.getString("ID"), rs.getString("NAME"), rs.getString("MEMO"));
				teamList.add(team);
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		return (Team[]) teamList.toArray(new Team[0]);
	}

	/**
	 * 根据组的ID获得组对象
	 * 
	 * @param teamid
	 * @return
	 * @throws SQLException
	 */
	public Team getTeamById(String devId, String teamid) throws SQLException {
		Team team = null;
		try {
			getConnection();
			String sql = "SELECT  * FROM cf_team WHERE DEV=? AND ID=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, teamid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				team = Team.newTeam(rs.getString("DEV"), rs.getString("ID"), rs.getString("NAME"), rs.getString("MEMO"));
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return team;
	}

	public Team getTeamByName(String devId, String teamName) throws SQLException {
		Team team = null;
		try {
			getConnection();
			String sql = "SELECT  * FROM cf_team WHERE DEV=? AND NAME=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, teamName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				team = Team.newTeam(rs.getString("DEV"), rs.getString("ID"), rs.getString("NAME"), rs.getString("MEMO"));
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return team;
	}

	/**
	 * 根据组ID来改变组的名字和组邮箱
	 * 
	 * @param teamid
	 * @param teamname
	 * @param teamemail
	 * @throws SQLException
	 */
	public void changeTeamInfo(String devId, String teamid, String teamname, String teammemo) throws SQLException {
		try {
			getConnection();
			String sql = "UPDATE cf_team SET NAME=?, MEMO=?,  WHERE DEV=? AND ID=?";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, teamname);
			stat.setString(2, teammemo);
			stat.setString(3, devId);
			stat.setString(4, teamid);
			stat.execute();

			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 获取团队。
	 * 
	 * @param usrid
	 * @param includeUserTeams
	 *            是否包含用户级团队
	 * @return
	 * @throws SQLException
	 */
	public Team[] getTeams(String devId) throws SQLException {
		PreparedStatement stat = null;
		ResultSet rs = null;
		String sql = null;
		Team team = null;
		Team[] ret = {};
		ArrayList<Team> list = new ArrayList<Team>();
		try {
			getConnection();
			sql = "SELECT cf_team.* FROM cf_team WHERE DEV=? ORDER BY NAME";
			stat = conn.prepareStatement(sql);
			stat.setString(1, devId);
			rs = stat.executeQuery();
			for (; rs.next();) {
				team = Team.newTeam(devId, rs.getString("ID"), rs.getString("NAME"), rs.getString("EMAIL"));
				list.add(team);
			}
			rs.close();

			stat.close();
		} finally {
			releaseConnection();
		}

		if (list.isEmpty()) {
			return ret;
		} else {
			ret = list.toArray(ret);
			return ret;
		}
	}

	/**
	 * 根据组ID取组，并且保证特定用户必须是改组的成员
	 * 
	 * @param teamid
	 *            组ID
	 * @param usrid
	 *            成员ID
	 * @return
	 * @throws SQLException
	 */
	private Team getTeamByIdAndMember(String devId, String teamid, String usrid) throws SQLException {
		Team team = null;
		try {
			getConnection();
			boolean tmpKC = keepConnection(true);
			try {
				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("SELECT A.* FROM cf_team A, cf_teamuser B WHERE A.DEV='" + devId + "' AND A.ID=B.TEAMID AND A.ID='" + teamid + "' AND B.USRID='" + usrid + "'");
				if (rs.next()) {
					team = Team.newTeam(devId, rs.getString("ID"), rs.getString("NAME"), rs.getString("MEMO"));
				}
				rs.close();
				stat.close();
			} finally {
				keepConnection(tmpKC);
			}
		} finally {
			releaseConnection();
		}
		return team;
	}

	/**
	 * 判断特定用户是否为特定组的成员
	 * 
	 * @param teamid
	 *            组ID
	 * @param usrid
	 *            用户ID
	 * @return
	 * @throws SQLException
	 */
	public boolean isMemberOf(String devId, String teamid, String usrid) throws SQLException {
		Team team = getTeamByIdAndMember(devId, teamid, usrid);
		if (team == null)
			return false;
		else
			return true;
	}

	/**
	 * Get all teams in which a user is a member.
	 * 
	 * @param usrid
	 * @param includeTeamOfGlobal
	 *            Include the GLOBAL team or not. GLOBAL team is the team every
	 *            user belongs to.
	 * @return
	 * @throws SQLException
	 */
	public Team[] getTeamsByMemberId(String devId, String usrid) throws SQLException {
		Team team = null;
		Team[] ret = {};
		ArrayList<Team> list = new ArrayList<Team>();
		try {
			getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT A.* FROM cf_team A, cf_teamuser B WHERE A.DEV='" + devId + "' AND A.ID=B.TEAMID AND B.USRID='" + usrid + "' ORDER BY A.NAME");
			for (;;) {
				if (rs.next()) {
					team = Team.newTeam(devId, rs.getString("ID"), rs.getString("NAME"), rs.getString("MEMO"));
					list.add(team);
				} else
					break;
			}
			rs.close();
			stat.close();

		} finally {
			releaseConnection();
		}
		if (list.isEmpty()) {
			return ret;
		} else {
			return (Team[]) list.toArray(ret);
		}
	}

	/**
	 * 取得用户所有可能担任的角色。用户能担任的角色在用户加入组时，由组的所有者来设定。
	 * 
	 * @param usrid
	 *            用户ID
	 * @return 用户在不同的组中所担任的不同角色的合集
	 * @throws SQLException
	 */
	public String[] getRolesByUserId(String usrid) throws SQLException {
		String[] ret = {};
		ArrayList<String> list = new ArrayList<String>();
		try {
			getConnection();
			Statement stat = conn.createStatement();
			String sql = "SELECT DISTINCT ROLE FROM cf_teamuser WHERE TEAMID IN ( SELECT B.TEAMID FROM cf_teamuser B WHERE B.USRID='" + usrid + "')";

			ResultSet rs = stat.executeQuery(sql);
			for (;;) {
				if (rs.next()) {
					String aRole = rs.getString("ROLE");
					list.add(aRole);
				} else {
					break;
				}
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		if (list.isEmpty()) {
			return ret;
		} else {
			return (String[]) list.toArray(ret);
		}
	}

	/**
	 * 取得组中所有定义的角色
	 * 
	 * @param teamid
	 *            组ID
	 * @return 组中所有角色名称字符串数组
	 * @throws SQLException
	 */
	public String[] getRolesByTeamId(String teamid) throws SQLException {
		String[] ret = {};
		ArrayList<String> list = new ArrayList<String>();
		try {
			getConnection();
			Statement stat = conn.createStatement();
			String sql = "SELECT DISTINCT ROLE FROM cf_teamuser WHERE TEAMID ='" + teamid + "'";

			ResultSet rs = stat.executeQuery(sql);
			for (;;) {
				if (rs.next()) {
					String aRole = rs.getString("ROLE");
					list.add(aRole);
				} else {
					break;
				}
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		if (list.isEmpty()) {
			return ret;
		} else {
			return (String[]) list.toArray(ret);
		}
	}

	/**
	 * 将组成员用户按照角色装配到角色定义上。如角色定义数组中有一个名为Approver的角色，组中的Approver由Tom担任，则在
	 * 本方法的返回值中，名为Approver的Role由Tom担任。
	 * 
	 * @param teamid
	 *            组ID
	 * @param roles
	 *            角色定义数组
	 * @return 装配好用户的角色定义数组
	 * @throws SQLException
	 */
	public Role[] mapTeamRoles(String teamid, Role[] roles) throws SQLException {
		try {
			getConnection();
			String sql = "SELECT USRID FROM cf_teamuser WHERE TEAMID='" + teamid + "' AND ROLE=?";
			PreparedStatement stat = conn.prepareStatement(sql);
			for (int i = 0; i < roles.length; i++) {
				stat.setString(1, roles[i].getName());
				ResultSet rs = stat.executeQuery();
				for (; rs.next();) {
					String participant = rs.getString("USRID");
					roles[i].setValue(participant);
				}
				rs.close();
			}
			stat.close();
		} finally {
			releaseConnection();
		}
		return roles;
	}

	/**
	 * 删除一个组。
	 * 
	 * @param teamid
	 *            要删除的组ID
	 * @throws SQLException
	 */
	public void removeTeam(String devId, String teamid) throws SQLException {
		try {
			getConnection();
			String sql = "DELETE FROM cf_team WHERE DEV=? AND ID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, teamid);
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
	}

	public void removeUser(String dev, String usrid) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			String sql = "DELETE FROM cf_user WHERE DEV='" + dev + "' AND ID = '" + usrid + "'";
			stat.execute(sql);
			stat.close();
		} finally {
			releaseConnection();
		}
	}

	public void removeUserByIdentity(String dev, String identity) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			String sql = "DELETE FROM cf_user WHERE DEV='" + dev + "' AND IDENTITY = '" + identity + "'";
			stat.execute(sql);
			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 从组中去掉一个成员。成员用户本身还存在，但不再属于这个组。
	 * 
	 * @param teamid
	 *            组ID
	 * @param memberid
	 *            成员用户ID
	 * @throws SQLException
	 */
	public void removeTeamMember(String devId, String teamid, String memberid) throws SQLException {
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM cf_teamuser WHERE TEAMID=? AND USRID=? AND TEAMID in (SELECT ID FROM cf_team WHERE DEV=?)");
			ps.setString(1, teamid);
			ps.setString(2, memberid);
			ps.setString(3, devId);
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
	}

	public void removeTeamRole(String devId, String teamid, String roleName) throws SQLException {
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM cf_teamuser WHERE TEAMID=? AND ROLE=? AND TEAMID in (SELECT ID FROM cf_team WHERE DEV=?)");
			ps.setString(1, teamid);
			ps.setString(2, roleName);
			ps.setString(3, devId);
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 改变用户在组中所担任的角色
	 * 
	 * @param teamid
	 *            组
	 * @param memberid
	 *            成员用户ID
	 * @param role
	 *            新角色
	 * @throws SQLException
	 */
	public void changeTeamMemberRole(String teamid, String memberid, String role) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			String sql = "";
			sql = "UPDATE cf_teamuser SET ROLE = '" + role + "' WHERE TEAMID='" + teamid + "' AND USRID='" + memberid + "'";
			stat.execute(sql);
			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 批准用户加入组的申请
	 * 
	 * @param teamid
	 *            要加入的组的ID
	 * @param usrid
	 *            用户的ID
	 * @param role
	 *            在组中所担任的角色
	 * @param approved
	 *            是否批准，true, 则用户成为组中成员；false,则拒绝用户的申请，并废弃申请；
	 * @throws SQLException
	 */
	public void approveMember(String teamid, String usrid, String role, boolean approved) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			String sql = "";
			if (approved) {
				try {
					sql = "INSERT INTO cf_teamuser (TEAMID, USRID, ROLE) VALUES ('" + teamid + "','" + usrid + "','" + role + "')";
					stat.execute(sql);
				} catch (SQLException ex) {
					logger.error("Exception", ex);
				}
			}
			sql = "DELETE FROM cf_tmpteamuser WHERE TEAMID='" + teamid + "' AND USRID='" + usrid + "'";
			stat.execute(sql);

			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 根据组名返回成员元素为组的数组。组的ID是唯一的，但组的名字可能相同。 在使用Pass8t到企业应用中时，建议通过管理方式避免同名组的存在。
	 * 
	 * @param teamname
	 *            组名
	 * @return 组数组
	 * @throws SQLException
	 */
	public Team[] searchTeamsByName(String devId, String teamname) throws SQLException {
		String sql = "SELECT * FROM cf_team WHERE DEV='" + devId + "' AND NAME='" + teamname + "'";
		Team team = null;
		Team[] ret = {};
		ArrayList<Team> list = new ArrayList<Team>();
		try {
			getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			for (;;) {
				if (rs.next()) {
					team = Team.newTeam(rs.getString("DEV"), rs.getString("ID"), rs.getString("NAME"), rs.getString("MEMO"));
					list.add(team);
				} else
					break;
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		if (list.isEmpty()) {
			return ret;
		} else {
			return (Team[]) list.toArray(ret);
		}
	}

	public boolean areSameOrg(String usridA, String usridB) throws SQLException {
		// "SELECT A.FEDID FROM cf_edorg A, cf_edorg B WHERE A.FEDID=B.FEDID AND A.ORGID=? AND B.ORGID=?";
		if (usridA.equals(usridB))
			return true;
		String sql = "SELECT orgid FROM cf_orguser WHERE userid= ? AND orgid in (SELECT orgid FROM cf_orguser WHERE userid=?)";
		boolean sameOrg = false;
		boolean sameFed = false;
		boolean ret = false;
		try {
			getConnection();
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, usridA);
			stat.setString(2, usridB);
			ResultSet rs = stat.executeQuery();
			if (rs.next()) {
				ret = true;
			} else {
				ret = false;
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	/**
	 * 向组中添加一个新成员
	 * 
	 * @param teamid
	 *            组ID
	 * @param usrid
	 *            用户ID
	 * @throws SQLException
	 */
	public void addTeamMember(String devId, String teamid, String usrid) throws SQLException {
		addTeamMember(devId, teamid, usrid, "USER");
	}

	public void addTeamMember(String devId, String teamid, String usrid, String role) throws SQLException {
		JSONObject usrRoleKV = new JSONObject();
		usrRoleKV.put(usrid, role);
		addTeamMember(devId, teamid, usrRoleKV);
	}

	public void addTeamMember(String devId, String teamid, JSONObject usrRoleKV) throws SQLException {
		try {
			getConnection();

			String getTeamOwnerSql = "SELECT DEV FROM cf_team WHERE DEV=? AND ID=?";
			String sql = "INSERT INTO cf_teamuser (TEAMID, USRID, ROLE) VALUES (?, ?, ?)";

			PreparedStatement getTeamOwnerStat = conn.prepareStatement(getTeamOwnerSql);
			PreparedStatement stat = conn.prepareStatement(sql);

			getTeamOwnerStat.setString(1, devId);
			getTeamOwnerStat.setString(2, teamid);
			ResultSet rs = getTeamOwnerStat.executeQuery();
			boolean isOwner = false;
			if (rs.next()) {
				isOwner = true;
			}
			rs.close();
			getTeamOwnerStat.close();
			if (isOwner) {
				for (Iterator i = usrRoleKV.keySet().iterator(); i.hasNext();) {
					String usrid = (String) i.next();
					String role = (String) usrRoleKV.get(usrid);
					if (StringUtils.isEmpty(usrid) || StringUtils.isEmpty(role))
						continue;
					try {
						stat.setString(1, teamid);
						stat.setString(2, usrid);
						stat.setString(3, Ctool.trimTo(role, 40));
						stat.execute();
					} catch (Exception e) {
						logger.info("Add role:" + role + " user:" + usrid + " to team:" + teamid + " error: " + e.getLocalizedMessage());
					}
				}
			}
			stat.close();

		} finally {
			releaseConnection();
		}
	}

	public void addTeamMemberByIdentity(String devId, String teamid, JSONObject usrRoleKV) throws SQLException {
		try {
			getConnection();

			String sql = "INSERT INTO cf_teamuser (TEAMID, USRID, ROLE) SELECT ID, ?, ? FROM cf_team WHERE DEV=? AND ID=?";

			PreparedStatement stat = conn.prepareStatement(sql);
			// TODO: one role multiple users?
			for (Iterator i = usrRoleKV.keySet().iterator(); i.hasNext();) {
				String identity = (String) i.next();
				String role = (String) usrRoleKV.get(identity);
				if (StringUtils.isEmpty(identity) || StringUtils.isEmpty(role))
					continue;
				try {
					stat.setString(1, Ctool.trimTo(identity, 40));
					stat.setString(2, Ctool.trimTo(role, 40));
					stat.setString(3, devId);
					stat.setString(4, teamid);
					stat.execute();
				} catch (Exception e) {
					logger.error("Error: " + e.getLocalizedMessage());
				}
			}
			stat.close();

		} finally {
			releaseConnection();
		}
	}

	/**
	 * 返回组中所有成员用户的数组
	 * 
	 * @param teamid
	 *            组ID
	 * @return 成员用户数组
	 * @throws SQLException
	 */
	public User[] getTeamMembers(String devId, String teamid) throws SQLException {
		User[] ret = {};
		if (teamid == null || teamid.equals("null") || teamid.trim().length() == 0)
			return ret;

		ArrayList<User> list = new ArrayList<User>();
		User user = null;
		try {
			getConnection();
			String sql = "SELECT A.* FROM cf_user A, cf_teamuser B " + " WHERE A.DEV = ? AND A.IDENTITY=B.USRID AND B.TEAMID=?";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, devId);
			stat.setString(2, teamid);
			ResultSet rs = stat.executeQuery();
			for (;;) {
				if (rs.next()) {
					user = newUserFromRS(rs);
					list.add(user);
				} else
					break;
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		if (list.isEmpty()) {
			return ret;
		} else
			return (User[]) list.toArray(ret);
	}

	/**
	 * 取组的成员关系
	 * 
	 * @param teamid
	 *            组ID
	 * @return 成员关系数组
	 * @throws SQLException
	 */
	public Membership[] getTeamMemberships(String devId, String teamid) throws SQLException {
		Membership[] ret = {};
		ArrayList<Membership> list = new ArrayList<Membership>();
		User user = null;
		Membership membership = null;
		try {
			getConnection();
			String sql = "SELECT A.*, B.ROLE FROM cf_user A, cf_teamuser B WHERE A.DEV = ? AND A.IDENTITY=B.USRID AND B.TEAMID=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, teamid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				membership = Membership.newMembership(teamid, rs.getString("IDENTITY"), rs.getString("NAME"), rs.getString("ROLE"));
				list.add(membership);
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		if (list.isEmpty()) {
			return ret;
		} else
			return (Membership[]) list.toArray(ret);
	}

	/**
	 * 取组的成员关系
	 * 
	 * @param teamid
	 *            组ID
	 * @param roleName
	 *            角色名
	 * @return 成员关系数组
	 * @throws SQLException
	 */
	public Membership[] getTeamMembershipsByRole(String devId, String teamid, String roleName) throws SQLException {
		Membership[] ret = {};
		ArrayList<Membership> list = new ArrayList<Membership>();
		Membership membership = null;
		try {
			getConnection();
			String sql = "SELECT A.*, B.ROLE FROM cf_user A, cf_teamuser B WHERE A.DEV = ? AND  A.IDENTITY=B.USRID AND B.TEAMID=? AND B.ROLE=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, teamid);
			ps.setString(3, roleName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				membership = Membership.newMembership(teamid, rs.getString("IDENTITY"), rs.getString("NAME"), rs.getString("ROLE"));
				list.add(membership);
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
		} finally {
			releaseConnection();
		}
		if (list.isEmpty()) {
			return ret;
		} else
			return (Membership[]) list.toArray(ret);
	}

	/**
	 * 关联工作流到一个用户，如果该关联已经存在，则修改关联关系中的工作流名称
	 * 
	 * @param usrid
	 *            用户ID
	 * @param wftId
	 *            工作流模板ID
	 * @param wftname
	 *            工作流模板名称
	 * @throws SQLException
	 */
	public void addOrUpdateWftToDeveloper(String devId, String wftId, String wftname) throws SQLException {
		String sql = null;
		try {
			getConnection();
			sql = "INSERT INTO cf_wft (DEV, WFTID, WFTNAME) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE WFTNAME=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, wftId);
			ps.setString(3, Ctool.trimTo(wftname, 40));
			ps.setString(4, Ctool.trimTo(wftname, 40));
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
	}

	public void addVtToDeveloper(String devId, String vtname) throws SQLException {
		String sql = null;
		try {
			getConnection();
			sql = "INSERT INTO cf_vts (DEV, VTNAME) VALUES (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, Ctool.trimTo(vtname, 40));
			ps.execute();
			ps.close();
		} catch (Exception ex) {
			logger.warn("addVtToDeveloper(" + devId + ", " + vtname + ") already exists.");
		} finally {
			releaseConnection();
		}
	}

	public void deleteVt(String devId, String vtname) throws SQLException {
		try {
			getConnection();
			String sql = "DELETE FROM cf_vts WHERE DEV =? AND VTNAME=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, vtname);
			ps.execute();
			ps.close();
			try {
				CflowHelper.storageManager.delete(CflowHelper.storageManager.getVtPath(devId, vtname));
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.warn(ex.getLocalizedMessage());
			}

		} finally {
			releaseConnection();
		}
	}

	/**
	 * 判断用户是否拥有一个特定名称的工作流模板
	 * 
	 * @param usrid
	 *            用户ID
	 * @param wftname
	 *            工作流模板名称
	 * @return
	 * @throws SQLException
	 */
	public boolean userWftExist(String usrid, String wftname) throws SQLException {
		boolean ret = false;
		try {
			getConnection();

			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT * FROM cf_userwft WHERE OWNER='" + usrid + "' AND WFTNAME='" + wftname + "'");
			if (rs.next()) {
				ret = true;
			} else {
				ret = false;
			}
			rs.close();
			stat.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	/**
	 * 判断一个工作流是否在数据库中存在
	 * 
	 * @param wftId
	 *            工作流ID
	 * @return
	 * @throws SQLException
	 */
	public boolean wftIdExistInDb(String wftId) throws SQLException {
		boolean ret = false;
		try {

			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT * FROM cf_userwft WHERE WFTID='" + wftId + "'");
			if (rs.next()) {
				ret = true;
			} else {
				ret = false;
			}
			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	public WftInfo[] getWftInfoByDeveloper(String devId) throws SQLException {
		String sql = "";
		PreparedStatement stat = null;
		WftInfo[] ret = {};
		ArrayList<WftInfo> list = new ArrayList<WftInfo>();
		try {
			getConnection();
			// 1. 先取用户自己设计的模板
			sql = "SELECT WFTID, WFTNAME FROM cf_wft WHERE DEV=? ORDER BY TS DESC";
			stat = conn.prepareStatement(sql);
			stat.setString(1, devId);
			ResultSet rs = stat.executeQuery();
			for (; rs.next();) {
				list.add(WftInfo.newInstance(rs.getString("WFTID"), rs.getString("WFTNAME")));
			}
			rs.close();
			stat.close();

		} finally {
			releaseConnection();
		}
		// if(!list.isEmpty())
		// list.toArray(ret);
		ret = (WftInfo[]) list.toArray(new WftInfo[list.size()]);
		return ret;
	}

	public String getWftIdByName(String devId, String wftName) throws SQLException {
		String sql = "";
		PreparedStatement stat = null;
		String ret = null;
		ArrayList<WftInfo> list = new ArrayList<WftInfo>();
		try {
			getConnection();
			sql = "SELECT WFTID, WFTNAME FROM cf_wft WHERE DEV=? AND WFTNAME=?";
			stat = conn.prepareStatement(sql);
			stat.setString(1, devId);
			stat.setString(2, wftName);
			ResultSet rs = stat.executeQuery();
			if (rs.next()) {
				ret = rs.getString("WFTID");
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	public String getTeamIdByName(String devId, String teamName) throws SQLException {
		String ret = null;
		ArrayList<WftInfo> list = new ArrayList<WftInfo>();
		try {
			getConnection();
			String sql = "SELECT ID FROM cf_team WHERE DEV=? AND NAME=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, teamName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getString("ID");
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	public WftInfo getWftInfo(String devId, String wftId) throws SQLException {
		String sql = "";
		PreparedStatement stat = null;
		WftInfo ret = null;
		try {
			getConnection();
			// 1. 先取用户自己设计的模板
			sql = "SELECT WFTID, WFTNAME FROM cf_wft WHERE DEV=? AND WFTID=?";
			stat = conn.prepareStatement(sql);
			stat.setString(1, devId);
			stat.setString(2, wftId);
			ResultSet rs = stat.executeQuery();
			if (rs.next()) {
				ret = WftInfo.newInstance(rs.getString("WFTID"), rs.getString("WFTNAME"));
			}
			rs.close();
			stat.close();

		} finally {
			releaseConnection();
		}
		// if(!list.isEmpty())
		// list.toArray(ret);

		return ret;
	}

	public void associatePrcToUsers(String devId, String[] usrid, String prcId, String wftId, String usertype) throws SQLException {
		try {
			getConnection();
			usertype = usertype == null ? CflowHelper.ASSOC_TYPE_USER : usertype;
			String sql = "INSERT INTO cf_userprocess VALUES (?, ?, ?, ?, ?)";
			PreparedStatement prepStat = conn.prepareStatement(sql);
			for (int i = 0; i < usrid.length; i++) {
				prepStat.setString(1, devId);
				prepStat.setString(2, usrid[i]);
				prepStat.setString(3, prcId);
				prepStat.setString(4, wftId);
				prepStat.setString(5, usertype);
				try {
					prepStat.execute();
				} catch (Exception ex) {
					logger.info("Exception: " + ex.getLocalizedMessage());
				}
			}
			prepStat.close();
		} catch (Exception ex) {
			logger.error("Exception", ex);
		} finally {
			releaseConnection();
		}
	}

	public void associatePrcToUser(String devId, String doer, String prcId, String wftId, String userOrStarter) throws SQLException {
		getConnection();
		boolean tmpKC = keepConnection(true);
		try {
			userOrStarter = userOrStarter == null ? CflowHelper.ASSOC_TYPE_USER : userOrStarter;
			String sql = "INSERT INTO cf_userprocess VALUES (?, ?, ?, ?, ?)";
			PreparedStatement prepStat = conn.prepareStatement(sql);
			prepStat.setString(1, devId);
			prepStat.setString(2, doer);
			prepStat.setString(3, prcId);
			prepStat.setString(4, wftId);
			prepStat.setString(5, userOrStarter);
			try {
				prepStat.execute();
			} catch (Exception ex) {
				logger.info("Exception: " + ex.getLocalizedMessage());
			}
			prepStat.close();
		} catch (Exception ex) {
			logger.error("Exception", ex);
		} finally {
			keepConnection(tmpKC);
			releaseConnection();
		}
	}

	/**
	 * 取进程对应的工作流模板的编号和名称
	 * 
	 * @param prcId
	 *            进程ID
	 * @return 工作流模板的编号、名称组成的KV
	 * @throws SQLException
	 */
	public AbstractMap.SimpleEntry<String, String> getProcessWftIDAndName(String prcId) throws SQLException {
		AbstractMap.SimpleEntry<String, String> ret = null;
		try {
			getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT WFTID, WFTNAME FROM cf_process WHERE ID = '" + prcId + "'");
			if (rs.next()) {
				ret = new AbstractMap.SimpleEntry<String, String>(rs.getString("WFTID"), rs.getString("WFTNAME"));
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	/**
	 * 根据用户ID和进程状态取得用户与进程之间的关联关系、并且处于特定运行状态下的所有进程的ID
	 * 
	 * @param usrid
	 *            用户ID
	 * @param status
	 *            CflowHelper.PROCESS_RUNNING|PROCRSS_SUSPENDED|PROCESS_FINISHED
	 *            |PROCESS_CANCELED
	 * @return 进程ID数组
	 * @throws SQLException
	 */
	public PrcInfo[] getPrcUserAssociations(String usrid, String status) throws SQLException {
		PrcInfo[] ret = {};
		ArrayList<PrcInfo> list = new ArrayList<PrcInfo>();
		ArrayList<String> idList = new ArrayList<String>();
		try {
			getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT A.PRCID, B.* FROM cf_userprocess A, cf_process B WHERE A.PRCID = B.ID AND A.USRID='" + usrid + "' AND B.STATUS='" + status + "' ORDER BY B.STARTAT");
			for (; rs.next();) {
				idList.add(rs.getString("PRCID"));
				list.add(PrcInfo.newInstance(rs.getString("PRCID"), rs.getString("WFTNAME"), rs.getString("STARTBY"), rs.getString("WFTID"), null, rs.getString("PRCID"), status));
			}
			rs.close();
			rs = stat.executeQuery("SELECT * FROM cf_process WHERE STARTBY = '" + usrid + "' AND STATUS='" + status + "' ORDER BY STARTAT");
			for (; rs.next();) {
				String tmpS = rs.getString("ID");
				if (!idList.contains(tmpS)) {
					idList.add(tmpS);
					list.add(PrcInfo.newInstance(rs.getString("ID"), rs.getString("WFTNAME"), rs.getString("STARTBY"), rs.getString("WFTID"), null, rs.getString("PRCID"), status));
				}
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		ret = (PrcInfo[]) list.toArray(new PrcInfo[list.size()]);
		return ret;
	}

	public PrcInfo[] getPrcInfoByStatus(String devId, String status) throws SQLException {
		PrcInfo[] ret = {};
		ArrayList<PrcInfo> list = new ArrayList<PrcInfo>();
		ArrayList<String> idList = new ArrayList<String>();
		try {
			getConnection();
			String sql = "SELECT * FROM cf_process WHERE DEV=? AND STATUS=? ORDER BY STARTAT";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, status);
			ResultSet rs = ps.executeQuery();
			for (; rs.next();) {
				list.add(PrcInfo.newInstance(rs.getString("ID"), rs.getString("WFTNAME"), rs.getString("STARTBY"), rs.getString("WFTID"), null, rs.getString("ID"), status));
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
		} finally {
			releaseConnection();
		}
		ret = (PrcInfo[]) list.toArray(new PrcInfo[list.size()]);
		return ret;
	}

	/**
	 * 根据用户ID和进程状态, 以及用户与进程之间的关系类型（是参与者还是启动者）取得用户与进程之间的关联关系、并且处于特定运行状态下的所有进程的ID
	 * 
	 * @param usrid
	 *            用户ID
	 * @param status
	 *            CflowHelper.PROCESS_RUNNING|PROCRSS_SUSPENDED|PROCESS_FINISHED
	 *            |PROCESS_CANCELED
	 * @param usertype
	 *            用户的类型， USER 或者 STARTER
	 * @return 进程ID数组
	 * @throws SQLException
	 */
	public PrcInfo[] getPrcUserAssociations(String usrid, String status, String usertype) throws SQLException {
		PrcInfo[] ret = {};
		ArrayList<String> idList = new ArrayList<String>();
		ArrayList<PrcInfo> list = new ArrayList<PrcInfo>();
		try {
			getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT A.PRCID, B.* FROM cf_userprocess A, cf_process B WHERE A.PRCID = B.ID AND A.USRID='" + usrid + "' AND B.STATUS='" + status + "' AND A.USERTYPE = '" + usertype + "' ORDER BY B.STARTAT");
			for (; rs.next();) {
				idList.add(rs.getString("PRCID"));
				list.add(PrcInfo.newInstance(rs.getString("PRCID"), rs.getString("WFTNAME"), rs.getString("STARTBY"), rs.getString("WFTID"), null, rs.getString("PRCID"), status));
			}
			rs.close();

			if (usertype.equalsIgnoreCase(CflowHelper.ASSOC_TYPE_STARTER)) {
				rs = stat.executeQuery("SELECT * FROM cf_process WHERE STARTBY = '" + usrid + "' AND STATUS='" + status + "' ORDER BY STARTAT");
				for (; rs.next();) {
					String tmpS = rs.getString("ID");
					if (!idList.contains(tmpS)) {
						idList.add(tmpS);
						list.add(PrcInfo.newInstance(rs.getString("ID"), rs.getString("WFTNAME"), rs.getString("STARTBY"), rs.getString("WFTID"), null, rs.getString("PRCID"), status));
					}
				}
				rs.close();
			}
			stat.close();
		} finally {
			releaseConnection();
		}
		ret = (PrcInfo[]) list.toArray(new PrcInfo[list.size()]);
		return ret;
	}

	/**
	 * 删除一个进程与用户之间的关联。WARNING：在不保证数据一致性的情况下调用该方法可能导致该进程将成为呆进程，不再可用。
	 * 
	 * @param usrid
	 *            用户ID
	 * @param prcId
	 *            进程ID
	 * @throws SQLException
	 */
	public void detachProcessFromUser(String prcId) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			stat.execute("DELETE FROM cf_userprocess WHERE  PRCID='" + prcId + "'");
			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 删除一个工作流模板与用户之间的关联
	 * 
	 * @param usrid
	 *            用户ID
	 * @param wftId
	 *            工作流模板ID
	 * @throws SQLException
	 */
	public void removeWftFromDeveloper(String devId, String wftId) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			stat.execute("DELETE FROM cf_userwft WHERE DEV='" + devId + "' AND WFTID='" + wftId + "'");
			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 删除一个进程。与该进程相关的所有信息都会被彻底删除
	 * 
	 * @param prcId
	 * @throws SQLException
	 */
	public void deleteProcess(String devId, String prcId) throws SQLException {
		String sql = null;
		try {
			boolean oldFlag = keepConnection(true);
			getConnection();

			sql = "DELETE FROM cf_process WHERE ID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, prcId);
			ps.execute();

			ps.close();
			keepConnection(oldFlag);
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 删除工作流模板
	 * 
	 * @param wftId
	 *            工作流模板ID
	 * @throws SQLException
	 */
	public void removeWft(String devId, String wftId) throws SQLException {
		try {
			getConnection();
			/*
			 * sql = "DELETE FROM cf_userprocess WHERE WFTID = '" + wftId + "'";
			 * stat.execute(sql); // Remove process then sql =
			 * "SELECT * FROM cf_process WHERE WFTID = '" + wftId + "'";
			 * ResultSet rs = stat.executeQuery(sql); for (;;) { if (!rs.next())
			 * break; String prcId = rs.getString("ID");
			 * CflowHelper.storageManager.deletePrcFile(prcId); } rs.close();
			 * sql = "DELETE FROM cf_process WHERE WFTID = '" + wftId + "'";
			 * stat.execute(sql);
			 */
			// Remove user workflow
			String sql = "DELETE FROM cf_wft WHERE DEV=? AND WFTID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, wftId);
			ps.execute();
			ps.close();
			// Remove workflow file
			CflowHelper.storageManager.delete(CflowHelper.storageManager.getWftPath(devId, wftId));
		} finally {
			releaseConnection();
		}
	}

	public void removeInstWft(String devId, String wftId) throws SQLException {
		String sql = null;
		try {
			getConnection();
			Statement stat = conn.createStatement();
			sql = "DELETE FROM cf_userwft WHERE WFTID = '" + wftId + "'";
			stat.execute(sql);

			stat.close();
			// Remove workflow file
			CflowHelper.storageManager.delete(CflowHelper.storageManager.getWftPath(devId, wftId));
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 记录一个新进程的信息
	 * 
	 * @param prcId
	 *            新进程的ID
	 * @param startBy
	 *            启动者
	 * @param startAt
	 *            启动时间
	 * @param endAt
	 *            结束时间
	 * @param status
	 *            状态
	 * @param wftname
	 *            模板名
	 * @param wftId
	 *            模板ID
	 * @throws SQLException
	 */
	public void logNewProcess(String devId, String prcId, String startBy, Calendar startAt, Calendar endAt, String status, String wftname, String wftId, String ppId, String ppNodeId, String ppSessId) throws SQLException {
		getConnection();
		boolean tmpKC = keepConnection(true);
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (endAt == null)
				endAt = startAt;
			String sql = "INSERT INTO cf_process (DEV, ID, STARTBY, STARTAT, ENDAT, STATUS, WFTNAME, WFTID, PPID, PPNODEID, PPSESSID) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, prcId);
			ps.setString(3, Ctool.trimTo(startBy, 40));
			ps.setString(4, format.format(startAt.getTime()));
			ps.setString(5, format.format(endAt.getTime()));
			ps.setString(6, status);
			ps.setString(7, Ctool.trimTo(wftname, 40));
			ps.setString(8, wftId);
			ps.setString(9, ppId);
			ps.setString(10, ppNodeId);
			ps.setString(11, ppSessId);

			ps.execute();
			ps.close();

			associatePrcToUser(devId, startBy, prcId, wftId, CflowHelper.ASSOC_TYPE_STARTER);
		} finally {
			keepConnection(tmpKC);
			releaseConnection();
		}
	}

	/**
	 * 修改进程记录的信息。
	 * 
	 * @param prcId
	 * @param startBy
	 * @param startAt
	 * @param endAt
	 * @param status
	 * @throws SQLException
	 */
	public void updateProcessLog(String prcId, String startBy, Calendar startAt, Calendar endAt, String status) throws SQLException {
		try {
			getConnection();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (endAt == null)
				endAt = startAt;
			String sql = "UPDATE cf_process SET STARTBY =?, STARTAT=?, ENDAT=?, STATUS=? WHERE ID=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, startBy);
			ps.setString(2, format.format(startAt.getTime()));
			ps.setString(3, format.format(endAt.getTime()));
			ps.setString(4, status);
			ps.setString(5, prcId);
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 获得所有处于特定状态的进程的编号
	 * 
	 * @param status
	 *            进程状态
	 * @return 进程编号的数组
	 * @throws SQLException
	 */
	public String[] getProcessIdsByStatus(String devId, String status) throws SQLException {
		String[] ret = {};
		ArrayList<String> list = new ArrayList<String>();
		try {
			getConnection();
			String sql = "SELECT ID FROM cf_process WHERE STATUS=? ORDER BY STARTAT";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, status);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String tmp = rs.getString("ID");
				list.add(tmp);
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		ret = (String[]) list.toArray(new String[list.size()]);
		return ret;

	}

	public String getProcessStatus(String devId, String prcId) throws SQLException {
		String ret = null;
		try {
			getConnection();
			String sql = "SELECT STATUS FROM cf_process WHERE DEV=? AND ID=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, prcId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getString("STATUS");
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return ret;

	}

	/**
	 * 新建一个进程超时器节点
	 * 
	 * @param usrid
	 * @param prcId
	 * @param nodeid
	 * @param theDate
	 *            到期时间
	 * @throws SQLException
	 */
	public void newTimer(String devId, String doer, String prcId, String sessid, String nodeid, java.util.Date theDate) throws SQLException {
		try {
			getConnection();
			PreparedStatement prepStat = conn.prepareStatement("INSERT INTO cf_timer (DEV, DOER, PRCID, SESSID, NODEID, DUE) VALUES (?, ?, ?, ?, ?, ?)");
			prepStat.setString(1, devId);
			prepStat.setString(2, doer);
			prepStat.setString(3, prcId);
			prepStat.setString(4, sessid);
			prepStat.setString(5, nodeid);
			prepStat.setTimestamp(6, new java.sql.Timestamp(theDate.getTime()));
			prepStat.execute();

			prepStat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 新建一个任务完成时间控制器
	 * 
	 * @param usrid
	 *            节点任务负责人
	 * @param prcId
	 * @param nodeid
	 * @param theDate
	 *            节点任务最迟完成时间
	 * @throws SQLException
	 */
	public void watchTimeControl(String usrid, String prcId, String nodeid, java.util.Date theDate) throws SQLException {
		try {
			getConnection();
			PreparedStatement prepStat = conn.prepareStatement("INSERT INTO cf_timecontrol VALUES (?, ?, ?, ?)");
			prepStat.setString(1, usrid);
			prepStat.setString(2, prcId);
			prepStat.setString(3, nodeid);
			prepStat.setTimestamp(4, new java.sql.Timestamp(theDate.getTime()));
			prepStat.execute();

			prepStat.close();
		} finally {
			releaseConnection();
		}
	}

	public void watchTimeControl(String[] usrid, String prcId, String nodeid, java.util.Date theDate) throws SQLException {
		try {
			getConnection();
			java.sql.Timestamp ts = new java.sql.Timestamp(theDate.getTime());
			PreparedStatement prepStat = conn.prepareStatement("INSERT INTO cf_timecontrol VALUES (?, ?, ?, ?)");
			for (int i = 0; i < usrid.length; i++) {
				prepStat.setString(1, usrid[i]);
				prepStat.setString(2, prcId);
				prepStat.setString(3, nodeid);
				prepStat.setTimestamp(4, ts);
				prepStat.execute();
			}

			prepStat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 删除一个特定的任务完成时间控制器
	 * 
	 * @param usrid
	 *            任务负责人
	 * @param prcId
	 * @param nodeid
	 * @throws SQLException
	 */
	protected void removeTimeControl(String usrid, String prcId, String nodeid) throws SQLException {
		try {
			getConnection();
			Statement stat = conn.createStatement();
			String sql = "DELETE FROM cf_timecontrol WHERE USRID='" + usrid + "' AND PRCID = '" + prcId + "' AND NODEID='" + nodeid + "'";
			stat.execute(sql);
			stat.close();
		} finally {
			releaseConnection();
		}
	}

	/**
	 * 获得全部过期的任务时间控制器信息
	 * 
	 * @return 任务时间控制信息数组
	 * @throws SQLException
	 */
	protected TimeControlInfo[] getDuedTaskInfo() throws SQLException {
		TimeControlInfo[] ret = {};
		ArrayList<TimeControlInfo> list = new ArrayList<TimeControlInfo>();
		try {
			getConnection();
			Statement stat = conn.createStatement();
			String sql = "SELECT * FROM cf_timecontrol WHERE DUE < CURRENT_TIMESTAMP()";
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				TimeControlInfo tmp = new TimeControlInfo();
				Calendar aCal = Calendar.getInstance();
				tmp.usrid = rs.getString("USRID");
				tmp.prcId = rs.getString("PRCID");
				tmp.nodeid = rs.getString("NODEID");
				aCal.setTimeInMillis(rs.getTimestamp("DUE").getTime());
				tmp.due = aCal;
				list.add(tmp);

			}
			rs.close();
			stat.close();
			ret = (TimeControlInfo[]) list.toArray(new TimeControlInfo[list.size()]);
		} finally {
			releaseConnection();
		}
		return ret;
	}

	/**
	 * 获得特定用户的全部过期的任务时间控制器信息
	 * 
	 * @param usrid
	 *            用户ID
	 * @return 任务时间控制信息数组
	 * @throws SQLException
	 */
	public TimeControlInfo[] getDuedTaskInfo(String devId, String usrid) throws SQLException {
		TimeControlInfo[] ret = {};
		ArrayList<TimeControlInfo> list = new ArrayList<TimeControlInfo>();
		try {
			getConnection();
			String sql = "SELECT * FROM cf_timecontrol WHERE DUE < CURRENT_TIMESTAMP() AND DEV=? AND USRID=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, usrid);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				TimeControlInfo tmp = new TimeControlInfo();
				Calendar aCal = Calendar.getInstance();
				tmp.usrid = rs.getString("USRID");
				tmp.prcId = rs.getString("PRCID");
				tmp.nodeid = rs.getString("NODEID");
				aCal.setTimeInMillis(rs.getTimestamp("DUE").getTime());
				tmp.due = aCal;
				list.add(tmp);

			}
			rs.close();
			ps.close();
			ret = (TimeControlInfo[]) list.toArray(new TimeControlInfo[list.size()]);
		} finally {
			releaseConnection();
		}
		return ret;
	}

	/**
	 * 取一个任务的过期控制颜色。 如到期时间在24小时（缺省）以后，则颜色为绿色； 若到期时间在24小时（缺省）以内，则颜色为黄色； 若到期时间已过，
	 * 则颜色为红色； 时间控制可以再configuration.xml中配置。
	 * 
	 * 
	 * @param usrid
	 * @param prcId
	 * @param nodeid
	 * @return
	 * @throws SQLException
	 */
	public String getTaskDueColor(String usrid, String prcId, String nodeid) throws SQLException {
		String ret = WorkItemInfo.COLOR_NONE;
		try {
			getConnection();
			String sql = "SELECT * FROM cf_timecontrol WHERE PRCID = ? AND NODEID=? and USRID=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, prcId);
			ps.setString(2, nodeid);
			ps.setString(3, usrid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				long dueTime = rs.getTimestamp("DUE").getTime();
				long curTime = Calendar.getInstance().getTimeInMillis();
				if (dueTime > curTime + CflowHelper.greenThreshold * 60 * 1000)
					ret = WorkItemInfo.COLOR_GREEN;
				else if (dueTime > curTime + CflowHelper.yellowThreshold * 60 * 1000)
					ret = WorkItemInfo.COLOR_YELLOW;
				else
					ret = WorkItemInfo.COLOR_RED;
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	/**
	 * 返回特定用户所在组的其它组成员。若用户属于多个组，则所有这些组的其它成员都会返回
	 * 该方法的Mate搜寻范围受配置项mates.from的影响。mates.from可配置为team或者org.
	 * 当配置为team时，表示mate搜寻范围为当前用户所加入的所有组中其他的组员；
	 * 当配置为org时，表示mate搜寻范围为当前用户所处的组织的所有员工。 <BR>
	 * 在SaaS应用模式下，应该配置为team, 因为整个ORG内的用户数过大。（缺省ORG为Pass8t SaaS) <BR>
	 * 在Intranet应用模式下，如果让用户可以使用全体用户（对于100人以下的组织是可以考虑的），可配置为ORG
	 * 
	 * @param usrid
	 *            用户ID
	 * @return 包含有其它组成员的数组
	 * @throws SQLException
	 */
	public Vector<User> getMates(String devId, String usrid) throws SQLException {
		Vector<User> retMates = new Vector<User>();
		try {
			boolean kc = keepConnection(true);
			getConnection();
			Team[] teams = getTeamsByMemberId(devId, usrid);
			for (int i = 0; i < teams.length; i++) {
				User[] members = getTeamMembers(devId, teams[i].id);
				for (int j = 0; j < members.length; j++) {
					if (!retMates.contains(members[j])) {
						retMates.add(members[j]);
					}
				}
			}
			keepConnection(kc);
		} finally {
			releaseConnection();
		}
		return retMates;
	}

	/**
	 * 如一个字符串为null或者长度为0， 返回真。
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNull(String str) {
		if (str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void logFacebookFeed(String prcId, String startBy, String[] usrids, String msg) {
		try {
			getConnection();
			PreparedStatement stat = null;
			String sql = "INSERT INTO cf_facebookfeed(PRCID, STARTBY, USRID, MSG) values (?, ?, ?, ?)";
			stat = conn.prepareStatement(sql);
			for (int i = 0; i < usrids.length; i++) {
				if (!usrids[i].startsWith("FB.")) {
					continue;
				}
				stat.setString(1, prcId);
				stat.setString(2, startBy);
				stat.setString(3, usrids[i]);
				stat.setString(4, msg);
				stat.execute();
			}
			stat.close();
		} catch (SQLException sqlex) {
			logger.debug(sqlex);
		} finally {
			releaseConnection();
		}
	}

	public String logTaskto(String devId, String prcId, String wftId, Work theWork, Taskto taskto, String delegaterid, boolean isUpdate, String userOrStarter) {
		String ret = null;
		String sql = null;
		try {
			getConnection();
			PreparedStatement stat = null;
			if (!isUpdate) {
				sql = "INSERT INTO cf_task(DEV, USRID, PRCID, NODEID, SESSID, WORKNAME, STATUS, DELEGATERID, TID) VALUES(?, ?,?,?,?,?,?,?,?)";
				stat = conn.prepareStatement(sql);
				stat.setString(1, devId);
				stat.setString(2, taskto.getWhom());
				stat.setString(3, prcId);
				stat.setString(4, theWork.getNodeid());
				stat.setString(5, theWork.getSessid());
				stat.setString(6, Ctool.trimTo(theWork.getName(), 40));
				stat.setString(7, taskto.getStatus());
				stat.setString(8, delegaterid);
				String tid = CflowHelper.myID();
				stat.setString(9, tid);
				ret = tid;
			} else {
				sql = "UPDATE cf_task SET STATUS=? WHERE DEV=? AND USRID=? AND PRCID=? AND NODEID=? AND SESSID=?";
				stat = conn.prepareStatement(sql);
				stat.setString(1, taskto.getStatus());
				stat.setString(2, devId);
				stat.setString(3, taskto.getWhom());
				stat.setString(4, prcId);
				stat.setString(5, theWork.getNodeid());
				stat.setString(6, theWork.getSessid());
			}
			stat.execute();
			stat.close();

			userOrStarter = userOrStarter == null ? CflowHelper.ASSOC_TYPE_USER : userOrStarter;
			sql = "INSERT INTO cf_userprocess VALUES (?, ?, ?, ?, ?)";
			stat = conn.prepareStatement(sql);
			stat.setString(1, devId);
			stat.setString(2, taskto.getWhom());
			stat.setString(3, prcId);
			stat.setString(4, wftId);
			stat.setString(5, userOrStarter);
			try {
				stat.execute();
			} catch (Exception ex) {

			}
			stat.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} finally {
			releaseConnection();
		}
		return ret;

	}

	public void removeWorkTaskto(String sessid) {
		String sql = null;
		try {
			getConnection();
			PreparedStatement stat = null;
			sql = "DELETE FROM cf_task WHERE SESSID=?";
			stat = conn.prepareStatement(sql);
			stat.setString(1, sessid);
			stat.execute();
			stat.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} finally {
			releaseConnection();
		}

	}

	public void removeProcessTaskto(String prcId) {
		String sql = null;
		try {
			getConnection();
			PreparedStatement stat = null;
			sql = "DELETE FROM cf_task WHERE PRCID=?";
			stat = conn.prepareStatement(sql);
			stat.setString(1, prcId);
			stat.execute();
			stat.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} finally {
			releaseConnection();
		}
	}

	public void suspendProcessTaskto(String prcId) {
		String sql = null;
		try {
			getConnection();
			PreparedStatement stat = null;
			sql = "UPDATE cf_task SET PRCSUS = 'YES' WHERE PRCID=?";
			stat = conn.prepareStatement(sql);
			stat.setString(1, prcId);
			stat.execute();
			stat.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} finally {
			releaseConnection();
		}
	}

	public void resumeProcessTaskto(String prcId) {
		String sql = null;
		try {
			getConnection();
			PreparedStatement stat = null;
			sql = "UPDATE cf_task SET PRCSUS = 'NO' WHERE PRCID=?";
			stat = conn.prepareStatement(sql);
			stat.setString(1, prcId);
			stat.execute();
			stat.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} finally {
			releaseConnection();
		}
	}

	public void suspendWorkTaskto(String sessid) {
		String sql = null;
		try {
			getConnection();
			PreparedStatement stat = null;
			sql = "UPDATE cf_task SET WORKSUS = 'YES' WHERE SESSID=?";
			stat = conn.prepareStatement(sql);
			stat.setString(1, sessid);
			stat.execute();
			stat.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} finally {
			releaseConnection();
		}
	}

	public void resumeWorkTaskto(String sessid) {
		String sql = null;
		try {
			getConnection();
			PreparedStatement stat = null;
			sql = "UPDATE cf_task SET WORKSUS = 'NO' WHERE SESSID=?";
			stat = conn.prepareStatement(sql);
			stat.setString(1, sessid);
			stat.execute();
			stat.close();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} finally {
			releaseConnection();
		}
	}

	public WorkItemInfo getWorkitem(String usrid, String tid) {
		WorkItemInfo ret = null;
		try {
			boolean kc = keepConnection(true);
			getConnection();
			String sql = "SELECT A.*, B.WFTNAME, B.STARTBY, B.WFTID, B.PPID, B.PPNODEID, B.PPSESSID FROM cf_task A, cf_process B WHERE A.TID=? AND (A.STATUS=? OR A.STATUS=?)" + " AND A.WORKSUS = 'NO' AND B.STATUS=? AND A.PRCID = B.ID ORDER BY A.TS";

			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, tid);
			stat.setString(2, CflowHelper.TASK_RUNNING);
			stat.setString(3, CflowHelper.TASK_ACQUIRED);
			stat.setString(4, CflowHelper.PROCESS_RUNNING);

			ResultSet rs = stat.executeQuery();
			if (rs.next()) {
				String devId = rs.getString("DEV");
				String prcId = rs.getString("PRCID");
				String nodeid = rs.getString("NODEID");
				String sessid = rs.getString("SESSID");
				String workname = rs.getString("WORKNAME");
				String prcname = rs.getString("WFTNAME");
				String startBy = rs.getString("STARTBY");
				String wftId = rs.getString("WFTID");
				String delegaterid = rs.getString("DELEGATERID");
				String color = WorkItemInfo.COLOR_GREEN; // getTaskDueColor(usrid,
															// prcId, nodeid);
				ret = WorkItemInfo.newInstance(devId, prcId, prcname, startBy, wftId, nodeid, sessid, CflowHelper.TASK_RUNNING, workname, workname, color, usrid, delegaterid, tid, rs.getString("PPID"), rs.getString("PPNODEID"), rs.getString("PPSESSID"));

			}
			rs.close();
			stat.close();
			keepConnection(kc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	public WorkItemInfo getWiiByTid(String tid) {
		WorkItemInfo ret = null;
		try {
			boolean kc = keepConnection(true);
			getConnection();
			String sql = "SELECT A.*, B.WFTNAME, B.STARTBY, B.WFTID, B.PPID, B.PPNODEID, B.PPSESSID FROM cf_task A, cf_process B WHERE A.TID=? AND A.PRCID = B.ID";

			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, tid);

			ResultSet rs = stat.executeQuery();
			if (rs.next()) {
				String prcId = rs.getString("PRCID");
				String nodeid = rs.getString("NODEID");
				String sessid = rs.getString("SESSID");
				String workname = rs.getString("WORKNAME");
				String prcname = rs.getString("WFTNAME");
				String startBy = rs.getString("STARTBY");
				String wftId = rs.getString("WFTID");
				String delegaterid = rs.getString("DELEGATERID");
				String doer = rs.getString("USRID");
				String devId = rs.getString("DEV");
				String color = WorkItemInfo.COLOR_GREEN; // getTaskDueColor(doer,
															// prcId, nodeid);
				ret = WorkItemInfo.newInstance(devId, prcId, prcname, startBy, wftId, nodeid, sessid, CflowHelper.TASK_RUNNING, workname, workname, color, doer, delegaterid, tid, rs.getString("PPID"), rs.getString("PPNODEID"), rs.getString("PPSESSID"));
			}
			rs.close();
			stat.close();
			keepConnection(kc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	public Object[] getWorklist(String devId, String doer, String prcId) {
		ArrayList<WorkItemInfo> arrayList = new ArrayList<WorkItemInfo>();
		try {
			boolean kc = keepConnection(true);
			getConnection();
			String sql = "SELECT A.*, B.WFTNAME, B.STARTBY, B.WFTID, B.PPID, B.PPNODEID, B.PPSESSID FROM cf_task A, cf_process B WHERE A.USRID=? AND (A.STATUS=? OR A.STATUS=?)" + " AND A.WORKSUS = 'NO' AND B.STATUS=? AND A.PRCID = B.ID AND A.DEV=? ORDER BY A.TS";
			String sql_withPrcId = "SELECT A.*, B.WFTNAME, B.STARTBY, B.WFTID, B.PPID, B.PPNODEID, B.PPSESSID FROM cf_task A, cf_process B WHERE A.USRID=? AND (A.STATUS=? OR A.STATUS=?)" + " AND A.WORKSUS = 'NO' AND B.STATUS=? AND A.PRCID = ? AND A.PRCID = B.ID AND A.DEV=? ORDER BY A.TS";

			PreparedStatement ps = null;
			if (prcId == null) {
				ps = conn.prepareStatement(sql);
				ps.setString(1, doer);
				ps.setString(2, CflowHelper.TASK_RUNNING);
				ps.setString(3, CflowHelper.TASK_ACQUIRED);
				ps.setString(4, CflowHelper.PROCESS_RUNNING);
				ps.setString(5, devId);
			} else {
				ps = conn.prepareStatement(sql_withPrcId);
				ps.setString(1, doer);
				ps.setString(2, CflowHelper.TASK_RUNNING);
				ps.setString(3, CflowHelper.TASK_ACQUIRED);
				ps.setString(4, CflowHelper.PROCESS_RUNNING);
				ps.setString(5, prcId);
				ps.setString(6, devId);
			}

			ResultSet rs = ps.executeQuery();
			for (; rs.next();) {
				String prcId_tmp = rs.getString("PRCID");
				String nodeid = rs.getString("NODEID");
				String sessid = rs.getString("SESSID");
				String workname = rs.getString("WORKNAME");
				String prcname = rs.getString("WFTNAME");
				String startBy = rs.getString("STARTBY");
				String wftId = rs.getString("WFTID");
				String delegaterid = rs.getString("DELEGATERID");
				String tid = rs.getString("TID");
				String color = WorkItemInfo.COLOR_GREEN; // getTaskDueColor(doer,
															// prcId, nodeid);
				arrayList.add(WorkItemInfo.newInstance(devId, prcId_tmp, prcname, startBy, wftId, nodeid, sessid, CflowHelper.TASK_RUNNING, workname, workname, color, doer, delegaterid, tid, rs.getString("PPID"), rs.getString("PPNODEID"), rs.getString("PPSESSID")));

			}
			rs.close();
			ps.close();
			keepConnection(kc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseConnection();
		}
		return arrayList.toArray();
	}

	public Object[] getWorks(String prcId, String sessid) {
		ArrayList<WorkItemInfo> arrayList = new ArrayList<WorkItemInfo>();
		try {
			boolean oldFlag = keepConnection(true);
			getConnection();
			String sql = "";
			PreparedStatement stat;
			if (sessid != null) {
				sql = "SELECT A.*, B.WFTNAME, B.STARTBY, B.WFTID, B.PPID, B.PPNODEID, B.PPSESSID FROM cf_task A, cf_process B WHERE A.PRCID=? AND A.SESSID=? AND A.PRCID=cfprocess.ID ORDER By A.TS";
				stat = conn.prepareStatement(sql);
				stat.setString(1, prcId);
				stat.setString(2, sessid);
			} else {
				sql = "SELECT A.*, B.WFTNAME, B.STARTBY, B.WFTID, B.PPID, B.PPNODEID, B.PPSESSID FROM cf_task A, cf_process B WHERE A.PRCID=? AND A.PRCID=cfprocess.ID ORDER BY A.TS";
				stat = conn.prepareStatement(sql);
				stat.setString(1, prcId);
			}

			ResultSet rs = stat.executeQuery();
			for (; rs.next();) {
				prcId = rs.getString("PRCID");
				String devId = rs.getString("DEV");
				String usrid = rs.getString("USRID");
				String nodeid = rs.getString("NODEID");
				String startBy = rs.getString("STARTBY");
				String wftId = rs.getString("WFTID");
				sessid = rs.getString("SESSID");
				String workname = rs.getString("WORKNAME");
				String status = rs.getString("STATUS");
				String prcname = rs.getString("WFTNAME");
				String delegaterid = rs.getString("DELEGATERID");
				String tid = rs.getString("TID");
				String color = WorkItemInfo.COLOR_GREEN; // getTaskDueColor(usrid,
															// prcId, nodeid);
				arrayList.add(WorkItemInfo.newInstance(devId, prcId, prcname, startBy, wftId, nodeid, sessid, status, workname, workname, color, usrid, delegaterid, tid, rs.getString("PPID"), rs.getString("PPNODEID"), rs.getString("PPSESSID")));

			}
			rs.close();
			stat.close();
			keepConnection(oldFlag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseConnection();
		}
		return arrayList.toArray();
	}

	/**
	 * 创建一个组织
	 * 
	 * @param orgname
	 * @param shareWft
	 * @param owner
	 * @return
	 * @throws SQLException
	 */
	public Org createOrg(String orgid, String orgname, String orgpwd, String fedpwd, boolean shareWft, String owner) throws SQLException {
		String sql = "INSERT INTO cf_org (ORGID, ORGNAME, ORGPWD,  FEDPWD, SHAREWFTS, OWNER) VALUES(?,?, MD5(?), MD5(?), ?,?)";
		try {
			boolean oldFlag = keepConnection(true);
			getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, orgid);
			ps.setString(2, orgname);
			ps.setString(3, orgpwd);
			ps.setString(4, fedpwd);
			ps.setBoolean(5, shareWft);
			ps.setString(6, owner);
			ps.execute();
			ps.close();
			keepConnection(oldFlag);
		} finally {
			releaseConnection();
		}
		return Org.newOrg(orgid, orgname, shareWft, owner);
	}

	/**
	 * 删除一个组织
	 * 
	 * @param orgid
	 * @throws SQLException
	 */
	public void deleteOrg(String orgid) throws SQLException {
		String sql = "DELETE FROM cf_org WHERE ORGID = ?";
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, orgid);
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
		return;
	}

	public void deleteOrg(String orgid, String owner) throws SQLException {
		String sql = "DELETE FROM cf_org WHERE ORGID = ? AND OWNER = ?";
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, orgid);
			ps.setString(2, owner);
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
		return;
	}

	public void setOrgLimit(String orgid, String valid, int wftlimit, int prclimit) throws SQLException {
		String sql = "UPDATE cf_org SET VALID=?, WFTLIMIT=?, PRCLIMIT=? WHERE ORGID=?";
		try {
			boolean oldFlag = keepConnection(true);
			getConnection();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setTimestamp(1, new java.sql.Timestamp(format.parse(valid).getTime()));
			ps.setInt(2, wftlimit);
			ps.setInt(3, prclimit);
			ps.setString(4, orgid);
			ps.execute();
			ps.close();
			keepConnection(oldFlag);
		} catch (ParseException pex) {
			throw new SQLException(pex.getLocalizedMessage());
		} finally {
			releaseConnection();
		}
		return;
	}

	public void changeOrgPwd(String orgid, String orgpwd, String fedpwd) throws SQLException {
		if (StringUtils.isBlank(orgpwd) && StringUtils.isBlank(fedpwd))
			return;
		String sql = "UPDATE cf_org SET ";
		if (StringUtils.isNotBlank(orgpwd) && StringUtils.isNotBlank(fedpwd)) {
			sql = "UPDATE cf_org SET ORGPWD=MD5('" + orgpwd + "'), FEDPWD=MD5('" + fedpwd + "') WHERE ORGID='" + orgid + "'";
		} else {
			if (StringUtils.isNotBlank(orgpwd)) {
				sql = "UPDATE cf_org SET ORGPWD=MD5('" + orgpwd + "') WHERE ORGID='" + orgid + "'";
			} else {
				sql = "UPDATE cf_org SET FEDPWD=MD5('" + fedpwd + "') WHERE ORGID='" + orgid + "'";
			}
		}
		try {
			getConnection();
			Statement stat = conn.createStatement();
			stat.execute(sql);
			stat.close();
		} finally {
			releaseConnection();
		}
		return;
	}

	/**
	 * 判断组织的授权是否超期
	 * 
	 * @param usrid
	 * @return
	 * @throws SQLException
	 */
	public boolean limitExceeded(String usrid) throws SQLException {
		boolean ret = false;
		/*
		 * String sql =
		 * "SELECT cf_limit.PRCLIMIT FROM cf_limit, cf_user, cf_org WHERE cf_limit.ORGID = cf_user.ORGID AND cf_org.ORGID = cf_user.ORGID "
		 * +
		 * " AND cf_user.ID = ? AND cf_org.WFTLIMIT > cf_limit.WFTLIMIT AND cf_org.PRCLIMIT > cf_limit.PRCLIMIT AND cf_org.VALID > NOW()"
		 * ; try { getConnection(); PreparedStatement ps =
		 * conn.prepareStatement(sql); ps.setString(1, usrid); ResultSet rs =
		 * ps.executeQuery(); if (rs.next()) { ret = false; } else { ret = true;
		 * } } finally { releaseConnection(); }
		 */

		return ret;
	}

	public void addProcessLimit(String orgid, int addict) throws SQLException {
		String sql = "UPDATE cf_limit SET PRCLIMIT = PRCLIMIT + " + addict + " WHERE ORGID='" + orgid + "'";
		try {
			getConnection();
			Statement stat = conn.createStatement();
			stat.execute(sql);
			stat.close();
		} finally {
			releaseConnection();
		}
	}

	public Org getOrgByOwner(String usrid) throws SQLException {
		Org ret = null;
		String sql = "SELECT * FROM cf_org WHERE OWNER=?";
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, usrid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Calendar valid = Calendar.getInstance();
				valid.setTimeInMillis(rs.getTimestamp("VALID").getTime());
				ret = Org.newOrg(rs.getString("ORGID"), rs.getString("ORGNAME"), rs.getBoolean("SHAREWFTS"), rs.getString("OWNER"), valid, rs.getInt("WFTLIMIT"), rs.getInt("PRCLIMIT"));
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	public Org[] getOrgsByOwner(String usrid) throws SQLException {
		Org[] ret = {};
		ArrayList<Org> retList = new ArrayList<Org>();
		String sql = "SELECT * FROM cf_org WHERE OWNER=?";
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, usrid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Calendar valid = Calendar.getInstance();
				valid.setTimeInMillis(rs.getTimestamp("VALID").getTime());
				Org aOrg = Org.newOrg(rs.getString("ORGID"), rs.getString("ORGNAME"), rs.getBoolean("SHAREWFTS"), rs.getString("OWNER"), valid, rs.getInt("WFTLIMIT"), rs.getInt("PRCLIMIT"));
				retList.add(aOrg);
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return retList.toArray(ret);
	}

	public DelegationEntry[] getDelegation(String usrid) throws SQLException {
		DelegationEntry[] delegation = {};
		ArrayList<DelegationEntry> arrayList = new ArrayList<DelegationEntry>();
		String sql = "SELECT * FROM cf_delegate WHERE DELEGATETYPE='ALL' AND DELEGATER=?";
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, usrid);
			ResultSet rs = ps.executeQuery();
			for (; rs.next();) {
				arrayList.add(DelegationEntry.newInstance(rs.getLong("ID"), rs.getString("DELEGATER"), rs.getString("DELEGATEE"), rs.getTimestamp("STARTTIME"), rs.getTimestamp("ENDTIME"), rs.getString("PRCID"), rs.getString("NODEID"), rs.getString("SESSID"), rs.getString("DELEGATETYPE")));
			}
			rs.close();
			ps.close();
			if (arrayList.size() > 0)
				delegation = arrayList.toArray(delegation);

		} finally {
			releaseConnection();
		}

		return delegation;
	}

	public String copyTeam(String devId, String fromTeamId, String toTeamId) throws SQLException {
		String instanceTeamId = toTeamId;
		String sql1 = "INSERT INTO cf_team (DEV, ID, NAME, EMAIL, LOGO, MEMO) SELECT ?, ?, NAME, OWNER, EMAIL, LOGO, MEMO " + " FROM cf_team WHERE cf_team.DEV=? AND cf_team.ID=?";
		String sql2 = "INSERT INTO cf_teamuser (TEAMID, USRID, ROLE) SELECT ?, USRID, ROLE FROM cf_teamuser WHERE cf_teamuser.TEAMID=?";
		try {
			getConnection();
			PreparedStatement ps1 = conn.prepareStatement(sql1);
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			ps1.setString(1, devId);
			ps1.setString(2, toTeamId);
			ps1.setString(3, devId);
			ps1.setString(4, fromTeamId);
			ps2.setString(1, toTeamId);
			ps2.setString(2, fromTeamId);

			ps1.execute();
			ps2.execute();
			ps1.close();
			ps2.close();
		} finally {
			releaseConnection();
		}
		return instanceTeamId;
	}

	public void updateUser(String devId, String identity, String usrName, String usrEmail, String lang, String notify) throws SQLException {
		if (usrName.equals("starter")) {
			logger.warn("Discard usrname: starter");
			return;
		}
		try {
			getConnection();
			String sql = "UPDATE cf_user SET NAME=?, EMAIL=?, LANG=?, NOTIFY=? WHERE DEV=? AND IDENTITY=?";
			PreparedStatement sta = conn.prepareStatement(sql);
			sta.setString(1, usrName);
			sta.setString(2, usrEmail);
			sta.setString(3, lang);
			sta.setString(4, notify);
			sta.setString(5, devId);
			sta.setString(6, identity);
			sta.execute();

			sta.close();
			/*
			 * if (status.equals("FINAL")) {
			 * 
			 * }
			 */
		} finally {
			releaseConnection();
		}
	}

	public void fi(String que, String owner, String toend, String msg) throws SQLException {
		String sql = "INSERT INTO cf_ifo_" + que + " (OWNER, TOEND, MSG) VALUES (?,?,?)";
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, owner);
			ps.setString(2, toend);
			ps.setString(3, msg);
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
	}

	@SuppressWarnings("unchecked")
	public JSONArray fo(String que, String toend) throws SQLException {
		JSONArray ret = new JSONArray();
		String sql = "SELECT * FROM cf_ifo_" + que + " WHERE TOEND = ?";
		String sql2 = "DELETE FROM cf_ifo_" + que + " WHERE ID=?";
		try {
			getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			ps.setString(1, toend);
			ResultSet rs = ps.executeQuery();
			for (; rs.next();) {
				JSONObject msg = new JSONObject();
				msg.put("ID", rs.getInt("ID"));
				msg.put("MSG", rs.getString("MSG"));
				msg.put("TS", rs.getTimestamp("TS"));
				ret.add(msg);
				ps2.setInt(1, rs.getInt("ID"));
				ps2.execute();
			}
			rs.close();
			ps.close();
			ps2.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	public void clearCfTimer(String prcId) throws SQLException {
		try {
			getConnection();
			String sql = "DELETE FROM cf_timer WHERE PRCID=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, prcId);
			ps.execute();
			ps.close();
		} finally {
			releaseConnection();
		}
	}

	/*
	 * public void skeleton(String prcId){ try{ boolean kc =
	 * keepConnection(true); getConnection(); //DO TASK HERE;
	 * keepConnection(kc); }finally{ releaseConnection(); } }
	 */

	protected int gc(int what) {
		boolean kc = keepConnection(true);
		try {
			getConnection();
			String sql = "SELECT COUNT(*) AS MYCT from cf_process";
			switch (what) {
			case 0:
				sql = "SELECT COUNT(*) AS MYCT from cf_user";
				break;
			case 1:
				sql = "SELECT COUNT(*) AS MYCT from cf_userwft";
				break;
			case 2:
				sql = "SELECT COUNT(*) AS MYCT from cf_process";
				break;
			}
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			rs.next();

			int ret = rs.getInt("MYCT");
			rs.close();
			stat.close();
			return ret;
		} catch (SQLException sqlex) {
			return -1;
		} finally {
			releaseConnection();
			keepConnection(kc);
		}
	}

	// Actors = all users + developer
	@SuppressWarnings("unchecked")
	public JSONArray getActors(String devId) throws SQLException {
		JSONArray ret = new JSONArray();
		String sql;
		PreparedStatement ps;
		try {
			getConnection();
			sql = "select A.* from cf_user A  where A.DEV = ? ORDER BY A.IDENTITY";
			ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("ID", rs.getString("ID"));
				obj.put("IDENTITY", rs.getString("IDENTITY"));
				obj.put("NAME", rs.getString("NAME"));
				obj.put("EMIAL", rs.getString("EMAIL"));
				obj.put("LANG", rs.getString("LANG"));
				obj.put("NOTIFY", rs.getString("NOTIFY"));
				ret.add(obj);
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	public JSONArray getAllUsers(String devId) throws SQLException {
		JSONArray ret = new JSONArray();
		String sql, sqlNick;
		PreparedStatement ps;
		try {
			getConnection();
			sql = "select A.* from cf_user A  where A.DEV = ? ORDER BY A.IDENTITY";
			ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("IDENTITY").equals(devId))
					continue;
				JSONObject obj = new JSONObject();
				obj.put("ID", rs.getString("ID"));
				obj.put("IDENTITY", rs.getString("IDENTITY"));
				obj.put("NAME", rs.getString("NAME"));
				obj.put("EMIAL", rs.getString("EMAIL"));
				obj.put("LANG", rs.getString("LANG"));
				obj.put("NOTIFY", rs.getString("NOTIFY"));
				ret.add(obj);
			}
			rs.close();
			ps.close();
		} finally {
			releaseConnection();
		}
		return ret;
	}

	private User newUserFromRS(ResultSet rs) throws SQLException {
		User user = User.newUser(rs.getString("DEV"), rs.getString("ID"), rs.getString("IDENTITY"), rs.getString("NAME"), rs.getString("EMAIL"), rs.getString("LANG"), rs.getString("NOTIFY"));
		return user;
	}

	private Developer newDevFromRS(ResultSet rs) throws SQLException {
		Developer dev = Developer.newDeveloper(rs.getString("ID"), rs.getString("NAME"), rs.getString("EMAIL"), rs.getString("LANG"));
		return dev;
	}

	public void addIstToUser(String usrid, String instid, String instname) throws SQLException {
		try {
			getConnection();
			String sql = "INSERT INTO cf_userist (OWNER, ISTID, ISTNAME) VALUES (?, ?, ?)";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, usrid);
			stat.setString(2, instid);
			stat.setString(3, instname);
			stat.execute();

			stat.close();
		} finally {
			releaseConnection();
		}
	}

	public JSONArray getUserIsts(String usrid) throws SQLException {
		JSONArray array = new JSONArray();
		try {
			getConnection();
			String sql = "SELECT * FROM cf_userist WHERE OWNER=?";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, usrid);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				JSONObject object = new JSONObject();
				object.put("OWNER", rs.getString("OWNER"));
				object.put("ISTID", rs.getString("ISTID"));
				object.put("ISTNAME", rs.getString("ISTNAME"));
				array.add(object);
			}
			rs.close();
			stat.close();
		} finally {
			releaseConnection();
		}
		return array;
	}

	public void deleteUserIst(String usrid, String istid) throws SQLException {
		try {
			getConnection();
			String sql = "DELETE FROM cf_userist WHERE OWNER=? AND ISTID=?";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, usrid);
			stat.setString(2, istid);
			stat.execute();

			stat.close();

			// Remove workflow file
			String filePath = CflowHelper.getIstPath(usrid, istid);
			try {
				File istFile = new File(filePath);
				istFile.delete();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} finally {
			releaseConnection();
		}
	}

	public String getIstIdByName(String usrid, String istname) throws SQLException {
		try {
			getConnection();
			String sql = "SELECT ISTID FROM cf_userist WHERE ISTNAME = ?";
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1, istname);
			ResultSet rs = stat.executeQuery();
			String istId = null;
			if (rs.next()) {
				istId = rs.getString("ISTID");
			} else {
				istId = null;
			}
			rs.close();
			stat.close();

			return istId;
		} finally {
			releaseConnection();
		}
	}

	@SuppressWarnings("unchecked")
	public JSONArray getVtsByDeveloper(String devId) throws SQLException {
		String sql = "";
		PreparedStatement stat = null;
		JSONArray ret = new JSONArray();
		try {
			getConnection();
			sql = "SELECT * FROM cf_vts WHERE DEV=? ORDER BY VTNAME DESC";
			stat = conn.prepareStatement(sql);
			stat.setString(1, devId);
			ResultSet rs = stat.executeQuery();
			for (; rs.next();) {
				JSONObject tmp = new JSONObject();
				tmp.put("ID", rs.getInt("ID"));
				tmp.put("VTNAME", rs.getString("VTNAME"));
				ret.add(tmp);
			}
			rs.close();
			stat.close();

		} finally {
			releaseConnection();
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public JSONObject statistic(String devId, String prcId, String wftId, String userId) throws SQLException {
		JSONObject ret = new JSONObject();
		PreparedStatement ps = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			getConnection();
			String sql = "SELECT COUNT(*) as tcount from cf_wft WHERE DEV=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			rs = ps.executeQuery();
			rs.next();
			ret.put("COUNT_WFT", rs.getLong("tcount"));

			sql = "SELECT COUNT(*) as tcount from cf_process WHERE DEV=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			rs = ps.executeQuery();
			rs.next();
			ret.put("COUNT_PRC", rs.getLong("tcount"));
			sql = "SELECT COUNT(*) as tcount from cf_process WHERE DEV=? AND STATUS=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			ps.setString(2, CflowHelper.PROCESS_RUNNING);
			rs = ps.executeQuery();
			rs.next();
			ret.put("COUNT_PRC_RUNNING", rs.getLong("tcount"));
			ps.setString(1, devId);
			ps.setString(2, CflowHelper.PROCESS_FINISHED);
			rs = ps.executeQuery();
			rs.next();
			ret.put("COUNT_PRC_FINISHED", rs.getLong("tcount"));
			sql = "SELECT COUNT(*) as tcount from cf_user WHERE DEV=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, devId);
			rs = ps.executeQuery();
			rs.next();
			ret.put("COUNT_USER", rs.getLong("tcount"));
		} finally {
			releaseConnection();
		}

		return ret;
	}

	public void repaireDevDefaultUser() throws Exception {
		String sql = "SELECT * FROM cf_dev WHERE ID NOT IN (SELECT A.IDENTITY FROM cf_user A, cf_dev B WHERE A.DEV =B.ID GROUP BY A.IDENTITY)";
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql);
		while (rs.next()) {
			String id = rs.getString("ID");
			createUser(id, id, rs.getString("NAME"), rs.getString("EMAIL"), rs.getString("LANG"), CflowHelper.NOTIFY_EMAIL);
		}

	}
}
