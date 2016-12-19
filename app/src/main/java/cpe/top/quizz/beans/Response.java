package cpe.top.quizz.beans;

import java.io.Serializable;

/**
 * @author Donatien
 * @since 10/11/2016
 * @version 0.1
 */

public class Response implements Serializable {
    private int id;

    private String label;

    private Boolean isValide;

    public Response() {

    }

    public Response(String label, Boolean isValide) {
        this.label = label;
        this.isValide = isValide;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getValide() {
        return isValide;
    }

    public void setValide(Boolean valide) {
        isValide = valide;
    }
}
