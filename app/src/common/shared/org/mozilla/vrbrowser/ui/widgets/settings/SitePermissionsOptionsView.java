/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.vrbrowser.ui.widgets.settings;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import org.mozilla.vrbrowser.R;
import org.mozilla.vrbrowser.databinding.OptionsPrivacyPopupsBinding;
import org.mozilla.vrbrowser.db.SitePermission;
import org.mozilla.vrbrowser.ui.adapters.SitePermissionAdapter;
import org.mozilla.vrbrowser.ui.callbacks.PermissionSiteItemCallback;
import org.mozilla.vrbrowser.ui.viewmodel.SitePermissionViewModel;
import org.mozilla.vrbrowser.ui.widgets.WidgetManagerDelegate;
import org.mozilla.vrbrowser.ui.widgets.WidgetPlacement;

import java.util.List;

class SitePermissionsOptionsView extends SettingsView {

    private OptionsPrivacyPopupsBinding mBinding;
    private SitePermissionAdapter mAdapter;
    private SitePermissionViewModel mViewModel;
    private @SitePermission.Category int mCategory;

    public SitePermissionsOptionsView(Context aContext, WidgetManagerDelegate aWidgetManager, @SitePermission.Category int category) {
        super(aContext, aWidgetManager);
        mCategory = category;
        initialize(aContext);
    }

    private void initialize(Context aContext) {
        LayoutInflater inflater = LayoutInflater.from(aContext);

        // Preferred languages adapter
        mAdapter = new SitePermissionAdapter(getContext(), mCallback);

        // View Model
        mViewModel = new SitePermissionViewModel(((Application)getContext().getApplicationContext()));

        // Inflate this data binding layout
        mBinding = DataBindingUtil.inflate(inflater, R.layout.options_privacy_popups, this, true);

        // Header
        mBinding.headerLayout.setBackClickListener(view -> {
            mDelegate.showView(new PrivacyOptionsView(getContext(), mWidgetManager));
        });

        // Adapters
        mBinding.siteList.setAdapter(mAdapter);

        // Footer
        mBinding.footerLayout.setFooterButtonClickListener(mClearAllListener);

        switch (mCategory) {
            case SitePermission.SITE_PERMISSION_POPUP:
                mBinding.headerLayout.setTitle(R.string.settings_privacy_policy_popups_title);
                mBinding.headerLayout.setDescription(R.string.privacy_options_popups_list_header);
                mBinding.contentText.setText(R.string.privacy_options_popups_list_header);
                mBinding.footerLayout.setDescription(R.string.privacy_options_popups_reset);
                break;
            case SitePermission.SITE_PERMISSION_WEBXR:
                mBinding.headerLayout.setTitle(R.string.settings_privacy_policy_webxr_title);
                mBinding.headerLayout.setDescription(R.string.settings_privacy_policy_webxr_description);
                mBinding.contentText.setText(R.string.settings_privacy_policy_webxr_description);
                mBinding.footerLayout.setDescription(R.string.settings_privacy_policy_webxr_reset);
                break;
        }

        mBinding.executePendingBindings();
    }

    private OnClickListener mClearAllListener = (view) -> {
        reset();
    };

    @Override
    public Point getDimensions() {
        return new Point( WidgetPlacement.dpDimension(getContext(), R.dimen.language_options_width),
                WidgetPlacement.dpDimension(getContext(), R.dimen.language_options_height));
    }

    @Override
    protected boolean reset() {
        mViewModel.deleteAll(mCategory);
        return true;
    }

    @Override
    public void onShown() {
        super.onShown();

        mViewModel.getAll(mCategory).observeForever(mObserver);

        mBinding.siteList.post(() -> mBinding.siteList.scrollToPosition(0));
    }

    @Override
    public void onHidden() {
        super.onHidden();

        mViewModel.getAll(mCategory).removeObserver(mObserver);
    }

    private Observer<List<SitePermission>> mObserver = new Observer<List<SitePermission>>() {
        @Override
        public void onChanged(List<SitePermission> sites) {
            if (sites != null) {
                mAdapter.setSites(sites);
            }
        }
    };

    private PermissionSiteItemCallback mCallback = new PermissionSiteItemCallback() {
        @Override
        public void onDelete(@NonNull SitePermission item) {
            mViewModel.deleteSite(item);
        }

    };
}
