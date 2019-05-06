package be.uliege.uce.smartgps.entities;

import java.sql.Timestamp;
import java.util.List;

public class Questions {

    private int idQuiz;
    private String imgView;
    private String txtDescription;
    private String txtValor;
    private List<Options> opcOptions;
    private int type;
    private Timestamp dateQuiz;

    public Questions() {

    }

    public Questions(int idQuiz, String imgView, String txtDescription, String txtValor, List<Options> opcOptions, int type, Timestamp dateQuiz) {
        this.idQuiz = idQuiz;
        this.imgView = imgView;
        this.txtDescription = txtDescription;
        this.txtValor = txtValor;
        this.type = type;
        this.dateQuiz = dateQuiz;
    }


    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public String getImgView() {
        return imgView;
    }

    public void setImgView(String imgView) {
        this.imgView = imgView;
    }

    public String getTxtDescription() {
        return txtDescription;
    }

    public void setTxtDescription(String txtDescription) {
        this.txtDescription = txtDescription;
    }

    public String getTxtValor() {
        return txtValor;
    }

    public void setTxtValor(String txtValor) {
        this.txtValor = txtValor;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Timestamp getDateQuiz() {
        return dateQuiz;
    }

    public void setDateQuiz(Timestamp dateQuiz) {
        this.dateQuiz = dateQuiz;
    }

    public List<Options> getOpcOptions() {
        return opcOptions;
    }

    public void setOpcOptions(List<Options> opcOptions) {
        this.opcOptions = opcOptions;
    }

    @Override
    public String toString() {
        return "Questions {" +
                " idQuiz=" + idQuiz +
                ", imgView='" + imgView + '\'' +
                ", txtDescription='" + txtDescription + '\'' +
                ", txtValor='" + txtValor + '\'' +
                ", opcOptions=" + opcOptions +
                ", type=" + type +
                ", dateQuiz=" + dateQuiz +
                '}';
    }
}
