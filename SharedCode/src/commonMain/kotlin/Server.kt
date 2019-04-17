package org.gospelcoding.biblebox.common

class BibleBoxServer {
    var bibleBoxManifest = BibleBoxManifest(emptyList())

    fun index() = "<html><body><h1>Hello, Android!</h1>${makeTable()}</body></html>"

    fun init(bibleBoxManifest: BibleBoxManifest) {
        this.bibleBoxManifest = bibleBoxManifest
    }

    private fun makeTable(): String {
        return "<table><tbody>" +
                bibleBoxManifest.languages.joinToString(separator = ""){
                    languageManifest ->
                        "<tr><th>${languageManifest.name}</th><td></td></tr>" +
                        languageManifest.items.joinToString(separator = "") {
                            "<tr><th></th><td>${it.name}</td></tr>"
                        }
                } +
                "</tbody></table>"
    }
}