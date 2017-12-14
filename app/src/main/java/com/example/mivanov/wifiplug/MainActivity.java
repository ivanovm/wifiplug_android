package com.example.mivanov.wifiplug;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar mTopToolbar;
    private List<RowItem> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
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

        if (id == R.id.action_favorite) {
            rowItems = new ArrayList<RowItem>();
            ListView listView;

            // TODO: scan the network for available plugs
            for (int i = 0; i < 2; i++) {
                RowItem rowItem = new RowItem("000123", "127.0.0.1", false);
                rowItems.add(rowItem);
            }

            listView = (ListView)findViewById(R.id.listView);
            CustomAdapter adapter = new CustomAdapter(this, rowItems);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(this);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = (ListView)findViewById(R.id.listView);
        RowItem rowItem = (RowItem)((CustomAdapter)listView.getAdapter()).getItem(position);
        // TODO: change state of plug and update the status
//        ((CustomAdapter.ViewHolder) view.getTag()).plug_id.setText("");
//        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
    }

    public String getWifiIpAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wi = wm.getConnectionInfo();

        int ipAddress = wi.getIpAddress();
        ipAddress = (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) ?
                Integer.reverseBytes(ipAddress) : ipAddress;

        byte[] ipAddressParts = BigInteger.valueOf(ipAddress).toByteArray();
        try {
            InetAddress myAddress = InetAddress.getByAddress(ipAddressParts);
            String hostAddress = myAddress.getHostAddress();
            return hostAddress;
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }
}
