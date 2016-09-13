package vitisoft.vitisoftapp.models.entities;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Plot {
    public UUID id;
    public UUID userId;
    public String name;
    public Float[][] position;
    public Date createdAt;
    public List<PlotAudit> audits;
}
