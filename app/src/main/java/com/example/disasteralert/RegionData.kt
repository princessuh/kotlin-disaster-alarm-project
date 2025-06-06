import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

// assets에 넣은 통합 GeoJSON 파일명
const val GEOJSON_FILE = "TL_SCCO_CTPRVN.json"


// [기능] 지역 고유 ID(행정코드)와 대략적인 지도 경계(BoundingBox)를 저장하는 데이터 클래스

data class RegionData(
    val id: String,           // CTPRVN_CD
    val boundingBox: LatLngBounds
)

val regionDataList = listOf(
    RegionData(
        id = "11",            // 서울특별시
        boundingBox = LatLngBounds(
            LatLng(37.5665 - 0.3, 126.9780 - 0.3),
            LatLng(37.5665 + 0.3, 126.9780 + 0.3)
        )
    ),
    RegionData(
        id = "26",            // 부산광역시
        boundingBox = LatLngBounds(
            LatLng(35.1796 - 0.3, 129.0756 - 0.3),
            LatLng(35.1796 + 0.3, 129.0756 + 0.3)
        )
    ),
    RegionData(
        id = "27",            // 대구광역시
        boundingBox = LatLngBounds(
            LatLng(35.8714 - 0.3, 128.6014 - 0.3),
            LatLng(35.8714 + 0.3, 128.6014 + 0.3)
        )
    ),
    RegionData(
        id = "28",            // 인천광역시
        boundingBox = LatLngBounds(
            LatLng(37.4563 - 0.3, 126.7052 - 0.3),
            LatLng(37.4563 + 0.3, 126.7052 + 0.3)
        )
    ),
    RegionData(
        id = "29",            // 광주광역시
        boundingBox = LatLngBounds(
            LatLng(35.1595 - 0.3, 126.8526 - 0.3),
            LatLng(35.1595 + 0.3, 126.8526 + 0.3)
        )
    ),
    RegionData(
        id = "30",            // 대전광역시
        boundingBox = LatLngBounds(
            LatLng(36.3504 - 0.3, 127.3845 - 0.3),
            LatLng(36.3504 + 0.3, 127.3845 + 0.3)
        )
    ),
    RegionData(
        id = "31",            // 울산광역시
        boundingBox = LatLngBounds(
            LatLng(35.5384 - 0.3, 129.3114 - 0.3),
            LatLng(35.5384 + 0.3, 129.3114 + 0.3)
        )
    ),
    RegionData(
        id = "36",            // 세종특별자치시
        boundingBox = LatLngBounds(
            LatLng(36.4800 - 0.3, 127.2890 - 0.3),
            LatLng(36.4800 + 0.3, 127.2890 + 0.3)
        )
    ),
    RegionData(
        id = "41",            // 경기도
        boundingBox = LatLngBounds(
            LatLng(37.4363 - 0.3, 127.5508 - 0.3),
            LatLng(37.4363 + 0.3, 127.5508 + 0.3)
        )
    ),
    RegionData(
        id = "42",            // 강원도
        boundingBox = LatLngBounds(
            LatLng(37.8228 - 0.3, 128.1555 - 0.3),
            LatLng(37.8228 + 0.3, 128.1555 + 0.3)
        )
    ),
    RegionData(
        id = "43",            // 충청북도
        boundingBox = LatLngBounds(
            LatLng(36.6357 - 0.3, 127.4912 - 0.3),
            LatLng(36.6357 + 0.3, 127.4912 + 0.3)
        )
    ),
    RegionData(
        id = "44",            // 충청남도
        boundingBox = LatLngBounds(
            LatLng(36.5184 - 0.3, 126.8000 - 0.3),
            LatLng(36.5184 + 0.3, 126.8000 + 0.3)
        )
    ),
    RegionData(
        id = "45",            // 전라북도
        boundingBox = LatLngBounds(
            LatLng(35.7175 - 0.3, 127.1530 - 0.3),
            LatLng(35.7175 + 0.3, 127.1530 + 0.3)
        )
    ),
    RegionData(
        id = "46",            // 전라남도
        boundingBox = LatLngBounds(
            LatLng(34.8679 - 0.3, 126.9910 - 0.3),
            LatLng(34.8679 + 0.3, 126.9910 + 0.3)
        )
    ),
    RegionData(
        id = "47",            // 경상북도
        boundingBox = LatLngBounds(
            LatLng(36.5760 - 0.3, 128.5056 - 0.3),
            LatLng(36.5760 + 0.3, 128.5056 + 0.3)
        )
    ),
    RegionData(
        id = "48",            // 경상남도
        boundingBox = LatLngBounds(
            LatLng(35.2383 - 0.3, 128.6921 - 0.3),
            LatLng(35.2383 + 0.3, 128.6921 + 0.3)
        )
    ),
    RegionData(
        id = "50",            // 제주특별자치도
        boundingBox = LatLngBounds(
            LatLng(33.4996 - 0.3, 126.5312 - 0.3),
            LatLng(33.4996 + 0.3, 126.5312 + 0.3)
        )
    )
)
