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

public class c_RegistrationActivity extends Activity implements View.OnClickListener {

  _GlobalState globalState;
  ProgressDialog progressDialog;
  User user;
  OperationPerformer operationPerformer;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    globalState = (_GlobalState)getApplication();
    setContentView(R.layout.c_registration);
    ((Button) findViewById(R.id.editregistrationButton)).setOnClickListener(this);
  }

  public void onClick(View arg0) {
    if (arg0 == findViewById(R.id.editregistrationButton)) {

      //...
      user = new User();
      user.setLogin((findViewById(R.id.EditLoginLoginEditText).toString()));
      user.setPassword((findViewById(R.id.EditLoginPasswordEditText).toString()));
      user.setEmail((findViewById(R.id.EditLoginLoginEditText).toString()));

      UserInfo user_info = new UserInfo();
      user_info.setSurname((findViewById(R.id.RegistrationSurnameText).toString()));
      user_info.setName((findViewById(R.id.RegistrationNameText).toString()));

      user.setUserInfo(user_info);


      progressDialog = ProgressDialog.show(this, "RegistrationActivity", "Registering for service...");
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
      UserInfo user_reply = RPC.registration(user);
      b.putSerializable("operator_reply", user_reply);

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
        toastShow("Registration successful");

        //...
        globalState.my_user = userInfo;
        globalState.save_my_user();
        startService(new Intent(c_RegistrationActivity.this, PushService.class));
        startService(new Intent(c_RegistrationActivity.this, LocationManager.class));
        //startActivity(new Intent(c_RegistrationActivity.this, d_WorkOrdersActivity_1.class));

        finish();
      }
      else if (userInfo.getId() == -1) {
        toastShow("Registration unsuccessful,\nlogin already used by another user");
      }
      else if (userInfo.getId() == -2) {
        toastShow("Not registered, connection problem due to: " + userInfo.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("error!!!");
        System.out.println(userInfo.getName());
        System.out.println("--------------------------------------------------");
      }
    }
  };

  private void toastShow(String text) {
    Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
    toast.setGravity(0, 0, 200);
    toast.show();
  }
}
