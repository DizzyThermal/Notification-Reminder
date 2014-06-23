package com.stephendiniz.notification_reminder;

import java.util.ArrayList;

import com.stephendiniz.notification_reminder.classes.ApplicationDataSource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class Service_Notification_Listener extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();

    private NLServiceReceiver nlservicereciver;
    
    private ArrayList<String> sensitiveApps = new ArrayList<String>();
    
    Uri notification;
	Ringtone r;
    
    PowerManager pm;

    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.stephendiniz.notification_reminder.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
        
        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    class NLServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.i(TAG, "Received");
        	getUpdatedSensitiveApps();
            for(StatusBarNotification sbn : Service_Notification_Listener.this.getActiveNotifications()) {
            	if(sensitiveApps.contains(sbn.getPackageName()) && !pm.isScreenOn()) {
            		r.play();
            		break;
            	}
            }
        }
    }
    
    private void getUpdatedSensitiveApps() {
    	ApplicationDataSource dataSource = new ApplicationDataSource(this);
    	dataSource.open();
    	
    	sensitiveApps = dataSource.getPackageNames();
    	
    	dataSource.close();
    }
}