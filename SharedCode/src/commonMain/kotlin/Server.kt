package org.gospelcoding.biblebox.common

class BibleBoxServer {
    var bibleBoxManifest = BibleBoxManifest(emptyList())

    fun index() = "<html><body><h1>Hello, Android!</h1>${langList()}</body></html>"

    fun langIndex(lang: String) = "<html><body><h1>$lang</h1>${itemsTable(lang)}</body></html>"

    fun init(bibleBoxManifest: BibleBoxManifest) {
        this.bibleBoxManifest = bibleBoxManifest
    }

    private fun langList(): String {
        return bibleBoxManifest.languages.joinToString(
            prefix = "<ul>",
            postfix = "</ul>",
            separator = ""
        ) { "<li><a href='/${it.name}'>${it.name}</a></li>" }
    }

    private fun itemsTable(lang: String): String {
        val langManifest = bibleBoxManifest.languages.find { it.name == lang }
        if (langManifest == null) return ""

        return "<table><tbody>" +
                "<tr><th>Apps:</th><td></td></tr>" +
                langManifest.apps.joinToString(separator = "") { "<tr><th></th><td>${it.name}</td></tr>" } +
                "<tr><th>Audio:</th><td></td></tr>" +
                langManifest.audio.joinToString(separator = "") { "<tr><th></th><td>${it.name}</td></tr>" } +
                "<tr><th>Films:</th><td></td></tr>" +
                langManifest.films.joinToString(separator = "") { "<tr><th></th><td>${it.name}</td></tr>" } +
                "</tbody></table>"
    }
}