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
package org.pageseeder.diffx.test;

import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.*;
import org.pageseeder.diffx.event.impl.*;
import org.pageseeder.diffx.format.StrictXMLFormatter;
import org.pageseeder.diffx.load.SAXRecorder;
import org.pageseeder.diffx.load.TextRecorder;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for events and testing.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Events {

  /**
   * Prevents creation of instances.
   */
  private Events() {
  }

  public static TextEvent toTextEvent(String text) {
    if (text.matches("\\s+")) {
      return new IgnorableSpaceEvent(text);
    }
    return new WordEvent(text);
  }

  public static List<TextEvent> toTextEvents(String... words) {
    List<TextEvent> events = new ArrayList<>();
    for (String word : words) {
      events.add(toTextEvent(word));
    }
    return events;
  }

  public static List<DiffXEvent> recordXMLEvents(String xml, TextGranularity granularity) throws DiffXException, IOException {
    if (xml.isEmpty()) return Collections.emptyList();
    DiffXConfig config = new DiffXConfig();
    config.setGranularity(granularity);
    return recordXMLEvents(xml, config);
  }

  public static List<DiffXEvent> recordXMLEvents(String xml, DiffXConfig config) throws DiffXException, IOException {
    SAXRecorder recorder = new SAXRecorder();
    recorder.setConfig(config);
    return recorder.process(xml).events();
  }

  public static List<DiffXEvent> recordLineEvents(String text) throws DiffXException, IOException  {
    if (text.isEmpty()) return Collections.emptyList();
    return new TextRecorder().process(text).events();
  }

  public static String toXML(List<? extends DiffXEvent> events) throws IOException {
    StringWriter xml = new StringWriter();
    StrictXMLFormatter f = new StrictXMLFormatter();
    for (DiffXEvent event : events) {
      f.format(event);
    }
    return xml.toString();
  }

}