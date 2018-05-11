package hu.creever.robots.interfaces;

public interface StatusListener {
    void onFinished();
    void onStart();
    void onFailure();
    void onHold();
    void onStop();
}
