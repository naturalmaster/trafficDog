package view.main.listener;

public interface OnFlowRefreshListener {
    void onRefreshItem(long todayflow, int index);
    void onRefreshFinish();
    void onRefreshFail();
}
