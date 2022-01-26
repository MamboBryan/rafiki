package com.mambo.rafiki.data.entities;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import com.mambo.rafiki.utils.DecisionUtils;
import com.mambo.rafiki.utils.NumberUtils;

import java.util.Date;

public class Decision {

    private String id;
    private String userID;
    private String content;
    private String description;

    @ServerTimestamp
    private Date date;

    private int proCounts = 0;
    private int proWeights = 0;
    private int prosAverage = 0;

    private int conCounts = 0;
    private int conWeights = 0;
    private int consAverage = 0;

    private int comments = 0;

    private String suggestedMessage = DecisionUtils.START;
    private int suggestion = DecisionUtils.UNCERTAIN;
    private int difference = 0;

    private boolean isCompleted = false;
    private boolean isArchived = false;
    private boolean isDecided = false;
    private boolean isPublic = false;

    private int decision = DecisionUtils.UNCERTAIN;

    public Decision() {
        //empty constructor for firebase
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getProCounts() {
        return proCounts;
    }

    public void setProCounts(int proCounts) {
        this.proCounts = proCounts;
    }

    public int getProWeights() {
        return proWeights;
    }

    public void setProWeights(int proWeights) {
        this.proWeights = proWeights;
    }

    public int getProsAverage() {
        return prosAverage;
    }

    public void setProsAverage(int prosAverage) {
        this.prosAverage = prosAverage;
    }

    public int getConCounts() {
        return conCounts;
    }

    public void setConCounts(int conCounts) {
        this.conCounts = conCounts;
    }

    public int getConWeights() {
        return conWeights;
    }

    public void setConWeights(int conWeights) {
        this.conWeights = conWeights;
    }

    public int getConsAverage() {
        return consAverage;
    }

    public void setConsAverage(int consAverage) {
        this.consAverage = consAverage;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
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

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getDecision() {
        return decision;
    }

    public void setDecision(int decision) {
        this.decision = decision;
    }

    public void addPro(Reason reason) {

        int totalWeights = getProWeights() + reason.getWeight();
        int total = getProCounts() + 1;

        setProWeights(totalWeights);
        setProCounts(total);

        update();

    }

    public void updatePro(Reason newReason, Reason oldReason) {

        int totalWeights = (getProWeights() - oldReason.getWeight()) + newReason.getWeight();

        setProCounts(totalWeights);

        update();

    }

    public void deletePro(Reason reason) {

        int totalWeights = getProWeights() - reason.getWeight();
        int total = getProCounts() - 1;

        setProWeights(totalWeights);
        setProCounts(total);

        update();

    }

    public void addCon(Reason reason) {

        int totalWeights = getConWeights() + reason.getWeight();
        int total = getConCounts() + 1;

        setConWeights(totalWeights);
        setConCounts(total);

        update();

    }

    public void updateCon(Reason newReason, Reason oldReason) {

        int totalWeights = (getConWeights() - oldReason.getWeight()) + newReason.getWeight();

        setConWeights(totalWeights);

        update();
    }

    public void deleteCon(Reason reason) {

        int totalWeights = getConWeights() + reason.getWeight();
        int total = getConCounts() + 1;

        setConWeights(totalWeights);
        setConCounts(total);

        update();

    }

    public void addComment() {

        int total = getComments() + 1;

        setComments(total);

    }

    public void deleteComment() {

        int total = getComments() - 1;

        setComments(total);

    }

    private void update() {
        setProsAverage(NumberUtils.getAverage(getProWeights(), getProCounts()));
        setConsAverage(NumberUtils.getAverage(getConWeights(), getConCounts()));

        int difference = prosAverage - consAverage;

        setDifference(difference);

        setSuggestion(DecisionUtils.getSuggestion(this.difference));
        setSuggestedMessage(DecisionUtils.getSuggestionMessage(this.difference));
    }

    public void decide(int decision) {

        setCompleted(true);

        setDecided(true);
        setDecision(decision);
    }
}
