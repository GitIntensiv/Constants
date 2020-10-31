package model;

public class Store {
    private long currentConstantId = -1;
    private boolean isAddChannels = false;
    private boolean isAddText = false;
    private boolean isAddStartTime = false;
    private boolean isAddEndTime = false;
    private boolean isAddCountWinners = false;
    private boolean isDeleteConstant = false;

    public long getCurrentConstantId() {
        return currentConstantId;
    }

    public void setCurrentConstantId(long currentConstantId) {
        this.currentConstantId = currentConstantId;
    }

    public boolean isAddChannels() {
        return isAddChannels;
    }

    public void setAddChannels(boolean addChannels) {
        isAddChannels = addChannels;
    }

    public boolean isAddText() {
        return isAddText;
    }

    public void setAddText(boolean addText) {
        isAddText = addText;
    }

    public boolean isAddStartTime() {
        return isAddStartTime;
    }

    public void setAddStartTime(boolean addStartTime) {
        isAddStartTime = addStartTime;
    }

    public boolean isAddEndTime() {
        return isAddEndTime;
    }

    public void setAddEndTime(boolean addEndTime) {
        isAddEndTime = addEndTime;
    }

    public boolean isAddCountWinners() {
        return isAddCountWinners;
    }

    public void setAddCountWinners(boolean addCountWinners) {
        isAddCountWinners = addCountWinners;
    }

    public boolean isDeleteConstant() {
        return isDeleteConstant;
    }

    public void setDeleteConstant(boolean deleteConstant) {
        isDeleteConstant = deleteConstant;
    }
}
