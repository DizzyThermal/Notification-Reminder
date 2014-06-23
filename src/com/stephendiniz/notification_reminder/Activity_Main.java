package com.stephendiniz.notification_reminder;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.stephendiniz.notification_reminder.classes.AppAdapter;
import com.stephendiniz.notification_reminder.classes.Application;
import com.stephendiniz.notification_reminder.classes.ApplicationDataSource;

public class Activity_Main extends Activity implements OnCheckedChangeListener, OnItemClickListener {

	private String TAG = this.getClass().getSimpleName();
	
	private final int MENU_PERIOD = 0;
	private final int MENU_ADD = 1;
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	private Switch toggle;

	public static String PREF_RUNNING = "PREF_RUNNING";
	public static String PREF_PERIOD = "PREF_PERIOD";

	private ApplicationDataSource dataSource;
	
	private PackageManager pManager;
	
	private AppAdapter appAdapter;
	
	private Application appChosen;
	
	private ListView list;
	
	private Dialog dialog;

	private NumberPicker np;
	
	private Intent notificationService;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        toggle = (Switch)findViewById(R.id.toggle);
        toggle.setOnCheckedChangeListener(this);
    	
    	prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		
		pManager = getPackageManager();
		
		dataSource = new ApplicationDataSource(this);
    	dataSource.open();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();

    	if(prefs.getBoolean(PREF_RUNNING, false) == false) {
    		toggle.setChecked(false);
		}
    	else {
    		toggle.setChecked(true);
    	}
    	
    	refreshApps();
    }

    @Override
    protected void onStop() {
    	super.onStop();

    	dataSource.close();
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	switch(buttonView.getId()) {
	    	case R.id.toggle:
	    		if(notificationService != null) {
	    			this.stopService(notificationService);

	    			editor.putBoolean(PREF_RUNNING, false);
	    			editor.commit();
	    			
	    			notificationService = null;
	    		}
	    		if(toggle.isChecked()) {
	    			notificationService = new Intent(this, Service_Notification_Reminder.class);
	    	    	notificationService.putExtra(PREF_PERIOD, prefs.getInt(PREF_PERIOD, 5));
	    	    	
	    	    	this.startService(notificationService);

	    			editor.putBoolean(PREF_RUNNING, true);
	    			editor.commit();
	    		}
	    	break;
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ADD, MENU_ADD, R.string.add_application).setIcon(R.drawable.ic_menu_add).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(0, MENU_PERIOD, MENU_PERIOD, R.string.edit_period).setIcon(R.drawable.ic_menu_period).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD:
            	list = new ListView(this);
                list.setAdapter(appAdapter);
                list.setOnItemClickListener(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.choose_application));
                builder.setView(list);
                dialog = builder.create();
                dialog.show();

                return true;
            case MENU_PERIOD:
            	dialog = new Dialog(this);
                dialog.setTitle(getResources().getString(R.string.choose_frequency));
                dialog.setContentView(R.layout.period_picker);
                
                Button ok = (Button)dialog.findViewById(R.id.periodOk);
                np = (NumberPicker)dialog.findViewById(R.id.periodPicker);
                np.setMinValue(1);
                np.setMaxValue(900);
                np.setWrapSelectorWheel(false);
                np.setValue(prefs.getInt(PREF_PERIOD, 5));
                ok.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						setPeriod();
						dialog.cancel();
					}
                });
                
                dialog.show();

                return true;
        }
        return false;
    }
    
    public void updateAppChosen(Application appChosen) {
    	this.appChosen = appChosen;
    }
    
    public Application getAppChosen() {
    	return this.appChosen;
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		dataSource.addApplication((Application)parent.getItemAtPosition(position));
		refreshApps();
		dialog.cancel();
	}
	
	private void showLongClickMenu(int pId, String appName) {
		final int id = pId;
		new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.delete))
			.setMessage(getResources().getString(R.string.delete_content) + " \"" + appName + "\"")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dataSource.removeApplication(id);
					refreshApps();
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.show();
	}
	
	private void setPeriod() {
		editor.putInt(PREF_PERIOD, np.getValue());
		editor.commit();
	}
	
	private void refreshApps() {
		Intent i = new Intent(Intent.ACTION_MAIN, null);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		
		// Update Adapter
		appAdapter = new AppAdapter(pManager.queryIntentActivities(i, 0), this, dataSource.getPackageNames());
        appAdapter.update();
        
		// Populate List of Sensitive Applications
    	LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
    	rootLayout.removeAllViews();
    	ArrayList<Application> applications = dataSource.getApplications();
    	
    	// Sort First
    	Collections.sort(applications);
    	
    	// Add Top Divisor
    	View v = new View(this);
    	v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2));
    	v.setBackgroundColor(Color.DKGRAY);
    	rootLayout.addView(v);
    	
    	for(Application pApp : applications) {
    		final Application app = pApp;
    		// Create Layout
    		LinearLayout layout = new LinearLayout(this);
    		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 200));
    		
    		// Grab Icon
    		ImageView ivIcon = null;
    		Drawable icon = null;
    		try {
				icon = getPackageManager().getApplicationIcon(app.getPackageName());
				ivIcon = new ImageView(this);
				ivIcon.setImageDrawable(icon);
			} catch (NameNotFoundException nnfe) {
				Log.e(TAG, "Unable to grab icon from: " + app.getApplicationName(), nnfe);
			}
    		
    		// Create Application Name TextView
    		TextView name = new TextView(this);
    		name.setText(app.getApplicationName());

    		// Add Pieces to View
    		if(ivIcon != null) {
    			ivIcon.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
    			ivIcon.setPadding(0, 0, 20, 0);
    			layout.addView(ivIcon);
    		}
    		name.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 5.0f));
    		name.setPadding(5, 0, 5, 0);
    		name.setTextSize(18f);
    		layout.addView(name);

    		layout.setPadding(35, 15, 35, 15);
    		layout.setGravity(Gravity.CENTER_VERTICAL);
    		
    		layout.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					showLongClickMenu(app.getId(), app.getApplicationName());
					return false;
				}
    		});
    		
    		rootLayout.addView(layout);
    		
    		View div = new View(this);
    		div.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2));
    		div.setBackgroundColor(Color.DKGRAY);
        	rootLayout.addView(div);
    	}
	}
}