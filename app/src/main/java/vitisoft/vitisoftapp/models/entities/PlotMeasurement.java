package wineplotstracker.wineplotstrackerapp.models.entities;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class PlotMeasurement implements Serializable {
    public UUID id;
    public UUID plotAuditId;
    public MeasuredItem item;
    public String value;
    public Date timestamp;
}
