package org.gospelcoding.biblebox.android

import org.gospelcoding.biblebox.common.AudioManifest
import org.gospelcoding.biblebox.common.BibleBoxManifest
import org.gospelcoding.biblebox.common.LanguageManifest
import org.gospelcoding.biblebox.common.ManifestItem
import java.io.File

fun generateBibleBoxManifest(rootDir: File): BibleBoxManifest {
    val bbManifest = BibleBoxManifest()
    scanRootDir(rootDir, bbManifest)
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
        scanLangDir(rootDir.absolutePath, langDir, langManifest)
    }
}

fun scanLangDir(rootPath: String, langDir: File, langManifest: LanguageManifest) {
    val files = langDir.listFiles()
    for (file in files) {
        when {
            hasMp3s(file) -> addAudioManifest(rootPath, langManifest, file)
            file.isDirectory -> scanLangDir(rootPath, file, langManifest)
            isFilm(file) -> addFilm(rootPath, langManifest, file)
            isApp(file) -> addApp(rootPath, langManifest, file)
        }
    }
}

fun hasMp3s(file: File) = file.isDirectory && file.listFiles().any{ it.extension == "mp3" }

fun isFilm(file: File) = arrayOf("3gp", "mp4").contains(file.extension)

fun isApp(file: File) = arrayOf("apk").contains(file.extension)

fun addAudioManifest(rootPath: String, langManifest: LanguageManifest, file: File) {
    val items = file
        .listFiles()
        .filter{ it.extension == "mp3" }
        .map{ManifestItem(it.name, relativePath(rootPath, it))}
    langManifest.audio += AudioManifest(file.name, items)
}

fun addFilm(rootPath: String, langManifest: LanguageManifest, file: File) {
    langManifest.films += ManifestItem(file.name, relativePath(rootPath, file))
}

fun addApp(rootPath: String, langManifest: LanguageManifest, file: File) {
    langManifest.apps += ManifestItem(file.name, relativePath(rootPath, file))
}

fun relativePath(rootPath: String, file: File) = file.absolutePath.substring(rootPath.length)
