package org.gospelcoding.biblebox.common

data class BibleBoxManifest(
    var languages: List<LanguageManifest> = emptyList()
)

data class LanguageManifest(
    val name: String,
    var films: List<ManifestItem> = emptyList(),
    var audio: List<ManifestItem> = emptyList(),
    var apps: List<ManifestItem> = emptyList()
)

data class ManifestItem(
    val name: String,
    val filepath: String
)