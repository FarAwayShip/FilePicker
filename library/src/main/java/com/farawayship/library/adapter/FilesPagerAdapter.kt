package com.farawayship.library.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.farawayship.library.FileSelectActivity
import com.farawayship.library.explorer.ExplorerMainFragment
import com.farawayship.library.file_type.fragment.FilesFragment
import java.lang.IllegalArgumentException

class FilesPagerAdapter(
    activity: FragmentActivity,
    val mode: Int = FileSelectActivity.MODE_NORMAL,
    val fileExts: Array<String> = emptyArray()
) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return if (mode == FileSelectActivity.MODE_NORMAL) 2 else 1
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> if (mode == FileSelectActivity.MODE_NORMAL) FilesFragment.newInstance() else ExplorerMainFragment.newInstance(
                fileExts
            )
            1 -> ExplorerMainFragment.newInstance()
            else -> throw IllegalArgumentException("Not found fragment for position: $position")
        }
    }


}