package edu.upc.whatsapp;

import edu.upc.whatsapp.comms.RPC;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

import edu.upc.whatsapp.service.PushService;
import entity.User;
import entity.UserInfo;

public class b_LoginActivity extends Activity implements View.OnClickListener {

  _GlobalState globalState;
  ProgressDialog progressDialog;
  User user;
  OperationPerformer operationPerformer;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    globalState = (_GlobalState)getApplication();
    setContentView(R.layout.b_login);
    ((Button) findViewById(R.id.editloginButton)).setOnClickListener(this);
  }

  public void onClick(View arg0) {
    if (arg0 == findViewById(R.id.editloginButton)) {

        //...
        user = new User();
        user.setLogin((findViewById(R.id.EditLoginLoginEditText).toString()));
        user.setPassword((findViewById(R.id.EditLoginPasswordEditText).toString()));

        progressDialog = ProgressDialog.show(this, "LoginActivity", "Logging into the server...");
        // if there's still a running thread doing something, we don't create a new one
        if (operationPerformer == null) {
          operationPerformer = new OperationPerformer();
          operationPerformer.start();
        }
    }
  }

  private class OperationPerformer extends Thread {

    @Override
    public void run() {
      Message msg = handler.obtainMessage();
      Bundle b = new Bundle();

      //...
      UserInfo user_reply = RPC.login(user);
      b.putSerializable("user_reply", user_reply);

      msg.setData(b);
      handler.sendMessage(msg);
    }
  }

  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {

      operationPerformer = null;
      progressDialog.dismiss();

      UserInfo userInfo = (UserInfo) msg.getData().getSerializable("userInfo");

      if (userInfo.getId() >= 0) {
        toastShow("Login successful");

        //...

        globalState.my_user = userInfo;
        globalState.save_my_user();
        startService(new Intent(b_LoginActivity.this,PushService.class));
        startService(new Intent(b_LoginActivity.this, LocationManager.class));
        //startActivity(new Intent(b_LoginActivity.this, d_WorkOrdersActivity_1.class));

        finish();
      }
      else if (userInfo.getId() == -1){
        toastShow("Login unsuccessful, try again please.");
      }
      else if (userInfo.getId() == -2){
        toastShow("Not logged in, connection problem due to: " + userInfo.getName());
      }

    }
  };

  private void toastShow(String text) {
    Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
    toast.setGravity(0, 0, 200);
    toast.show();
  }
}
