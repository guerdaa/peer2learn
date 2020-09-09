package com.pse.peer2learn.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pse.peer2learn.R
import com.pse.peer2learn.models.StudyGroup
import kotlinx.android.synthetic.main.group_result_layout.view.*

abstract class ResultsGroupsRecyclerViewAdapter(val groupList: ArrayList<StudyGroup>): RecyclerView.Adapter<ResultsGroupsRecyclerViewAdapter.GroupsViewHolder>() {

    var clickedItemPosition = 0

    /**
     * Method used to inflate the corresponding layout item of a result group
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_result_layout, parent, false)
        return GroupsViewHolder(parent.context, view)
    }

    /**
     * @return the number of result groups
     */
    override fun getItemCount(): Int {
        return groupList.size
    }

    /**
     * Method used to set the details of each displayed result group in a given [position]
     */
    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        holder.setGroupDetail(groupList[position])
        holder.itemView.setOnClickListener {
            clickedItemPosition = position
            joinGroup()
        }
    }

    /**
     * Method used to join a group
     */
    abstract fun joinGroup()

    class GroupsViewHolder(val context: Context, itemView: View): RecyclerView.ViewHolder(itemView) {
        /**
         * method used to set the [group] detail
         */
        fun setGroupDetail(group: StudyGroup) {
            itemView.group_title_text_view.text = group.title
            itemView.group_description_text_view.text =
                if(group.description.isNotEmpty())
                    group.description
                else
                    context.getString(R.string.description_not_available)
            itemView.number_of_users_text_view.text = group.numberOfMembers.toString()
            itemView.language_text_view.text = group.language.shortName.toUpperCase()
            itemView.progress_text_view.text = "${group.progress}%"
        }
    }
}