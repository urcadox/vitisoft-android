package wineplotstracker.wineplotstrackerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import wineplotstracker.wineplotstrackerapp.models.entities.PlotAudit;

public class PlotAuditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_audit);

        Intent intent = getIntent();
        PlotAudit audit = (PlotAudit) intent.getSerializableExtra(Consts.PLOTAUDIT);
        TextView text = (TextView) findViewById(R.id.text_test);
        text.setText(audit.date.toString());
    }
}
