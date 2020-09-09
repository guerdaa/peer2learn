package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.University
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MessageRepository
import com.pse.peer2learn.repositories.UniversityRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.ISubmitListener
import com.pse.peer2learn.utils.RepositoryProgress
/**
 * Holds the logic of the RegistrationActivity
 */
class RegistrationViewModel: ViewModel {

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

    private var universityRepository = UniversityRepository(
            checkValidUniversityLiveData =  _checkValidUniversityLiveData,
            retrievedUniList = _retrievedUniList
    )

    private var userRepository = UserRepository(
            updatedLiveData = _updatedLiveData,
            checkUniqueUserLiveData = _checkUniqueUserLiveData
    )

    constructor()

    constructor(userRepository: UserRepository, universityRepository: UniversityRepository): this() {
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
     * Vefifies if a [University] exists
     * Help method to "connect" the view with the repository
     */
    private fun verifyUniversity(university: String) {
        universityRepository.verify(university)
    }

    /**
     * Returns all the universities inside the [UniversityRepository]
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
     * Updates a [Student] information inside the [UserRepository]
     * Help method to "connect" the view with the repository
     */
    fun submitUserInfos(student: Student) {
        userRepository.update(student)
    }

    /**
     * Checks if any input fields are empty,
     * if not, starts university verification,
     * else displays appropriate error message
     */
    fun verifyValidInfos(username: String, university: String, studyCourse: String, context: Context) {
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
     * Starts home activity if submissions were accepted
     */
    fun updateInfos(newValue: RepositoryProgress, context: Context, submitListener: ISubmitListener) {
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

    fun checkUsername(newValue: RepositoryProgress, username: String, context: Context) {
        if (newValue == RepositoryProgress.SUCCESS) {
            verifyUniqueUsername(username)
        } else if (newValue == RepositoryProgress.FAILED) {
            Toast.makeText(context, context.getString(R.string.uni_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * If name check was passed, starts information submission,
     * else displays appropriate error message
     */

    fun submitChanges(newValue: RepositoryProgress, submitListener: ISubmitListener, context: Context) {
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

}