package com.example.appclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int HANDLER_FLAG=0;
    private int randomValue;
    private boolean isBound;
    private Messenger requestMessanger,receiveMessanger;


    Button startService;
    Button stopService;
    Button displayNumbers;
    TextView textView;
    Intent serviceIntent;


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
             isBound=true;
            requestMessanger =new Messenger(new ClientHandler());
            receiveMessanger = new Messenger(iBinder);


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound=false;
            requestMessanger=null;
            receiveMessanger=null;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startService= (Button)findViewById(R.id.startService);
        stopService= (Button)findViewById(R.id.stopService);
        displayNumbers= (Button)findViewById(R.id.show_numbers);
        textView= (TextView) findViewById(R.id.randomdisplay);


        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);
         displayNumbers.setOnClickListener(this);
        serviceIntent=new Intent();

        ComponentName component =new ComponentName("com.example.app_service","com.example.app_service.MainActivity");
        serviceIntent.setComponent(component);
    }

    @Override
    public void onClick(View view) {


        switch(view.getId()){

            case R.id.startService :{
                isBound=true;
                bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);

                Toast.makeText(this,"Services is bound",Toast.LENGTH_SHORT).show();
             } break;

            case R.id.stopService: {
                isBound=false;
                unbindService(serviceConnection);

                Toast.makeText(this,"Services is InBound",Toast.LENGTH_SHORT).show();
            } break;

//            case R.id.randomdisplay: {
//                isBound=false;
//                fetchRandomNumbers();
//                Toast.makeText(this,"Services is InBound",Toast.LENGTH_SHORT).show();
//            } break;

            case R.id.show_numbers: {
                fetchRandomNumbers();
                Toast.makeText(this,"Services is Obtaining a number ",Toast.LENGTH_SHORT).show();
            } break;
        }

    }


    private  void fetchRandomNumbers(){

        if(isBound){

            Message message = Message.obtain();

            message.replyTo = receiveMessanger;

            try {
               requestMessanger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    class ClientHandler extends Handler {


        @Override
        public void handleMessage(@NonNull Message msg) {

            switch (msg.what){

                case  HANDLER_FLAG: {

                   randomValue= msg.arg1;
                    textView.setText("Random Number " + randomValue);
                }
            }

            super.handleMessage(msg);
        }
    }


}