package org.mozilla.vrbrowser.browser.engine;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import mozilla.components.concept.engine.Settings;

public class FxRSessionSettings extends Settings {

    private Session mSession;

    public FxRSessionSettings(@NonNull Session session) {
        mSession = session;
    }

    @Override
    public boolean getJavascriptEnabled() {
        return mSession.getGeckoSession().getSettings().getAllowJavascript();
    }

    @Override
    public void setJavascriptEnabled(boolean b) {
        mSession.getGeckoSession().getSettings().setAllowJavascript(b);
    }

    @Nullable
    @Override
    public String getUserAgentString() {
        return mSession.getGeckoSession().getSettings().getUserAgentOverride();
    }

    @Override
    public void setUserAgentString(@Nullable String s) {
        mSession.getGeckoSession().getSettings().setUserAgentOverride(s);
    }

    @Override
    public boolean getSuspendMediaWhenInactive() {
        return mSession.getGeckoSession().getSettings().getSuspendMediaWhenInactive();
    }

    @Override
    public void setSuspendMediaWhenInactive(boolean b) {
        mSession.getGeckoSession().getSettings().setSuspendMediaWhenInactive(b);
    }
}
