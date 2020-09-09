package com.pse.peer2learn.ui.activities.interfaces

/**
 * This interface is added to seperate logic from the activity and avoid using views from inside a viewmodel
 * It is also helpful for unit testing
 */
interface IChatsListener {
    /**
     * This method is used to scroll to the bottom whenever a new message is added
     * It is added to avoid calling views from inside the viewModels
     */
    fun scrollToBottom()

    /**
     * This method is used to send a message
     * It is added to avoid calling views from inside the viewModels
     */
    fun send(messageContent: String)
}