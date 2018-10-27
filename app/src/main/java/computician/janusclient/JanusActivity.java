package computician.janusclient;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.util.HashMap;
import java.util.Map;

import computician.janusclient.util.SystemUiHider;

public class JanusActivity extends FragmentActivity {
    private static final boolean AUTO_HIDE = true;


    private ListView langauges;
    private GLSurfaceView vsv;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender1;
    private VideoRenderer.Callbacks remoteRender2;
    private EchoTest echoTest;
    private StreamBBVideo stream;
    private StreamBBAudio streamAudio;
    private VideoRoomTest videoRoomTest;
    private HashMap<String,state>buttonState;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private Map<String,String> langsIdmap;
    private Dialog playDialog;
    private BluetoothAdapter mBtAdapter;
    private AudioManager mAudioManager;

    private class MyInit implements Runnable {
        public void run() {
            init();
        }
    }

    private void init() {
        try {
            EGLContext con = VideoRendererGui.getEGLContext();
            /*VideoRenderer.Callbacks[] renderers = new VideoRenderer.Callbacks[1];
            renderers[0] = remoteRender;
            videoRoomTest = new VideoRoomTest(localRender, renderers);
            videoRoomTest.initializeMediaContext(this, true, true, true, con);
            videoRoomTest.Start();
            */
//            echoTest = new EchoTest(localRender, remoteRender);
//            echoTest.initializeMediaContext(this, true, true, true, con);
//            echoTest.Start();

//            stream = new StreamBBVideo(remoteRender1);
//            stream.initializeMediaContext(this, true, true, true, con);
//            stream.Start();
//
//
//
//
//            streamAudio = new StreamBBAudio(remoteRender2);
//            streamAudio.initializeMediaContext(this, true, true, true, con);
//            streamAudio.Start();

        } catch (Exception ex) {
            Log.e("computician.janusclient", ex.getMessage());
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        super.onCreate(savedInstanceState);

        buttonState =  new HashMap<>(3);
        buttonState.put("speaker",state.OFF);
        buttonState.put("ear",state.ON);
        buttonState.put("bt",state.DISCONNECTED);

        final Intent intent = new Intent();
		final String packageName = getPackageName();
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (pm.isIgnoringBatteryOptimizations(packageName))
				intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
			else {
//			    new AlertDialog.Builder(this).setTitle("Continues Playing").setMessage("Please remove the app from battery optimization to be able to get continues play audio playing without interupptions")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                }).create().show();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);

			}

		}


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_janus);

        langauges = (ListView) findViewById(R.id.language_list);

