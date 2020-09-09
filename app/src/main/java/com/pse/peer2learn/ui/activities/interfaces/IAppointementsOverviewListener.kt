package com.pse.peer2learn.ui.activities.interfaces


/**
 * This interface is added to seperate logic from the activity and avoid using views from inside a viewmodel
 * It is also helpful for unit testing
 */
interface IAppointementsOverviewListener {

    /**
     * method used to init CategoriesRecyclerViewAdapter.
     * This method is added to avoid calling views from inside the viewModels
     */
    fun initCategoriesRecyclerViewAdapter()
}