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
package org.pageseeder.diffx.action;

import org.pageseeder.diffx.event.DiffXEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An action associated to a sequence of DiffX events.
 * <p>
 * Wraps an event and binds it with an action type.
 * <p>
 * A type of action for the events:
 * <ul>
 *   <li>Add a diffx event to a sequence (+);</li>
 *   <li>Remove a diffx event to sequence (-);</li>
 *   <li>Preserve a diffx event.</li>
 * </ul>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public class Action {

  /**
   * The type of this action.
   */
  private final Operator type;

  /**
   * The list of events associated with this action.
   */
  private final List<DiffXEvent> events;

  /**
   * Creates a new action.
   *
   * @param type The type of action.
   * @throws NullPointerException If the given type is <code>null</code>.
   */
  public Action(Operator type) {
    if (type == null) throw new NullPointerException("An action must have a type.");
    this.type = type;
    this.events = new LinkedList<>();
  }

  /**
   * Creates a new action.
   *
   * @param type The type of action.
   * @throws NullPointerException If the given type is <code>null</code>.
   */
  public Action(Operator type, List<DiffXEvent> events) {
    if (type == null) throw new NullPointerException("An action must have a type.");
    this.type = type;
    this.events = events;
  }

  /**
   * Add a DiffX event to the list for this action.
   *
   * @param e The event to add.
   */
  public void add(DiffXEvent e) {
    this.events.add(e);
  }

  /**
   * @return the list of DiffXEvents.
   */
  public List<DiffXEvent> events() {
    return this.events;
  }

  /**
   * @return The type of this action.
   */
  public Operator type() {
    return this.type;
  }

  /**
   * @return the reserve action by swapping INS with DEL.
   */
  public Action reverse() {
    switch (this.type) {
      case DEL:
        return new Action(Operator.INS, this.events);
      case INS:
        return new Action(Operator.DEL, this.events);
      default:
        return this;
    }
  }

}