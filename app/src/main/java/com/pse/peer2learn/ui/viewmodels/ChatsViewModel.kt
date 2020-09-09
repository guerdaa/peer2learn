package com.pse.peer2learn.ui.viewmodels

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.repositories.MessageRepository
import com.pse.peer2learn.ui.activities.interfaces.IChatsListener
import com.pse.peer2learn.utils.Constants
import java.util.*
/**
 * Holds the logic of the ChatsFragment
 */
class ChatsViewModel: ViewModel {

    lateinit var register: ListenerRegistration
        private set
    private var messageRepository = MessageRepository()

    constructor()

    constructor(register: ListenerRegistration, messageRepository: MessageRepository): this() {
        this.register = register
        this.messageRepository = messageRepository
    }

    /**
     * Starts the listener for new messages
     */
    fun register(currentGroupId: String, chatsListener: IChatsListener) {
        register = messageRepository.register(currentGroupId, chatsListener)
    }

    /**
     * Removes the listener
     */
    fun unregister() {
        register.remove()
    }

    /**
     * Queries the messages from the repository
     * Help method to "connect" the view with the repository
     */
    fun setFirestoreOptions(currentGroupId: String): FirestoreRecyclerOptions<Message> {
        return messageRepository.setFirestoreOptions(currentGroupId)
    }

    /**
     * Sets the seen flag on a message
     * Help method to "connect" the view with the repository
     */
    fun setSeen(userId: String, currentGroupId: String, messageId: String) {
        messageRepository.setSeen(userId, currentGroupId, messageId)
    }
    /***
     * Sends a [messageContent], by creating a new [Message], to a group with [currentGroupId]
     */
    fun send(messageContent: String, currentGroupId: String, currentUser: Student, count: Int) {
        val message = Message("", messageContent, hashMapOf(), currentUser.id, currentUser.nickname, count)
        messageRepository.send(message, currentGroupId)
    }

    /**
     * Used to send a downloadable fire
     * Help method to "connect" the view with the repository
     */
    fun upload(data: Intent, chatsListener: IChatsListener, currentGroupId: String) {
        messageRepository.sendFile(data, chatsListener, currentGroupId)
    }


}