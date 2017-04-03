package wineplotstracker.wineplotstrackerapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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

import wineplotstracker.wineplotstrackerapp.models.entities.Plot;
import wineplotstracker.wineplotstrackerapp.views.AuditListRecyclerViewAdapter;

public class PlotActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GoogleMap map;
    public LinkedList<LatLng> polygonCoordinates;
    public String plotId;
    public CollapsingToolbarLayout collapsingToolbar;
    public Plot plot;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        plotId = intent.getStringExtra(Consts.PLOT_ID);
        plot = (Plot) intent.getSerializableExtra(Consts.PLOT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_white_48px);
        fab.setBackgroundColor(Color.GREEN);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AuditFormActivity.class);
                intent.putExtra(Consts.PLOT_ID, plotId.toString());
                view.getContext().startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Parcelle");
        collapsingToolbar.setTitle("Parcelle");

        if(plot != null) {
            displayPlot(plot);
        } else {
            String url = "https://wineplotstracker.cleverapps.io/api/plots/" + plotId;
            new RetrievePlotTask().execute(url);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.plot_map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Plot Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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
                } catch (IOException e) {
                    Log.e("wineplotstracker IO error", e.getMessage());
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Erreur réseau", Toast.LENGTH_LONG).show();
                        }
                    });
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (MalformedURLException e) {
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
            if (rawData != null) {
                Gson gson = new Gson();
                Type plotType = new TypeToken<Plot>() {
                }.getType();
                Plot plot = gson.fromJson(rawData, plotType);

                displayPlot(plot);
            }
        }
    }

    private void displayPlot(Plot plot) {
        getSupportActionBar().setTitle(plot.name);
        collapsingToolbar.setTitle(plot.name);

        ImageView header = (ImageView) findViewById(R.id.header);
        Glide.with(PlotActivity.this).load(plot.pictureUrl).asBitmap().into(new BitmapImageViewTarget(header) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        if(collapsingToolbar != null) {
                            collapsingToolbar.setContentScrimColor(palette.getVibrantColor(R.attr.colorPrimary));
                        }
                        getWindow().setStatusBarColor(palette.getVibrantColor(R.attr.colorPrimary));
                    }
                });
            }
        });

        polygonCoordinates = new LinkedList<>();
        for (int i = 0; i < plot.position.length; i++) {
            polygonCoordinates.add(new LatLng(plot.position[i][1], plot.position[i][0]));
        }

        showPolygonOnMap();

        RecyclerView rv = (RecyclerView) findViewById(R.id.auditsRV);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);

        AuditListRecyclerViewAdapter adapter = new AuditListRecyclerViewAdapter(plot.audits, R.layout.auditcardview);
        rv.setAdapter(adapter);
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
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics()
            ));
            int padding = (int) (width * 0.12);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }
    }

}
