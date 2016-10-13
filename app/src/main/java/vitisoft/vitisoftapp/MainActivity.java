package vitisoft.vitisoftapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import vitisoft.vitisoftapp.models.entities.Plot;
import vitisoft.vitisoftapp.views.PlotListRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "https://vitisoft.cleverapps.io/api/plots";
        new RetrievePlotsTask().execute(url);
    }

    class RetrievePlotsTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... url) {
            try {
                URL plotsURL = new URL(url[0]);
                HttpsURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpsURLConnection) plotsURL.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                    String rawData = IOUtils.toString(reader);
                    IOUtils.closeQuietly(reader);
                    return rawData;
                }
                catch(IOException e) {
                    Log.e("vitisoft IO error", e.getMessage());
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Erreur réseau", Toast.LENGTH_LONG).show();
                        }
                    });
                    return null;
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch(MalformedURLException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "URL invalide", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
        }

        protected void onPostExecute(String rawData) {
            if(rawData != null) {
                Gson gson = new Gson();
                Type plotListType = new TypeToken<List<Plot>>(){}.getType();
                List<Plot> plots = gson.fromJson(rawData, plotListType);

                RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                rv.setLayoutManager(llm);

                for(Plot p : plots) {
                    Glide.with(MainActivity.this).load(p.pictureUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE).preload();
                }

                PlotListRecyclerViewAdapter adapter = new PlotListRecyclerViewAdapter(plots, R.layout.plotcardview);
                rv.setAdapter(adapter);
            }
        }
    }
}
