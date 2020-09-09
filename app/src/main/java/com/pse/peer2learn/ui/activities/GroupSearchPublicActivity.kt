package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.ui.activities.interfaces.IGroupSearchPublicListener
import com.pse.peer2learn.ui.viewmodels.GroupSearchPublicViewModel

import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress
import com.pse.peer2learn.utils.adapters.ResultsGroupsRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_group_search_public.*
import kotlin.math.min

/**
 * This activity is used to search a group, by name module and allows to apply different filters on the search
 */
class GroupSearchPublicActivity : AppCompatActivity(), IGroupSearchPublicListener {

    private lateinit var currentStudent: Student
    lateinit var groupSearchPublicViewModel: GroupSearchPublicViewModel

    /**
     * Used for the creation of the Activity without any dataloss.
     * Sets up the click listeners
     * Initialises the requiered variables
     * Observes the requiered LiveData
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_search_public)
        groupSearchPublicViewModel =
            ViewModelProvider(this).get(GroupSearchPublicViewModel::class.java)
        currentStudent = intent.getSerializableExtra(Constants.USER_EXTRA) as Student
        add_filter_button.setOnClickListener(addFilterListener)
        submit_button.setOnClickListener(submitListener)
        profile_image_view.setOnClickListener(profileClickListener)
        selectFilters()
        groupSearchPublicViewModel.retrievedResultLiveData.observe(this, Observer { newValue ->
            groupSearchPublicViewModel.observeRetrievedList(newValue, this)
        })
        groupSearchPublicViewModel.groupAlreadyJoined.observe(this, Observer { newValue ->
            groupSearchPublicViewModel.observeGroupAlreadyJoined(newValue, this)
        })
        groupSearchPublicViewModel.updateMemberLiveData.observe(this, Observer { newValue ->
            groupSearchPublicViewModel.observeMemberUpdated(newValue, this)
        })
    }

    private val addFilterListener = View.OnClickListener {
        selectFilters()
    }

    private val submitListener = View.OnClickListener {
        add_filter_layout.visibility = View.GONE
        added_filter_layout.visibility = View.VISIBLE
        results_group_recycler_view.visibility = View.VISIBLE
        val progress = set_progress.text.trim().toString().toLowerCase()
        val language = set_language.text.trim().toString().toLowerCase()
        val minimumUsers = set_minimum_of_users.text.trim().toString().toLowerCase()
        val moduleName = search_edit_text.text.trim().toString().toLowerCase()
        groupSearchPublicViewModel.searchGroups(progress, language, minimumUsers, moduleName, currentStudent.university, this)
    }

    /**
     * Shows/hides the select filters menu, depending on that, whether its already opened or not
     */
    private fun selectFilters() {
        if (add_filter_layout.visibility == View.VISIBLE) {
            add_filter_layout.visibility = View.GONE
            added_filter_layout.visibility = View.VISIBLE
            results_group_recycler_view.visibility = View.VISIBLE
        } else {
            add_filter_layout.visibility = View.VISIBLE
            added_filter_layout.visibility = View.GONE
            results_group_recycler_view.visibility = View.GONE
        }
    }


    /**
     * Assures that when the back button is pressed the user will be navigated to HomeActivity
     */
    override fun onBackPressed() {
        val mainIntent = Intent(this, HomeActivity::class.java)
        mainIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(mainIntent)
        finish()
    }

    private val profileClickListener = View.OnClickListener {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        profileIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(profileIntent)
        finish()
    }

    /**
     * Used to call the correct fragment after the user clicks on the option button "Create a new group"
     * This method is called directly from the xml file
     */
    fun createPublicGroup(view: View) {
        val createGroupIntent = Intent(this, CreateGroupActivity::class.java)
        createGroupIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(createGroupIntent)
        finish()
    }
    /**
     * Used to call the correct fragment after the user clicks on the option button "Search a private group".
     * This method is called directly from the xml file
     */
    fun searchPrivateGroup(view: View) {
        supportFragmentManager.beginTransaction()
            .add(GroupSearchPrivateFragment.newInstance(currentStudent), TAG
            ).commitAllowingStateLoss()
    }

    /**
     * Gets the information that the user has put in the filter text boxes for group search
     * @return an array of filters which contains the progress, numberOfUser, language filters
     */
    private fun setFilters(): ArrayList<String> {
        val arrayOfFilters = arrayListOf<String>("", "", "")
        arrayOfFilters[Constants.PROGRESS_FILTER_INDEX] = progress_filter.text.toString()
        arrayOfFilters[Constants.NUMBER_OF_PERSONS_FILTER_INDEX] = users_filter.text.toString()
        arrayOfFilters[Constants.LANGUAGE_FILTER_INDEX] = language_filter.text.toString()
        return arrayOfFilters
    }

    /**
     * Initialises the RecyclerViewAdapter for the group search results
     * This method belongs to IGroupSearchPublicListener. It is called from inside the GroupSearchPublicViewModel whenever the results group are fetched and ready
     * to be displayed
     */
    override fun initRecyclerViewAdapter() {
        results_group_recycler_view.layoutManager = LinearLayoutManager(this)
        val results = groupSearchPublicViewModel.listAllGroups(setFilters(), groupSearchPublicViewModel.retrievedResultLiveData.value!!)
        val groupsRecyclerViewAdapter = object : ResultsGroupsRecyclerViewAdapter(results) {
            override fun joinGroup() {
                groupSearchPublicViewModel.joinGroup(
                    currentStudent,
                    this.groupList[this.clickedItemPosition]
                )
            }

        }
        results_group_recycler_view.adapter = groupsRecyclerViewAdapter
    }

    /**
     * Help method that is used to set the [progress] text with the corresponding of the group result in the RecyclerView
     */
    override fun setProgressText(progress: String) {
        progress_filter.text = progress
    }
    /**
     * Help method that is used to set the [language] text with the corresponding of the group result in the RecyclerView
     */
    override fun setLanguageText(language: String) {
        language_filter.text = language
    }
    /**
     * Help method that is used to set the [minimumUsers] text with the corresponding of the group result in the RecyclerView
     */
    override fun setMinimumUsersText(minimumUsers: String) {
        users_filter.text = minimumUsers
    }


    companion object {
        private const val TAG = "GroupSearchPrivateFragment"
    }
}
