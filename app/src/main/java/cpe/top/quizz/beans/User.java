package cpe.top.quizz.beans;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Donatien
 * @version 1.0
 * @since 06/11/2016
 */

public class User implements Serializable {

    private String pseudo;
    private String mail;
    private String password;
    private Collection<User> friends;
    private Collection<Question> questions;

    public User() {

    }

    public User(String pseudo, String mail, String password, Collection<User> friends, Collection<Question> questions) {
        this.pseudo = pseudo;
        this.mail = mail;
        this.password = password;
        this.friends = friends;
        this.questions = questions;
    }

    public User(String pseudo) {
        this.pseudo = pseudo;
        this.password = null;
        this.mail = null;
        this.friends = null;
        this.questions = null;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<User> getFriends() {
        if (friends == null) {
            return new ArrayList<User>();
        }
        return friends;
    }

    public void setFriends(Collection<User> friends) {
        this.friends = friends;
    }

    public Collection<Question> getQuestions() {
        if (questions == null) {
            return new ArrayList<Question>();
        }
        return questions;
    }

    public void setQuestions(Collection<Question> questions) {
        this.questions = questions;
    }

}
