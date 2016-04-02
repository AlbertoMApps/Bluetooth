package development.alberto.com.mybluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity  {

    Button b1,b2,b3,b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
    private static final int REQUEST_BLUETOOTH = 0;
    private static final int DISCOVER_DURATION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);
        b4=(Button)findViewById(R.id.button4);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);
    }

    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, REQUEST_BLUETOOTH);
            Toast.makeText(getApplicationContext(),"Turned on",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(),"Turned off" ,Toast.LENGTH_LONG).show();
    }

    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(getVisible, REQUEST_BLUETOOTH);
    }

    public void list(View v){
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());
        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLUETOOTH) {
            Intent i = new Intent ();
            i.setAction(Intent.ACTION_SEND);
            i.setType("text/plain");
//            String uri = "/mnt/sdcard/test.txt";
//            File f = new File(uri);
            String base = Environment.getExternalStorageDirectory().getAbsolutePath();
            File f = new File(base, "pc.txt");
            FileWriter escribir= null;
            try {
                escribir = new FileWriter(f,true);
                escribir.write("saludo");
                escribir.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
            PackageManager pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
            if(list.size() >0){
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info :list){
                    packageName = info.activityInfo.packageName;
                    if(packageName.equals("com.android.bluetooth")){
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Toast.makeText(getApplicationContext()," Bluethooth havent been found " ,Toast.LENGTH_LONG).show();
                } else {
                    i.setClassName(packageName, className);
                    startActivity(i);
                }
            } else {
                Toast.makeText(getApplicationContext()," Bluethooth is cancelled " ,Toast.LENGTH_LONG).show();
            }

        }
    }
}
