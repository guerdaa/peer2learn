package com.pse.peer2learn.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.ui.activities.interfaces.IChatsListener
import com.pse.peer2learn.ui.viewmodels.ChatsViewModel
import com.pse.peer2learn.utils.adapters.ChatRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.fragment_chats.view.*

/**
 * This class is used to show the chat and chat message inside a group
 */
class ChatsFragment : Fragment(), IChatsListener {

    lateinit var chatsViewModel: ChatsViewModel
    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter

    /**
     * This method is used to set up the scene for the Chat.
     * Sets Listeners and inizialises recycler view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsViewModel = ViewModelProvider(this).get(ChatsViewModel::class.java)

        //Setting the adapter and layout for the RecyclerView
        initChatRecyclerView()
        //Sets click listeners
        view.send_button.setOnClickListener {
            send(send_edit_text.text.toString())
        }
        view.gallery_button.setOnClickListener {
            pickImage()
        }
        view.send_edit_text.addTextChangedListener(sendTextWatcher)
    }

    /**
     * A help method that is used to set all the values of the RecyclerView
     */
    private fun initChatRecyclerView() {
        chatRecyclerViewAdapter =
            object : ChatRecyclerViewAdapter(currentUser.id, chatsViewModel.setFirestoreOptions(
                currentGroupId), requireContext()) {
                override fun setSeen(message: Message) {
                    chatsViewModel.setSeen(currentUser.id, currentGroupId, message.id)
                }
            }
        messages_recyclerview.adapter = chatRecyclerViewAdapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        messages_recyclerview.layoutManager = linearLayoutManager
    }

    /**
     * Used for the creation of the Fragment without any dataloss.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    /**
     * This is a help method that allows the user to select an image file to sent
     */
    private fun pickImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = "image/*"
        val chooserIntent = Intent.createChooser(getIntent, requireContext().getString(R.string.select_image))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, PICK_IMAGE_RESULT)
    }

    /**
     * This method is launched when the user comes back from the Gallery after selecting an image
     * On selection of an image from the gallery, the image is send a message to the other users
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {intent->
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                send_edit_text.isEnabled = false
                chatsViewModel.upload(intent, this, currentGroupId)
            }
        }
    }

    /***
     * Sends a [messageContent], by creating a new [Message], to a group with [currentGroupId]
     */
    override fun send(messageContent: String){
        chatsViewModel.send(messageContent, currentGroupId, currentUser, chatRecyclerViewAdapter.itemCount)
        send_edit_text.text.clear()
        send_edit_text.isEnabled = true
    }

    /**
     * remove the listener from chatRecyclerViewAdapter
     */
    override fun onPause() {
        super.onPause()
        chatRecyclerViewAdapter.stopListening()
        chatsViewModel.unregister()
    }

    /**
     *  In this method, the chatRecyclerViewAdapter starts listening to the changes that occurs inside the MessageRepositoy
     *  and displays the new added message
     */
    override fun onResume() {
        super.onResume()
        chatsViewModel.register(currentGroupId, this)
        chatRecyclerViewAdapter.startListening()
    }

    /**
     * Used to change the button for image or send, depending on that if there text in the field or not
     */
    private val sendTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (s.isNotEmpty()) {
                send_button.visibility = View.VISIBLE
                gallery_button.visibility = View.GONE
            } else {
                send_button.visibility = View.GONE
                gallery_button.visibility = View.VISIBLE
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }

    companion object {

        const val PICK_IMAGE_RESULT = 1
        private lateinit var currentUser: Student
        private lateinit var currentGroupId: String

        @JvmStatic
        fun newInstance(student: Student, groupId: String): ChatsFragment {
            currentUser = student
            currentGroupId = groupId
            return ChatsFragment()
        }

    }

    override fun scrollToBottom() {
        messages_recyclerview.smoothScrollToPosition(chatRecyclerViewAdapter.itemCount)
    }
}