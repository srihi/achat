package com.dankira.achat.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dankira.achat.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AddNewItemDialog extends DialogFragment
{
    private static final String BAR_CODE_ARG_KEY = "BAR_CODE_ARG_KEY";
    private String barCodeArg;
    EditText editItemTitle;
    EditText editItemDesc;
    TextView txtItemBarcode;
    Button btnScanBarcode;
    Button btnSubmitNewItem;

    public static AddNewItemDialog newInstance(String barcode)
    {
        AddNewItemDialog instance = new AddNewItemDialog();

        Bundle args = new Bundle();
        args.putString(BAR_CODE_ARG_KEY, barcode);

        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        barCodeArg = getArguments().getString(BAR_CODE_ARG_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_add_new_item, container);

        editItemTitle = (EditText) rootView.findViewById(R.id.edit_item_title);
        editItemDesc = (EditText) rootView.findViewById(R.id.edit_item_desc);
        txtItemBarcode = (TextView) rootView.findViewById(R.id.txt_item_barcode);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(getResources().getString(R.string.add_new_item_dialog_title));

        btnScanBarcode = (Button) rootView.findViewById(R.id.btn_scan_item_barcode);
        btnSubmitNewItem = (Button) rootView.findViewById(R.id.btn_submit_new_item);

        btnScanBarcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator.forSupportFragment(AddNewItemDialog.this).initiateScan();
            }
        });

        btnSubmitNewItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });

        if (!TextUtils.isEmpty(barCodeArg))
        {
            txtItemBarcode.setText(barCodeArg);
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                // there is nothing scanned, must have been canceled.
            }
            else
            {
                txtItemBarcode.setText(result.getContents());
                // TODO: 7/21/2016 This is where we get the code for sharing, then verify and move on.
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
