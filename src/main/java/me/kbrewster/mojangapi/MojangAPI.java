package me.kbrewster.mojangapi;

import com.google.gson.*;
import me.kbrewster.API;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.authentication.AuthenticatedUser;
import me.kbrewster.mojangapi.profile.Model;
import me.kbrewster.mojangapi.profile.Name;
import me.kbrewster.mojangapi.profile.Profile;
import me.kbrewster.mojangapi.stats.MetricKeys;
import me.kbrewster.mojangapi.stats.MojangStatistics;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Allows access to the MojangAPI
 * http://wiki.vg/Mojang_API
 */
@API.Reference(apiName = "Mojang API", apiVersion = "1.1.4")
public class MojangAPI extends API {

    private final static String BASE_URL = "https://api.mojang.com";
    private final static String STATUS_URL = "https://status.mojang.com/check";

    private final static Pattern STRIPPED_UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    private final static Pattern UUID_PATTERN = Pattern.compile("(\\w{8})-(\\w{4})-(\\w{4})-(\\w{4})-(\\w{12})");

    public static Map<String, String> getStatus() throws IOException {
        Map<String, String> map = new TreeMap<>();
        String json = sendGet(STATUS_URL);
        JsonArray status = new JsonParser().parse(json).getAsJsonArray();
        for(JsonElement element: status) {
            JsonObject obj = element.getAsJsonObject();
            obj.entrySet().forEach(s -> map.put(s.getKey(), s.getValue().getAsString()));
        }
        return map;
    }

    public static Profile getProfile(UUID uuid) throws IOException, APIException {
        String json = sendGet("https://sessionserver.mojang.com/session/minecraft/profile/" + stripDashes(uuid));
        JsonElement jsonElement = new JsonParser().parse(json);
        if (jsonElement.isJsonNull()) throw new APIException("This should'nt happen");
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.get("error") instanceof JsonNull)
            throw new APIException(jsonObject.get("errorMessage").getAsString());
        return new Gson().fromJson(jsonObject, Profile.class);
    }

    public static String getName(UUID uuid) throws IOException {
        ArrayList<Name> names = getNameHistory(uuid);
        return names.get(names.size() - 1).getName(); // i mean i could get it vai profile, but im hacky af
    }

    public static String stripDashes(String uuid) {
        return uuid.replaceAll("-", "");
    }

    public static String stripDashes(UUID uuid) {
        return stripDashes(uuid.toString());
    }

    public static String addDashes(String uuid) {
        return STRIPPED_UUID_PATTERN.matcher(uuid).replaceAll("$1-$2-$3-$4-$5");
    }

    public static ArrayList<Name> getNameHistory(String username) throws IOException, APIException {
        UUID uuid = getUUID(username);
        return getNameHistory(uuid);
    }

    public static ArrayList<Name> getNameHistory(UUID uuid) throws IOException {
        Gson gson = new Gson();
        ArrayList<Name> names = new ArrayList<>();
        System.out.println(stripDashes(uuid));
        String json = sendGet(String.format(BASE_URL + "/user/profiles/%s/names", stripDashes(uuid)));
        System.out.println(json);
        JsonElement parser = new JsonParser().parse(json);
        if (json.isEmpty()) throw new InvalidPlayerException();
        JsonArray arrayNames = parser.getAsJsonArray();
        arrayNames.forEach(obj -> {
            Name name = gson.fromJson(obj, Name.class);
            names.add(name);
        });
        return names;
    }

    public static UUID getUUID(String username) throws IOException, APIException, InvalidPlayerException {
        String json = sendGet("https://api.mojang.com/users/profiles/minecraft/" + username);
        JsonElement parse = new JsonParser().parse(json);
        if (parse.isJsonNull())
            throw new InvalidPlayerException();
        JsonObject obj = parse.getAsJsonObject();
        if(obj.get("error") instanceof JsonNull)
            throw new APIException(obj.get("errorMessage").getAsString());
        return UUID.fromString(addDashes(obj.get("id").getAsString()));
    }

    public static MojangStatistics getStatistics(MetricKeys key) throws IOException {
        String json = sendPost("https://api.mojang.com/orders/statistics", "{\"metricKeys\": [\"" + key.getKey() + "\"]}");
        JsonElement obj = new JsonParser().parse(json);
        return new Gson().fromJson(obj, MojangStatistics.class);
    }

    public static void resetSkin(String sessionid, UUID uuid) throws IOException, APIException {
        Request request = new Request.Builder()
                .url("https://api.mojang.com/user/profile/" + stripDashes(uuid) + "/skin")
                .header("Authorization", "Bearer " + sessionid)
                .delete()
                .build();

        String output = new OkHttpClient().newCall(request).execute().body().string();
        if (!output.isEmpty()) {
            throw new APIException(output);
        }

    }

    public static void changeSkin(String accessToken, UUID uuid, Model model, String url) throws IOException, APIException {
        String payload = String.format("model=%s&url=%s", model.getModel(), url);
        MediaType MEDIA_TYPE = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(MEDIA_TYPE, payload);
        Request request = new Request.Builder()
                .url("https://api.mojang.com/user/profile/" + stripDashes(uuid) + "/skin")
                .header("Authorization", "Bearer " + accessToken)
                .post(body)
                .build();

        String output = new OkHttpClient().newCall(request).execute().body().string();
        if (!output.isEmpty()) {
            throw new APIException(output);
        }
    }

    public static void uploadSkin(String accessToken, UUID uuid, Model model, File file) throws IOException, APIException {
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), file))
                .addFormDataPart("model", model.getModel())
                .build();

        Request request = new Request.Builder()
                .url("https://api.mojang.com/user/profile/" + stripDashes(uuid) + "/skin")
                .header("Authorization", "Bearer " + accessToken)

                .put(body)
                .build();

        String output = new OkHttpClient().newCall(request).execute().body().string();
        if (!output.isEmpty()) {
            throw new APIException(output);
        }

    }

    public static AuthenticatedUser authenticate(String username, String password, String token, boolean requestUser) throws IOException, APIException {
        JsonObject payload = new JsonObject();
        JsonObject agent = new JsonObject();
        agent.addProperty("name", "minecraft");
        agent.addProperty("version", 1);
        payload.add("agent", agent);
        payload.addProperty("username", username);
        payload.addProperty("password", password);
        if (token != null) payload.addProperty("clientToken", token); //optional
        if (requestUser) payload.addProperty("requestUser", true); //optional
        String response = sendPost("https://authserver.mojang.com/authenticate", payload.toString());
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        if (json.get("error") instanceof JsonNull)
            throw new APIException(json.get("errorMessage").getAsString());
        return new Gson().fromJson(json, AuthenticatedUser.class);
    }


    public static void resetSkin(String accessToken, String username) throws IOException, APIException {
        resetSkin(accessToken, getUUID(username));
    }

    public static void changeSkin(String accessToken, String username, Model model, String url) throws IOException, APIException {
        changeSkin(accessToken, getUUID(username), model, url);
    }

    public static void uploadSkin(String accessToken, String username, Model model, File skin) throws IOException, APIException {
        uploadSkin(accessToken, getUUID(username), model, skin);
    }

    public static AuthenticatedUser authenticate(String username, String password, boolean requestUser) throws IOException, APIException {
        return authenticate(username, password, null, requestUser);
    }

    public static Profile getProfile(String username) throws IOException, APIException {
        return getProfile(getUUID(username));
    }


}