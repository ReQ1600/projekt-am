package com.lidar.projektam.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuItem(
    @StringRes val titleId: Int,
    @DrawableRes val imageId: Int,
    val index : Int
)
