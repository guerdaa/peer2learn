package com.pse.peer2learn.models

/**
 * A model class that represents Categories with appointments in a StudyGroup
 */
class Category (
    var id: String,
    var name: String,
    val groupId: String,
    val appointments: ArrayList<Appointment>
){
    constructor(): this("", "", "", arrayListOf())
}