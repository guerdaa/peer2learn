package com.pse.peer2learn.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress

/**
 * The class is used to manage instances of the model class [Student] inside the Firebase database.
 */

class UserRepository(
    private val createdLiveData: MutableLiveData<Boolean> = MutableLiveData(),
    private val retrievedLiveData: MutableLiveData<Student?> = MutableLiveData(),
    private val updatedLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val checkUniqueUserLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData()
) : AbstractRepository<Student>(){

    private val userCollection = firebaseInstance.collection(Constants.USER_COLLECTION)
    /***
     * Creates a new student [instance] in the [userCollection]
     */
    override fun create(instance: Student) {
        userCollection.document(instance.id)
            .set(instance)
            .addOnSuccessListener {
                createdLiveData.value = true
                Log.d(TAG, "User created successfully")
            }.addOnFailureListener {
                createdLiveData.value = false
                Log.d(TAG, "User creation failed")
            }
    }
    /***
     * Deletes a student [instance] in the [userCollection]
     */
    override fun delete(instance: Student) {
        userCollection.document(instance.id).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "instance deleted")
                }.addOnFailureListener {
                    Log.d(TAG, "update failed")
                }
    }
    /***
     * Updates a student [instance] in the [userCollection]
     */
    override fun update(instance: Student) {
        updatedLiveData.value = RepositoryProgress.IN_PROGRESS
        userCollection.document(instance.id)
            .set(instance)
            .addOnSuccessListener {
                Log.d(TAG, "instance updated")
                updatedLiveData.value = RepositoryProgress.SUCCESS
            }.addOnFailureListener {
                Log.d(TAG, "update failed")
                updatedLiveData.value = RepositoryProgress.FAILED
            }
    }
    /***
     * Retrieves a student from the [userCollection] that corresponds to [userId]
     */
    fun retrieve(userId: String) {
        userCollection.document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.toObject(Student::class.java) != null) {
                    retrievedLiveData.value = doc.toObject(Student::class.java)
                } else {
                    retrievedLiveData.value = null
                }
            }
    }

    /**
     * Verifies if a user has already this [username].
     * Help method to check whether a username is unique.
     */
    fun getUsername(username: String) {
        checkUniqueUserLiveData.value = RepositoryProgress.IN_PROGRESS
        userCollection.whereEqualTo("nickname", username)
            .get()
            .addOnSuccessListener {docs ->
                if (docs.documents.isNotEmpty()) {
                    Log.d("------------", docs.documents.size.toString())
                    checkUniqueUserLiveData.value = RepositoryProgress.FAILED
                } else {
                    checkUniqueUserLiveData.value = RepositoryProgress.SUCCESS
                }
            }
    }

    companion object {
        const val TAG = "UserRepository"
    }
}