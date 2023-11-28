package com.example.stas1

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Context

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private val audioFiles = intArrayOf(R.raw.blanka, R.raw.comprendsas, R.raw.uranus)
    private var currentTrackIndex = 0
    private var isPlaying = false

    private lateinit var seekBar: SeekBar
    private lateinit var imageView: ImageView
    private lateinit var trackTitleTextView: TextView
    private lateinit var playPauseButton: Button
    private lateinit var audioManager: AudioManager // Объявите audioManager

    private val trackTitles = arrayOf("PNL - Blanka", "PNL - J’Comprends Pas", "PNL - Uranus")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val previousButton = findViewById<Button>(R.id.previousButton)
        playPauseButton = findViewById<Button>(R.id.playPauseButton)
        val nextButton = findViewById<Button>(R.id.nextButton)
        val increaseVolumeButton = findViewById<Button>(R.id.increaseVolumeButton)
        val decreaseVolumeButton = findViewById<Button>(R.id.decreaseVolumeButton)

        increaseVolumeButton.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
        }

        decreaseVolumeButton.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
        }

        mediaPlayer = MediaPlayer.create(this, audioFiles[currentTrackIndex])

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        trackTitleTextView = findViewById(R.id.trackTitleTextView)

        previousButton.setOnClickListener {
            if (currentTrackIndex > 0) {
                currentTrackIndex--
            } else {
                currentTrackIndex = audioFiles.size - 1
            }
            switchTrack()
        }

        playPauseButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                playPauseButton.text = "Play"
            } else {
                mediaPlayer.start()
                playPauseButton.text = "Pause"
            }
            isPlaying = !isPlaying
        }

        nextButton.setOnClickListener {
            currentTrackIndex = (currentTrackIndex + 1) % audioFiles.size
            switchTrack()
        }

        seekBar = findViewById(R.id.seekBar)
        imageView = findViewById(R.id.imageView2)

        seekBar.max = mediaPlayer.duration

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mediaPlayer.setOnCompletionListener {
            currentTrackIndex = (currentTrackIndex + 1) % audioFiles.size
            switchTrack()
        }

        Thread {
            while (mediaPlayer != null) {
                try {
                    val currentPosition = mediaPlayer.currentPosition
                    seekBar.progress = currentPosition
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        switchTrack()
    }

    private fun switchTrack() {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(this, audioFiles[currentTrackIndex])
        mediaPlayer.start()
        playPauseButton.text = "Pause"
        seekBar.max = mediaPlayer.duration
        updateTrackTitle()
    }

    private fun updateTrackTitle() {
        trackTitleTextView.text = trackTitles[currentTrackIndex]
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}

