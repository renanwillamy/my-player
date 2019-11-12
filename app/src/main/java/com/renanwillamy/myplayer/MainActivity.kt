package com.renanwillamy.myplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img_main.setImageResource(R.drawable.player_background)

        img_play.setOnClickListener{}
        img_back.setOnClickListener{}
        img_forward.setOnClickListener{
            Toast.makeText(this,"Forward",Toast.LENGTH_SHORT).show()
        }
        img_folder.setOnClickListener{
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Toast.makeText(this,selectedFile?.path.toString(),Toast.LENGTH_LONG).show()
        }
    }
}
