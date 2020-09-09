package com.pse.peer2learn.ui.activities.interfaces

/**
 * This interface is added to seperate logic from the activity and avoid using views from inside a viewmodel
 * It is also helpful for unit testing
 */
interface ICreateGroupListener {

    /**
     * Method used to show the PrivateGroupCreatedFragment
     */
    fun showPrivateGroupCreatedFragment()

    /**
     * method used to set the generated access code in the textview
     */
    fun setAccessCode()

    /**
     * method used to navigate back home after successfully creating a group
     */
    fun navigateHome()
}