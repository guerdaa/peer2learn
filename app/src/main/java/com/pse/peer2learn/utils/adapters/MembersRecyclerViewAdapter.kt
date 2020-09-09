package com.pse.peer2learn.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Member
import com.pse.peer2learn.models.Message
import kotlinx.android.synthetic.main.member_item_layout.view.*

abstract class MembersRecyclerViewAdapter(firestoreOptions: FirestoreRecyclerOptions<Member>, private val context: Context): FirestoreRecyclerAdapter<Member, MembersRecyclerViewAdapter.MembersViewHolder>(firestoreOptions) {

    var clickedMember = Member()

    class MembersViewHolder(itemView: View, private val context: Context): RecyclerView.ViewHolder(itemView) {
        /**
         * method used to set the [member] detail
         */
        fun setMemberDetails(member: Member) {
            itemView.mtrl_list_item_text.text = member.memberName
            if(member.isAdmin)
                itemView.mtrl_list_item_secondary_text.text = context.getString(R.string.admin)
            else
                itemView.mtrl_list_item_secondary_text.text = context.getString(R.string.member)
        }
    }

    /**
     * Method used to inflate the corresponding layout item of a member
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member_item_layout, parent, false)
        return MembersViewHolder(view, context)
    }

    /**
     * Method used to set the details of each displayed member in a given [position]
     */
    override fun onBindViewHolder(holder: MembersViewHolder, position: Int, member: Member) {
        holder.setMemberDetails(member)
        holder.itemView.mtrl_list_item_text.setOnLongClickListener {
            clickedMember = member
            return@setOnLongClickListener giveAdminRight()
        }
    }

    /**
     * method used to change the admin rights of the clicked item
     */
    abstract fun giveAdminRight(): Boolean
}
