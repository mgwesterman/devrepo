/*
  Copyright 2017, Google, Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.mw.ocr.vision;

import com.google.cloud.vision.v1.AnnotateImageRequest;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Detect {

  /**
   * Detects entities,sentiment and syntax in a document using the Vision API.
   *
   * @throws Exception on errors while closing the client.
   * @throws IOException on Input/Output errors.
   */
  public static void main(String[] args) throws Exception, IOException {
    argsHelper(args, System.out);
  }

  /**
   * Helper that handles the input passed to the program.
   *
   * @throws Exception on errors while closing the client.
   * @throws IOException on Input/Output errors.
   */
  public static void argsHelper(String[] args, PrintStream out) throws Exception, IOException {
    if (args.length < 1) {
      out.println("Usage:");
      out.printf(
          "\tmvn exec:java -DDetect -Dexec.args=\"<command> <path-to-image>\"\n"
              + "Commands:\n"
              + "\tfaces | labels | landmarks | logos | text | safe-search | properties"
              + "| web | crop \n"
              + "Path:\n\tA file path (ex: ./resources/wakeupcat.jpg) or a URI for a Cloud Storage "
              + "resource (gs://...)\n");
      return;
    }
    String command = args[0];
    String path = args.length > 1 ? args[1] : "";

    Detect app = new Detect();
    if (command.equals("text")) {
        detectText(path, out);
    } else if (command.equals("isbn")) {
          app.detectIsbn(path);
    } else if (command.equals("lotto")) {
        app.detectLotto(path);
    }
  }


  /**
   * Detects text in the specified image.
   *
   * @param filePath The path to the file to detect text in.
   * @param out A {@link PrintStream} to write the detected text to.
   * @throws Exception on errors while closing the client.
   * @throws IOException on Input/Output errors.
   */
  public static void detectText(String filePath, PrintStream out) throws Exception, IOException {
    List<AnnotateImageRequest> requests = new ArrayList<>();

    ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

    Image img = Image.newBuilder().setContent(imgBytes).build();
    Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          out.printf("Error: %s\n", res.getError().getMessage());
          return;
        }

        // For full list of available annotations, see http://g.co/cloud/vision/docs
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
          out.printf("Text: %s\n", annotation.getDescription());
          out.printf("Position : %s\n", annotation.getBoundingPoly());
        }
      }
    }
  }

  /**
   * Detects an ISBN number from a specified image.
   *
   * @param filePath The path to the file to detect ISBN in.
   * @throws Exception on errors while closing the client.
   * @throws IOException on Input/Output errors.
   */
  public String detectIsbn(String filePath) throws Exception, IOException {
    
	  List<AnnotateImageRequest> requests = new ArrayList<>();

    ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

    Image img = Image.newBuilder().setContent(imgBytes).build();
    Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) 
    {
      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) 
      {
        if (res.hasError()) 
        {
          System.err.println("Error: %s\n" + res.getError().getMessage());
          throw new Exception("ERROR:" + res.getError().getMessage());
        }

        // For full list of available annotations, see http://g.co/cloud/vision/docs
        boolean isbn = false;
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) 
        {
        	String s = annotation.getDescription();
        	if (isbn)
        	{
        		System.out.println("return:" + s);
        		return s;
        	}
        	if (s.trim().equalsIgnoreCase("ISBN"))
        	{
        		isbn=true;
        	}
        }
      }
    }
    return null;
  }
  /**
   * Detects lottery numbers from a specified image.
   *
   * @param filePath The path to the file to detect ISBN in.
   * @throws Exception on errors while closing the client.
   * @throws IOException on Input/Output errors.
   */
  public String detectLotto(String filePath) throws Exception, IOException {
    
	  List<AnnotateImageRequest> requests = new ArrayList<>();

    ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

    Image img = Image.newBuilder().setContent(imgBytes).build();
    Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) 
    {
      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) 
      {
        if (res.hasError()) 
        {
          System.err.println("Error: %s\n" + res.getError().getMessage());
          throw new Exception("ERROR:" + res.getError().getMessage());
        }

        // regex to match a pattern of 5 two-digit numbers separate by spaces, plus
        //   a sixth number separated from the five by spaces and text
        //  \\d{2}\\s){4}\\d{2}.*\\d{2}"
        
        Pattern p = Pattern.compile("(\\d{2}\\s){4}\\d{2}.*\\d{2}");

        // For full list of available annotations, see http://g.co/cloud/vision/docs
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) 
        {
        	String s = annotation.getDescription();
      //      Matcher m = p.matcher(s);
            // find the string that has all the numbers in it and return that 
            //  #hack
      //      if (m.find()) 
      //      {
            	System.out.println("Detect returns:" + s);
      //          return s; 
       //     } 
        }
      }
    }
	System.out.println("Detect didn't find anything that looks like lotto numbers!");
    return null;
  }

}
