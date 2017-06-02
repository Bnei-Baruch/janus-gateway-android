package computician.janusclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import computician.janusclient.LanguageSeletedListener;
//import computician.janusclient.MyApplication;
//import computician.janusclient.StreamListActivity;

//import com.apphance.android.Log;
//import com.google.analytics.tracking.android.EasyTracker;

public class CommonUtils {

	
	public final static ArrayList<String> languages = new ArrayList<String>(Arrays.asList("English","Español","Français","Pycckий","Italiano","Duetsch","עברית","Türkçe"));
	public final static ArrayList<String> langs = new ArrayList<String>(Arrays.asList("eng","spa","fre","rus","ita","ger","heb","trk"));
	 
	public static int FROM_WIDGET = 10;
	public static void RemoveOldPlugin(final Context ct)
	{
		 boolean isInstalled = false;
		    List<ApplicationInfo> packages;
	        PackageManager pm;
	            pm = ct.getPackageManager();        
	            packages = pm.getInstalledApplications(0);
	            for (ApplicationInfo packageInfo : packages) {
	        if(packageInfo.packageName.equals("io.vov.vitamio")) isInstalled =  true;
	        }        
	            
	   

		   if(isInstalled)
		   {
		    AlertDialog chooseToInstall = new AlertDialog.Builder(ct).create();
			chooseToInstall.setTitle("Remove old plug-in");
			chooseToInstall.setMessage("Plugin was installed previously with this app, it is recommneded to uninstall it this plugin in case you do not use it by other app");
			
			chooseToInstall.setButton("Ok", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				   Uri packageURI = Uri.parse("package:io.vov.vitamio");
				    Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
				    ct.startActivity(uninstallIntent);
				    
				   
				 
		    		
			   }
			});
			chooseToInstall.setButton2("Cancel", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      // here you can add functions
				  
			   }
			});
			chooseToInstall.setIcon(R.drawable.icon);
			chooseToInstall.show();
			
		   }
	}
	
	public static boolean checkConnectivity(Context context)
	{
		Dialog blockApp;
		boolean state;
		if(!(state = isOnline(context)))
			return false;
		return true;
	}
	 public static boolean isOnline(Context context) { 
		    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);    
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();    
		    return netInfo != null && netInfo.isConnected();
		}
	 
	 
	 public static void ShowLanguageSelection(Context context,final LanguageSeletedListener listener)
	 {
	 	
	 	AlertDialog.Builder builder = new AlertDialog.Builder(context);
	 	builder.setTitle(R.string.language).setItems((languages.toArray(new CharSequence[languages.size()])), new DialogInterface.OnClickListener() {
	 		
	 		@Override
	 		public void onClick(DialogInterface dialog, int which) {
	 			// TODO Auto-generated method stub
	 			listener.onSelectLanguage(languages.get(which));
	 		}
	 	}).show();
	 	
	 	
	 }



		public static Map<String,String> getHashMapResource(Context c, int hashMapResId) {
			Map<String,String> map = null;
			XmlResourceParser parser = c.getResources().getXml(hashMapResId);

			String key = null, value = null;

			try {
				int eventType = parser.getEventType();

				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_DOCUMENT) {
						Log.d("utils", "Start document");
					} else if (eventType == XmlPullParser.START_TAG) {
						if (parser.getName().equals("map")) {
							boolean isLinked = parser.getAttributeBooleanValue(null, "linked", false);

							map = isLinked ? new LinkedHashMap<String, String>() : new HashMap<String, String>();
						} else if (parser.getName().equals("entry")) {
							key = parser.getAttributeValue(null, "key");

							if (null == key) {
								parser.close();
								return null;
							}
						}
					} else if (eventType == XmlPullParser.END_TAG) {
						if (parser.getName().equals("entry")) {
							map.put(key, value);
							key = null;
							value = null;
						}
					} else if (eventType == XmlPullParser.TEXT) {
						if (null != key) {
							value = parser.getText();
						}
					}
					eventType = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return map;
		}


	 public static void setValid(boolean val,Context context)
	    {
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor edit = shared.edit();
			edit.putBoolean("valid", val);
			edit.commit();
	    }
	 public static void setKey(String val,Context context)
	    {
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor edit = shared.edit();
			edit.putString("key", val);
			edit.commit();
	    }
	    
	 public static void setActivated(boolean val,Context context)
	    {
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor edit = shared.edit();
			edit.putBoolean("activated", val);
			edit.commit();
	    }
		
	 public static boolean getActivated(Context context)
	    {
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
			return shared.getBoolean("activated", false);
	    }
		
	 public static void setGroup(String val,Context context)
	    {
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor edit = shared.edit();
			edit.putString("group", val);
			edit.commit();
	    }
		
	 public static String getGroup(Context context)
	    {
	    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
			return shared.getString("group", "");
	    }
	 public static void setLastKnownLang(String val,Context context)
		    {
		    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor edit = shared.edit();
				edit.putString("lang", val);
				edit.commit();
		    }
	 public static String getLastKnownLang(Context context)
		    {
		    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
				return shared.getString("lang", "eng");
		    }
	 
}
