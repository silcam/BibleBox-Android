package org.gospelcoding.biblebox.android

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.Manifest

enum class PermissionRequestCode(val code: Int) {
    TURN_ON_HOTSPOT(0),
    START_SERVER(1)
}


class MainActivity : AppCompatActivity() {
    private var server: AndroidServer = AndroidServer(this)

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
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PermissionRequestCode.START_SERVER.code
        )
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
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            PermissionRequestCode.TURN_ON_HOTSPOT.code
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            when (requestCode) {
                PermissionRequestCode.TURN_ON_HOTSPOT.code -> turnOnHotspot()
                PermissionRequestCode.START_SERVER.code -> server.start()
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