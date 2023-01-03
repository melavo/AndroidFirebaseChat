package com.melavo.firebasechat.core

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.melavo.firebasechat.db.data.GroupMessage
import com.melavo.firebasechat.di.GroupCollection
import com.melavo.firebasechat.fragments.single_chat.asMap
import com.melavo.firebasechat.fragments.single_chat.serializeToMap
import com.melavo.firebasechat.utils.LogMessage
import com.melavo.firebasechat.utils.Utils.myIndexOfStatus
import com.melavo.firebasechat.utils.Utils.myMsgStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupMsgStatusUpdater @Inject constructor(
    @GroupCollection
    private val groupCollection: CollectionReference,
     private val firestore: FirebaseFirestore) {

    fun updateToDelivery(myUserId: String, messageList: List<GroupMessage>, vararg groupId: String){
        try {
            val batch= firestore.batch()
            for (id in groupId){
                val msgSubCollection=groupCollection.document(id).collection("group_messages")
                val filterList=  messageList
                    .filter { it.from!=myUserId && myMsgStatus(myUserId,it)==0 && it.groupId==id }
                    .map {
                        val myIndex=myIndexOfStatus(myUserId,it)
                        it.status[myIndex]=2
                        it.deliveryTime[myIndex]=System.currentTimeMillis()
                        it
                    }
                    for (msg in filterList){
                        LogMessage.v("message date ${msg.deliveryTime}")
                        batch.update(msgSubCollection
                            .document(msg.createdAt.toString()),msg.asMap())
                    }
            }
            batch.commit().addOnSuccessListener {
                LogMessage.v("Batch update success from group")
            }.addOnFailureListener {
                LogMessage.v("Batch update failure ${it.message} from group")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateToSeen(myUserId: String, messageList: List<GroupMessage>, groupId: String){
        val batch= firestore.batch()
        val currentTime = System.currentTimeMillis()
        val msgSubCollection=groupCollection.document(groupId).collection("group_messages")
            val filterList=  messageList
                .filter { it.from!=myUserId && myMsgStatus(myUserId,it)<3 }
                .map {
                    val myIndex=myIndexOfStatus(myUserId,it)
                    it.status[myIndex]=3
                    it.deliveryTime[myIndex]= if (it.deliveryTime[myIndex]==0L)
                        currentTime else it.deliveryTime[myIndex]
                    it.seenTime[myIndex]=currentTime
                    it
                }

            if (filterList.isNotEmpty()){
                for (msg in filterList){
                    batch.update(msgSubCollection
                        .document(msg.createdAt.toString()),msg.serializeToMap())
                }
            }
        batch.commit().addOnSuccessListener {
            LogMessage.v("Seen Batch update success from group")
        }.addOnFailureListener {
            LogMessage.v("Seen Batch update failure ${it.message} from group")
        }
    }
}