package com.example.shhh

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shhh.profile_sheet_dialog.ProfileBottomSheetDialog
import com.example.shhh.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    companion object{
        val RINGER_CONST = "RINGER_MODE_NORMAL"
        val VIBRATE_CONST = "RINGER_MODE_VIBRATE"
        val SILENT_CONST = "RINGER_MODE_SILENT"
    }

    lateinit var koinContainer: KoinApplication
    lateinit var binding: ActivityMainBinding
    private lateinit var locationCallback: LocationCallback
    private val viewModel: LocationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        koinContainer = startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        viewModel.loadList()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            return
        }
        viewModel.startLiveLocation()

        viewModel.profileSettingArray.observe(this){
            var count = 0
            for(list in it){
                if(list.switch == 1){
                    count += 1
                }
            }
            if(count == 0) {
                viewModel.stopLocation()
                ToastFactory().toast(this,"there are no active profiles")
                println(it)
            }
        }

        binding.addProfileItem.setOnClickListener {
            ToastFactory().toast(this,"icon clicked")
            val profileDetails = ProfileBottomSheetDialog()
            profileDetails.show(supportFragmentManager,"Profile details")
        }

        val rv = binding.profilesRecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        viewModel.profileSettingArray.observe(this) {
            rv.adapter = RecyclerViewAdapter(it, this)
        }

    }

    override fun onPause() {
        super.onPause()
        koinContainer.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveList()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                viewModel.startLiveLocation()
            }else{
                ToastFactory().toast(this,"Permission are denied")
            }
        }

    }

}