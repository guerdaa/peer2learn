package com.pse.peer2learn.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MessageRepository
import com.pse.peer2learn.ui.activities.interfaces.IHomeListener
import com.pse.peer2learn.utils.RepositoryProgress

/**
 * Holds the logic of the HomeActivity
 */
class HomeViewModel: ViewModel {

    private val _retrieveLiveData = MutableLiveData<RepositoryProgress>()
    val retrieveLiveData: LiveData<RepositoryProgress>
        get() = _retrieveLiveData

    private val _retrievedUserGroupsLiveData = MutableLiveData<ArrayList<StudyGroup>>()
    val retrievedUserGroupsLiveData: LiveData<ArrayList<StudyGroup>>
        get() = _retrievedUserGroupsLiveData

    private val _retrievedLastMessages = MutableLiveData<HashMap<String, Message>>()
    val retrievedLastMessages: LiveData<HashMap<String, Message>>
        get() = _retrievedLastMessages

    private val _retrievedMessages = MutableLiveData<RepositoryProgress>()
    val retrievedMessages: LiveData<RepositoryProgress>
        get() = _retrievedMessages

    init {
        _retrieveLiveData.value = RepositoryProgress.NOT_STARTED
        _retrievedUserGroupsLiveData.value = arrayListOf()
        _retrievedLastMessages.value = hashMapOf()
    }

    private var groupRepository = GroupRepository(
        retrieveLiveData = _retrieveLiveData,
        retrievedResultLiveData = _retrievedUserGroupsLiveData
    )

    private var messageRepository = MessageRepository(
        retrievedLastMessages = _retrievedLastMessages,
        retrievedLiveData = _retrievedMessages
    )

    constructor()

    constructor(groupRepository: GroupRepository, messageRepository: MessageRepository): this() {
        this.groupRepository = groupRepository
        this.messageRepository = messageRepository
    }

    /**
     * Retrieves a lists of groups from an [listOfId] Array that contains the ids of the joined by the user groups
     * Help method to "connect" the view with the repository
     */
    fun retrieveUserGroups(listOfId: ArrayList<String>) {
        groupRepository.retrieveGroupsOfUser(listOfId)
    }
    /**
     * Puts last messages from each group with id in [listOfId] to RetrievedLastMessages LiveData
     */
    private fun retrieveLastMessage(listOfId: ArrayList<String>) {
        messageRepository.retrieveLastMessages(listOfId)
    }

    /**
     * Retrieves last messages from each groups [currentStudent] is member of
     * Called every time HomeActivity is created
     */
    fun observeRetrieve(newValue: RepositoryProgress, currentStudent: Student) {
        if (newValue == RepositoryProgress.SUCCESS && currentStudent.studyGroupList.size != 0) {
            retrieveLastMessage(currentStudent.studyGroupList)
        }
    }

    /**
     * Calls on HomeActivity to create a RecyclerViewAdapter to put groups of [currentStudent] into
     * Called every time HomeActivity is created
     */
    fun observeRetrieveMessage(newValue: RepositoryProgress, currentStudent: Student, homeListener: IHomeListener) {
        if (newValue == RepositoryProgress.SUCCESS && currentStudent.studyGroupList.size != 0) {
            homeListener.initRecyclerViewAdapter()
        }
    }
}