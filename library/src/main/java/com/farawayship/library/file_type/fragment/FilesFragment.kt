package pl.itto.file_manager.file_type.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pl.itto.file_manager.R
import pl.itto.file_manager.databinding.FragmentFileTypesBinding
import com.farawayship.library.file_type.ShowActivity

class FilesFragment : Fragment(), View.OnClickListener {

    companion object {
        const val TAG = "FilesFragment"

        fun newInstance(): FilesFragment {
            return FilesFragment()
        }

        private const val FILE_REQUEST_CODE = 2000
    }

    private lateinit var mBinding: FragmentFileTypesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFileTypesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        mBinding.layoutZip.setOnClickListener(this)
        mBinding.layoutApk.setOnClickListener(this)
        mBinding.layoutVideo.setOnClickListener(this)
        mBinding.layoutMusic.setOnClickListener(this)
        mBinding.layoutImage.setOnClickListener(this)
        mBinding.layoutDocs.setOnClickListener(this)

        mBinding.layoutApkIcon.clipToOutline = true
        mBinding.layoutZipIcon.clipToOutline = true
        mBinding.layoutVideoIcon.clipToOutline = true
        mBinding.layoutMusicIcon.clipToOutline = true
//        mBinding.layoutImageIcon.clipToOutline = true
        mBinding.layoutDocsIcon.clipToOutline = true

    }

    override fun onClick(v: View) {
        val intent = Intent()
        requireActivity()
        when (v.id) {
            R.id.layout_image -> {
                intent.setClass(requireActivity(), ShowActivity::class.java)
                intent.putExtra("class", "image")
                startActivityForResult(intent, FILE_REQUEST_CODE)
            }

            R.id.layout_music -> {
                intent.setClass(requireActivity(), ShowActivity::class.java)
                intent.putExtra("class", "music")
                startActivityForResult(intent, FILE_REQUEST_CODE)
            }
            R.id.layout_video -> {
                intent.setClass(requireActivity(), ShowActivity::class.java)
                intent.putExtra("class", "video")
                startActivityForResult(intent, FILE_REQUEST_CODE)
            }

            R.id.layout_docs -> {
                intent.setClass(requireActivity(), ShowActivity::class.java)
                intent.putExtra("class", "word")
                startActivityForResult(intent, FILE_REQUEST_CODE)
            }
            R.id.layout_apk -> {
                intent.setClass(requireActivity(), ShowActivity::class.java)
                intent.putExtra("class", "apk")
                startActivityForResult(intent, FILE_REQUEST_CODE)
            }

            R.id.layout_zip -> {
                intent.setClass(requireActivity(), ShowActivity::class.java)
                intent.putExtra("class", "zip")
                startActivityForResult(intent, FILE_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: ")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FILE_REQUEST_CODE && data != null) {
                val filePath = data.getStringExtra("EXTRA_FILE_PATH")
                if (!TextUtils.isEmpty(filePath)) {
                    val intent = Intent()
                    intent.putExtra("file_path", filePath)
                    requireActivity().setResult(Activity.RESULT_OK, intent)
                    requireActivity().finish()
                }
            }
        }
    }
}