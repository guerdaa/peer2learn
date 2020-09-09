package com.pse.peer2learn.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Language
import com.pse.peer2learn.models.Member
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.ui.viewmodels.GroupOverviewViewModel
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress
import com.pse.peer2learn.utils.adapters.MembersRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_group_overview.*
import kotlinx.android.synthetic.main.fragment_group_overview.view.*
import kotlinx.android.synthetic.main.fragment_group_overview.view.access_code_text_view
import kotlinx.android.synthetic.main.fragment_group_overview.view.description_text_view

/**
 * This fragment is used to show the "about" information of a study group
 */
class GroupOverviewFragment: Fragment(), SeekBar.OnSeekBarChangeListener {
    private lateinit var membersRecyclerViewAdapter: MembersRecyclerViewAdapter
    lateinit var groupOverviewViewModel: GroupOverviewViewModel


    /**
     * Used for the creation of the Fragment without any dataloss.
     * Observes the updateGroupLiveData to create a suitable taost
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        groupOverviewViewModel = ViewModelProvider(this).get(GroupOverviewViewModel::class.java)
        groupOverviewViewModel.updateGroupLiveData.observe(viewLifecycleOwner, Observer {newValue ->
            if (newValue == RepositoryProgress.SUCCESS) {
                Toast.makeText(context, "Changes have been saved!", Toast.LENGTH_SHORT).show()
            } else if (newValue == RepositoryProgress.FAILED) {
                Toast.makeText(context, "Saving failed!", Toast.LENGTH_SHORT).show()
            }
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_overview, container, false)
    }

    /**
     * Help method that is used to set up the UI  correspoting to that, whether the user is an admin or not
     */
    private fun updateViews() {
        (requireActivity() as GroupActivity).isUserAdmin.let {
            progress_seek_bar.isEnabled = it
            description_text_view.isEnabled = it
            language_text_view.isEnabled = it
            update_admin_rights.visibility = if(it) View.VISIBLE else View.GONE
        }
    }
    /**
     * Sets the correct values of the ui attributes that are corresponding to the [currentGroup],that the user has chosen
     * Overrides the method that is called when an admin holds on another member to make him admin
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.progress_text_view.text = currentGroup.progress.toString()
        view.progress_seek_bar.progress = currentGroup.progress
        view.progress_seek_bar.setOnSeekBarChangeListener(this)
        view.description_text_view.setText(currentGroup.description)
        view.access_code_text_view.text = currentGroup.accessCode
        view.language_text_view.setText(currentGroup.language.name)
        view.submit_button.setOnClickListener(submitClickListener)

        val firestoreOptions = FirestoreRecyclerOptions.Builder<Member>()
            .setQuery(
                FirebaseFirestore.getInstance().collection(Constants.GROUPS_COLLECTION)
                .document(currentGroup.id)
                .collection(Constants.MEMBER_COLLECTION), Member::class.java).build()

        membersRecyclerViewAdapter = object : MembersRecyclerViewAdapter(firestoreOptions, requireContext()) {
            override fun giveAdminRight(): Boolean {
                return if((requireActivity() as GroupActivity).isUserAdmin) {
                    groupOverviewViewModel.giveAdminRight(clickedMember)
                    true
                } else
                    false
            }

        }

        val linearLayoutManager = LinearLayoutManager(context)
        view.members_recycler_view.adapter = membersRecyclerViewAdapter
        view.members_recycler_view.layoutManager = linearLayoutManager
        updateViews()
    }

    private val submitClickListener = View.OnClickListener {
        updateGroup()
    }

    /**
     * Help method that collects the user input of the screen and updates the Repository using the [groupOverviewViewModel]
     */
    private fun updateGroup() {
        val description = description_text_view.text.trim().toString()
        val language = language_text_view.text.toString().toUpperCase()
        val progress = progress_seek_bar.progress.toInt()
        val studyGroup = StudyGroup(
            id = currentGroup.id,
            title = currentGroup.title,
            description = description,
            accessCode = currentGroup.accessCode,
            university = currentGroup.university,
            language = Language(language),
            maxMembers = currentGroup.maxMembers,
            numberOfMembers = currentGroup.numberOfMembers,
            progress = progress
        )
        groupOverviewViewModel.updateStudyGroup(studyGroup)
    }

    /**
     * Starts the Listener for the MemberRecyclerViewAdapter, to detect changes
     * */
    override fun onStart() {
        super.onStart()
        membersRecyclerViewAdapter.startListening()
    }
    /**
     * Stops the Listener for the MemberRecyclerViewAdapter
     **/
    override fun onStop() {
        super.onStop()
        membersRecyclerViewAdapter.stopListening()
    }

    companion object {
        private lateinit var currentGroup: StudyGroup
        private lateinit var currentStudent: Student

        @JvmStatic
        fun newInstance(student: Student, group: StudyGroup): GroupOverviewFragment {
            currentGroup = group
            currentStudent = student
            return GroupOverviewFragment()
        }
    }

    /**
     * Updates the [progress_text_view] in the view to the corresponding number [p1] on the seek bar
     */
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        progress_text_view.text = p1.toString()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        return
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        return
    }
}