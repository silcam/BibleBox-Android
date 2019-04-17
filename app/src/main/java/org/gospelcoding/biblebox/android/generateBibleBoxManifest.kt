package org.gospelcoding.biblebox.android

import org.gospelcoding.biblebox.common.BibleBoxManifest
import org.gospelcoding.biblebox.common.LanguageManifest
import org.gospelcoding.biblebox.common.ManifestItem
import java.io.File

fun generateBibleBoxManifest(rootDirs: List<File>): BibleBoxManifest {
    val languages: List<LanguageManifest> = rootDirs.fold(
        emptyList(), {
            languages, rootDir ->
            mergeLangLists(languages, indexDir(rootDir))
        }
    )
    return BibleBoxManifest(languages.sortedBy { it.name })
}

fun mergeLangLists(list1: List<LanguageManifest>, list2: List<LanguageManifest>): List<LanguageManifest> {
    return list2.fold(
        list1, {
            totalList, langManifest ->
                when {
                    totalList.any{ it.name == langManifest.name } -> mergeInLang(totalList, langManifest)
                    else -> totalList + langManifest
                }
        }
    )
}

fun mergeInLang(list: List<LanguageManifest>, lang: LanguageManifest): List<LanguageManifest> {
    val existing = list.find{ it.name == lang.name }
    if (existing == null) return list
    val newLang = LanguageManifest(existing.name, existing.items + lang.items)
    return list.filter{ it.name != lang.name } + newLang
}

fun indexDir(dir: File): List<LanguageManifest> {
    val langDirs = dir.listFiles().filter{ it.isDirectory }
    return langDirs.map{
        langDir ->
            LanguageManifest(
                langDir.name,
                bbItems(langDir)
            )
    }
}

fun bbItems(langDir: File): List<ManifestItem> {
    return langDir.listFiles().fold(
        emptyList(), {
        items, file ->
            when {
                file.isDirectory -> items + bbItems(file)
                bbItemMatch(file) -> items + ManifestItem(file.nameWithoutExtension, file.absolutePath)
                else -> items
            }
        }
    )
}

fun bbItemMatch(file: File) = arrayOf("mp3", "3gp").contains(file.extension)