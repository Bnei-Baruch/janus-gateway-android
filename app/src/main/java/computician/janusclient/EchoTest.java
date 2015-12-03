package computician.janusclient;

import android.app.Application;
import android.content.Context;
import android.opengl.EGLContext;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.util.ArrayList;
import java.util.List;

import computician.janusclientapi.IJanusGatewayCallbacks;
import computician.janusclientapi.IJanusPluginCallbacks;
import computician.janusclientapi.IPluginHandleWebRTCCallbacks;
import computician.janusclientapi.JanusMediaConstraints;
import computician.janusclientapi.JanusPluginHandle;
import computician.janusclientapi.JanusServer;
import computician.janusclientapi.JanusSupportedPluginPackages;
import computician.janusclientapi.PluginHandleSendMessageCallbacks;

/**
 * Created by ben.trent on 7/24/2015.
 */

//TODO create message classes unique to this plugin

public class EchoTest {

    private final String JANUS_URI = "http://itgb.net:8088/janus";
    //private final String JANUS_URI = "http://188.165.249.44:8088/janus";
    private JanusPluginHandle handle = null;
    private final VideoRenderer.Callbacks localRender, remoteRender;
    private final JanusServer janusServer;

    public class JanusGlobalCallbacks implements IJanusGatewayCallbacks {

        @Override
        public void onSuccess() {
            janusServer.Attach(new JanusPluginCallbacks());
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public String getServerUri() {
            return JANUS_URI;
        }

        @Override
        public List<PeerConnection.IceServer> getIceServers() {
            ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<PeerConnection.IceServer>();
            iceServers.add(new PeerConnection.IceServer("stun:v4g.kbb1.com:3478"));
            return  iceServers;
        }

        @Override
        public Boolean getIpv6Support() {
            return Boolean.FALSE;
        }

        @Override
        public Integer getMaxPollEvents() {
            return 0;
        }

        @Override
        public void onCallbackError(String error) {

            //Show error message to user
        }
    }

    public class JanusPluginCallbacks implements IJanusPluginCallbacks {

        @Override
        public void success(JanusPluginHandle pluginHandle) {
            EchoTest.this.handle = pluginHandle;

                JSONObject msg = new JSONObject();
                JSONObject obj = new JSONObject();
            JSONObject msg1 = new JSONObject();
            JSONObject obj1 = new JSONObject();
                try {

                    obj.put("request", "watch");
                    obj.put("id", 1);
                    msg.put("message", obj);
                    handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));

//                    obj1.put("request", "watch");
//                    obj1.put("id", 1);
//                    msg1.put("message", obj1);
//                    handle.sendMessage(new PluginHandleSendMessageCallbacks(msg1));
                } catch (Exception ex) {

                }

//            handle.createOffer(new IPluginHandleWebRTCCallbacks() {
//                @Override
//                public JSONObject getJsep() {
//                    return null;
//                }
//
//                @Override
//                public void onCallbackError(String error) {
//
//                }
//
//                @Override
//                public Boolean getTrickle() {
//                    return true;
//                }
//
//                @Override
//                public JanusMediaConstraints getMedia() {
//                    JanusMediaConstraints t =     new JanusMediaConstraints();
//                    t.setSendAudio(false);
//
//                    return t;
//                }
//
//                @Override
//                public void onSuccess(JSONObject obj) {
//                    Log.d("JANUSCLIENT", "OnSuccess for CreateOffer called");
//                    try {
//                        JSONObject body = new JSONObject();
//                        JSONObject msg = new JSONObject();
//                        body.put("request", "watch");
//
//                        body.put("id", 1);
//                        msg.put("message", body);
//                        msg.put("jsep", obj);
//                        handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));
//                        JSONObject body1 = new JSONObject();
//                        JSONObject msg1 = new JSONObject();
//                        body1.put("request", "watch");
//                        body1.put("id", 2);
//                        msg1.put("message", body1);
//                        msg1.put("jsep", obj);
//                        handle.sendMessage(new PluginHandleSendMessageCallbacks(msg1));
//
//                    } catch (Exception ex) {
//
//                    }
//                }
//            });

        }

        @Override
        public void onMessage(JSONObject msg, final JSONObject jsepLocal) {
            if(jsepLocal != null)
            {

                handle.createAnswer(new IPluginHandleWebRTCCallbacks() {

                    @Override
                    public void onSuccess(JSONObject obj) {
                        try {
                            JSONObject body = new JSONObject();
                            JSONObject msg = new JSONObject();
                            body.put("request", "start");

                            msg.put("message", body);
                            msg.put("jsep", obj);
                            handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));
                        } catch (Exception e) {

                        }
                    }


                    @Override
                    public JSONObject getJsep() {
                        return null;
                    }

                    @Override
                    public JanusMediaConstraints getMedia() {
                        JanusMediaConstraints m = new JanusMediaConstraints();
                        m.setSendAudio(true);
                        return m;
                    }

                    @Override
                    public Boolean getTrickle() {
                        return null;
                    }

                    @Override
                    public void onCallbackError(String error) {

                    }

                });
//                handle.handleRemoteJsep(new IPluginHandleWebRTCCallbacks() {
//                    final JSONObject myJsep = jsepLocal;
//                    @Override
//                    public void onSuccess(JSONObject obj) {
//
//                    }
//
//                    @Override
//                    public JSONObject getJsep() {
//                        return myJsep;
//                    }
//
//                    @Override
//                    public JanusMediaConstraints getMedia() {
//
//                        return null;
//                    }
//
//                    @Override
//                    public Boolean getTrickle() {
//                        return null;
//                    }
//
//                    @Override
//                    public void onCallbackError(String error) {
//
//                    }
//                });
            }
        }

        @Override
        public void onLocalStream(MediaStream stream) {
           // stream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
           // VideoRendererGui.update(localRender, 0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        }

        @Override
        public void onRemoteStream(MediaStream stream) {
            stream.videoTracks.get(0).setEnabled(true);
            if(stream.videoTracks.get(0).enabled())
                Log.d("JANUSCLIENT", "video tracks enabled");
            stream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
            VideoRendererGui.update(remoteRender, 0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
           // VideoRendererGui.update(localRender, 72, 72, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        }

        @Override
        public void onDataOpen(Object data) {

        }

        @Override
        public void onData(Object data) {

        }

        @Override
        public void onCleanup() {

        }

        @Override
        public JanusSupportedPluginPackages getPlugin() {
            return JanusSupportedPluginPackages.JANUS_STREAMING;
        }

        @Override
        public void onCallbackError(String error) {

        }

        @Override
        public void onDetached() {

        }

    }

    public EchoTest(VideoRenderer.Callbacks localRender, VideoRenderer.Callbacks remoteRender) {
        this.localRender = localRender;
        this.remoteRender = remoteRender;
        janusServer = new JanusServer(new JanusGlobalCallbacks());
    }

    public boolean initializeMediaContext(Context context, boolean audio, boolean video, boolean videoHwAcceleration, EGLContext eglContext){
        return janusServer.initializeMediaContext(context, audio, video, videoHwAcceleration, eglContext);
    }

    public void Start() {
        janusServer.Connect();
    }
}
