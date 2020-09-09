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
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.PrivateGroupRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.IGroupSearchPrivateListener
import com.pse.peer2learn.utils.RepositoryProgress
/**
 * Holds the logic of the GroupSearchPrivateFragment
 */

class GroupSearchPrivateViewModel: ViewModel {

    private val _retrievedGroup = MutableLiveData<StudyGroup>()
    val retrievedGroup: LiveData<StudyGroup>
        get() = _retrievedGroup

    private val _retrievedState = MutableLiveData<RepositoryProgress>()

    private val _updateUserLiveData = MutableLiveData<RepositoryProgress>()
    val updateUserLiveData: LiveData<RepositoryProgress>
        get() = _updateUserLiveData

    private val _groupAlreadyJoined = MutableLiveData<Boolean>()
    val groupAlreadyJoined: LiveData<Boolean>
        get() = _groupAlreadyJoined


    private var userRepository = UserRepository(
        updatedLiveData = _updateUserLiveData
    )

    private var memberRepository = MemberRepository()

    private var privateGroupRepository = PrivateGroupRepository(
        retrievedGroup = _retrievedGroup,
        retrievedState = _retrievedState
    )

    constructor()

    constructor(userRepository: UserRepository, memberRepository: MemberRepository, privateGroupRepository: PrivateGroupRepository): this() {
        this.userRepository = userRepository
        this.memberRepository = memberRepository
        this.privateGroupRepository = privateGroupRepository
    }

    init {
        _groupAlreadyJoined.value = false
    }
    /**
     * Checks if there is a private group with [accessCode] inside the [privateGroupRepository]
     * Help method to "connect" the view with the repository
     */
    fun findPrivateGroup(accessCode: String) {
        privateGroupRepository.retrieve(accessCode)
    }
    /**
     * Adds a group to the user lists of group, if he is not already member
     * Help method to "connect" the view with the repository
     */
    fun joinGroup(student: Student, groupId: String) {
        if (!student.studyGroupList.contains(groupId)) {
            val member = Member(student.id, groupId,false, student.nickname, System.currentTimeMillis())
            memberRepository.update(member)
            student.studyGroupList.add(groupId)
            userRepository.update(student)
            privateGroupRepository.incrementNumberOfMember(groupId)
            _groupAlreadyJoined.value = false
        } else {
            _groupAlreadyJoined.value = true
        }

    }
    /**
     * Help method that is called when there is a new value in the retrievedGroupLiveData and joins a group if it exists
     */
    fun observeRetrieveGroup(newValue: StudyGroup?, groupSearchPrivateListener: IGroupSearchPrivateListener,context: Context) {
        if(newValue != null) {
            groupSearchPrivateListener.joinGroup()
        } else {
            Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * Help method that is called when there is a new value in the updateUserLiveData and show suitable Toast
     */
    fun observeUpdateUser(newValue: RepositoryProgress, context: Context) {
        if(newValue == RepositoryProgress.SUCCESS) {
            Toast.makeText(context, context.getString(R.string.group_joined), Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * Help method that is called when there is a new value in the groupAlreadyJoinedLiveData and shows suitable Toast
     */
    fun observeGroupAlreadyJoined(newValue: Boolean, context: Context) {
        if(newValue) {
            Toast.makeText(context, context.getString(R.string.group_already_joined), Toast.LENGTH_SHORT).show()
        }
    }
}
