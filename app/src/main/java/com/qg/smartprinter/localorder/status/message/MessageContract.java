package com.qg.smartprinter.localorder.status.message;

import com.qg.smartprinter.BasePresenter;
import com.qg.smartprinter.BaseView;
import com.qg.smartprinter.data.source.Message;

import java.util.List;

/**
 * @author TZH
 * @version 1.0
 */
public interface MessageContract {
    interface View extends BaseView<Presenter> {

        void showLoadingIndicator(boolean active);

        void showMessages(List<Message> messages);

        void showNoMessages();

        void showLoadingError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void loadMessages(boolean forceUpdate);
    }
}
