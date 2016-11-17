package com.zjh.mail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by ZJH on 2016-11-17-0017.
 */

public class write_Activity extends Activity {
    EditText et_receive;
    EditText et_title;
    EditText et_content;
    Button   btn_send;

    String from;
    String to;
    String flag;
    String subject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_layout);
        et_content= (EditText) findViewById(R.id.et_content_write);
        et_receive= (EditText) findViewById(R.id.et_receive_write);
        et_title= (EditText) findViewById(R.id.et_title_write);
        btn_send= (Button) findViewById(R.id.btn_send);
        flag=getIntent().getStringExtra("flag");
        if(flag.equals("2")) //回复打开
        {
            from=ConnectMail.name;
            String line=getIntent().getStringExtra("from");
            line=line.split("<")[line.split("<").length-1];
            line=line.split(">")[0];
            to=line;
            et_receive.setText(to);
        }
        if(flag.equals("1")) //转发打开
        {
            subject=getIntent().getStringExtra("subject");
            et_title.setText(subject);
            et_content.setText(getIntent().getStringExtra("content"));
        }
        if(flag.equals("3")) //发送打开
        {

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        setResult(5,intent);
        finish();
    }

    public  void btn_send_clicked(View v)
    {
        new SendTask().execute(et_content.getText().toString().trim(),et_title.getText().toString().trim(),et_receive.getText().toString().trim());
    }

    private  class SendTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... params) {
            return ConnectMail.sendMail(params[0],params[1],params[2]);
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("OK"))
            {
                Toast.makeText(write_Activity.this, "发送成功", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(write_Activity.this, "发送失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
