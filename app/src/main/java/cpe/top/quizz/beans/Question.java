package cpe.top.quizz.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Donatien
 * @since 10/11/2016
 * @version 0.1
 */

public class Question implements Serializable {
    private int id;

    private String label;

    private String pseudo;

    private String explanation;

    private Collection<Response> responses;

    public Collection<Theme> themes;

    public Collection<Quizz> quizzs;

    public Question() {

    }

    public Question(int id, String label, String pseudo, String explanation, Collection<Response> responses, Collection<Theme> themes, Collection<Quizz> quizzs) {
        this.id = id;
        this.label = label;
        this.pseudo = pseudo;
        this.explanation = explanation;
        this.responses = responses;
        this.themes = themes;
        this.quizzs = quizzs;
    }

    public Question(String label, String pseudo, String explanation, Collection<Response> responses, Collection<Theme> themes, Collection<Quizz> quizzs) {
        this.label = label;
        this.pseudo = pseudo;
        this.explanation = explanation;
        this.responses = responses;
        this.themes = themes;
        this.quizzs = quizzs;
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

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public Collection<Response> getReponses() {
        if(responses == null){
            return new ArrayList<Response>();
        }
        return responses;
    }

    public void setReponses(Collection<Response> reponses) {
        this.responses = reponses;
    }

    public Collection<Theme> getThemes() {
        if(themes == null){
            return new ArrayList<Theme>();
        }
        return themes;
    }

    public void setThemes(Collection<Theme> themes) {
        this.themes = themes;
    }

    public Collection<Quizz> getQuizzs() {
        if(quizzs == null){
            return new ArrayList<Quizz>();
        }
        return quizzs;
    }

    public void setQuizzs(Collection<Quizz> quizzs) {
        this.quizzs = quizzs;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}

