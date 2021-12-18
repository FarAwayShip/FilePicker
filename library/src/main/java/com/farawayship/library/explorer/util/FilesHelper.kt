package com.farawayship.library.explorer.util

import android.util.Log
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


object FilesHelper {
    private const val TAG = "FilesHelper"

    /**
     * Load all files from a directory, specify file extension if want
     */
    fun loadAllFiles(path: String, fileExtensions: Array<String> = emptyArray()): List<File> {
        Log.i(TAG, "loadAllFiles from: $path ...")
        val rootFile = File(path)
        return loadAllFiles(rootFile, fileExtensions)
    }

    /**
     * Load all files from a directory File, with recursion
     */

    private fun loadAllFiles(
        directoryFile: File,
        fileExtensions: Array<String> = emptyArray()
    ): List<File> {
//        Log.d(TAG, "Load folder: ${directoryFile.name}")
        val result = ArrayList<File>()
        if (directoryFile.isDirectory) {
            directoryFile.listFiles()?.let { it ->
                for (file in it) {
                    if (file.isDirectory) {
                        result.addAll(loadAllFiles(file, fileExtensions))
                    } else {
                        if (file.endWiths(fileExtensions)) {
                            result.add(file)
                        }
                    }
                }
            }
        } else {
            return emptyList()
        }
        return result
    }

    /**
     * Check file end with one of extensions specified in a list
     */
    private fun File.endWiths(exts: Array<String>): Boolean {
        Log.i(TAG, "Check file: $name")
        val fileName = name.lowercase()
        for (ext in exts) {
            if (fileName.endsWith(".${ext.lowercase()}")) return true
        }
        return false
    }
}