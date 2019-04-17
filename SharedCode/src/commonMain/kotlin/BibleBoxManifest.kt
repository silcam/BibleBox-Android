package org.gospelcoding.biblebox.common

data class BibleBoxManifest(
    var languages: List<LanguageManifest>
)

data class LanguageManifest(
    val name: String,
    val items: List<ManifestItem>
)

data class ManifestItem(
    val name: String,
    val filepath: String
)