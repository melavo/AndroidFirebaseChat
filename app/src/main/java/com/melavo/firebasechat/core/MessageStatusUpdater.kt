package com.melavo.firebasechat.core

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.melavo.firebasechat.db.data.ChatUser
import com.melavo.firebasechat.db.data.Message
import com.melavo.firebasechat.di.MessageCollection
import com.melavo.firebasechat.fragments.single_chat.serializeToMap
import com.melavo.firebasechat.utils.LogMessage
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageStatusUpdater @Inject constructor(
    @MessageCollection
    private val msgCollection: CollectionReference,
    private val firebaseFirestore: FirebaseFirestore
) {

    fun updateToDelivery(messageList: List<Message>, vararg chatUsers: ChatUser) {
        val batch = firebaseFirestore.batch()
        for (chatUser in chatUsers) {
            if (chatUser.documentId.isNullOrBlank())
                continue
            val msgSubCollection =
                msgCollection.document(chatUser.documentId!!).collection("messages")
            val filterList = messageList
                .filter { msg -> msg.status == 1 && msg.from == chatUser.id }
                .map {
                    it.chatUserId = null
                    it.status = 2
                    it.deliveryTime = System.currentTimeMillis()
                    it
                }
            if (filterList.isNotEmpty()) {
                for (msg in filterList) {
                    batch.update(
                        msgSubCollection
                            .document(msg.createdAt.toString()), msg.serializeToMap()
                    )
                }
            }
        }
        batch.commit().addOnSuccessListener {
            LogMessage.v("Batch update success from home")
        }.addOnFailureListener {
            LogMessage.v("Batch update failure ${it.message} from home")
        }
    }

     fun updateToSeen(toUser: String, docId: String?, messageList: List<Message>) {
         if(docId==null)
             return
        val msgSubCollection = msgCollection.document(docId).collection("messages")
        val batch = firebaseFirestore.batch()
        val currentTime = System.currentTimeMillis()
        val filterList = messageList
            .filter { msg -> msg.from == toUser && msg.status != 3 }
            .map {
                it.status = 3
                it.chatUserId = null
                it.deliveryTime = it.deliveryTime
                it.seenTime = currentTime
                it
            }
        if (filterList.isNotEmpty()) {
            Timber.v("Size of list ${filterList.last().createdAt}")
            for (message in filterList) {
                batch.update(
                    msgSubCollection
                        .document(message.createdAt.toString()), message.serializeToMap()
                )
            }
            batch.commit().addOnSuccessListener {
                LogMessage.v("All Message Seen Batch update success")
            }.addOnFailureListener {
                LogMessage.v("All Message Seen Batch update failure ${it.message}")
            }
        } else
            LogMessage.v("All message already seen")
    }
}