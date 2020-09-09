package com.pse.peer2learn.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.University
import com.pse.peer2learn.ui.activities.interfaces.ISubmitListener
import com.pse.peer2learn.ui.viewmodels.RegistrationViewModel
import com.pse.peer2learn.utils.Constants
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_registration.study_course_edit_text
import kotlinx.android.synthetic.main.activity_registration.submit_button
import kotlinx.android.synthetic.main.activity_registration.university_edit_text
import kotlinx.android.synthetic.main.activity_registration.username_edit_text

/**
 * This classed is used to register an user and allows the user to fill out the requiered Information for Profile Creation
 */
class RegistrationActivity : AppCompatActivity(), ISubmitListener {

    lateinit var registrationViewModel: RegistrationViewModel
    private val userId = FirebaseAuth.getInstance().uid
    private lateinit var currentStudent: Student

    private var profileReference: StorageReference? = null
    val uniList: ArrayList<University> = arrayListOf()

    /**
     * Method for creating the activity. It also initialize the view model for the activity and observe the changes of liveData
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)


        registrationViewModel.checkValidUniversityLiveData.observe(this, Observer { newValue ->
            registrationViewModel.checkUsername(newValue, username_edit_text.text.trim().toString(), this)
        })

        registrationViewModel.checkUniqueUserLiveData.observe(this, Observer { newValue ->
            registrationViewModel.submitChanges(newValue, this, this)
        })

        registrationViewModel.updatedLiveData.observe(this, Observer { newValue ->
            registrationViewModel.updateInfos(newValue, this, this)
        })

        registrationViewModel.retrievedUniList.observe(this, Observer { universities ->
            universities.forEach { university -> uniList.add(university) }
        })

        //Set listeners for UI elements
        submit_button.setOnClickListener(submitClickListener)
        registration_profile_image.setOnClickListener(profileClickListener)

        setUniList()
        profileReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH + userId + Constants.IMG_DIR)
    }

    private val submitClickListener = View.OnClickListener {
        val username = username_edit_text.text.trim().toString()
        val university = university_edit_text.text.trim().toString().toUpperCase()
        val studyCourse = study_course_edit_text.text.trim().toString()
        registrationViewModel.verifyValidInfos(username, university, studyCourse, this)
    }

    /**
     * Starts the home activity with the correct student data being passed in the Intent as extra
     */
    private fun startHomeActivity() {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(homeIntent)
        finish()
    }

    /**
     * Assures that when the user clicks on the back button Sign In Activity will be called.
     */
    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    /**
     * This method is used to update the infos of the current sign in user
     * Takes user inputs and passes them to the [UserRepository].
     */
    override fun updateCurrentUser() {
        val username = username_edit_text.text.trim().toString()
        val university = university_edit_text.text.trim().toString().toUpperCase()
        val studyCourse = study_course_edit_text.text.trim().toString()
        val uni = uniList.first { it.shortName == university }

        currentStudent = Student(userId!!, username, studyCourse, uni, arrayListOf())
        registrationViewModel.submitUserInfos(currentStudent)
    }

    private val profileClickListener = View.OnClickListener {
        startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)
    }

    /**
     * This method is launched when the user comes back from the Gallery after selecting an image
     * On selection of an image from the gallery, loads image into Firebase Storage and loads it into profile image
     */
    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            profileReference!!.putFile(data!!.data!!)
            Glide.with(this).load(data.data).placeholder(R.drawable.profile_picture).into(registration_profile_image)
        }
    }

    /**
     * Fetches list of universities from [UniversityRepository] and set the universities inside an adapter, so that the user can select a university
     * from a given list.
     */
    private fun setUniList() {
        registrationViewModel.retrieveAllUnis()
        university_edit_text.setAdapter(ArrayAdapter<University>(this, android.R.layout.simple_dropdown_item_1line, uniList))
    }

    /**
     * This method belongs to ISubmitListener and used inside the RegistrationViewModel.
     * Assures that, after successful user info submission, the user is redirected back to home screen
     */
    override fun submit() {
        startHomeActivity()
    }
}