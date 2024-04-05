package app.revanced.integrations.youtube.settings.preference;

import static app.revanced.integrations.shared.utils.StringRef.str;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import app.revanced.integrations.shared.settings.Setting;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.Utils;
import app.revanced.integrations.youtube.settings.Settings;

/**
 * Shows what thumbnails will be used based on the current settings.
 * @noinspection ALL
 */
public class AlternativeThumbnailsStatusPreference extends Preference {

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, str) -> {
        // Because this listener may run before the ReVanced settings fragment updates Settings,
        // this could show the prior config and not the current.
        //
        // Push this call to the end of the main run queue,
        // so all other listeners are done and Settings is up to date.
        Utils.runOnMainThread(this::updateUI);
    };

    public AlternativeThumbnailsStatusPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public AlternativeThumbnailsStatusPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public AlternativeThumbnailsStatusPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public AlternativeThumbnailsStatusPreference(Context context) {
        super(context);
    }

    private void addChangeListener() {
        Setting.preferences.preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private void removeChangeListener() {
        Setting.preferences.preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        updateUI();
        addChangeListener();
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        removeChangeListener();
    }

    private void updateUI() {
        Logger.printDebug(() -> "updateUI");
        final boolean usingDeArrow = Settings.ALT_THUMBNAIL_DEARROW.get();
        final boolean usingVideoStills = Settings.ALT_THUMBNAIL_STILLS.get();

        final String summaryTextKey;
        if (usingDeArrow && usingVideoStills) {
            summaryTextKey = "revanced_alt_thumbnail_about_status_dearrow_stills";
        } else if (usingDeArrow) {
            summaryTextKey = "revanced_alt_thumbnail_about_status_dearrow";
        } else if (usingVideoStills) {
            summaryTextKey = "revanced_alt_thumbnail_about_status_stills";
        } else {
            summaryTextKey = "revanced_alt_thumbnail_about_status_disabled";
        }

        setSummary(str(summaryTextKey));
    }
}