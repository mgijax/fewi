package org.jax.mgi.fewi.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

  /* read the contents of 'file' and return them as a newline-delimited
   * String.  Returns null if 'file' does not exist in the file system.
   */
  public static String readFile (File file) throws IOException 
  {
    if (!file.exists()) {
      return null;
    }

    String line = null;
    StringBuffer contents = new StringBuffer();

    BufferedReader inFile = new BufferedReader(new FileReader(file));

    while ((line = inFile.readLine()) != null) {
      contents.append (line);
      contents.append ("\n");
    }
    if(contents.length()>0)
    {
    	contents.deleteCharAt(contents.length()-1); // remove last \n
    }
    inFile.close();

    return contents.toString();
  }
}
