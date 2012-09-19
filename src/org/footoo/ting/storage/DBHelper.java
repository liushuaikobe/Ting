package org.footoo.ting.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "local_source.db";
	private static final int DB_VERSION = 1;

	public static final String MY_FAVO_TABLE_NAME = "my_favo";
	public static final String PLAY_HISTORY_TABLE_NAME = "play_history";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS my_favo "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "chapter_url VARCHAR, " + "source_name VARCHAR,"
				+ "chapter_name VARCHAR," + "last_time VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS play_history "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "chapter_url VARCHAR," + "source_name VARCHAR,"
				+ "chapter_name VARCHAR," + "last_time VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
