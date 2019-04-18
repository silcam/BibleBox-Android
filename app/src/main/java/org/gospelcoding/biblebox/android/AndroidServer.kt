package org.gospelcoding.biblebox.android

import android.content.Context
import fi.iki.elonen.SimpleWebServer
import org.gospelcoding.biblebox.common.BibleBoxHTML
import java.io.*
import java.util.*

const val PORT = 8080

class AndroidServer(
    private val context: Context,
    private val rootDir: File
): SimpleWebServer(null, PORT, rootDir, true) {
    private val bbHTML = BibleBoxHTML()

    override fun start() {
        bbHTML.init(generateBibleBoxManifest(rootDir))
        if (!isAlive)
            super.start()
    }

    override fun serve(session: IHTTPSession?): Response {
        val uriPieces = session?.uri?.split("/") ?: emptyList()
        if (uriPieces.size < 2 || uriPieces[1]=="") return rootIndex()
        if (uriPieces.size >= 3 && uriPieces[1]=="static") return assetResponse(uriPieces[2])
        if (uriPieces.size >= 3 && uriPieces[1]=="languages") return langIndex(uriPieces[2])
        return super.serve(session)
    }

    private fun rootIndex() = newFixedLengthResponse(bbHTML.index())

    private fun langIndex(lang: String) = newFixedLengthResponse(bbHTML.langIndex(lang))

    private fun assetResponse(assetName: String): Response {
        val mgr = context.assets
        val fileStream = mgr.open("web-assets/$assetName")
        val scanner = Scanner(fileStream).useDelimiter("\\A")
        val content = if (scanner.hasNext()) scanner.next() else ""
        return newFixedLengthResponse(content)
    }
}