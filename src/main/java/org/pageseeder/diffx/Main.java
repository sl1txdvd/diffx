/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx;

import org.pageseeder.diffx.algorithm.DiffXAlgorithm;
import org.pageseeder.diffx.algorithm.DiffXKumarRangan;
import org.pageseeder.diffx.algorithm.GuanoAlgorithm;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.format.*;
import org.pageseeder.diffx.load.DOMRecorder;
import org.pageseeder.diffx.load.LineRecorder;
import org.pageseeder.diffx.load.Recorder;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.sequence.SequenceSlicer;
import org.pageseeder.diffx.util.CommandLine;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to centralise the access to this API from the command line.
 *
 * @author Christophe Lauret
 * @version 10 May 2010
 */
public final class Main {

  /**
   * Prevents creation of instances.
   */
  private Main() {
  }

  // equivalent methods -------------------------------------------------------------------

  /**
   * Returns <code>true</code> if the two specified files are XML equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(File xml1, File xml2)
      throws DiffXException, IOException {
    Recorder recorder = new SAXRecorder();
    Sequence seq0 = recorder.process(xml1);
    Sequence seq1 = recorder.process(xml2);
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified input streams are equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(InputStream xml1, InputStream xml2)
      throws DiffXException, IOException {
    SAXRecorder recorder = new SAXRecorder();
    Sequence seq0 = recorder.process(new InputSource(xml1));
    Sequence seq1 = recorder.process(new InputSource(xml2));
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified readers are equivalent by looking at the
   * sequence SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   * <code>false</code> otherwise.
   * @throws DiffXException If a DiffX exception is reported by the recorders.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(Reader xml1, Reader xml2)
      throws DiffXException, IOException {
    SAXRecorder recorder = new SAXRecorder();
    Sequence seq0 = recorder.process(new InputSource(xml1));
    Sequence seq1 = recorder.process(new InputSource(xml2));
    return seq0.equals(seq1);
  }

  // diff methods -------------------------------------------------------------------------

  /**
   * Compares the two specified XML nodes and prints the diff onto the given writer.
   *
   * @param xml1   The first XML node to compare.
   * @param xml2   The second XML node to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Node xml1, Node xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the tokens from the XML
    DOMRecorder loader = new DOMRecorder();
    if (config != null) {
      loader.setConfig(config);
    }
    Sequence seq1 = loader.process(xml1);
    Sequence seq2 = loader.process(xml2);
    // start slicing
    diff(seq1, seq2, out, config);
  }

  /**
   * Compares the two specified <code>NodeList</code>s and prints the diff onto the given writer.
   *
   * <p>Only the first node in the node list is sequenced.
   *
   * @param xml1   The first XML node list to compare.
   * @param xml2   The second XML node list to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(NodeList xml1, NodeList xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the tokens from the XML
    DOMRecorder loader = new DOMRecorder();
    if (config != null) {
      loader.setConfig(config);
    }
    Sequence seq1 = loader.process(xml1);
    Sequence seq2 = loader.process(xml2);
    // start slicing
    diff(seq1, seq2, out, config);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1   The first XML reader to compare.
   * @param xml2   The first XML reader to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Reader xml1, Reader xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the tokens from the XML
    SAXRecorder recorder = new SAXRecorder();
    if (config != null) {
      recorder.setConfig(config);
    }
    Sequence seq1 = recorder.process(new InputSource(xml1));
    Sequence seq2 = recorder.process(new InputSource(xml2));
    // start slicing
    diff(seq1, seq2, out, config);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1 The first XML reader to compare.
   * @param xml2 The first XML reader to compare.
   * @param out  Where the output goes
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Reader xml1, Reader xml2, Writer out)
      throws DiffXException, IOException {
    // records the tokens from the XML
    SAXRecorder recorder = new SAXRecorder();
    Sequence seq1 = recorder.process(new InputSource(xml1));
    Sequence seq2 = recorder.process(new InputSource(xml2));
    // start slicing
    diff(seq1, seq2, out, new DiffXConfig());
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1 The first XML input stream to compare.
   * @param xml2 The first XML input stream to compare.
   * @param out  Where the output goes
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(InputStream xml1, InputStream xml2, OutputStream out)
      throws DiffXException, IOException {
    // records the tokens from the XML
    SAXRecorder recorder = new SAXRecorder();
    Sequence seq1 = recorder.process(new InputSource(xml1));
    Sequence seq2 = recorder.process(new InputSource(xml2));
    diff(seq1, seq2, new OutputStreamWriter(out), new DiffXConfig());
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param seq1   The first XML reader to compare.
   * @param seq2   The first XML reader to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws IOException Should an I/O exception occur.
   */
  private static void diff(Sequence seq1, Sequence seq2, Writer out, DiffXConfig config)
      throws IOException {
    SafeXMLFormatter formatter = new SafeXMLFormatter(out);
    PrefixMapping mapping = new PrefixMapping();
    mapping.add(seq1.getPrefixMapping());
    mapping.add(seq2.getPrefixMapping());
    formatter.declarePrefixMapping(mapping);

    if (config != null) {
      formatter.setConfig(config);
    }
    SequenceSlicer slicer = new SequenceSlicer(seq1, seq2);
    slicer.slice();
    slicer.formatStart(formatter);
    DiffXAlgorithm df = new GuanoAlgorithm(seq1, seq2);
    df.process(formatter);
    slicer.formatEnd(formatter);
  }

