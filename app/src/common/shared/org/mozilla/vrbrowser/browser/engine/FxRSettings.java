package org.mozilla.vrbrowser.browser.engine;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;
import org.mozilla.geckoview.GeckoRuntimeSettings;

import mozilla.components.concept.engine.Settings;

public class FxRSettings extends Settings {

    private GeckoRuntimeSettings mSettings;

    public FxRSettings(@NonNull GeckoRuntimeSettings settings) {
        mSettings = settings;
    }

    @Override
    public boolean getJavascriptEnabled() {
        return mSettings.getJavaScriptEnabled();
    }

    @Override
    public void setJavascriptEnabled(boolean b) {
        mSettings.setJavaScriptEnabled(b);
    }

    @Override
    public boolean getWebFontsEnabled() {
        return mSettings.getWebFontsEnabled();
    }

    @Override
    public void setWebFontsEnabled(boolean b) {
        mSettings.setWebFontsEnabled(b);
    }

    @Override
    public boolean getAutomaticFontSizeAdjustment() {
        return mSettings.getAutomaticFontSizeAdjustment();
    }

    @Override
    public void setAutomaticFontSizeAdjustment(boolean b) {
        mSettings.setAutomaticFontSizeAdjustment(b);
    }

    @Override
    public boolean getAllowAutoplayMedia() {
        return mSettings.getAutoplayDefault() == GeckoRuntimeSettings.AUTOPLAY_DEFAULT_ALLOWED;
    }

    @Override
    public void setAllowAutoplayMedia(boolean b) {
        mSettings.setAutoplayDefault(b ? GeckoRuntimeSettings.AUTOPLAY_DEFAULT_ALLOWED : GeckoRuntimeSettings.AUTOPLAY_DEFAULT_BLOCKED);
    }

    @Override
    public boolean getRemoteDebuggingEnabled() {
        return mSettings.getRemoteDebuggingEnabled();
    }

    @Override
    public void setRemoteDebuggingEnabled(boolean b) {
        mSettings.setRemoteDebuggingEnabled(b);
    }

    @Nullable
    @Override
    public Boolean getFontInflationEnabled() {
        return mSettings.getFontInflationEnabled();
    }

    @Override
    public void setFontInflationEnabled(@Nullable Boolean aBoolean) {
        mSettings.setFontInflationEnabled(aBoolean);
    }

    @Nullable
    @Override
    public Float getFontSizeFactor() {
        return mSettings.getFontSizeFactor();
    }

    @Override
    public void setFontSizeFactor(@Nullable Float aFloat) {
        mSettings.setFontSizeFactor(aFloat);
    }

    @Override
    public boolean getForceUserScalableContent() {
        return mSettings.getForceUserScalableEnabled();
    }

    @Override
    public void setForceUserScalableContent(boolean b) {
        mSettings.setForceUserScalableEnabled(b);
    }
}
