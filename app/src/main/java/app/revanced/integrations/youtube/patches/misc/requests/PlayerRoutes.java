package app.revanced.integrations.youtube.patches.misc.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import app.revanced.integrations.shared.requests.Requester;
import app.revanced.integrations.shared.requests.Route;
import app.revanced.integrations.shared.utils.Logger;
import app.revanced.integrations.shared.utils.PackageUtils;

public final class PlayerRoutes {
    public static final Route.CompiledRoute GET_STORYBOARD_SPEC_RENDERER = new Route(
            Route.Method.POST,
            "player" +
                    "?fields=storyboards.playerStoryboardSpecRenderer," +
                    "storyboards.playerLiveStoryboardSpecRenderer," +
                    "playabilityStatus.status," +
                    "playabilityStatus.errorScreen"
    ).compile();

    public static final String WEB_INNER_TUBE_BODY;
    public static final String ANDROID_INNER_TUBE_BODY;
    public static final String TV_EMBED_INNER_TUBE_BODY;

    private static final String YT_API_URL = "https://www.youtube.com/youtubei/v1/";

    /**
     * TCP connection and HTTP read timeout
     */
    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 4 * 1000; // 4 Seconds.

    static {
        JSONObject innerTubeBody = new JSONObject();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "ANDROID");
            client.put("clientVersion", PackageUtils.getVersionName());
            client.put("androidSdkVersion", 33);

            context.put("client", client);

            innerTubeBody.put("context", context);
            innerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create innerTubeBody", e);
        }

        ANDROID_INNER_TUBE_BODY = innerTubeBody.toString();

        JSONObject tvEmbedInnerTubeBody = new JSONObject();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "TVHTML5_SIMPLY_EMBEDDED_PLAYER");
            client.put("clientVersion", "2.0");
            client.put("platform", "TV");
            client.put("clientScreen", "EMBED");

            JSONObject thirdParty = new JSONObject();
            thirdParty.put("embedUrl", "https://www.youtube.com/watch?v=%s");

            context.put("thirdParty", thirdParty);
            context.put("client", client);

            tvEmbedInnerTubeBody.put("context", context);
            tvEmbedInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create tvEmbedInnerTubeBody", e);
        }

        TV_EMBED_INNER_TUBE_BODY = tvEmbedInnerTubeBody.toString();

        JSONObject webInnerTubeBody = new JSONObject();

        try {
            JSONObject context = new JSONObject();

            JSONObject client = new JSONObject();
            client.put("clientName", "WEB");
            client.put("clientVersion", "2.20240201.01.00");
            client.put("clientScreen", "WATCH");

            context.put("client", client);

            webInnerTubeBody.put("context", context);
            webInnerTubeBody.put("videoId", "%s");
        } catch (JSONException e) {
            Logger.printException(() -> "Failed to create webInnerTubeBody", e);
        }

        WEB_INNER_TUBE_BODY = webInnerTubeBody.toString();
    }

    private PlayerRoutes() {
    }

    /**
     * @noinspection SameParameterValue
     */
    public static HttpURLConnection getPlayerResponseConnectionFromRoute(Route.CompiledRoute route) throws IOException {
        var connection = Requester.getConnectionFromCompiledRoute(YT_API_URL, route);

        connection.setRequestProperty(
                "User-Agent", "com.google.android.youtube/" +
                        PackageUtils.getVersionName() +
                        "(Linux; U; Android 13; en_US; sdk_gphone64_x86_64 Build/UPB4.230623.005) gzip"
        );
        connection.setRequestProperty("X-Goog-Api-Format-Version", "2");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        connection.setConnectTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
        connection.setReadTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
        return connection;
    }
}