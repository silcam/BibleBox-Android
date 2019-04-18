package org.gospelcoding.biblebox.android

import android.content.Context
import android.widget.Toast
import fi.iki.elonen.NanoHTTPD
import org.gospelcoding.biblebox.common.BibleBoxHTML
import org.gospelcoding.biblebox.common.StorageScan
import java.io.*
import java.util.*

const val PORT = 8080

class AndroidServer(val context: Context): NanoHTTPD(PORT) {
    private val bbHTML = BibleBoxHTML()

    override fun start() {
        super.start()
        StorageScan(context) {
            file ->
            if (file != null) bbHTML.init(generateBibleBoxManifest(file))
            else Toast.makeText(context, "No BibleBox folder found on device.", Toast.LENGTH_LONG).show()
        }.execute()
    }

    override fun serve(session: IHTTPSession?): Response {
        val uriPieces = session?.uri?.split("/") ?: emptyList()
        if (uriPieces.size < 2) return rootIndex()
        return when (uriPieces[1]) {
            "" -> rootIndex()
            "biblebox.css", "biblebox.js" -> assetResponse(uriPieces[1])
            else -> langIndex(uriPieces[1])
        }
    }

    private fun rootIndex() = newFixedLengthResponse(bbHTML.index())

    private fun langIndex(lang: String) = newFixedLengthResponse(bbHTML.langIndex(lang))

    private fun newFixedFileResponse(file: File): Response {
        val mime = getMimeTypeForFile(file.absolutePath)
        return newFixedLengthResponse(Response.Status.OK, mime, FileInputStream(file), file.length())
    }

    private fun assetResponse(assetName: String): Response {
        val mgr = context.assets
        val fileStream = mgr.open("web-assets/$assetName")
        val scanner = Scanner(fileStream).useDelimiter("\\A")
        val content = if (scanner.hasNext()) scanner.next() else ""
        return newFixedLengthResponse(content)
    }
}