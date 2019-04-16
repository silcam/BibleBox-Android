package org.gospelcoding.biblebox

import fi.iki.elonen.NanoHTTPD

const val PORT = 8080

class AndroidServer: NanoHTTPD(PORT) {
    override fun serve(session: IHTTPSession?): Response {
        return newFixedLengthResponse("<html><body><h1>Hello, Android!</h1></body></html>")
    }
}