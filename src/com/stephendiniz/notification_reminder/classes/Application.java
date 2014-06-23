package com.stephendiniz.notification_reminder.classes;

import java.util.Comparator;

import android.graphics.drawable.Drawable;

public class Application implements Comparable<Application> {
	private Drawable icon;
	private int id;
	private String packageName;
	private String applicationName;
	
	public Drawable getIcon()                              { return this.icon;                       }
	public int getId()                                     { return this.id;                         }
	public String getPackageName()                         { return this.packageName;                }
	public String getApplicationName()                     { return this.applicationName;            }
	
	public void setIcon(Drawable icon)                     { this.icon = icon;                       }
	public void setId(int id)                              { this.id = id;                           }
	public void setPackageName(String packageName)         { this.packageName = packageName;         }
	public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
	
	@Override
	public int compareTo(Application another) {
		return this.getApplicationName().compareTo(another.getApplicationName());
	}
	
	static class ApplicationComparator implements Comparator<Application> {
		@Override
		public int compare(Application la, Application ra) {
			return la.getApplicationName().compareTo(ra.getApplicationName());
		}
		
	}
}