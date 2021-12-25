package com.farawayship.library.search

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farawayship.library.R
import com.farawayship.library.enum.FileType

class SearchFileActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SearchFileActivity"

        /**
         * Extra file extension
         */
        const val EXTRA_EXT = "EXTRA_EXT"
    }

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
            it?.let { text ->
                searchViewModel.filter(text.toString())
            }
        }
        val searchRv = findViewById<RecyclerView>(R.id.result_rv)
        searchRv.layoutManager = LinearLayoutManager(this)
        searchRv.adapter = searchResultAdapter

        observe()

        val fileExt = intent.getStringExtra(EXTRA_EXT) ?: ""
        val fileType = when (fileExt.lowercase()) {
            "pdf" -> FileType.PDF
            else -> FileType.PDF
        }

        searchViewModel.search(fileType)
    }

    private fun observe() {
        searchViewModel.searchResult.observe(this) {
            searchResultAdapter.update(it)
        }
    }
}