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
package org.pageseeder.diffx.load;

import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.impl.AttributeTokenImpl;
import org.pageseeder.diffx.token.impl.AttributeTokenNSImpl;

import java.util.Comparator;

/**
 * A comparator in order to put attributes in the correct order, that is in the alphabetical order
 * of the attribute name and namespace URI.
 *
 * <p>Sorting attribute allows for more efficient diffing.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.8.0
 */
final class AttributeComparator implements Comparator<AttributeToken> {

  /**
   * Compares two objects if they are attributes.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int compare(AttributeToken o1, AttributeToken o2) throws ClassCastException {
    if (o1 instanceof AttributeTokenImpl && o2 instanceof AttributeTokenImpl)
      return compare((AttributeTokenImpl) o1, (AttributeTokenImpl) o2);
    else if (o1 instanceof AttributeTokenNSImpl && o2 instanceof AttributeTokenNSImpl)
      return compare((AttributeTokenNSImpl) o1, (AttributeTokenNSImpl) o2);
    else
      return 0;
  }

  /**
   * Compares two attribute tokens using their name.
   *
   * @param att1 The first attribute to be compared.
   * @param att2 The second attribute to be compared.
   *
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   * than the second.
   */
  public int compare(AttributeTokenImpl att1, AttributeTokenImpl att2) {
    return att1.getName().compareTo(att2.getName());
  }

  /**
   * Compares two simple attribute tokens using their name and namespace URI.
   *
   * @param att1 The first attribute to be compared.
   * @param att2 The second attribute to be compared.
   *
   * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
   * than the second.
   */
  public int compare(AttributeTokenNSImpl att1, AttributeTokenNSImpl att2) {
    return toCName(att1).compareTo(toCName(att2));
  }

  /**
   * Returns a comparable name from the given attribute's namespace URI and name.
   *
   * @param att The attribute.
   *
   * @return The comparable name.
   */
  private static String toCName(AttributeTokenNSImpl att) {
    return att.getURI().isEmpty() ? att.getName() : att.getURI() + ':' + att.getName();
  }

}
