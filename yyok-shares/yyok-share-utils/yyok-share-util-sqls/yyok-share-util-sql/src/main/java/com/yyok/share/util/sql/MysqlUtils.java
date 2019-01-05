package com.yyok.share.util.sql;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Hello world!
 *
 */
public abstract class MysqlUtils<T extends Serializable> implements ISyncTask {

	public static void main(String[] args) {
		try {

			int id = 0;
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://rm-bp1mnwmta5778y0d3jo.mysql.rds.aliyuncs.com/devgjj?createDatabaseIfNotExist=true&amp;useSSL=false&zeroDateTimeBehavior=convertToNull";
			String username = "wangluning";
			String password = "EhyQ5RL61NIE74J263M7i";
			
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement state = conn.createStatement();
			ResultSet rs = state.executeQuery("");

			String str = "";

			while (true) {
				if (rs.next()) {
					id = rs.getInt(1);
					str = rs.getString(1) + " " + rs.getString(2) + "\n";
					System.out.print(str);
				} else {
					System.out.println("没有数据了，先冷静3秒钟。");
					Thread.sleep(3000);
					rs.close();
					rs = state.executeQuery("select * from aa where id > " + id + " order by id limit 3");
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 同步时间间隔 可以稍微大点（比真实在定时任务的执行中的间隔大，但是千万别小于他，等于定时任务执行间隔最好）
	private int sync_time_interval_in_milsecond = 10 * 60 * 1000;

	// 事物处理最长时间 建议同步时间间隔大于此时间
	private int tx_time_out_in_milsecond = 5 * 60 * 1000;

	// 上次同步时间
	private Long up_sync_time = null;
	// 本次同步时间
	private Long this_sync_time = null;

	/**
	 * 执行总体架构
	 */
	public final void sync() {

		// 获取同步时间 与主服务器商定同步时间
		long nowSyncTime = getSyncTime();

		// 开始数据同步
		syncDatas(nowSyncTime);

		// 同步数据仅仅解决更新与插入的问题 这里去解决删除的问题
		// 有些表不会存在删除操作，这里对那些不需要删除的表直接跳过
		if (isNeedDel)
			syncDel();

		// 这个放到最后 怕事物回滚 而时间没有被回滚 导致下次同步时，up_sync_time不正确
		updateUpSyncTime();
	}

	// 加synchronized 仅仅是怕jvm优化 导致语句重排 一定要在最后来更新这个时间
	protected synchronized void updateUpSyncTime() {
		up_sync_time = this_sync_time;
	}

	private void syncDel() {

		// 1、本地取全部id集合的摘要 MD5，以及记录数 拿去远程比较，相等则啥都不做
		CommonKVQuery<String, Integer> abstractAndCount = getLocalIdsAbstractAndCount();
		boolean isMach = isMachMasterServer(abstractAndCount);
		if (isMach)
			return;
		// 2、把本地的数据按照id进行分页拿到远程去对比，没有则拿回来进行删除 。
		DefaultPageRequest pageRequest = new DefaultPageRequest();
		pageRequest.setLimit(100);
		pageRequest.setPage(1);
		List<T> ids = null;
		List<T> delIds = null;
		do {
			ids = listLocalIds(pageRequest);
			delIds = getNeedDelIdsFromMasterServer(ids);
			deleteLocalByIds(delIds);
			pageRequest.setPage(pageRequest.getPage() + 1);
		} while (ids.size() == pageRequest.getLimit());
	}

	/**
	 * 删除本地的数据 通过id集合
	 * 
	 * @param delIds
	 */
	protected abstract void deleteLocalByIds(List<T> delIds);

	/**
	 * 去远处匹配 找出需要删除的id集合
	 * 
	 * @param ids
	 * @return
	 */
	protected abstract List<T> getNeedDelIdsFromMasterServer(List ids);

	// 分页获取本地id集合
	protected abstract List<T> listLocalIds(DefaultPageRequest pageRequest);

	// 去主服务器匹配摘要及记录数
	protected abstract boolean isMachMasterServer(CommonKVQuery<String, Integer> abstractAndCount);

	// 获取本地id集合摘要及记录数量
	protected abstract CommonKVQuery<String, Integer> getLocalIdsAbstractAndCount();

	// 同步数据 更新时间在指定的时间之后的数据进行更新
	protected abstract void syncDatas(long nowSyncTime);

	private long getSyncTime() {

		final long masterServerTime = getMasterServiceTime();
		if (up_sync_time == null) {

			up_sync_time = getLocalMaxUpdateTime() - sync_time_interval_in_milsecond;

			// 若上次跟新时间为null，表示系统重启，进行第一次同步，那么来一次全量同步
			// this_sync_time = masterServerTime -
			// sync_time_interval_in_milsecond-tx_time_out_in_milsecond;
			// return 0l;

		}

		// min(主服务器时间 - 同步时间间隔（1小时） - 最大事物超时时间（5分钟）,上次商定的时间 + 同步时间间隔)
		// 这里的5分钟我考虑的是最大事物的用时。就是假定所有事物的时间长度不可以超过5分钟。
		// 因为我们在程序中经常是先设置更新时间，然后插入数据库，然后再做些别的（浪费了一些时间），
		// 最后提交了事物。那么根据mvcc模式，非锁定读，是读快照。导致更新时间本应该在本次同步中被同步的，而并没有同步到
		// （不可见），而下一次的同步时间又大于了这个更新时间。导致会丢失更新。所以每次同步，都多同步5分钟的数据。
		// 就怕丢下这种间隙中的数据。
		this_sync_time = Math.min(up_sync_time + sync_time_interval_in_milsecond,
				masterServerTime - sync_time_interval_in_milsecond - tx_time_out_in_milsecond);
		final long result = Math.max(0, this_sync_time);
		// 这里的这一次同步时间取值是 主服务器时间-同步时间间隔-事物最大超时时间
		// 而舍弃了up_sync_time + sync_time_interval_in_milsecond
		// 这个取值，原因在于让下一次的更新跟上主服务器的时间，不要距离太远
		this_sync_time = masterServerTime - sync_time_interval_in_milsecond - tx_time_out_in_milsecond;
		return result;
	}

	/**
	 * 获取本地记录中的最大更新时间
	 * 
	 * @return
	 */
	protected abstract long getLocalMaxUpdateTime();

	// 获取主服务器的当前时间
	protected abstract long getMasterServiceTime();

	/**
	 * 表数据是否需要删除操作，不会删除，则可以减少去同步被删的数据
	 */
	private boolean isNeedDel = false;

	public void setSync_time_interval_in_milsecond(int sync_time_interval_in_milsecond) {
		this.sync_time_interval_in_milsecond = sync_time_interval_in_milsecond;
	}

	public void setTx_time_out_in_milsecond(int tx_time_out_in_milsecond) {
		this.tx_time_out_in_milsecond = tx_time_out_in_milsecond;
	}

	public void setNeedDel(boolean needDel) {
		isNeedDel = needDel;
	}

}
