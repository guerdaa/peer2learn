package com.pse.peer2learn.ui.activities

import android.content.Intent
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
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.ui.activities.interfaces.IHomeMenuListener
import com.pse.peer2learn.ui.viewmodels.HomeMenuViewModel
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_home_menu.*
import kotlinx.android.synthetic.main.fragment_home_menu.view.*
import kotlinx.android.synthetic.main.fragment_home_menu.view.quit_group_text_view

/**
 * This fragment is used to show the different options when an user clicks on the option menu of an already joined group
 */
class HomeMenuFragment : BottomSheetDialogFragment(), IHomeMenuListener {

    lateinit var homeMenuViewModel: HomeMenuViewModel
    /**
     * Used for the creation of the Fragment without any dataloss.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_menu, container, false)
        view.open_group_text_view.setOnClickListener(openClickListener)
        view.quit_group_text_view.setOnClickListener(quitClickListener)
        return view
    }

    /**
     * Method called when the view is created. It also initialize the view model for the Fragment and observe the changes of liveData
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeMenuViewModel = ViewModelProvider(this).get(HomeMenuViewModel::class.java)
        homeMenuViewModel.deleteMemberLiveData.observe(viewLifecycleOwner, Observer { newValue ->
            homeMenuViewModel.observeDeleteMember(newValue, this, requireContext())
        })
        homeMenuViewModel.updatedUserLiveData.observe(viewLifecycleOwner, Observer { newValue ->
            homeMenuViewModel.observeUpdateUser(newValue, currentGroup.id, this, requireContext())
        })
        /** Assures that the current group will always have admins or be deleted if members = 0*/
        homeMenuViewModel.participantsCountLiveData.observe(viewLifecycleOwner, Observer { count ->
            homeMenuViewModel.observeParticipantsCount(count, currentGroup)
        })
        homeMenuViewModel.deletedLiveData.observe(viewLifecycleOwner, Observer { newValue ->
            homeMenuViewModel.observeDeleteGroup(newValue, this, requireContext())
        })
        homeMenuViewModel.updateAdminOfGroupLiveData.observe(viewLifecycleOwner, Observer { newValue ->
            homeMenuViewModel.observeUpdateAdminOfGroup(newValue, this, requireContext())
        })
    }

    /**
     * Opens the chosen, by the user, group
     */
    private val openClickListener = View.OnClickListener {
        isCancelable = false
        val groupIntent = Intent(requireActivity(), GroupActivity::class.java)
        groupIntent.putExtra(Constants.GROUP_EXTRA, GroupsRecyclerViewAdapter.selectedGroup)
        groupIntent.putExtra(Constants.USER_EXTRA, currentUser)
        dismiss()
        startActivity(groupIntent)
        requireActivity().finish()
    }

    /**
     * Quits the chosen, by the user, group
     */
    private val quitClickListener = View.OnClickListener {
        isCancelable = false
        homeMenuViewModel.updateListOfMembers(currentUser.id, currentGroup.id)
        disableButtons()
    }

    /**
     * Disables the buttons inside the fragment, after quiting a group so that the user isn't able to click it twice.
     */
    private fun disableButtons() {
        quit_group_text_view.isEnabled = false
        open_group_text_view.isEnabled = false
    }

    /**
     * Shows appropriate [message] on deletion of a group and dismiss the fragment when the action is finished
     */
    override fun dismissDialog(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        dismiss()
    }

    /**
     * Updates list of groups in HomeActivity on quitting a group
     */
    override fun updateListOfGroup() {
        (requireActivity() as HomeActivity).homeViewModel.retrievedUserGroupsLiveData.value?.remove(currentGroup)
        (requireActivity() as HomeActivity).adapter.notifyDataSetChanged()
        homeMenuViewModel.updateListOfGroup(currentUser, currentGroup.id)
    }

    companion object {
        const val TAG = "HomeMenuFragment"

        private var currentUser: Student = Student()
        private var currentGroup: StudyGroup = StudyGroup()

        fun newInstance(userExtra: Student, groupExtra: StudyGroup): HomeMenuFragment {
            currentGroup = groupExtra
            currentUser =  userExtra
            return HomeMenuFragment()
        }
    }
}