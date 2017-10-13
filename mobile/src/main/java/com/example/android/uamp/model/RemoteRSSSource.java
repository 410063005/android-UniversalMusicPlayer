package com.example.android.uamp.model;

import android.support.v4.media.MediaMetadataCompat;

import com.example.android.uamp.utils.LogHelper;
import com.icosillion.podengine.exceptions.InvalidFeedException;
import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kingcmchen on 2017/10/13.
 */

public class RemoteRSSSource implements MusicProviderSource {
    private static final String TAG = LogHelper.makeLogTag(RemoteRSSSource.class);

    protected static final String CATALOG_URL =
            "http://feed.thisamericanlife.org/talpodcast";

    @Override
    public Iterator<MediaMetadataCompat> iterator() {
        try {
            Podcast podcast = new Podcast(new URL(CATALOG_URL));
            List<Episode> episodeList = podcast.getEpisodes();
            ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();

            if (episodeList != null) {
                for (Episode episode : episodeList) {
                    tracks.add(buildFromRSS(podcast, episode));
                }
            }
            return tracks.iterator();
        } catch (InvalidFeedException | MalformedFeedException | MalformedURLException e) {
            LogHelper.e(TAG, e, "Could not retrieve music list");
            throw new RuntimeException(e);
        }
    }

    private MediaMetadataCompat buildFromRSS(Podcast podcast, Episode episode) throws MalformedFeedException, MalformedURLException {
        String title = episode.getTitle();
        String album = podcast.getTitle();
        String artist = episode.getAuthor();
        String genre = "Podcast";
        String source = episode.getEnclosure().getURL().toString();
        String iconUrl = "https://www.thisamericanlife.org/sites/all/themes/thislife/images/logo-square-1400.jpg"; // TODO podcast.getImageURL().toString();
        int trackNumber = 2;
        int totalTrackCount = 6;
        int duration = parseDuration(episode.getITunesInfo().getDuration()) * 1000; // ms

        // Since we don't have a unique ID in the server, we fake one using the hashcode of
        // the music source. In a real world app, this could come from the server.
        String id = String.valueOf(source.hashCode());

        // Adding the music source to the MediaMetadata (and consequently using it in the
        // mediaSession.setMetadata) is not a good idea for a real world music app, because
        // the session metadata can be accessed by notification listeners. This is done in this
        // sample for convenience only.
        //noinspection ResourceType
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
                .putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, source)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, iconUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, totalTrackCount)
                .build();
    }

    static int parseDuration(String durationStr) {
        String[] tmp = durationStr.split(":");
        int duration = 0;
        int multi = 1;
        for (int i = tmp.length - 1; i >= 0; i--) {
            int e = Integer.parseInt(tmp[i]);
            duration += e * multi;
            multi *= 60;
        }
        return duration;
    }
}
