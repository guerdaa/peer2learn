package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Member
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.IHomeMenuListener
import com.pse.peer2learn.utils.RepositoryProgress
/**
 * Holds the logic of the HomeMenuFragment
 */
class HomeMenuViewModel: ViewModel {

    private val _deleteMemberLiveData = MutableLiveData<RepositoryProgress>()
    val deleteMemberLiveData: LiveData<RepositoryProgress>
        get() = _deleteMemberLiveData

    private val _deletedLiveData = MutableLiveData<RepositoryProgress>()
    val deletedLiveData: LiveData<RepositoryProgress>
        get() = _deletedLiveData

    private val _participantsCountLiveData = MutableLiveData<Int>()
    val participantsCountLiveData: LiveData<Int>
        get() = _participantsCountLiveData

    private val _updateAdminOfGroupLiveData = MutableLiveData<RepositoryProgress>()
    val updateAdminOfGroupLiveData: LiveData<RepositoryProgress>
        get() = _updateAdminOfGroupLiveData

    private val _updatedUserLiveData = MutableLiveData<RepositoryProgress>()
    val updatedUserLiveData: LiveData<RepositoryProgress>
        get() = _updatedUserLiveData

    private var memberRepository = MemberRepository(
        deleteMemberLiveData = _deleteMemberLiveData,
        participantsCountLiveData = _participantsCountLiveData,
        updateAdminOfGroupLiveData = _updateAdminOfGroupLiveData
    )

    private var userRepository = UserRepository(
        updatedLiveData = _updatedUserLiveData
    )

    private var groupRepository = GroupRepository(
        deleteLiveData = _deletedLiveData
    )

    constructor()

    constructor(userRepository: UserRepository, groupRepository: GroupRepository, memberRepository: MemberRepository): this() {
        this.memberRepository = memberRepository
        this.groupRepository = groupRepository
        this.userRepository = userRepository
    }
    init {
        _participantsCountLiveData.value = -1
    }

    /**
     * A help method that removes an user from the [memberRepository] when he quits a group
     */
    fun updateListOfMembers(userId: String, groupId: String) {
        val memberToDelete = Member(userId, groupId)
        memberRepository.delete(memberToDelete)
        groupRepository.decrementNumberOfMember(groupId)
    }
    /**
     * Changes the admins of a group with [groupId]
     * Help method to "connect" the view with the repository
     */
    private fun updateAdminOfGroup(groupId: String) {
        memberRepository.updateAdminsOfGroup(groupId)
    }

    /**
     * Returns the number of members in the group with [groupId]
     * Help method to "connect" the view with the repository
     */
    private fun getNumberOfParticipants(groupId: String) {
        memberRepository.getNumberOfParticipant(groupId)
    }
    /**
     * Deletes a [studyGroup] inside the [groupRepository]
     * Help method to "connect" the view with the repository
     */
    private fun deleteGroup(studyGroup: StudyGroup) {
        groupRepository.delete(studyGroup)
    }

    /**
     * Removes a group with [groupId] from the [student] list of groups
     * Help method to be used after a student leaves a group
     * Help method to "connect" the view with the repository
     */
    fun updateListOfGroup(student: Student, groupId: String) {
        student.studyGroupList.remove(groupId)
        userRepository.update(student)
    }

    /**
     * Updates list of groups in [homeMenuListener] View if a group was quitted from successfully
     */
    fun observeDeleteMember(newValue: RepositoryProgress, homeMenuListener: IHomeMenuListener, context: Context) {
        if(newValue == RepositoryProgress.SUCCESS) {
            homeMenuListener.updateListOfGroup()
        } else if(newValue == RepositoryProgress.FAILED) {
            homeMenuListener.dismissDialog(context.getString(R.string.failed))
        }
    }

    /**
     * Checks number of participants of a group that has been exitted from
     */
    fun observeUpdateUser(newValue: RepositoryProgress, currentGroupId: String, homeMenuListener: IHomeMenuListener, context: Context) {
        if(newValue == RepositoryProgress.SUCCESS) {
            getNumberOfParticipants(currentGroupId)
        } else if(newValue == RepositoryProgress.FAILED) {
            homeMenuListener.dismissDialog(context.getString(R.string.failed))
        }
    }

    /**
     * After leaving a group, depending on number of participants left, either:
     * - reassign admin credentials if group not empty
     * - delete group if it is
     */
    fun observeParticipantsCount(count: Int, currentGroup: StudyGroup) {
        when {
            count > 0 -> {
                updateAdminOfGroup(currentGroup.id)
            }
            count == 0 -> {
                deleteGroup(currentGroup)
            }
        }
    }

    /**
     * After leaving a group, if group has been deleted, displays appropriate message,
     * else display a generic error message
     */
    fun observeDeleteGroup(newValue: RepositoryProgress, homeMenuListener: IHomeMenuListener, context: Context) {
        if(newValue == RepositoryProgress.SUCCESS) {
            homeMenuListener.dismissDialog(context.getString(R.string.deleted))
        } else if(newValue == RepositoryProgress.FAILED) {
            homeMenuListener.dismissDialog(context.getString(R.string.failed))
        }
    }

    /**
     * After leabing the group, if a group admin was not reassigned properly, displays appropriate error message,
     * else display confirmation
     */
    fun observeUpdateAdminOfGroup(newValue: RepositoryProgress, homeMenuListener: IHomeMenuListener, context: Context) {
        if (newValue == RepositoryProgress.FAILED) {
            homeMenuListener.dismissDialog(context.getString(R.string.failed_to_update_admins))
        } else if (newValue == RepositoryProgress.SUCCESS) {
            homeMenuListener.dismissDialog(context.getString(R.string.quit_successfully))
        }
    }
}