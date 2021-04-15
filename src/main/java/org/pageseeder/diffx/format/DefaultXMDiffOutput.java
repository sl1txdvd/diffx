/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.*;
import org.pageseeder.diffx.event.impl.SpaceEvent;
import org.pageseeder.diffx.sequence.Namespace;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.xmlwriter.XMLWriter;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultXMDiffOutput implements XMLDiffOutput {

  /**
   * The namespace URI reserved for the diff.
   */
  public static final String DIFF_NS_URI = "https://www.pageseeder.org/diffx";

  /**
   * The prefix used by diff by default.
   */
  public static final String DIFF_NS_PREFIX = "diff";

  /**
   * The namespace used for diff elements.
   */
  public static final Namespace DIFF_NAMESPACE = new Namespace(DIFF_NS_URI, DIFF_NS_PREFIX);

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = false;

  private final XMLWriter xml;

  private PrefixMapping mapping = PrefixMapping.noNamespace();

  /**
   * {@code true} (default) to include the XML namespace declaration when the {@link #start()} method is called.
   */
  private boolean includeXMLDeclaration = true;

  /**
   * {@code true} (default) to use the diff namespace for the {@code <ins/>} and {@code <del/>} elements.
   */
  private boolean useDiffNamespaceForElements = true;

  /**
   * Holds the list of attributes inserted to the previous element.
   */
  private final List<AttributeEvent> insertedAttributes = new ArrayList<>();

  /**
   * Holds the list of attributes deleted from the previous element.
   */
  private final List<AttributeEvent> deletedAttributes = new ArrayList<>();

  /**
   * Used to know if all elements have been closed, in which case the namespace
   * mapping should be redeclared before opening a new element
   */
  private int openElements = 0;

  public DefaultXMDiffOutput(Writer out) {
    this.xml = new XMLWriterNSImpl(out);
  }

  public void useDiffNamespaceForElements(boolean yes) {
    this.useDiffNamespaceForElements = yes;
  }

  @Override
  public void setWriteXMLDeclaration(boolean show) {
    this.includeXMLDeclaration = show;
  }

  @Override
  public void declarePrefixMapping(PrefixMapping mapping) {
    this.mapping = mapping;
  }

  @Override
  public void start() {
    try {
      if (this.includeXMLDeclaration)
        this.xml.xmlDecl();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void end() {
    try {
      this.xml.flush();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public void handle(Operator operator, DiffXEvent event) throws UncheckedIOException, IllegalStateException {
    if (DEBUG) System.err.println(operator.toString()+event);
    try {
      // We must flush the inserted/deleted attributes
      if (!(event instanceof AttributeEvent)) {
        this.flushAttributes();
      }
      // namespaces declaration
      if (event instanceof OpenElementEvent) {
        if (this.openElements == 0) declareNamespaces();
        this.openElements++;
      } else if (event instanceof CloseElementEvent) {
        this.openElements--;
      }
      // Handle matches and clashes
      if (operator == Operator.MATCH) handleMatch(event);
      else handleClash(operator, event);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private void handleMatch(DiffXEvent event) throws IOException {
    event.toXML(this.xml);
    this.insertSpaceIfRequired(event);
  }

  private void handleClash(Operator operator, DiffXEvent event) throws IOException {
    // insert an attribute to specify
    if (event instanceof OpenElementEvent) {
      // namespaces declaration
      if (this.openElements == 0) {
        declareNamespaces();
        this.openElements++;
      }
      event.toXML(this.xml);
      this.xml.attribute(DIFF_NS_URI, operator == Operator.INS ? "ins" : "del", "true");

      // just output the new line
    } else if (event == SpaceEvent.NEW_LINE) {
      event.toXML(this.xml);

      // wrap the characters in a <ins> element
    } else if (event instanceof TextEvent) {
      this.xml.openElement(DIFF_NS_URI, operator == Operator.INS ? "ins" : "del", false);
      event.toXML(this.xml);
      this.xml.closeElement();
      this.insertSpaceIfRequired(event);

    } else if (event instanceof AttributeEvent) {
      if (operator == Operator.INS) {
        event.toXML(this.xml);
        this.insertedAttributes.add((AttributeEvent) event);
      } else {
        this.deletedAttributes.add((AttributeEvent) event);
      }

    } else if (event instanceof CloseElementEvent) {
      this.openElements--;
      event.toXML(this.xml);

    } else {
      event.toXML(this.xml);
    }
  }

  /**
   * Write the namespaces mapping to the XML output
   */
  private void declareNamespaces() {
    // TODO Change so that there is no side-effect
    PrefixMapping diff = new PrefixMapping(DIFF_NAMESPACE);
    diff.add(this.mapping);
    for (Namespace namespace : diff) {
      this.xml.setPrefixMapping(namespace.getUri(), namespace.getPrefix());
    }
  }

  private void insertSpaceIfRequired(DiffXEvent event) throws IOException {
//    if (event instanceof TextEvent && !(event instanceof CharEvent) && this.config.isIgnoreWhiteSpace() && !this.config.isPreserveWhiteSpace()) {
//      this.xml.writeXML(" ");
//    }
  }

  /**
   * Flush the inserted or deleted attributes on the element.
   *
   * This method must be called before we finish writing the start element tag.
   */
  private void flushAttributes() throws IOException {
    String namespace = useDiffNamespaceForElements ? DIFF_NS_URI : XMLConstants.NULL_NS_URI;
    // Attributes first
    if (!this.insertedAttributes.isEmpty()) {
      this.xml.attribute(DIFF_NS_URI, "ins-attributes", this.insertedAttributes.stream().map(attribute -> attribute.getName()).collect(Collectors.joining(" ")));
    }
    if (!this.deletedAttributes.isEmpty()) {
      this.xml.attribute(DIFF_NS_URI, "del-attributes", this.deletedAttributes.stream().map(attribute -> attribute.getName()).collect(Collectors.joining(" ")));
    }
    // Elements
    if (!this.insertedAttributes.isEmpty()) {
      this.xml.openElement(namespace, "ins", false);
      for (AttributeEvent attribute : this.insertedAttributes) {
        this.xml.attribute(attribute.getURI(), attribute.getName(), attribute.getValue());
      }
      this.xml.closeElement();
      this.insertedAttributes.clear();
    }
    if (!this.deletedAttributes.isEmpty()) {
      this.xml.openElement(namespace, "del", false);
      for (AttributeEvent attribute : this.deletedAttributes) {
        this.xml.attribute(attribute.getURI(), attribute.getName(), attribute.getValue());
      }
      this.xml.closeElement();
      this.deletedAttributes.clear();
    }
  }

}