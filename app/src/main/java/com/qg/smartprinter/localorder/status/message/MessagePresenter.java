package com.qg.smartprinter.localorder.status.message;

import android.support.annotation.NonNull;

import com.qg.smartprinter.data.source.Message;
import com.qg.smartprinter.data.source.OrdersDataSource;
import com.qg.smartprinter.util.scheduler.BaseSchedulerProvider;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.qg.common.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link MessageFragment}), retrieves the data and updates
 * the UI as required.
 */
public class MessagePresenter implements MessageContract.Presenter {

    private String mOrderId;

    private OrdersDataSource mOrdersRepository;

    private MessageContract.View mMessageView;

    private BaseSchedulerProvider mSchedulerProvider;

    private CompositeSubscription mSubscriptions;

    public MessagePresenter(@NonNull String orderId,
                            @NonNull OrdersDataSource ordersRepository,
                            @NonNull MessageContract.View view,
                            @NonNull BaseSchedulerProvider schedulerProvider) {
        mOrderId = checkNotNull(orderId);
        mOrdersRepository = checkNotNull(ordersRepository);
        mMessageView = checkNotNull(view);
        mSchedulerProvider = checkNotNull(schedulerProvider);
        mMessageView.setPresenter(this);

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void loadMessages(boolean forceUpdate) {
        mSubscriptions.clear();
        Subscription subscription = mOrdersRepository
                .getMessagesWithOrderId(mOrderId)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onCompleted() {
                        mMessageView.showLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMessageView.showLoadingError();
                    }

                    @Override
                    public void onNext(List<Message> messages) {
                        processMessages(messages);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void processMessages(List<Message> messages) {
        if (messages.isEmpty()) {
            mMessageView.showNoMessages();
        } else {
            mMessageView.showMessages(messages);
        }
    }


    @Override
    public void subscribe() {
        loadMessages(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
