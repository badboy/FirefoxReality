/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.vrbrowser.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import org.mozilla.vrbrowser.R;
import org.mozilla.vrbrowser.ui.widgets.dialogs.UIDialog;

public class NoInternetWidget extends UIDialog {

    public NoInternetWidget(Context aContext) {
        super(aContext);
        initialize(aContext);
    }

    public NoInternetWidget(Context aContext, AttributeSet aAttrs) {
        super(aContext, aAttrs);
        initialize(aContext);
    }

    public NoInternetWidget(Context aContext, AttributeSet aAttrs, int aDefStyle) {
        super(aContext, aAttrs, aDefStyle);
        initialize(aContext);
    }

    private void initialize(Context aContext) {
        inflate(aContext, R.layout.no_internet, this);

        Button mAcceptButton = findViewById(R.id.acceptButton);
        mAcceptButton.setOnClickListener(view -> {
            view.requestFocusFromTouch();
            hide(REMOVE_WIDGET);
        });
    }

    @Override
    protected void initializeWidgetPlacement(WidgetPlacement aPlacement) {
        Context context = getContext();
        aPlacement.width = WidgetPlacement.dpDimension(context, R.dimen.no_internet_width);
        aPlacement.height = WidgetPlacement.dpDimension(context, R.dimen.no_internet_height);
        aPlacement.translationZ = WidgetPlacement.unitFromMeters(getContext(), R.dimen.base_app_dialog_z_distance);
        aPlacement.anchorX = 0.5f;
        aPlacement.anchorY = 0.5f;
        aPlacement.parentAnchorX = 0.5f;
        aPlacement.parentAnchorY = 0.5f;
        aPlacement.opaque = false;
        aPlacement.visible = false;
    }

}
