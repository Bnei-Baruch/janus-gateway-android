package computician.janusclient;

import android.content.Context;
import android.opengl.EGLContext;
import android.util.Log;

import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RendererCommon;
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
 * Created by igal on 12/5/15.
 */
public class StreamBBAudio {

//  private final String JANUS_URI = "http://itgb.net:8088/janus";
    //private final String JANUS_URI = "http://jnseur.kbb1.com:8088/janus";

    private final String JANUS_URI = "https://v4g.kbb1.com/janusios";
    //private final String JANUS_URI = " http://v4g.kbb1.com:8088/janus";

    //private final String JANUS_URI = "http://188.165.249.44:8088/janus";
    private JanusPluginHandle handle = null;
    private final VideoRenderer.Callbacks  remoteRender;
    private final JanusServer janusServer;
    private int streamId = 15;


    public class JanusGlobalCallbacks implements IJanusGatewayCallbacks {


        @Override
        public void onSuccess() {
            janusServer.Attach(new JanusPluginCallbacksAudio());
        //    janusServer.Attach(new JanusPluginCallbacksVideo());
            //janusServer.Attach(new JanusPluginCallbacks(1));
           // janusServer.Attach(new JanusPluginCallbacks(2));
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


    public class JanusPluginCallbacksAudio implements IJanusPluginCallbacks {

//        public JanusPluginCallbacks(int id) {
//             streamId = id;
//        }
        @Override
        public void success(JanusPluginHandle pluginHandle) {

            StreamBBAudio.this.handle = pluginHandle;

            JSONObject msg = new JSONObject();
            JSONObject obj = new JSONObject();
            JSONObject msg1 = new JSONObject();
            JSONObject obj1 = new JSONObject();
            try {

                obj.put("request", "watch");
                obj.put("id", streamId);
                msg.put("message", obj);
                handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));

            }
            catch (Exception e) {
            }

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
                        return jsepLocal;
                    }

                    @Override
                    public JanusMediaConstraints getMedia() {
                        JanusMediaConstraints m = new JanusMediaConstraints();
                        m.setSendAudio(false);
                        m.setRecvVideo(true);
                        m.setRecvAudio(true);
                        return m;
                    }

                    @Override
                    public Boolean getTrickle() {
                        return true;
                    }

                    @Override
                    public void onCallbackError(String error) {

                    }

                });
//
            }

        }

        @Override
        public void onLocalStream(MediaStream stream) {
            // stream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
            // VideoRendererGui.update(localRender, 0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        }

        @Override
        public void onRemoteStream(MediaStream stream) {
            if(stream.videoTracks.size()>0) {
                stream.videoTracks.get(0).setEnabled(true);
                if (stream.videoTracks.get(0).enabled()) {
                    Log.d("JANUSCLIENT", "video tracks enabled");
                    stream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
                    VideoRendererGui.update(remoteRender, 0, 0, 25, 50, RendererCommon.ScalingType.SCALE_ASPECT_FIT, false);
                    // VideoRendererGui.update(localRender, 72, 72, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);

                }
            }
//            stream.audioTracks.get(0).setEnabled(true);
//            if(stream.audioTracks.get(0).enabled())
//            {
//
//            }

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


    public class JanusPluginCallbacksVideo implements IJanusPluginCallbacks {
        int streamId;
        //        public JanusPluginCallbacks(int id) {
//             streamId = id;
//        }
        @Override
        public void success(JanusPluginHandle pluginHandle) {

            StreamBBAudio.this.handle = pluginHandle;

            JSONObject msg = new JSONObject();
            JSONObject obj = new JSONObject();
            JSONObject msg1 = new JSONObject();
            JSONObject obj1 = new JSONObject();
            try {

                obj.put("request", "watch");
                obj.put("id", 1);
                msg.put("message", obj);
                handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));

            }
            catch (Exception e) {
            }

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
                        return jsepLocal;
                    }

                    @Override
                    public JanusMediaConstraints getMedia() {
                        JanusMediaConstraints m = new JanusMediaConstraints();
                        m.setSendAudio(false);
                        m.setRecvVideo(true);
                        m.setRecvAudio(true);
                        return m;
                    }

                    @Override
                    public Boolean getTrickle() {
                        return true;
                    }

                    @Override
                    public void onCallbackError(String error) {

                    }

                });
//
            }

        }

        @Override
        public void onLocalStream(MediaStream stream) {
            // stream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
            // VideoRendererGui.update(localRender, 0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        }

        @Override
        public void onRemoteStream(MediaStream stream) {
            if(stream.videoTracks.size()>0) {
                stream.videoTracks.get(0).setEnabled(true);
                if (stream.videoTracks.get(0).enabled()) {
                    Log.d("JANUSCLIENT", "video tracks enabled");
                    stream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
                    VideoRendererGui.update(remoteRender, 0, 0, 25, 50, RendererCommon.ScalingType.SCALE_ASPECT_FIT, false);
                    // VideoRendererGui.update(localRender, 72, 72, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
                }
            }
//            stream.audioTracks.get(0).setEnabled(true);
//            if(stream.audioTracks.get(0).enabled())
//            {
//
//            }

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

    public StreamBBAudio(VideoRenderer.Callbacks remoteRender) {

        this.remoteRender = remoteRender;
        janusServer = new JanusServer(new JanusGlobalCallbacks());
    }

    public boolean initializeMediaContext(Context context, boolean audio, boolean video, boolean videoHwAcceleration, EGLContext eglContext){
        return janusServer.initializeMediaContext(context, audio, video, videoHwAcceleration, eglContext);
    }

    public void Start() {
        janusServer.Connect();
    }

    public void setId(int id)
    {
        streamId = id;
    }

    public void stop ()
    {



        try {

//            handle.createOffer(new IPluginHandleWebRTCCallbacks() {
//                                   @Override
//                                   public void onSuccess(JSONObject obj) {
//                                       JSONObject msg = new JSONObject();
//
//                                       JSONObject body = new JSONObject();
//
//                                       try {
//                                           body.put("request", "stop");
//                                           msg.put("message", body);
//                                           msg.put("jsep", obj);
//                                           handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));
//
//                                       } catch (JSONException e) {
//                                           e.printStackTrace();
//                                       }
//
//
//                                   }
//
//                                   @Override
//                                   public JSONObject getJsep() {
//                                       return null;
//                                   }
//
//                                   @Override
//                                   public JanusMediaConstraints getMedia() {
//                                       JanusMediaConstraints cons = new JanusMediaConstraints();
//                                       cons.setRecvAudio(false);
//                                       cons.setRecvVideo(false);
//                                       cons.setSendAudio(false);
//                                       return cons;
//                                   }
//
//                                   @Override
//                                   public Boolean getTrickle() {
//                                       return true;
//                                   }
//
//                                   @Override
//                                   public void onCallbackError(String error) {
//
//                                   }
//                               });

                                        JSONObject msg = new JSONObject();

                                        JSONObject body = new JSONObject();
                                        body.put("request", "stop");
                                        msg.put("message", body);
                                        handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));




        }
        catch (Exception e) {
        }

    }
}


