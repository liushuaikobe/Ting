package org.footoo.ting.storage;

import java.util.ArrayList;

import org.footoo.ting.entity.MyFavoDbItem;
import org.footoo.ting.entity.PlayHistoryDbItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	// TODO 在这个类里面实现对数据库的操作

	/**
	 * 插入一条播放历史
	 * 
	 * @param oneHistory
	 *            一条播放历史
	 */
	public void insertOneHistory(PlayHistoryDbItem oneHistory) {
		db.beginTransaction();
		try {
			String sqlStr = "insert into " + DBHelper.PLAY_HISTORY_TABLE_NAME
					+ " values(NULL,?,?,?,?)";
			Object[] args = new Object[] { oneHistory.getChapter_url(),
					oneHistory.getSource_name(), oneHistory.getChapter_name(),
					oneHistory.getLast_time() };
			db.execSQL(sqlStr, args);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 删除一条特定的播放历史
	 * 
	 * @param historyId
	 *            要删除的那条播放历史的本地数据库ID
	 */
	public void deleteOneHistory(int historyId) {
		db.beginTransaction();
		try {
			String[] args = new String[] { String.valueOf(historyId) };
			db.delete(DBHelper.PLAY_HISTORY_TABLE_NAME, "_id = ?", args);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 清空所有的播放历史
	 */
	public void clearHistory() {
		db.beginTransaction();
		try {
			db.delete(DBHelper.PLAY_HISTORY_TABLE_NAME, null, null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 查询所有的播放历史
	 * 
	 * @return
	 */
	public ArrayList<PlayHistoryDbItem> getAllPlayHistory() {
		ArrayList<PlayHistoryDbItem> historyList = new ArrayList<PlayHistoryDbItem>();
		PlayHistoryDbItem historyDbItem = null;
		String sqlStr = "select * from " + DBHelper.PLAY_HISTORY_TABLE_NAME
				+ " ORDER BY last_time DESC";
		Cursor cursor = db.rawQuery(sqlStr, null);
		while (cursor.moveToNext()) {
			historyDbItem = new PlayHistoryDbItem();

			historyDbItem.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
			historyDbItem.setChapter_name(cursor.getString(cursor
					.getColumnIndex("chapter_name")));
			historyDbItem.setChapter_url(cursor.getString(cursor
					.getColumnIndex("chapter_url")));
			historyDbItem.setLast_time(cursor.getString(cursor
					.getColumnIndex("last_time")));
			historyDbItem.setSource_name(cursor.getString(cursor
					.getColumnIndex("source_name")));

			historyList.add(historyDbItem);
		}
		return historyList;
	}

	/**
	 * 查询所有我的最爱
	 * 
	 * @return
	 */
	public ArrayList<MyFavoDbItem> getAllMyFavo() {
		ArrayList<MyFavoDbItem> myFavoList = new ArrayList<MyFavoDbItem>();
		MyFavoDbItem myFavoDbItem = null;
		String sqlStr = "select * from " + DBHelper.MY_FAVO_TABLE_NAME
				+ " ORDER BY last_time DESC";
		Cursor cursor = db.rawQuery(sqlStr, null);
		while (cursor.moveToNext()) {
			myFavoDbItem = new MyFavoDbItem();

			myFavoDbItem.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
			myFavoDbItem.setChapter_name(cursor.getString(cursor
					.getColumnIndex("chapter_name")));
			myFavoDbItem.setChapter_url(cursor.getString(cursor
					.getColumnIndex("chapter_url")));
			myFavoDbItem.setLast_time(cursor.getString(cursor
					.getColumnIndex("last_time")));
			myFavoDbItem.setSource_name(cursor.getString(cursor
					.getColumnIndex("source_name")));

			myFavoList.add(myFavoDbItem);
		}
		return myFavoList;
	}

	/**
	 * 向数据库中插入一条我的最爱
	 * 
	 * 
	 * @param myFavo
	 */
	public boolean insertOneMyFavo(MyFavoDbItem myFavo) {
		db.beginTransaction();
		try {
			if (hasBeenMyFavo(myFavo.getChapter_url())) {
				db.setTransactionSuccessful();
				return false;
			}
			String sqlStr = "INSERT INTO " + DBHelper.MY_FAVO_TABLE_NAME
					+ " VALUES(NULL,?,?,?,?)";
			Object[] args = new Object[] { myFavo.getChapter_url(),
					myFavo.getSource_name(), myFavo.getChapter_name(),
					myFavo.getLast_time() };
			db.execSQL(sqlStr, args);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return true;
	}

	/**
	 * 判断一个章节是不是已经在我的收藏中
	 * 
	 * @param myFavo
	 * @return
	 */
	public boolean hasBeenMyFavo(String chapterUrl) {
		String queryStr = "SELECT chapter_url FROM "
				+ DBHelper.MY_FAVO_TABLE_NAME;
		Cursor c = db.rawQuery(queryStr, null);
		String eachUrl;
		while (c.moveToNext()) {
			eachUrl = c.getString(c.getColumnIndex("chapter_url"));
			if (eachUrl.equals(chapterUrl)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 清空所有的我的最爱记录
	 */
	public void clearMyFavo() {
		db.beginTransaction();
		try {
			db.delete(DBHelper.MY_FAVO_TABLE_NAME, null, null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
}
