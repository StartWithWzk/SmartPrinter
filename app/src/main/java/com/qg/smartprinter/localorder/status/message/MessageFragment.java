package com.qg.smartprinter.localorder.status.message;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qg.smartprinter.R;
import com.qg.smartprinter.data.source.Message;
import com.qg.smartprinter.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import static com.qg.common.Preconditions.checkNotNull;

/**
 * Display a list of {@link Message}s.
 */
public class MessageFragment extends BaseFragment implements MessageContract.View {

    private static final String ARGUMENT_ORDER_ID = "ORDER_ID";

    public MessageAdapter mAdapter;

    private MessageContract.Presenter mPresenter;

    public static MessageFragment newInstance(String orderId) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MessageAdapter(new ArrayList<Message>(0), mItemListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(mAdapter);
        }
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    MessageItemListener mItemListener = new MessageItemListener() {
        @Override
        public void onMessageClick(Message clickedMessage) {
            // Do nothing.
        }
    };

    @Override
    public void showLoadingIndicator(boolean active) {
    }

    @Override
    public void showMessages(List<Message> messages) {
        mAdapter.replaceData(messages);
    }

    @Override
    public void showNoMessages() {
        mAdapter.replaceData(new ArrayList<Message>(0));
    }

    @Override
    public void showLoadingError() {
        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(MessageContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        private List<Message> mValues;

        private MessageItemListener mItemListener;

        MessageAdapter(List<Message> items, MessageItemListener listener) {
            mValues = items;
            mItemListener = listener;
        }

        void replaceData(@NonNull List<Message> messages) {
            setList(messages);
            notifyDataSetChanged();
        }

        public void setList(@NonNull List<Message> messages) {
            mValues = checkNotNull(messages);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(String.valueOf(position));
            holder.mContentView.setText(mValues.get(position).toString());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mItemListener) {
                        mItemListener.onMessageClick(holder.mItem);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;

            private final TextView mIdView;

            private final TextView mContentView;

            private Message mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    public interface MessageItemListener {
        void onMessageClick(Message clickedMessage);
    }
}
