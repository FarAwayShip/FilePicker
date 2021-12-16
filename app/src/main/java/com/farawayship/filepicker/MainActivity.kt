package com.farawayship.filepicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.farawayship.library.FileSelectActivity

class MainActivity : AppCompatActivity() {
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
        }
    }
}