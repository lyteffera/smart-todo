package com.andrewrs.sps.data;

import com.google.appengine.api.blobstore.BlobKey;

public class BlobKeyValuePair{
    private BlobKey key;
    private String url;
    public BlobKeyValuePair(BlobKey key,String url)
    {
        this.key = key;
        this.url = url;
    }
    public BlobKey getKey()
    {
        return key;
    }
    public String getValue()
    {
        return url;
    }
}