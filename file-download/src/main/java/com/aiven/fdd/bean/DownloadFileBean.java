package com.aiven.fdd.bean;

public class DownloadFileBean {

    protected String url;
    protected String filename;

    public DownloadFileBean(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String toJson() {
        return "{\"url\": \"" + url + "\"" +
                "\"filename\": \"" + filename + "\"}"
                ;
    }
}
