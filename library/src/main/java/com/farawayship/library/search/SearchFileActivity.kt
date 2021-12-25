package com.farawayship.library.search

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farawayship.library.R
import com.farawayship.library.enum.FileType
import com.farawayship.library.explorer.util.FilesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFileActivity : AppCompatActivity() {

    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var searchResultAdapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_file)

        searchResultAdapter = SearchResultAdapter(mutableListOf()) {
            intent.putExtra("file_path", it.path)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        findViewById<AppCompatEditText>(R.id.search_view).addTextChangedListener {
            it?.let {
                text -> searchViewModel.filter(text.toString())
            }
        }
        val searchRv = findViewById<RecyclerView>(R.id.result_rv)
        searchRv.layoutManager = LinearLayoutManager(this)
        searchRv.adapter = searchResultAdapter

        observe()
        searchViewModel.search(FileType.PDF)
    }

    private fun observe() {
        searchViewModel.searchResult.observe(this) {
            searchResultAdapter.update(it)
        }
    }
}