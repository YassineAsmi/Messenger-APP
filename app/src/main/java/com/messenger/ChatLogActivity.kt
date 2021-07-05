package com.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_person.view.*
import kotlinx.android.synthetic.main.chat_too_person.view.*
import kotlinx.android.synthetic.main.chat_too_person.view.imageView_from as imageView_from1

class ChatLogActivity : AppCompatActivity() {
    companion object {
        val TAG = "chatlog"
    }

    var toUser: User? = null
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerview_chat.adapter = adapter
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username
        recy_msg()
        btn_send.setOnClickListener {
            Log.d(TAG, "attempt to send message")
            send_msg()

        }

    }

    private fun recy_msg() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue((Messages::class.java))
                Log.d(TAG, chatMessage?.text)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)
                    val currentUser = LastMessages.currentUser
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(chatItem(chatMessage.text, currentUser!!))
                    } else {
                        adapter.add(chattoItem(chatMessage.text, toUser!!))
                    }

                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }


        })

    }



    private fun send_msg() {
        val text = text_chat.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid
        if (fromId == null) return
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val Message =
            Messages(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(Message)
            .addOnSuccessListener {
                Log.d(TAG, "tsajel el msg fi ${reference.key}")
                text_chat.text.clear()
                recyclerview_chat.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue((Message))
        val lastMsgRef =
            FirebaseDatabase.getInstance().getReference("/latest-message/$fromId/$toId")
        lastMsgRef.setValue(Message)
        val lastMsgtoRef =
            FirebaseDatabase.getInstance().getReference("/latest-message/$toId/$fromId")
        lastMsgtoRef.setValue(Message)

    }
    class Messages(
        val id: String = "",
        val text: String = "",
        val fromId: String = "",
        val toId: String = "",
        val timestamp: Long = -1
    )
}

class chatItem(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from.text = text
        val uri = user.imgUrl
        val targetImageView = viewHolder.itemView.imageView_from
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_person
    }
}

class chattoItem(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_too.text = text
        val uri = user.imgUrl
        val targetImageView = viewHolder.itemView.imageView_from
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_too_person
    }
}
