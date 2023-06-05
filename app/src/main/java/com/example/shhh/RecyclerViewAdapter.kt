package com.example.shhh

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shhh.project_data_models.ProfileSetting
import com.example.shhh.databinding.ProfileModalItemBinding

class RecyclerViewAdapter(private val profileSettingArray: List<ProfileSetting>, private val context: Context)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>()
{

    class ViewHolder(private val binding: ProfileModalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage = binding.iconBtn
        val profileTitle = binding.profileName
        val profileSwitch = binding.profileSwitch
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProfileModalItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return profileSettingArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.profileTitle.text = profileSettingArray[position].title
        holder.profileImage.setIconResource(R.drawable.baseline_work_24)
        if(profileSettingArray[position].switch == 1){
            holder.profileSwitch.backgroundTintList = ContextCompat.getColorStateList(context,R.color.green)
        }else{
            holder.profileSwitch.backgroundTintList = ContextCompat.getColorStateList(context,R.color.red)
        }
        holder.profileSwitch.setOnClickListener {
            if(profileSettingArray[position].switch == 1){
                profileSettingArray[position].switch = 0
                holder.profileSwitch.backgroundTintList = ContextCompat.getColorStateList(context,R.color.red)

            }else{
                profileSettingArray[position].switch = 1
                holder.profileSwitch.backgroundTintList = ContextCompat.getColorStateList(context,R.color.green)
            }
        }
    }

}