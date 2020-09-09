package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.R
import com.pse.peer2learn.models.*
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.IGroupSearchPublicListener
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress

/**
 * Holds the logic of the GroupSearchPublicActivity
 */
class GroupSearchPublicViewModel: ViewModel {


    private var _retrievedResultLiveData = MutableLiveData<ArrayList<StudyGroup>>()
    val retrievedResultLiveData: LiveData<ArrayList<StudyGroup>>
        get() = _retrievedResultLiveData

    private val _updateMemberLiveData = MutableLiveData<RepositoryProgress>()
    val updateMemberLiveData: LiveData<RepositoryProgress>
        get() = _updateMemberLiveData

    private val _updateUserGroupsLiveData = MutableLiveData<RepositoryProgress>()


    private val _groupAlreadyJoined = MutableLiveData<Boolean>()
    val groupAlreadyJoined: LiveData<Boolean>
        get() = _groupAlreadyJoined

    private val _participantsCountLiveData = MutableLiveData<Int>()

    init {
        _retrievedResultLiveData.value = arrayListOf()
        _groupAlreadyJoined.value = false

    }

    private var groupRepository = GroupRepository(
        retrievedResultLiveData = _retrievedResultLiveData
    )

    private var memberRepository = MemberRepository(
        updateMemberLiveData = _updateMemberLiveData,
        participantsCountLiveData = _participantsCountLiveData
    )
    private var userRepository = UserRepository(
        updatedLiveData = _updateUserGroupsLiveData
    )

    constructor()

    constructor(userRepository: UserRepository, memberRepository: MemberRepository, groupRepository: GroupRepository): this() {
        this.userRepository = userRepository
        this.memberRepository = memberRepository
        this.groupRepository = groupRepository
    }

    /**
     * Retrieves a list of groups corresponding to the [nameOfModule] and [university] inside the [groupRepository]
     * Help method to "connect" the view with the repository
     */
    private fun retrieveGroups(nameOfModule: String, university: University) {
        _retrievedResultLiveData.value?.clear()
        groupRepository.retrieve(nameOfModule, university)
    }

    /**
     * Returns a list of all groups that are complying with the [filters]
     */
    fun listAllGroups(filters: ArrayList<String>, arrayToFilter: ArrayList<StudyGroup>): ArrayList<StudyGroup> {
        return arrayToFilter.filterByLanguage(filters[Constants.LANGUAGE_FILTER_INDEX])
            .filterByProgress(filters[Constants.PROGRESS_FILTER_INDEX])
            .filterByMinParticipant(filters[Constants.NUMBER_OF_PERSONS_FILTER_INDEX])
    }

    /**
     * A help method that filters an ArrayList of [StudyGroup]  by [language]
     */
    private fun ArrayList<StudyGroup>.filterByLanguage(language: String): ArrayList<StudyGroup> {
        val returnResult = ArrayList<StudyGroup>()
        return if (language == "-") {
            this
        } else {
            forEach {
                if (it.language.name.equals(language, true)) {
                    returnResult.add(it)
                }
            }
            return returnResult
        }
    }
    /**
     * A help method that filters an ArrayList of [StudyGroup]  by [minNumberOfUsers]
     */
    private fun ArrayList<StudyGroup>.filterByMinParticipant(minNumberOfUsers: String): ArrayList<StudyGroup> {
        val returnResult = ArrayList<StudyGroup>()
        return if (minNumberOfUsers == "-") {
            this
        } else {
            forEach {
                if (it.numberOfMembers >= minNumberOfUsers.toInt()) {
                    returnResult.add(it)
                }
            }
            returnResult
        }
    }

    /**
     * A help method that filters an ArrayList of [StudyGroup]  by [progress]
     */
    private fun ArrayList<StudyGroup>.filterByProgress(progress: String): ArrayList<StudyGroup> {
        val returnResult = ArrayList<StudyGroup>()
        return if (progress == "-") {
            this
        } else {
            forEach {
                if (it.progress >= progress.removeSuffix("%").toInt()) {
                    returnResult.add(it)
                }
            }
            return returnResult
        }
    }
    /**
     * Adds a group to the user lists of group, if he is not already member
     * Help method to "connect" the view with the repository
     */
    fun joinGroup(student: Student, studyGroup: StudyGroup) {
        if (!student.studyGroupList.contains(studyGroup.id)) {
            val member = Member(student.id, studyGroup.id,false, student.nickname, System.currentTimeMillis())
            memberRepository.update(member)
            student.studyGroupList.add(studyGroup.id)
            userRepository.update(student)
            groupRepository.incrementNumberOfMember(studyGroup.id)
            _groupAlreadyJoined.value = false
        } else {
            _groupAlreadyJoined.value = true
        }
    }
    /**
     * Help method that is called when there is a new value in the RetrievedResultLiveData and if it isnt empty initialises the RecyclerViewAdapter
     */
    fun observeRetrievedList(newValue: ArrayList<StudyGroup>, groupSearchPublicListener: IGroupSearchPublicListener) {
        if (newValue.isNotEmpty()) {
            groupSearchPublicListener.initRecyclerViewAdapter()
        }
    }
    /**
     * Help method that is called when there is a new value in the groupAlreadyJoinedLiveData and show suitable Toast
     */
    fun observeGroupAlreadyJoined(newValue: Boolean, context: Context) {
        if (newValue) {
            Toast.makeText(context, context.getString(R.string.group_already_joined), Toast.LENGTH_SHORT)
                .show()
        }
    }
    /**
     * Help method that is called when there is a new value in the updateMemberLivedata and show suitable Toast
     */
    fun observeMemberUpdated(newValue: RepositoryProgress, context: Context) {
        if (newValue == RepositoryProgress.SUCCESS) {
            Toast.makeText(context, context.getString(R.string.group_joined), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Searches for a group with the chosen by the user filters, depending on that, whether the corresponding text field is empty or not
     */
    fun searchGroups(progress: String, language: String, minimumUsers: String, moduleName: String,
                             university: University?, groupSearchPublicListener: IGroupSearchPublicListener) {
        progress.let { text ->
            groupSearchPublicListener.setProgressText(if (text.isNullOrEmpty()) Constants.EMPTY_FILTER else "$text%")
        }
        language.let { text ->
            groupSearchPublicListener.setLanguageText(if (text.isNullOrEmpty()) Constants.EMPTY_FILTER else text)
        }
        minimumUsers.let { text ->
            groupSearchPublicListener.setMinimumUsersText(if (text.isNullOrEmpty()) Constants.EMPTY_FILTER else text)
        }
        retrieveGroups(
            moduleName,
            university!!
        )
    }
}