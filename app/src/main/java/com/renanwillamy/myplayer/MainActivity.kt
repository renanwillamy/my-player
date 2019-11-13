package com.renanwillamy.myplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

   // private var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaPlayer: MediaPlayer
    private var currentUri: Uri? = null
    private var handler: Handler = Handler()
    private lateinit var runnable:Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img_main.setImageResource(R.drawable.player_background)

        img_play.setOnClickListener{
            if(!mediaPlayer!!.isPlaying){
                img_play.setImageResource(R.drawable.ic_stop_black_24dp)
                playSong()
            }else{
                img_play.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                mediaPlayer?.stop()
            }
        }
        img_back.setOnClickListener{
            mediaPlayer.seekTo(mediaPlayer.currentPosition - (30000))
        }
        img_forward.setOnClickListener{
            mediaPlayer.seekTo(mediaPlayer.currentPosition+(30000))
        }
        img_folder.setOnClickListener{
            var file = File(currentUri?.path.toString())
            var uri = if(file!=null) Uri.parse(file.parentFile?.path.toString()) else null
            if(uri==null){
                uri = currentUri
            }
            val intent = Intent()
                .setDataAndType(uri,"*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }

        // Seek bar change listener
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    private fun formatTimeToMin(time : Int) : String {

        val minutes = time / 60
        val seconds = time % 60

        return "${minutes}:${String.format("%02d", seconds)}"
    }

    private fun initializeSeekBar() {
        seek_bar.max = mediaPlayer.seconds

        runnable = Runnable {
            seek_bar.progress = mediaPlayer.currentSeconds

            tv_time_progress.text = "${formatTimeToMin(mediaPlayer.currentSeconds)} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_time_end.text = "${formatTimeToMin(diff)} sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    // Creating an extension property to get the media player time duration in seconds
    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }
    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }

    private fun playSong() {
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            if (currentUri != null) {
                setDataSource(applicationContext, currentUri!!)
            }
            prepare()
            start()
            img_play.setImageResource(R.drawable.ic_stop_black_24dp)
        }
        initializeSeekBar()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),0)
        }else {
            if (requestCode == 111 && resultCode == RESULT_OK) {
                currentUri = data?.data //The uri with the location of the file
                tv_song_name.text = File(currentUri?.path.toString()).name.toString()
                if(!::mediaPlayer.isInitialized || !mediaPlayer.isPlaying) {
                    playSong()
                }
                Toast.makeText(this, currentUri?.path.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
