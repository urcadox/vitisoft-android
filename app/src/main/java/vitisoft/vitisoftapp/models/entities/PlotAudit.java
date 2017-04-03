package wineplotstracker.wineplotstrackerapp.models.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlotAudit implements Serializable {
    public UUID id;
    public UUID userId;
    public Date date;
    public List<PlotMeasurement> measurements;
}
