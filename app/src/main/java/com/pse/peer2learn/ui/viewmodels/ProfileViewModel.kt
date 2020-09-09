package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Member
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.University
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.UniversityRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.SignInActivity
import com.pse.peer2learn.ui.activities.interfaces.IProfileListener
import com.pse.peer2learn.ui.activities.interfaces.ISubmitListener
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress

/**
 * Holds the logic of the ProfileActivity
 */
class ProfileViewModel : ViewModel {

    private val storageReference = FirebaseStorage.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _checkUniqueUserLiveData = MutableLiveData<RepositoryProgress>()
    val checkUniqueUserLiveData: LiveData<RepositoryProgress>
        get() = _checkUniqueUserLiveData


    private val _checkValidUniversityLiveData = MutableLiveData<RepositoryProgress>()
    val checkValidUniversityLiveData: LiveData<RepositoryProgress>
        get() = _checkValidUniversityLiveData


    private val _updatedLiveData = MutableLiveData<RepositoryProgress>()
    val updatedLiveData: LiveData<RepositoryProgress>
        get() = _updatedLiveData


    private val _retrievedUniList = MutableLiveData<ArrayList<University>>()
    val retrievedUniList: LiveData<ArrayList<University>>
        get() = _retrievedUniList


    private val _deleteMember = MutableLiveData<RepositoryProgress>()
    val deleteMember: LiveData<RepositoryProgress> get() = _deleteMember


    private val _deleteGroup = MutableLiveData<RepositoryProgress>()
    val deleteGroup: LiveData<RepositoryProgress> get() = _deleteGroup

    private val _updateAdminOfGroupLiveData = MutableLiveData<RepositoryProgress>()
    val updateAdminOfGroupLiveData: LiveData<RepositoryProgress> get() = _updateAdminOfGroupLiveData

    private var memberRepository = MemberRepository(
        deleteMemberLiveData = _deleteMember,
        updateAdminOfGroupLiveData = _updateAdminOfGroupLiveData
    )

    private var groupRepository = GroupRepository(
        deleteLiveData = _deleteGroup
    )

    private var universityRepository = UniversityRepository(
        checkValidUniversityLiveData = _checkValidUniversityLiveData,
        retrievedUniList = _retrievedUniList
    )


    private var userRepository = UserRepository(
        updatedLiveData = _updatedLiveData,
        checkUniqueUserLiveData = _checkUniqueUserLiveData
    )

    constructor()

    constructor(userRepository: UserRepository, memberRepository: MemberRepository, groupRepository: GroupRepository, universityRepository: UniversityRepository): this() {
        this.memberRepository = memberRepository
        this.groupRepository = groupRepository
        this.userRepository = userRepository
        this.universityRepository = universityRepository
    }

    init {
        _retrievedUniList.value = arrayListOf()
        _checkUniqueUserLiveData.value = RepositoryProgress.NOT_STARTED
        _updatedLiveData.value = RepositoryProgress.NOT_STARTED
        _checkValidUniversityLiveData.value = RepositoryProgress.NOT_STARTED

    }

    /**
     * Verifies if an university exists
     * Help method to "connect" the view with the repository
     */
    private fun verifyUniversity(university: String) {
        universityRepository.verify(university)
    }

    /**
     * Returns all the universities inside the [universityRepository]
     * Help method to "connect" the view with the repository
     */
    fun retrieveAllUnis() {
        universityRepository.retrieve()
    }

    /**
     * Gets an user [nickname] and verifies if its unique
     * Help method to "connect" the view with the repository
     */
    private fun verifyUniqueUsername(nickname: String) {
        userRepository.getUsername(nickname)
    }

    /**
     * Updates a [student] information inside the [userRepository]
     * Help method to "connect" the view with the repository
     */
    fun submitUserInfos(student: Student) {
        userRepository.update(student)
    }

    /**
     * Deletes a [student] from the [userRepository]
     * Help method to "connect" the view with the repository
     */
    private fun deleteStudent(student: Student) {
        userRepository.delete(student)
    }

