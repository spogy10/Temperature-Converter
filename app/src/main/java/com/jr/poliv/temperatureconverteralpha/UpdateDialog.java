package com.jr.poliv.temperatureconverteralpha;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by poliv on 5/8/2016.
 */
public class UpdateDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        return new AlertDialog.Builder(getActivity())
                .setTitle("Please update Exchange Rate")
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int boss) {
                                ((CurrencyConverter)getActivity()).update();
                            }
                        }
                )
                .setNegativeButton("Don't Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
                .create();
    }
}
