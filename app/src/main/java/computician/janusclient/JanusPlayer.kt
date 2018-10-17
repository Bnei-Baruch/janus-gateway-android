package computician.janusclient

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.webrtc.VideoRendererGui

class JanusPlayer : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    internal var streamAudio: StreamBBAudio? = null


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Send a notification that service is started
        Log.d("Service","Service started.")

        // Do a periodic task
        val con = VideoRendererGui.getEGLContext()

        streamAudio = StreamBBAudio(null)
        streamAudio!!.setId(Integer.parseInt(intent.action))
        streamAudio!!.initializeMediaContext(this@JanusPlayer, true, false, true, con)
        streamAudio!!.Start()

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

    }




}
