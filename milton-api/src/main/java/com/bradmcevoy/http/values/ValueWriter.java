/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package com.bradmcevoy.http.values;

import com.bradmcevoy.http.XmlWriter;
import java.util.Map;

/**
 * An implementation of ValueWriter will generate xml for some particular property
 * or type, and be able to parse proppatch textual values into its typed form
 *
 * Generally ValueWriter's should be symmetrical, in that they can parse what
 * they generate.
 *
 * @author brad
 */
public interface ValueWriter {

    /**
     * Does this ValueWriter support the data type or property for writing xml
     *
     * @param prefix
     * @param nsUri
     * @param localName
     * @param val
     * @return
     */
    boolean supports( String nsUri, String localName, Class valueClass );

    /**
     * Write the value out to XML using the given XmlWriter
     *
     * @param writer
     * @param nsUri
     * @param prefix
     * @param localName
     * @param val
     * @param href
     * @param nsPrefixes
     */
    void writeValue( XmlWriter writer, String nsUri, String prefix, String localName, Object val, String href, Map<String, String> nsPrefixes );

    /**
     * Parse the given textual representation, probably from a PROPPATCH request
     *
     * @param namespaceURI
     * @param localPart
     * @param value
     * @return
     */
    Object parse( String namespaceURI, String localPart, String value );
}
