package com.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "select User"

        fetchUsers()

    }
companion object{
    val USER_KEY ="USER_KEY"
}
    private fun fetchUsers() {
        Log.d("NewMessage", "d5al fetch user")
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        Log.d("NewMessage", "bech yod5ol fel addListner")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("NewMessage", "d5al lel ondatachange")
                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem=item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }
                Log.d("NewMessage", "mezelt")
                recyclerview_newmsg.adapter = adapter
                Log.d("NewMessage", "mchet")
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("NewMessage", "d5al lel ondcancelled")
            }
        })
    }

}

class UserItem(val user: User) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_new_message.text = user.username
        Log.d("NewMessage", "9a3edd nchargi fel taswira")
        Picasso.get().load(user.imgUrl).into(viewHolder.itemView.img_new_msg)
        Log.d("NewMessage", "taswira charget")
    }

    override fun getLayout(): Int {
        return R.layout.user_new_message
    }
}

