package com.pse.peer2learn.ui.activities.interfaces
/**
 * This interface is added to seperate logic from the activity and avoid using views from inside a viewmodel
 * It is also helpful for unit testing
 */
interface IHomeMenuListener {

    /**
     * method used to dismiss a dialog and printing [message] as a toast
     */
    fun dismissDialog(message: String)

    /**
     * method used to update the list of groups of the currentUser
     */
    fun updateListOfGroup()
}