package com.stephendiniz.notification_reminder.classes;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ApplicationDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_PACKAGE, MySQLiteHelper.COLUMN_NAME };
	
	public ApplicationDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public void addApplication(Application application) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PACKAGE, application.getPackageName());
		values.put(MySQLiteHelper.COLUMN_NAME, application.getApplicationName());
		database.insert(MySQLiteHelper.TABLE_APPLICATIONS, null, values);
	}
	
	public void removeApplication(int id) {
		database.delete(MySQLiteHelper.TABLE_APPLICATIONS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	public ArrayList<Application> getApplications() {
		ArrayList<Application> applications = new ArrayList<Application>();
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_APPLICATIONS, allColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			Application application = new Application();
			application.setId(cursor.getInt(0));
			application.setPackageName(cursor.getString(1));
			application.setApplicationName(cursor.getString(2));
			
			applications.add(application);
			
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return applications;
	}
	
	public ArrayList<String> getPackageNames() {
		ArrayList<String> packageNames = new ArrayList<String>();
		ArrayList<Application> applications = getApplications();
		for(Application app : applications) {
			packageNames.add(app.getPackageName());
		}
		
		return packageNames;
	}
}