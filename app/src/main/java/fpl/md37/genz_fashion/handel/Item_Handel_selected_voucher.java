package fpl.md37.genz_fashion.handel;

import fpl.md37.genz_fashion.models.Voucher;

public interface Item_Handel_selected_voucher {
    void selected_voucher(String userId, String voucherId);
    void onVoucherSelected(Voucher voucher);
    void unselected_voucher(String userId);
    void unVoucherDeselected(String userId);}
