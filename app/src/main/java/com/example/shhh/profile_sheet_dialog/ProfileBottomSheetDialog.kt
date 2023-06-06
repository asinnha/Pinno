package com.example.shhh.profile_sheet_dialog

import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.shhh.project_data_models.Coordinates
import com.example.shhh.project_data_models.ProfileSetting
import com.example.shhh.LocationViewModel
import com.example.shhh.MainActivity
import com.example.shhh.R
import com.example.shhh.ToastFactory
import com.example.shhh.databinding.BottomSheetAddProfileSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileBottomSheetDialog : BottomSheetDialogFragment() {

    lateinit var latitude:String
    lateinit var longitude:String
    private lateinit var binding: BottomSheetAddProfileSettingsBinding
    lateinit var soundProfile: String
    private val viewModel: LocationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddProfileSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = binding.titleEdt
        val address = binding.addressTextView
        val discardBtn = binding.discardBtn
        val radioGroup = binding.radioGroup
        val saveBtn = binding.saveBtn

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.ringerBtn -> {
                    soundProfile = MainActivity.RINGER_CONST
                }
                R.id.silentBtn -> {
                    soundProfile = MainActivity.SILENT_CONST
                }
                R.id.vibrateBtn -> {
                    soundProfile = MainActivity.VIBRATE_CONST
                }
            }
        }

        viewModel.liveLatitude.observe(this) {
            println("live latitude in bottomSheetDialog ---> $it")
            latitude = it
        }

        viewModel.liveLongitude.observe(this) {
            println("live longitude in bottomSheetDialog ---> $it")
            longitude = it
        }



        address.text = viewModel.getAddress(requireContext())

        saveBtn.setOnClickListener {
//            if(soundProfile.isEmpty() || title.text.toString().isEmpty())

                val profileSetting = ProfileSetting(
                    Coordinates(latitude, longitude),
                    title.text.toString(),
                    soundProfile,
                    1
                )
                viewModel.addProfileSettingList(profileSetting)
                this.dismiss()
//            }
//            else{
//                context?.let { it1 -> ToastFactory().toast(it1,"Cannot leave fields empty") }
//            }
        }
        discardBtn.setOnClickListener {
            this.dismiss()
        }


    }

}