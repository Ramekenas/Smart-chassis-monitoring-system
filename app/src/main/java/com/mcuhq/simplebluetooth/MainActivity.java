package com.mcuhq.simplebluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    public static int i = 0;
    public static BarChart barCh;
    public static BarData barData;
    public static BarDataSet barDataSet;
    public static ArrayList barEntries;

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private ListView mDevicesListView;
    private CheckBox mLED1;

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothStatus = (TextView)findViewById(R.id.bluetooth_status);
        mReadBuffer = (TextView) findViewById(R.id.read_buffer);
        mScanBtn = (Button)findViewById(R.id.scan);
        mOffBtn = (Button)findViewById(R.id.off);
        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button)findViewById(R.id.paired_btn);
        mLED1 = (CheckBox)findViewById(R.id.checkbox_led_1);
        barCh = (BarChart) findViewById(R.id.barChart);



        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);


        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);



		//testforgit
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //Log.d("TESTOUT", readMessage);
                    char ch;
                    int[] temp= new int[6];
                    for(int j=0; j<6;j++)
                    {
                        temp[j]=-1;
                    }
                    int l=0;
                    int k=0;
                    int g=0;
                    int count=0;
                    int [] data = new int[25600];
                    for(int j=0; j<2048;j++)
                    {

                            ch=readMessage.charAt(j);
                           // Log.d("beforeif", String.valueOf(ch));
                            if(ch=='0'||ch=='1'||ch=='2'||ch=='3'||ch=='4'||ch=='5'||ch=='6'||ch=='7'||ch=='8'||ch=='9')
                            {
                                Log.d("inif", String.valueOf(ch));
                                temp[l] = Character.getNumericValue(ch); //.hz
                                Log.d("inif1", Integer.toString(temp[l]));
                                l++;
                            }
                            else if(ch==';')
                            {
                                //Log.d("inifelse", String.valueOf(ch));
                                for(int b=0; b<6;b++)
                                {
                                    Log.d("inifelsefor0", Integer.toString(temp[b]));
                                    Log.d("inifelsefor1", Integer.toString(b));
                                    if(temp[b]>=0)
                                    {
                                        Log.d("inifelsefor11", Integer.toString(b));
                                        g++;
                                    }

                                }
                                for(int b=0; b<g+2;b++)
                                {
                                        data[count]=data[count]+(int)(temp[b]*Math.pow(10, g-1));
                                        g--;

                                }
                                //Log.d("inifelsefor2", Integer.toString(data[count]));
                                count++;
                                l=0;
                                for(int y=0; y<6;y++)
                                {
                                    temp[y]=-1;
                                }
                            }
                            else
                                continue;


                            g=0;

                    }
                    /*
                    for(int j=0; j<60;j++)
                    {
                       Log.d("afterif", Integer.toString(data[j]));
                    }
                    */

                    //String [] parts = new String[25600];
                    //Arrays.fill(parts, null);
                    //parts = readMessage.split(";",0);
                    //Log.d("CONTENTS", readMessage);


                    int x=0;
                    int y=0;
                    //algortimas isskirt i atskirus masyvus info----------------------------------------




                        barEntries = new ArrayList<>();
                        for(int j=0; j < count+1; j+=2)
                        {
                            x=data[j];
                            y=data[j+1];
                            barEntries.add(new BarEntry(x,y));
                        }

                        barDataSet = new BarDataSet(barEntries, "Ratų vibracijų spektras");
                        barData = new BarData(barDataSet);

                        barCh.setData(barData);
                        barCh.invalidate();
                        //barCh.notifyDataSetChanged();
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        //barDataSet.setColor(50);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barDataSet.setValueTextSize(8f);





                    mReadBuffer.setText(readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Prisijungta prie įrenginio: " + msg.obj);
                    else
                        mBluetoothStatus.setText("Prisijungti nepavyko");
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Būsena: neįjungtas Bluetooth'as");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {

            mLED1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write("1");
                }
            });


            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn();
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff();
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices();
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover();
                }
            });
        }
    }

    private void bluetoothOn(){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth'as įjungtas",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth'as jau įjungtas", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Įjungtas");
            }
            else
                mBluetoothStatus.setText("Išjungtas");
        }
    }

    private void bluetoothOff(){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth'as išjungtas");
        Toast.makeText(getApplicationContext(),"Bluetooth'as išjungtas", Toast.LENGTH_SHORT).show();
    }

    private void discover(){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Paieška išjungta",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Paieška  pradėta", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth'as neįjungtas'", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(){
        mBTArrayAdapter.clear();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Parodyti prijungtus įtaisus", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth'as neįjungtas", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth'as neįjungtas", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Jungiamasi...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Sukurti jungties nepavyko", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Sukurti jungties nepavyko", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!fail) {
                        mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Nepavyko sukurit nesaugaus ryšio",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}