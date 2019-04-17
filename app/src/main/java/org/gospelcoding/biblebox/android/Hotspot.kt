package org.gospelcoding.biblebox.android

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.net.wifi.WifiConfiguration
import android.util.Log
import org.gospelcoding.biblebox.android.Wifi.WifiInfo

const val DEFAULT_HOTSPOT_IP = "192.168.43.1" // Sad to say this might be the best we can do
const val ALT_HOTSPOT_IP = "192.168.1.1" // It might be this too :(

class Hotspot(val context: Context) {
    fun existingHotspotInfo(): WifiInfo? {
        val hotspotConfig = getHotspotNameFromReflection()
        return when(hotspotConfig){
            null -> null
            else -> WifiInfo(hotspotConfig.SSID, DEFAULT_HOTSPOT_IP, hotspotConfig.preSharedKey)
        }
    }

    fun startHotspot(callback: (wifiInfo: WifiInfo?) -> Unit) {
        if (Build.VERSION.SDK_INT >= 26) {
            val wm = wifiManager()
            wm.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
                override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation) {
                    val config = reservation.wifiConfiguration
                    callback(WifiInfo(config.SSID, DEFAULT_HOTSPOT_IP, config.preSharedKey))
                }
            }, null)
        } else {
            callback(startHotspotWithReflection())
        }
    }

    private fun wifiManager() = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private fun getHotspotNameFromReflection():WifiConfiguration? {
        try {
            val wm = wifiManager()
            val isWifiApEnabled = wm.javaClass.getDeclaredMethod("isWifiApEnabled")
            isWifiApEnabled.isAccessible = true
            val hotspotOn = isWifiApEnabled.invoke(wm) as Boolean
            if (!hotspotOn) return null
            return getWifiApConfiguration(wm)
        } catch (error: Throwable) {
            Log.e("BibleBox", "Error in getHotspotStatusFromReflection")
            error.printStackTrace()
            return null
        }
    }

    private fun startHotspotWithReflection():WifiInfo? {
        val wm = wifiManager()
        val config = getWifiApConfiguration(wm)
        config.SSID = "BibleBox"
        config.preSharedKey = "biblebox"
        val success = setWifiApEnabled(wm, config, true)
        return if (success) WifiInfo("BibleBox", DEFAULT_HOTSPOT_IP, "biblebox")
        else null
    }

    private fun startHotspotWithApi() {

    }

    private fun getWifiApConfiguration(wm: WifiManager): WifiConfiguration {
        val method = wm.javaClass.getDeclaredMethod("getWifiApConfiguration")
        method.isAccessible = true
        return method.invoke(wm) as WifiConfiguration
    }

    private fun setWifiApEnabled(wm: WifiManager, config: WifiConfiguration?, enabled: Boolean): Boolean {
        try {
            val method = wm
                .javaClass
                .getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method.invoke(wm, config, enabled)
            return true
        } catch (error: Throwable) {
            Log.e("BibleBox", "Error in getHotspotStatusFromReflection")
            error.printStackTrace()
            return false
        }
    }
}