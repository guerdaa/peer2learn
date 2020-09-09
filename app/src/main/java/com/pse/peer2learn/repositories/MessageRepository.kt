package com.pse.peer2learn.repositories

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.ui.activities.interfaces.IChatsListener
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * The class is used to manage instances of the model class [Message] inside the Firebase database.
 */

class MessageRepository(

    private val retrievedLastMessages: MutableLiveData<HashMap<String, Message>> = MutableLiveData(),
    private val retrievedLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData()

): AbstractRepository<Message>() {

    /***
     * Creates a Message [instance] inside the [memberCollection].
     */
    override fun create(instance: Message) {
    }
    /***
     * Deletes a Message [instance] inside the [memberCollection].
     */
    override fun delete(instance: Message) {
    }
    /***
     * Updates a Message [instance] inside the [memberCollection].
     */
    override fun update(instance: Message) {
    }

    /**
     * Retrieves the last messages that are sent in each group of the current user.
     * Using the passed parameter [listGroups], which constains the id's of the groups the user participates in.
     */
    fun retrieveLastMessages(listGroups: ArrayList<String>) {
        for (i in 0 until listGroups.size) {
            firebaseInstance.collection(Constants.GROUPS_COLLECTION)
                .document(listGroups[i])
                .collection(Constants.MESSAGES_COLLECTION)
                .orderBy("order", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { doc ->
                    if(doc.documents.isNotEmpty())
                        doc.documents[0].toObject(Message::class.java)?.let {message ->
                               retrievedLastMessages.value!!.put(listGroups[i], message)
                        }
                    if(i == listGroups.size - 1) {
                        retrievedLiveData.value = RepositoryProgress.SUCCESS
                    }
                }.addOnFailureListener {

                }
        }
    }

    /**
     * Sets a seen flag to the message with [messageId], inside the [currentGroupId], from the user with [userId]
     */
    fun setSeen(userId: String, currentGroupId: String, messageId: String) {
        firebaseInstance.collection(Constants.GROUPS_COLLECTION)
            .document(currentGroupId).collection(Constants.MESSAGES_COLLECTION)
            .document(messageId)
            .update("${Constants.SEEN_BY_ATTRIBUT}.${userId}", true)
    }

    /**
     * Used to send a message.
     * Creates an empty document in the [currentGroupId] messages collection and then sets the value of the document in the database
     */
    fun send(message: Message, currentGroupId: String) {
        val doc = firebaseInstance.collection(Constants.GROUPS_COLLECTION).document(
            currentGroupId
        ).collection(Constants.MESSAGES_COLLECTION).document()
        message.id = doc.id
        doc.set(message)
    }

    /**
     * Used to send a downloadable file
     */
    fun sendFile(data: Intent, chatsListener: IChatsListener, currentGroupId: String) {
        val ref = FirebaseStorage.getInstance().getReference("${Constants.CHAT_STORAGE_PATH}${currentGroupId}/${UUID.randomUUID()}.jpg")
        val uploadTask = ref.putFile(data.data!!)
        uploadTask.continueWithTask { task ->
            ref.downloadUrl
        }.addOnSuccessListener { task ->
            chatsListener.send(task.toString())
        }
    }

    /**
     * Used to query the messages from the database
     */
    fun setFirestoreOptions(currentGroupId: String): FirestoreRecyclerOptions<Message> {
        return FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(
                FirebaseFirestore.getInstance().collection(Constants.GROUPS_COLLECTION)
                    .document(currentGroupId)
                    .collection(Constants.MESSAGES_COLLECTION).orderBy(Constants.ORDER_ATTRIBUT),
                Message::class.java
            ).build()
    }

    /**
     * Used to detect the receiving or the sent of a new message, so that the adapter will scroll to the bottom and show the new message
     */
    fun register(currentGroupId: String, chatsListener: IChatsListener): ListenerRegistration {
        return FirebaseFirestore.getInstance().collection(Constants.GROUPS_COLLECTION)
            .document(currentGroupId)
            .collection(Constants.MESSAGES_COLLECTION)
            .addSnapshotListener { querySnapshot, _ ->
                if(querySnapshot!!.documentChanges.size > 1)
                    return@addSnapshotListener
                for (dc in querySnapshot.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        chatsListener.scrollToBottom()
                    }
                }
            }
    }
}