package com.example.madcamp_project_2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ContactsFragment()
            }
            1 -> GalleryFragment()
            else -> {
                return Tab3Fragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Contacts"
            1 -> "Gallery"
            else -> {
                return "Alarm"
            }
        }
    }
}