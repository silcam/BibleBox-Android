package org.gospelcoding.biblebox.common

class BibleBoxHTML {
    var bibleBoxManifest = BibleBoxManifest(emptyList())

    fun init(bibleBoxManifest: BibleBoxManifest) {
        this.bibleBoxManifest = bibleBoxManifest
    }

    fun index() = layout(indexHTML())

    fun langIndex(lang: String) = layout(langIndexHTML(lang))

    private fun layout(content: String) = """
        <html>
           <head>
               <title>BibleBox</title>
               <link rel="stylesheet" media="all" href="/static/biblebox.css" />
           </head>
           <body>
               $content
           </body>
        </html>
    """.trimIndent()

    private fun indexHTML() = """
        <h1>BibleBox Android</h1>
        ${langList()}
    """.trimIndent()

    private fun langIndexHTML(lang: String) = """
        <h1>$lang</h1>
        ${itemsTable(lang)}
    """.trimIndent()


    private fun langList() = bibleBoxManifest.languages.joinToString(
            prefix = "<ul>",
            postfix = "</ul>",
            separator = ""
        ) { langListRow(it) }

    private fun langListRow(lang: LanguageManifest) = """
        <li>
            <a href='/languages/${lang.name}'>
                ${lang.name}
            </a>
        </li>
    """.trimIndent()

    private fun itemsTable(lang: String): String {
        val langManifest = bibleBoxManifest.languages.find { it.name == lang }
        return if (langManifest == null)  "" else itemsTableHTML(langManifest)
    }

    private fun itemsTableHTML(lang: LanguageManifest) = """
        <table>
            <tbody>
                ${itemsTableHeaderRow("Apps")}
                ${lang.apps.joinToString("") { itemRow(it)}}
                ${itemsTableHeaderRow("Audio")}
                ${lang.audio.joinToString("") { audioItemRow(it) }}
                ${itemsTableHeaderRow("Films")}
                ${lang.films.joinToString("") { itemRow(it) }}
            </tbody>
        </table>
    """.trimIndent()

    private fun itemsTableHeaderRow(heading: String) = """
        <tr>
            <th>$heading:</th>
            <td></td>
        </tr>
    """.trimIndent()

    private fun itemRow(item: ManifestItem) = """
        <tr>
            <th></th>
            <td>
                <a href="${item.filepath}">${item.name}</a>
            </td>
        </tr>
    """.trimIndent()

    private fun audioItemRow(item: AudioManifest) = """
        <tr>
            <th></th>
            <td>${item.name}</td>
        </tr>
    """.trimIndent()
}