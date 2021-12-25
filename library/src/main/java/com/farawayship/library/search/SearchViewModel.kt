package com.farawayship.library.search

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farawayship.library.enum.FileType
import com.farawayship.library.explorer.util.FilesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {

    companion object {
        val TAG = SearchViewModel::class.simpleName
    }

    private val _searchResults = MutableLiveData<List<SearchResult>>(emptyList())
    val searchResult: LiveData<List<SearchResult>> = _searchResults
    private val _allFile: MutableList<SearchResult> = mutableListOf()

    fun search(fileType: FileType) {
        val extension = when (fileType) {
            FileType.PDF -> "pdf"
            FileType.TEXT -> "txt"
            else -> "pdf"
        }
        viewModelScope.launch(Dispatchers.IO) {
            val files = FilesHelper.loadAllFiles(
                Environment.getExternalStorageDirectory().path,
                arrayOf(extension)
            )

            Log.d(TAG, "files: ${files.size}")
            val results = files.map { f ->
                SearchResult(
                    path = f.absolutePath,
                    name = f.nameWithoutExtension,
                    fileType,
                    capacity = f.length()
                )
            }
            _allFile.clear()
            _allFile.addAll(results)

            _searchResults.postValue(results)
        }
    }

    fun filter(text: String) {
        val formattedText = text.trim().lowercase()

        Log.d(TAG, "text: $formattedText - ${_allFile.size}")

        viewModelScope.launch(Dispatchers.Default) {
            val filterResult = _allFile.filter { f ->

                Log.d(TAG, "file name: ${f.name.lowercase()}")

                f.name.lowercase().contains(formattedText)
            }
            _searchResults.postValue(filterResult)
        }
    }
}