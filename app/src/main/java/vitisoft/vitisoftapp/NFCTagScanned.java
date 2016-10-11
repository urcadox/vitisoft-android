package vitisoft.vitisoftapp;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Optional;

public class NFCTagScanned extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_tag_scanned);
    }

    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        TextView text = (TextView) findViewById(R.id.textView);
        NdefMessage[] msgs;
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                NdefRecord record = msgs[0].getRecords()[0];
                String rawContent = new String(record.getPayload());
                Optional<String> plotId = Optional.ofNullable(rawContent.split("vitisoft://plots/")[1]);
                if(plotId.isPresent()) {
                    loadWebViewFromPlotId(plotId.get());
                } else {
                    Toast.makeText(getApplicationContext(), "Contenu du tag invalide", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Tag NFC incorrect", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Tag NFC invalide", Toast.LENGTH_LONG).show();
        }
    }

    protected void loadWebViewFromPlotId(String plotId) {
        Intent intent = new Intent(this, PlotActivity.class);
        intent.putExtra(Consts.PLOT_ID, plotId);
        startActivity(intent);
    }
}
