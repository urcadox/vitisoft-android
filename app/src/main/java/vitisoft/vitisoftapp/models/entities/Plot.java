package vitisoft.vitisoftapp.models.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Plot implements Serializable {
    public UUID id;
    public UUID userId;
    public String name;
    public Float[][] position;
    public Date createdAt;
    public String pictureUrl;
    public List<PlotAudit> audits;
}
