package wifidirect.wifidirect;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import wifidirect.wifidirect.ChatMultiThread.Client;
import wifidirect.wifidirect.ChatMultiThread.Server;
import wifidirect.wifidirect.Message.MessageAdapter;

public class ChatActivity extends AppCompatActivity {
    public static final String TAG = "MyApp: ChatActivity: ";
    public static final int MESSAGE_READ = 1;

    Server server = null;
    Client client = null;
    EditText writeMsg;
    ImageButton btnSend;
    static MessageAdapter messageAdapter;
    static ListView messagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Initialize();
        StartServerOrClient();
    }

    /**
     * TODO : JavaDoc
     */
    public void Initialize() {
        writeMsg = findViewById(R.id.writeMsg);
        btnSend = findViewById(R.id.btnSend);

        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
    }

    /**
     * TODO : JavaDoc
     */
    public void StartServerOrClient() {
        // ToDo: with just one line, change between Multithread and Async
        Intent intent = getIntent();
        String isGo = intent.getStringExtra("isGo");
        Log.d(TAG,isGo);

        if (isGo.equals("true")) {
            //ChatMultithread
            server = new Server();
            server.Accept();

            //ChatAsync
            //wifidirect.wifidirect.ChatAsync.Server server = new wifidirect.wifidirect.ChatAsync.Server();
            //server.Start();
        } else if (isGo.equals("false")) {
            //ChatMultithread
            String HostAddr = intent.getStringExtra("ipAddress");
            client = new Client(HostAddr);
            client.Connect();

            //ChatAsync
            //wifidirect.wifidirect.ChatAsync.Client client = new wifidirect.wifidirect.ChatAsync.Client();
            //client.Start();
        }
    }

    /**
     * TODO : JavaDoc
     * @param view
     */
    public void Send(View view) {
        String msg = writeMsg.getText().toString();
        writeMsg.getText().clear();

        if (server != null) {
            server.Send(msg);
        }
        if (client != null) {
            client.Send(msg);
        }

        //ToDo: put this piece of code in seperate function
        wifidirect.wifidirect.Message.Message message = new wifidirect.wifidirect.Message.Message(msg, true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdapter.add(message);
                messagesView.setSelection(messagesView.getCount() - 1);
            }
        });
    }

    /**
     * TODO : JavaDoc
     */
    static public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMSg = new String(readBuff, 0, msg.arg1);
                    Log.d(TAG, tempMSg);
                    wifidirect.wifidirect.Message.Message message = new wifidirect.wifidirect.Message.Message(tempMSg, false);

                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);

                    break;
            }
            return true;
        }
    });
}
