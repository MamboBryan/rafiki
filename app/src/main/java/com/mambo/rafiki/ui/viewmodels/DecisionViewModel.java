package com.mambo.rafiki.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mambo.rafiki.data.entities.Comment;
import com.mambo.rafiki.data.entities.Decision;
import com.mambo.rafiki.data.entities.Reason;
import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.repositories.CommentRepository;
import com.mambo.rafiki.repositories.DecisionRepository;
import com.mambo.rafiki.repositories.ReasonRepository;

import java.util.List;

public class DecisionViewModel extends AndroidViewModel {

    private DecisionRepository decisionRepository;
    private ReasonRepository reasonRepository;
    private CommentRepository commentRepository;

    public Reason reason;
    public Comment comment;

    public MutableLiveData<Response> reasonResponse = new MutableLiveData<>();
    public MutableLiveData<Response> decisionResponse = new MutableLiveData<>();
    public MutableLiveData<Response> commentResponse = new MutableLiveData<>();

    public MutableLiveData<Decision> decision = new MutableLiveData<>();

    public MutableLiveData<List<Reason>> pros = new MutableLiveData<>();
    public MutableLiveData<List<Reason>> cons = new MutableLiveData<>();

    public MutableLiveData<List<Comment>> comments = new MutableLiveData<>();

    public DecisionViewModel(@NonNull Application application) {
        super(application);

        decisionRepository = DecisionRepository.getInstance(application);
        reasonRepository = ReasonRepository.getInstance(application);
        commentRepository = CommentRepository.getInstance(application);

    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void init(Decision update) {
        decisionRepository.setDecisionListener(update);

        decision.setValue(update);

        reasonResponse = reasonRepository.getResponse();
        decisionResponse = decisionRepository.getResponse();
        commentResponse = commentRepository.getResponse();

        pros = reasonRepository.getPros(update);
        cons = reasonRepository.getCons(update);
        comments = commentRepository.getComments(update);

    }

    //decision

    public void setDecision(Decision update) {
        decision.setValue(update);
    }

    private Decision getDecision() {
        return decision.getValue();
    }

    public void update() {
        if (getDecision() != null)
            decisionRepository.updateDecision(getDecision());
    }

    private void updateDecision(Decision newDecision) {

        decision.setValue(newDecision);

    }

    public void updateContent(String content) {

        Decision updatedDecision = getDecision();
        updatedDecision.setContent(content);

        decisionRepository.updateDecision(updatedDecision);

        updateDecision(updatedDecision);

    }

    public void updateDescription(String content) {

        Decision updatedDecision = getDecision();

        updatedDecision.setDescription(content);
        decisionRepository.updateDecision(updatedDecision);

        updateDecision(updatedDecision);

    }

    public void archiveDecision() {

        Decision updatedDecision = getDecision();

        boolean isArchived = !updatedDecision.isArchived();

        updatedDecision.setArchived(isArchived);
        updateDecision(updatedDecision);

        decisionResponse = decisionRepository.updateDecision(updatedDecision);

    }

    public void changeDecisionVisibility() {

        Decision updatedDecision = getDecision();

        boolean isPublic = !updatedDecision.isPublic();
        updatedDecision.setPublic(isPublic);

        updateDecision(updatedDecision);

        decisionResponse = decisionRepository.updateDecision(updatedDecision);

    }

    public void deleteDecision() {
        decisionResponse = decisionRepository.delete(getDecision());
    }

    public void decide(int decisionInt) {

        Decision updatedDecision = getDecision();

        updatedDecision.decide(decisionInt);
        updateDecision(updatedDecision);

        decisionResponse = decisionRepository.updateDecision(updatedDecision);

    }

    public void retryDeciding() {
        decisionResponse = decisionRepository.updateDecision(decision.getValue());
    }

    //pros

    public void refreshPros() {
        reasonRepository.getPros(decision.getValue());
    }

    public void addPro() {

        reason.setDecisionID(decision.getValue().getId());
        reasonResponse = reasonRepository.addPro(reason);

        Decision updatedDecision = getDecision();
        updatedDecision.addPro(reason);

        updateDecision(updatedDecision);
    }

    public void updatePro() {
        reasonResponse = reasonRepository.updatePro(reason);
    }

    public void deletePro() {
        reasonResponse = reasonRepository.deletePro(reason);

        Decision updatedDecision = getDecision();
        updatedDecision.deletePro(reason);

        updateDecision(updatedDecision);
    }

    //cons

    public void refreshCons() {
        reasonRepository.getCons(decision.getValue());
    }

    public void addCon() {

        reason.setDecisionID(decision.getValue().getId());
        reasonResponse = reasonRepository.addCon(reason);

        Decision updatedDecision = getDecision();
        updatedDecision.addCon(reason);


        updateDecision(updatedDecision);
    }

    public void updateCon() {
        reasonResponse = reasonRepository.updateCon(reason);

    }

    public void deleteCon() {
        reasonResponse = reasonRepository.deleteCon(reason);

        Decision updatedDecision = getDecision();
        updatedDecision.deleteCon(reason);

        updateDecision(updatedDecision);
    }

    //reviews

    public void refreshComments() {
        commentRepository.getComments(decision.getValue());
    }

    public void addComment() {

        comment.setDecisionID(decision.getValue().getId());
        commentResponse = commentRepository.addComment(comment);

        Decision updatedDecision = getDecision();
        updatedDecision.addComment();

        updateDecision(updatedDecision);
    }

    public void deleteComment() {

        commentResponse = commentRepository.deleteComment(comment);

        Decision updatedDecision = getDecision();
        updatedDecision.deleteComment();

        updateDecision(updatedDecision);
    }

    public void updateComment() {

        comment.setUpdated(true);

        commentResponse = commentRepository.updateComment(comment);

    }

}
