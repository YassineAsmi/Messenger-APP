package com.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_last_messages.*
import kotlinx.android.synthetic.main.last_msg_content.view.*

class LastMessages : AppCompatActivity() {
    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_messages)


        recyclerView_last_msg.adapter = adapter

        recycLastMsg()

        fetchCurrentUser()

        verifyUserLoggedIn()

    }

    class LastMessageContent(val chatMessage: ChatLogActivity.Messages) :
        Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.content_last_msg.text = chatMessage.text
        }

        override fun getLayout(): Int {
            return R.layout.last_msg_content
        }
    }

    val lastMsgMap = HashMap<String, ChatLogActivity.Messages>()

    private fun refRecycViewMsg() {
        adapter.clear()
        lastMsgMap.values.forEach {
            adapter.add(LastMessageContent(it))
        }


    }

    private fun recycLastMsg() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/last-Messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatLogActivity.Messages::class.java) ?: return
                adapter.add(LastMessageContent(chatMessage))
                lastMsgMap[p0.key!!] = chatMessage
                refRecycViewMsg()
            }


            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue((ChatLogActivity.Messages::class.java)) ?: return
                adapter.add(LastMessageContent(chatMessage!!))
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }


    val adapter = GroupAdapter<GroupieViewHolder>()


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue((User::class.java))
                Log.d("LastMessages", "Current user ${currentUser?.imgUrl}")
            }

        })

    }

    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.new_msg -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, SignUpActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_nav, menu)
        return super.onCreateOptionsMenu(menu)
    }


}
