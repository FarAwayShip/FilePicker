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

class SearchViewModel : ViewModel() {

    companion object {
        val TAG = SearchViewModel::class.simpleName
    }

    private val _searchResults = MutableLiveData<List<SearchResult>>(emptyList())
    val searchResult: LiveData<List<SearchResult>> = _searchResults
    private val _allFile: MutableList<SearchResult> = mutableListOf()

    private val _showLoading = MutableLiveData(false)
    val showLoading: LiveData<Boolean> = _showLoading

    fun search(fileType: FileType) {
        val extension = when (fileType) {
            FileType.PDF -> "pdf"
            FileType.TEXT -> "txt"
            else -> "pdf"
        }
        _showLoading.value = true
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
            _showLoading.postValue(false)
        }
    }

    fun filter(text: String) {
        val formattedText = text.trim().lowercase()
        viewModelScope.launch(Dispatchers.Default) {
            val filterResult = _allFile.filter { f ->
                f.name.lowercase().contains(formattedText)
            }
            _searchResults.postValue(filterResult)
        }
    }
}