package com.pse.peer2learn.utils

import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        /**
         * method used to convert date from milliseconds to "dd.MM.yyyy HH:mm" format
         */
        fun convertDate(date: Long): String {
            val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            return dateFormatter.format(calendar.time)
        }
    }
}