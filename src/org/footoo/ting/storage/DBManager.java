package org.footoo.ting.storage;

import java.util.ArrayList;

import org.footoo.ting.entity.MyFavoDbItem;
import org.footoo.ting.entity.PlayHistoryDbItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
	 * 查询所有的播放历史
	 * 
	 * @return
	 */
	public ArrayList<PlayHistoryDbItem> getAllPlayHistory() {
		ArrayList<PlayHistoryDbItem> historyList = new ArrayList<PlayHistoryDbItem>();
		PlayHistoryDbItem historyDbItem = null;
		String sqlStr = "select * from " + DBHelper.PLAY_HISTORY_TABLE_NAME;
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
		String sqlStr = "select * from " + DBHelper.MY_FAVO_TABLE_NAME;
		Cursor cursor = db.rawQuery(sqlStr, null);
		while (cursor.moveToNext()) {
			myFavoDbItem = new MyFavoDbItem();

			myFavoDbItem.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
			myFavoDbItem.setCategory_id(cursor.getInt(cursor
					.getColumnIndex("category_id")));
			myFavoDbItem.setCover_path(cursor.getString(cursor
					.getColumnIndex("cover_path")));
			myFavoDbItem.setSource_desc(cursor.getString(cursor
					.getColumnIndex("source_desc")));
			myFavoDbItem.setSource_id(cursor.getInt(cursor
					.getColumnIndex("source_id")));
			myFavoDbItem.setSource_url(cursor.getString(cursor
					.getColumnIndex("source_url")));

			myFavoList.add(myFavoDbItem);
		}
		return myFavoList;
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
