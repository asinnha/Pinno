package com.example.shhh

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shhh.profile_sheet_dialog.ProfileBottomSheetDialog
import com.example.shhh.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin


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
        viewModel.liveLocation

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

}