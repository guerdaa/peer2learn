package com.pse.peer2learn.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.UserRepository
/**
 * Holds the logic of the GroupActivity
 */
class GroupsViewModel: ViewModel {

    private val _isAdmin = MutableLiveData(false)
    val isAdmin: LiveData<Boolean> get() = _isAdmin

    private var memberRepository = MemberRepository(isAdmin = _isAdmin)

    constructor()

    constructor(memberRepository: MemberRepository): this() {
        this.memberRepository = memberRepository
    }

    /**
     * Help methode that calls the [memberRepository] to check if an user with [currentUserId] is an admin
     * Help method to "connect" the view with the repository
     */
    fun checkIfUserIsAdmin(currentGroupId: String, currentUserId: String) {
        memberRepository.checkIfUserIsAdmin(currentGroupId, currentUserId)
    }
}