package com.pse.peer2learn.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.ui.viewmodels.GroupsViewModel
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * This activity has contains the AppointmentsOverviewFragment, ChatsFragment and GroupOverviewFragment.
 * Responsible for managing the nagivation between the fragments
 */
class GroupActivity : FragmentActivity() {

    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var groupViewModel: GroupsViewModel
    private lateinit var currentStudent: Student
    private lateinit var currentGroup: StudyGroup
    var isUserAdmin = false

    /**
     * initialises the requiered for the view variables
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        // Sets the current user and the current group,in that the user is
        currentStudent = intent.getSerializableExtra(Constants.USER_EXTRA) as Student
        currentGroup = intent.getSerializableExtra(Constants.GROUP_EXTRA) as StudyGroup
        groupViewModel = ViewModelProvider(this).get(GroupsViewModel::class.java)
        groupViewModel.checkIfUserIsAdmin(currentGroup.id, currentStudent.id)
        groupViewModel.isAdmin.observe(this, Observer { newValue ->
            isUserAdmin = newValue
        })
        viewPagerAdapter = ViewPagerAdapter(this, currentStudent, currentGroup)
        group_title_text_view.text = currentGroup.title
        view_pager.adapter = viewPagerAdapter
        view_pager.isUserInputEnabled = false
        back.setOnClickListener { backClickListener() }
        onOptionClickListener()
    }

    /**
     * Assures that when the back arrows is clicked the user will be redirected to Home Activity
     */
    private fun backClickListener() {
        val backIntent = Intent(this, HomeActivity::class.java)
        backIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(backIntent)
        finish()
    }

    /**
     * Assures that the correct fragment will be showed when the users clicks on the corresponding tab
     */
    private fun onOptionClickListener() {
        chat_text_view.setOnClickListener {
            view_pager.currentItem = 0
            setSelectedOption()
        }

        appointments_text_view.setOnClickListener {
            view_pager.currentItem = 1
            setSelectedOption()
        }

        overview_text_view.setOnClickListener {
            view_pager.currentItem = 2
            setSelectedOption()
        }
    }

    /**
     * Adjust the UI depending on the selected fragment.
     */
    private fun setSelectedOption() {
        when(view_pager.currentItem) {
            0 -> {
                chat_text_view.alpha = 1.0f
                appointments_text_view.alpha = 0.3f
                overview_text_view.alpha = 0.3f
            }
            1 -> {
                chat_text_view.alpha = 0.3f
                appointments_text_view.alpha = 1.0f
                overview_text_view.alpha = 0.3f
            }
            else -> {
                chat_text_view.alpha = 0.3f
                appointments_text_view.alpha = 0.3f
                overview_text_view.alpha = 1.0f
            }
        }
    }
    /**
     * Assures that when the back button is pressed the user will be navigated to Home Activity
     */
    override fun onBackPressed() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(homeIntent)
        finish()
    }
}