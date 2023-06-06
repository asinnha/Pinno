package com.example.shhh

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.shhh.project_data_models.Coordinates
import com.example.shhh.project_data_models.ProfileSetting
import java.util.Locale

class LocationViewModel(val locationRepo: LocationRepo, val liveLocationManager: LiveLocationUpdates): ViewModel() {

//    val mLocation:LiveData<Coordinates> = locationRepo.getCurrentLocation()

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
        val geocoder = Geocoder(context, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val address = geocoder.getFromLocation(
                liveLocationManager.latitude.value!!.toDouble(),
                liveLocationManager.longitude.value!!.toDouble(),
                1
            ) {
                it[0].subAdminArea
            }
            return address.toString()
        } else {
            val address = geocoder.getFromLocation(
                liveLocationManager.latitude.value!!.toDouble(),
                liveLocationManager.longitude.value!!.toDouble(),
                1)
            return address?.get(0)?.getAddressLine(0)
        }

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