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
package org.pageseeder.diffx.format;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.event.impl.AttributeEventNSImpl;
import org.pageseeder.diffx.event.impl.CloseElementEventNSImpl;
import org.pageseeder.diffx.event.impl.OpenElementEventNSImpl;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.util.Constants;
import org.xml.sax.InputSource;

import java.io.*;

/**
 * Test class for the strict formatter.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class StrictXMLFormatterTest {

  /**
   * The namespace declaration.
   */
  private static final String XML_DECL = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";

  /**
   * The namespace declaration.
   */
  private static final String NS_DECL = "xmlns:dfx=\"" + Constants.BASE_NS_URI + '"';

  /**
   * The loader being tested.
   */
  private SAXRecorder recorder = new SAXRecorder();

  /**
   * The formatter being tested.
   */
  private XMLDiffXFormatter formatter = null;

  /**
   * The string writer.
   */
  private StringWriter w = null;

  @BeforeEach
  public void setUp() {
    this.w = new StringWriter();
    this.formatter = new StrictXMLFormatter(this.w);
  }

//opening and closing elements ---------------------------------------------------------------

  /**
   * Test open and closing an element.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testOpenAndClose0() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.format(new CloseElementEventNSImpl("a"));
    assertEquivalentToXML("<a/>");
    String xml = XML_DECL + "<a " + NS_DECL + "></a>";
    assertEquals(xml, this.w.toString());
  }

// playing with attributes --------------------------------------------------------------------

  /**
   * Test formatting an attribute.
   *
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  @Test
  public void testAttributes0() throws DiffXException, IOException {
    this.formatter.format(new OpenElementEventNSImpl("a"));
    this.formatter.format(new AttributeEventNSImpl("x", "", "y"));
    this.formatter.format(new CloseElementEventNSImpl("a"));
    assertEquivalentToXML("<a x='y'/>");
    String xml = XML_DECL + "<a " + NS_DECL + " x=\"y\"></a>";
    assertEquals(xml, this.w.toString());
  }

// helpers ------------------------------------------------------------------------------------

  /**
   * Tests whether the content generated by the formatter is equivalent to the specified XML.
   *
   * @param xml The first XML to test.
   * @throws DiffXException Should an error occur whilst parsing one of the XML files.
   * @throws IOException    Should an I/O error occur.
   */
  private void assertEquivalentToXML(String xml) throws DiffXException, IOException {
    // process the XML to get the sequence
    Reader xmlr = new StringReader(xml);
    EventSequence exp = this.recorder.process(new InputSource(xmlr));
    // process the output of the formatter
    Reader xmlr2 = new StringReader(this.w.toString());
    EventSequence seq = this.recorder.process(new InputSource(xmlr2));
    try {
      assertEquals(exp.size(), seq.size());
      assertEquals(exp, seq);
    } catch (AssertionError ex) {
      PrintWriter pw = new PrintWriter(System.err);
      seq.export(pw);
      pw.flush();
      throw ex;
    }
    System.err.println(this.w.toString());
  }

}

