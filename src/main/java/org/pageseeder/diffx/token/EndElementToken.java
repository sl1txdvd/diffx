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
package org.pageseeder.diffx.token;

/**
 * The token corresponding to the <code>endElement</code> SAX event.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.5.0
 */
public interface EndElementToken extends Namespaceable, Token {

  /**
   * Returns the local name of the element.
   *
   * @return The local name of the element.
   */
  String getName();

  /**
   * Returns the namespace URI the element belongs to.
   *
   * <p>This method should return <code>null</code> if the implementation
   * is not namespace aware.
   *
   * @return The namespace URI the element belongs to.
   */
  String getURI();

  /**
   * Returns the corresponding token element.
   *
   * @return The corresponding token element.
   */
  StartElementToken getOpenElement();

  /**
   * Indicates whether the specified open element token matches this close
   * element token.
   *
   * <p>This method first checks whether the open element token is the same as
   * token returned by the {@link #getOpenElement()} method, if not it simply
   * compares the name of the element and the namespace URI it belongs to.
   *
   * @param token The open element token to test.
   *
   * @return <code>true</code> if there is a match;
   * <code>false</code> otherwise.
   */
  boolean match(StartElementToken token);

  @Override
  default TokenType getType() {
    return TokenType.END_ELEMENT;
  }

}
