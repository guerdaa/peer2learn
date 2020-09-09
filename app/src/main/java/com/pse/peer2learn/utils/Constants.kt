package com.pse.peer2learn.utils

import com.pse.peer2learn.ui.activities.GroupSearchPrivateFragment

/**
 * Help Class that is used to make the programm more flexible and easy adjustable
 */
class Constants {

    companion object {
        const val NUMBER_OF_FRAGMENTS = 3
        const val USER_COLLECTION = "USERS"
        const val MEMBER_COLLECTION = "MEMBERS"
        const val UNIVERSITY_COLLECTION = "UNIVERSITY"
        const val GROUPS_COLLECTION = "GROUPS"
        const val MESSAGES_COLLECTION = "MESSAGES"
        const val CATEGORY_COLLECTION = "CATEGORY"

        const val EMPTY_FILTER = "-"


        const val TITLE_ATTRIBUT = "title"
        const val UNIVERSITY_ATTRIBUT = "university"
        const val ENTRY_DATE_ATTRIBUT = "entryDate"
        const val ACCESS_CODE_ATTRIBUT = "accessCode"
            const val NUMBER_OF_MEMBERS = "numberOfMembers"

        const val SEEN_BY_ATTRIBUT = "seenBy"
        const val ORDER_ATTRIBUT = "order"
        const val STORAGE_URL = "https://firebasestorage.googleapis.com/"

        const val NUMBER_OF_PERSONS_FILTER_INDEX = 0
        const val LANGUAGE_FILTER_INDEX = 1
        const val PROGRESS_FILTER_INDEX = 2

        const val USER_EXTRA = "USER EXTRA"
        const val GROUP_EXTRA = "GROUP EXTRA"
        const val CATEGORY_NAME_EXTRA = "CATEGORY NAME EXTRA"
        const val APPOINTMENT_EXTRA = "APPOINTMENT EXTRA"

        const val CHANNEL_ID = "APPOINTMENTS CHANNEL"
        const val CHANNEL_NAME = "APPOINTMENTS"
        const val NOTIFICATION_ID = 1672
        const val REQUEST_CODE = "REQUEST_CODE"

        const val STORAGE_PATH = "profileImage/"
        const val IMG_DIR = "/img"

        const val CHAT_STORAGE_PATH = "chatImages/"

    }
}
