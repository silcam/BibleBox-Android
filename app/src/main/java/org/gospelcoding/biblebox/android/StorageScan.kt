package org.gospelcoding.biblebox.common

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File

const val TARGET_FILENAME = "BibleBox"
const val TARGET_IS_DIR = true

class StorageScan(
    private val context: Context,
    private val callback: (List<File>)->Unit
): AsyncTask <Void, Void, List<File>>() {
    override fun doInBackground(vararg p0: Void?): List<File> {
        Log.i("BibleBox", "Starting storage scan")
        val sdCardList = scan(removablePublicStorageRoot(context))
        val builtInStorageList = scan(nonRemovablePublicStorageRoot(context))
        Log.i("BibleBox", "Finished storage scan")
        return sdCardList + builtInStorageList
    }

    override fun onPostExecute(files: List<File>) = callback(files)

    private fun scan(root: File?): List<File> {
        if (root == null) return emptyList()

        val files = root.listFiles()
        if (files == null) return emptyList()

        return files.fold(emptyList(), {
            foundFiles, file ->
                when {
                    isMatch(file) -> foundFiles + file
                    file.isDirectory -> foundFiles + scan(file)
                    else -> foundFiles
                }
        })
    }

    private fun isMatch(file: File) =
        file.isDirectory == TARGET_IS_DIR &&
        file.name == TARGET_FILENAME

    private fun nonRemovablePublicStorageRoot(context: Context): File? {
        return publicStorageRoot(context, false)
    }

    private fun removablePublicStorageRoot(context: Context): File? {
        return publicStorageRoot(context, true)
    }

    private fun publicStorageRoot(context: Context, removable: Boolean): File? {
        if (Environment.isExternalStorageRemovable() == removable)
            return Environment.getExternalStorageDirectory()

        val appFilesDirs = context.getExternalFilesDirs(null)
        for (appFilesDir in appFilesDirs) {
            if (appFilesDir != null) {
                val root = storageRootFromAppFilesDir(appFilesDir)
                if (root != null && isRemovable(root) == removable)
                    return root
            }
        }
        return null
    }

    private fun isRemovable(dir: File): Boolean {
        if (Build.VERSION.SDK_INT >= 21)
            return Environment.isExternalStorageRemovable(dir)

        val defaultStorageRemovable = Environment.isExternalStorageRemovable()
        return if (dir.path.startsWith(Environment.getExternalStorageDirectory().path))
            defaultStorageRemovable
        else
            !defaultStorageRemovable
    }

    private fun storageRootFromAppFilesDir(appFilesDir: File): File? {
        // appStorageDir is a directory within the public storage with a path like
        // /path/to/public/storage/Android/data/org.sil.bloom.reader/files

        val path = appFilesDir.path
        val androidDirIndex = path.indexOf(File.separator + "Android" + File.separator)
        return if (androidDirIndex > 0) File(path.substring(0, androidDirIndex)) else null
    }
}