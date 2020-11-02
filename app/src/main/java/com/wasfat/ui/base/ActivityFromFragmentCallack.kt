package com.os.urservice.ui.base

import androidx.fragment.app.Fragment

interface ActivityFromFragmentCallack {
    fun changeFragment(fragment: Fragment, isAddToBackStack: Boolean)

    fun changeToolbarTitle(title: String)
}
