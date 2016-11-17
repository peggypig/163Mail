package com.zjh.mail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button btn_login;
    EditText et_name;
    EditText et_pwd;
    CheckBox cb;
    SharedPreferences sharedPreferences;
    String name;
    String pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_login=(Button)findViewById(R.id.btn_click);
        et_name=(EditText)findViewById(R.id.et_username);
        et_pwd=(EditText)findViewById(R.id.et_pwd);
        cb= (CheckBox) findViewById(R.id.cb_save);
        sharedPreferences=getSharedPreferences("user_info", Context.MODE_PRIVATE);
        init();
    }
    public  void init()
    {
        name = sharedPreferences.getString("username", "");
        pwd=sharedPreferences.getString("pwd","");
        et_pwd.setText(pwd);
        et_name.setText(name);
    }

    public  void btn_login_clicked(View v)
    {
        name=et_name.getText().toString().trim();
        if(name.length()==0)
        {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        pwd=et_pwd.getText().toString();
        if(pwd.length()==0)
        {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //记住密码的功能
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(cb.isChecked())
        {
            editor.putString("username",name);
            editor.putString("pwd",pwd);
        }
        else{
            editor.putString("username","");
            editor.putString("pwd","");
        }
        editor.apply();
        new LoginTask().execute();
    }


    public  class LoginTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... params) {
            ConnectMail.getStringAfterBase64(name,pwd);
            String  s= ConnectMail.connectMailCompany_Smtp();
            if(s.equals("OK"))
            {
                ConnectMail.quit_smtp();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("OK"))
            {
                Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,maillist_Activity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "登陆失败，账号或者密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
