package com.farawayship.library.explorer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.farawayship.library.R
import java.io.File
import java.util.*

class ExplorerMainFragment : Fragment(), ExplorerFragment.Listener {
    private val SHARE_REQUEST = 1

    companion object {
        const val TAG = "ExplorerMainFragment"
        fun newInstance(): ExplorerMainFragment {
            return ExplorerMainFragment()
        }
    }


    private var mFragment: ExplorerFragment? = null
    private val mShowHidden = false
    private var mDirectories: ArrayList<StorageDirectory>? = null

    private inner class StorageDirectory internal constructor(
        private val mName: String,
        private val mPath: String
    ) {
        fun name(): String {
            return mName
        }

        fun path(): String {
            return mPath
        }
    }


    /**
     * Create and display a fragment for the specified directory
     * @param directory path to directory
     * @param clearStack clear the back stack with this item
     */
    private fun showDirectory(directory: String?, clearStack: Boolean) {
        mFragment = ExplorerFragment()

        // If directory is not empty, pass it along to the fragment
        if (directory != null) {
            val arguments = Bundle()
            arguments.putString(ExplorerFragment.DIRECTORY, directory)
            arguments.putBoolean(ExplorerFragment.SHOW_HIDDEN, mShowHidden)
            mFragment!!.arguments = arguments
        }

        // Begin a transaction to insert the fragment
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()

        // If the stack isn't being cleared, animate the transaction
        if (!clearStack) {
            transaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        }

        // Insert the new fragment
        transaction.replace(R.id.directory_container, mFragment!!)

        // If the stack isn't being cleared, add this one to the back
        if (!clearStack) {
            transaction.addToBackStack(null)
        }

        // Commit the transaction
        transaction.commit()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_explorer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            showDirectory(null, true)
//            Toast.makeText(
//                requireContext(), getText(R.string.activity_explorer_hint),
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    override fun onResume() {

        // (Re)initialize the list of directories
        mDirectories = ArrayList<StorageDirectory>()

        // Finding the path to the storage directories is very difficult - for
        // API 19+, use the getExternalFilesDirs() method and extract the path
        // from the app-specific path returned; older devices cannot access
        // removably media :(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // Enumerate all of the storage directories
            val files: Array<File?> = getExternalFilesDirs(requireContext(), null)
            for (i in files.indices) {

                // If the directory is usable, retrieve its path
                if (files[i] == null) {
                    continue
                }
                var path = files[i]!!.absolutePath

                // The path should contain Android/data and the portion of the
                // path preceding that is the root (a hack, but it works)
                val rootIndex = path.indexOf("Android/data")
                if (rootIndex == -1) {
                    continue
                }
                path = path.substring(0, rootIndex)

                // Assume that the first directory is for internal storage and
                // the others are removable (either card slots or USB OTG)
                Log.i(TAG, String.format("found storage directory: \"%s\"", path))
                mDirectories!!.add(
                    StorageDirectory(
                        if (i == 0) getString(R.string.activity_explorer_internal) else getString(
                            R.string.activity_explorer_removable,
                            File(path).name
                        ),
                        path
                    )
                )
            }
        }

        // Invoke the parent method
        super.onResume()
    }

    override fun onBrowseDirectory(directory: String?) {
        showDirectory(directory, false)
    }


    override fun onSendUris(uris: ArrayList<Uri?>?) {
//        Intent shareIntent = new Intent(this, ShareActivity.class);
//        shareIntent.setAction("android.intent.action.SEND_MULTIPLE");
//        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
//        startActivityForResult(shareIntent, SHARE_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SHARE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                activity?.finish()
            }
        }
    }
}