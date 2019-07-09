package com.example.myproject

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_message.*
import kotlinx.android.synthetic.main.activity_add_topic.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class AddMessageActivity: AppCompatActivity(){
    lateinit var person_id : String
    lateinit var topic_id : String
    lateinit var message : String
    val urlMessage = "http://192.168.0.17:3000/api/topics/ID/new"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_message)
        var intent = intent
        var data = intent.getSerializableExtra("data") as Array<*>
        topic_id = data[0].toString()
        person_id = data[1].toString()
        var topic= data[2].toString()
        topic_contents.text = topic
        btn_add_message.setOnClickListener(){
            message = new_message.text.toString().trim()
            if(message.length==0){
                Toast.makeText(applicationContext, "Topic can't empty", Toast.LENGTH_SHORT).show()
            } else{
                InsertMessage().execute(urlMessage)
                new_message.setText("")
                val intent: Intent = Intent(applicationContext, MessageActivity::class.java)
                startActivity(intent)
            }
        }
    }

    inner class InsertMessage : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String{
            return postData(params[0])
        }
        override fun onPostExecute(result:String?) {
            super.onPostExecute(result)
            if(result.equals("succes")){
                Toast.makeText(applicationContext, "Add message succes", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Add message succes", Toast.LENGTH_SHORT).show()
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
                .appendQueryParameter("person_id", person_id)
                .appendQueryParameter("topic_id", topic_id)
                .appendQueryParameter("message_contents", message)
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