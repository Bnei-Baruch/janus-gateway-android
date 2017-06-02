package computician.janusclient;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SvivaTovaLoginHelper extends AsyncTask< ArrayList<String>, Void, Boolean> {

	final public String kSuccesfulLoginIndicator= "<a href=\"http://kabbalahgroup.info/internet/he/users/logout\" title=\"יציאה\">יציאה</a>";
	final public String kSuccesfulLoginIndicator2= "<div id=\"internet\"></div>";
	final public String  kFailedLoginIndicator =  "אימייל או סיסמא שגויים";
	final public String  kSvivaTovaLoginURL ="http://kabbalahgroup.info/internet/he/users/login";

	HttpClient mHttpclient;
	String mUser;
	String mPassword;
	String mLocalization;
	boolean mSuccess;
	

	
	public HttpGet getHeaderget(HttpGet get)
	{
		get.addHeader("Content-Type","application/x-www-form-urlencoded");
		get.addHeader("Connection","keep-alive");
		get.addHeader("Accept-Encoding","gzip,deflate,sdch");
		get.addHeader("Host","kabbalahgroup.info");
		get.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
		
		return get;
	}
	public HttpPost getHeaderpost(HttpPost post)
	{
		post.addHeader("Content-Type","application/x-www-form-urlencoded");
		post.addHeader("Connection","keep-alive");
		post.addHeader("Accept-Encoding","gzip,deflate,sdch");
		post.addHeader("Host","kabbalahgroup.info");
		post.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
		post.addHeader("Referer", kSvivaTovaLoginURL);
		//post.addHeader("Cookie", servercookies);
		
		return post;
		
		
	}
	public String preGetLogin()
	{
		try{
			
		
		 mHttpclient = new DefaultHttpClient();
		
		HttpGet req = new HttpGet(kSvivaTovaLoginURL);
		req = getHeaderget(req);
//		CookieManager manager = CookieManager.getInstance();
//		manager.setAcceptCookie(true);
		
		org.apache.http.HttpResponse response = (org.apache.http.HttpResponse) mHttpclient.execute(req);
		if( response.getEntity() != null ) {
	         response.getEntity().consumeContent();
	      }//if
		return postLoginDetails( mUser, mPassword, mLocalization);
		}
		catch(Exception e)
		{
			 Log.e("log_tag", "Error in http connection "+e.toString());	
		}
		return "";
	}
	
	private String postLoginDetails(String user,String password,String localization) throws Exception 
	{
		 BufferedReader in = null;
		try{
			
		
//		CookieManager manager = CookieManager.getInstance();
//		manager.setAcceptCookie(true);
		HttpPost request = new HttpPost(kSvivaTovaLoginURL);
		request = getHeaderpost(request);
		
		 List<String> Body = new ArrayList<String>(2);
		    Body.add("utf8=%E2%9C%93");
		    Body.add("authenticity_token=F78FFKLAanNNgR131e2Oh1XST99TYp+jjTcvjyGxqs4=");
		    Body.add("user[email]="+mUser);
		    Body.add("user[password]="+mPassword);
		    Body.add("user[remember_me]=0");
		    Body.add("commit=כניסה למערכת");
		    String bodyToSend = TextUtils.join("&", Body);
			StringEntity entity = new StringEntity(bodyToSend);
		    request.setEntity(entity);
		    
		   // request.setHeader("Content-Length", Integer.toString(bodyToSend.toString().length()));
//		    mHttpclient.getConnectionManager().closeExpiredConnections();
		    org.apache.http.HttpResponse response =  (org.apache.http.HttpResponse) mHttpclient.execute(request);
		    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            return result;
		}
            catch(Exception e)
            {
            	 Log.e("log_tag", "Error in http connection "+e.toString());	
            }
         finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		
		
		return "";    
	}

	@Override
	protected Boolean doInBackground(ArrayList<String>... params) {
		// TODO Auto-generated method stub
		mUser = (String)params[0].get(0);
		mPassword = (String)params[0].get(1);
		return process(preGetLogin());
		
	}
	 private boolean process(String result) {
         
		if(result.contains(kSuccesfulLoginIndicator) && result.contains(kSuccesfulLoginIndicator2))
        	 mSuccess = true;
         else
        	 mSuccess = false;
		return mSuccess;
        	 
     }
	
}


