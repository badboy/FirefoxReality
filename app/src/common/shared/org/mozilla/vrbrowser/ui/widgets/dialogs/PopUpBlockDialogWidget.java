/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.vrbrowser.ui.widgets.dialogs;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import org.mozilla.vrbrowser.R;
import org.mozilla.vrbrowser.databinding.PopupBlockDialogBinding;

public class PopUpBlockDialogWidget extends BaseAppDialogWidget {

    private PopupBlockDialogBinding mPopUpBinding;
    private boolean mIsChecked;

    public PopUpBlockDialogWidget(Context aContext) {
        super(aContext);
    }

    @Override
    protected void initialize(Context aContext) {
        super.initialize(aContext);

        LayoutInflater inflater = LayoutInflater.from(aContext);

        // Inflate this data binding layout
        mPopUpBinding = DataBindingUtil.inflate(inflater, R.layout.popup_block_dialog, mBinding.dialogContent, true);
        mPopUpBinding.contentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> mIsChecked = isChecked);

        setButtons(new int[] {
                R.string.popup_block_button_cancel,
                R.string.popup_block_button_show
        });

    }

    public boolean askAgain() {
        return !mIsChecked;
    }

}
