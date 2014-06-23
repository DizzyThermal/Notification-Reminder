package com.stephendiniz.notification_reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Service_Notification_Reminder extends Service {

	private String TAG = this.getClass().getSimpleName();

	private Handler handler;
	
	private int period;
	
	private final int NOTIFICATION_ID = 0;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Log.d(TAG, "Service Started");
    	
    	this.period = intent.getIntExtra(Activity_Main.PREF_PERIOD, 5);
    	
    	handler = new Handler();
    	handler.postDelayed(notificationChecker, (period * 60 * 1000));
    	
    	createNotification();
    	
    	return Service.START_STICKY;
	}
	
    Runnable notificationChecker = new Runnable() {
    	@Override
    	public void run() {
    		Intent i = new Intent("com.stephendiniz.notification_reminder.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
    		sendBroadcast(i);
    		Log.i(TAG, "Sent");
    		handler.postDelayed(notificationChecker, (period * 60 * 1000));
    	}
    };
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Remove Queued up Calls
        handler.removeCallbacks(notificationChecker);
        
        // Destroy Notification
     	NotificationManager nManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
     	nManager.cancel(NOTIFICATION_ID);
        
        Log.d(TAG, "Service Stopped");
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void createNotification()
	{
		NotificationManager nManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification;

		// Service started, first notification
		notification = new Notification(R.drawable.notification_icon, getResources().getString(R.string.starting), System.currentTimeMillis());
		notification.setLatestEventInfo(this, getResources().getString(R.string.app_name), getResources().getString(R.string.running), PendingIntent.getActivity(this, 0, new Intent(this, Activity_Main.class), 0));

		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(NOTIFICATION_ID, notification);
		nManager.notify(NOTIFICATION_ID, notification);
	}
}
