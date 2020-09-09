package com.pse.peer2learn.ui.activities.interfaces

/**
 * This interface is added to seperate logic from the activity and avoid using views from inside a viewmodel
 * It is also helpful for unit testing
 */
interface IGroupSearchPublicListener {

    /**
     * method used to init ResultsGroupRecyclerViewAdapter.
     * This method is added to avoid calling views from inside the viewModels
     */
    fun initRecyclerViewAdapter()

    /**
     * Method used to set the [progress] text without calling the view from inside the viewModel
     */
    fun setProgressText(progress: String)

    /**
     * Method used to set the [language] text without calling the view from inside the viewModel
     */
    fun setLanguageText(language: String)

    /**
     * Method used to set the [minimumUsers] text without calling the view from inside the viewModel
     */
    fun setMinimumUsersText(minimumUsers: String)
}