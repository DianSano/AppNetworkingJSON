package id.co.blogspot.diansano.appnetworkingjson;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_INTERNET = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET);
        } else {
            new ReadJSONFeedTask().execute("http://extjs.org.cn/extjs/examples/grid/survey.html");
        }
    }

    public String readJSONFeed(String address) {
        URL url = null;
        try {
            url = new URL(address);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        try {
            InputStream content = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return  stringBuilder.toString();
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return  readJSONFeed(urls[0]);
        }


        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                Log.i("JSON", "Number of surveys in feed: " + jsonArray.length());
                //print out content of json feed
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Toast.makeText(getBaseContext(), jsonObject.getString("appeId") +
                            " - " + jsonObject.getString("inputTime"),
                            Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
