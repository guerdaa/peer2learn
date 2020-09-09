package com.pse.peer2learn.models

import java.io.Serializable

/**
 * A model class that represents a Study group
 */
class StudyGroup (
    var id: String,
    val title: String,
    val description: String,
    var progress: Int = 0,
    val maxMembers: Int = 0,
    var accessCode: String = NON_PRIVATE_GROUP,
    val university: University,
    var language: Language,
    var numberOfMembers: Int = 0
    ) : Serializable {

    constructor(): this(
        "",
        "",
        "",
        0,
        0,
        "",
        University(),
        Language(),
        0
        )

    constructor(
        title: String,
        description: String,
        maxNumber: Int,
        accessCode: String,
        university: University,
        language: Language
    ): this (
        "",
        title,
        description,
        0,
        maxNumber,
        accessCode,
        university,
        language,
    1)

    companion object {
        const val NON_PRIVATE_GROUP = "IS NOT PRIVATE"
    }
}