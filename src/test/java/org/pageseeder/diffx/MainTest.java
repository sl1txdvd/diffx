/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.test.TestHandler;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.test.TestUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Test class for the Main class, uses the files that are stored in the config directories.
 *
 * <p>All use cases are in:
 * <pre>
 *   /data/org/pageseeder/diffx/Main/source
 * </pre>
 *
 * <p>To add a new use case:
 * 1. create a directory starting with 'UC-'
 * 2. add the two files to compare as 'a.xml' and 'b.xml'
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class MainTest {

  /**
   * The source files to test
   */
  private static final File source = TestUtils.getDataDirectory(Main.class);
  /**
   * The folder containing the results.
   */
  private static final File tmp = TestUtils.getTempDirectory(Main.class);
  /**
   * Folder containing the resulting Diff XML.
   */
  private static final File result = new File(tmp, "result");

  /**
   * The diff-X configuration.
   */
  private static final DiffConfig config = DiffConfig.getDefault()
      .whitespace(WhiteSpaceProcessing.IGNORE)
      .granularity(TextGranularity.WORD);

  /**
   * The XML reader.
   */
  private static XMLReader reader;

  /**
   * Initialises the XML reader.
   *
   * @throws SAXException If one of the features could not be set.
   */
  @BeforeAll
  public static void setUpXMLReader() throws SAXException {
    reader = XMLReaderFactory.createXMLReader();
    reader.setFeature("http://xml.org/sax/features/validation", false);
    reader.setFeature("http://xml.org/sax/features/namespaces", true);
    reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);

  }

  /**
   * Ensures that all required folders are created.
   */
  @BeforeAll
  public static void setUpFolders() {
    if (!result.exists()) result.mkdirs();
  }

  /**
   * Tests all the use cases located in:
   * <pre>
   *   /src/test/resources/main
   * </pre>
   *
   * @throws IOException Should an I/O error occur.
   */
  @Test
  public void testAll() throws IOException {
    // list all use cases
    File[] usecases = source.listFiles(f -> f.isDirectory() && f.getName().startsWith("UC-"));
    Arrays.sort(usecases, Comparator.comparing(File::getName));
    // iterate over the use cases.
    for (File uc : usecases) {
      File xml1 = new File(uc, "a.xml");
      File xml2 = new File(uc, "b.xml");
      File rc = new File(result, uc.getName());
      if (!rc.exists()) {
        rc.mkdirs();
      }
      if (!xml1.exists()) {
        System.err.println("missing file a.xml in " + uc.getName());
      }
      if (!xml2.exists()) {
        System.err.println("missing file b.xml in " + uc.getName());
      }
      if (xml1.exists() && xml2.exists()) {
        // setup the info print writer
        File infoFile = new File(rc, "info.txt");
        PrintStream info = new PrintStream(new BufferedOutputStream(new FileOutputStream(infoFile)), true);
        // print the sequences
        Sequence s1 = printSequence(xml1, info);
        Sequence s2 = printSequence(xml2, info);
        // process the diff
        long ta = processDiffX(xml1, xml2, info);
        long tb = processDiffX(xml2, xml1, info);
        System.out.println("Processed " + uc.getName() + " " + s1.size() + "x" + s2.size() + " tokens in " + ta + "ms / " + tb + "ms (" + xml1.length() + "x" + xml2.length() + " bytes) ");
      }
    }
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Processes the diff of the two documents.
   *
   * @param xml1 The first XML doc.
   * @param xml2 The second XML doc.
   * @param info The print writer where additional info goes.
   *
   * @throws IOException Should an error occur.
   */
  private long processDiffX(File xml1, File xml2, PrintStream info) throws IOException {
    long t = 0;
    info.println("Executing the diff-x as");
    // output
    int x = xml1.getName().compareTo(xml2.getName());
    File rc = new File(result, xml1.getParentFile().getName());
    File out = new File(rc, (x > 0) ? "a-b.xml" : "b-a.xml");
    PrintStream tmp = System.err;
    try (Writer diff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8))) {
      info.println("  diff(" + xml1.getParent() + "," + xml2.getParent() + ") -> " + out.getName());
      long t0 = new Date().getTime();
      System.setErr(info);
//      Main.diff(xmlr1, xmlr2, diff, config);
      Main.diff(toNode(xml1, true), toNode(xml2, true), diff, config);
      t = new Date().getTime() - t0;
      info.println("Processed in " + t + "ms");
      System.setErr(tmp);
    } catch (Exception ex) {
      System.setErr(tmp);
      System.err.println("Error with diff-X of " + xml1.getName() + ((x > 0) ? " [+]" : " [-]"));
      System.err.println("  -> " + ex.getMessage());
      info.println("Could not be processed.");
      info.println("It generated the following exception:");
      ex.printStackTrace(info);
    } finally {
      info.flush();
    }
    verifyWellFormed(out, info);
    return t;
  }

  /**
   * Prints the sequence of the given XML.
   *
   * @param xml  The XML doc which sequence needs to be printed.;
   * @param info Where the additional information goes.
   *
   * @throws IOException Should an error occur.
   */
  private Sequence printSequence(File xml, PrintStream info) throws IOException {
    Sequence s = new Sequence();
    // report the sequence of tokens
    SAXLoader loader = new SAXLoader();
    loader.setConfig(config);
    info.println("Printing sequence");
    info.println("  file = " + xml.getParent() + "\\" + xml.getName());
    try {
      long t0 = System.nanoTime();
      s = loader.load(xml);
      long t1 = System.nanoTime();
      info.println("  size = " + s.size());
      info.println("  loading time = " + (t1 - t0) + "nanoseconds");
    } catch (LoadingException ex) {
      info.println("Could no print the sequence, because of the following error:");
      ex.printStackTrace(info);
    }
    info.println("  size = " + s.size());
    info.println("::start");
    try {
      s = loader.load(xml);
      info.println(TestHandler.format(s.tokens()));
    } catch (Exception ex) {
      info.println("Could no print the sequence, because of the following error:");
      ex.printStackTrace(info);
    }
    info.println("::end");
    info.println();
    return s;
  }

  /**
   * Check the XML file for well-formedness.
   *
   * @param xml  The file to check.
   * @param info Where the additional info goes.
   *
   * @throws IOException Should an error occur.
   */
  private void verifyWellFormed(File xml, PrintStream info) throws IOException {
    // check that it is well-formed with SAX
    try {
      reader.parse(new InputSource(xml.getCanonicalPath()));
    } catch (SAXException ex) {
      System.err.println(xml.getName() + " is not well formed.");
      info.println("[!] XML is NOT well-formed.");
      info.println("[!] " + ex.getMessage());
    }
  }

  /**
   * Converts the reader to a node.
   *
   * @param xml       The reader on the XML.
   * @param isNSAware Whether the factory should be namespace aware.
   *
   * @return The corresponding node.
   */
  private Node toNode(File xml, boolean isNSAware) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(isNSAware);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.parse(xml);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

}