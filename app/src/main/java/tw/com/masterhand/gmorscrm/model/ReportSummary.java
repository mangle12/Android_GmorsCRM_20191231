package tw.com.masterhand.gmorscrm.model;

public class ReportSummary {
    int winAmount;
    int contractAmount;
    int lose;
    int signIn;
    int signOut;
    int taskCreate;
    int opportunityCreate;

    public int getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(int winAmount) {
        this.winAmount = winAmount;
    }

    public int getcontractAmount() {
        return contractAmount;
    }

    public void setcontractAmount(int contractAmount) {
        this.contractAmount = contractAmount;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getSignIn() {
        return signIn;
    }

    public void setSignIn(int signIn) {
        this.signIn = signIn;
    }

    public int getSignOut() {
        return signOut;
    }

    public void setSignOut(int signOut) {
        this.signOut = signOut;
    }

    public int getTaskCreate() {
        return taskCreate;
    }

    public void setTaskCreate(int taskCreate) {
        this.taskCreate = taskCreate;
    }

    public int getOpportunityCreate() {
        return opportunityCreate;
    }

    public void setOpportunityCreate(int opportunityCreate) {
        this.opportunityCreate = opportunityCreate;
    }
}
