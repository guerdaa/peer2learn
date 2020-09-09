package com.pse.peer2learn.ui.activities.interfaces
/**
 * This interface is added to seperate logic from the activity and avoid using views from inside a viewmodel
 * It is also helpful for unit testing
 */
interface IProfileListener {

    /**
     * method used to set [deleteCounter]. This counter is used for deleting account to verify the number of updated groups joined by a user
     * when he is leaving a group
     */
    fun setDeleteCounter(counter : Int)

    /**
     * @return [deleteCounter]
     */
    fun getDeleteCounter(): Int
}