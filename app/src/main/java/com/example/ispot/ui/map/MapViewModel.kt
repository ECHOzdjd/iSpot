package com.example.ispot.ui.map

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.example.ispot.R
import com.example.ispot.data.model.MapMarker
import com.example.ispot.data.model.Place

@Suppress("DEPRECATION")
class MapViewModel : ViewModel() {

    // 过滤器状态
    private var showPeople = false
    private var showActivities = false
    private var showSpots = false

    // 地图标记数据
    private val _mapMarkers = MutableLiveData<List<MapMarker>>()
    val mapMarkers: LiveData<List<MapMarker>> = _mapMarkers

    // 搜索结果
    private val _searchResults = MutableLiveData<List<Place>>()
    val searchResults: LiveData<List<Place>> = _searchResults

    // 标记列表（延迟初始化）
    private var peopleMarkers: List<MapMarker> = emptyList()
    private var activityMarkers: List<MapMarker> = emptyList()
    private var spotMarkers: List<MapMarker> = emptyList()
    private var isInitialized = false

    // 初始化标记数据（需要 Context）
    @Suppress("UNUSED_PARAMETER")
    fun initializeMarkers(context: Context) {
        if (isInitialized) return
        
        // 创建图标 - 使用默认标记（不同颜色区分类型）
        // 红色：附近的人，绿色：活动，紫色：兴趣点
        val personIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        val activityIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        val spotIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        
        // 确保图标不为空
        if (personIcon == null || activityIcon == null || spotIcon == null) {
            android.util.Log.e("MapViewModel", "图标创建失败，使用默认图标")
            return
        }
        
        // 创建固定的标记数据（杭州地区）
        peopleMarkers = listOf(
            MapMarker(
                id = "person1",
                latitude = 30.2741,
                longitude = 120.1551,
                title = "张三",
                description = "喜欢摄影",
                type = MapMarker.TYPE_PERSON,
                icon = personIcon
            ),
            MapMarker(
                id = "person2",
                latitude = 30.2841,
                longitude = 120.1651,
                title = "李四",
                description = "运动爱好者",
                type = MapMarker.TYPE_PERSON,
                icon = personIcon
            ),
            MapMarker(
                id = "person3",
                latitude = 30.2641,
                longitude = 120.1451,
                title = "王五",
                description = "美食达人",
                type = MapMarker.TYPE_PERSON,
                icon = personIcon
            )
        )
        
        activityMarkers = listOf(
            MapMarker(
                id = "activity1",
                latitude = 30.2741,
                longitude = 120.1751,
                title = "晨跑活动",
                description = "每天早上6点",
                type = MapMarker.TYPE_ACTIVITY,
                icon = activityIcon
            ),
            MapMarker(
                id = "activity2",
                latitude = 30.2941,
                longitude = 120.1551,
                title = "摄影聚会",
                description = "周末摄影活动",
                type = MapMarker.TYPE_ACTIVITY,
                icon = activityIcon
            ),
            MapMarker(
                id = "activity3",
                latitude = 30.2541,
                longitude = 120.1551,
                title = "读书会",
                description = "每周三晚上",
                type = MapMarker.TYPE_ACTIVITY,
                icon = activityIcon
            )
        )
        
        spotMarkers = listOf(
            MapMarker(
                id = "spot1",
                latitude = 30.2741,
                longitude = 120.1351,
                title = "咖啡厅",
                description = "环境优雅",
                type = MapMarker.TYPE_SPOT,
                icon = spotIcon
            ),
            MapMarker(
                id = "spot2",
                latitude = 30.3041,
                longitude = 120.1551,
                title = "西湖公园",
                description = "适合散步",
                type = MapMarker.TYPE_SPOT,
                icon = spotIcon
            ),
            MapMarker(
                id = "spot3",
                latitude = 30.2441,
                longitude = 120.1551,
                title = "图书馆",
                description = "安静学习",
                type = MapMarker.TYPE_SPOT,
                icon = spotIcon
            ),
            MapMarker(
                id = "spot4",
                latitude = 30.2741,
                longitude = 120.1851,
                title = "健身房",
                description = "设备齐全",
                type = MapMarker.TYPE_SPOT,
                icon = spotIcon
            )
        )
        
        isInitialized = true
        val allMarkers = peopleMarkers + activityMarkers + spotMarkers
        _mapMarkers.value = allMarkers
        
        // 添加调试信息
        android.util.Log.d("MapViewModel", "标记初始化完成，共 ${allMarkers.size} 个标记")
    }
    

