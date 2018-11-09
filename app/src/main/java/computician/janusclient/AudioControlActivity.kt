package computician.janusclient

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import org.webrtc.VideoRenderer
import org.webrtc.VideoRendererGui

class AudioControlActivity : AppCompatActivity() {

    private var streamAudio: StreamBBAudio? = null
    private var langsIdmap: Map<String, String>? = null


    override fun onDestroy() {
        super.onDestroy()
        streamAudio!!.release()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mediacontroller)
        val shared = PreferenceManager.getDefaultSharedPreferences(this@AudioControlActivity)
        val ed = shared.edit()
        
        val but = findViewById<ImageButton>(R.id.mediacontroller_play_pause)

        intent.action

        langsIdmap = CommonUtils.getHashMapResource(this@AudioControlActivity, R.xml.langsids)

        val con = VideoRendererGui.getEGLContext()
        streamAudio = StreamBBAudio(null)
        streamAudio!!.setId(Integer.parseInt((langsIdmap as MutableMap<String, String>?)?.get(CommonUtils.getLastKnownLang(this@AudioControlActivity))))
        streamAudio!!.initializeMediaContext(this@AudioControlActivity, true, false, true, con)
        streamAudio!!.Start()
        ed.putBoolean("isPlaying", true)

        val route = findViewById<ImageButton>(R.id.mediacontroller_route)

        route.setImageResource(R.drawable.head_24)
        route.setOnClickListener(View.OnClickListener {
            //mBtAdapter.getProfileProxy(this, mA2dpListener , BluetoothProfile.A2DP);
            var mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (isBluetoothHeadsetConnected() && mAudioManager.isBluetoothScoOn()) {
                mAudioManager.setBluetoothScoOn(false)
                mAudioManager.stopBluetoothSco()
                if (!mAudioManager.isWiredHeadsetOn()) {
                    mAudioManager.setSpeakerphoneOn(true)
                    route.setImageResource(R.drawable.sp_24)
                } else {
                    route.setImageResource(R.drawable.head_24)
                }
                return@OnClickListener
            }

            if (mAudioManager.isSpeakerphoneOn()) {
                mAudioManager.setBluetoothScoOn(false)
                mAudioManager.stopBluetoothSco()
                mAudioManager.setSpeakerphoneOn(false)
                route.setImageResource(R.drawable.head_24)
                return@OnClickListener
            }

            if (!mAudioManager.isSpeakerphoneOn() && !mAudioManager.isBluetoothScoOn()) {
                if (isBluetoothHeadsetConnected()) {
                    mAudioManager.setBluetoothScoOn(true)
                    mAudioManager.startBluetoothSco()
                    route.setImageResource(R.drawable.bt_24)
                } else {
                    if (!mAudioManager.isWiredHeadsetOn()) {
                        mAudioManager.setSpeakerphoneOn(true)
                        route.setImageResource(R.drawable.sp_24)
                    }
                }
                return@OnClickListener
            }
        })
        but.setImageResource(R.drawable.mediacontroller_pause01)


        but.setOnClickListener {
            // TODO Auto-generated method stub
            val con = VideoRendererGui.getEGLContext()



            //String location = shared.getString("audiourl", "http://icecast.kab.tv/heb.mp3");
            if (shared.getBoolean("isPlaying", true)) {
                but.setImageResource(R.drawable.mediacontroller_play01)

                //stop the player
                //					stream = new StreamBBVideo(remoteRender1);
                //					stream.initializeMediaContext(this, true, true, true, con);
                //					stream.Start();

                val remoteRender2: VideoRenderer.Callbacks
                //remoteRender2 = VideoRendererGui.create(0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, false);

                if (streamAudio != null) {
                    try {

                        ed.putBoolean("isPlaying", false)
                        ed.commit()
                        streamAudio!!.stop()

                    } catch (e: Exception) {

                    }

                }

            } else {
                but.setImageResource(R.drawable.mediacontroller_pause01)
                //									svc=new Intent(StreamListActivity.this, AudioPlayerFactory.GetAudioPlayer(StreamListActivity.this).getClass());

                //start player
                //					 stream = new StreamBBVideo(remoteRender1);
                //					stream.initializeMediaContext(this, true, true, true, con);
                //					stream.Start();
                val remoteRender2: VideoRenderer.Callbacks
                //remoteRender2 = VideoRendererGui.create(0, 0, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, false);

                streamAudio = StreamBBAudio(null)
                streamAudio!!.setId(Integer.parseInt((langsIdmap as MutableMap<String, String>?)!![CommonUtils.getLastKnownLang(this@AudioControlActivity)]))
                streamAudio!!.initializeMediaContext(this@AudioControlActivity, true, false, true, con)
                streamAudio!!.Start()
                val ed = shared.edit()
                ed.putBoolean("isPlaying", true)
                ed.commit()

            }
        }


    }

    fun isBluetoothHeadsetConnected(): Boolean {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED)
    }
}
