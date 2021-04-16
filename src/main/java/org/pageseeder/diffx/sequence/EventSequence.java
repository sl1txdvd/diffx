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
package org.pageseeder.diffx.sequence;

import java.io.PrintWriter;
import java.util.*;

import org.pageseeder.diffx.token.Token;

/**
 * A sequence of tokens used for the Diff-X algorithm.
 *
 * <p>This class wraps a list of <code>Token</code>s and provide method to
 * access and modify the content of the list using strongly typed methods.
 *
 * <p>Implementation note: we use an <code>ArrayList</code> to store the tokens because some algorithms
 * need random access. Other list implementations may affect performance.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 *
 * @since 0.7
 */
public final class EventSequence implements Iterable<Token> {

  /**
   * The prefix mapping for the elements in this sequence.
   */
  private final PrefixMapping prefixMapping = new PrefixMapping();

  /**
   * The sequence of tokens.
   */
  private final List<Token> tokens;

  /**
   * Creates a new event sequence.
   */
  public EventSequence() {
    this.tokens = new ArrayList<>();
  }

  /**
   * Creates a new event sequence of the specified size.
   *
   * @param size The size of the sequence.
   */
  public EventSequence(int size) {
    this.tokens = new ArrayList<>(size);
  }

  /**
   * Creates a new event sequence of the specified size.
   *
   * <p>Use a <code>List</code> implementation with that provide good random access performance.</p>
   *
   * @param tokens The size of the sequence.
   */
  public EventSequence(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   * Adds a sequence of tokens to this sequence.
   *
   * @param seq The sequence of tokens to be added.
   */
  public void addSequence(EventSequence seq) {
    this.tokens.addAll(seq.tokens);
  }

  /**
   * Adds an event to this sequence.
   *
   * @param token The token to be added.
   */
  public void addToken(Token token) {
    this.tokens.add(token);
  }

  /**
   * Inserts an event to this sequence at the specified position.
   *
   * @param i The position of the event.
   * @param token The token to be added.
   */
  public void addToken(int i, Token token) {
    this.tokens.add(i, token);
  }

  /**
   * Adds an event to this sequence.
   *
   * @param tokens The event to be added.
   */
  public void addTokens(List<? extends Token> tokens) {
    this.tokens.addAll(tokens);
  }

  /**
   * Returns the event at position i.
   *
   * @param i The position of the event.
   *
   * @return the event at position i.
   */
  public Token getToken(int i) {
    return this.tokens.get(i);
  }

  /**
   * Replaces an event of this sequence at the specified position.
   *
   * @param index The 0-based index of the position.
   * @param e     The event to be inserted.
   *
   * @return The event at the previous position.
   */
  public Token setToken(int index, Token e) {
    return this.tokens.set(index, e);
  }

  /**
   * Removes an event from this sequence at the specified position.
   *
   * @param index The 0-based index of the position.
   *
   * @return The removed event.
   */
  public Token removeToken(int index) {
    return this.tokens.remove(index);
  }

  /**
   * @return The number of tokens in the sequence.
   */
  public int size() {
    return this.tokens.size();
  }

  /**
   * @return the sequence of tokens.
   */
  public List<Token> tokens() {
    return this.tokens;
  }

  @Override
  public int hashCode() {
    return this.tokens.size();
  }

  /**
   * Returns <code>true</code> if the specified event sequence is the same as this one.
   *
   * @param seq The sequence of tokens to compare with this one.
   *
   * @return <code>true</code> if the specified event sequence is equal to this one;
   *         <code>false</code> otherwise.
   */
  public boolean equals(EventSequence seq) {
    if (seq == null) return false;
    return equals(this.tokens, seq.tokens);
  }

  /**
   * Returns <code>true</code> if the specified event sequence is the same as this one.
   *
   * <p>This method will redirect to the {@link #equals(EventSequence)} method if the
   * specified object is an instance of {@link EventSequence}.
   *
   * @param o The sequence of tokens to compare with this one.
   *
   * @return <code>true</code> if the specified event sequence is equal to this one;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof EventSequence)) return false;
    return this.equals((EventSequence)o);
  }

  @Override
  public String toString() {
    return "EventSequence{" +
        "prefixMapping=" + prefixMapping +
        ", tokens=" + tokens +
        '}';
  }

  /**
   * Export the sequence.
   *
   * @param w The print writer receiving the SAX tokens.
   */
  public void export(PrintWriter w) {
    for (Token event : this.tokens) {
      w.println(event.toString());
    }
    w.flush();
  }

  /**
   * Maps a namespace URI to a prefix.
   *
   * @deprecated Use {@link #addNamespace(String, String)}
   *
   * @see PrefixMapping#add(String, String)
   *
   * @param uri    The namespace URI to map.
   * @param prefix The prefix to use.
   *
   * @throws NullPointerException if the URI or prefix is <code>null</code>
   */
  @Deprecated
  public void mapPrefix(String uri, String prefix) throws NullPointerException {
    this.prefixMapping.add(uri, prefix);
  }

  /**
   * Maps a namespace URI to a prefix.
   *
   * <p>The replace element is usually used for the document element in order to override the
   * default namespace.</p>
   *
   * @see PrefixMapping#add(String, String)
   * @see PrefixMapping#replace(String, String)
   *
   * @param uri     The namespace URI to map.
   * @param prefix  The prefix to use.
   * @param replace Whether to replace the namespace
   *
   * @throws NullPointerException if the URI or prefix is <code>null</code>
   */
  public void addNamespace(String uri, String prefix, boolean replace) throws NullPointerException {
    if (replace) {
      this.prefixMapping.replace(uri, prefix);
    } else {
      this.prefixMapping.add(uri, prefix);
    }
  }

  /**
   * Maps a namespace URI to a prefix.
   *
   * @see PrefixMapping#add(String, String)
   *
   * @param uri    The namespace URI to map.
   * @param prefix The prefix to use.
   *
   * @throws NullPointerException if the URI or prefix is <code>null</code>
   */
  public void addNamespace(String uri, String prefix) throws NullPointerException {
    this.prefixMapping.add(uri, prefix);
  }

  /**
   * Returns the prefix mapping for the namespace URIs in this sequence.
   *
   * @return the prefix mapping for the namespace URIs in this sequence.
   */
  public PrefixMapping getPrefixMapping() {
    return this.prefixMapping;
  }

  @Override
  public Iterator<Token> iterator() {
    return this.tokens().iterator();
  }

  private static boolean equals(List<Token> first, List<Token> second) {
    if (first.size() != second.size()) return false;
    Token x1;
    Token x2;
    for (int i = 0; i < first.size(); i++) {
      x1 = first.get(i);
      x2 = second.get(i);
      if (!x1.equals(x2)) return false;
    }
    return true;
  }

}
