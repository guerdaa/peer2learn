package com.pse.peer2learn.utils.adapters

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.Utils
import kotlinx.android.synthetic.main.message_item_layout.view.*

abstract class ChatRecyclerViewAdapter(
    private val userId: String
    , firestoreOptions: FirestoreRecyclerOptions<Message>, private val context: Context
) :
    FirestoreRecyclerAdapter<Message, ChatRecyclerViewAdapter.ChatViewHolder>(firestoreOptions) {

    class ChatViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        /**
         * set the details of the received [message] and adjust the UI
         */
        fun setReceivedMessage(message: Message) {
            itemView.send_message_layout.visibility = View.GONE
            itemView.send_message_file_layout.visibility = View.GONE
            itemView.receive_file_message_layout.visibility = View.GONE
            itemView.receive_message_layout.visibility = View.VISIBLE
            itemView.receiver_name_text_view.text = message.senderName
            itemView.received_message_text_view.text = message.message
            itemView.received_date_text_view.text = Utils.convertDate(message.date * 1000)
        }

        /**
         * set the details of the sent [message] and adjust the UI
         */
        fun setSentMessage(message: Message) {
            itemView.send_message_layout.visibility = View.VISIBLE
            itemView.send_message_file_layout.visibility = View.GONE
            itemView.receive_file_message_layout.visibility = View.GONE
            itemView.receive_message_layout.visibility = View.GONE
            itemView.sender_name_text_view.text = message.senderName
            itemView.sent_message_text_view.text = message.message
            itemView.sent_date_text_view.text = Utils.convertDate(message.date * 1000)
        }

        /**
         * set the details of the sent File[message] and adjust the UI
         */
        fun setSentFileMessage(message: Message) {
            itemView.send_message_layout.visibility = View.GONE
            itemView.send_message_file_layout.visibility = View.VISIBLE
            itemView.receive_file_message_layout.visibility = View.GONE
            itemView.receive_message_layout.visibility = View.GONE
            itemView.sender_file_name_text_view.text = message.senderName
            itemView.sent_file_date_text_view.text = Utils.convertDate(message.date * 1000)
            Glide.with(context).load(message.message).fitCenter()
                .into(itemView.sent_file_message_image_view)
        }

        /**
         * set the details of the received File[message] and adjust the UI
         */
        fun setReceivedFileMessage(message: Message) {
            itemView.send_message_layout.visibility = View.GONE
            itemView.send_message_file_layout.visibility = View.GONE
            itemView.receive_file_message_layout.visibility = View.VISIBLE
            itemView.receive_message_layout.visibility = View.GONE
            itemView.receiver_name_file_text_view.text = message.senderName
            itemView.received_file_date_text_view.text = Utils.convertDate(message.date * 1000)
            Glide.with(context).load(message.message).fitCenter()
                .into(itemView.received_message_file_image_view)
            itemView.setOnClickListener {
                startDownload(message.message);
            }
        }

        /**
         * start downloading the received File[message]
         */
        private fun startDownload(message: String) {
            val request = DownloadManager.Request(Uri.parse(message))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle(R.string.download.toString())
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${System.currentTimeMillis()}"
            )
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }

    }

    /**
     * Method used to inflate the corresponding layout item for message
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item_layout, parent, false)
        return ChatViewHolder(view, context)
    }
    /**
     * Method used to set the details of each displayed [message] in a given [position]
     */
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, message: Message) {
        if (message.senderID == userId && !message.message.startsWith(Constants.STORAGE_URL)) {
            holder.setSentMessage(message)
        } else if (message.senderID != userId && !message.message.startsWith(Constants.STORAGE_URL)) {
            holder.setReceivedMessage(message)
            setSeen(message)
        } else if (message.senderID == userId && message.message.startsWith(Constants.STORAGE_URL)) {
            holder.setSentFileMessage(message)
        } else if (message.senderID != userId && message.message.startsWith(Constants.STORAGE_URL)) {
            holder.setReceivedFileMessage(message)
            setSeen(message)
        }
    }

    /**
     * method called every time a user sees the received [message]
     */
    abstract fun setSeen(message: Message)
}