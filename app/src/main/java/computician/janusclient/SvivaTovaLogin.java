package computician.janusclient;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import computician.janusclient.CommonUtils;
import computician.janusclient.SvivaTovaLoginApiHelper;

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
	
	public SvivaTovaLogin() {
		// TODO Auto-generated constructor stub
	}
	
	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
	    setContentView(R.layout.login);


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
						CommonUtils.ShowLanguageSelection(SvivaTovaLogin.this,SvivaTovaLogin.this);
						
						
						
						
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

			RelativeLayout mainLayout=(RelativeLayout)this.findViewById(R.id.loginLayout);
			mainLayout.setVisibility(RelativeLayout.GONE);
			CommonUtils.ShowLanguageSelection(SvivaTovaLogin.this, SvivaTovaLogin.this);
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
			SharedPreferences.Editor ed = shared.edit();
			ed.putBoolean("isPlaying", true);
			ed.commit();


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
					but.setImageResource(R.drawable.mediacontroller_pause01);

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
					but.setImageResource(R.drawable.mediacontroller_play01);
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



	}


}
