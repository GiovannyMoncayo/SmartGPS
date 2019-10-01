package be.uliege.uce.smartgps.entities;

import java.io.Serializable;
import java.sql.Timestamp;

public class Sensor implements Serializable {

    private Integer dspId;
    private Float grsX;
    private Float grsY;
    private Float grsZ;
    private Float aclX;
    private Float aclY;
    private Float aclZ;
    private Integer nSatellites;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Float velocity;
    private Integer activity;
    private Double accuracy;
    private Timestamp dateInsert;
    private Timestamp dateUpdate;
    private Integer providerStatus;
    private Integer providerStatusUpdate;

    private Double dop;
    private Double hDop;
    private Double vDpo;

    public Sensor() {
    }

    public Sensor(Float grsX, Float grsY, Float grsZ, Float aclX, Float aclY, Float aclZ, Integer nSatellites, Double latitude, Double longitude, Double altitude, Float velocity, Integer activity, Double dop, Double hDop, Double vDpo, Double accuracy, Timestamp dateInsert, Timestamp dateUpdate, Integer providerStatus, Integer providerStatusUpdate, Integer dspId) {
        this.grsX = grsX;
        this.grsY = grsY;
        this.grsZ = grsZ;
        this.aclX = aclX;
        this.aclY = aclY;
        this.aclZ = aclZ;
        this.nSatellites = nSatellites;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.velocity = velocity;
        this.activity = activity;
        this.dop = dop;
        this.hDop = hDop;
        this.vDpo = vDpo;
        this.accuracy = accuracy;
        this.dateInsert = dateInsert;
        this.dateUpdate = dateUpdate;
        this.providerStatus = providerStatus;
        this.providerStatusUpdate = providerStatusUpdate;
        this.dspId = dspId;
    }

    public Float getGrsX() {
        return grsX;
    }

    public void setGrsX(Float grsX) {
        this.grsX = grsX;
    }

    public Float getGrsY() {
        return grsY;
    }

    public void setGrsY(Float grsY) {
        this.grsY = grsY;
    }

    public Float getGrsZ() {
        return grsZ;
    }

    public void setGrsZ(Float grsZ) {
        this.grsZ = grsZ;
    }

    public Float getAclX() {
        return aclX;
    }

    public void setAclX(Float aclX) {
        this.aclX = aclX;
    }

    public Float getAclY() {
        return aclY;
    }

    public void setAclY(Float aclY) {
        this.aclY = aclY;
    }

    public Float getAclZ() {
        return aclZ;
    }

    public void setAclZ(Float aclZ) {
        this.aclZ = aclZ;
    }

    public Integer getnSatellites() {
        return nSatellites;
    }

    public void setnSatellites(Integer nSatellites) {
        this.nSatellites = nSatellites;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Float getVelocity() {
        return velocity;
    }

    public void setVelocity(Float velocity) {
        this.velocity = velocity;
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }

    public Double getDop() {
        return dop;
    }

    public void setDop(Double dop) {
        this.dop = dop;
    }

    public Double gethDop() {
        return hDop;
    }

    public void sethDop(Double hDop) {
        this.hDop = hDop;
    }

    public Double getvDpo() {
        return vDpo;
    }

    public void setvDpo(Double vDpo) {
        this.vDpo = vDpo;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Timestamp getDateInsert() {
        return dateInsert;
    }

    public void setDateInsert(Timestamp dateInsert) {
        this.dateInsert = dateInsert;
    }

    public Timestamp getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Timestamp dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Integer getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(Integer providerStatus) {
        this.providerStatus = providerStatus;
    }

    public Integer getProviderStatusUpdate() {
        return providerStatusUpdate;
    }

    public void setProviderStatusUpdate(Integer providerStatusUpdate) {
        this.providerStatusUpdate = providerStatusUpdate;
    }

    public Integer getDspId() {
        return dspId;
    }

    public void setDspId(Integer dspId) {
        this.dspId = dspId;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "dspId=" + dspId +
                ", grsX=" + grsX +
                ", grsY=" + grsY +
                ", grsZ=" + grsZ +
                ", aclX=" + aclX +
                ", aclY=" + aclY +
                ", aclZ=" + aclZ +
                ", nSatellites=" + nSatellites +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", velocity=" + velocity +
                ", activity=" + activity +
                ", accuracy=" + accuracy +
                ", dateInsert=" + dateInsert +
                ", dateUpdate=" + dateUpdate +
                ", providerStatus=" + providerStatus +
                ", providerStatusUpdate=" + providerStatusUpdate +
                ", dop=" + dop +
                ", hDop=" + hDop +
                ", vDpo=" + vDpo +
                '}';
    }
}