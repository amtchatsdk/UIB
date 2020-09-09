package com.uib

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

class MyUtility {
    companion object {

        fun replaceFragment(
            activity: FragmentActivity,
            mFragment: Fragment?,
            TAG: String?,
            backSack: Boolean
        ) {
            val fragmentTransaction: FragmentTransaction =
                activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.content, mFragment!!, TAG)
            if (backSack) {
                fragmentTransaction.addToBackStack(TAG)
            }
            fragmentTransaction.commit()
        }
    }
}