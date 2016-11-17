package com.zjh.mail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZJH on 2016-11-16-0016.
 */

public class maillist_Activity extends Activity {

    TextView tv_change;
    TextView tv_write;
    ListView lv_mail;

    List<MAIL> lists;
    BaseAdapter adapter;
    int p=0;
    int num=0;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maillist_layout);
        tv_change= (TextView) findViewById(R.id.tv_change);
        tv_write= (TextView) findViewById(R.id.tv_write);
        lv_mail= (ListView) findViewById(R.id.lv_mail);
        lv_mail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(maillist_Activity.this,MailDetailActivity.class);
                p=position;
                intent.putExtra("id",position+1);
                intent.putExtra("From",lists.get(position).getFrom());
                intent.putExtra("subject",lists.get(position).getSubject());
                startActivityForResult(intent,1);
            }
        });
        dialog=new ProgressDialog(maillist_Activity.this);
        dialog.setMax(100);
        dialog.setMessage("正在更新消息列表...");
        dialog.incrementProgressBy(20);
        new ConnectMailTask().execute();
    }


    public  void tv_change_clicked(View view)
    {
        new ConnectMailTask().execute();
    }

    public void tv_write_clicked(View view)
    {
        Intent intent=new Intent(this,write_Activity.class);
        intent.putExtra("flag","3");
        startActivityForResult(intent,1);
    }

    public class ConnectMailTask extends AsyncTask<Void,String,String>
    {
        @Override
        protected String doInBackground(Void... params) {
            publishProgress();
            return ConnectMail.connectMailCompany_Pop3(getApplicationContext());
        }

        @Override
        protected void onProgressUpdate(String... values) {
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            ConnectMail.quit_pop3();
            if(!s.equals("-1")&&!s.equals("0"))
            {
                num=Integer.parseInt(s);
                new GetMailListFromFiles().execute();
            }
            else if(s.equals("0"))
            {
                Toast.makeText(maillist_Activity.this, "空邮箱", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetMailListFromFiles extends  AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... params) {
            publishProgress();
            return GetMailList();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            dialog.cancel();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("-1")){
                Toast.makeText(maillist_Activity.this, "获取邮件列表失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
            else if(s.equals("1"))
            {
                adapter=new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return lists.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return lists.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View v;
                        if(convertView!=null)
                        {
                            v=convertView;
                        }
                        else{
                            v=View.inflate(maillist_Activity.this,R.layout.listitem_layout,null);
                        }
                        TextView tv_sub= (TextView) v.findViewById(R.id.tv_subject);
                        TextView tv_from_to= (TextView) v.findViewById(R.id.tv_from_to);
                        tv_sub.setText(lists.get(position).getSubject());
                        tv_from_to.setText(lists.get(position).getFrom());

                        return  v;
                    }
                };
                lv_mail.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1)
        {
            String flag=data.getStringExtra("DELE");
            if(flag.equals("Yes"))
            {
                lists.remove(p);
                num--;
                adapter=new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return lists.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return lists.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View v;
                        if(convertView!=null)
                        {
                            v=convertView;
                        }
                        else{
                            v=View.inflate(maillist_Activity.this,R.layout.listitem_layout,null);
                        }
                        TextView tv_sub= (TextView) v.findViewById(R.id.tv_subject);
                        TextView tv_from_to= (TextView) v.findViewById(R.id.tv_from_to);
                        tv_sub.setText(lists.get(position).getSubject());
                        tv_from_to.setText(lists.get(position).getFrom());
                        return  v;
                    }
                };
                lv_mail.setAdapter(adapter);
            }
        }
        if(resultCode==5)
        {
            new ConnectMailTask().execute();
        }
    }

    public String GetMailList(){
        String s=tv_change.getText().toString().trim();
        if(s.equals("收件箱(刷新)"))
        {
            s="mail_receive.txt";
        }

        try {
            lists=new ArrayList<>();
            for(int i=1;i<num+1;i++)
            {
                FileInputStream fis=this.openFileInput(i+s);
                BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
                String line="";
                MAIL mail=new MAIL();
                boolean flag=false;
                while((line=reader.readLine())!=null)
                {
                    if(line.contains("SUBJECT:")&&flag==false)
                    {
                        mail.setSubject(line.split("SUBJECT:")[1]);
                        flag=true;
                    }
                    if(line.contains("Subject:")&&flag==false)
                    {
                        mail.setSubject(line.split("Subject:")[1]);
                        flag=true;
                    }
                    if(line.startsWith("From:")||line.startsWith("FROM:"))
                    {
                        mail.setFrom(line.split(" ")[line.split(" ").length-1]);
                    }
                    if(line.contains("To:")||line.contains("TO:"))
                    {
                        mail.setTo(line.split(" ")[line.split(" ").length-1]);
                    }
                }
                //this.deleteFile(i+s);
                lists.add(mail);
            }
            return "1";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return  "-1";
        }

    }

}
