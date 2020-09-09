package com.pse.peer2learn.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.models.Category
import com.pse.peer2learn.repositories.*
import com.pse.peer2learn.ui.activities.interfaces.IAppointementsOverviewListener
import com.pse.peer2learn.utils.RepositoryProgress

/**
 * Holds the logic of the AppointmentsOverviewFragment
 */
class AppointementsOverviewViewModel: ViewModel {

    private val _deleteLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData()

    private val _retrieveLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData()
    val retrieveLiveData: LiveData<RepositoryProgress> get() = _retrieveLiveData

    private val _retrievedCategoriesLiveData: MutableLiveData<ArrayList<Category>> = MutableLiveData()
    val retrievedCategoriesLiveData: LiveData<ArrayList<Category>> get() = _retrievedCategoriesLiveData

    init {
        _retrievedCategoriesLiveData.value = arrayListOf()
        _retrieveLiveData.value = RepositoryProgress.NOT_STARTED
    }
    
    private var appointementRepository = AppointmentRepository(
        deleteLiveData = _deleteLiveData,
        retrieveLiveData = _retrieveLiveData,
        retrievedCategoriesLiveData = _retrievedCategoriesLiveData
    )

    constructor()

    constructor(
        appointmentRepository: AppointmentRepository
    ) : this() {
        this.appointementRepository = appointmentRepository
    }

    /**
     * Retrieves categories from the [appointementRepository]
     * Help method to "connect" the view with the repository
     */
    fun retrieveCategories(groupId: String) {
        appointementRepository.retrieve(groupId)
    }
    /**
     * Deletes a [category] from the [appointementRepository]
     * Help method to "connect" the view with the repository
     */
    fun deleteCategory(category: Category) {
        appointementRepository.delete(category)
    }

    /**
     * Help method that is called when there is a new value in the retrieveLiveData and initialies the CategoriesRecyclerViewAdapter
     */
    fun observeRetrieve(newValue: RepositoryProgress, appointementsOverviewListener: IAppointementsOverviewListener) {
        if(newValue == RepositoryProgress.SUCCESS) {
            appointementsOverviewListener.initCategoriesRecyclerViewAdapter()
        }
    }
}