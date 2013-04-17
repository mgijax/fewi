package org.jax.mgi.fewi.util;

import java.util.*;
import java.io.*;

/**
 * provides an easy mechanism to read text files from the file system into a
 * String
 */
public class TextFileReader
{
  //----------------------//
  // public static methods
  //----------------------//

  // convenience wrapper to allow reading a file by specifying its path as a
  // String
  public static String readFile (String fullPath) throws IOException {
    return readFile(new File(fullPath));
  } 

  // read the contents of 'myFile' and return them as a newline-delimited
  // String.  Returns null if 'myFile' does not exist in the file system.
  public static String readFile (File myFile) throws IOException {

    if (!myFile.exists()) {
      return null;
    }

    String line = null;
    StringBuffer contents = new StringBuffer();

    BufferedReader inFile = new BufferedReader(new FileReader(myFile));

    while ((line = inFile.readLine()) != null) {
      contents.append (line);
      contents.append ("\n");
    }
    inFile.close();

    return contents.toString();
  }
}
