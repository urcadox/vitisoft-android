package vitisoft.vitisoftapp.models.entities;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlotAudit {
    public UUID id;
    public UUID userId;
    public Date date;
    public List<PlotMeasurement> measurements;
}
