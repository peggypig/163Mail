package com.zjh.mail;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
//    }
    Socket socket_connect;
    OutputStream outputStream = null;
    InputStream inputStream = null;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    @Test
    public void connectMailCompany()
    {
        try{


            InetAddress address=InetAddress.getByName("smtp.qq.com");
            socket_connect=new Socket(address,25);//sslSocketFactory.createSocket(new Socket(address,25),address.getHostName(),25,true);
            inputStream = socket_connect.getInputStream();
            outputStream = socket_connect.getOutputStream();
            printWriter=new PrintWriter(outputStream,true);
            bufferedReader=new BufferedReader(new InputStreamReader(inputStream));



            printWriter.println("HELO"+" smtp.163.com");
            receive();
            printWriter.println("auth login");
            receive();

            printWriter.println("Nzk5MjQzOTE3QHFxLmNvbQ==");
            receive();
            printWriter.println("MTk5NHpqaDA4MTlaSkg=");
            receive();

        }catch (Exception e){
        }

    }


    void receive()
    {

        try {
            byte [] bytes=new byte[1024];
            inputStream.read(bytes);
            System.out.print(new String(bytes));
            System.out.println("接受完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}