    // 切换过滤器状态
    fun togglePeopleFilter() {
        showPeople = !showPeople
        android.util.Log.d("MapViewModel", "切换人员过滤器: $showPeople")
    }

    fun toggleActivitiesFilter() {
        showActivities = !showActivities
        android.util.Log.d("MapViewModel", "切换活动过滤器: $showActivities")
    }

    fun toggleSpotsFilter() {
        showSpots = !showSpots
        android.util.Log.d("MapViewModel", "切换兴趣点过滤器: $showSpots")
    }
    
    // 查询过滤器状态
    fun isPeopleFilterEnabled(): Boolean = showPeople
    fun isActivitiesFilterEnabled(): Boolean = showActivities
    fun isSpotsFilterEnabled(): Boolean = showSpots

    // 获取过滤后的标记
    fun getFilteredMarkers(): List<MapMarker> {
        val allMarkers = _mapMarkers.value ?: emptyList()
        
        android.util.Log.d("MapViewModel", "获取过滤标记 - 总标记数: ${allMarkers.size}")
        android.util.Log.d("MapViewModel", "过滤器状态 - 人员: $showPeople, 活动: $showActivities, 兴趣点: $showSpots")
        
        // 如果没有任何过滤器被选中，显示所有标记
        if (!showPeople && !showActivities && !showSpots) {
            android.util.Log.d("MapViewModel", "没有过滤器选中，显示所有标记")
            return allMarkers
        }
        
        // 根据选中的过滤器显示对应的标记
        val filteredMarkers = mutableListOf<MapMarker>()
        
        if (showPeople) {
            val peopleMarkers = allMarkers.filter { it.type == MapMarker.TYPE_PERSON }
            filteredMarkers.addAll(peopleMarkers)
            android.util.Log.d("MapViewModel", "添加人员标记: ${peopleMarkers.size} 个")
        }
        if (showActivities) {
            val activityMarkers = allMarkers.filter { it.type == MapMarker.TYPE_ACTIVITY }
            filteredMarkers.addAll(activityMarkers)
            android.util.Log.d("MapViewModel", "添加活动标记: ${activityMarkers.size} 个")
        }
        if (showSpots) {
            val spotMarkers = allMarkers.filter { it.type == MapMarker.TYPE_SPOT }
            filteredMarkers.addAll(spotMarkers)
            android.util.Log.d("MapViewModel", "添加兴趣点标记: ${spotMarkers.size} 个")
        }
        
        android.util.Log.d("MapViewModel", "过滤后标记总数: ${filteredMarkers.size}")
        return filteredMarkers
    }

    // 搜索地点
    fun searchPlaces(context: Context, keyword: String) {
        // 使用高德地图POI搜索
        val query = PoiSearch.Query(keyword, "", "")
        query.pageSize = 10
        query.pageNum = 0
        
        val poiSearch = PoiSearch(context, query)
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiSearched(result: PoiResult?, rCode: Int) {
                if (rCode == 1000 && result != null) {
                    val places = result.pois.map { poiItem ->
                        Place(
                            id = poiItem.poiId,
                            name = poiItem.title,
                            address = poiItem.snippet,
                            latitude = poiItem.latLonPoint.latitude,
                            longitude = poiItem.latLonPoint.longitude
                        )
                    }
                    _searchResults.postValue(places)
                } else {
                    _searchResults.postValue(emptyList())
                }
            }

            override fun onPoiItemSearched(poiItem: PoiItem?, rCode: Int) {
                // 不处理单个POI搜索
            }
        })
        poiSearch.searchPOIAsyn()
    }

    // 获取当前位置
    fun getCurrentLocation(context: Context, callback: (Location?) -> Unit) {
        try {
            AMapLocationClient.updatePrivacyShow(context, true, true)
            AMapLocationClient.updatePrivacyAgree(context, true)
            
            val locationClient = AMapLocationClient(context)
            val locationOption = AMapLocationClientOption()
            locationOption.isOnceLocation = true
            locationOption.isNeedAddress = true
            locationClient.setLocationOption(locationOption)
            
            locationClient.setLocationListener { location ->
                if (location != null && location.errorCode == 0) {
                    val result = Location("AMap")
                    result.latitude = location.latitude
                    result.longitude = location.longitude
                    callback(result)
                } else {
                    callback(null)
                }
                locationClient.stopLocation()
                locationClient.onDestroy()
            }
            
            locationClient.startLocation()
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }
}