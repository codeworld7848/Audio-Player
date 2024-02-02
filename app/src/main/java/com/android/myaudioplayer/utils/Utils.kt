package com.android.myaudioplayer.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.myaudioplayer.presentation.screens.AudioData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object Utils {
    fun deleteSongs(songList: ArrayList<AudioData>, context: Context) {
        songList.forEach {
            val contentResolver = context.contentResolver
//            val uri = Uri.fromFile(File(it.path)) // Convert file path to URI
            val uri = Uri.parse(it.uri)
            val rowsDeleted = contentResolver.delete(uri, null, null)
            if (rowsDeleted > 0) {
                Log.d("DeleteStatus", "deleted: ")
                // File deleted successfully
            } else {
                Log.d("DeleteStatus", "Not delete ")
            }
        }
    }

    fun addToFavPreference(context: Context, songList: ArrayList<AudioData>) {
        val favPlayList: ArrayList<AudioData> = getFavPreference(context)
        songList.forEach { audioData ->
            if (favPlayList.size < 20) {
                if (!favPlayList.contains(audioData)) {
                    favPlayList.add(audioData)
                }
            } else {
                favPlayList.removeLast()
                favPlayList.removeFirst()
            }
        }
        val sharedPreferences = context.getSharedPreferences("my_music", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson() // You can use any JSON library of your choice
        val favSongListJson: String = gson.toJson(favPlayList)
        editor.putString("my_fav_songs", favSongListJson)
        editor.apply()
    }

    fun removeFromFavPreference(context: Context, songList: ArrayList<AudioData>) {
        val favPlayList: ArrayList<AudioData> = getFavPreference(context)
        songList.forEach { audioData ->
            if (favPlayList.contains(audioData)) {
                favPlayList.remove(audioData)
            }
        }
        val sharedPreferences = context.getSharedPreferences("my_music", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson() // You can use any JSON library of your choice
        val favSongListJson: String = gson.toJson(favPlayList)
        editor.putString("my_fav_songs", favSongListJson)
        editor.apply()
    }

    fun getFavPreference(context: Context): ArrayList<AudioData> {
        val sharedPreferences = context.getSharedPreferences("my_music", Context.MODE_PRIVATE)
        try {
            if (sharedPreferences.contains("my_fav_songs")) {
                val favSongJson = sharedPreferences.getString("my_fav_songs", null)
                if (favSongJson != null) {
                    val gson = Gson()
                    val type: Type? = object : TypeToken<ArrayList<AudioData?>?>() {}.type
                    val mySongList: ArrayList<AudioData> = gson.fromJson(favSongJson, type)
                    return mySongList
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun addRecentPlayedPreference(context: Context, audioData: AudioData) {
        val recentPlayerList: ArrayList<AudioData> = getRecentPlayedPreference(context)
        if (recentPlayerList.size < 20) {
            if (recentPlayerList.contains(audioData)) {
                recentPlayerList.remove(audioData)
            }
            recentPlayerList.add(0, audioData)
        } else {
            recentPlayerList.removeLast()
            recentPlayerList.removeAt(recentPlayerList.size - 1)
        }
        val sharedPreferences = context.getSharedPreferences("my_music", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson() // You can use any JSON library of your choice
        val favSongListJson: String = gson.toJson(recentPlayerList)
        editor.putString("recent_played", favSongListJson)
        editor.apply()
    }

    fun getRecentPlayedPreference(context: Context): ArrayList<AudioData> {
        val sharedPreferences = context.getSharedPreferences("my_music", Context.MODE_PRIVATE)
        try {
            if (sharedPreferences.contains("recent_played")) {
                val favSongJson = sharedPreferences.getString("recent_played", null)
                if (favSongJson != null) {
                    val gson = Gson()
                    val type: Type? = object : TypeToken<ArrayList<AudioData?>?>() {}.type
                    val mySongList: ArrayList<AudioData> = gson.fromJson(favSongJson, type)
                    return mySongList
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayListOf()
    }
}