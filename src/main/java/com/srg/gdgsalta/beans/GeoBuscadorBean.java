/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.srg.gdgsalta.beans;

import com.srg.gdgsalta.youtube.VideoWrapper;
import com.srg.gdgsalta.youtube.YoutubeApiImpl;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author sebas
 */
@Named(value = "geoBuscadorBean")
@ViewScoped
public class GeoBuscadorBean implements Serializable {

    private String searchPhrase = "";
    private List<VideoWrapper> videosList;
    private VideoWrapper selectedVideo;
    private YoutubeApiImpl youtubeApi;
    private MapModel simpleModel;
    private double lat;
    private double lng;

    public GeoBuscadorBean() {
    }

    @PostConstruct
    private void initBean() {
        youtubeApi = new YoutubeApiImpl();
        simpleModel = new DefaultMapModel();
    }

    public void searchVideos() {
        System.out.println("Lat: " + this.lat);
        System.out.println("Long: " + this.lng);

        videosList = youtubeApi.geoSearch(searchPhrase, lat, lng, 500);
        if (videosList != null) {
            System.out.println("Size list:" + videosList.size());
            for (VideoWrapper v : videosList) {
                LatLng coord = new LatLng(v.getLatitude(), v.getLongitude());
                simpleModel.addOverlay(new Marker(coord, v.getTitle()));
            }
        }
    }

    //*********gets y sets************
    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public List<VideoWrapper> getVideosList() {
        return videosList;
    }

    public void setVideosList(List<VideoWrapper> videosList) {
        this.videosList = videosList;
    }

    public VideoWrapper getSelectedVideo() {
        return selectedVideo;
    }

    public void setSelectedVideo(VideoWrapper selectedVideo) {
        this.selectedVideo = selectedVideo;
    }

    public MapModel getSimpleModel() {
        return simpleModel;
    }

    public void setSimpleModel(MapModel simpleModel) {
        this.simpleModel = simpleModel;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
