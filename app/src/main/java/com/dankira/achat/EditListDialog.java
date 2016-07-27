package com.dankira.achat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.dankira.achat.models.ShoppingList;

public class EditListDialog extends DialogFragment
{
    private static final String ARG_LIST_KEY = "arg_list_key";
    EditText editListTitle;
    EditText editListDesc;
    Button btnSubmit;
    private ShoppingList currentShoppingList;

    public static EditListDialog newInstance(ShoppingList shoppingList)
    {
        EditListDialog instance = new EditListDialog();

        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_KEY, shoppingList);

        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        currentShoppingList = (ShoppingList) getArguments().getSerializable(ARG_LIST_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_add_new_list_dialog, container);
        editListTitle = (EditText) rootView.findViewById(R.id.edit_list_title);
        editListDesc = (EditText) rootView.findViewById(R.id.edit_list_description);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit_new_list);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(getResources().getString(R.string.add_new_list_dialog_title));

        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditListDialog.this.dismiss();
            }
        });

        editListTitle.setText(currentShoppingList.getListTitle());
        editListDesc.setText(currentShoppingList.getListDesc());

        return rootView;
    }
}
