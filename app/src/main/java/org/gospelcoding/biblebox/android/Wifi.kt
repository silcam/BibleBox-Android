package org.gospelcoding.biblebox.android

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter

class Wifi(val context: Context) {
    val currentWifiSSID: WifiInfo? get() {
        val wm = wifiManager()
        return if (wm.isWifiEnabled) {
            val ip = Formatter.formatIpAddress(wm.connectionInfo.ipAddress).toString()
            WifiInfo(wm.connectionInfo.ssid, ip)
        }
        else Hotspot(context).existingHotspotInfo()
    }

    private fun wifiManager() = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    data class WifiInfo(val ssid: String, val ip: String, val password: String? = null)
}