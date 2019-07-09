package com.example.myproject

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.Manifest
import android.os.Build
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ListView
import android.content.Intent
import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.os.AsyncTask.execute
import android.widget.ArrayAdapter
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket


class MainActivity : AppCompatActivity() {
    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
        val CONNECTON_TIMEOUT_MILLISECONDS = 60000
    }

    lateinit var listView : ListView
    var adapterTopic : ArrayAdapter<Topic>?=null
    lateinit var email_p : String
    lateinit var name_p : String
    lateinit var listTopic:ArrayList<Topic>
//    var adapterTopic : ArrayAdapter<Topic>? = null
//    var listTopic = mutableListOf<Topic>()
    val urlGetData:String = "http://192.168.0.17:3000/api/topics"
//val urlGetData:String = "https://developer.yahoo.com/weather/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        read()
        add_topic.setOnClickListener(){
            val intent: Intent = Intent(applicationContext, AddTopicActivity::class.java)
            val person: Person=Person(name_p, email_p)
            intent.putExtra("person", person)
            startActivity(intent)
        }

        listView = findViewById(R.id.listTopic)
        val list = mutableListOf<Topic>()
        listTopic = ArrayList()
        GetTopics().execute(urlGetData)

        adapterTopic = TopicListAdapter(this,R.layout.row,listTopic)
        listView.setAdapter ( adapterTopic)
        listView.setOnItemClickListener{parent, view, position, id->
            var d_email =email_p
            var d_topic_contents =listTopic.get(position).topic.toString()
            var d_topic_id = listTopic.get(position).id
            var d_person_id = listTopic.get(position).person_id
            var data = arrayOf(d_email, d_topic_contents, d_topic_id, d_person_id)
            Log.d("topic", data[0].toString())
            val intent: Intent = Intent(applicationContext, MessageActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
    }

    private fun read() {
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS)

        } else {
            getContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                read()
            } else {
            }
        }
    }

    private fun getContacts() {
        val cursorProfile = applicationContext.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if(cursorProfile == null){
        } else {
            if(cursorProfile.moveToFirst()){
                var emailAddress = ""
                val name = cursorProfile.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                var email = cursorProfile.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                name_p = cursorProfile.getString(name).toString()
                Log.d("tet", cursorProfile.getString(name).toString())
                val contactId = cursorProfile.getString(cursorProfile.getColumnIndex(ContactsContract.Contacts._ID))
                val emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null)
                while (emails!!.moveToNext()) {
                    emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    email_p = emailAddress
                }
                emails.close()
                cursorProfile.close()
            } else {
            }
        }
    }

    inner class GetTopics : AsyncTask<String, Void, String>(){
//        ArrayAdapter<Topic> arrayAdapter;
        override fun doInBackground(vararg params: String?): String {

            return getContentURL(params[0])
        }

        override fun onPreExecute() {
            listView.getAdapter()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var jsonArray:JSONArray = JSONArray(result)
            var topic_contents:String = ""
            var author:String = ""
            var date_create = ""
            var id: Int
            var person_id:Int
//            val list = mutableListOf<Topic>()
            listTopic.clear()
            for (topic in 0..jsonArray.length()-1){
                var objectTopic: JSONObject = jsonArray.getJSONObject(topic)
                topic_contents = objectTopic.getString("topic")
                author = objectTopic.getString("name")
                date_create = objectTopic.getString("date_create")
                id = objectTopic.getInt("id")
                person_id = objectTopic.getInt("person_id")
                listTopic.add(Topic(id, topic_contents, author, date_create, person_id))
            }
            adapterTopic!!.notifyDataSetChanged()
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

