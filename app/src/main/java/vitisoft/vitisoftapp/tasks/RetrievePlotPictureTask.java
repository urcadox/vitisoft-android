package vitisoft.vitisoftapp.tasks;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import vitisoft.vitisoftapp.R;
import vitisoft.vitisoftapp.Tools;

public class RetrievePlotPictureTask extends AsyncTask<String, Void, Drawable> {

    private Activity activity;
    private Window window;
    private CollapsingToolbarLayout collapsingToolbar;

    public RetrievePlotPictureTask(Activity activity, Window window, CollapsingToolbarLayout toolbar) {
        super();
        this.activity = activity;
        this.window = window;
        this.collapsingToolbar = toolbar;
    }

    protected Drawable doInBackground(String... url) {
        try {
            URL pictureURL = new URL(url[0]);
            try {
                return Tools.drawableFromUrl(pictureURL.toString());
            } catch (IOException e) {
                Log.e("vitisoft IO error", e.getMessage());
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Erreur réseau", Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
        } catch (MalformedURLException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "URL invalide", Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }
    }

    protected void onPostExecute(Drawable picture) {
        if (picture != null) {
            ImageView header = (ImageView) activity.findViewById(R.id.header);
            header.setBackground(picture);
            BitmapDrawable bitmap = (BitmapDrawable) picture;
            Palette.from(bitmap.getBitmap()).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if(collapsingToolbar != null) {
                        collapsingToolbar.setContentScrimColor(palette.getVibrantColor(R.attr.colorPrimary));
                    }
                    window.setStatusBarColor(palette.getVibrantColor(R.attr.colorPrimary));
                }
            });
        }
    }
}
