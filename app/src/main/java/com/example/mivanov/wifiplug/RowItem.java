package com.example.mivanov.wifiplug;

/**
 * Created by mivanov on 14/12/2017.
 */

public class RowItem {

    public RowItem(String plugId, String plugIp, boolean plugState) {
        this.plugId = plugId;
        this.plugIp = plugIp;
        this.plugState = plugState;
    }

    public String getPlugId() {
        return plugId;
    }

    public void setPlugId(String plugId) {
        this.plugId = plugId;
    }

    public String getPlugIp() {
        return plugIp;
    }

    public void setPlugIp(String plugIp) {
        this.plugIp = plugIp;
    }

    public boolean isPlugState() {
        return plugState;
    }

    public void setPlugState(boolean plugState) {
        this.plugState = plugState;
    }

    private String plugId;
    private String plugIp;
    private boolean plugState;
}
