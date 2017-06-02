package computician.janusclient;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class SvivaTovaLoginApiHelper extends AsyncTask< ArrayList<String>, Void, SvivaTovaLoginApiHelper.status> {

	final public String kSuccesfulLoginIndicator= "<a href=\"http://kabbalahgroup.info/internet/he/users/logout\" title=\"יציאה\">יציאה</a>";
	final public String kSuccesfulLoginIndicator2= "<div id=\"internet\"></div>";
	final public String  kFailedLoginIndicator =  "אימייל או סיסמא שגויים";
	final public String  kSvivaTovaLoginURL ="http://kabbalahgroup.info/internet/api/v1/tokens.json";

	HttpClient mHttpclient;
	String mUser;
	String mPassword;
	String mLocalization;
	status mSuccess = status.fail;


	static public enum status{
		sucess,
		fail,
		not_allowed
	}
	
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
		post.addHeader("Content-Type","application/json");
//		post.addHeader("Connection","keep-alive");
//		post.addHeader("Accept-Encoding","gzip,deflate,sdch");
//		post.addHeader("Host","kabbalahgroup.info");
//		post.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
//		post.addHeader("Referer", kSvivaTovaLoginURL);
		//post.addHeader("Cookie", servercookies);
		
		return post;
		
		
	}
	
	
	public String postLoginDetails() throws Exception 
	{
		mHttpclient = new DefaultHttpClient();
		 BufferedReader in = null;
		try{
			
		
//		CookieManager manager = CookieManager.getInstance();
//		manager.setAcceptCookie(true);
		HttpPost request = new HttpPost(kSvivaTovaLoginURL);
		request = getHeaderpost(request);
		
		 JSONObject jsonObject = new JSONObject();
         jsonObject.accumulate("email", mUser);
         jsonObject.accumulate("password", mPassword);
        

         // 4. convert JSONObject to JSON to String
         String json = jsonObject.toString();

         // ** Alternative way to convert Person object to JSON string usin Jackson Lib 
         // ObjectMapper mapper = new ObjectMapper();
         // json = mapper.writeValueAsString(person); 

         // 5. set json to StringEntity
         StringEntity se = new StringEntity(json);

         // 6. set httpPost Entity
         request.setEntity(se);

//         // 7. Set some headers to inform server about the type of the content   
//         httpPost.setHeader("Accept", "application/json");
//         httpPost.setHeader("Content-type", "application/json");
		    
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
	protected status doInBackground(ArrayList<String>... params) {
		// TODO Auto-generated method stub
		mUser = (String)params[0].get(0);
		mPassword = (String)params[0].get(1);
		try {
			return process(postLoginDetails());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status.fail;
	}
	 private status process(String result) {
         
		 
		 try {
			JSONObject res = new JSONObject(result);
			if(!res.has("error"))
			{
				
				boolean allowed = res.getBoolean("allow_archived_broadcasts");
				if(allowed)
		        	 mSuccess = status.sucess;
		         else
		        	 mSuccess = status.not_allowed;
				
				
			}
			 else
			{
				mSuccess = status.fail;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mSuccess;
		
		 
        	 
     }
	
}


