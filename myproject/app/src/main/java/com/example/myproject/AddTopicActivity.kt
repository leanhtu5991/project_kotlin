package com.example.myproject

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_topic.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class AddTopicActivity : AppCompatActivity() {
    lateinit var email : String
    lateinit var name : String
    lateinit var topic : String
    val urlTopic:String = "http://192.168.0.17:3000/api/topics/new"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic)
        var intent = intent
        var person:Person = intent.getSerializableExtra("person") as Person
        Log.d("person name", person.name)
        Log.d("person email", person.email)
        name = person.name
        email = person.email
        name_p.text = person.name
        email_p.text = person.email

        btn_add_topic.setOnClickListener(){
            val topic_contents = new_topic.text.toString().trim()
            if(topic_contents.length==0){
                Toast.makeText(applicationContext, "Topic can't empty", Toast.LENGTH_SHORT).show()
            } else{
                topic = topic_contents
                InsertTopic().execute(urlTopic)
                new_topic.setText("")
                val intent: Intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
    inner class InsertTopic : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String{
            return postData(params[0])
        }
        override fun onPostExecute(result:String?) {
            super.onPostExecute(result)
            if(result.equals("succes")){
                Toast.makeText(applicationContext, "Add topic succes", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Add topic succes", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun postData(link: String?): String {
        val connect: HttpURLConnection
        var url: URL =  URL(link)
        try {
            connect = url.openConnection() as HttpURLConnection
            connect.readTimeout = 10000
            connect.connectTimeout = 15000
            connect.requestMethod = "POST"
            // POST theo tham số
            val builder = Uri.Builder()
                .appendQueryParameter("person_name", name)
                .appendQueryParameter("person_email", email)
                .appendQueryParameter("topic_contents", topic)
            val query = builder.build().getEncodedQuery()
            val os = connect.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(query)
            writer.flush()
            writer.close()
            os.close()
            connect.connect()
        } catch (e1: IOException) {
            e1.printStackTrace()
            return "Error!"
        }

        try {
            // Đọc nội dung trả về sau khi thực hiện POST
            val response_code = connect.responseCode
            if (response_code == HttpURLConnection.HTTP_OK) {
                val input = connect.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                val result = StringBuilder()
                var line: String
                try {
                    do{
                        line = reader.readLine()
                        if(line != null){
                            result.append(line)
                        }
                    }while (line != null)

                    reader.close()
                }catch (e:Exception){}

                return result.toString()
            } else {
                return "Error!"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return "Error!"
        } finally {
            connect.disconnect()
        }
    }
}
