package edu.upc.whatsapp.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import edu.upc.whatsapp.R;
import edu.upc.whatsapp._GlobalState;
import edu.upc.whatsapp.e_MessagesActivity_3_broadcast_receiver;
import edu.upc.whatsapp.e_MessagesActivity_4_broadcast_and_persistence;
import entity.Message;

import static edu.upc.whatsapp.comms.Comms.ENDPOINT;
import static edu.upc.whatsapp.comms.Comms.gson;

public class PushService extends Service {

  private _GlobalState globalState;
  private Timer timer;

  private String json_userInfo_current_session;
  private boolean reconnect_requested;

  @Override
  public void onCreate() {
    super.onCreate();
    globalState = (_GlobalState) getApplication();
    toastShow("PushService created");
    timer = new Timer();
    timer.scheduleAtFixedRate(new MyTimerTask(), 0, 120000);

    json_userInfo_current_session = "";
    reconnect_requested = false;
  }
  
  private class MyTimerTask extends TimerTask {
    public void run() {
      //esto pasa cuando Android destruye/recrea el servicio
      //de forma automatica, por ejemplo al parar la aplicacion con el
      //servicio arrancado, entonces se intentaria abrir un WebSocket
      //de nuevo sin un usuario identificado: login/password.
      if(globalState.my_user==null){
        return;
      }
      ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isConnected() && !connectedToServer) {
        sendMessageToHandler("open","trying to connect to server!!!");
        connectToServer();
      }
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flag, int startId) {
    super.onStartCommand(intent, flag, startId);
//    toastShow("PushService started");

    //aqui no hay nada que hacer, ya lo hace todo el timer,
    //salvo si el servicio ya estaba iniciado:
    String json_my_user = gson.toJson(globalState.my_user);
    if(!json_userInfo_current_session.equals("") &&
       !json_userInfo_current_session.equals(json_my_user)){
      reconnect_requested = true;
      new Thread(new Runnable() {
        public void run() {
          disconnectFromServer();
        }
      }).start();
    }
    
    return Service.START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    toastShow("PushService destroyed");
    if(timer!=null)
      timer.cancel();
    new Thread(new Runnable() {
      public void run() {
        disconnectFromServer();
      }
    }).start();
  }

  private final IBinder myBinder = new MyBinder();
  public class MyBinder extends Binder {
    PushService getService() {
      return PushService.this;
    }
  }
  @Override
  public IBinder onBind(Intent intent) {
    toastShow("PushService bound");
    return myBinder;
  }

  private boolean connectedToServer;
  private Session session;

  private void connectToServer(){
    try {
      ClientManager client = ClientManager.createClient();
      client.connectToServer(new PushService.MyEndPoint(),
          ClientEndpointConfig.Builder.create().build(),
          URI.create(ENDPOINT));
    }
    catch (Exception e) {
      e.printStackTrace();
      sendMessageToHandler("error","connectToServer error");
      connectedToServer = false;
      session = null;
    }
  }
  private void disconnectFromServer(){
    if(session!=null){
      try {
        session.close();
      } catch (IOException e) {
        e.printStackTrace();
        sendMessageToHandler("error","disconnectFromServer error");
      }
    }
  }
  //this is executed by an independent thread:
  public class MyEndPoint extends Endpoint {

    @Override
    public void onOpen(Session session, EndpointConfig EndpointConfig) {
      try {

        //...

      }
      catch (Exception e) {
        e.printStackTrace();
        sendMessageToHandler("error","onOpen error: "+e.getMessage());
      }
    }

    @Override
    public void onError(Session session, Throwable t) {
      t.printStackTrace();
      sendMessageToHandler("error","onError error: "+t.getMessage());
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
      sendMessageToHandler("close","connection closed");
      connectedToServer = false;
      PushService.this.session = null;
      json_userInfo_current_session = "";
      if(reconnect_requested){
        connectToServer();
      }
    }
  }

  private void sendMessageToHandler(String type, String content){
    android.os.Message msg = handler.obtainMessage();
    Bundle bundle = new Bundle();
    bundle.putCharSequence("type", type);
    bundle.putCharSequence("content", content);
    msg.setData(bundle);
    handler.sendMessage(msg);
  }

  Handler handler = new Handler() {
    @Override
    public void handleMessage(android.os.Message msg) {
      String type = msg.getData().getCharSequence("type").toString();
      String content = msg.getData().getCharSequence("content").toString();
      if(type.equals("message")){

        //...

      }
      else{
        toastShow(content);
      }
    }
  };
  
  private void sendPushNotification(Context context, String content, String json_msg){

    Intent mIntent = new Intent(context, e_MessagesActivity_3_broadcast_receiver.class);
    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //con esto MessagesActivity obtiene con quien estoy hablando
    //al entrar en dicha pantalla:
    mIntent.putExtra("message", json_msg);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);

    Notification.Builder mBuilder = new Notification.Builder(context)
      .setContentTitle("_WhatsApp")
      .setContentText(content)
      .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_logo))
      .setContentIntent(pendingIntent)
      .setContentInfo("1")
      .setSmallIcon(R.drawable.app_logo)
      .setAutoCancel(true);
    
    Notification notification = mBuilder.build();
    notification.defaults |= Notification.DEFAULT_SOUND;
    
    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify(2, notification);
  }

  private void toastShow(String text) {
    Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
    toast.setGravity(0, 0, 200);
    toast.show();
  }
 
}
