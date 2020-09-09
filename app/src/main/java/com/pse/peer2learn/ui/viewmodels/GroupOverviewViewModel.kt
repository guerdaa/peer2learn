package com.pse.peer2learn.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.models.Member
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.repositories.AppointmentRepository
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.utils.RepositoryProgress
/**
 * Holds the logic of the GroupOverviewFragment
 */
class GroupOverviewViewModel: ViewModel{

    private val _updateGroupLiveData = MutableLiveData<RepositoryProgress>()
    val updateGroupLiveData: LiveData<RepositoryProgress>
        get() = _updateGroupLiveData

    private var groupRepository = GroupRepository(
        updateLiveData = _updateGroupLiveData
    )

    private val _updateMemberLiveData = MutableLiveData<RepositoryProgress>()

    private var memberRepository = MemberRepository(
        updateMemberLiveData = _updateMemberLiveData

    )

    init {
        _updateGroupLiveData.value = RepositoryProgress.NOT_STARTED
        _updateMemberLiveData.value = RepositoryProgress.NOT_STARTED
    }
    constructor()

    constructor(groupRepository: GroupRepository, memberRepository: MemberRepository): this() {
        this.groupRepository = groupRepository
        this.memberRepository = memberRepository
    }
    /**
     * Updates a [studyGroup] in the [groupRepository]
     * Help method to "connect" the view with the repository
     */
    fun updateStudyGroup(studyGroup: StudyGroup) {
        groupRepository.update(studyGroup)
    }

    /**
     * Sets the [member] isAdmin attribute to true and then updates it in the [memberRepository]
     */
    fun giveAdminRight(member: Member) {
        member.isAdmin = true
        memberRepository.update(member)
    }
}