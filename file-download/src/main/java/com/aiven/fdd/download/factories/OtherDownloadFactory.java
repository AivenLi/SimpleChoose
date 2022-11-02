package com.aiven.fdd.download.factories;

public class OtherDownloadFactory extends BaseDownloadFactory {

    @Override
    public void startDownload() {

    }

    @Override
    public void stopDownload(boolean clearFiles) {

    }

    @Override
    public boolean isDownload() {
        return false;
    }
}
