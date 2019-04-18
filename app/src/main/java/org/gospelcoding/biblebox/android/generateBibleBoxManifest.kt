package org.gospelcoding.biblebox.android

import org.gospelcoding.biblebox.common.AudioManifest
import org.gospelcoding.biblebox.common.BibleBoxManifest
import org.gospelcoding.biblebox.common.LanguageManifest
import org.gospelcoding.biblebox.common.ManifestItem
import java.io.File

fun generateBibleBoxManifest(rootDirs: List<File>): BibleBoxManifest {
    val bbManifest = BibleBoxManifest()
    for (rootDir in rootDirs) scanRootDir(rootDir, bbManifest)
    return bbManifest
}

fun scanRootDir(rootDir: File, bbManifest: BibleBoxManifest) {
    val langDirs = rootDir.listFiles().filter { it.isDirectory }
    for (langDir in langDirs) {
        var langManifest = bbManifest.languages.find { it.name == langDir.name }
        if (langManifest == null) {
            langManifest = LanguageManifest(langDir.name)
            bbManifest.languages += langManifest
        }
        scanLangDir(langDir, langManifest)
    }
}

fun scanLangDir(langDir: File, langManifest: LanguageManifest) {
    val files = langDir.listFiles()
    for (file in files) {
        when {
            hasMp3s(file) -> addAudioManifest(langManifest, file)
            file.isDirectory -> scanLangDir(file, langManifest)
            isFilm(file) -> addFilm(langManifest, file)
            isApp(file) -> addApp(langManifest, file)
        }
    }
}

fun hasMp3s(file: File) = file.isDirectory && file.listFiles().any{ it.extension == "mp3" }

fun isFilm(file: File) = arrayOf("3gp", "mp4").contains(file.extension)

fun isApp(file: File) = arrayOf("apk").contains(file.extension)

fun addAudioManifest(langManifest: LanguageManifest, file: File) {
    val items = file.listFiles().filter{ it.extension == "mp3" }.map{ManifestItem(it.name, it.absolutePath)}
    langManifest.audio += AudioManifest(file.name, items)
}

fun addFilm(langManifest: LanguageManifest, file: File) {
    langManifest.films += ManifestItem(file.name, file.absolutePath)
}

fun addApp(langManifest: LanguageManifest, file: File) {
    langManifest.apps += ManifestItem(file.name, file.absolutePath)
}
