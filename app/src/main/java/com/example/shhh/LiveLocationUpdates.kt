package com.example.shhh

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.shhh.project_data_models.Coordinates
import com.example.shhh.project_data_models.ProfileSetting
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LiveLocationUpdates(val context: Context,val locationRepo: LocationRepo) {

    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback: LocationCallback

    var latitude = MutableLiveData<String>()
    var longitude = MutableLiveData<String>()
    var coordinates = MutableLiveData<Coordinates>()
    private val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
    private val audioManger = ContextCompat.getSystemService(context,AudioManager::class.java) as AudioManager


    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY,1000).build()

        locationCallback = object: LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                for(location in result.locations){
                    latitude.value = location.latitude.toString()
                    longitude.value = location.longitude.toString()
                    coordinates.value = Coordinates(latitude.value,longitude.value)
                    compareLocation(locationRepo.profileSettingArray)
                }
            }
        }

        fusedLocation.requestLocationUpdates(locationRequest,locationCallback,null)
            .addOnSuccessListener {
                ToastFactory().toast(context,"the location has started")
            }
            .addOnFailureListener {
                ToastFactory().toast(context,"the location failed")
                println(it.message)
            }

    }

    fun stopLiveLocationUpdates(){
        fusedLocation.removeLocationUpdates(locationCallback).
                addOnSuccessListener {
                }
    }

    fun compareLocation(list: MutableLiveData<List<ProfileSetting>>){

        val mList: List<ProfileSetting>? = list.value

        mList?.let{
            it.forEach { it1 ->
                if(it1.switch == 1 && it1.soundProfile!=null &&
                        it1.coordinates?.latitude == latitude.value &&
                        it1.coordinates?.longitude == longitude.value)
                {
                    when(it1.soundProfile){

                        MainActivity.RINGER_CONST -> audioManger.ringerMode = AudioManager.RINGER_MODE_NORMAL

                        MainActivity.VIBRATE_CONST -> audioManger.ringerMode = AudioManager.RINGER_MODE_VIBRATE

                        MainActivity.SILENT_CONST -> audioManger.ringerMode = AudioManager.RINGER_MODE_SILENT

                        else -> ToastFactory().toast(context,"null sound profile status")
                    }
                }
            }
        }


//        if( latitude.value == list.coordinate.latitude && longitude.value == coordinate.longitude){
//
//        }

    }

}