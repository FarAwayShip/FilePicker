package com.farawayship.library.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.farawayship.library.explorer.ExplorerMainFragment
import com.farawayship.library.file_type.fragment.FilesFragment
import java.lang.IllegalArgumentException

class FilesPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FilesFragment.newInstance()
            1 -> ExplorerMainFragment.newInstance()
            else -> throw IllegalArgumentException("Not found fragment for position: $position")
        }
    }



}