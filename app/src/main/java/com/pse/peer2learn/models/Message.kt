package com.pse.peer2learn.models

/**
 * A model class that represents the messages that are sent in the chat of a study group
 */
class Message (
    var id: String,
    var message: String,
    val seenBy: HashMap<String, Boolean>,
    val senderID: String,
    val senderName: String,
    val order: Int,
    val date: Long = System.currentTimeMillis() / 1000
    ){

    constructor(): this("", "", hashMapOf(), "", "", 0,System.currentTimeMillis() / 1000)
}
