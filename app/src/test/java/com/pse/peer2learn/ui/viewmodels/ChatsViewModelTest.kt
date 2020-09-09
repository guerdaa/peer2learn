package com.pse.peer2learn.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.repositories.MessageRepository
import com.pse.peer2learn.ui.activities.interfaces.IChatsListener
import io.mockk.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers.any

class ChatsViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var chatsViewModel: ChatsViewModel
    private lateinit var register: ListenerRegistration
    private lateinit var messageRepository: MessageRepository

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        messageRepository = mockk(relaxed = true)
        register = mockk(relaxed = true)
        chatsViewModel = ChatsViewModel(register, messageRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun unregisterTest() {
        chatsViewModel.unregister()

        verify { register.remove() }
    }

    @Test
    fun registerTest() {
        val currentId = "ID"
        val chatListener: IChatsListener = mockk(relaxed = true)
        val registerMocked: ListenerRegistration = mockk(relaxed = true)
        every { messageRepository.register(currentId, chatListener) } returns registerMocked

        chatsViewModel.register(currentId, chatListener)

        assertEquals(chatsViewModel.register, registerMocked)
    }

    @Test
    fun setFirestoreOptionsTest() {
        val firestoreMocked: FirestoreRecyclerOptions<Message> = mockk(relaxed = true)
        every { messageRepository.setFirestoreOptions(any()) } returns firestoreMocked

        assertEquals(chatsViewModel.setFirestoreOptions("ID"), firestoreMocked)
    }

    @Test
    fun setSeenTest() {
        val groupId = "Group ID"
        val userId = "user ID"
        val messageId = "message ID"

        chatsViewModel.setSeen(userId, groupId, messageId)

        verify { messageRepository.setSeen(userId, groupId, messageId) }
    }

    @Test
    fun sendTest() {
        val groupId = "group ID"

        chatsViewModel.send("any", groupId, mockk(relaxed = true), 0)

        verify { messageRepository.send(any(), groupId) }
    }

}