package com.zjh.mail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * Created by ZJH on 2016-11-17-0017.
 */

public class MailDetailActivity extends Activity {
    TextView tv_sender;
    TextView tv_content;
    Button btn_replay;
    Button btn_del;
    Button btn_transmit;
    String from;
    String content;
    String subject;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_detail_layout);
        tv_sender= (TextView) findViewById(R.id.tv_sender);
        tv_content= (TextView) findViewById(R.id.tv_content);
        btn_del= (Button) findViewById(R.id.btn_del);
        btn_replay= (Button) findViewById(R.id.btn_replay);
        btn_transmit= (Button) findViewById(R.id.btn_transmit);
        from=getIntent().getStringExtra("From");
        id=getIntent().getIntExtra("id",0);
        subject=getIntent().getStringExtra("subject");
        tv_sender.setText(from);
        new GetContentTask().execute();
    }

    private class GetContentTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... params) {
         return getContentFromFile();
        }

        @Override
        protected void onPostExecute(String s) {
            if(!s.equals("OK"))
            {
                Toast.makeText(MailDetailActivity.this, "获取正文内容失败,请稍后再试", Toast.LENGTH_SHORT).show();
            }
            else{
                tv_content.setText(content);
            }
        }
    }


    public String getContentFromFile()
    {
        FileInputStream fis=null;
        try {
            fis=this.openFileInput(String.valueOf(id)+"mail_receive.txt");
            BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
            StringBuilder builder=new StringBuilder();
            String line="";
            while((line=reader.readLine())!=null)
            {
                builder.append(line);
            }
            content=builder.toString();
        } catch (Exception e) {
           return "NO";
        }
        return "OK";
    }


    public  void btn_del_clicked(View v)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("确定删除？");
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleMailTask().execute();
            }
        });
        builder.show();
    }

    private class DeleMailTask extends  AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... params) {
            return ConnectMail.deleMail(id,getApplicationContext());
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("NO"))
            {
                Toast.makeText(MailDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MailDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                MailDetailActivity.this.deleteFile(String.valueOf(id)+"mail_receive.txt");
                getIntent().putExtra("DELE","Yes");
                setResult(1,getIntent());
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        getIntent().putExtra("DELE","No");
        setResult(2,getIntent());
        finish();
    }

    public  void btn_replay_clicked(View v)
    {
        Intent intent=new Intent(this,write_Activity.class);
        intent.putExtra("from",from);
        intent.putExtra("flag","2");
        startActivityForResult(intent,2);
    }

    public void btn_transmit_clicked(View v)
    {
        Intent intent=new Intent(this,write_Activity.class);
        intent.putExtra("content",content);
        intent.putExtra("flag","1");
        intent.putExtra("subject",subject);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
