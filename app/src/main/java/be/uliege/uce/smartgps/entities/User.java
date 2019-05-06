package be.uliege.uce.smartgps.entities;

public class User {

    private int id;
    private String usuario;
    private String nombres;
    private String correoElectronico;
    private int usroId;
    private int rolId;
    private int dspId;
    private String fcmToken;
    private int estado;
    private String horaSinc;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public int getUsroId() {
        return usroId;
    }

    public void setUsroId(int usroId) {
        this.usroId = usroId;
    }

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getDspId() {
        return dspId;
    }

    public void setDspId(int dspId) {
        this.dspId = dspId;
    }

    public String getHoraSinc() {
        return horaSinc;
    }

    public void setHoraSinc(String horaSinc) {
        this.horaSinc = horaSinc;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "usuario='" + usuario + '\'' +
                ", nombres='" + nombres + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", dspId='" + dspId + '\'' +
                ", horaSinc='" + horaSinc + '\'' +
                '}';
    }
}