  // command line -------------------------------------------------------------------------

  /**
   * Main entry point from the command line.
   *
   * @param args The command-line arguments
   */
  public static void main(String[] args) {
    // TODO: better command-line interface
    if (args.length < 2) {
      usage();
      return;
    }
    try {
      boolean profile = CommandLine.hasSwitch("-profile", args);
      boolean slice = !CommandLine.hasSwitch("-noslice", args);
      boolean quiet = CommandLine.hasSwitch("-quiet", args);

      // get the files
      File xml1 = new File(args[args.length - 2]);
      File xml2 = new File(args[args.length - 1]);

      // loading
      long t0 = System.currentTimeMillis();
      Recorder recorder = getRecorder(args);
      if (recorder == null) return;
      Sequence seq1 = recorder.process(xml1);
      Sequence seq2 = recorder.process(xml2);
      long t1 = System.currentTimeMillis();
      if (profile) {
        System.err.println("Loaded files in " + (t1 - t0) + "ms");
      }

      // get the config
      DiffXConfig config = new DiffXConfig();
      config.setGranularity(getTextGranularity(args));
      config.setWhiteSpaceProcessing(getWhiteSpaceProcessing(args));
      if (!quiet) {
        System.err.println("Whitespace processing: " + getTextGranularity(args) + " " + getWhiteSpaceProcessing(args));
      }

      // get and setup the formatter
      Writer out = new OutputStreamWriter(getOutput(args), StandardCharsets.UTF_8);
      DiffXFormatter formatter = getFormatter(args, out);
      if (formatter instanceof XMLDiffXFormatter) {
        PrefixMapping mapping = new PrefixMapping();
        mapping.add(seq1.getPrefixMapping());
        mapping.add(seq2.getPrefixMapping());
        ((XMLDiffXFormatter) formatter).declarePrefixMapping(mapping);
      }
      if (formatter == null) return;
      formatter.setConfig(config);

      // pre-slicing
      SequenceSlicer slicer = new SequenceSlicer(seq1, seq2);
      if (slice) {
        slicer.slice();
        slicer.formatStart(formatter);
      }

      // start algorithm
      if (!quiet) {
        System.err.println("Matrix: " + seq1.size() + "x" + seq2.size());
      }
      DiffXAlgorithm df = getAlgorithm(args, seq1, seq2);
      if (df == null) return;
      df.process(formatter);

      // post-slicing
      if (slice) {
        slicer.formatEnd(formatter);
      }

      long t2 = System.currentTimeMillis();
      if (profile) {
        System.err.println("Executed algorithm files in " + (t2 - t1) + "ms");
      }

    } catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Displays the usage on the System.err console
   */
  public static void usage() {
    System.err.println("Compare the SAX events returned by two XML files.");
    System.err.println("usage:");
    System.err.println("  Main [options] xml_file1 xml_file2");
    System.err.println("where:");
    System.err.println("  xml_file1 = Path to the new XML file");
    System.err.println("  xml_file2 = Path to the old XML file");
    System.err.println("options:");
    System.err.println("  -profile    Display profiling info");
    System.err.println("  -noslice    Do not use slicing");
    System.err.println("  -o [output] The output file");
    System.err.println("  -L [loader] Choose a specific loader");
    System.err.println("               sax* | dom | text");
    System.err.println("  -A [algo]   Choose a specific algorithm");
    System.err.println("               fitsy* | guano | fitopsy | kumar | wesyma");
    System.err.println("  -F [format] Choose a specific formatter");
    System.err.println("               smart* | basic | convenient | strict | short");
    System.err.println("  -W [wsp]    Define whitespace processing");
    System.err.println("               preserve* | compare | ignore");
    System.err.println("  -G [granul] Define text diffing granularity");
    System.err.println("               word* | text | character");
    System.err.println(" * indicates option used by default.");
    System.exit(1);
  }

  /**
   * @param args The command line arguments.
   *
   * @return The recorder to use.
   */
  private static Recorder getRecorder(String[] args) {
    String loaderArg = CommandLine.getParameter("-L", args);
    if (loaderArg == null || "sax".equals(loaderArg))
      return new SAXRecorder();
    if ("dom".equals(loaderArg))
      return new DOMRecorder();
    if ("text".equals(loaderArg))
      return new LineRecorder();
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   *
   * @return The output to use.
   * @throws FileNotFoundException If the file does not exist.
   */
  private static OutputStream getOutput(String[] args) throws FileNotFoundException {
    String outArg = CommandLine.getParameter("-o", args);
    if (outArg == null)
      return System.out;
    return new BufferedOutputStream(new FileOutputStream(outArg));
  }

  /**
   * @param args The command line arguments.
   * @param seq1 The first sequence.
   * @param seq2 The second sequence.
   *
   * @return The algorithm to use.
   */
  private static DiffXAlgorithm getAlgorithm(String[] args, Sequence seq1, Sequence seq2) {
    // TODO duplicated code from factory
    String loaderArg = CommandLine.getParameter("-A", args);
    if (loaderArg == null || "guano".equals(loaderArg))
      return new GuanoAlgorithm(seq1, seq2);
    if ("kumar".equals(loaderArg))
      return new DiffXKumarRangan(seq1, seq2);
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   * @param out  The writer to use.
   *
   * @return The formatter to use.
   * @throws IOException Should and I/O error occur
   */
  private static DiffXFormatter getFormatter(String[] args, Writer out) throws IOException {
    String formatArg = CommandLine.getParameter("-F", args);
    if (formatArg == null || "smart".equals(formatArg))
      return new SmartXMLFormatter(out);
//    if ("convenient".equals(formatArg)) FIXME
//      return new ConvenientXMLFormatter(out);
//    if ("basic".equals(formatArg)) FIXME
//      return new BasicXMLFormatter(out);
//    if ("strict".equals(formatArg))
//      return new StrictXMLFormatter(out);
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   *
   * @return The formatter to use.
   */
  private static WhiteSpaceProcessing getWhiteSpaceProcessing(String[] args) {
    String formatArg = CommandLine.getParameter("-W", args);
    if (formatArg == null || "preserve".equals(formatArg))
      return WhiteSpaceProcessing.PRESERVE;
    if ("compare".equals(formatArg))
      return WhiteSpaceProcessing.COMPARE;
    if ("ignore".equals(formatArg))
      return WhiteSpaceProcessing.IGNORE;
    usage();
    return null;
  }

  /**
   * @param args The command line arguments.
   *
   * @return The formatter to use.
   */
  private static TextGranularity getTextGranularity(String[] args) {
    String formatArg = CommandLine.getParameter("-G", args);
    if (formatArg == null || "word".equals(formatArg))
      return TextGranularity.WORD;
    if ("text".equals(formatArg))
      return TextGranularity.TEXT;
    if ("character".equals(formatArg))
      return TextGranularity.CHARACTER;
    usage();
    return null;
  }
}
