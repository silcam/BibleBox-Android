package org.gospelcoding.biblebox.android

import fi.iki.elonen.NanoHTTPD
import org.gospelcoding.biblebox.common.Server

const val PORT = 8080

class AndroidServer: NanoHTTPD(PORT) {
    override fun serve(session: IHTTPSession?): Response {
        return newFixedLengthResponse(Server().index())
    }
}