/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.srg.gdgsalta.beans;

import com.google.api.services.youtube.model.SearchResult;
import com.srg.gdgsalta.youtube.VideoWrapper;
import com.srg.gdgsalta.youtube.YoutubeApiImpl;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;


/**
 *
 * @author sebas
 */
@Named(value = "buscadorBean")
@ViewScoped
public class BuscadorBean implements Serializable{
    
    private String searchPhrase = "";
    private List<VideoWrapper> videosList;  
    private VideoWrapper selectedVideo;
    
    YoutubeApiImpl youtubeApi;
    
    public BuscadorBean() {
    }
    
    @PostConstruct
    private void initBean(){
        youtubeApi = new YoutubeApiImpl();
    }
    
    public void searchVideos(){            
        videosList = youtubeApi.searchVideos(searchPhrase);        
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
}
