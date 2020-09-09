package com.pse.peer2learn.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pse.peer2learn.models.Appointment
import com.pse.peer2learn.models.Category
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress
/**
 * The class is used to manage instances of the model class [Category] and [Appointment] inside the Firebase database.
 */
class AppointmentRepository(
    private val createLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val deleteLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val updateLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val retrieveLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val retrievedCategoriesLiveData: MutableLiveData<ArrayList<Category>> = MutableLiveData()
): AbstractRepository<Category>() {
    /***
     * Creates a Category [instance] inside the category collection.
     */
    override fun create(instance: Category) {
        createLiveData.value = RepositoryProgress.IN_PROGRESS
        val doc = firebaseInstance.collection(Constants.GROUPS_COLLECTION)
            .document(instance.groupId)
            .collection(Constants.CATEGORY_COLLECTION)
            .document()
        instance.id = doc.id
        doc.set(instance)
            .addOnSuccessListener {
                createLiveData.value = RepositoryProgress.SUCCESS
            }.addOnFailureListener {
                createLiveData.value = RepositoryProgress.FAILED
            }
    }
    /***
     * Deletes a Category [instance] inside the category collection.
     */
    override fun delete(instance: Category) {
        deleteLiveData.value = RepositoryProgress.IN_PROGRESS
        firebaseInstance.collection(Constants.GROUPS_COLLECTION)
            .document(instance.groupId)
            .collection(Constants.CATEGORY_COLLECTION)
            .document(instance.id)
            .delete()
            .addOnSuccessListener {
                deleteLiveData.value = RepositoryProgress.SUCCESS
            }.addOnFailureListener {
                deleteLiveData.value = RepositoryProgress.FAILED
            }
    }
    /***
     * Updates a Category [instance] inside the category collection.
     */
    override fun update(instance: Category) {
        updateLiveData.value = RepositoryProgress.IN_PROGRESS
        firebaseInstance.collection(Constants.GROUPS_COLLECTION)
            .document(instance.groupId)
            .collection(Constants.CATEGORY_COLLECTION)
            .document(instance.id)
            .set(instance)
            .addOnSuccessListener {
                updateLiveData.value = RepositoryProgress.SUCCESS
            }.addOnFailureListener {
                updateLiveData.value = RepositoryProgress.FAILED
            }
    }
    /***
     * Retrieves a list of all categories, that are inside the group with [groupId]
     */
    fun retrieve(groupId: String) {
        val categories = arrayListOf<Category>()
        retrieveLiveData.value = RepositoryProgress.IN_PROGRESS
        firebaseInstance.collection(Constants.GROUPS_COLLECTION)
            .document(groupId)
            .collection(Constants.CATEGORY_COLLECTION)
            .get()
            .addOnSuccessListener { docs ->
                for (document in docs) {
                    categories.add(document.toObject(Category::class.java))
                }
                retrievedCategoriesLiveData.value = categories
                retrieveLiveData.value = RepositoryProgress.SUCCESS
            }.addOnFailureListener {
                retrieveLiveData.value = RepositoryProgress.FAILED
            }
    }
}