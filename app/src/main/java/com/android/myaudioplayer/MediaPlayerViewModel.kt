package com.android.myaudioplayer

import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.myaudioplayer.presentation.components.getAlbumArt
import com.android.myaudioplayer.presentation.screens.AudioData
import kotlinx.coroutines.launch

class MediaPlayerViewModel : ViewModel() {
    var mediaPlayer: MediaPlayer? = null
    var manuallyPaused = mutableStateOf(false)
    var isPlaying = mutableStateOf(false)
    var progress = mutableStateOf(0f)
    private var tempList: ArrayList<AudioData> = arrayListOf()
    var audioList: MutableState<List<AudioData>> = mutableStateOf(emptyList())
    var selectedAudioFile: MutableState<AudioData?>? = mutableStateOf(null)
    var currentMusicPosition: MutableState<Int> = mutableStateOf(-1)
    val searchedMusic = mutableStateOf("")
    val currentPosition = mutableStateOf("")
    val duration = mutableStateOf("")

    fun getSongsFromDevice(
        context: Context
    ) {
        viewModelScope.launch {
            val projection = arrayOf(
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,  //For path
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )
            cursor?.use {
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                while (cursor.moveToNext()) {
                    val album = cursor.getString(albumColumn)
                    val title = cursor.getString(titleColumn)
                    val duration = cursor.getString(durationColumn)
                    val path = cursor.getString(pathColumn)
                    val artist = cursor.getString(artistColumn)
                    val id = cursor.getLong(idColumn)
                    val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        .buildUpon()
                        .appendPath(id.toString())
                        .build()
                    val albumData = getAlbumArt(path)
                    tempList.add(
                        AudioData(path, title, artist, album, duration, contentUri, albumData)
                    )
                }
                audioList.value = tempList
            }
        }
    }


    fun playMusic() {
        // Implement code to start playing music
        // You need to set mediaPlayer and isPlaying accordingly.
        mediaPlayer?.start()
        isPlaying.value = true
        manuallyPaused.value = false
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        isPlaying.value = false
//        manuallyPaused.value = true
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun updateProgress(playNextOnComplete: () -> Unit) {
        // Implement code to update progress
        // You need to set progress based on the current playback position.
        this.mediaPlayer?.let {
            progress.value = (it.currentPosition.toFloat() / it.duration.toFloat())
            Log.d("Progresssss", progress.toString())
        }
        this.mediaPlayer?.setOnCompletionListener {
            if (audioList.value.size in 0..<audioList.value.size) {
                currentMusicPosition.value += 1
                selectedAudioFile?.value = audioList.value[currentMusicPosition.value]
                isPlaying.value = false
                manuallyPaused.value = false
                playNextOnComplete()
            }else{
                manuallyPaused.value = true
                pauseMusic()
            }
        }
    }

    fun seekToPosition(position: Int) {
        mediaPlayer?.seekTo(position)
        this.mediaPlayer?.let {
            currentPosition.value =
                formatDuration(mediaPlayer?.currentPosition ?: 0, mediaPlayer?.duration ?: 0)
            progress.value = (it.currentPosition.toFloat() / it.duration.toFloat())
            Log.d("Progresssss", progress.toString())
        }
    }

    fun getCurrentPosition(): String {
        return formatDuration(mediaPlayer?.currentPosition ?: 0, mediaPlayer?.duration ?: 0)
    }

    fun getDuration(): String {
        return formatTotalDuration(mediaPlayer?.duration ?: 0)
    }

    private fun formatDuration(currentPosition: Int, duration: Int): String {
        val currentSeconds = currentPosition / 1000
        val currentMinutes = currentSeconds / 60
        val currentHours = currentMinutes / 60

        val formattedCurrentHours = currentHours % 24
        val formattedCurrentMinutes = currentMinutes % 60
        val formattedCurrentSeconds = currentSeconds % 60

        return String.format(
            "%02d:%02d:%02d",
            formattedCurrentHours,
            formattedCurrentMinutes,
            formattedCurrentSeconds
        )
    }

    private fun formatTotalDuration(duration: Int): String {
        val totalSeconds = duration / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60
        val formattedTotalHours = totalHours % 24
        val formattedTotalMinutes = totalMinutes % 60
        val formattedTotalSeconds = totalSeconds % 60
        return String.format(
            "%02d:%02d:%02d",
            formattedTotalHours,
            formattedTotalMinutes,
            formattedTotalSeconds
        )
    }

}