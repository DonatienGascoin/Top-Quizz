package cpe.top.quizz;

import java.util.ArrayList;

public class Quizz {

    public String name;
    public String theme;

    public Quizz(String aName, String aTheme) {
        name = aName;
        theme = aTheme;
    }

    /**
     * Initialise une liste de quizz
     * @return une liste de "quizz"
     */
    public static ArrayList<Quizz> getAListOfQuizz() {
        ArrayList<Quizz> listQuizz = new ArrayList<Quizz>();

        listQuizz.add(new Quizz("Nom1", "Prenom1"));
        listQuizz.add(new Quizz("Nom2", "Prenom2"));
        listQuizz.add(new Quizz("Nom3", "Prenom3"));
        listQuizz.add(new Quizz("Nom4", "Prenom4"));
        listQuizz.add(new Quizz("Nom5", "Prenom5"));
        listQuizz.add(new Quizz("Nom6", "Prenom6"));
        listQuizz.add(new Quizz("Nom7", "Prenom7"));
        listQuizz.add(new Quizz("Nom8", "Prenom8"));
        listQuizz.add(new Quizz("Nom9", "Prenom9"));
        listQuizz.add(new Quizz("Nom10", "Prenom10"));

        return listQuizz;
    }
}