package com.pse.peer2learn.models

import java.io.Serializable

/**
 * A model class that represents the user
 */
class Student(
    var id: String,
    var nickname: String,
    var studyCourse: String,
    var university: University?,
    var studyGroupList: ArrayList<String>
): Serializable {

    constructor() : this(
        "",
        "",
        "",
        null,
        arrayListOf<String>()
    )

    constructor(id: String): this(
        id,
        "",
        "",
        null,
        arrayListOf<String>()
    )
}