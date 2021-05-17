/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.sequence.XMLSequence;

/**
 * Test case for Guano Diff-X algorithm.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
@Deprecated
public final class GuanoDiffXAlgorithmTest extends BaseDiffXAlgorithmLevel2Test {

  public DiffXAlgorithm makeDiffX(XMLSequence seq1, XMLSequence seq2) {
    return new GuanoAlgorithm(new EventSequence(seq1), new EventSequence(seq2));
  }

}
