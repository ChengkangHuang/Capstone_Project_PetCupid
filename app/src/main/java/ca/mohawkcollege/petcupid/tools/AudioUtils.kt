package ca.mohawkcollege.petcupid.tools

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import java.io.File

class AudioUtils {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Start recording audio
     * @param outputFile The output file to store the audio
     */
    fun startRecording(outputFile: File) {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }

    /**
     * Stop recording audio
     */
    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    /**
     * Start playback audio
     * @param context The context
     * @param uri The uri of the audio file
     * @param callback The callback function to return the duration of the audio
     * @param onCompletionListener The callback function to invoke when the audio is completed
     */
    fun startPlaybackAudio(context: Context, uri: Uri, callback: ((Int) -> Unit), onCompletionListener: (() -> Unit)? = null) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, uri)
            prepare()
            setOnPreparedListener {
                callback.invoke(it.duration / 1000)
                start()
            }
            setOnCompletionListener {
                onCompletionListener?.invoke()
                release()
                mediaPlayer = null
            }
        }
    }

    /**
     * Stop playback audio
     */
    fun stopPlaybackAudio() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }
}