package cpe.top.quizz.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Donatien
 * @since 10/11/2016
 * @version 0.1
 */

public class Quizz implements Serializable {
    private int id;

    private String name;

    private char isVisible;

    public Collection<Question> questions;

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

    public char getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(char isVisible) {
        this.isVisible = isVisible;
    }

    public Collection<Question> getQuestions() {
        if(questions == null){
            return new ArrayList<Question>();
        }
        return questions;
    }

    public void setQuestions(Collection<Question> questions) {
        this.questions = questions;
    }
}
