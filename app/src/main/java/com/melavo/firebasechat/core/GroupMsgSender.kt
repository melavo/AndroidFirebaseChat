package com.melavo.firebasechat.core

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.melavo.firebasechat.db.data.Group
import com.melavo.firebasechat.db.data.GroupMessage

interface OnGrpMessageResponse{
    fun onSuccess(message: GroupMessage)
    fun onFailed(message: GroupMessage)
}

class GroupMsgSender(private val groupCollection: CollectionReference) {

    fun sendMessage(message: GroupMessage,group: Group,listener: OnGrpMessageResponse){
        message.status[0]=1
        groupCollection.document(group.id).collection("group_messages")
            .document(message.createdAt.toString()).set(message, SetOptions.merge())
            .addOnSuccessListener {
                listener.onSuccess(message)
            }.addOnFailureListener {
                message.status[0]=4
                listener.onFailed(message)
            }
    }

}