package app.revanced.integrations.youtube.patches.components;

import static app.revanced.integrations.youtube.utils.ReVancedUtils.hideViewBy0dpUnderCondition;

import android.view.View;

import androidx.annotation.Nullable;

import app.revanced.integrations.youtube.patches.utils.NavBarIndexPatch;
import app.revanced.integrations.youtube.settings.SettingsEnum;

/**
 * @noinspection rawtypes
 */
@SuppressWarnings("unused")
public final class SuggestionsShelfFilter extends Filter {
    private final StringFilterGroup horizontalShelf;
    private final StringFilterGroup libraryShelf;
    private final StringFilterGroup searchResult;

    public SuggestionsShelfFilter() {
        horizontalShelf = new StringFilterGroup(
                SettingsEnum.HIDE_SUGGESTIONS_SHELF,
                "horizontal_shelf.eml",
                "horizontal_tile_shelf.eml",
                "horizontal_video_shelf.eml"
        );

        libraryShelf = new StringFilterGroup(
                SettingsEnum.HIDE_SUGGESTIONS_SHELF,
                "library_recent_shelf.eml"
        );

        searchResult = new StringFilterGroup(
                SettingsEnum.HIDE_SUGGESTIONS_SHELF,
                "compact_channel.eml",
                "search_video_with_context.eml"
        );

        identifierFilterGroupList.addAll(libraryShelf, searchResult);
        pathFilterGroupList.addAll(horizontalShelf);
    }

    /**
     * Injection point.
     * <p>
     * Only used to tablet layout.
     */
    public static void hideBreakingNewsShelf(View view) {
        hideViewBy0dpUnderCondition(
                SettingsEnum.HIDE_SUGGESTIONS_SHELF.getBoolean(),
                view
        );
    }

    @Override
    boolean isFiltered(String path, @Nullable String identifier, String allValue, byte[] protobufBufferArray,
                       FilterGroupList matchedList, FilterGroup matchedGroup, int matchedIndex) {
        // Even though [NavBarIndex] has not been set yet, but [LithoFilterPatch] can be called.
        // In this case, the patch may not work normally.
        // To prevent this, you need to detect a specific component that exists only in some [NavBarIndex],
        // And manually update the [NavBarIndex].
        if (matchedGroup == searchResult) {
            NavBarIndexPatch.setNavBarIndex(0);
        } else if (matchedGroup == libraryShelf) {
            NavBarIndexPatch.setNavBarIndex(4);
        } else if (matchedGroup == horizontalShelf) {
            return NavBarIndexPatch.isNotLibraryTab();
        }
        return false;
    }
}
