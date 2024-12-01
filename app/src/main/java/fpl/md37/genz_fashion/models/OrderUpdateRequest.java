package fpl.md37.genz_fashion.models;

public class OrderUpdateRequest {
    private int state;
    private String cancleOrder_time;

    public OrderUpdateRequest(int state, String cancleOrder_time) {
        this.state = state;
        this.cancleOrder_time = cancleOrder_time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCancleOrderTime() {
        return cancleOrder_time;
    }

    public void setCancleOrderTime(String cancleOrder_time) {
        this.cancleOrder_time = cancleOrder_time;
    }
}