package org.gospelcoding.biblebox.android

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.Manifest
import org.gospelcoding.biblebox.common.StorageScan
import java.io.File

enum class PermissionRequestCode(val code: Int) {
    TURN_ON_HOTSPOT(0),
    START_SERVER(1)
}

const val SHARED_PREFS_KEY = "org.gospelcoding.biblebox"
const val BIBLE_BOX_DIR_KEY = "bibleBoxDir"

class MainActivity : AppCompatActivity() {
    private var server: AndroidServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStop() {
        super.onStop()
        server?.stop()
    }

    override fun onStart() {
        super.onStart()
        displayWifiInfo(Wifi(this).currentWifiSSID)
        if (server != null) {
            server?.start()
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PermissionRequestCode.START_SERVER.code
            )
        }
    }

    private fun startServer() {
        getBibleBoxDir {
            rootDir ->
            if (rootDir == null) {
                Toast.makeText(
                    this,
                    "No BibleBox folder found on device.",
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                server = AndroidServer(this, rootDir)
                server?.start()
            }
        }

    }

    private fun getBibleBoxDir(callback: (File?)->Unit) {
        val prefs = getSharedPreferences(SHARED_PREFS_KEY, 0)
        val rootDirPath = prefs.getString(BIBLE_BOX_DIR_KEY, null)
        if (rootDirPath != null) {
            val rootDir = File(rootDirPath)
            if (rootDir.exists()) {
                callback(rootDir)
                return
            }
        }
        StorageScan(this) {
            rootDir ->
                callback(rootDir)
            if (rootDir != null) {
                val prefsEditor = prefs.edit()
                prefsEditor.putString(BIBLE_BOX_DIR_KEY, rootDir.absolutePath)
                prefsEditor.apply()
            }
        }.execute()
    }

    private fun displayWifiInfo(wifiInfo: Wifi.WifiInfo?) {
        val message = when (wifiInfo) {
            null -> "Not Connected"
            else -> "${wifiInfo.ssid.trim('"')}\n${wifiInfo.ip}:$PORT\n${wifiInfo.password ?: ""}"
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
                PermissionRequestCode.START_SERVER.code -> startServer()
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
