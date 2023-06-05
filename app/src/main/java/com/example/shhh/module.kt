package com.example.shhh

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        LocationRepo(androidContext())
    }

    single {
        LiveLocationUpdates(androidContext(),get())
    }

    viewModel{
        LocationViewModel(get(),get())
    }
}