package com.example.ispot.data.model

import com.amap.api.maps.model.BitmapDescriptor

data class MapMarker(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String,
    val type: Int,
    val icon: BitmapDescriptor
) {
    companion object {
        const val TYPE_PERSON = 1
        const val TYPE_ACTIVITY = 2
        const val TYPE_SPOT = 3
    }
}