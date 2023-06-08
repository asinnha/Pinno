package com.example.shhh

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shhh.profile_sheet_dialog.ProfileBottomSheetDialog
import com.example.shhh.databinding.ActivityMainBinding
import com.example.shhh.project_data_models.ProfileSetting
import com.google.android.gms.location.LocationCallback
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.util.Collections
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    companion object{
        val RINGER_CONST = "RINGER_MODE_NORMAL"
        val VIBRATE_CONST = "RINGER_MODE_VIBRATE"
        val SILENT_CONST = "RINGER_MODE_SILENT"
    }

    lateinit var mList: ArrayList<ProfileSetting>
    lateinit var mAdapter: RecyclerViewAdapter
    lateinit var rv: RecyclerView

    lateinit var binding: ActivityMainBinding
    private val viewModel: LocationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startKoin {
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

        if (!isGPSEnabled(this)) {
            AlertDialog.Builder(this)
                .setTitle("Please Turn:ON your GPS")
                .setPositiveButton("OK"){dialog,_ ->
                    promptToEnableGPS(this)
                    dialog.dismiss()
                }
                .setNegativeButton("CANCEL"){dialog,_->
                    dialog.dismiss()
                }
                .setIcon(R.drawable.baseline_location_on)
                .create()
                .show()
        }else{
            viewModel.startLiveLocation()
        }

        viewModel.profileSettingArray.observe(this){

            mList = it as ArrayList<ProfileSetting>
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
            if(viewModel.gpsStatus){
                val profileDetails = ProfileBottomSheetDialog()
                profileDetails.show(supportFragmentManager, "Profile details")
            }else{
                viewModel.startLiveLocation()
                ToastFactory().toast(this,"Tap the Button Again")
            }
        }

        rv = binding.profilesRecyclerView
        itemTouchHelper.attachToRecyclerView(rv)
        rv.layoutManager = LinearLayoutManager(this)
        viewModel.profileSettingArray.observe(this) {
            mAdapter = RecyclerViewAdapter(it, this)
            rv.adapter = mAdapter
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveList()
        stopKoin()
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

    private val itemTouchListener = object: ItemTouchHelper.Callback(){
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(UP or DOWN,END)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val dragIndex:Int = viewHolder.adapterPosition
            val targetIndex:Int = target.adapterPosition
            Collections.swap(mList,dragIndex,targetIndex)
            mAdapter.notifyItemMoved(dragIndex,targetIndex)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            when(direction){
                END-> {
                    mList.removeAt(viewHolder.adapterPosition)
                    mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                    rv.adapter = mAdapter
                    ToastFactory().toast(this@MainActivity,"item removed...")
                }
            }
        }

    }
    private val itemTouchHelper = ItemTouchHelper(itemTouchListener)

    private fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun promptToEnableGPS(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}