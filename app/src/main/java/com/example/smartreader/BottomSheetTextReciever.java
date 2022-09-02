package com.example.smartreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class BottomSheetTextReciever extends BottomSheetDialogFragment {

    private BottomSheetListener mlistener;
    private TextView displayScannedText;
    private ImageButton doneScanningTick, rescanCross;
    private String toDisplayString;

    //used to check was intent successful
    public static final int EDITOR_REQUEST_CODE = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        displayScannedText = v.findViewById(R.id.displayScannedText);
        doneScanningTick = v.findViewById(R.id.doneScanningTick);
        rescanCross = v.findViewById(R.id.rescanCross);

        displayScannedText.setText(toDisplayString);

        //accept the scanned text....
        doneScanningTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditNote.class);
                intent.putExtra("StringfromExternal", "FROM THE SCANNER: \n\n" + toDisplayString);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);

            }
        });

        //rescan again....
        rescanCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.setState(false);
                dismiss();
            }
        });

        return v;
    }

    //displaying text....
    public void setTextToDisplay(String text) {
        toDisplayString = text;
    }

    public interface BottomSheetListener {
        void setState(boolean state);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mlistener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "space must implement BottomListener");
        }
    }
}
