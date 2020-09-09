package com.pse.peer2learn.models

/**
 * A Model class that represents Appointments in a Study Group
 */
class Appointment(
    val title: String,
    val date: Long
) {
    constructor(): this("", 0)
}