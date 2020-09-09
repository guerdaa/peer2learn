package com.pse.peer2learn.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.University
import com.pse.peer2learn.ui.activities.interfaces.IProfileListener
import com.pse.peer2learn.ui.activities.interfaces.ISubmitListener
import com.pse.peer2learn.ui.viewmodels.ProfileViewModel
import com.pse.peer2learn.utils.Constants
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.collections.ArrayList

/**
 * This class is used for profile editing
 */
class ProfileActivity : AppCompatActivity(), ISubmitListener, IProfileListener {

    lateinit var profileViewModel: ProfileViewModel
    lateinit var currentStudent: Student
        private set
    private lateinit var profileReference: StorageReference
    val uniList: ArrayList<University> = arrayListOf()

    private var deleteCounter = 0

    /**
     * Method for creating the activity. It also initialize the view model for the activity and observe the changes of liveData
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        currentStudent = intent.getSerializableExtra(Constants.USER_EXTRA) as Student
        deleteCounter = currentStudent.studyGroupList.size
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        //Listener for updating user infos on successful
        profileViewModel.checkValidUniversityLiveData.observe(this, Observer { newValue ->
            profileViewModel.checkUsername(newValue, username_edit_text.text.trim().toString(), currentStudent, this, this)
        })

        profileViewModel.checkUniqueUserLiveData.observe(this, Observer { newValue ->
            profileViewModel.submitChanges(newValue, this, this)
        })

        //Listener for starting of home activity on successful update of user infos
        profileViewModel.updatedLiveData.observe(this, Observer { newValue ->
            profileViewModel.updateUserInfos(newValue, this, this)
        })

        profileViewModel.retrievedUniList.observe(this, Observer { universities ->
            universities.forEach { university -> uniList.add(university) }
        })

        profileViewModel.deleteMember.observe(this, Observer {newValue ->
            profileViewModel.observeDeleteMember(newValue, currentStudent, deleteCounter, this)
        })

        profileViewModel.updateAdminOfGroupLiveData.observe(this, Observer { newValue ->
            profileViewModel.observeUpdateAdminsOfGroup(newValue, currentStudent, deleteCounter, this)
        })

        profileViewModel.deleteGroup.observe(this, Observer { newValue ->
            profileViewModel.observeDeleteGroup(newValue, currentStudent, deleteCounter, this, this)
        })

        //Set listeners for UI elements
        settings_button.setOnClickListener(showOptionsMenu)
        submit_button.setOnClickListener(submitClickListener)
        profile_profile_image.setOnClickListener(profileClickListener)
        profileReference =
            FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH + currentStudent.id + Constants.IMG_DIR)
        setInitialInfo()
    }

    /**
     * Assures that when the user clicks on the back button Home Activity will be called.
     */
    override fun onBackPressed() {
        backToHomePage()
    }

    /**
     * Assures that on "Submit" button click process of verification of inputs will start
     */
    private val submitClickListener = View.OnClickListener {
        val username = username_edit_text.text.trim().toString()
        val university = university_edit_text.text.trim().toString().toUpperCase()
        val studyCourse = study_course_edit_text.text.trim().toString()
        profileViewModel.verifyValidInfos(username, university, studyCourse, this)
    }

    /**
     * Assures that on profile button click the user will be redirected to select an image from the gallery
     */
    private val profileClickListener = View.OnClickListener {
        val chooseProfileFromGalleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(chooseProfileFromGalleryIntent, 1)
    }

    /**
     * Help method to get save the input of the user
     */
    override fun updateCurrentUser() {
        val username = username_edit_text.text.trim().toString()
        val university = university_edit_text.text.trim().toString().toUpperCase()
        val studyCourse = study_course_edit_text.text.trim().toString()
        val uni = uniList.first { it.shortName == university }

        currentStudent =
            Student(currentStudent.id, username, studyCourse, uni, currentStudent.studyGroupList)
        profileViewModel.submitUserInfos(currentStudent)
    }

    /**
     * Sets [currentStudent]'s information into corresponding input fields
     */
    private fun setInitialInfo() {
        username_edit_text.setText(currentStudent.nickname);
        university_edit_text.setText(currentStudent.university!!.shortName);
        study_course_edit_text.setText(currentStudent.studyCourse);
        Glide.with(this).load(profileReference).diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true).placeholder(R.drawable.profile_picture)
            .into(profile_profile_image as ImageView)
        profileViewModel.retrieveAllUnis()
        university_edit_text.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                uniList
            )
        )
    }

    /**
     * This method is launched when the user comes back from the Gallery after selecting an image
     * On selection of an image from the gallery, loads image into Firebase Storage and loads it into profile image
     */
    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            profileReference.putFile(data!!.data!!)
            Glide.with(this).load(data.data).placeholder(R.drawable.profile_picture)
                .into(profile_profile_image as ImageView)
        }
    }

    /**
     * Used to show and execute the options of the advanced profile options menu, the options being:
     * - sign out
     * - delete account
     * - go back to homepage without saving changes
     */
    private val showOptionsMenu = View.OnClickListener {
        val popup = PopupMenu(this, settings_button)
        popup.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.sign_out -> {
                    profileViewModel.signOut(this)
                    true
                }
                R.id.delete_acc -> {
                    profileViewModel.quitAllGroups(this, currentStudent)
                    true
                }
                R.id.back_to_homepage -> {
                    backToHomePage()
                    true
                }
                else -> false
            }
        }
        popup.inflate(R.menu.log_options_menu)
        popup.show()
    }

    /**
     * Redirects the user to the home page without data loss by putting the currentStudent as extra
     * Is called either on successful user info update or on "Back to homepage" menu item click
     */
    private fun backToHomePage() {
        val settingsIntent = Intent(this, HomeActivity::class.java)
        settingsIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(settingsIntent)
        finish()
    }

    /**
     * Assures that, after successful user info update, the user is redirected back to home screen
     * This method belongs to ISubmitListener and will be called inside the ProfileViewModel
     */
    override fun submit() {
        backToHomePage()
    }

    /**
     * Sets the [deleteCounter] to [counter]
     * Used for going through the already updated user's groups
     * This method belongs to IProfileListener and will be called inside the ProfileViewModel
     */
    override fun setDeleteCounter(counter: Int) {
        deleteCounter = counter
    }

    /**
     * Returns [deleteCounter]
     * Used for going through the already updated user's groups
     * This method belongs to IProfileListener and will be called inside the ProfileViewModel
     */
    override fun getDeleteCounter(): Int {
        return deleteCounter
    }
}