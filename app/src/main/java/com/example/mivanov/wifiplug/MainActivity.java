package com.example.mivanov.wifiplug;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
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
            ArrayList<String> ips = getWifiIpAddresses();
            for (String ip : ips) {
                Log.d("STATE", "Trying ip: " + ip);
                try {
//                    JSONObject state = getState(ip);
                    JSONObject state = getState("192.168.1.110");
                    if (state != null) {
                        Log.d("STATE", "Found a live http server!");
                        RowItem rowItem = new RowItem(state.getString("id"), ip, state.getInt("state") == 1);
                        rowItems.add(rowItem);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
        rowItem.setPlugState(!rowItem.getPlugState());
        ((CustomAdapter.ViewHolder) view.getTag()).updateView(rowItem);
        // TODO: change state of plug and update the status
//        ((CustomAdapter.ViewHolder) view.getTag()).plug_id.setText("");
//        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
    }

    private String getWifiIpAddress() {
        byte[] ipAddressParts = getWifiIpAddressParts();
        return getIpAddress(ipAddressParts);
    }

    private String getIpAddress(byte[] ipAddressParts) {
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

    private byte[] getWifiIpAddressParts() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wi = wm.getConnectionInfo();

        int ipAddress = wi.getIpAddress();
        ipAddress = (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) ?
                Integer.reverseBytes(ipAddress) : ipAddress;

        byte[] ipAddressParts = BigInteger.valueOf(ipAddress).toByteArray();

        return ipAddressParts;
    }

    private ArrayList<String> getWifiIpAddresses() {
        ArrayList<String> result = new ArrayList<String>();
        byte[] ipAddressParts = getWifiIpAddressParts();
        for (int i = 1; i <= 254; ++i) {
            ipAddressParts[3] = (byte)i;
            String ip = getIpAddress(ipAddressParts);
            result.add(ip);
        }

        return result;
    }

    public class HttpGetThread implements Runnable {
        private String host;
        private JSONObject jsonResponse;

        public HttpGetThread(String host) {
            this.host = host;
            this.jsonResponse = null;
        }

        @Override
        public void run() {
            try  {
                // Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used. 
                int timeoutConnection = 30000;
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT) 
// in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 10000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpGet httpget = new HttpGet("http://" + this.host + "/state");
                HttpResponse response = httpClient.execute(httpget);

                if (response.getStatusLine().getStatusCode() == 200){
                    String serverResponse = EntityUtils.toString(response.getEntity());
                    this.jsonResponse = new JSONObject(serverResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public JSONObject getJsonResponse() {
            return this.jsonResponse;
        }
    }

    private JSONObject getState(String url) throws IOException, JSONException, InterruptedException {
        HttpGetThread runnable = new HttpGetThread(url);
        Thread thread = new Thread(runnable);

        thread.start();
        thread.join();
        return runnable.getJsonResponse();
    }

}
