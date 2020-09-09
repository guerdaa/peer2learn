package com.pse.peer2learn.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress
/**
 * The class is used to manage instances of the model class [StudyGroup] inside the Firebase database.
 */
class PrivateGroupRepository(
    private val privateGroupCreated: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val accessCodeUnique: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val retrievedGroup: MutableLiveData<StudyGroup> = MutableLiveData(),
    private val retrievedState: MutableLiveData<RepositoryProgress> = MutableLiveData()
): AbstractRepository<StudyGroup>() {

    private val privateGroupCollection = firebaseInstance.collection(Constants.GROUPS_COLLECTION)

    /**
     * Creates an private Study Group [instance] inside the [privateGroupCollection]
     */
    override fun create(instance: StudyGroup) {
        privateGroupCreated.value = RepositoryProgress.IN_PROGRESS
        privateGroupCollection
            .document(instance.accessCode)
            .set(instance)
            .addOnSuccessListener {
                privateGroupCreated.value = RepositoryProgress.SUCCESS
                Log.d(TAG, "Instance created and added")
            }.addOnFailureListener {
                privateGroupCreated.value = RepositoryProgress.FAILED
                Log.d(TAG, "Instance creation failed")
            }
    }

    /**
     * Deletes an private Study Group [instance] inside the [privateGroupCollection]
     */

    override fun delete(instance: StudyGroup) {
    }
    /**
     * Updates an private Study Group [instance] inside the [privateGroupCollection]
     */
    override fun update(instance: StudyGroup) {
    }
    /**
     * Retrieves an private Study Group corresponding to [accessCode] inside the [privateGroupCollection]
     */
    fun retrieve(accessCode: String) {
        retrievedState.value = RepositoryProgress.IN_PROGRESS
        privateGroupCollection.document(accessCode)
            .get()
            .addOnSuccessListener {
                retrievedGroup.value = it.toObject(StudyGroup::class.java)
                retrievedState.value = RepositoryProgress.SUCCESS
                Log.d(TAG, "retrieve: $accessCode")
            }.addOnFailureListener {
                Log.d(TAG, "retrieve: $accessCode")
                retrievedState.value = RepositoryProgress.FAILED
            }
    }

    /**
     * Verifies if an [accessCode] of a group is unique
     */
    fun verifyUniqueAccessCode(accessCode: String) {
        accessCodeUnique.value = RepositoryProgress.IN_PROGRESS

        privateGroupCollection.document(accessCode)
            .get()
            .addOnSuccessListener {
                if (it.toObject(StudyGroup::class.java) != null) {
                    Log.d(TAG, "verifyUniqueAccessCode: code not unique")
                    accessCodeUnique.value = RepositoryProgress.FAILED
                } else {
                    Log.d(TAG, "verifyUniqueAccessCode: $accessCode")
                    accessCodeUnique.value = RepositoryProgress.SUCCESS
                }
            }.addOnFailureListener {
                Log.d(TAG, "verifyUniqueAccessCode: failed")
                accessCodeUnique.value = RepositoryProgress.FAILED
            }
    }

    /***
     * Increases the number of members in the group with [groupId]
     */
    fun incrementNumberOfMember(groupId: String) {
        privateGroupCollection.document(groupId).update(Constants.NUMBER_OF_MEMBERS, FieldValue.increment(1))
    }


    companion object {
        private const val TAG = "PrivateGroupRepository"
    }

}