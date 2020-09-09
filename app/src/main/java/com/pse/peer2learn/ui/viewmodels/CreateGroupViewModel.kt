package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Member
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.PrivateGroupRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.ICreateGroupListener
import com.pse.peer2learn.utils.RepositoryProgress
import java.util.*
/**
 * Holds the logic of the CreateGroupActivity
 */
class CreateGroupViewModel: ViewModel {

    private val _createGroupLiveData = MutableLiveData<RepositoryProgress>()
    val createGroupLiveData: LiveData<RepositoryProgress>
        get() = _createGroupLiveData

    private val _groupIdLiveData = MutableLiveData<String>()
    val groupIdLiveData: LiveData<String>
        get() = _groupIdLiveData

    private val _updateMemberLiveData = MutableLiveData<RepositoryProgress>()
    val updateMemberLiveData: LiveData<RepositoryProgress>
        get() = _updateMemberLiveData

    private val _updateUserGroupsLiveData = MutableLiveData<RepositoryProgress>()
    val updateUserGroupsLiveData: LiveData<RepositoryProgress>
        get() = _updateUserGroupsLiveData

    private val _accessCodeUniqueLiveData = MutableLiveData<RepositoryProgress>()
    val accessCodeUnique: LiveData<RepositoryProgress>
        get() = _accessCodeUniqueLiveData

    private val _privateGroupCreated = MutableLiveData<RepositoryProgress>()
    val privateGroupCreated: LiveData<RepositoryProgress>
        get() = _privateGroupCreated

    var groupRepository = GroupRepository(
        createLiveData = _createGroupLiveData,
        groupIdLiveData = _groupIdLiveData
    )
    private var memberRepository = MemberRepository(
        updateMemberLiveData = _updateMemberLiveData
    )
    private var userRepository = UserRepository(
        updatedLiveData = _updateUserGroupsLiveData
    )

    private var privateGroupRepository = PrivateGroupRepository(
        privateGroupCreated = _privateGroupCreated,
        accessCodeUnique =  _accessCodeUniqueLiveData
    )

    var privatAccessCode = ""


    constructor()

    constructor(groupRepository: GroupRepository, userRepository: UserRepository,
                privateGroupRepository: PrivateGroupRepository, memberRepository: MemberRepository) : this() {
        this.groupRepository = groupRepository
        this.userRepository = userRepository
        this.privateGroupRepository = privateGroupRepository
        this.memberRepository = memberRepository
    }

    /**
     * Creates the connection between View and Repository for creating a new [studyGroup]
     */
    fun createPublicGroup(studyGroup: StudyGroup) {
        groupRepository.create(studyGroup)
    }
    /**
     * Updates a member in the [memberRepository]
     * Help method to "connect" the view with the repository
     */
    private fun updateMemberOfGroup(student: Student, groupId: String?) {
        groupId?.let {
            val member = Member(student.id, it,true, student.nickname, System.currentTimeMillis())
            memberRepository.update(member)
        }
    }
    /**
     * Adds a group with [groupId] inside of the [student] list of groups
     * Help method to "connect" the view with the repository
     */
    private fun addGroupToUserRepository(student: Student, groupId: String) {
        student.studyGroupList.add(groupId)
        userRepository.update(student)
    }

    /**
     * Sets the [accessCode] of an private group, if its unique
     * Help method to "connect" the view with the repository
     */
    private fun setAccessCode(accessCode: String) {
        privateGroupRepository.verifyUniqueAccessCode(accessCode)
    }

    /**
     * Creates a private group inside the [privateGroupRepository] //welche von beide * klingt besser?
     * Call the method for creation of a private group inside the [privateGroupRepository]
     * Help method to "connect" the view with the repository
     */
    fun createPrivateGroup(studyGroup: StudyGroup) {
        privateGroupRepository.create(studyGroup)
        _groupIdLiveData.value = studyGroup.id
    }
    /**
     * Help method that is called when there is a new value in the createGroupLiveData and initialies and updates the members of the group with [groupId] if the creation was successfull
     * Shows suitable Toast on fail
     */
    fun observeCreateGroup(newValue: RepositoryProgress, currentStudent: Student, groupId: String?, context: Context) {
        if (newValue == RepositoryProgress.SUCCESS) {
            updateMemberOfGroup(
                currentStudent, groupId)
        } else if (newValue == RepositoryProgress.FAILED) {
            Toast.makeText(
                context,
                context.getString(R.string.group_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    /**
     * Help method that is called when there is a new value in the updateMemberLiveData
     */
    fun observeUpdateMember(newValue: RepositoryProgress, currentStudent: Student, groupId: String?, context: Context) {
        if(newValue == RepositoryProgress.SUCCESS) {
            addGroupToUserRepository(currentStudent, groupId!!)
        } else if (newValue == RepositoryProgress.FAILED) {
            Toast.makeText(
                context,
                context.getString(R.string.group_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    /**
     * Help method that is called when there is a new value in the updateUserGroupsLiveDate and shows a suitable Toast
     */
    fun observeUpdateUserGroups(newValue: RepositoryProgress, createGroupListener: ICreateGroupListener, context: Context) {
        if(newValue == RepositoryProgress.SUCCESS) {
            if(privatAccessCode.isNotEmpty()) {
                createGroupListener.showPrivateGroupCreatedFragment()
                privatAccessCode = ""
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.group_created),
                    Toast.LENGTH_SHORT
                ).show()
                createGroupListener.navigateHome()
            }
        } else if(newValue == RepositoryProgress.FAILED) {
            Toast.makeText(
                context,
                context.getString(R.string.group_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Help method that is called when there is a new value in the accessCodeUnique LiveData and depending on that whether its unique or not the code is setted to the group or a new code is generated
     */
    fun observeAccessCode(newValue: RepositoryProgress, createGroupListener: ICreateGroupListener) {
        if(newValue == RepositoryProgress.SUCCESS) {
            createGroupListener.setAccessCode()
        } else if (newValue == RepositoryProgress.FAILED) {
            generateAccessCode()
        }
    }

    /**
     * Assures that all the forms in the Group Activity that are required for the creation of the group are correctly set.
     * Checks if the [title] field of the group is empty or too long
     * Checks if the [description] field of the group is empty
     * Checks if the [numberOfPersons] is in the allowed range
     * Checks if the [language] field is empty
     */
    fun validateFormValues(title: String, description: String, numberOfPersons: Int, language: String, context: Context): Boolean {
        if (title.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.title_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (title.length > 64) {
            Toast.makeText(context, context.getString(R.string.title_too_long), Toast.LENGTH_SHORT).show()
            return false
        }

        if (description.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.description_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (numberOfPersons <= 0 || numberOfPersons > 99) {
            Toast.makeText(context, context.getString(R.string.invalid_num_of_persons), Toast.LENGTH_SHORT).show()
            return false
        }
        if (language.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.language_required), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Generates a randon String, that is used as an access code
     */
    fun generateAccessCode() {
        val randomUI = UUID.randomUUID().toString().subSequence(0, 8).toString()
        setAccessCode(randomUI)
        privatAccessCode = randomUI
    }
}