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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.stericson.RootTools.RootTools;


public class MainActivity extends Activity {
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        CheckSu checkit = new CheckSu();
        checkit.execute();
    }


    // Async task to execute SU in the background
    public class CheckSu extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage(getString(R.string.gaining_root));
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (RootTools.isAccessGiven()) {


                return null;

            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            SetDpiFragment mPresetDPI = new SetDpiFragment();
            FragmentTransaction mPresetDialogTransaction = getFragmentManager().beginTransaction();
            mPresetDialogTransaction.add(mPresetDPI, "dpichange");
            if (!RootTools.isAccessGiven()) {
                //If no su access detected, detach First dialog and throw and alert dialog with single button that will finish the sctivity
                mPresetDialogTransaction.detach(mPresetDPI);
                AlertDialog.Builder mNoSuBuilder = new AlertDialog.Builder(MainActivity.this);
                mNoSuBuilder.setTitle(R.string.missing_su_title);
                mNoSuBuilder.setMessage(R.string.missing_su);
                mNoSuBuilder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                mNoSuBuilder.create();
                Dialog mNoSu = mNoSuBuilder.create();
                mNoSu.show();
            } else {
                //if su access is given, commit the transaction and start dialog sequence. Allowing state loss to escape illegalstateexcep/**/tion
                mPresetDialogTransaction.commitAllowingStateLoss();
            }


        }
    }
}