package computician.janusclient;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//import com.kab.channel66.utils.status;


public class SvivaTovaLogin extends BaseActivity implements LanguageSeletedListener {

	EditText mUser;
	EditText mPass;
	Button mSubmit;
	SvivaTovaLoginApiHelper mHelper;
	private TextView mError;
	Map<String,String> langsIdmap;
	StreamBBAudio streamAudio;
    private BluetoothAdapter mBtAdapter;
    AudioManager mAudioManager;
;
	private static final String ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS = "power";
	private static final String ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "request_power";

    public SvivaTovaLogin() {
		// TODO Auto-generated constructor stub
	}
	
	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
	    setContentView(R.layout.login);
//
//		Intent intent = new Intent();
//		String packageName = getPackageName();
//		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//			if (pm.isIgnoringBatteryOptimizations(packageName))
//				intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//			else {
//				intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//				intent.setData(Uri.parse("package:" + packageName));
//			}
//		}
//		startActivity(intent);



        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        //mBtAdapter.getProfileProxy(this, mA2dpListener , BluetoothProfile.A2DP);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        mUser = (EditText)findViewById(R.id.et_un);
	    mPass = (EditText)findViewById(R.id.et_pw);
	    mSubmit = (Button)findViewById(R.id.btn_login);
	    mError = (TextView)findViewById(R.id.tv_error);


