package edu.upc.whatsapp.comms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entity.Message;
import entity.User;
import entity.UserInfo;

import static edu.upc.whatsapp.comms.Comms.gson;
import static edu.upc.whatsapp.comms.Comms.url_rpc;

public class RPC {
  public static final int TIMEOUT = 5000;
  public static UserInfo registration(User user) {
    try {
      URL url = new URL(url_rpc+"/1_registration.jsp");
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
      ucon.setRequestMethod("POST");
      ucon.setDoInput(true);
      ucon.setDoOutput(true);
      ucon.setConnectTimeout(TIMEOUT);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");

      PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
      out.println(gson.toJson(user));

      ucon.connect();

      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      UserInfo userInfo = gson.fromJson(in, UserInfo.class);

      in.close();
      out.close();
      ucon.getInputStream().close();

      return userInfo;

    } catch (Exception e) {
      e.printStackTrace();
      UserInfo userInfo_exception = new UserInfo(-2);
      userInfo_exception.setName(e.getMessage());
      return userInfo_exception;
    }
  }
  public static UserInfo login(User user) {
    try {
      URL url = new URL(url_rpc+"/2_login.jsp");
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
      ucon.setRequestMethod("POST");
      ucon.setDoInput(true);
      ucon.setDoOutput(true);
      ucon.setConnectTimeout(TIMEOUT);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");

      PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
      out.println(gson.toJson(user));

      ucon.connect();

      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      UserInfo userInfo = gson.fromJson(in, UserInfo.class);

      in.close();
      out.close();
      ucon.getInputStream().close();

      return userInfo;

    } catch (Exception e) {
      e.printStackTrace();
      UserInfo userInfo_exception = new UserInfo(-2);
      userInfo_exception.setName(e.getMessage());
      return userInfo_exception;
    }
  }
  public static List<UserInfo> allUserInfos(){
    try {
      URL url = new URL(url_rpc+"/3_all_userinfos.jsp");
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
      ucon.setRequestMethod("GET");
      ucon.setDoInput(true);
      ucon.setConnectTimeout(TIMEOUT);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");

      ucon.connect();

      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      UserInfo[] userArray = gson.fromJson(in, UserInfo[].class);
      List<UserInfo> users = new ArrayList<UserInfo>();
      users.addAll(Arrays.asList(userArray));

      in.close();
      ucon.getInputStream().close();

      return users;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  public static boolean postMessage(Message message) {
    try {
      URL url = new URL(url_rpc+"/4_post_message.jsp");
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
      ucon.setRequestMethod("POST");
      ucon.setDoInput(true);
      ucon.setDoOutput(true);
      ucon.setConnectTimeout(TIMEOUT);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");

      PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
      out.println(gson.toJson(message));

      ucon.connect();

      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      String line;
      System.out.println("reply:");
      while ((line = in.readLine()) != null) {
        System.out.println(line);
      }

      in.close();
      out.close();
      ucon.getInputStream().close();

      return true;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  public static List<Message> retrieveMessages(int from, int to) {
    try {
      URL url = new URL(url_rpc+"/5_retrieve_messages.jsp?id_1="+from+"&id_2="+to);
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
      ucon.setRequestMethod("GET");
      ucon.setDoInput(true);
      ucon.setConnectTimeout(TIMEOUT);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");

      ucon.connect();

      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      Message[] messageArray = gson.fromJson(in, Message[].class);
      List<Message> messages = new ArrayList<Message>();
      messages.addAll(Arrays.asList(messageArray));

      in.close();
      ucon.getInputStream().close();

      return messages;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  public static List<Message> retrieveNewMessages(int from, int to, Message message) {
    try {
      URL url = new URL(url_rpc+"/6_retrieve_new_messages.jsp?id_1="+from+"&id_2="+to);
      HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
      ucon.setRequestMethod("POST");
      ucon.setDoInput(true);
      ucon.setDoOutput(true);
      ucon.setConnectTimeout(TIMEOUT);
      ucon.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      ucon.setRequestProperty("Accept", "application/json; charset=utf-8");

      PrintWriter out = new PrintWriter(ucon.getOutputStream(), true);
      out.println(gson.toJson(message));

      ucon.connect();

      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      Message[] messageArray = gson.fromJson(in, Message[].class);
      List<Message> messages = new ArrayList<Message>();
      messages.addAll(Arrays.asList(messageArray));

      in.close();
      out.close();
      ucon.getInputStream().close();

      return messages;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
