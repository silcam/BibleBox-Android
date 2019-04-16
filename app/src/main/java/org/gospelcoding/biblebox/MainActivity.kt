package org.gospelcoding.biblebox

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.net.wifi.WifiManager
import android.support.v4.app.ActivityCompat
import android.text.format.Formatter
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.Manifest


class MainActivity : AppCompatActivity() {
    private var server: AndroidServer = AndroidServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStop() {
        super.onStop()
        server.stop()
    }

    override fun onStart() {
        super.onStart()
        displayWifiInfo(Wifi(this).currentWifiSSID)
        server.start()
    }

    private fun displayWifiInfo(wifiInfo: Wifi.WifiInfo?) {
        val message = when (wifiInfo) {
            null -> "Not Connected"
            else -> "${wifiInfo.ssid}\n${wifiInfo.ip}\n${wifiInfo.password ?: ""}"
        }
//        val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress).toString()
        findViewById<TextView>(R.id.IpTextView).text = message
    }


    fun refreshWifiInfoButtonPress(button: View) {
        val wifiInfo = Wifi(this).currentWifiSSID
        displayWifiInfo(wifiInfo)
    }

    fun wifiToggleButtonPress(ignored: View) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 42)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            42 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    turnOnHotspot()
                }
            }
        }
    }

    private fun turnOnHotspot() {
        Hotspot(this).startHotspot {
            if (it == null)
                Toast
                    .makeText(
                        this,
                        "Couldn't start the Hotspot. Try going to the settings.",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            displayWifiInfo(it)
        }
    }
}

// 192.168.43.1 ? or
// 192.168.1.1