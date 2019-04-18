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
    private val callback: (File?)->Unit
): AsyncTask <Void, Void, File?>() {
    override fun doInBackground(vararg p0: Void?): File? {
        val sdCardFound =  scan(removablePublicStorageRoot(context))
        return sdCardFound ?: scan(nonRemovablePublicStorageRoot(context))
    }

    override fun onPostExecute(file: File?) = callback(file)

    private fun scan(root: File?): File? {
        if (root == null) return null

        val files = root.listFiles()
        if (files == null) return null

        for (file in files) {
            if (isMatch(file)) return file
            if (file.isDirectory) {
                val found = scan(file)
                if (found != null) return found
            }
        }
        return null
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