package com.farawayship.library.search

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.farawayship.library.databinding.ActivitySearchFileBinding
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
    private lateinit var mBinding: ActivitySearchFileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySearchFileBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        mBinding.toolbar.setNavigationOnClickListener {
            finish()
        }

        searchResultAdapter = SearchResultAdapter(mutableListOf()) {
            intent.putExtra("file_path", it.path)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        mBinding.searchView.addTextChangedListener {
            it?.let { text ->
                searchViewModel.filter(text.toString())
            }
        }
        mBinding.resultRv.layoutManager = LinearLayoutManager(this)
        mBinding.resultRv.adapter = searchResultAdapter

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