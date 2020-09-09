package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.SignInActivity
import com.pse.peer2learn.ui.activities.interfaces.ISignInListener
import com.pse.peer2learn.utils.RepositoryProgress
/**
 * Holds the logic of the SignInActivity
 */
class SignInViewModel: ViewModel {

    var googleSignInClient: GoogleSignInClient? = null

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _signInLiveData = MutableLiveData<RepositoryProgress>()
    private val _currentUidLiveData = MutableLiveData<String>()
    private val _userCreated = MutableLiveData<Boolean>()
    private val _userRetrieved = MutableLiveData<Student?>()
    private var userRepository = UserRepository(
        createdLiveData = _userCreated,
        retrievedLiveData = _userRetrieved
    )


    val signInLiveData: LiveData<RepositoryProgress>
        get() = _signInLiveData

    val currentUidLiveData: LiveData<String>
        get() = _currentUidLiveData

    val userCreated: LiveData<Boolean>
        get() = _userCreated

    val userRetrieved: LiveData<Student?>
        get() = _userRetrieved

    init {
        _currentUidLiveData.value = ""
        _signInLiveData.value = RepositoryProgress.NOT_STARTED
        _userRetrieved.value = Student()
    }

    constructor()

    constructor(userRepository: UserRepository): this() {
        this.userRepository = userRepository
    }

    /**
     * Configures Google sign-in to request E-Mail and ID Token
     */
    fun setGoogleSignInClient(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    /**
     * Initializes the user
     */
    fun initUser() {
        auth.uid?.let {
            _currentUidLiveData.value = it
        }
    }

    /**
     * Checks if the user already exists
     * Help method to "connect" the view with the repository
     */
    private fun checkIfUserAvailable() {
        _userRetrieved.value?.let {
            userRepository.retrieve(_currentUidLiveData.value!!)
        }
    }

    /**
     * Adds the current user into the [userRepository]
     */
    private fun createUserInDatabase() {
        userRepository.create(Student(_currentUidLiveData.value!!))
    }

    /**
     * Checks if the current user authentication was successful
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        _signInLiveData.value = RepositoryProgress.IN_PROGRESS
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInLiveData.value = RepositoryProgress.SUCCESS
                    _currentUidLiveData.value = auth.uid
                } else {
                    _signInLiveData.value = RepositoryProgress.FAILED
                }
            }
    }

    /**
     * Get signed in Google account and authenticates user in Firebase with Google Account token
     */
    fun getSignedInAccount(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    /**
     * Observe Sign-In and display appropriate message depending on the result
     */
    fun observeSignIn(newValue: RepositoryProgress, context: Context) {
        when (newValue) {
            RepositoryProgress.IN_PROGRESS -> {
                Toast.makeText(
                    context, context.getString(R.string.loading), Toast.LENGTH_SHORT
                ).show()
            }
            RepositoryProgress.SUCCESS -> {
                Toast.makeText(
                    context, context.getString(R.string.succeeded), Toast.LENGTH_SHORT
                ).show()
            }
            RepositoryProgress.FAILED -> {
                Toast.makeText(
                    context, context.getString(R.string.failed), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * On user signing in, checks if user with this id already present in the database
     */
    fun observeCurrentUid(newValue: String) {
        if (newValue.isNotEmpty()) {
            checkIfUserAvailable()
        }
    }

    /**
     * If a user exists in the database, starts home activity, otherwise creates a new entry in database
     */
    fun observeUserRetrieved(newValue: Student?, signInListener: ISignInListener) {
        if(!newValue?.id.isNullOrEmpty()
            && !newValue?.nickname.isNullOrEmpty()
            && !newValue?.studyCourse.isNullOrEmpty()
            && newValue?.university != null) {
            signInListener.startHomeActivity(newValue)
        } else if((newValue == null && !currentUidLiveData.value.isNullOrEmpty())
            || (!newValue?.id.isNullOrEmpty()
                    && newValue?.nickname.isNullOrEmpty()
                    && newValue?.studyCourse.isNullOrEmpty()
                    && newValue?.university == null)){
            createUserInDatabase()
        }
    }

    /**
     * Starts RegistrationActivity when a user entry was successfully created in database
     */
    fun observeUserCreated(newValue: Boolean, signInListener: ISignInListener, context: Context) {
        if(newValue) {
            signInListener.startRegistrationActivity()
        } else {
            Toast.makeText(
                context, context.getString(R.string.failed), Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val TAG = "SignInViewModel"
    }

}