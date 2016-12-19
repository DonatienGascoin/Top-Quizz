package cpe.top.quizz.beans;

import java.io.Serializable;

/**
 * @author Donatien
 * @since 10/11/2016
 * @version 0.1
 */

public class Theme implements Serializable {
    private int id;

    private String name;

    public Theme(){

    }

    public Theme(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Theme(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
