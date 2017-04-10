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

import static java.lang.Math.abs;
import static java.lang.System.currentTimeMillis;

public class TrainingActivity extends AppCompatActivity{

    public Recorder myDatarecorder = new Recorder();
    LineGraphSeries<DataPoint> LGSh = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> LGSs = new LineGraphSeries<DataPoint>();



    public Client myClient;


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
    double calories  = 0;
    double DIST = 0;
    double Speed = 0;
    double lastSpeed =0;
    double lastSpeedTime =0;
    boolean _30SecPass = false;
    boolean isConnected = false;
    //double oldTime, currTime;
    //int newHR, oldHR, currHR;
    int HR30;
    int Age = 0;
    int Mass = 0;
    public Button mSave;
    public String path = Environment.DIRECTORY_DOCUMENTS + "/HXMAPP";
    //getInternalStorageDirectory().getAbsolutePath() + "/HXMAPP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_training);
        Age = (int) getIntent().getExtras().getInt("Age");
        Mass = (int) getIntent().getExtras().getInt("Mass");
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
/*              // Not used anymore
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
                if(isConnected)
                {
                    _bt.removeConnectedEventListener(_NConnListener);
                    _bt.Close();
                    TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                    tv.setVisibility(View.VISIBLE);
                    isConnected = false;
                }
                Intent ii = new Intent();
                ii.putExtra("CaloriesReturn", (int)calories);
                ii.putExtra("DistanceReturn", DIST);
                if(calories==0) setResult(RESULT_CANCELED,ii);
                else setResult(RESULT_OK,ii);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                finish();

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
            if(!isConnected) {
                String BhMacID = "00:07:80:0E:B1:E2";
                adapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().startsWith("HXM")) {
                            BluetoothDevice btDevice = device;
                            BhMacID = btDevice.getAddress();
                            break;
                        }
                    }
                }

                BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
                String DeviceName = Device.getName();
                _bt = new BTClient(adapter, BhMacID);
                _NConnListener = new NewConnectedListener(Newhandler, Newhandler);
                _bt.addConnectedEventListener(_NConnListener);

                TextView tv1 = (TextView) findViewById(R.id.labelHeartRate);
                tv1.setText("0");
                tv1 = (TextView) findViewById(R.id.labelInstantSpeed);
                tv1.setText("0.0");

                if (_bt.IsConnected()) {
                    _bt.start();
                    TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                    tv.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    isConnected = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to Connect", Toast.LENGTH_LONG).show();
                }
                return true;
            }
            else Toast.makeText(getApplicationContext(), "Already Connected", Toast.LENGTH_LONG).show();

        }
        if (id == R.id.mDisconnect) {
            if(isConnected)
            {
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
                _bt.removeConnectedEventListener(_NConnListener);
                _bt.Close();
                TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                tv.setVisibility(View.VISIBLE);
                isConnected = false;
                return true;
            }
            else Toast.makeText(getApplicationContext(), "Cannot Disconnect if it is not Connected",
                    Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(isConnected)
        {
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
            _bt.removeConnectedEventListener(_NConnListener);
            _bt.Close();
            TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
            tv.setVisibility(View.VISIBLE);
            isConnected = false;
        }
        else
        {
            TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
            tv.setVisibility(View.VISIBLE);
            isConnected = false;
        }
        finish();
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

                    double thcurr = System.currentTimeMillis();
                    double tdh = (thcurr - t0) / 1000;
                    double roundedTime = Math.round(thcurr* 1000.0) / 1000.0;

                    double td = thcurr - t0;
                    td = Math.round(td);
                    double tdsec = Math.round(td* 1000.0) / 1000000.0;
                    if(tdsec%30>5) _30SecPass = true;

                    String HeartRatetext = msg.getData().getString("HeartRate");
                    HR = Integer.parseInt(HeartRatetext);

                    myDatarecorder.AddHRDatapoint(tdh, HR);



                    GraphView mGraph = (GraphView) findViewById(R.id.mGraph);

                    if((tdsec%30<5)&&tdsec>30&&_30SecPass)
                    {
                        _30SecPass = false;
                        double last30Sec =0;
                        int lasttime =0;
                        boolean out= false;
                        for(int i =0; i<myDatarecorder.getHeartrate().length; i++)
                        {
                            if(td - myDatarecorder.getHRTime()[myDatarecorder.getHeartrate().length -i-1] >=30)
                            {
                                lasttime = myDatarecorder.getHeartrate().length -i -1;
                                last30Sec = myDatarecorder.getHRTime()[myDatarecorder.getHeartrate().length -i -1];
                                out = true;
                                break;
                            }
                        }
                        HR30 = 0;
                        for(int i =0; i<lasttime; i++)
                        {
                            HR30 = HR30 + myDatarecorder.getHeartrate()[myDatarecorder.getHeartrate().length-i-1];
                        }
                        HR30 = (int)((double)HR30/last30Sec);

                        calories = ((-55.0969 + 0.6309*HR30  + 0.1988*Mass + 0.2017*Age)/(2*4.184)) + calories;

                        tv = (TextView) findViewById(R.id.mCalories);
                        tv.setText(String.valueOf(Math.round(calories*100)/100));
                    }

                    //newHR = (oldHR*oldTime + currHR)/currTime



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

                    //Post 10 second graph follower
                    if((int)tdh>10) {
                        mGraph.getViewport().setXAxisBoundsManual(true);
                        mGraph.getViewport().setMinX((int) tdh - 10);
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
                    Speed = Double.parseDouble(InstantSpeedtext);

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


                    //The two conditions at which the distance must be calculated when the speed is increasing or decreasing using simple mathematic formula as if it was linear.
                    if(lastSpeedTime<Speed)
                    {
                        DIST=DIST + (Speed-lastSpeed)*abs(tds - lastSpeedTime)*0.5 + lastSpeed*abs(tds - lastSpeedTime);
                    }
                    else
                    {
                        DIST=DIST + (lastSpeed-Speed)*abs(tds - lastSpeedTime)*0.5 + Speed*abs(tds - lastSpeedTime);
                    }

                    // Once the function of distance calculation is complete, update the values for the next round.
                    lastSpeedTime = tds;
                    lastSpeed = Speed;

                    //Display Rounded value of distance. Simply to not make it extend and go over other texts.
                    String Sdist = String.valueOf(Math.round(DIST* 100.0) / 100.0);
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
