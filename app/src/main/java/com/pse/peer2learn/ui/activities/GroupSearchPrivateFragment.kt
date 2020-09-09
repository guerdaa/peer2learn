package com.pse.peer2learn.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.ui.activities.interfaces.IGroupSearchPrivateListener
import com.pse.peer2learn.ui.viewmodels.GroupSearchPrivateViewModel
import com.pse.peer2learn.utils.RepositoryProgress
import kotlinx.android.synthetic.main.fragment_group_search_private.*
import kotlinx.android.synthetic.main.fragment_group_search_private.view.*

/**
 * This Fragment is used for search of a private groups, showed inside the GroupSearchPublicActivity
 */
class GroupSearchPrivateFragment : BottomSheetDialogFragment(), IGroupSearchPrivateListener {

    lateinit var groupSearchPrivateViewModel: GroupSearchPrivateViewModel

    /**
     * Used for the creation of the Fragment without any dataloss.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_search_private, container, false)
    }

    /**
     * Intialiases the corresponding ViewModel
     * Observes the requiered LiveData
     * Sets up the click listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.submit_button.setOnClickListener(searchGroupClickListener)
        groupSearchPrivateViewModel = ViewModelProvider(this).get(GroupSearchPrivateViewModel::class.java)
        groupSearchPrivateViewModel.retrievedGroup.observe(viewLifecycleOwner, Observer { newValue ->
            groupSearchPrivateViewModel.observeRetrieveGroup(newValue, this, requireContext())
        })
        //Observing if there is a successfull update in the user data (Updating the groups list)
        groupSearchPrivateViewModel.updateUserLiveData.observe(viewLifecycleOwner, Observer {newValue ->
            groupSearchPrivateViewModel.observeUpdateUser(newValue, requireContext())
        })
        groupSearchPrivateViewModel.groupAlreadyJoined.observe(viewLifecycleOwner, Observer { newValue ->
            groupSearchPrivateViewModel.observeGroupAlreadyJoined(newValue, requireContext())
        })
    }

    private val searchGroupClickListener = View.OnClickListener {
        searchGroup()
    }

    /**
     * Searches for private group using the text that the user has put in search_private_edit_text
     */
    private fun searchGroup() {
        groupSearchPrivateViewModel.findPrivateGroup(search_private_edit_text.text.toString().trim())
    }

    /**
     * Calls the ViewModel method to join the group with the retrieved from searchGroup id
     * This method belongs to IGroupSearchPrivateListener, and called from inside GroupSearchPrivateViewModel to join a group
     */
    override fun joinGroup() {
        groupSearchPrivateViewModel.joinGroup(
            student,
            groupSearchPrivateViewModel.retrievedGroup.value!!.id
        )
    }

    companion object {
        private lateinit var student: Student
        fun newInstance(instance: Student): GroupSearchPrivateFragment {
            student = instance
            return GroupSearchPrivateFragment()
        }
    }
}
