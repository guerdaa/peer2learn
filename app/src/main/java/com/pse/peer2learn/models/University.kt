package com.pse.peer2learn.models

import java.io.Serializable

/**
 * A model class that represents the universities
 */
class University (
    val name: String,
    val shortName: String,
    val country: String
): Serializable {
    constructor(): this("", "", "")

    /**
     * Get short name of University
     * Used by ArrayAdapter to fill AutoCompleteTextView in profile and registration activities
     */
    override fun toString(): String {
        return  shortName
    }
}