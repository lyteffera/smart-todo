package com.andrewrs.sps.utils;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
//vision api utils
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
//blobstore utils
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;


import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import com.andrewrs.sps.data.BlobKeyValuePair;

public class BlobStoreUtils {

public final BlobstoreService blobstoreService;

  private ImagesService imagesService;
  public BlobStoreUtils(BlobstoreService blobstoreService,ImagesService imagesService)
  {
      this.blobstoreService = blobstoreService;
      this.imagesService = imagesService;
  }
  public BlobStoreUtils(BlobstoreService blobstoreService)
  {
      this.blobstoreService = blobstoreService;
      imagesService = ImagesServiceFactory.getImagesService();
  }
  public BlobStoreUtils()
  {
      this.blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      imagesService = ImagesServiceFactory.getImagesService();
  }
  /**
   * Blobstore stores files as binary data. This function retrieves the binary data stored at the
   * BlobKey parameter.
   */
  public byte[] getBlobBytes(BlobKey blobKey) throws IOException 
  {
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

    int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
    long currentByteIndex = 0;
    boolean continueReading = true;
    while (continueReading) 
    {
      // end index is inclusive, so we have to subtract 1 to get fetchSize bytes
      byte[] b =
          blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
      outputBytes.write(b);

      // if we read fewer bytes than we requested, then we reached the end
      if (b.length < fetchSize) 
      {
        continueReading = false;
      }

      currentByteIndex += fetchSize;
    }

    return outputBytes.toByteArray();
  }

    /** Returns an object that has the URL and the BlobKey that points to the uploaded file, or null if the user didn't upload a file. */
  public BlobKeyValuePair getUploadedFileIdentifyingInfo(HttpServletRequest request, String formInputElementName) {

    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) 
    {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) 
    {
      blobstoreService.delete(blobKey);
      return null;
    }

    // We could check the validity of the file here, e.g. to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    String url = imagesService.getServingUrl(options);

    // GCS's localhost preview is not actually on localhost,
    // so make the URL relative to the current domain.
    if(url.startsWith("http://localhost:8080/"))
    {
      url = url.replace("http://localhost:8080/", "/");
    }
    return new BlobKeyValuePair(blobKey,url);
  }
}
