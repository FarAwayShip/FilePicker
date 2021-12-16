package com.farawayship.library

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.farawayship.library.adapter.FilesPagerAdapter
import com.farawayship.library.databinding.ActivityFileSelectBinding
import com.farawayship.library.event_bus.FileSelectEvent
import com.farawayship.library.file_type.ShowActivity

class FileSelectActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "FileSelectActivity"
    }

    private lateinit var mActivityResultLauncher: ActivityResultLauncher<String>

    private lateinit var mBinding: ActivityFileSelectBinding
    private lateinit var mAdapter: FilesPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_file_select)
        setup()
    }

    private fun setup() {
        mAdapter = FilesPagerAdapter(this)
        mBinding.pager.adapter = mAdapter
        TabLayoutMediator(mBinding.tabLayout, mBinding.pager) { tab, position ->
            when (position) {
                0 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_history)
                1 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_folder_outline)
            }
        }.attach()
        EventBus.getDefault().register(this);
        mActivityResultLauncher =
            registerForActivityResult(PickFileContract()) { uri: Uri? ->
                Log.i(TAG, "Result: $uri")
            }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileSelect(event: FileSelectEvent) {
        Log.d(TAG, "onFileSelect: " + event.file.absolutePath)
        val intent = Intent()
        intent.putExtra("file_path", event.file.absolutePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

//    override fun onClick(v: View) {
//        val intent = Intent()
//        when (v.id) {
//            R.id.layout_image -> {
////                intent.setClass(this, ShowActivity::class.java)
////                intent.putExtra("class", "image")
////                startActivityForResult(intent, FILE_REQUEST_ CODE)
////                mActivityResultLauncher.launch("image")
//            }
//
//            R.id.layout_music -> {
//                intent.setClass(this, ShowActivity::class.java)
//                intent.putExtra("class", "music")
//
//            }
//            R.id.layout_video -> {
//                intent.setClass(this, ShowActivity::class.java)
//                intent.putExtra("class", "video")
//                startActivityForResult(intent, FILE_REQUEST_CODE)
//            }
//
//            R.id.layout_docs -> {
//                intent.setClass(this, ShowActivity::class.java)
//                intent.putExtra("class", "word")
//                startActivityForResult(intent, FILE_REQUEST_CODE)
//            }
//            R.id.layout_apk -> {
//                intent.setClass(this, ShowActivity::class.java)
//                intent.putExtra("class", "apk")
//                startActivityForResult(intent, FILE_REQUEST_CODE)
//            }
//
//            R.id.layout_zip -> {
//                intent.setClass(this, ShowActivity::class.java)
//                intent.putExtra("class", "zip")
//                startActivityForResult(intent, FILE_REQUEST_CODE)
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Log.d(TAG, "onActivityResult: ")
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == FILE_REQUEST_CODE && data != null) {
//                val filePath = data.getStringExtra("EXTRA_FILE_PATH")
//                if (!TextUtils.isEmpty(filePath)) {
//                    val intent = Intent()
//                    intent.putExtra("file_path", filePath)
//                    setResult(Activity.RESULT_OK, intent)
//                    finish()
//                }
//            }
//        }
//    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        Log.i(TAG, "onBackPressed: $count")
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    inner class PickFileContract : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String): Intent {
            val intent = Intent()
            intent.setClass(context, ShowActivity::class.java)
            intent.putExtra("class", "input")
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode != Activity.RESULT_OK) {
                return null
            }
            Log.d(TAG, "parseResult: " + intent?.data)
            return intent?.data
        }

    }
}