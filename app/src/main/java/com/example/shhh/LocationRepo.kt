package com.example.shhh

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.shhh.project_data_models.Coordinates
import com.example.shhh.project_data_models.ProfileSetting
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocationRepo(private val context: Context) {

    var sharedPreferences = context.getSharedPreferences("save_list",Context.MODE_PRIVATE)
    val KEY_CONST = "profile_settings_list"

    var profileSettingArray = MutableLiveData<List<ProfileSetting>>()

    fun addProfileSettingList(profileSetting: ProfileSetting){
        val list = profileSettingArray.value?.toMutableList()?: mutableListOf()
        list.add(profileSetting)
        profileSettingArray.value = list
        println(profileSettingArray.value)
    }

    fun saveList(){
        val list = profileSettingArray.value?.toMutableList()?: mutableListOf()
        val editor = sharedPreferences.edit()
        val jsonString = Gson().toJson(list)
        //testing the JSON structure
        println(jsonString)
        //by printing the converted JSON string
        editor.putString(KEY_CONST,jsonString)
        editor.apply()
    }

    fun loadList(){
        val jsonString = sharedPreferences.getString(KEY_CONST,null)
        if(!jsonString.isNullOrEmpty()){
            val type = object: TypeToken<List<ProfileSetting>>() {}.type
            val mlist =Gson().fromJson<List<ProfileSetting>>(jsonString,type)
            profileSettingArray.value = mlist
        }
    }

}