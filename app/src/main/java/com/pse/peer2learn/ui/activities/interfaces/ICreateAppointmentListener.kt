package com.pse.peer2learn.ui.activities.interfaces


/**
 * This interface is added to seperate logic from the activity and avoid using views from inside a viewmodel
 * It is also helpful for unit testing
 */
interface ICreateAppointmentListener {

    /**
     * This method is used to dismiss the CreateAppointmentFragment
     */
    fun dismissDialog()
}