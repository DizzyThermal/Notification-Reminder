package com.stephendiniz.notification_reminder.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stephendiniz.notification_reminder.R;

public class AppAdapter extends BaseAdapter {
    protected List<ResolveInfo> mInstalledAppInfo;
    protected List<Application> mInstalledApps = new LinkedList<Application>();
    private PackageManager pManager;
    private Object systemService;
    private ArrayList<String> packageNames;
    
    private void reloadList() {
    	final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mInstalledApps) {
                    mInstalledApps.clear();
                    for (ResolveInfo info : mInstalledAppInfo) {
                    	
                    	if(packageNames.contains(info.activityInfo.packageName)) {
                    		continue;
                    	}
                    	
                        final Application item = new Application();
                        item.setApplicationName(info.loadLabel(pManager).toString());
                        item.setIcon(info.loadIcon(pManager));
                        item.setPackageName(info.activityInfo.packageName);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                int index = Collections.binarySearch(mInstalledApps, item);
                                if (index < 0) {
                                    index = -index - 1;
                                    mInstalledApps.add(index, item);
                                }
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public AppAdapter(List<ResolveInfo> installedAppsInfo, Context context, ArrayList<String> packageNames) {
        mInstalledAppInfo = installedAppsInfo;
        pManager = context.getPackageManager();
        systemService = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.packageNames = packageNames;
    }

    public void update() {
        reloadList();
    }

    @Override
    public int getCount() {
        return mInstalledApps.size();
    }

    @Override
    public Application getItem(int position) {
        return mInstalledApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mInstalledApps.get(position).getApplicationName().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            final LayoutInflater layoutInflater = (LayoutInflater)systemService;
            convertView = layoutInflater.inflate(R.layout.preference_icon, null, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.title = (TextView)convertView.findViewById(R.id.titleText);
            holder.icon = (ImageView)convertView.findViewById(R.id.icon);
        }
        Application applicationInfo = getItem(position);

        if (holder.title != null) {
            holder.title.setText(applicationInfo.getApplicationName());
        }
        if (holder.icon != null) {
            Drawable loadIcon = applicationInfo.getIcon();
            holder.icon.setImageDrawable(loadIcon);
        }
        return convertView;
    }
}

class ViewHolder {
    TextView title;
    ImageView icon;
}