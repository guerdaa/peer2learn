package com.pse.peer2learn.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.ui.activities.interfaces.ISignInListener
import com.pse.peer2learn.ui.viewmodels.SignInViewModel
import com.pse.peer2learn.utils.Constants
import kotlinx.android.synthetic.main.activity_sign_in.*

/**
 * THis class is used for sign in and registration with Google-Account
 */
class SignInActivity : AppCompatActivity(), ISignInListener {

    lateinit var signInViewModel: SignInViewModel

    /**
     * Method for creating the activity. It also initialize the view model for the activity and observe the changes of liveData
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        sign_in_button.setOnClickListener(signInWithGoogleClickListener)
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        signInViewModel.setGoogleSignInClient(this)
        signInViewModel.signInLiveData.observe(this, Observer {newValue ->
            signInViewModel.observeSignIn(newValue, this)
        })
        signInViewModel.currentUidLiveData.observe(this, Observer {newValue ->
            signInViewModel.observeCurrentUid(newValue)
        })
        signInViewModel.userRetrieved.observe(this, Observer {newValue ->
            signInViewModel.observeUserRetrieved(newValue, this)
        })
        signInViewModel.userCreated.observe(this, Observer {newValue ->
            signInViewModel.observeUserCreated(newValue, this, this)
        })
    }

    /**
     * Auto initialises the user on start, if the user is already signed in with a Google account.
     */
    override fun onStart() {
        super.onStart()
        signInViewModel.initUser()
    }

    /**
     * Assures that when the back button is pressed, nothing will happen
     */
    override fun onBackPressed() {
    }

    private val signInWithGoogleClickListener =  View.OnClickListener {
        signIn()
    }

    /**
     * This method is launched when the user comes back from the Google SignIn Dialog
     * The intent will content the account used to sign in
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            signInViewModel.getSignedInAccount(data)
        }
    }

    /**
     * Help method that calls the Registration activity on the screen. This method belongs to ISignInListener.
     * It will be called from SignInViewModel when the user has already an account
     */
    override fun startRegistrationActivity() {
        val registrationIntent = Intent(this, RegistrationActivity::class.java)
        startActivity(registrationIntent)
        finish()
    }

    /**
     * Help method that calls the Home Activity on the screen. This method belongs to ISignInListener.
     * It will be called from SignInViewModel when the user has already an account
     */
    override fun startHomeActivity(student: Student) {
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra(Constants.USER_EXTRA, student)
        startActivity(homeIntent)
        finish()
    }

    /**
     * Help method to start the Google SignIn Dialog
     */
    private fun signIn() {
        val signInIntent = signInViewModel.googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

}