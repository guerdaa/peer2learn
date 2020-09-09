package com.pse.peer2learn.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.utils.Utils
import kotlinx.android.synthetic.main.group_item_layout.view.*


abstract class GroupsRecyclerViewAdapter(private val groupList: ArrayList<StudyGroup>, private val listMessage: HashMap<String, Message>, private val userId: String): RecyclerView.Adapter<GroupsRecyclerViewAdapter.GroupsViewHolder>(){


    class GroupsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        /**
         * method used to set the [group] detail
         */
        fun setGroupDetail(group: StudyGroup) {
            itemView.group_title_text_view.text = group.title
        }

        /**
         * method used to set the [lastMessage] below the group detail
         */
        fun setLastMessage(lastMessage: Message) {
            itemView.date_text_view.text = Utils.convertDate(lastMessage.date*1000)
            itemView.last_message_text_view.text =
                "${lastMessage.senderName}: ${if (lastMessage.message.length > 30) lastMessage.message.substring(0, 30) else lastMessage.message}"
        }

        /**
         * method used to set a indicator whether the last received message was read by the user
         */
        fun setLastMessageRead(message: Message, userId: String) {
            if(message.seenBy[userId] != null && message.seenBy[userId]!! || message.senderID == userId) {
                itemView.unread_image_view.visibility = View.GONE
            } else {
                itemView.unread_image_view.visibility = View.VISIBLE
            }
        }

    }
    /**
     * Method used to inflate the corresponding layout item for group
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group_item_layout, parent, false)
        return GroupsViewHolder(itemView)
    }

    /**
     * @return the number of groups joined by a user
     */
    override fun getItemCount() = groupList.size

    /**
     * Method used to set the details of each displayed group in a given [position]
     */
    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        holder.setGroupDetail(groupList[position])
        holder.itemView.setOnClickListener {
            selectedGroup = groupList[position]
            openGroup()
        }
        holder.itemView.menu_image_view.setOnClickListener {
            selectedGroup = groupList[position]
            openMenu()
        }
        listMessage[groupList[position].id]?.let {
            holder.setLastMessage(it)
            holder.setLastMessageRead(it, userId)
        }
    }

    /**
     * method used to open a group when user clicks on the item
     */
    abstract fun openGroup()

    /**
     * method used to open a menu when the user clicks on the menu item
     */
    abstract fun openMenu()

    /**
     * method to ascending sort groups by name
     */
    fun sortNameAsc(){
        groupList.isNotEmpty().let {
            groupList.sortBy { it -> it.title }
        }
    }

    /**
     * method to descending sort groups by name
     */
    fun sortNameDesc(){
        groupList.isNotEmpty().let {
            groupList.sortByDescending { it -> it.title }
        }
    }

    companion object {
        var selectedGroup: StudyGroup = StudyGroup()
    }
}