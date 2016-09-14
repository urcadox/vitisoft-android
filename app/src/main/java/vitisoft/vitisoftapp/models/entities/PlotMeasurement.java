package vitisoft.vitisoftapp.models.entities;

import com.google.gson.Gson;

import java.util.Date;
import java.util.UUID;

public class PlotMeasurement {
    public UUID id;
    public UUID plotAuditId;
    public MeasuredItem item;
    public String value;
    public Date timestamp;
}
