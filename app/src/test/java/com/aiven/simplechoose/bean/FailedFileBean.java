package com.aiven.simplechoose.bean;

public class FailedFileBean extends DownloadFileBean {

    private String reason;

    public FailedFileBean(
            String url,
            String filename,
            String reason
    ) {
        super(url, filename);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toJson() {
        return "{\"url\": \"" + url + "\"" +
                "\"filename\": \"" + filename + "\"" +
                "\"reson\": \"" + reason + "\"}"
                ;
    }
}
