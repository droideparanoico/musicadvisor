package advisor.model;

public class AdvisorModel {
    String album = null;
    String artists = null;
    String link = null;

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtists(String authors) {
        this.artists = authors;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        if (album != null) { info.append(album).append("\n"); }
        if (artists != null) { info.append(artists).append("\n"); }
        if (link != null) { info.append(link).append("\n"); }
        return info.toString().replaceAll("\"", "");
    }
}
