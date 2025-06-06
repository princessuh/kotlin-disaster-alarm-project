// GeoJsonManager.kt
package com.example.disasteralert

import RegionData
import android.content.Context
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

// [기능] GeoJSON 데이터를 로딩하여 지도에 행정구역별 폴리곤을 표시하고 관리하는 클래스

class GeoJsonManager(
    private val context: Context,
    private val googleMap: GoogleMap,
    private val regionDataList: List<RegionData>
) {
    companion object {
        private const val TAG = "GeoJsonManager"
        private const val GEOJSON_FILE = "TL_SCCO_CTPRVN.json" // GeoJson 파일 이름
    }

    private val loadedLayers = mutableMapOf<String, GeoJsonLayer>()

    fun getLoadedCount(): Int = loadedLayers.size

    suspend fun showRegionAsync(region: RegionData) {
        Log.d(TAG, "▶ showRegionAsync(${region.id})")
        try {
            // 1) 통합 GeoJSON 읽기
            val fullJson = withContext(Dispatchers.IO) {
                context.assets.open(GEOJSON_FILE).use { s ->
                    JSONObject(s.bufferedReader().readText())
                }
            }

            // 2) 해당 region.id에 맞는 feature만 필터링
            val filteredJson = filterGeoJsonByRegion(fullJson, region.id)

            // 3) 메인 스레드에서 레이어 만들고 지도에 추가
            withContext(Dispatchers.Main) {
                val layer = GeoJsonLayer(googleMap, filteredJson)
                layer.defaultPolygonStyle.apply {
                    strokeWidth = 3f
                    fillColor   = 0x5500FF00 // 폴리곤 색상
                }
                layer.addLayerToMap()
                loadedLayers[region.id] = layer
                Log.d(TAG, "✅ loaded '${region.id}', count=${loadedLayers.size}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ failed to load ${region.id}", e)
        }
    }


    // GeoJson에서 특정 지역 피처 추출
    private fun filterGeoJsonByRegion(fullJson: JSONObject, regionId: String): JSONObject {
        val features = fullJson.getJSONArray("features")
        val filtered = JSONArray()
        for (i in 0 until features.length()) {
            val feat = features.getJSONObject(i)
            val props = feat.optJSONObject("properties")
            if (props?.optString("CTPRVN_CD") == regionId) {
                filtered.put(feat)
            }
        }
        return JSONObject().apply {
            put("type", "FeatureCollection")
            put("features", filtered)
        }
    }

    // 모든 레이어 제거 및 캐시 초기화
    fun removeAllLayers() {
        loadedLayers.values.forEach { it.removeLayerFromMap() }
        loadedLayers.clear()
    }

    // 모든 레이어 임시 숨김 (캐시 유지)
    fun hideAllLayers() {
        loadedLayers.values.forEach { it.removeLayerFromMap() }
    }

    // 모든 숨겨진 레이어 지도에 표시
    fun showAllLayers() {
        loadedLayers.values.forEach { it.addLayerToMap() }
    }

    // 특정 지역의 레이가 이미 로딩되어 있는 지 확인
    fun isLoaded(regionId: String): Boolean = loadedLayers.containsKey(regionId)
}
