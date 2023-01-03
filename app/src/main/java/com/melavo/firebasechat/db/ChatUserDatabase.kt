package com.melavo.firebasechat.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.melavo.firebasechat.db.daos.ChatUserDao
import com.melavo.firebasechat.db.daos.GroupDao
import com.melavo.firebasechat.db.daos.GroupMessageDao
import com.melavo.firebasechat.db.daos.MessageDao
import com.melavo.firebasechat.db.data.ChatUser
import com.melavo.firebasechat.db.data.Group
import com.melavo.firebasechat.db.data.GroupMessage
import com.melavo.firebasechat.db.data.Message

@Database(entities = [ChatUser::class, Message::class,Group::class,GroupMessage::class],
    version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class ChatUserDatabase : RoomDatabase()  {
    abstract fun getChatUserDao(): ChatUserDao
    abstract fun getMessageDao(): MessageDao
    abstract fun getGroupDao(): GroupDao
    abstract fun getGroupMessageDao(): GroupMessageDao
}