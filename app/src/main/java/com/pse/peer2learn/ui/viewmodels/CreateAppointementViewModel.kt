package com.pse.peer2learn.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.models.Category
import com.pse.peer2learn.repositories.AppointmentRepository
import com.pse.peer2learn.ui.activities.interfaces.ICreateAppointmentListener
import com.pse.peer2learn.utils.RepositoryProgress
/**
 * Holds the logic of the CreateAppointmentFragment
 */
class CreateAppointementViewModel: ViewModel {
    private val _updateLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData()
    val updateLiveData: LiveData<RepositoryProgress> get() = _updateLiveData

    private val _createLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData()
    val createLiveData: LiveData<RepositoryProgress> get() = _createLiveData

    private var appointmentRepository = AppointmentRepository(
        createLiveData = _createLiveData,
        updateLiveData = _updateLiveData
    )

    constructor()

    constructor(appointmentRepository: AppointmentRepository): this() {
        this.appointmentRepository = appointmentRepository
    }

    /**
     * Updates a category in the [appointmentRepository]
     * Help method to "connect" the view with the repository
     */
    fun updateCategory(category: Category) {
        appointmentRepository.update(category)
    }
    /**
     * Creates a category in the [appointementRepository]
     * Help method to "connect" the view with the repository
     */
    fun createCategory(category: Category) {
        appointmentRepository.create(category)
    }

    /**
     *  Help method that is called when there is a new value in the create/update LiveData and dismisses the Listener
     * Help method to "connect" the view with the repository
     */
    fun observeLiveData(newValue: RepositoryProgress, createAppointmentListener: ICreateAppointmentListener) {
        if(newValue == RepositoryProgress.SUCCESS) {
            createAppointmentListener.dismissDialog()
        }
    }
}