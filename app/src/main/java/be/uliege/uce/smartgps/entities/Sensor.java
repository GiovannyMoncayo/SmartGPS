package be.uliege.uce.smartgps.entities;

import java.io.Serializable;
import java.sql.Timestamp;

public class Sensor implements Serializable {

    private Float grsX;
    private Float grsY;
    private Float grsZ;
    private Float aclX;
    private Float aclY;
    private Float aclZ;
    private Integer numSatelites;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Float velocidad;
    private Integer actividad;
    private Double pdop;
    private Double hDpo;
    private Double vDpo;
    private Double precision;
    private Timestamp dateInsert;
    private Timestamp dateUpdate;
    private Integer providerStatus;
    private Integer providerStatusUpdate;
    private Integer dspId;


    public Sensor() {
    }

    public Sensor(Float grsX, Float grsY, Float grsZ, Float aclX, Float aclY, Float aclZ, Integer numSatelites, Double latitude, Double longitude, Double altitude, Float velocidad, Integer actividad, Double pdop, Double hDpo, Double vDpo, Double precision, Timestamp dateInsert, Timestamp dateUpdate, Integer providerStatus, Integer providerStatusUpdate, Integer dspId) {
        this.grsX = grsX;
        this.grsY = grsY;
        this.grsZ = grsZ;
        this.aclX = aclX;
        this.aclY = aclY;
        this.aclZ = aclZ;
        this.numSatelites = numSatelites;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.velocidad = velocidad;
        this.actividad = actividad;
        //prueba commit 1
        this.pdop = pdop;
        this.hDpo = hDpo;
        this.vDpo = vDpo;
        this.precision = precision;
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

    public Integer getNumSatelites() {
        return numSatelites;
    }

    public void setNumSatelites(Integer numSatelites) {
        this.numSatelites = numSatelites;
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

    public Float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Float velocidad) {
        this.velocidad = velocidad;
    }

    public Integer getActividad() {
        return actividad;
    }

    public void setActividad(Integer actividad) {
        this.actividad = actividad;
    }

    public Double getPdop() {
        return pdop;
    }

    public void setPdop(Double pdop) {
        this.pdop = pdop;
    }

    public Double gethDpo() {
        return hDpo;
    }

    public void sethDpo(Double hDpo) {
        this.hDpo = hDpo;
    }

    public Double getvDpo() {
        return vDpo;
    }

    public void setvDpo(Double vDpo) {
        this.vDpo = vDpo;
    }

    public Double getPrecision() {
        return precision;
    }

    public void setPrecision(Double precision) {
        this.precision = precision;
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
        return "Sensor {" +
                " numSatelites=" + numSatelites  +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", velocidad=" + velocidad +
                ", actividad=" + actividad +
                ", precision=" + precision +
                ", grsX=" + grsX +
                ", grsY=" + grsY +
                ", grsZ=" + grsZ +
                ", aclX=" + aclX +
                ", aclY=" + aclY +
                ", aclZ=" + aclZ +
                ", pdop=" + pdop +
                ", hDpo=" + hDpo +
                ", vDpo=" + vDpo +
                ", dateInsert=" + dateInsert +
                ", dateUpdate=" + dateUpdate +
                ", providerStatus=" + providerStatus +
                ", providerStatusUpdate=" + providerStatusUpdate +
                ", dspId=" + dspId +
                " }";
    }
}