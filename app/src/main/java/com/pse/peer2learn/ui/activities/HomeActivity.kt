package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.ui.viewmodels.HomeViewModel
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_home.*
import android.widget.PopupMenu
import com.pse.peer2learn.ui.activities.interfaces.IHomeListener

/**
 * This activity is used to show the groups of the [currentStudent] and also for main navigation to and from the different activities/fragments
 */
class HomeActivity : AppCompatActivity(), IHomeListener {

    private lateinit var currentStudent: Student
    lateinit var homeViewModel: HomeViewModel
    lateinit var adapter: GroupsRecyclerViewAdapter

    /**
     * Method for creating the activity. It also initialize the view model for the activity and observe the changes of liveData
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        currentStudent = intent.getSerializableExtra(Constants.USER_EXTRA) as Student
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.retrieveUserGroups(currentStudent.studyGroupList)
        profile_image_view.setOnClickListener(profileClickListener)
        add_group_button.setOnClickListener(addGroupListener)
        sort_by_text_view.setOnClickListener {
            inflateMenu()
        }
        homeViewModel.retrieveLiveData.observe(this, Observer { newValue ->
            homeViewModel.observeRetrieve(newValue, currentStudent)
        })
        homeViewModel.retrievedMessages.observe(this, Observer { newValue ->
            homeViewModel.observeRetrieveMessage(newValue, currentStudent, this)
        })
    }

    /**
     * Used to show and execute the options of the pop up menu, the options being:
     * - sort groups by name in descending/ascending order
     * - sort groups by date of last message sent in descending/ascending order
     */
    private fun inflateMenu() {
        val popupMenu = PopupMenu(this, sort_by_text_view)
        popupMenu.menuInflater.inflate(R.menu.popup_menufile_sort, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            if (currentStudent.studyGroupList.size != 0) {
                when (item.itemId) {
                    R.id.sort_desc -> adapter.sortNameDesc()
                    R.id.sort_asc -> adapter.sortNameAsc()
                }
                groups_recycler_view.adapter = adapter
            }
            true
        })
        popupMenu.show()
    }

    /**
     * Used to call the Profile Activity when the user clicks on the profile icon
     */
    private val profileClickListener = View.OnClickListener {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        profileIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(profileIntent)
        finish()
    }

    /**
     * Used to call the Group Search Activity when the user clicks on the plus
     */
    private val addGroupListener = View.OnClickListener {
        val groupSearchPublicIntent = Intent(this, GroupSearchPublicActivity::class.java)
        groupSearchPublicIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(groupSearchPublicIntent)
        finish()
    }

    /**
     * Used to call the Open Group/Quit Group fragment after the user clicks on one of the groups he is already member of.
     */
    private fun startMenuFragment() {
        supportFragmentManager.beginTransaction().add(
            HomeMenuFragment.newInstance(currentStudent, GroupsRecyclerViewAdapter.selectedGroup),
            HomeMenuFragment.TAG
        ).commitNowAllowingStateLoss()
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    /**
     * Used to open the selected by the user group
     */
    private fun startGroupActivty() {
        val groupIntent = Intent(this, GroupActivity::class.java)
        groupIntent.putExtra(Constants.GROUP_EXTRA, GroupsRecyclerViewAdapter.selectedGroup)
        groupIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(groupIntent)
        finish()
    }

    /**
     * Creates a RecyclerViewAdapter and fills it with [currentStudent]'s groups and last sent messages within those groups
     * This method belongs to IHomeListener, and called from inside HomeViewModel. It will init the recyclerview for the groups when
     * the groups are fetched.
     */
    override fun initRecyclerViewAdapter() {
        groups_recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = object : GroupsRecyclerViewAdapter(
            homeViewModel.retrievedUserGroupsLiveData.value!!,
            homeViewModel.retrievedLastMessages.value!!,
            currentStudent.id
        ) {
            override fun openGroup() {
                startGroupActivty()
            }

            override fun openMenu() {
                startMenuFragment()
            }
        }
        groups_recycler_view.adapter = adapter
    }
}
