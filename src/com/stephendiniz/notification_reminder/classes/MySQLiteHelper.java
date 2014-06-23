package com.stephendiniz.notification_reminder.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_APPLICATIONS = "applications";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_PACKAGE = "package";
	public static final String COLUMN_NAME = "name";
	
	private static final String DATABASE_NAME = "applications.db";
	private static final int DATABASE_VERSION = 1;
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String SQL = "CREATE TABLE " + TABLE_APPLICATIONS + "(" + COLUMN_ID + " integer primary key autoincrement, "
					+ COLUMN_PACKAGE +  " text not null, "
					+ COLUMN_NAME + " text not null);";
		database.execSQL(SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS" + TABLE_APPLICATIONS);
	    onCreate(database);
	  }

	} 