package com.example.android.uamp.model;

import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by kingcmchen on 2017/10/13.
 */
public class RemoteRSSSourceTest {
    @Test
    public void iterator() throws Exception {
        InputStream is = new URL(RemoteRSSSource.CATALOG_URL).openConnection(
                new Proxy(Proxy.Type.HTTP, new InetSocketAddress("dev-proxy.oa.com", 8080))).getInputStream();
        String xmlData = IOUtils.toString(is, Charset.defaultCharset());
        IOUtils.closeQuietly(is);

        Podcast podcast = new Podcast(xmlData);
        List<Episode> episodeList = podcast.getEpisodes();

        if (episodeList != null) {
            for (Episode episode : episodeList) {

                System.out.println(episode.getAuthor()
                        + "\n" + episode.getTitle()
                        + "\n" + episode.getDescription()
                        //+ "\n" + episode.getEnclosure().getLength()
                        + "\n" + episode.getEnclosure().getURL().toString()
                        + "\n" + episode.getITunesInfo().getDuration()
                        + "\n" + podcast.getImageURL().toString()
                        + "\n\n"
                );
            }
        }
    }

    @Test
    public void parseDuration() {
        assertEquals(1 * 3600 + 10 * 60, RemoteRSSSource.parseDuration("01:10:00"));
        assertEquals(1 * 3600 + 0 * 60 + 55, RemoteRSSSource.parseDuration("01:00:55"));
        assertEquals(1 * 3600 + 1 * 60 + 13, RemoteRSSSource.parseDuration("01:01:13"));
        assertEquals(1 * 60 + 13, RemoteRSSSource.parseDuration("01:13"));
    }

}