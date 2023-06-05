package com.example.shhh

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.shhh.project_data_models.Coordinates
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task

class LiveLocationUpdates(val context: Context,val locationRepo: LocationRepo) {

    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback: LocationCallback

    var latitude = MutableLiveData<String>()
    var longitude = MutableLiveData<String>()
    private val fusedLocation = LocationServices.getFusedLocationProviderClient(context)
    private val audioManger = ContextCompat.getSystemService(context,AudioManager::class.java) as AudioManager

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,1000).apply {
            setMinUpdateDistanceMeters(100f)
        }.build()

        locationCallback = object: LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                for(location in result.locations){
                    latitude.value = location.latitude.toString()
                    longitude.value = location.longitude.toString()
                }
            }
        }

        fusedLocation.requestLocationUpdates(locationRequest,locationCallback,null)
        ToastFactory().toast(context,"the location has started")
    }

    fun stopLiveLocationUpdates(){
        fusedLocation.removeLocationUpdates(locationCallback)
    }

    fun compareLocation(coordinate: Coordinates, soundProfile: String){

        if( latitude.value == coordinate.latitude && longitude.value == coordinate.longitude){
            when(soundProfile){
                MainActivity.RINGER_CONST -> audioManger.ringerMode = AudioManager.RINGER_MODE_NORMAL

                MainActivity.VIBRATE_CONST -> audioManger.ringerMode = AudioManager.RINGER_MODE_VIBRATE

                MainActivity.SILENT_CONST -> audioManger.ringerMode = AudioManager.RINGER_MODE_SILENT

                else -> ToastFactory().toast(context,"null sound profile status")
            }
        }

    }

}