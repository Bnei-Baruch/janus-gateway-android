package computician.janusclient;

import computician.janusclient.util.SystemUiHider;

import android.app.Activity;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

public class JanusActivity extends Activity {
    private static final boolean AUTO_HIDE = true;

    private GLSurfaceView vsv;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender1;
    private VideoRenderer.Callbacks remoteRender2;
    private EchoTest echoTest;
    private StreamBBVideo stream;
    private StreamBBAudio streamAudio;
    private VideoRoomTest videoRoomTest;

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

            stream = new StreamBBVideo(remoteRender1);
            stream.initializeMediaContext(this, true, true, true, con);
            stream.Start();

            streamAudio = new StreamBBAudio(remoteRender2);
            streamAudio.initializeMediaContext(this, true, true, true, con);
            streamAudio.Start();

        } catch (Exception ex) {
            Log.e("computician.janusclient", ex.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_janus);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        vsv = (GLSurfaceView) findViewById(R.id.glview);
        vsv.setPreserveEGLContextOnPause(true);
        vsv.setKeepScreenOn(true);
        VideoRendererGui.setView(vsv, new MyInit());

       // localRender = VideoRendererGui.create(72, 72, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        remoteRender1 = VideoRendererGui.create(0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, false);
        remoteRender2 = VideoRendererGui.create(0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, false);
    }
}
