package com.example.shhh

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.shhh.project_data_models.Coordinates
import com.example.shhh.project_data_models.ProfileSetting
import java.util.Locale

class LocationViewModel(val locationRepo: LocationRepo, val liveLocationManager: LiveLocationUpdates): ViewModel() {

//    val mLocation:LiveData<Coordinates> = locationRepo.getCurrentLocation()

    val liveLatitude: LiveData<String> = liveLocationManager.latitude
    val liveLongitude: LiveData<String> = liveLocationManager.longitude

    val profileSettingArray:LiveData<List<ProfileSetting>> = locationRepo.profileSettingArray

    fun addProfileSettingList(profileSetting: ProfileSetting){
        locationRepo.addProfileSettingList(profileSetting)
    }

    fun loadList(){
        locationRepo.loadList()
    }

    fun saveList(){
        locationRepo.saveList()
    }

    fun getAddress(context:Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(liveLatitude.value!!.toDouble(),liveLongitude.value!!.toDouble(),1)
        return address!!.get(0).getAddressLine(0).toString()
    }

    val liveLocation = liveLocationManager.startLocationUpdates()

    fun stopLocation(){
        liveLocationManager.stopLiveLocationUpdates()
    }

    fun changeProfileOnLocationUpdate(coordinates: Coordinates,soundProfile: String){
        liveLocationManager.compareLocation(coordinates,soundProfile)
    }

    override fun onCleared() {
        super.onCleared()
    }
}