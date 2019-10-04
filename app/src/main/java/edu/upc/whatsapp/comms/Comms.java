package edu.upc.whatsapp.comms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * Created by juanluis.
 */

public interface Comms {
  String WhatsApp_server = "10.0.2.2:8080/WhatsAppServer_without_ddbb";
  String url_rpc = "http://"+WhatsApp_server+"/rpc";
  String ENDPOINT = "ws://"+WhatsApp_server+"/push";
//  Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss+01:00").create();
  Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializerDeserializer()).create();
}
