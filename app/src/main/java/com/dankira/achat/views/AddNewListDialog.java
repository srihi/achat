package com.dankira.achat.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.dankira.achat.IDialogSubmitListener;
import com.dankira.achat.R;

public class AddNewListDialog extends DialogFragment
{
    EditText editListTitle;
    EditText editListDesc;
    Button btnSubmit;
    private IDialogSubmitListener dialogSubmitListener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        dialogSubmitListener = (IDialogSubmitListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_add_new_list_dialog, container);
        editListTitle = (EditText) rootView.findViewById(R.id.edit_list_title);
        editListDesc = (EditText) rootView.findViewById(R.id.edit_list_description);
        //btnSubmit = (Button) rootView.findViewById(R.id.btn_submit_new_list);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(getResources().getString(R.string.add_new_list_dialog_title));

        /*btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                ShoppingList list = new ShoppingList();
                list.setListTitle(editListTitle.getText().toString().trim());
                list.setListDesc(editListDesc.getText().toString().trim());

                // TODO: 7/25/2016 Submit the new list to the api and sync right away

                if (dialogSubmitListener != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ShoppingListFragment.NEW_LIST_BUNDLE_KEY, list);
                    dialogSubmitListener.OnDialogSubmit(bundle);
                }

                AddNewListDialog.this.dismiss();
            }
        });
*/
        return rootView;
    }


}
