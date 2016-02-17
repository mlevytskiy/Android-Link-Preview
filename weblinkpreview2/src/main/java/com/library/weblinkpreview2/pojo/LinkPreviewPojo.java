package com.library.weblinkpreview2.pojo;

/**
 * Created by max on 15.02.16.
 */
public class LinkPreviewPojo {

    private String baseUrl;
    private String title;
    private String description;
    private String imageUrl;
    private LoadingStatus loadingStatus = LoadingStatus.empty;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     *  This method not thread-save
     */
    public LoadingStatus getLoadingStatus() {
        return loadingStatus;
    }

    /**
     *  This method not thread-save
     */
    public void setLoadingStatus(LoadingStatus loadingStatus) {
        this.loadingStatus = loadingStatus;
    }

}
