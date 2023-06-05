package com.example.shhh

import android.content.Context
import android.widget.Toast

class ToastFactory{

    fun toast(context: Context,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
}