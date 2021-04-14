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
package org.pageseeder.diffx.event;

/**
 * The event corresponding to the <code>startElement</code> SAX event.
 *
 * @author Christophe Lauret
 * @version 23 December 2004
 */
public interface OpenElementEvent extends Namespaceable, DiffXEvent {

  /**
   * Returns the local name of the element.
   *
   * @return The local name of the element.
   */
  String getName();

  /**
   * Returns the namespace URI the element belongs to.
   *
   * @return The namespace URI the element belongs to.
   */
  String getURI();

  @Override
  default String getType() { return "open-element"; }
}
