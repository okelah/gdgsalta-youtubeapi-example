/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.srg.gdgsalta.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Joiner;
import com.google.api.services.samples.youtube.cmdline.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.GeoPoint;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author sebas
 */
public class YoutubeApiImpl {

    //Global instance App Name.
    private static final String APP_NAME = "PONER TU APP NAME AQUI";
    //Global instance Api Key.     
    private static final String API_KEY = "PONER TU API KEY AQUI";
    //Global instance of the HTTP transport.
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    //Global instance of the JSON factory.     
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    //Global instance of the max number of videos we want returned (50 = upper limit per page).  
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
    //Global instance of Youtube object to make all API requests.
    private static YouTube youtube;

    public List<VideoWrapper> searchVideos(String querySearch) {
        SearchListResponse searchResponse = null;
        try {
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                    new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            })
                    .setApplicationName(APP_NAME).build();

            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setKey(API_KEY);
            search.setQ(querySearch);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            searchResponse = search.execute();
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (searchResponse != null) {
            return wrapResults(searchResponse.getItems().iterator());
        }
        return null;
    }

    public List<VideoWrapper> geoSearch(String querySearch, Double latitud, Double longitud, int radius) {
        List<Video> videoList = null;
        try {
//            youtube = new YouTube.Builder(
//                    HTTP_TRANSPORT,
//                    JSON_FACTORY,
//                    new HttpRequestInitializer() {
//                @Override
//                public void initialize(HttpRequest request) throws IOException {
//                }
//            }).setApplicationName(APP_NAME).build();

            List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
            Credential credential = Auth.authorize(scopes, "liststreams");
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("gdgsalta-1314").build();

            String location = latitud + "," + longitud;
            String locationRadius = radius + "km";

            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setKey(API_KEY);
            search.setQ(querySearch);
            search.setLocation(location);
            search.setLocationRadius(locationRadius);
            search.setType("video");
            //search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setFields("items(id/videoId)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            SearchListResponse searchResponse = search.execute();

            List<SearchResult> searchResultList = searchResponse.getItems();
            List<String> videoIds = new ArrayList<String>();
            if (searchResultList != null) {
                // Merge video IDs
                for (SearchResult searchResult : searchResultList) {
                    videoIds.add(searchResult.getId().getVideoId());
                }
                Joiner stringJoiner = Joiner.on(',');
                String videoId = stringJoiner.join(videoIds);

                YouTube.Videos.List listVideosRequest = youtube.videos()
                        .list("snippet, recordingDetails").setId(videoId);
                VideoListResponse listResponse = listVideosRequest.execute();

                videoList = listResponse.getItems();
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (videoList != null) {
            return wrapGeoResults(videoList.iterator());
        }
        return null;
    }

    private List<VideoWrapper> wrapResults(Iterator<SearchResult> listResult) {
        List<VideoWrapper> list = new ArrayList<VideoWrapper>();
        while (listResult.hasNext()) {
            VideoWrapper video = new VideoWrapper();
            SearchResult singleVideo = listResult.next();
            //SearchResult singleVideo = listResult.next();
            ResourceId rId = singleVideo.getId();
            // Double checks the kind is video.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                video.setId(rId.getVideoId());
                video.setTitle(singleVideo.getSnippet().getTitle());
                video.setThumbnailUrl(thumbnail.getUrl());
                video.setDescription(singleVideo.getSnippet().getDescription());
                list.add(video);
            }
        }
        return list;
    }

    private List<VideoWrapper> wrapGeoResults(Iterator<Video> listResult) {
        List<VideoWrapper> list = new ArrayList<VideoWrapper>();
        while (listResult.hasNext()) {
            VideoWrapper video = new VideoWrapper();
            Video singleVideo = (Video) listResult.next();
            Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
            GeoPoint location = singleVideo.getRecordingDetails().getLocation();

            video.setId(singleVideo.getId());
            video.setTitle(singleVideo.getSnippet().getTitle());
            video.setThumbnailUrl(thumbnail.getUrl());
            video.setDescription(singleVideo.getSnippet().getDescription());
            video.setLatitude(location.getLatitude());
            video.setLongitude(location.getLongitude());
            list.add(video);
        }
        return list;
    }
}
