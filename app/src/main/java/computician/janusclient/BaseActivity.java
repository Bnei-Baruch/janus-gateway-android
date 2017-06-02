package computician.janusclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BaseActivity extends Activity {

	@Override
	public void onResume()
	{
		super.onResume();
		checkConnectivity();
	}
	public boolean checkConnectivity()
	{
		Dialog blockApp;
		boolean state;
		if(!(state = isOnline(BaseActivity.this)))
		{
			new AlertDialog.Builder(this)
		    .setTitle("Data not available")
		    .setMessage("Appliaction needs data connection")
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		        	//finish();
		        	return;
		        }
		     })
		    
		    
		     ;
			//.show()
			return false;
		}
		return true;
	}
	 public boolean isOnline(Context context) { 
		    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);    
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();    
		    return netInfo != null && netInfo.isConnected();
		}
}