		langsIdmap= CommonUtils.getHashMapResource(SvivaTovaLogin.this, R.xml.langsids);
	    mSubmit.setOnClickListener(new OnClickListener() {
			
		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<String> details = new ArrayList<String>();
				details.add(mUser.getText().toString());
				details.add(mPass.getText().toString());
				SvivaTovaLoginApiHelper.status st;
				mHelper = (SvivaTovaLoginApiHelper) new SvivaTovaLoginApiHelper().execute(details);
				try {
					if(( st = (SvivaTovaLoginApiHelper.status)mHelper.get())!= SvivaTovaLoginApiHelper.status.sucess)
						if(st== SvivaTovaLoginApiHelper.status.not_allowed)
							mError.setText("Login failed (code 2)");
						else
							mError.setText("Login failed (code 1)");
					else
					{
						CommonUtils.setActivated(true, SvivaTovaLogin.this);
						//run language selector
						//with the language load the streams relevant like in weblogin and pass to stream list
						//CommonUtils.ShowLanguageSelection(SvivaTovaLogin.this,SvivaTovaLogin.this);
						startActivity(new Intent(SvivaTovaLogin.this,JanusActivity.class));

						finish();
						
//						CommonUtils.ShowLanguageSelection(SvivaTovaLogin.this,new LanguageSeletedListener() {
//							
//							@Override
//							public void onSelectLanguage(String lang) {
//								// TODO Auto-generated method stub
//								
//							}
//						});
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		if (CommonUtils.getActivated(this)) {

			startActivity(new Intent(this,JanusActivity.class));

			finish();
//			RelativeLayout mainLayout=(RelativeLayout)this.findViewById(R.id.loginLayout);
//			mainLayout.setVisibility(RelativeLayout.GONE);
			//CommonUtils.ShowLanguageSelection(SvivaTovaLogin.this, SvivaTovaLogin.this);
		}

	}

	@Override
	public void onSelectLanguage(final String lang) {
		// TODO Auto-generated method stub
		int index = CommonUtils.languages.indexOf(lang);
		String langcode = CommonUtils.langs.get(index);
		CommonUtils.setLastKnownLang(langcode, this);

		//play audio with selected language
		//startActivity(new Intent(this,JanusActivity.class));
		//startActivity(new Intent(this,MediaController.class));
		//Start streamlist activity
		Dialog playDialog = new Dialog(this);
		playDialog.setTitle("Playing audio");
		playDialog.setContentView(R.layout.mediacontroller);
		final ImageButton but = (ImageButton) playDialog.findViewById(R.id.mediacontroller_play_pause);
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(SvivaTovaLogin.this);

			but.setImageResource(R.drawable.mediacontroller_pause01);
			EGLContext con = VideoRendererGui.getEGLContext();
			streamAudio = new StreamBBAudio(null);
			streamAudio.setId(Integer.parseInt(langsIdmap.get(CommonUtils.getLastKnownLang(SvivaTovaLogin.this))));
			streamAudio.initializeMediaContext(SvivaTovaLogin.this, true, false, true, con);
			streamAudio.Start();
//		  Intent intent1  = new Intent(this,JanusPlayer.class);
//		  intent1.setAction((langsIdmap.get(CommonUtils.getLastKnownLang(SvivaTovaLogin.this))));
//
//            startService(intent1);
			SharedPreferences.Editor ed = shared.edit();
			ed.putBoolean("isPlaying", true);
			ed.commit();

//            mAudioManager.setBluetoothScoOn(true);
//            mAudioManager.startBluetoothSco();

		final ImageButton ask = (ImageButton) playDialog.findViewById(R.id.mediacontroller_ask);

		but.setImageResource(R.drawable.mediacontroller_pause01);
		but.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EGLContext con = VideoRendererGui.getEGLContext();
				SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(SvivaTovaLogin.this);


				//String location = shared.getString("audiourl", "http://icecast.kab.tv/heb.mp3");
				if (shared.getBoolean("isPlaying", true)) {
					but.setImageResource(R.drawable.mediacontroller_play01);

					//stop the player
//					stream = new StreamBBVideo(remoteRender1);
//					stream.initializeMediaContext(this, true, true, true, con);
//					stream.Start();

					VideoRenderer.Callbacks remoteRender2;
					//remoteRender2 = VideoRendererGui.create(0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, false);

					if (streamAudio != null) {
						try {
							SharedPreferences.Editor ed = shared.edit();
							ed.putBoolean("isPlaying", false);
							ed.commit();
							streamAudio.stop();

						} catch (Exception e) {

						}
					}

				} else

				{
					but.setImageResource(R.drawable.mediacontroller_pause01);
//									svc=new Intent(StreamListActivity.this, AudioPlayerFactory.GetAudioPlayer(StreamListActivity.this).getClass());

					//start player
//					 stream = new StreamBBVideo(remoteRender1);
//					stream.initializeMediaContext(this, true, true, true, con);
//					stream.Start();
					VideoRenderer.Callbacks remoteRender2;
					//remoteRender2 = VideoRendererGui.create(0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, false);

					streamAudio = new StreamBBAudio(null);
					streamAudio.setId(Integer.parseInt(langsIdmap.get(CommonUtils.getLastKnownLang(SvivaTovaLogin.this))));
					streamAudio.initializeMediaContext(SvivaTovaLogin.this, true, false, true, con);
					streamAudio.Start();
					SharedPreferences.Editor ed = shared.edit();
					ed.putBoolean("isPlaying", true);
					ed.commit();

				}
			}
		});

		playDialog.show();

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// Create NotificationManager.

		// NotificationTargetActivity is the activity opened when user click notification.
		Intent intent = new Intent(SvivaTovaLogin.this, JanusActivity.class);
		Intent intentArr[] = {intent};

		PendingIntent pendingIntent = PendingIntent.getActivities(SvivaTovaLogin.this, 0, intentArr, 0);

		// Create a Notification Builder instance.
		String title = "Normal Size Happy Christmas. ";
		String textContent = "Christmas is comming --- dev2qa.com";

		long sendTime = System.currentTimeMillis();
		boolean autoCancel = false;

		// Get general settings Builder instance.
		NotificationCompat.Builder builder = getGeneralNotificationBuilder(title, textContent, R.drawable.icon, R.drawable.icon, autoCancel, sendTime);

		// Set content intent.
		builder.setContentIntent(pendingIntent);

		// Use both light, sound and vibrate.
		builder.setDefaults(Notification.DEFAULT_ALL);

		// Create Notification instance.
		Notification notification = builder.build();
		//notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_INSISTENT;

		// Send the notification.
		notificationManager.notify(1, notification);

	}
	private NotificationCompat.Builder getGeneralNotificationBuilder(String title, String textContent, int smallIconResId, int largeIconResId, boolean autoCancel, long sendTime)
	{
		// Create a Notification Builder instance.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(SvivaTovaLogin.this);

		// Set small icon.
		builder.setSmallIcon(smallIconResId);

		// Set large icon.
		BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(largeIconResId);
		Bitmap largeIconBitmap = bitmapDrawable.getBitmap();
		builder.setLargeIcon(largeIconBitmap);

		// Set title.
		builder.setContentTitle(title);

		// Set content text.
		builder.setContentText(textContent);

		// Set notification send time.
		builder.setWhen(sendTime);

		// If true then cancel the notification automatically.
		builder.setAutoCancel(autoCancel);

		return builder;
	}


}
