package com.hnust.trumi.scheduleview;

/**
 * Created by trumi on 16-12-24.
 */

public class PositionInfo {
    private int start;
    private int size;

    public PositionInfo(int start, int size) {
        this.start = start;
        this.size = size;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
