package com.pse.peer2learn.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Language
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.ui.activities.interfaces.ICreateGroupListener
import com.pse.peer2learn.ui.viewmodels.CreateGroupViewModel
import com.pse.peer2learn.utils.Constants
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.activity_home.profile_image_view

/**
 * This activity is used to create a group with the given from the user input
 */
class CreateGroupActivity : AppCompatActivity(), ICreateGroupListener {

    private lateinit var currentStudent: Student
        lateinit var createGroupViewModel: CreateGroupViewModel

    /**
     * Sets the corresponding click listener to the ui buttons
     * Obsersers the requiered LiveData
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        createGroupViewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)
        currentStudent = intent.getSerializableExtra(Constants.USER_EXTRA) as Student
        profile_image_view.setOnClickListener(profileClickListener)
        submit_button.setOnClickListener(submitClickListener)
        private_group_switch_button.setOnCheckedChangeListener(switchButtonChangeListener)
        createGroupViewModel.createGroupLiveData.observe(this, Observer {newValue ->
            createGroupViewModel.observeCreateGroup(newValue, currentStudent, createGroupViewModel.groupIdLiveData.value,this)
        })
        createGroupViewModel.updateMemberLiveData.observe(this, Observer {newValue ->
            createGroupViewModel.observeUpdateMember(newValue, currentStudent, createGroupViewModel.groupIdLiveData.value, this)
        })
        createGroupViewModel.updateUserGroupsLiveData.observe(this, Observer { newValue ->
            createGroupViewModel.observeUpdateUserGroups(newValue, this, this)
        })
        createGroupViewModel.accessCodeUnique.observe(this, Observer { newValue ->
            createGroupViewModel.observeAccessCode(newValue, this)
        })
        createGroupViewModel.privateGroupCreated.observe(this, Observer { newValue ->
            createGroupViewModel.observeCreateGroup(newValue,
                currentStudent, createGroupViewModel.privatAccessCode, this)
        })
    }

    private val profileClickListener = View.OnClickListener {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        profileIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(profileIntent)
        finish()
    }

    private val submitClickListener = View.OnClickListener {
        val title = module_edit_text.text.trim().toString().toLowerCase()
        val description = description_edit_text.text.trim().toString()
        val numberOfPersons = number_of_users_text_view.text.toString().toInt()
        val language = language_edit_text.text.trim().toString().toUpperCase()
        if(createGroupViewModel.validateFormValues(title, description, numberOfPersons, language, this)) {
            createGroup()
        }
    }
    private val switchButtonChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            createGroupViewModel.generateAccessCode()
            submit_button.isEnabled = false
        } else {
            access_code_text_view.text = ""
            submit_button.isEnabled = true
        }
    }

    /**
     * Increments the number of maximum members in a group
     */
    fun incrementMaxParticipant(view: View) {
        var numberOfUsers = number_of_users_text_view.text.toString().toInt()
        numberOfUsers++
        number_of_users_text_view.text = numberOfUsers.toString()
    }

    /**
     * Decrements the number of maximumm members in a group
     */
    fun decrementMinParticipant(view: View) {
        var numberOfUsers = number_of_users_text_view.text.toString().toInt()
        if(numberOfUsers > 0)
            numberOfUsers--
        else
            numberOfUsers = 0
        number_of_users_text_view.text = numberOfUsers.toString()
    }

    /**
     * Assures that when the back button is pressed the user will be navigated to HomeActivity
     */
    override fun onBackPressed() {
        navigateHome()
    }

    /**
     * Navigates to HomeActivity
     * This method belongs to ICreateGroupListener. It is used inside the HomeViewModel
     */
    override fun navigateHome() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(homeIntent)
        finish()
    }

    /**
     * Help method that creates an istance of PrivateGroupCreatedFragment and display the dialog after a private group is created
     * This method belongs to ICreateGroupListener. It is used inside the HomeViewModel
     */
    override fun showPrivateGroupCreatedFragment() {
        supportFragmentManager.beginTransaction().add(
            PrivateGroupCreatedFragment.newInstance(
                createGroupViewModel.privatAccessCode, currentStudent), PRIVATE_GROUP
        ).commitAllowingStateLoss()    }

    /**
     * A method for setting the access_code_text_view text
     * This method belongs to ICreateGroupListener. It is used inside the HomeViewModel
     */
    override fun setAccessCode() {
        access_code_text_view.text = createGroupViewModel.privatAccessCode
        submit_button.isEnabled = true
    }

    /**
     * Calls the necessary methods and constructors to create a group and gets the information from the UI
     */
    private fun createGroup() {
        submit_button.isEnabled = false
        submit_button.alpha = 0.3f
        val title = module_edit_text.text.trim().toString().toLowerCase()
        val description = description_edit_text.text.trim().toString()
        val numberOfPersons = number_of_users_text_view.text.toString().toInt()
        val isPrivate = private_group_switch_button.isChecked
        val language = language_edit_text.text.trim().toString().toUpperCase()
        val studyGroup = StudyGroup(
            title,
            description,
            numberOfPersons,
            StudyGroup.NON_PRIVATE_GROUP,
            currentStudent.university!!,
            Language(language))
        if (isPrivate) {
            studyGroup.id = access_code_text_view.text.toString()
            Log.d("∞∞∞∞∞∞∞∞", studyGroup.id + "...")
            studyGroup.accessCode = access_code_text_view.text.toString()
            createGroupViewModel.privatAccessCode = studyGroup.accessCode
            createGroupViewModel.createPrivateGroup(studyGroup)
            private_group_switch_button.isChecked = false
        } else {
            createGroupViewModel.createPublicGroup(studyGroup)
        }
    }

    companion object {
        private const val PRIVATE_GROUP = "PRIVATE_GROUP"
    }
}