    /**
     * Deletes [student] entry from database, associated storage directory and Firebase user authentication
     */
    private fun deleteAccount(context: Context, student: Student) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.client_id))
            .requestEmail()
            .build()
        val user = firebaseAuth.currentUser

        val credential = GoogleAuthProvider.getCredential(
            GoogleSignIn.getLastSignedInAccount(context)?.idToken,
            null
        )
        user?.reauthenticate(credential)

        storageReference.getReference(Constants.STORAGE_PATH + student.id + Constants.IMG_DIR)
            .delete()
        
        deleteStudent(student)
        user?.delete()
            ?.addOnSuccessListener {
                GoogleSignIn.getClient(context, gso).revokeAccess()
                val signInIntent = Intent(context, SignInActivity::class.java)
                context.startActivity(signInIntent)
            }
        signOut(context) // ToDo Prüfe ob SignOut jetzt immer richtig funktioniert, wenn ja, kann dies in einer Woche entfernt werden
    }

    /**
     * Help method for signing an user out
     */
    fun signOut(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.client_id))
            .requestEmail()
            .build()
        firebaseAuth.signOut()
        GoogleSignIn.getClient(context, gso).signOut()
        val signInIntent = Intent(context, SignInActivity::class.java)
        context.startActivity(signInIntent)
    }

    /**
     * Starts home activity if submissions were accepted
     */
    fun updateUserInfos(
        newValue: RepositoryProgress,
        context: Context,
        submitListener: ISubmitListener
    ) {
        if (newValue == RepositoryProgress.SUCCESS) {
            Toast.makeText(
                context, context.getString(R.string.update_successfully), Toast.LENGTH_SHORT
            ).show()
            submitListener.submit()
        } else if (newValue == RepositoryProgress.FAILED) {
            Toast.makeText(
                context, context.getString(R.string.failed), Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * If university check was passed, start username verification,
     * else displays appropriate error message
     */
    fun checkUsername(
        newValue: RepositoryProgress,
        username: String,
        student: Student,
        submitListener: ISubmitListener,
        context: Context
    ) {
        if (newValue == RepositoryProgress.SUCCESS) {
            if (username != student.nickname)
                verifyUniqueUsername(username)
            else {
                submitListener.updateCurrentUser()
            }
        } else if (newValue == RepositoryProgress.FAILED) {
            Toast.makeText(context, context.getString(R.string.uni_not_found), Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * If name check was passed, starts information submission,
     * else displays appropriate error message
     */
    fun submitChanges(
        newValue: RepositoryProgress,
        submitListener: ISubmitListener,
        context: Context
    ) {
        when (newValue) {
            RepositoryProgress.SUCCESS -> {
                submitListener.updateCurrentUser()
            }
            RepositoryProgress.FAILED -> Toast.makeText(
                context,
                context.getString(R.string.nickname_exists),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Checks if any input fields are empty,
     * if not, starts university verification,
     * else displays appropriate error message
     */
    fun verifyValidInfos(
        username: String,
        university: String,
        studyCourse: String,
        context: Context
    ) {
        when {
            username.isEmpty() -> Toast.makeText(
                context,
                context.getString(R.string.name_empty),
                Toast.LENGTH_SHORT
            ).show()
            university.isEmpty() -> Toast.makeText(
                context,
                context.getString(R.string.university_empty),
                Toast.LENGTH_SHORT
            ).show()
            studyCourse.isEmpty() -> Toast.makeText(
                context,
                context.getString(R.string.study_course_empty),
                Toast.LENGTH_SHORT
            ).show()
            else -> {
                verifyUniversity(university)
            }
        }
    }

    /**
     * Goes through [student]'s list of groups,
     * deletes the corresponding member entry and decrements group member counter
     */
    fun quitAllGroups(context: Context, student: Student) {
        if (student.studyGroupList.isNotEmpty()) {
            var member: Member
            student.studyGroupList.forEach {
                member = Member(student.id, it)
                memberRepository.delete(member)
                groupRepository.decrementNumberOfMember(it)
            }
        } else {
            profileViewModelListener.delete(context, student)
        }
    }

    /**
     * Goes through [student]'s list of groups and updates admin credentials
     * Method called on deletion of [student]'s user account
     */
    private fun updateAdminRights(student: Student) {
        student.studyGroupList.forEach {
            memberRepository.updateAdminsOfGroup(it)
        }
    }

    /**
     * Goes through [student]'s list of groups and deletes all the ones which are empty
     * Method called on deletion of [student]'s user account
     */
    private fun deleteEmptyGroup(student: Student) {
        student.studyGroupList.forEach {
            groupRepository.deleteEmptyGroup(it)
        }
    }

    /**
     * On every successful deletion of member entry decrements [deleteCounter]
     * If [deleteCounter] reaches zero, starts deletion of empty groups
     */
    fun observeDeleteMember(newValue: RepositoryProgress, currentStudent: Student, counter: Int, profileListener: IProfileListener) {
        if(newValue == RepositoryProgress.SUCCESS) {
            profileListener.setDeleteCounter(counter - 1)
        }
        if(profileListener.getDeleteCounter() == 0) {
            updateAdminRights(currentStudent)
            profileListener.setDeleteCounter(currentStudent.studyGroupList.size)
        }
    }

    /**
     * On every successful update of admin credentials decrements [deleteCounter]
     * If [deleteCounter] reaches zero, starts deletion of empty groups
     */
    fun observeUpdateAdminsOfGroup(newValue: RepositoryProgress, student: Student, deleteCounter: Int, profileListener: IProfileListener) {
        if(newValue == RepositoryProgress.SUCCESS) {
            profileListener.setDeleteCounter(deleteCounter - 1)
        }
        if (profileListener.getDeleteCounter() == 0) {
            deleteEmptyGroup(student)
            profileListener.setDeleteCounter(student.studyGroupList.size)
        }
    }

    /**
     * On every successful deletion of empty group decrements [deleteCounter]
     * If [deleteCounter] reaches zero, starts deletion of user from the database
     */
    fun observeDeleteGroup(newValue: RepositoryProgress, student: Student, counter: Int, profileListener: IProfileListener, context: Context) {
        if(newValue == RepositoryProgress.SUCCESS) {
            profileListener.setDeleteCounter(counter - 1)
        }
        if (profileListener.getDeleteCounter() == 0) {
            profileViewModelListener.delete(context, student)
        }
    }

    var profileViewModelListener = object: IProfileViewModelListener {
        override fun delete(context: Context, student: Student) {
            deleteAccount(context, student)
        }

    }

    interface IProfileViewModelListener {
        fun delete(context: Context, student: Student)
    }
}