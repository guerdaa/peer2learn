package com.pse.peer2learn.utils

import org.junit.Assert.*
import org.junit.Test

class UtilsTest {

    @Test
    fun testConvertDate() {
        assertEquals(Utils.convertDate(1597067942), "19.01.1970 12:37")
    }
}