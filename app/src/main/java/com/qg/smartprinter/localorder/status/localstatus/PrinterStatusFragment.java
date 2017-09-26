package com.qg.smartprinter.localorder.status.localstatus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qg.smartprinter.Injection;
import com.qg.smartprinter.R;
import com.qg.smartprinter.localorder.Printer;

import java.util.List;

import static com.qg.common.Preconditions.checkNotNull;

/**
 * Display a list of {@link Printer}s.
 */
public class PrinterStatusFragment extends Fragment
        implements LocalStatusContract.PrinterStatusView {

    private LocalStatusContract.PrinterStatusPresenter mPresenter;

    private TextView mStatusView;

    public static PrinterStatusFragment newInstanceWithPresenter() {
        PrinterStatusFragment fragment = new PrinterStatusFragment();

        new PrinterStatusPresenter(
                Injection.providePrintersRepository(),
                fragment,
                Injection.provideBaseSchedulerProvider()
        );
        return fragment;
    }

    public static PrinterStatusFragment newInstance() {
        PrinterStatusFragment fragment = new PrinterStatusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_local_printer_status, container, false);
        mStatusView = (TextView) v.findViewById(R.id.printer_status);
        setRetainInstance(true);
        return v;
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

    @Override
    public void setLoadingIndicator(boolean active) {
    }

    @Override
    public void showPrinters(List<Printer> printers) {
        mStatusView.setText(null);
        for (Printer printer : printers) {
            mStatusView.append(
                    getString(R.string.printer_status_format,
                            printer.getName(), printer.getAddress(), printer.getStatusString(), printer.getConnectMethod())
            );
        }
    }

    @Override
    public void showNoPrinters() {
        mStatusView.setText(getString(R.string.not_any_printer));
    }

    @Override
    public void showLoadingPrintersError() {
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(LocalStatusContract.PrinterStatusPresenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
