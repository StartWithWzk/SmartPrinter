package com.qg.smartprinter.localorder.status.localstatus;

import android.support.annotation.NonNull;

import com.qg.smartprinter.BasePresenter;
import com.qg.smartprinter.BaseView;
import com.qg.smartprinter.data.source.Order;
import com.qg.smartprinter.localorder.Printer;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface LocalStatusContract {

    interface OrderStatusView extends BaseView<OrderStatusPresenter> {

        void setLoadingIndicator(boolean active);

        void showOrderDetailsUi(String orderId);

        void showOrders(List<Order> orders);

        void showNoOrders();

        void showLoadingOrdersError();

        boolean isActive();

        void showError(String s);
    }

    interface OrderStatusPresenter extends BasePresenter {

        void loadOrders(boolean forceUpdate);

        void openOrderDetails(@NonNull Order order);

        boolean resend(Order item);
    }

    interface PrinterStatusView extends BaseView<PrinterStatusPresenter> {

        void setLoadingIndicator(boolean active);

        void showPrinters(List<Printer> printers);

        void showNoPrinters();

        void showLoadingPrintersError();

        boolean isActive();
    }

    interface PrinterStatusPresenter extends BasePresenter {

        void loadPrinters(boolean forceUpdate);
    }
}
