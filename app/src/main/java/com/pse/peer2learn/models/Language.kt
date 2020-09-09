package com.pse.peer2learn.models

import java.io.Serializable

/**
 * A model class that represents the used language in a study grouup
 */
class Language (
    val name: String,
    val shortName: String
): Serializable {
    constructor(): this("", "")

    constructor(name: String): this(
        name,
        if (name.length > 2) name.substring(0, 2) else name
    )
}