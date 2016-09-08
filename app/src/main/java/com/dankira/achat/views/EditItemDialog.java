package com.dankira.achat.views;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dankira.achat.IDialogSubmitListener;
import com.dankira.achat.R;
import com.dankira.achat.models.ShoppingItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by da on 7/25/2016.
 */
public class EditItemDialog extends DialogFragment
{
    private static final String ITEM_ARG_KEY = "item_arg_key";
    EditText editItemTitle;
    EditText editItemDesc;
    Button btnSubmit;
    Button btnScanBarcode;
    TextView txtItemBarcode;
    private ShoppingItem currentItem;
    private IDialogSubmitListener dialogSubmitListener;

    public static EditItemDialog newInstance(ShoppingItem shoppingItem)
    {
        EditItemDialog instance = new EditItemDialog();

        Bundle args = new Bundle();
        args.putSerializable(ITEM_ARG_KEY, shoppingItem);

        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        dialogSubmitListener = (IDialogSubmitListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        currentItem = (ShoppingItem) getArguments().getSerializable(ITEM_ARG_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootDialogView = inflater.inflate(R.layout.fragment_add_new_item, container);

        editItemTitle = (EditText) rootDialogView.findViewById(R.id.edit_item_title);
        editItemDesc = (EditText) rootDialogView.findViewById(R.id.edit_item_desc);
        txtItemBarcode = (TextView) rootDialogView.findViewById(R.id.txt_item_barcode);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(getResources().getString(R.string.add_new_item_dialog_title));

        btnScanBarcode = (Button) rootDialogView.findViewById(R.id.btn_scan_item_barcode);


        editItemTitle.setText(currentItem.getItemTitle());
        editItemDesc.setText(currentItem.getItemDescription());
        txtItemBarcode.setText(currentItem.getBarCode());

        btnScanBarcode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator.forSupportFragment(EditItemDialog.this).initiateScan();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                currentItem.setBarCode(txtItemBarcode.getText().toString().trim());
                currentItem.setItemTitle(editItemTitle.getText().toString().trim());
                currentItem.setItemTitle(editItemDesc.getText().toString().trim());

                if (dialogSubmitListener != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ShoppingItemsFragment.EDIT_ITEM_BUNDLE_KEY, currentItem);
                    dialogSubmitListener.OnDialogSubmit(bundle);
                }

                EditItemDialog.this.dismiss();
            }
        });

        return rootDialogView;
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
