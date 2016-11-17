package com.zjh.mail;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;


/**
 * Created by ZJH on 2016-10-31-0031.
 */

public class ConnectMail {

    static Socket socket;
    static Socket socket_smtp;

    static String username_base64;
    static String pwd_base64;
    static  String  name;
    static  String pwd;

    static String mailCompany_smtp="smtp.163.com";
    static String mailCompany_pop3="pop3.163.com";

    static int port_smtp=25;
    static int port_pop3=110;


    static BufferedReader bufferedReader;
    static PrintWriter printWriter;

    static BufferedReader bufferedReader_smtp;
    static PrintWriter printWriter_smtp;

    static Context context;

    public  static String flag="NO";


    public static void getStringAfterBase64(String name,String pwd)
    {
        ConnectMail.name=name;
        ConnectMail.pwd=pwd;
        username_base64=Base64.encodeToString(name.getBytes(),Base64.NO_WRAP);
        pwd_base64=Base64.encodeToString(pwd.getBytes(),Base64.NO_WRAP);
    }



   public  static  String connectMailCompany_Smtp()
    {
        try{

            socket_smtp=new Socket(mailCompany_smtp,port_smtp);
            String line="";
            StringBuilder builder=new StringBuilder();


            bufferedReader_smtp=new BufferedReader(new InputStreamReader(socket_smtp.getInputStream()));
            printWriter_smtp=new PrintWriter(socket_smtp.getOutputStream(),true);


            bufferedReader_smtp.readLine();
            //Log.i("youxiang:",bufferedReader.readLine());

            printWriter_smtp.println("HELO 163");
            bufferedReader_smtp.readLine();
            //Log.i("youxiang:",bufferedReader.readLine());


            printWriter_smtp.println("auth login");
            bufferedReader_smtp.readLine();
            //Log.i("youxiang:",bufferedReader.readLine());


            printWriter_smtp.println(username_base64);
            bufferedReader_smtp.readLine();
//            Log.i("name",username_base64);
//            Log.i("youxiang:",bufferedReader.readLine());

            printWriter_smtp.println(pwd_base64);
           // bufferedReader.readLine();
           // Log.i("pwd",pwd_base64);
            // Log.i("youxiang:",bufferedReader.readLine());
            line=bufferedReader_smtp.readLine();
            if(line.equals("235 Authentication successful"))
            {
                return "OK";
            }

        }catch (Exception e){
            e.printStackTrace();
            return "NO";
        }
        return "NO";
    }


    static  void quit_smtp()  {
        printWriter_smtp.println("quit");
    }

    static  void quit_pop3()  {
        printWriter.println("quit");
    }


    public  static  String connectMailCompany_Pop3(Context context){
        try{

            socket=new Socket(mailCompany_pop3,port_pop3);
            String line="";
            StringBuilder builder=new StringBuilder();

            bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter=new PrintWriter(socket.getOutputStream(),true);

            printWriter.println("USER "+name);
            bufferedReader.readLine();
            //Log.i("youxiang:",bufferedReader.readLine());


            printWriter.println("PASS "+pwd);
            //Log.i("youxiang:",bufferedReader.readLine());


            line=bufferedReader.readLine();
            if(line.startsWith("+OK"))
            {
                return GetMailInfo(context);
            }

        }catch (Exception e){
            return "-1";
        }
        return "-1";
    }

    public static String GetMailInfo(Context context)
    {
        try {
            printWriter.println("STAT");
            String line="";
            String info="";
            StringBuilder builder=new StringBuilder();
            info=bufferedReader.readLine();
            int num=Integer.parseInt(info.split(" ")[1]);
            for(int i=1;i<=num;i++)
            {
                FileOutputStream ops=context.openFileOutput(i+"mail_receive.txt",Context.MODE_PRIVATE);
                printWriter.println("RETR "+i);
                line=bufferedReader.readLine();
                while(!line.equals("."))
                {
                    line=bufferedReader.readLine();
                    //Log.i("信息",line);
                    if(!line.startsWith("+OK"))
                    {
                        ops.write(line.getBytes(),0,line.getBytes().length);
                        ops.write("\n".getBytes());
                        ops.flush();
                    }
                }
            }
            return String.valueOf(num);
        } catch (Exception e) {
            return  "-1";
        }
    }



    public  static String deleMail(int position,Context context)
    {
        connectMailCompany_Pop3(context);
        printWriter.println("DELE "+String.valueOf(position));
        String line="";
        try {
            line=bufferedReader.readLine();
            if(line.startsWith("+OK"))
            {
                Log.i("shanchu:",line);
                return "OK";
            }
        } catch (IOException e) {
           return "NO";
        }
        return "NO";
    }




    public  static   String sendMail(String content,String sub,String receives) {
       if(connectMailCompany_Smtp().equals("OK"))
       {
           try {
               printWriter_smtp.println("MAIL FROM:<"+name+"@163.com>");
//               Log.i("信息","MAIL FROM:<"+name+"@163.com>");
//               Log.i("信息",bufferedReader_smtp.readLine());
               bufferedReader_smtp.readLine();

               printWriter_smtp.println("RCPT TO:<"+receives+">");
//               Log.i("信息","RCPT TO:<"+receives+">");
//               Log.i("信息",bufferedReader_smtp.readLine());
               bufferedReader_smtp.readLine();

               printWriter_smtp.println("DATA");
//               Log.i("信息",bufferedReader_smtp.readLine());
               bufferedReader_smtp.readLine();

                   printWriter_smtp.println("TO:"+receives);
                   //Log.i("信息","TO:"+receives);
                   printWriter_smtp.println("FROM:"+name+"@163.com");
                   //Log.i("信息","FROM:"+name+"@163.com");
                   printWriter_smtp.println("SUBJECT:"+sub);
                   //Log.i("信息","SUBJECT:"+sub);
                   printWriter_smtp.println();
                   printWriter_smtp.println(content);
                   printWriter_smtp.println(".");
                   //Log.i("信息",bufferedReader_smtp.readLine());
                   quit_smtp();
                   return "OK";
           }
           catch (Exception e) {
               return "NO";
           }
       }
        else{
        return "NO";
       }
    }

}
