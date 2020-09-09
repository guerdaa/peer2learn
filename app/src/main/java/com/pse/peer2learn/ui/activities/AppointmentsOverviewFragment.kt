package com.pse.peer2learn.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pse.peer2learn.R
import com.pse.peer2learn.ui.activities.interfaces.IAppointementsOverviewListener
import com.pse.peer2learn.ui.activities.interfaces.IDialogDismissListener
import com.pse.peer2learn.ui.viewmodels.AppointementsOverviewViewModel
import com.pse.peer2learn.utils.adapters.CategoriesRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_appointements_overview.*

/**
 * This class is used to show the Appointments and categories inside a group
 */
class AppointmentsOverviewFragment : Fragment(), IAppointementsOverviewListener {

    lateinit var appointementsOverviewViewModel: AppointementsOverviewViewModel
    private val dismissListener = object: IDialogDismissListener {
        override fun dismissListener() {
            displayCategories()
        }
    }
    /**
     * This is a help method, that is called when there are any changes made to the categories list inside the group with [currentGroupId]
     * Retrieves the changed liveData and displays it
     */
    private fun displayCategories() {
        appointementsOverviewViewModel.retrievedCategoriesLiveData.value?.clear()
        appointementsOverviewViewModel.retrieveCategories(currentGroupId)
    }

    /**
     * Used for the creation of the Fragment without any dataloss.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointements_overview, container, false)
    }

    /**
     * Help method that is used to set up the UI correspoting to that, whether the user is an admin or not
     */
    private fun adjustUI() {
        if((requireActivity() as GroupActivity).isUserAdmin)
            add_category_button.visibility =  View.VISIBLE
        else
            add_category_button.visibility = View.GONE
    }

    /**
     * Adjusts the ui on the start of the Fragment
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustUI()
    }

    /**
     * This method is used to update the layout when another fragment is chosen.
     * Sets up the ui and the listenters
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        appointementsOverviewViewModel = ViewModelProvider(this).get(AppointementsOverviewViewModel::class.java)
        appointementsOverviewViewModel.retrieveCategories(currentGroupId)
        appointementsOverviewViewModel.retrieveLiveData.observe(viewLifecycleOwner, Observer {newValue ->
            appointementsOverviewViewModel.observeRetrieve(newValue,this)
        })
        add_category_button.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().
            add(CreateAppointmentFragment.newInstance(null, currentGroupId, dismissListener), CreateAppointmentFragment.TAG).commitNowAllowingStateLoss()
        }
    }

    /**
     * Help method to initialise the Adapter of the categories recycler view
     * Defines delete and edit actions on a category
     * This method belong to IAppointementsOverviewListener and called from inside the viewModel
     */
    override fun initCategoriesRecyclerViewAdapter() {
        categories_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        categories_recycler_view.adapter = object : CategoriesRecyclerViewAdapter(
            appointementsOverviewViewModel.retrievedCategoriesLiveData.value!!, requireContext(), (requireActivity() as GroupActivity).isUserAdmin) {
            override fun delete() {
                appointementsOverviewViewModel.deleteCategory(clickedCategory!!)
                displayCategories()
            }
            override fun edit() {
                requireActivity().supportFragmentManager.beginTransaction().
                add(CreateAppointmentFragment.newInstance(clickedCategory, currentGroupId, dismissListener), CreateAppointmentFragment.TAG).commitNowAllowingStateLoss()
            }
        }
    }

    companion object {
        private lateinit var currentGroupId: String

        @JvmStatic
        fun newInstance(groupId: String) =
            AppointmentsOverviewFragment().apply {
                currentGroupId = groupId
            }
    }
}