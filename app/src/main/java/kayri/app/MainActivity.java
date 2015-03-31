package kayri.app;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends ActionBarActivity {


    private Socket socket;
    private Switch switch1;
    private TextView switchStatus;
    private Handler myHandler;

    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            socket.emit("test");
            myHandler.postDelayed(this, 60000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;

            socket = IO.socket("http://192.168.1.65:1234",opts);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socket.emit("who", "Android");
                    socket.emit("test");
                }

            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.d("Socket", "Error connection");
                }

            }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.d("Socket", "Time out connection");
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.disconnect();
                }

            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        switchStatus = (TextView) findViewById(R.id.switchStatus);
        switch1 = (Switch) findViewById(R.id.switch1);

        switch1.setChecked(false);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switchStatus.setText("Switch ON");
                    myHandler = new Handler();
                    myHandler.postDelayed(myRunnable, 60000);
                }else{
                    switchStatus.setText("Switch OFF");
                    if(myHandler !=null){
                        myHandler.removeCallbacks(myRunnable);
                    }
                }
            }
        });

        if(switch1.isChecked()){
            switchStatus.setText("Switch On");
            socket.emit("test");

        }else{
            switchStatus.setText("Switch OFF");
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}