package com.pse.peer2learn.utils.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.ui.activities.AppointmentsOverviewFragment
import com.pse.peer2learn.ui.activities.ChatsFragment
import com.pse.peer2learn.ui.activities.GroupOverviewFragment
import com.pse.peer2learn.utils.Constants

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val currentUser: Student, private val group: StudyGroup): FragmentStateAdapter(fragmentActivity) {

    /**
     * @return the number of fragments inside GroupActivity = 3
     */
    override fun getItemCount(): Int {
        return Constants.NUMBER_OF_FRAGMENTS
    }

    /**
     * method used to return the corresponding fragment depending on the [position]
     */
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ChatsFragment.newInstance(currentUser, group.id)
            1 -> AppointmentsOverviewFragment.newInstance(group.id)
            else -> GroupOverviewFragment.newInstance(currentUser, group)
        }
    }
}