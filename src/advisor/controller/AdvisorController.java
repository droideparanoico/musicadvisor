package advisor.controller;

import advisor.model.AdvisorModel;
import advisor.config.Params;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class AdvisorController {
    private static String resourceServer;

    public AdvisorController(String resourceServer) {
        AdvisorController.resourceServer = resourceServer;
    }

    public List<AdvisorModel> getNewReleases() throws IOException, InterruptedException {
        List<AdvisorModel> newReleases = new ArrayList<>();
        HttpRequest newReleasesRequest = createRequest(resourceServer + Params.NEW_RELEASES);
        for (JsonElement item: Objects.requireNonNull(getJsonItems(newReleasesRequest, "albums"))) {
            AdvisorModel album = new AdvisorModel();
            album.setAlbum(item.getAsJsonObject().get("name").toString());
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            for (JsonElement artist: item.getAsJsonObject().getAsJsonArray("artists")) {
                String artistName = artist.getAsJsonObject().get("name").getAsString();
                joiner.add(artistName);
            }
            album.setArtists(String.valueOf(joiner));
            album.setLink(item.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").toString());
            newReleases.add(album);
        }
        return newReleases;
    }

    public List<AdvisorModel> getFeaturedPlaylists() throws IOException, InterruptedException {
        List<AdvisorModel> featuredPlaylists = new ArrayList<>();
        HttpRequest featuredPlaylistsRequest = createRequest(resourceServer + Params.FEATURED_PLAYLISTS);
        return getPlaylists(featuredPlaylists, featuredPlaylistsRequest);
    }

    private List<AdvisorModel> getPlaylists(List<AdvisorModel> categoryPlaylists, HttpRequest categoryPlaylistsRequest) throws IOException, InterruptedException {
        for (JsonElement item: Objects.requireNonNull(getJsonItems(categoryPlaylistsRequest, "playlists"))) {
            AdvisorModel categoryPlaylist = new AdvisorModel();
            categoryPlaylist.setAlbum(item.getAsJsonObject().get("name").toString());
            categoryPlaylist.setLink(item.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").toString());
            categoryPlaylists.add(categoryPlaylist);
        }
        return categoryPlaylists;
    }

    public List<AdvisorModel> getCategories() throws IOException, InterruptedException {
        List<AdvisorModel> categories = new ArrayList<>();
        HttpRequest categoriesRequest = createRequest(resourceServer + Params.CATEGORIES);
        for (JsonElement item: Objects.requireNonNull(getJsonItems(categoriesRequest, "categories"))) {
            AdvisorModel category = new AdvisorModel();
            category.setAlbum(item.getAsJsonObject().get("name").toString());
            categories.add(category);
        }
        return categories;
    }

    public List<AdvisorModel> getCategoryPlaylists(String categoryName) throws IOException, InterruptedException {
        List<AdvisorModel> categoryPlaylists = new ArrayList<>();
        String categoryID = getCategoryIdByCategoryName(categoryName);
        HttpRequest categoryPlaylistsRequest =
                createRequest(resourceServer + Params.CATEGORIES + "/" + categoryID + Params.PLAYLISTS);
        return (categoryID == null) ? null : getPlaylists(categoryPlaylists, categoryPlaylistsRequest);
    }

    private String getCategoryIdByCategoryName(String categoryName) throws IOException, InterruptedException {
        HttpRequest categories = createRequest(resourceServer + Params.CATEGORIES);
        JsonArray items = getJsonItems(categories, "categories");
        assert items != null;
        for(JsonElement item: items){
            String name = item.getAsJsonObject().get("name").getAsString();
            if(categoryName.equalsIgnoreCase(name)){
                return item.getAsJsonObject().get("id").getAsString();
            }
        }
        return null;
    }

    private JsonArray getJsonItems(HttpRequest request, String element) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        if (responseJson.get("error") == null){
            JsonObject featured = responseJson.getAsJsonObject(element);
            return featured.getAsJsonArray("items");
        }
        System.out.println(responseJson.getAsJsonObject("error").get("message").getAsString());
        return null;
    }

    private static HttpRequest createRequest(String requestedFeatureURL) {
        return HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + Params.TOKEN_CODE)
                .uri(URI.create(requestedFeatureURL))
                .GET()
                .build();
    }
}