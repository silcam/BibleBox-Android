package org.gospelcoding.biblebox.common

data class BibleBoxManifest(
    var languages: List<LanguageManifest> = emptyList()
)

data class LanguageManifest(
    val name: String,
    var films: List<ManifestItem> = emptyList(),
    var audio: List<AudioManifest> = emptyList(),
    var apps: List<ManifestItem> = emptyList()
)

data class AudioManifest(
    val name: String,
    val items: List<ManifestItem>
)

data class ManifestItem(
    val name: String,
    val filepath: String
)