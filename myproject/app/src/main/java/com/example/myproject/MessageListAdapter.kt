package com.example.myproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MessageListAdapter(var mCtx: Context, var resource:Int, var items:List<Message>)
    : ArrayAdapter<Message>( mCtx , resource , items ) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource, null)

        var textView: TextView = view.findViewById(R.id.title)
        var textView1: TextView = view.findViewById(R.id.author)

        var message: Message = items[position]

        textView.text = message.message
        textView1.text = "At " + message.date_create + " By " + message.author
        return view
    }
}