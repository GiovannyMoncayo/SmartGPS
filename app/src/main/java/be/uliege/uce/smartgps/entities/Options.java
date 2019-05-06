package be.uliege.uce.smartgps.entities;

public class Options {

    private int opcKey;
    private String opcDescription;

    public Options() {
    }

    public Options(int opcKey, String opcDescription) {
        this.opcKey = opcKey;
        this.opcDescription = opcDescription;
    }

    public int getOpcKey() {
        return opcKey;
    }

    public void setOpcKey(int opcKey) {
        this.opcKey = opcKey;
    }

    public String getOpcDescription() {
        return opcDescription;
    }

    public void setOpcDescription(String opcDescription) {
        this.opcDescription = opcDescription;
    }
}
