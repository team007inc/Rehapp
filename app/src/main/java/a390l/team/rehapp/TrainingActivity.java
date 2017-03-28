package a390l.team.rehapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import zephyr.android.HxMBT.*;

import static java.lang.System.currentTimeMillis;

public class TrainingActivity extends AppCompatActivity{

    public Recorder myDatarecorder = new Recorder();
    LineGraphSeries<DataPoint> LGSh = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> LGSs = new LineGraphSeries<DataPoint>();

    BluetoothAdapter adapter = null;
    BTClient _bt;
    ZephyrProtocol _protocol;
    NewConnectedListener _NConnListener;
    private final int HEART_RATE = 0x100;
    private final int INSTANT_SPEED = 0x101;
    boolean Clicked = false;
    long t0 = 0;
    int HR;
    static boolean initialized = false;

    public Button mSave;
    public String path = Environment.DIRECTORY_DOCUMENTS + "/HXMAPP";
    //getInternalStorageDirectory().getAbsolutePath() + "/HXMAPP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_training);

        /*Saving Features Initialization*/ /* INCOMPLETE*/
        mSave = (Button)findViewById(R.id.mSave);
        File dir = new File(path);
        dir.mkdirs();
        /*/

         */
        /* Graph initialization*/
        GraphView sGraph = (GraphView)findViewById(R.id.sGraph);
        sGraph.getViewport().setYAxisBoundsManual(true);
        sGraph.getViewport().setMinY(0);
        sGraph.getViewport().setMaxY(5);

        GraphView mGraph = (GraphView) findViewById(R.id.mGraph);
        mGraph.getViewport().setYAxisBoundsManual(true);
        mGraph.getViewport().setMinY(50);
        mGraph.getViewport().setMaxY(160);

        /*****************/
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);
        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
        this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);

        TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
        tv.setVisibility(View.VISIBLE);

        mSave.setOnClickListener(new View.OnClickListener() { //NonFunctional Saving features, to be done.
            @Override
            public void onClick(View view)
            {
/*
                String filename = "HXM";

                String  saveText = "Hi";
                //saveText[0] = String.valueOf(myDatarecorder.getHeartrate()[1]);
                //saveText[1] = String.valueOf(myDatarecorder.getHeartrate()[10]);
                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(saveText.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
*/
            }

        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.training, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mConnect) {
            String BhMacID = "00:07:80:0E:B1:E2";
            adapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
            if (pairedDevices.size() > 0)
            {
                for (BluetoothDevice device : pairedDevices)
                {
                    if (device.getName().startsWith("HXM"))
                    {
                        BluetoothDevice btDevice = device;
                        BhMacID = btDevice.getAddress();
                        break;
                    }
                }
            }

            BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
            String DeviceName = Device.getName();
            _bt = new BTClient(adapter, BhMacID);
            _NConnListener = new NewConnectedListener(Newhandler,Newhandler);
            _bt.addConnectedEventListener(_NConnListener);

            TextView tv1 = (TextView) findViewById(R.id.labelHeartRate);
            tv1.setText("0");
            tv1 = (TextView)findViewById(R.id.labelInstantSpeed);
            tv1.setText("0.0");

            if(_bt.IsConnected())
            {
                _bt.start();
                TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                tv.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Unable to Connect", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (id == R.id.mDisconnect) {
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
            _bt.removeConnectedEventListener(_NConnListener);
            _bt.Close();
            TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
            tv.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class BTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BTIntent", intent.getAction());
            Bundle b = intent.getExtras();
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
            try {
                BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", new Class[] {String.class} );
                byte[] pin = (byte[])m.invoke(device, "1234");
                m = device.getClass().getMethod("setPin", new Class [] {pin.getClass()});
                Object result = m.invoke(device, pin);
                Log.d("BTTest", result.toString());
            } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    final Handler Newhandler = new Handler(){
        public void handleMessage(Message msg)
        {
            TextView tv;
            switch (msg.what) {
                case HEART_RATE:
                    if (!initialized) {
                        t0 = currentTimeMillis();
                        initialized = true;
                    }

                    String HeartRatetext = msg.getData().getString("HeartRate");
                    HR = Integer.parseInt(HeartRatetext);
                    long thcurr = System.currentTimeMillis();
                    double tdh = (thcurr - t0) / 1000;
                    myDatarecorder.AddHRDatapoint(tdh, HR);

                    GraphView mGraph = (GraphView) findViewById(R.id.mGraph);
/*
                    for(int i = 0;i<10;i++)
                    {
                        Double tt = myDatarecorder.getHRTime()[myDatarecorder.getHRTime().length - 10 + i];
                        Integer HHRR = myDatarecorder.getHeartrate()[myDatarecorder.getHeartrate().length -10 + 1];
                        LGS.appendData(new DataPoint(tt,HHRR),true,myDatarecorder.getHRTime().length);
                    }*/

                    LGSh.appendData(new DataPoint(tdh, HR), true, myDatarecorder.getHRTime().length);
                    mGraph.addSeries(LGSh);

                    tv = (TextView) findViewById(R.id.labelHeartRate);
                    System.out.println("Heart Rate Info is " + HeartRatetext);
                    if (tv != null) tv.setText(HeartRatetext);

                    //Post 30 second graph follower
                    if((int)tdh>30) {
                        mGraph.getViewport().setXAxisBoundsManual(true);
                        mGraph.getViewport().setMinX((int) tdh - 30);
                        mGraph.getViewport().setMaxX((int) tdh);
                    }
                    break;

                case INSTANT_SPEED:
                    //get the time at which the training began
                    if(!initialized)
                    {
                        t0=currentTimeMillis();
                        initialized = true;
                    }

                    String InstantSpeedtext = msg.getData().getString("InstantSpeed");
                    Double Speed = Double.parseDouble(InstantSpeedtext);

                    long tscurr = System.currentTimeMillis();
                    double tds = (tscurr - t0)/1000;
                    myDatarecorder.AddSDatapoint(tds,Speed);

                    GraphView sGraph = (GraphView)findViewById(R.id.sGraph);
                    LGSs.appendData(new DataPoint(tds,Speed),true,100);
                    sGraph.addSeries(LGSs);

                    //Post 30 second graph follower
                    if((int)tds>30) {
                        sGraph.getViewport().setXAxisBoundsManual(true);
                        sGraph.getViewport().setMinX((int) tds - 30);
                        sGraph.getViewport().setMaxX((int) tds);
                    }

                    //Display Rounded value of speed. Simply to not make it extend and go over other texts.
                    double roundedSpeed = Math.round(Speed* 100.0) / 100.0;
                    tv = (TextView) findViewById(R.id.labelInstantSpeed);
                    if (tv != null)tv.setText(String.valueOf(roundedSpeed));

                    //Display Rounded value of distance. Simply to not make it extend and go over other texts.
                    double DIST = Math.round(myDatarecorder.getDistance()* 10.0) / 10.0;
                    //int distance = Integer.parseInt(String.valueOf(myDatarecorder.getDistance()));
                    String Sdist = String.valueOf(DIST);
                    tv = (TextView) findViewById(R.id.mDist);
                    if (tv != null)tv.setText(Sdist);

                    break;
            }
        }

    };

    private class BTBondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("Bond state", "BOND_STATED = " + device.getBondState());
        }
    }
}
