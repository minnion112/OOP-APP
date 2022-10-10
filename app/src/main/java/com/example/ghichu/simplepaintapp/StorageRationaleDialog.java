package com.example.ghichu.simplepaintapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import androidx.annotation.NonNull;

import com.example.ghichu.R;

public class StorageRationaleDialog extends Dialog implements View.OnClickListener
{
    private StorageRationaleOptionSelectedListener listener;

    public StorageRationaleDialog(@NonNull Context context)
    {
        // set the current activity using the context
        super(context);
        setOwnerActivity ((Activity) context);
        // define the window and set the background as transparent to allow for rounded corners
        Window window = super.getWindow ();
        if (window != null)
            window.setBackgroundDrawable (new ColorDrawable(Color.TRANSPARENT));
        // set the resource to be used for the dialog
        super.setContentView (R.layout.activity_storage_rationale_dialog);
        super.setCancelable (false);
        // Initialise the buttons
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonAllow = findViewById(R.id.buttonAllow);
        // Set the click listeners for the buttons
        buttonCancel.setOnClickListener(this);
        buttonCancel.setBackgroundTintList(null);
        buttonAllow.setOnClickListener(this);
        buttonAllow.setBackgroundTintList(null);
    }

    public void setOnStorageRationaleOptionSelectedListener (StorageRationaleOptionSelectedListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View v)
    {
        // get the ID of the view object being clicked
        int viewID = v.getId();
        // prompt the listener depending on the option selected
        if (viewID == R.id.buttonCancel)
        {
            listener.onStorageRationaleOptionSelected(false);
            dismiss();
        } else if (viewID == R.id.buttonAllow)
        {
            listener.onStorageRationaleOptionSelected(true);
            dismiss();
        }
    }

    public interface StorageRationaleOptionSelectedListener
    {
        void onStorageRationaleOptionSelected (boolean allow);
    }
}