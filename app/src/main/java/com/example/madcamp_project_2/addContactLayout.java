package com.example.madcamp_project_2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class addContactLayout extends AppCompatActivity {

    private EditText editText_name;
    private EditText editText_phone;
    ImageView choosePersonImage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_layout);
        Log.d("created","yes");
        this.getEditTextObject();
    }

    public void getEditTextObject(){
        editText_name = (EditText)findViewById(R.id.edittext_name);
        editText_phone = (EditText)findViewById(R.id.edittext_phone);
    }

    public void backBtnClicked(View v){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        Log.d("백","ㄴㅇㄹ");
    }

    public void contactadd(View view)
    {
        Intent resultIntent = new Intent();

        resultIntent.putExtra("name", editText_name.getText().toString());
        resultIntent.putExtra("phone", editText_phone.getText().toString());
        Log.d("sub에서",editText_name.getText().toString());
        setResult(Code.resultCode, resultIntent);
        finish();
    }

    public void choosePersonImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 2 && resultCode == RESULT_OK){
            try{
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();
                //가져온 이미지로 비트맵 생성
                choosePersonImage = (ImageView)findViewById(R.id.choosePersonImage);
                choosePersonImage.setImageBitmap(bitmap);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}