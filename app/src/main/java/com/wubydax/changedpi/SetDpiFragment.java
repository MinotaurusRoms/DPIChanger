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
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;


public class SetDpiFragment extends DialogFragment {

    public SetDpiFragment() {

    }
    String value = "";
    int intvalue;
    String dpi = "";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Resources res = getResources();
        // retrieve lcd density from get.prop - it will update displayed after reboot.. is not reading from build.prop

        File file = new File("/system/build.prop");
        if (file.exists())
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    if (line.contains("ro.sf.lcd_density")) {
                        dpi = line.substring(line.indexOf("=") + 1, line.length());
                        if (Objects.equals(dpi, "480")){
                            intvalue = 0;
                        } else if (Objects.equals(dpi, "450")){
                            intvalue = 1;
                        } else if (Objects.equals(dpi, "420")){
                            intvalue = 2;
                        } else if (Objects.equals(dpi, "390")){
                            intvalue = 3;
                        } else if (Objects.equals(dpi, "360")){
                            intvalue = 4;
                        } else if (Objects.equals(dpi, "330")){
                            intvalue = 5;
                        } else {
                            intvalue = 6;
                        }
                    }
                }
            }catch (IOException e) {
            e.printStackTrace();
        }

        // we build title pushing inside value from buffer reader (dpi string)
        builder.setTitle(String.format(res.getString(R.string.show_current_dpi), dpi))
                .setIcon(R.drawable.ic_launcher)

                .setSingleChoiceItems(R.array.dpi_values, intvalue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                value = "480";
                                break;
                            case 1:
                                value = "450";
                                break;
                            case 2:
                                value = "420";
                                break;
                            case 3:
                                value = "390";
                                break;
                            case 4:
                                value = "360";
                                break;
                            case 5:
                                value = "330";
                                break;
                            case 6:
                                break;
                        }
                    }
                })

                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        if (selectedPosition != 6) {
                            try {
                                Command applyLive = new Command(0, "wm density " + value);
                                RootTools.getShell(true).add(applyLive);
                                Command applyToBuild = new Command(0, "busybox mount -o remount,rw /system", "cd /system", "sed -i '/ro.sf.lcd_density/c\\ro.sf.lcd_density=" + value + "' build.prop");
                                RootTools.getShell(true).add(applyToBuild);
                                getActivity().finish();
                            } catch (TimeoutException | RootDeniedException | IOException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getActivity().getApplicationContext(), value, Toast.LENGTH_LONG).show();

                        } else if (selectedPosition == 6) {

                            CustomDpiFragment mCustomDpiFragment = new CustomDpiFragment();
                            FragmentTransaction mCustomTransaction = getFragmentManager().beginTransaction();
                            mCustomTransaction.add(mCustomDpiFragment, "custom dpi");
                            mCustomTransaction.commitAllowingStateLoss();

                        }
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            //upon back pressed activity finishes. this is the first dialog. we cannot use ondetach
                            getActivity().finish();
                            return true;
                        }
                        return false;

                    }
                });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); //has to be false or else will hit invisible activity blocking ui. cannot use ondetach
        return dialog;
    }





}

