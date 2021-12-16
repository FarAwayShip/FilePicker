package com.farawayship.library.file_type

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.farawayship.library.R
import com.farawayship.library.file_type.fragment.*

class ShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)
        val transaction = supportFragmentManager.beginTransaction()
        val aClass = intent.getStringExtra("class")
        when (aClass) {
            "image" -> transaction.add(R.id.show_detial, ImageFragment())
            "music" -> transaction.add(R.id.show_detial, MusicFragment())
            "video" -> transaction.add(R.id.show_detial, VideoFragment())
            "word" -> transaction.add(R.id.show_detial, WordFragment())
            "apk" -> transaction.add(R.id.show_detial, ApkFragment())
            "zip" -> transaction.add(R.id.show_detial, ZipFragment())
            "filename" -> transaction.add(R.id.show_detial, FileNameFragment())
            "filetype" -> transaction.add(R.id.show_detial, FileTypeFragment())
        }
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}