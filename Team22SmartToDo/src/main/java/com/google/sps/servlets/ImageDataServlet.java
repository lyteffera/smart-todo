
package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.PrintWriter;
//blobstore utils
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.andrewrs.sps.utils.BlobStoreUtils;
//Google Cloud Vision 
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.andrewrs.sps.utils.VisionApiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.andrewrs.sps.data.ListRecord;
import com.andrewrs.sps.utils.StringUtil;
import com.andrewrs.sps.data.BlobKeyValuePair;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/image_handler")
public class ImageDataServlet extends HttpServlet {
  private DatastoreService datastore;
  private BlobstoreService blobstoreService;
  private VisionApiUtils visionApi;
  private BlobStoreUtils blobUtils;
  public void init()
  {
    datastore = DatastoreServiceFactory.getDatastoreService();
    blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    blobUtils = new BlobStoreUtils(blobstoreService);
    visionApi = new VisionApiUtils();
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException 
  {
    // Get the Blobstore URL
    String uploadUrl = blobstoreService.createUploadUrl("/image_handler");
    System.out.println("\n\nUpload URL to BlobStore: "+ uploadUrl +"\n");
    response.setContentType("text/html");

    // This demonstrates creating a form that uses the Blobstore URL.
    // This is not how you'd do this in a real codebase!
    // See the hello-world-jsp or hello-world-fetch examples for more info.
    response.getWriter().println(
        "<form method=\"POST\" enctype=\"multipart/form-data\" action=\"" + uploadUrl + "\">");


    response.getWriter().println("<p>Or Upload a Handwritten list or text document to be automatically added:</p>");
    response.getWriter().println("<input type=\"file\" name=\"image\">");
    response.getWriter().println("<br/><br/>");

    response.getWriter().println("<button>Submit</button>");
    response.getWriter().println("</form>");
    response.getWriter().println("<p>*Please note each new line will be considered a new to do list item.</p>");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("text/html;");
    // Get the URL of the image that the user uploaded to Blobstore.
    BlobKeyValuePair imageInfo = blobUtils.getUploadedFileIdentifyingInfo(request, "image");
    String imageUrl = imageInfo.getValue();
    
    // Get the text of the image that the user uploaded.
    String text = visionApi.getEntireText(blobUtils.getBlobBytes(imageInfo.getKey()));
    blobstoreService.delete(imageInfo.getKey());
    //text = te.replaceAll("\\\\","\\");
    String[] messages = text.split("\\\\n");

    for(String message: messages)
    {
      Entity entity = new Entity("message_log");
      entity.setProperty("timeStamp", System.currentTimeMillis());
      entity.setProperty("message", StringUtil.escapeQuotesInParameter(message));
      datastore.put(entity);
    }


    response.sendRedirect("/todo.html");
  }

  
}
