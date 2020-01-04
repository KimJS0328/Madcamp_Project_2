package com.example.madcamp_project_2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            else -> {
                ContactsFragment()
            }
            /*1 -> GalleryFragment()
            else -> {
                return CalendarFragment()
            }*/
        }
    }

    override fun getCount(): Int {
        return 1
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            else -> "Contacts"
            /*1 -> "Gallery"
            else -> {
                return "Calendar"
            }*/
        }
    }
}