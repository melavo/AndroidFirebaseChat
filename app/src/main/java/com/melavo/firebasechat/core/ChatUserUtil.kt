package com.melavo.firebasechat.core

import android.content.Context
import com.google.firebase.firestore.CollectionReference
import com.melavo.firebasechat.FirebasePush
import com.melavo.firebasechat.db.DbRepository
import com.melavo.firebasechat.db.data.ChatUser
import com.melavo.firebasechat.models.UserProfile
import com.melavo.firebasechat.utils.OnSuccessListener
import com.melavo.firebasechat.utils.UserUtils
import com.melavo.firebasechat.utils.Utils

class ChatUserUtil(private val dbRepository: DbRepository,
                   private val usersCollection: CollectionReference,
                   private val listener: OnSuccessListener?) {

    fun queryNewUserProfile(context: Context,chatUserId: String,docId: String?, unReadCount: Int=1,
                            showNotification: Boolean=false) {
        try {
            usersCollection.document(chatUserId)
                .get().addOnSuccessListener { profile ->
                    if (profile.exists()) {
                        val userProfile = profile.toObject(UserProfile::class.java)
                        val mobile = userProfile?.mobile?.country + " " + userProfile?.mobile?.number
                        val chatUser = ChatUser(userProfile?.uId!!, mobile, userProfile)
                        chatUser.unRead=unReadCount
                        if(docId!=null)
                            chatUser.documentId=docId
                        if (Utils.isContactPermissionOk(context)) {
                            val contacts = UserUtils.fetchContacts(context)
                            val savedContact=contacts.firstOrNull { it.mobile.contains(userProfile.mobile!!.number) }
                            savedContact?.let {
                                chatUser.localName=it.name
                                chatUser.locallySaved=true
                            }
                        }
                        listener?.onResult(true,chatUser)
                        dbRepository.insertUser(chatUser)
                        if(showNotification)
                        FirebasePush.showNotification(context,dbRepository)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}