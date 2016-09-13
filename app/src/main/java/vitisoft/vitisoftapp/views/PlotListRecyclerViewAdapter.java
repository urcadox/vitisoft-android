package vitisoft.vitisoftapp.views;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

import vitisoft.vitisoftapp.Consts;
import vitisoft.vitisoftapp.PlotActivity;
import vitisoft.vitisoftapp.R;
import vitisoft.vitisoftapp.models.entities.Plot;

public class PlotListRecyclerViewAdapter extends RecyclerView.Adapter<PlotListRecyclerViewAdapter.PlotViewHolder> {

    public static class PlotViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView plotName;
        TextView plotNumberOfAudits;
        UUID plotId;

        PlotViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            plotName = (TextView)itemView.findViewById(R.id.plot_name);
            plotNumberOfAudits = (TextView)itemView.findViewById(R.id.plot_numberOfAudits);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(cv.getContext(), PlotActivity.class);
                    intent.putExtra(Consts.PLOT_ID, plotId.toString());
                    cv.getContext().startActivity(intent);
                }
            });
        }
    }

    List<Plot> plots;
    int itemLayout;
    public PlotListRecyclerViewAdapter(List<Plot> plots, int itemLayout) {
        this.plots = plots;
        this.itemLayout = itemLayout;
    }

    @Override
    public int getItemCount() {
        return plots.size();
    }

    @Override
    public PlotViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(this.itemLayout, viewGroup, false);
        PlotViewHolder pvh = new PlotViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PlotViewHolder plotViewHolder, int i) {
        plotViewHolder.plotName.setText(plots.get(i).name);
        plotViewHolder.plotNumberOfAudits.setText(plots.get(i).audits.size() + " audits");
        plotViewHolder.plotId = plots.get(i).id;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}