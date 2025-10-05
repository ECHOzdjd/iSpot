package com.example.ispot

import android.app.Application
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.MapsInitializer

class ISpotApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        

        // 这个 Key 必须与 AndroidManifest.xml 中的 Key 一致
        AMapLocationClient.setApiKey("78f29b21b03f123638b66fd5bba7a113")
        
        // 初始化高德地图SDK隐私合规
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)
        AMapLocationClient.updatePrivacyShow(this, true, true)
        AMapLocationClient.updatePrivacyAgree(this, true)
    }
}
