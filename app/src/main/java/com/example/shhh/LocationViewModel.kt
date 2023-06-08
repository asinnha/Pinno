package com.example.shhh

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.shhh.project_data_models.ProfileSetting
import java.util.Locale

class LocationViewModel(val locationRepo: LocationRepo, val liveLocationManager: LiveLocationUpdates): ViewModel() {

    var gpsStatus = false

    val liveLatitude: LiveData<String> = liveLocationManager.latitude
    val liveLongitude: LiveData<String> = liveLocationManager.longitude

    var profileSettingArray:LiveData<List<ProfileSetting>> = locationRepo.profileSettingArray

    fun addProfileSettingList(profileSetting: ProfileSetting){
        locationRepo.addProfileSettingList(profileSetting)
    }

    fun loadList(){
        locationRepo.loadList()
    }

    fun saveList(){
        locationRepo.saveList()
    }

    fun getAddress(context:Context): String? {

        val local_latitude = liveLocationManager.latitude.value!!.toDouble().div(1000)
        val local_longitude = liveLocationManager.longitude.value!!.toDouble().div(1000)

        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(
            local_latitude,
            local_longitude,
            1)
        return address?.get(0)?.getAddressLine(0)
    }

    fun startLiveLocation(){
        liveLocationManager.startLocationUpdates()
        gpsStatus = true
    }

    fun stopLocation(){
        liveLocationManager.stopLiveLocationUpdates()
        gpsStatus = false
    }

}