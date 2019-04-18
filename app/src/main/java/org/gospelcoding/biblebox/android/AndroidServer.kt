package org.gospelcoding.biblebox.android

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import org.gospelcoding.biblebox.common.BibleBoxServer
import org.gospelcoding.biblebox.common.StorageScan

const val PORT = 8080

class AndroidServer: NanoHTTPD(PORT) {
    private val bbServer = BibleBoxServer()

    override fun start() {
        throw Throwable("Use start(context: Context) instead.")
    }

    fun start(context: Context) {
        super.start()
        StorageScan(context) {
            files ->
                bbServer.init(generateBibleBoxManifest(files))
        }.execute()
    }

    override fun serve(session: IHTTPSession?): Response {
        val response = when (session?.uri) {
            "/", null -> bbServer.index()
            else -> bbServer.langIndex(session.uri.substring(1))
        }
        return newFixedLengthResponse(response)
    }
}