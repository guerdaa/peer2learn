package com.pse.peer2learn.models

/**
 * A model class that represents the members in a studygroup
 */
class Member (
    val id: String,
    val groupId: String,
    var isAdmin: Boolean,
    var memberName: String,
    val entryDate: Long
) {
    constructor(): this("", "",false, "", 0)
    constructor(id: String, groupId: String): this(id, groupId, false, "", 0)
}