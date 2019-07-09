package com.example.myproject

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_topic.*
import kotlinx.android.synthetic.main.activity_message.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MessageActivity : AppCompatActivity() {
    lateinit var listView : ListView
    var adapterMessage : ArrayAdapter<Message>?=null
    lateinit var listMessage:ArrayList<Message>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        var intent = intent
        var data = intent.getSerializableExtra("data")as Array<*>
        Log.d("email", data[0].toString())
        Log.d("content", data[1].toString())
        Log.d("topic_id", data[2].toString())
        Log.d("person_id", data[3].toString())
        var d_topic_contents = data[1].toString()
        var d_topic_id = data[2]
        var d_person_id = data[3]
        topic_contents.text = data[1].toString()
        val urlGetData:String = "http://192.168.0.17:3000/api/topics/"+d_topic_id
        listView = findViewById(R.id.listTopic)
        val list = mutableListOf<Topic>()
        listMessage = ArrayList()
        GetMessage().execute(urlGetData)

        adapterMessage = MessageListAdapter(this,R.layout.row,listMessage)
        listView.setAdapter ( adapterMessage)

        btn_add_message.setOnClickListener(){
            val intent: Intent = Intent(applicationContext, AddMessageActivity::class.java)
            var data = arrayOf(d_topic_id, d_person_id, d_topic_contents)
            intent.putExtra("data", data)
            startActivity(intent)
        }
    }
    inner class GetMessage : AsyncTask<String, Void, String>(){
        //        ArrayAdapter<Topic> arrayAdapter;
        override fun doInBackground(vararg params: String?): String {

            return getContentURL(params[0])
        }

        override fun onPreExecute() {
            listView.getAdapter()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var jsonArray: JSONArray = JSONArray(result)
            var message:String = ""
            var author:String = ""
            var date_create = ""
//            var id: Int
//            var person_id:Int
//            val list = mutableListOf<Topic>()
            listMessage.clear()
            for (topic in 0..jsonArray.length()-1){
                var objectTopic: JSONObject = jsonArray.getJSONObject(topic)
                message = objectTopic.getString("message")
                author = objectTopic.getString("name")
                date_create = objectTopic.getString("date_create")
                listMessage.add(Message(message, author, date_create))
            }
            adapterMessage!!.notifyDataSetChanged()
        }
    }

    private fun getContentURL(url: String?): String {
        var content:StringBuilder = StringBuilder();
        var url : URL = URL(url)
        var urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
//    urlConnection.connectTimeout = CONNECTON_TIMEOUT_MILLISECONDS
//    urlConnection.readTimeout = CONNECTON_TIMEOUT_MILLISECONDS
        val inputStreamReader: InputStreamReader = InputStreamReader(urlConnection.inputStream)
        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)

        var line:String = ""
        try{
            do{
                line = bufferedReader.readLine()
                if(line!=null){
                    content.append(line)
                }
            } while(line!=null)
            bufferedReader.close()
        } catch(e:Exception){
            Log.d("erreur", e.toString())
        }
        return content.toString()
    }

}
