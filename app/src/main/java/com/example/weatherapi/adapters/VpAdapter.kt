package com.example.weatherapi.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class VpAdapter(fa:FragmentActivity, private val listOfFragments: List<Fragment> ) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return listOfFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return listOfFragments[position]
    }
}