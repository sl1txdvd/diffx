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
package org.pageseeder.diffx.load.text;

import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.SpaceEvent;
import org.pageseeder.diffx.event.impl.WordEvent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test case for the tokenizer.
 *
 * @author Christophe Lauret
 * @version 3 April 2005
 */
public final class TokenizerByWordTest {

  /**
   * Tests that a <code>NullPointerException</code> is thrown for a </code>null</code>
   * character sequence.
   */
  @Test public void testNull() {
    try {
      TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.IGNORE);
      assertNull(t.tokenize(null));
    } catch (NullPointerException ex) {
      assertTrue(true);
    }
  }

  /**
   * Tests that an empty array is returned for empty string.
   */
  @Test  public void testEmpty() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.IGNORE);
    List<TextEvent> e = t.tokenize("");
    assertEquals(0, e.size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test public void testCountToken1() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.IGNORE);
    assertEquals(1, t.tokenize(" ").size());
    assertEquals(2, t.tokenize(" a").size());
    assertEquals(2, t.tokenize("a ").size());
    assertEquals(3, t.tokenize(" b ").size());
    assertEquals(3, t.tokenize("b b").size());
    assertEquals(4, t.tokenize("c c ").size());
    assertEquals(4, t.tokenize(" c c").size());
    assertEquals(5, t.tokenize(" d d ").size());
    assertEquals(5, t.tokenize("d d d").size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test public void testCountToken2() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.IGNORE);
    assertEquals(1, t.tokenize(" ").size());
    assertEquals(2, t.tokenize("  a").size());
    assertEquals(2, t.tokenize("aa ").size());
    assertEquals(2, t.tokenize(" aa").size());
    assertEquals(2, t.tokenize("a  ").size());
    assertEquals(3, t.tokenize(" bb ").size());
    assertEquals(3, t.tokenize("b bb").size());
    assertEquals(3, t.tokenize("b   bb").size());
    assertEquals(4, t.tokenize("xx  yy  ").size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test public void testCountToken3() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    assertEquals(1, t.tokenize(" ").size());
    assertEquals(3, t.tokenize("  \na").size());
    assertEquals(3, t.tokenize("aa \n").size());
    assertEquals(3, t.tokenize(" \naa").size());
    assertEquals(4, t.tokenize("a \n ").size());
    assertEquals(4, t.tokenize(" bb\n ").size());
    assertEquals(4, t.tokenize("b\n bb").size());
    assertEquals(5, t.tokenize("b \n  bb").size());
    assertEquals(7, t.tokenize("xx \n yy\n  ").size());
  }

  /**
   * Tests that the tokeniser counts the correct number of tokens.
   */
  @Test public void testCountToken4() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    assertEquals(1, t.tokenize("\n").size());
    assertEquals(3, t.tokenize("\n \n").size());
    assertEquals(3, t.tokenize(" \n\n").size());
    assertEquals(3, t.tokenize("\n\n\n").size());
  }

  /**
   * Tests that the tokeniser finds a space event as token.
   */
  @Test public void testSpace1() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize(" ");
    assertEquals(1, e.size());
    DiffXEvent space = e.get(0);
    assertEquals(new SpaceEvent(" "), space);
    assertSame(SpaceEvent.SINGLE_WHITESPACE, space);
  }

  /**
   * Tests that the tokeniser finds a space event as token.
   */
  @Test public void testSpace2() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize("  ");
    assertEquals(1, e.size());
    DiffXEvent space = e.get(0);
    assertEquals(new SpaceEvent("  "), space);
    assertSame(SpaceEvent.DOUBLE_WHITESPACE, space);
  }

  /**
   * Tests that the tokeniser finds a space event as token.
   */
  @Test public void testSpace3() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize("\n");
    assertEquals(1, e.size());
    DiffXEvent space = e.get(0);
    assertEquals(new SpaceEvent("\n"), space);
    assertSame(SpaceEvent.NEW_LINE, space);
  }

  /**
   * Tests that the tokeniser finds a word event as token.
   */
  @Test public void testWord1() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize("x");
    assertEquals(1, e.size());
    assertEquals(new WordEvent("x"), e.get(0));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of events.
   */
  @Test public void testSeq1() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize("xx  ");
    assertEquals(2, e.size());
    assertEquals(new WordEvent("xx"), e.get(0));
    assertEquals(new SpaceEvent("  "), e.get(1));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of events.
   */
  @Test public void testSeq2() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize("  xx");
    assertEquals(2, e.size());
    assertEquals(new SpaceEvent("  "), e.get(0));
    assertEquals(new WordEvent("xx"), e.get(1));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of events.
   */
  @Test public void testSeq3() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize("  xx\n");
    assertEquals(3, e.size());
    assertEquals(new SpaceEvent("  "), e.get(0));
    assertEquals(new WordEvent("xx"), e.get(1));
    assertEquals(SpaceEvent.NEW_LINE, e.get(2));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of events.
   */
  @Test public void testSeq4() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize("  xx\n\n");
    assertEquals(4, e.size());
    assertEquals(new SpaceEvent("  "), e.get(0));
    assertEquals(new WordEvent("xx"), e.get(1));
    assertEquals(SpaceEvent.NEW_LINE, e.get(2));
    assertEquals(SpaceEvent.NEW_LINE, e.get(3));
  }

  /**
   * Tests that the tokeniser finds the correct sequence of events.
   */
  @Test public void testSeq5() {
    TextTokenizer t = new TokenizerByWord(WhiteSpaceProcessing.PRESERVE);
    List<TextEvent> e = t.tokenize("  \n\nxx");
    assertEquals(4, e.size());
    assertEquals(new SpaceEvent("  "), e.get(0));
    assertEquals(SpaceEvent.NEW_LINE, e.get(1));
    assertEquals(SpaceEvent.NEW_LINE, e.get(2));
    assertEquals(new WordEvent("xx"), e.get(3));
  }

}
