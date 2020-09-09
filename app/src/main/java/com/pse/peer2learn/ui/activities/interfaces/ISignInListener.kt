package com.pse.peer2learn.ui.activities.interfaces

import com.pse.peer2learn.models.Student

/**
 * This interface is added to seperate logic from the activity and avoid using views from inside a viewmodel
 * It is also helpful for unit testing
 */
interface ISignInListener {

    /**
     * start home activity and pass [student] as extra
     */
    fun startHomeActivity(student: Student)

    /**
     * start registration activity
     */
    fun startRegistrationActivity()

}