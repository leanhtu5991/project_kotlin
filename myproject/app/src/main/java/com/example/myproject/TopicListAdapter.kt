package com.example.myproject
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class TopicListAdapter(var mCtx:Context , var resource:Int,var items:List<Topic>)
    :ArrayAdapter<Topic>( mCtx , resource , items ) {
//    override fun getItem(position: Int): Any {
//        return MainActivity.modelArrayList.get(position)
//    }
//
//    override fun getItemId(position: Int): Long {
//        return 0
//    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater :LayoutInflater = LayoutInflater.from(mCtx)

        val view : View = layoutInflater.inflate(resource , null )

        var textView : TextView = view.findViewById(R.id.title)
        var textView1 : TextView = view.findViewById(R.id.author)


        var topic : Topic = items[position]

        textView.text = topic.topic
        textView1.text = "At "+topic.date_create+" By "+ topic.author


        return view
    }
}