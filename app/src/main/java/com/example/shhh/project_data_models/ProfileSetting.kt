package com.example.shhh.project_data_models

data class ProfileSetting(
    val coordinates: Coordinates?=null,
    val title: String?=null,
    val soundProfile: String?=null,
    var switch: Int? = null
)
