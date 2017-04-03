package wineplotstracker.wineplotstrackerapp.views;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import wineplotstracker.wineplotstrackerapp.Consts;
import wineplotstracker.wineplotstrackerapp.PlotAuditActivity;
import wineplotstracker.wineplotstrackerapp.R;
import wineplotstracker.wineplotstrackerapp.models.entities.PlotAudit;

public class AuditListRecyclerViewAdapter extends RecyclerView.Adapter<AuditListRecyclerViewAdapter.AuditViewHolder> {

    public static class AuditViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView auditTitle;
        TextView auditNumberOfMeasures;
        PlotAudit audit;

        AuditViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            auditTitle = (TextView)itemView.findViewById(R.id.audit_title);
            auditNumberOfMeasures = (TextView)itemView.findViewById(R.id.audit_numberOfMeasures);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent intent = new Intent(cv.getContext(), PlotAuditActivity.class);
                intent.putExtra(Consts.PLOTAUDIT, audit);
                cv.getContext().startActivity(intent);
                }
            });
        }
    }

    List<PlotAudit> audits;
    int itemLayout;
    public AuditListRecyclerViewAdapter(List<PlotAudit> audits, int itemLayout) {
        this.audits = audits;
        this.itemLayout = itemLayout;
    }

    @Override
    public int getItemCount() {
        return audits.size();
    }

    @Override
    public AuditViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(this.itemLayout, viewGroup, false);
        AuditViewHolder pvh = new AuditViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(AuditViewHolder auditViewHolder, int i) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        auditViewHolder.auditTitle.setText("Audit du " + df.format(audits.get(i).date));
        auditViewHolder.auditNumberOfMeasures.setText(audits.get(i).measurements.size() + " mesures");
        auditViewHolder.audit = audits.get(i);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}