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

/**
 *
 *
 * @author Carlos Cabral
 * @version 08 Jan 2015
 */
public class RecorderFactory {

	public static final String RECORDER_TYPE_SAX  = "sax";
	public static final String RECORDER_TYPE_DOM  = "dom";
	public static final String RECORDER_TYPE_TEXT = "text";

	/**
	 * Return a Recorder according the parameter.
	 *
	 * @param recorderType
	 * @return <code>Recorder</code>
	 */
	public Recorder getRecorder(String recorderType){
		Recorder recorder = null;
    if (recorderType == null || recorderType.equals(RECORDER_TYPE_SAX)) {
    	recorder = new SAXRecorder();
    } else if (recorderType.equals(RECORDER_TYPE_DOM)) {
    	recorder = new DOMRecorder();
    } else if (recorderType.equals(RECORDER_TYPE_TEXT)) {
    	recorder =  new TextRecorder();
		} else {
    	recorder =  new SAXRecorder();
    }
    return recorder;
	}
}