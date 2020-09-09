package com.pse.peer2learn.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.University
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress

/**
 * This class is used to manage instances of the [University] model class inside the Firebase database
 */
class UniversityRepository(
        private val retrievedUniList: MutableLiveData<ArrayList<University>> = MutableLiveData(),
        private val checkValidUniversityLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData()
) {
    private val uniCollection = FirebaseFirestore.getInstance().collection(Constants.UNIVERSITY_COLLECTION)

    /**
     * Verifies whether a university with [shortName] exists in the database
     */
    fun verify(shortName: String) {
        checkValidUniversityLiveData.value = RepositoryProgress.IN_PROGRESS
        uniCollection.whereEqualTo("shortName", shortName).get().addOnSuccessListener { sName ->
            if (sName.documents.isNotEmpty()) {
                checkValidUniversityLiveData.value = RepositoryProgress.SUCCESS
            }
            else {
                checkValidUniversityLiveData.value = RepositoryProgress.FAILED
            }
        }
    }

    /**
     * Gets a list of all universities from the database
     */
    fun retrieve() {
        val results = arrayListOf<University>()
        uniCollection.get().addOnSuccessListener { documents ->
            for (document in documents) {
                results.add(document.toObject(University::class.java))
            }
            retrievedUniList.value = results
        }
    }
}