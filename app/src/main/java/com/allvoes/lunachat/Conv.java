package com.allvoes.lunachat;

public class Conv {
    public boolean seen;
    public long lastseen;

    public Conv() {
    }

    public Conv(boolean seen, long lastseen) {
        this.seen = seen;
        this.lastseen = lastseen;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        seen = seen;
    }

    public long getLastseen() {
        return lastseen;
    }

    public void setLastseen(long lastseen) {
        this.lastseen = lastseen;
    }
}
