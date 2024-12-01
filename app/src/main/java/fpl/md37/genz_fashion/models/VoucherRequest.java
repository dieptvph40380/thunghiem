package fpl.md37.genz_fashion.models;

public class VoucherRequest {
    private String userId;
    private String voucherId;

    public VoucherRequest(String userId) {
        this.userId = userId;
    }

    public VoucherRequest(String userId, String voucherId) {
        this.userId = userId;
        this.voucherId = voucherId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }
}
