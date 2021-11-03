package com.farawayship.library.file_type

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

class FileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityFileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_file)
        setup()
    }

    private fun setup() {
        mBinding.layoutZip.setOnClickListener(this)
        mBinding.layoutApk.setOnClickListener(this)
        mBinding.layoutVideo.setOnClickListener(this)
        mBinding.layoutMusic.setOnClickListener(this)
        mBinding.layoutImage.setOnClickListener(this)
        mBinding.layoutDocs.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val intent = Intent()
        when (v.id) {
            R.id.layout_image -> {
                intent.setClass(this, ShowActivity::class.java)
                intent.putExtra("class", "image")
                startActivity(intent)
            }

            R.id.layout_music -> {
                intent.setClass(this, ShowActivity::class.java)
                intent.putExtra("class", "music")
                startActivity(intent)
            }
            R.id.layout_video -> {
                intent.setClass(this, ShowActivity::class.java)
                intent.putExtra("class", "video")
                startActivity(intent)
            }

            R.id.layout_docs -> {
                intent.setClass(this, ShowActivity::class.java)
                intent.putExtra("class", "word")
                startActivity(intent)
            }
            R.id.layout_apk -> {
                intent.setClass(this, ShowActivity::class.java)
                intent.putExtra("class", "apk")
                startActivity(intent)
            }

            R.id.layout_zip -> {
                intent.setClass(this, ShowActivity::class.java)
                intent.putExtra("class", "zip")
                startActivity(intent)
            }


        }
    }
}