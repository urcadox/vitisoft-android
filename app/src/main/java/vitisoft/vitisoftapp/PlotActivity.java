package vitisoft.vitisoftapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

import vitisoft.vitisoftapp.models.entities.Plot;
import vitisoft.vitisoftapp.views.AuditListRecyclerViewAdapter;
import vitisoft.vitisoftapp.views.PlotListRecyclerViewAdapter;

public class PlotActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GoogleMap map;
    public LinkedList<LatLng> polygonCoordinates;
    public String plotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        plotId = intent.getStringExtra(Consts.PLOT_ID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_white_48px);
        fab.setBackgroundColor(Color.GREEN);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AuditFormActivity.class);
                intent.putExtra(Consts.PLOT_ID, plotId.toString());
                view.getContext().startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Parcelle");

        String url = "https://vitisoft.cleverapps.io/api/plots/" + plotId;
        new RetrievePlotTask().execute(url);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.plot_map);
        mapFragment.getMapAsync(this);
    }

    class RetrievePlotTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... url) {
            try {
                URL plotURL = new URL(url[0]);
                HttpsURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpsURLConnection) plotURL.openConnection();
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
                      @Override
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
                    @Override
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
                Type plotType = new TypeToken<Plot>(){}.getType();
                Plot plot = gson.fromJson(rawData, plotType);

                getSupportActionBar().setTitle(plot.name);

                new RetrievePlotPictureTask().execute(plot.pictureUrl);

                polygonCoordinates = new LinkedList();
                for(int i = 0; i < plot.position.length; i++) {
                    polygonCoordinates.add(new LatLng(plot.position[i][1], plot.position[i][0]));
                }

                showPolygonOnMap();

                RecyclerView rv = (RecyclerView)findViewById(R.id.auditsRV);
                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                rv.setLayoutManager(llm);

                AuditListRecyclerViewAdapter adapter = new AuditListRecyclerViewAdapter(plot.audits, R.layout.auditcardview);
                rv.setAdapter(adapter);
            }
        }
    }

    class RetrievePlotPictureTask extends AsyncTask<String, Void, Drawable> {
        protected Drawable doInBackground(String... url) {
            try {
                URL pictureURL = new URL(url[0]);
                try {
                    return Tools.drawableFromUrl(pictureURL.toString());
                }
                catch(IOException e) {
                    Log.e("vitisoft IO error", e.getMessage());
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Erreur réseau", Toast.LENGTH_LONG).show();
                        }
                    });
                    return null;
                }
            } catch(MalformedURLException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "URL invalide", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
        }

        protected void onPostExecute(Drawable picture) {
            if(picture != null) {
                ImageView header = (ImageView)findViewById(R.id.header);
                header.setBackground(picture);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.273645, -0.406838), 11));
        showPolygonOnMap();
    }

    public void showPolygonOnMap() {
        if (map != null && polygonCoordinates != null) {
            PolygonOptions polygon = new PolygonOptions()
                .addAll(polygonCoordinates)
                .fillColor(Color.argb(20, 0, 170, 0))
                .strokeColor(Color.argb(80, 0, 255, 0))
                .strokeWidth(5);
            map.addPolygon(polygon);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latlng : polygonCoordinates) {
                builder.include(latlng);
            }
            LatLngBounds bounds = builder.build();
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
        }
    }

}
