package com.pse.peer2learn.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.models.University
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress
import kotlin.collections.ArrayList

/**
 * The class is used to manage instances of the model class [StudyGroup] inside the Firebase database.
 */
class GroupRepository(
    private val retrieveLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val updateLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val deleteLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val createLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val groupIdLiveData: MutableLiveData<String> = MutableLiveData(),
    private val retrievedResultLiveData: MutableLiveData<ArrayList<StudyGroup>> = MutableLiveData()
) {

    private val groupCollection = FirebaseFirestore.getInstance().collection(Constants.GROUPS_COLLECTION)

    /***
     * Creates a StudyCroup [instance] in the [groupCollection].
     */
    fun create(instance: StudyGroup) {
        groupIdLiveData.value = ""
        createLiveData.value = RepositoryProgress.IN_PROGRESS
        val doc = groupCollection.document()
        instance.id = doc.id
        groupIdLiveData.value = instance.id
        doc.set(instance).addOnSuccessListener { _ ->
            createLiveData.value = RepositoryProgress.SUCCESS
            Log.d(TAG, "Instance created and added")
        }.addOnFailureListener {
            createLiveData.value = RepositoryProgress.FAILED
            Log.d(TAG, "Instance creation failed")
        }

    }

    /***
     * Deletes a StudyCroup [instance] in the [groupCollection]
     */
    fun delete(instance: StudyGroup) {
        deleteLiveData.value = RepositoryProgress.IN_PROGRESS
        if (instance.id.isNotEmpty()) {
            FirebaseStorage.getInstance().getReference("${Constants.CHAT_STORAGE_PATH}${instance.id}").delete()
            groupCollection
                .document(instance.id)
                .delete()
                .addOnSuccessListener {
                    deleteLiveData.value = RepositoryProgress.SUCCESS
                    Log.d(TAG, "Instance deleted")
                }.addOnFailureListener {
                    deleteLiveData.value = RepositoryProgress.FAILED
                    Log.d(TAG, "Instance deletion failed")
                }
        }
    }

    /***
     * Updates a StudyGroup [instance] in the [groupCollection]
     */
    fun update(instance: StudyGroup) {
        updateLiveData.value = RepositoryProgress.IN_PROGRESS
        if (instance.id.isNotEmpty()) {
            groupCollection
                .document(instance.id)
                .set(instance)
                .addOnSuccessListener {
                    updateLiveData.value = RepositoryProgress.SUCCESS
                }.addOnFailureListener {
                    updateLiveData.value = RepositoryProgress.FAILED
                }
        }
    }

    /***
     * Retrieves a StudyGroup corresponding to [nameOfModule] and [university] inside the [groupCollection].
     */
    fun retrieve(nameOfModule: String, university: University) {
        val resultsGroup = arrayListOf<StudyGroup>()
        retrieveLiveData.value = RepositoryProgress.IN_PROGRESS
        groupCollection
            .whereEqualTo(Constants.UNIVERSITY_ATTRIBUT, university)
            .whereEqualTo(Constants.TITLE_ATTRIBUT, nameOfModule)
            .whereEqualTo(Constants.ACCESS_CODE_ATTRIBUT, StudyGroup.NON_PRIVATE_GROUP)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.toObject(StudyGroup::class.java).let {
                        if (it.numberOfMembers < it.maxMembers) {
                            resultsGroup.add(it)
                        }
                    }
                }
                retrieveLiveData.value = RepositoryProgress.SUCCESS
                retrievedResultLiveData.value = resultsGroup
            }.addOnFailureListener {
                retrieveLiveData.value = RepositoryProgress.FAILED
            }
    }

    /**
     * Retrieves the groups, using [listOfId] - an ArrayList that cointans the ids of the groups in which the current user participates.
     * This is a help method that is called when HomeActivity is started to show the joined groups of the current user.
     */
    fun retrieveGroupsOfUser(listOfId: ArrayList<String>) {
        retrieveLiveData.value = RepositoryProgress.IN_PROGRESS
        for (i in 0 until listOfId.size) {
            groupCollection.document(listOfId[i])
                .get()
                .addOnSuccessListener { doc ->
                    doc.toObject(StudyGroup::class.java)?.let { studyGroup ->
                        retrievedResultLiveData.value!!.add(studyGroup)
                        if (i == listOfId.size - 1) {
                            retrieveLiveData.value = RepositoryProgress.SUCCESS
                        }
                    }
                }.addOnFailureListener {
                    retrieveLiveData.value = RepositoryProgress.FAILED
                }
        }
    }

    /**
     * Deletes an group with [groupId], if its number of members is zero
     * This is a help method that is called after an user quits a group or deletes his account
     */
    fun deleteEmptyGroup(groupId: String) {
        deleteLiveData.value = RepositoryProgress.IN_PROGRESS
        groupCollection.document(groupId)
            .get().addOnSuccessListener { doc ->
                doc.toObject(StudyGroup::class.java)?.let {
                    if(it.numberOfMembers == 0) {
                        doc.reference.delete().addOnSuccessListener {
                            deleteLiveData.value = RepositoryProgress.SUCCESS
                        }
                    } else {
                        deleteLiveData.value = RepositoryProgress.SUCCESS
                    }
                }
            }
    }

    /***
     * Increases the number of members in the group with [groupId]
     */
    fun incrementNumberOfMember(groupId: String) {
        groupCollection.document(groupId).update(Constants.NUMBER_OF_MEMBERS, FieldValue.increment(1))
    }

    /***
     * Decreases the number of members in the group with [groupId]
     */
    fun decrementNumberOfMember(groupId: String) {
        groupCollection.document(groupId)
            .update(Constants.NUMBER_OF_MEMBERS, FieldValue.increment(-1))
    }

    companion object {
        private const val TAG = "GroupRepository"
    }

}