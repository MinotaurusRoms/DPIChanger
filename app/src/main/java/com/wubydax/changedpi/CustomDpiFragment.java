package com.wubydax.changedpi;


/*
 * Copyright (c) 2014-15 Anna Berkovitch & Roberto Mariani
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class CustomDpiFragment extends DialogFragment {

    public CustomDpiFragment() {

    }

    EditText mEdit;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialoginput, null);
        mEdit = (EditText) view.findViewById(R.id.custom_dpi);
        builder.setView(view);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer x = Integer.valueOf(String.valueOf(mEdit.getText()));
                try {
                    Log.v("EditText", mEdit.getText().toString());
                    //we check if the value is in range and create/override the previous out script
                    //if not it will close activity and delete previous out script if present

                    if ((x) >= 330 && (x) <= 480) {
                            try {
                                Command applyLive = new Command(0, "wm density " + mEdit.getText() );
                                RootTools.getShell(true).add(applyLive);
                                Command applyToBuild = new Command(0, "busybox mount -o remount,rw /system", "cd /system", "sed -i '/ro.sf.lcd_density/c\\ro.sf.lcd_density=" + mEdit.getText() + "' build.prop");
                                RootTools.getShell(true).add(applyToBuild);
                                getActivity().finish();
                            } catch (TimeoutException | RootDeniedException | IOException e) {
                                e.printStackTrace();
                            }
//                        Toast.makeText(getActivity().getApplicationContext(), mEdit.getText(), Toast.LENGTH_LONG).show();
                    } else {
                        IllegalValueFragment mIllegalFragment = new IllegalValueFragment();
                        //passing the entered value as set dpi value for the illegal dialog message string
                        mIllegalFragment.mUpdateText(mEdit.getText().toString());
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add(mIllegalFragment, "illegal value fragment");
                        ft.addToBackStack(null);
                        ft.commitAllowingStateLoss();

                    }
                } catch (Exception e) {
                    //Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().finish();

                }
                return false;
            }
        });
        builder.setNeutralButton(R.string.back_to_preset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SetDpiFragment mSetDpiFragment = new SetDpiFragment();
                FragmentTransaction mBackToSetDpi = getFragmentManager().beginTransaction();
                mBackToSetDpi.add(mSetDpiFragment, "dpiset");
                mBackToSetDpi.commit();
            }
        });


        builder.create();
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        //showing keyboard upon creating dialog
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }



}

