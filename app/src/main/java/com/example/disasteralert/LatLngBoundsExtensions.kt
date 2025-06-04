package com.example.disasteralert

// 화면 상의 카메라 뷰 안에 특정 지역이 들어왔는 지 판단하는 함수 (아직 사용하지 않음)

import com.google.android.gms.maps.model.LatLngBounds

fun LatLngBounds.intersects(other: LatLngBounds): Boolean {
    return !(other.southwest.latitude > this.northeast.latitude ||
            other.northeast.latitude < this.southwest.latitude ||
            other.southwest.longitude > this.northeast.longitude ||
            other.northeast.longitude < this.southwest.longitude)
}
