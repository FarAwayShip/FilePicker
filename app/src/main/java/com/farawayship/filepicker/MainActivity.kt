package com.farawayship.filepicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.farawayship.library.FileSelectActivity
import com.farawayship.library.explorer.util.FilesHelper
import com.farawayship.library.search.SearchFileActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: android.view.View) {
        when (view.id) {
            R.id.btn_tree -> {
                val intent = Intent(this, FileSelectActivity::class.java)
                intent.putExtra(FileSelectActivity.EXTRA_MODE, FileSelectActivity.MODE_FILE_TREE)
                intent.putExtra(FileSelectActivity.EXTRA_EXTS, arrayOf("pdf"))
                startActivity(intent)
            }
            R.id.btn_normal -> {
                val intent = Intent(this, FileSelectActivity::class.java)
                intent.putExtra(FileSelectActivity.EXTRA_MODE, FileSelectActivity.MODE_NORMAL)
                startActivity(intent)
            }

            R.id.btn_loadFile -> {
                val intent = Intent(this, SearchFileActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadFiles() {
        lifecycleScope.launch {
            val files = FilesHelper.loadAllFiles(
                Environment.getExternalStorageDirectory().path,
                arrayOf("pdf")
            )
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "Load file success with ${files.size} files",
                    Toast.LENGTH_SHORT
                ).show()
            }
            files.forEach {
                Log.d(TAG, "File name: ${it.name}")
            }
        }
    }
}