        langsIdmap= CommonUtils.getHashMapResource(JanusActivity.this, R.xml.langsids);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, CommonUtils.languages);

        langauges.setAdapter(itemsAdapter);
        langauges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onSelectLanguage(CommonUtils.languages.get(i));
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        vsv = (GLSurfaceView) findViewById(R.id.glview);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        VideoRendererGui.setView(vsv, new MyInit());

       // localRender = VideoRendererGui.create(72, 72, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        remoteRender1 = VideoRendererGui.create(0, 0, 25, 25, RendererCommon.ScalingType.SCALE_ASPECT_FIT, false);
        remoteRender2 = VideoRendererGui.create(0, 0, 25, 25, RendererCommon.ScalingType.SCALE_ASPECT_FIT, false);
    }

    public  boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
    }

    public void onSelectLanguage(final String lang) {
        // TODO Auto-generated method stub
        int index = CommonUtils.languages.indexOf(lang);
        String langcode = CommonUtils.langs.get(index);
        CommonUtils.setLastKnownLang(langcode, this);

        //play audio with selected language
        //startActivity(new Intent(this,JanusActivity.class));
        //startActivity(new Intent(this,MediaController.class));
        //Start streamlist activity
        playDialog = new Dialog(this);
        playDialog.setTitle("Playing audio");
        playDialog.setContentView(R.layout.mediacontroller);
        final ImageButton but = (ImageButton) playDialog.findViewById(R.id.mediacontroller_play_pause);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(JanusActivity.this);

        but.setImageResource(R.drawable.mediacontroller_pause01);
        EGLContext con = VideoRendererGui.getEGLContext();
        streamAudio = new StreamBBAudio(null);
        streamAudio.setId(Integer.parseInt(langsIdmap.get(CommonUtils.getLastKnownLang(JanusActivity.this))));
        streamAudio.initializeMediaContext(JanusActivity.this, true, false, true, con);
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

        final ImageButton route = (ImageButton) playDialog.findViewById(R.id.mediacontroller_route);

        route.setImageResource(R.drawable.head_24);
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //mBtAdapter.getProfileProxy(this, mA2dpListener , BluetoothProfile.A2DP);
                mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
               if(isBluetoothHeadsetConnected() && mAudioManager.isBluetoothScoOn()) {
                   mAudioManager.setBluetoothScoOn(false);
                   mAudioManager.stopBluetoothSco();
                   if(!mAudioManager.isWiredHeadsetOn()) {
                       mAudioManager.setSpeakerphoneOn(true);
                       route.setImageResource(R.drawable.sp_24);
                   }else
                   {
                       route.setImageResource(R.drawable.head_24);
                   }
                   return;
               }

                if( mAudioManager.isSpeakerphoneOn()) {
                    mAudioManager.setBluetoothScoOn(false);
                    mAudioManager.stopBluetoothSco();
                    mAudioManager.setSpeakerphoneOn(false);
                    route.setImageResource(R.drawable.head_24);
                    return;
                }

                if(!mAudioManager.isSpeakerphoneOn() && !mAudioManager.isBluetoothScoOn()) {
                    if (isBluetoothHeadsetConnected()) {
                        mAudioManager.setBluetoothScoOn(true);
                        mAudioManager.startBluetoothSco();
                        route.setImageResource(R.drawable.bt_24);
                    } else {
                        if(!mAudioManager.isWiredHeadsetOn()) {
                            mAudioManager.setSpeakerphoneOn(true);
                            route.setImageResource(R.drawable.sp_24);
                        }
                    }
                    return;
                }


                
            }
        });
        but.setImageResource(R.drawable.mediacontroller_pause01);
        but.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EGLContext con = VideoRendererGui.getEGLContext();
                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(JanusActivity.this);


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
                    streamAudio.setId(Integer.parseInt(langsIdmap.get(CommonUtils.getLastKnownLang(JanusActivity.this))));
                    streamAudio.initializeMediaContext(JanusActivity.this, true, false, true, con);
                    streamAudio.Start();
                    SharedPreferences.Editor ed = shared.edit();
                    ed.putBoolean("isPlaying", true);
                    ed.commit();

                }
            }
        });

        playDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(JanusActivity.this);
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

                }
            }
        });
        playDialog.show();

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// Create NotificationManager.
//
//        // NotificationTargetActivity is the activity opened when user click notification.
//        Intent intent = new Intent(JanusActivity.this, JanusActivity.class);
//        Intent intentArr[] = {intent};
//
//        PendingIntent pendingIntent = PendingIntent.getActivities(JanusActivity.this, 0, intentArr, 0);
//
//        // Create a Notification Builder instance.
//        String title = "Normal Size Happy Christmas. ";
//        String textContent = "Christmas is comming --- dev2qa.com";
//
//        long sendTime = System.currentTimeMillis();
//        boolean autoCancel = false;
//
//        // Get general settings Builder instance.
//        NotificationCompat.Builder builder = getGeneralNotificationBuilder(title, textContent, R.drawable.icon, R.drawable.icon, autoCancel, sendTime);
//
//        // Set content intent.
//        builder.setContentIntent(pendingIntent);
//
//        // Use both light, sound and vibrate.
//        builder.setDefaults(Notification.DEFAULT_ALL);
//
//        // Create Notification instance.
//        Notification notification = builder.build();
//        //notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_INSISTENT;
//
//        // Send the notification.
//        notificationManager.notify(1, notification);

    }

    private void turnOn(String key) {

    }

    private NotificationCompat.Builder getGeneralNotificationBuilder(String title, String textContent, int smallIconResId, int largeIconResId, boolean autoCancel, long sendTime)
    {
        // Create a Notification Builder instance.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(JanusActivity.this);

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

    @Override
    public void onResume()
    {
        super.onResume();
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(JanusActivity.this);
        if (shared.getBoolean("isPlaying", false) && (playDialog==null || !playDialog.isShowing()))
        {
            playDialog = new Dialog(this);
            playDialog.setTitle("Playing audio");
            playDialog.setContentView(R.layout.mediacontroller);
            final ImageButton but = (ImageButton) playDialog.findViewById(R.id.mediacontroller_play_pause);

            but.setImageResource(R.drawable.mediacontroller_pause01);
            but.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    EGLContext con = VideoRendererGui.getEGLContext();
                    SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(JanusActivity.this);


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
                        streamAudio.setId(Integer.parseInt(langsIdmap.get(CommonUtils.getLastKnownLang(JanusActivity.this))));
                        streamAudio.initializeMediaContext(JanusActivity.this, true, false, true, con);
                        streamAudio.Start();
                        SharedPreferences.Editor ed = shared.edit();
                        ed.putBoolean("isPlaying", true);
                        ed.commit();

                    }
                }
            });

            playDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(JanusActivity.this);
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

                    }
                }
            });
            playDialog.show();
        }
    }

    public enum state{
        ON,
        OFF,
        DISCONNECTED
    }

}
