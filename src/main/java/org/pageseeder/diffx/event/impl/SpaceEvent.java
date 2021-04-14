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
package org.pageseeder.diffx.event.impl;

import org.pageseeder.diffx.event.TextEvent;

/**
 * A particular type of event reserved for white spaces.
 *
 * @author Christophe Lauret
 * @version 27 March 2010
 */
public final class SpaceEvent extends CharactersEventBase implements TextEvent {

  /**
   * A static instance for the single white spaces.
   *
   * <p>Use this constant instead of creating new instances
   */
  public static final SpaceEvent SINGLE_WHITESPACE = new SpaceEvent(" ");

  /**
   * A static instance for the double white spaces.
   *
   * <p>Use this constant instead of creating new instances
   */
  public static final SpaceEvent DOUBLE_WHITESPACE = new SpaceEvent("  ");

  /**
   * A static instance for the new lines.
   *
   * <p>Use this constant instead of creating new instances
   */
  public static final SpaceEvent NEW_LINE = new SpaceEvent("\n");

  /**
   * A static instance for tabs.
   *
   * <p>Use this constant instead of creating new instances
   */
  public static final SpaceEvent TAB = new SpaceEvent("\t");

  /**
   * Creates a new space event.
   *
   * @param s The space as a string.
   *
   * @throws NullPointerException If the given String is <code>null</code>.
   */
  public SpaceEvent(CharSequence s) throws NullPointerException {
    super(s);
  }

  @Override
  public String toString() {
    return "space: \""+toString(getCharacters().toCharArray())+'"';
  }

  /**
   * Returns the white space event corresponding to the given string.
   *
   * @param space The string for the white space event.
   *
   * @return A readable string.
   */
  public static SpaceEvent getInstance(CharSequence space) {
    // check constants
    if (" ".contentEquals(space))  return SINGLE_WHITESPACE;
    if ("  ".contentEquals(space)) return DOUBLE_WHITESPACE;
    if ("\n".contentEquals(space)) return NEW_LINE;
    if ("\t".contentEquals(space)) return TAB;
    // create a new instance
    return new SpaceEvent(space);
  }

  /**
   * Returns the white space event corresponding to the given string.
   *
   * @param c The string for the white space event.
   *
   * @return A readable string.
   */
  public static SpaceEvent getInstance(char c) {
    // check constants
    if (c == ' ')  return SINGLE_WHITESPACE;
    if (c == '\n') return NEW_LINE;
    if (c == '\t') return TAB;
    // create a new instance
    return new SpaceEvent(String.valueOf(c));
  }

  /**
   * Returns the white space characters as a readable string.
   *
   * @param chars The whitespace characters
   *
   * @return A readable string.
   */
  private static String toString(char[] chars) {
    StringBuilder out = new StringBuilder();
    for (char c : chars) {
      switch(c) {
        case '\n':
          out.append("\\n");
          break;
        case '\t':
          out.append("\\t");
          break;
        default :
          out.append(c);
      }
    }
    return out.toString();
  }

}
