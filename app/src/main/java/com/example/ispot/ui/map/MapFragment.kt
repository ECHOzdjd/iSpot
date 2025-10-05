package com.example.ispot.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.example.ispot.R
import com.example.ispot.databinding.FragmentMapBinding
import com.google.android.material.chip.Chip

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: MapViewModel
    private lateinit var aMap: AMap
    
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        
        binding.mapView.onCreate(savedInstanceState)
        
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化 ViewModel 的标记数据
        viewModel.initializeMarkers(requireContext())
        
        initMap()
        setupListeners()
        observeViewModel()
        
        // 初始化芯片状态
        updateChipStates()
        
        // 不自动请求位置权限，让用户主动选择
    }
    
    private fun initMap() {
        aMap = binding.mapView.map
        
        // 设置地图UI控件
        val uiSettings = aMap.uiSettings
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true
        
        // 设置地图初始位置（杭州）
        val defaultLocation = LatLng(30.2741, 120.1551)
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        
        // 添加调试信息：显示地图中心点
        android.util.Log.d("MapFragment", "地图中心点: ${defaultLocation.latitude}, ${defaultLocation.longitude}")
        
        // 设置地图标记点击监听器
        aMap.setOnMarkerClickListener { marker ->
            android.util.Log.d("MapFragment", "点击了标记: ${marker.title}")
            // 显示标记信息
            marker.showInfoWindow()
            true
        }
        
        // 设置地图加载完成监听器
        aMap.setOnMapLoadedListener {
            android.util.Log.d("MapFragment", "地图加载完成，开始添加标记")
            updateMapMarkers()
            
            // 强制显示所有标记 - 调整地图视野以包含所有标记
            showAllMarkers()
        }
        
        // 地图初始化完成后，立即显示所有标记
        updateMapMarkers()
    }
    
    private fun setupListeners() {
        // 我的位置按钮
        binding.fabMyLocation.setOnClickListener {
            moveToMyLocation()
        }
        
        // 过滤器芯片组
        binding.chipPeople.setOnClickListener {
            viewModel.togglePeopleFilter()
            updateChipStates()
            updateMapMarkers()
        }
        
        binding.chipActivities.setOnClickListener {
            viewModel.toggleActivitiesFilter()
            updateChipStates()
            updateMapMarkers()
        }
        
        binding.chipSpots.setOnClickListener {
            viewModel.toggleSpotsFilter()
            updateChipStates()
            updateMapMarkers()
        }
        
        // 搜索框
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchPlaces(requireContext(), it)
                }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
    
    private fun observeViewModel() {
        // 观察地图标记数据
        viewModel.mapMarkers.observe(viewLifecycleOwner) { _ ->
            updateMapMarkers()
        }
        
        // 观察搜索结果
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            // 显示搜索结果
            aMap.clear()
            results.forEach { place ->
                aMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(place.latitude, place.longitude))
                        .title(place.name)
                        .snippet(place.address)
                )
            }
            
            // 如果有结果，移动到第一个结果位置
            if (results.isNotEmpty()) {
                val firstResult = results.first()
                aMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(firstResult.latitude, firstResult.longitude),
                        15f
                    )
                )
            }
        }
    }
    
    private fun updateChipStates() {
        // 更新芯片的选中状态
        binding.chipPeople.isChecked = viewModel.isPeopleFilterEnabled()
        binding.chipActivities.isChecked = viewModel.isActivitiesFilterEnabled()
        binding.chipSpots.isChecked = viewModel.isSpotsFilterEnabled()
    }
    
    private fun updateMapMarkers() {
        // 清除现有标记
        aMap.clear()
        
        // 获取过滤后的标记
        val filteredMarkers = viewModel.getFilteredMarkers()
        
        // 添加调试信息
        android.util.Log.d("MapFragment", "更新地图标记，数量: ${filteredMarkers.size}")
        
        // 根据过滤器添加标记
        filteredMarkers.forEach { marker ->
            android.util.Log.d("MapFragment", "添加标记: ${marker.title} (${marker.latitude}, ${marker.longitude})")
            
            val markerOptions = MarkerOptions()
                .position(LatLng(marker.latitude, marker.longitude))
                .title(marker.title)
                .snippet(marker.description)
            
            // 如果图标不为空，则使用自定义图标，否则使用默认图标
            if (marker.icon != null) {
                markerOptions.icon(marker.icon)
            } else {
                // 使用默认图标，根据类型设置颜色
                val defaultIcon = when (marker.type) {
                    com.example.ispot.data.model.MapMarker.TYPE_PERSON -> 
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    com.example.ispot.data.model.MapMarker.TYPE_ACTIVITY -> 
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    com.example.ispot.data.model.MapMarker.TYPE_SPOT -> 
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    else -> 
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                }
                markerOptions.icon(defaultIcon)
            }
            
            val addedMarker = aMap.addMarker(markerOptions)
            android.util.Log.d("MapFragment", "标记添加成功: ${addedMarker?.title}, 位置: ${marker.latitude}, ${marker.longitude}")
        }
        
        // 添加调试信息：标记添加完成
        android.util.Log.d("MapFragment", "标记添加完成，共添加 ${filteredMarkers.size} 个标记")
        
        // 调整地图视野以显示所有标记
        showAllMarkers()
    }
    
    private fun showAllMarkers() {
        val filteredMarkers = viewModel.getFilteredMarkers()
        if (filteredMarkers.isNotEmpty()) {
            // 计算所有标记的边界
            val bounds = com.amap.api.maps.model.LatLngBounds.Builder()
            filteredMarkers.forEach { marker ->
                bounds.include(com.amap.api.maps.model.LatLng(marker.latitude, marker.longitude))
            }
            
            // 调整地图视野以显示所有标记
            val cameraUpdate = com.amap.api.maps.CameraUpdateFactory.newLatLngBounds(bounds.build(), 100)
            aMap.animateCamera(cameraUpdate)
            
            android.util.Log.d("MapFragment", "调整地图视野以显示所有标记，数量: ${filteredMarkers.size}")
        } else {
            android.util.Log.d("MapFragment", "没有标记需要显示")
        }
    }
    
    
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 设置定位蓝点
            val myLocationStyle = MyLocationStyle()
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
            myLocationStyle.interval(2000)
            aMap.myLocationStyle = myLocationStyle
            
            // 启用定位层
            aMap.isMyLocationEnabled = true
            
            // 获取当前位置但不自动移动地图
            viewModel.getCurrentLocation(requireContext()) { location ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    
                    // 只显示用户位置标记，不移动地图
                    aMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(latitude, longitude))
                            .title("我的位置")
                            .snippet("当前位置")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    )
                    
                    android.util.Log.d("MapFragment", "用户位置标记已添加: ($latitude, $longitude)")
                }
            }
        }
    }
    
    private fun moveToMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 先启用定位功能（只显示位置标记，不移动地图）
            enableMyLocation()
            
            // 然后移动到当前位置
            viewModel.getCurrentLocation(requireContext()) { location ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    
                    // 移动到用户位置
                    aMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(latitude, longitude),
                            15f
                        )
                    )
                    
                    android.util.Log.d("MapFragment", "地图已移动到用户位置: ($latitude, $longitude)")
                }
            }
        } else {
            // 如果没有权限，请求权限
            android.util.Log.d("MapFragment", "请求位置权限")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限授予后，移动到用户位置（因为这是用户主动点击定位按钮的结果）
                moveToMyLocation()
            } else {
                android.util.Log.d("MapFragment", "位置权限被拒绝")
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }
}