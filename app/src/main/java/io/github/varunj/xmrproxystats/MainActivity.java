package io.github.varunj.xmrproxystats;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Varun on 12-10-2018.
 */

public class MainActivity extends AppCompatActivity {
    private TextView tv_minersNow;
    private TextView tv_minersMax;
    private EditText et_miscInfo;

    private String minersNow = "---";
    private String minersMax = "---";
    private String workers = "---";
    private String hashOneMin = "---";
    private String hashTenMin = "---";
    private String hashOneHr = "---";
    private String hashTwelveHr = "---";
    private String hashTwentyFourHr = "---";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        tv_minersNow = (TextView) findViewById(R.id.minersNow);
        tv_minersMax = (TextView) findViewById(R.id.minersMax);
        et_miscInfo = (EditText) findViewById(R.id.miscInfo);

        RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.mainlayout);
        rlayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "now fetching!", Toast.LENGTH_SHORT).show();
                myClickHandler(v);
            }
        });
    }


    // When user clicks button, calls AsyncTask. Before attempting to fetch the URL, makes sure that there is a network connection.
    public void myClickHandler(View view) {

        String stringUrl = "http://xx.xx.xx.xx:pppp/";
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        }
        else {
            Toast.makeText(getApplicationContext(), "No network connection available!", Toast.LENGTH_SHORT).show();
        }
    }


    // Uses AsyncTask to create a task away from the main UI thread.
    private class DownloadWebpageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            try {
                downloadData(urls[0]);
            }
            catch (IOException e) {
                Log.d("myapp", "Unable to retrieve web page. URL may be invalid.");
            }
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            tv_minersNow.setText(minersNow + " of");
            tv_minersMax.setText(minersMax + " max");
            et_miscInfo.setText(hashOneMin + "\n" + hashTenMin + "\n" + hashOneHr + "\n" + hashTwelveHr + "\n" + hashTwentyFourHr +
                    "\n\n" + workers);
        }
    }


    // Given a URL, establishes an HttpUrlConnection and retrieves JSON
    private void downloadData(String myurl) throws IOException {

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.addRequestProperty("Authorization", "Bearer test");
        JSONObject jObject = null;

        try {
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            jObject = new JSONObject(sb.toString());
            minersNow = jObject.getJSONObject("miners").getString("now");
            minersMax = jObject.getJSONObject("miners").getString("max");
            workers = jObject.getString("workers");
            hashOneMin = jObject.getJSONObject("hashrate").getJSONArray("total").getString(0);
            hashTenMin = jObject.getJSONObject("hashrate").getJSONArray("total").getString(1);
            hashOneHr = jObject.getJSONObject("hashrate").getJSONArray("total").getString(2);
            hashTwelveHr = jObject.getJSONObject("hashrate").getJSONArray("total").getString(3);
            hashTwentyFourHr = jObject.getJSONObject("hashrate").getJSONArray("total").getString(4);

        }
        catch (Exception e) {
            Log.d("myapp", e.getStackTrace().toString());
        }
        finally {
            conn.disconnect();
        }

    }

}
