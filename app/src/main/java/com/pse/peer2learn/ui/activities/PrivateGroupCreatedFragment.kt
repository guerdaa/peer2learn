package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.utils.Constants
import kotlinx.android.synthetic.main.fragment_private_group_created.*

/**
 * This Fragment is used to show the user when a private group was successful created and shows the corresponing access code
 */
class PrivateGroupCreatedFragment : BottomSheetDialogFragment() {

    /**
     * Used for the creation of the Fragment without any dataloss.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_group_created, container, false)
    }

    /**
     * Assures that after a private group was successfully created,
     * success message with corresponding access code will be shown
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** Shows the access code of the newly created private group*/
        access_code_text_view.text = "${getString(R.string.access_code)}:  $accessCode"
        hide_button.setOnClickListener(dismissClickListener)
        isCancelable = false
    }

    /**
     * Ensures that on click of "Hide" Button the user returns to HomeActivity without data loss
     */
    private val dismissClickListener = View.OnClickListener {
        val homeIntent = Intent(activity, HomeActivity::class.java)
        homeIntent.putExtra(Constants.USER_EXTRA, currentStudent)
        startActivity(homeIntent)
    }


    companion object {

        var accessCode = ""
        lateinit var currentStudent : Student
        @JvmStatic
        fun newInstance(newAccessCode: String,currentStudent: Student): PrivateGroupCreatedFragment {
            this.accessCode = newAccessCode
            this.currentStudent = currentStudent
            return PrivateGroupCreatedFragment()
        }
    }
}