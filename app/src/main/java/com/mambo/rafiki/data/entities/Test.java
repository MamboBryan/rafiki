package com.mambo.rafiki.data.entities;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import com.mambo.rafiki.utils.DecisionUtils;
import com.mambo.rafiki.utils.NumberUtils;

import java.util.Date;
import java.util.List;

public class Test {

    private String id;
    private String user;

    private String content;

    @ServerTimestamp
    private Date date;

    private List<Reason> pros;
    private int positivity;

    private List<Reason> cons;
    private int negativity;

    private String suggestedMessage = DecisionUtils.START;
    private int suggestion = DecisionUtils.UNCERTAIN;
    private int probability = 0;

    private boolean isCompleted = false;
    private boolean isArchived = false;
    private boolean isDecided = false;

    private int choice = DecisionUtils.UNCERTAIN;

    public Test() {
        //public constructor for fire store
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Reason> getPros() {
        return pros;
    }

    public void setPros(List<Reason> pros) {
        this.pros = pros;
    }

    public int getPositivity() {
        return positivity;
    }

    public void setPositivity(int positivity) {
        this.positivity = positivity;
    }

    public List<Reason> getCons() {
        return cons;
    }

    public void setCons(List<Reason> cons) {
        this.cons = cons;
    }

    public int getNegativity() {
        return negativity;
    }

    public void setNegativity(int negativity) {
        this.negativity = negativity;
    }

    public String getSuggestedMessage() {
        return suggestedMessage;
    }

    public void setSuggestedMessage(String suggestedMessage) {
        this.suggestedMessage = suggestedMessage;
    }

    public int getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(int suggestion) {
        this.suggestion = suggestion;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isDecided() {
        return isDecided;
    }

    public void setDecided(boolean decided) {
        isDecided = decided;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }


    //updating methods
    private void updateWeights() {

        int proWeights = 0;
        int proCounts = 0;

        if (pros != null) {

            for (int i = 0; i < pros.size(); i++) {
                proCounts = i;
                proWeights += pros.get(i).getWeight();
            }

        }

        int prosAverage = NumberUtils.getAverage(proWeights, proCounts);
        setPositivity(prosAverage);

        int conWeights = 0;
        int conCounts = 0;

        if (cons != null) {

            for (int i = 0; i < cons.size(); i++) {
                conCounts = i;
                conWeights += cons.get(i).getWeight();
            }

        }

        int consAverage = NumberUtils.getAverage(conWeights, conCounts);
        setPositivity(consAverage);

        updateSuggestion();
    }

    private void updateSuggestion() {

        int difference = positivity - negativity;

        setProbability(difference);

        setSuggestedMessage(DecisionUtils.getSuggestionMessage(difference));
        setSuggestion(DecisionUtils.getSuggestion(difference));

    }

    private void completeChoice(int choice) {

        setCompleted(true);

        setDecided(true);
        setChoice(choice);
    }
}
