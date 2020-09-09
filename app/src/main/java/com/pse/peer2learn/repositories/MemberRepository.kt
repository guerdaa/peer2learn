package com.pse.peer2learn.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pse.peer2learn.models.Member
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress

/***
 * The class is used to manage instances of the model class [Member] inside the Firebase database.
 */
class MemberRepository(
    private val updateMemberLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val participantsCountLiveData: MutableLiveData<Int> = MutableLiveData(),
    private val deleteMemberLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val updateAdminOfGroupLiveData: MutableLiveData<RepositoryProgress> = MutableLiveData(),
    private val isAdmin: MutableLiveData<Boolean> = MutableLiveData()
): AbstractRepository<Member>() {

    private val groupCollection = firebaseInstance.collection(Constants.GROUPS_COLLECTION)

    /***
     * Creates a member [instance] in the member collection of a specific group with [instance].groupId
     */
    override fun create(instance: Member) {
    }

    /***
     * Removes a member [instance] from a member collection of a specific group with [instance].groupId
     */
    override fun delete(instance: Member) {
        deleteMemberLiveData.value = RepositoryProgress.IN_PROGRESS
        groupCollection.document(instance.groupId)
            .collection(Constants.MEMBER_COLLECTION)
            .document(instance.id)
            .delete()
            .addOnSuccessListener {
                deleteMemberLiveData.value = RepositoryProgress.SUCCESS
                Log.d(TAG, "delete: successfully")
            }.addOnFailureListener {
                deleteMemberLiveData.value = RepositoryProgress.FAILED
                Log.d(TAG, "delete: failed")
            }
    }

    /***
     * Updates the data a member, in the member collection of a specific group with [instance].groupId, with the data in [instance].
     */
    override fun update(instance: Member) {
        updateMemberLiveData.value = RepositoryProgress.IN_PROGRESS
        groupCollection.document(instance.groupId)
            .collection(Constants.MEMBER_COLLECTION)
            .document(instance.id)
            .set(instance)
            .addOnSuccessListener {
                updateMemberLiveData.value = RepositoryProgress.SUCCESS
                Log.d(TAG, "update: successfully")
            }.addOnFailureListener {
                updateMemberLiveData.value = RepositoryProgress.FAILED
                Log.d(TAG, "update: failed")
            }
    }

    /***
     * Updates the admin of the group with [groupId] by checking :
     * Whether one of the members already an admin is and if that is the case nothing is changed
     * If no one from the members list is admin, then the first member is made admin
     * This method is called everytime when an user quits a group or deletes his account, if the number of members is not zero in the correponging groups.
     */
    fun updateAdminsOfGroup(groupId: String) {
        updateAdminOfGroupLiveData.value = RepositoryProgress.IN_PROGRESS
        var firstMember = Member()
        var groupHasAdmin = false
        groupCollection.document(groupId)
            .collection(Constants.MEMBER_COLLECTION)
            .orderBy(Constants.ENTRY_DATE_ATTRIBUT)
            .get()
            .addOnSuccessListener { docs ->
                for(doc in docs) {
                    doc.toObject(Member::class.java).let { member ->
                        firstMember = member
                        if(member.isAdmin) {
                            groupHasAdmin = true
                            updateAdminOfGroupLiveData.value = RepositoryProgress.SUCCESS
                            return@addOnSuccessListener
                        }
                    }
                }
                if(!groupHasAdmin && firstMember.id.isNotEmpty()) {
                    Log.d("∞∞∞∞∞∞∞∞, updateAdmin", "${firstMember.id} +++")
                    firstMember.isAdmin = true
                    update(firstMember)
                }
                updateAdminOfGroupLiveData.value = RepositoryProgress.SUCCESS
            }.addOnFailureListener {
                updateAdminOfGroupLiveData.value = RepositoryProgress.FAILED
            }
    }
    /***
     * Returns the number of members in a group with the [groupId]
     * Used to detect whether the group should be deleted if it's empty or the admins inside a group should be updated if there are no more admins inside
     */
    fun getNumberOfParticipant(groupId: String) {
        var count = 0
        groupCollection.document(groupId)
            .collection(Constants.MEMBER_COLLECTION)
            .get()
            .addOnSuccessListener {docs ->
                for (document in docs) {
                    count++
                }
                participantsCountLiveData.value = count
            }
    }

    /**
     * Verifies whether an user with [userId] is and admin inside the group with [groupId]
     * Help method that is used when a new GroupActivity() is called to know what access to give to the current user.
     */
    fun checkIfUserIsAdmin(groupId: String, userId: String) {
        groupCollection.document(groupId)
            .collection(Constants.MEMBER_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(Member::class.java).let {
                    isAdmin.value = it?.isAdmin
                }
            }
    }

    companion object {
        const val TAG = "MemberRepository"
    }


}