// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.sf.kdgcommons.net;

/**
 *  An ever-growing list of MIME types that my applications might use, to avoid
 *  typos. Why doesn't the JDK provide something like this?!?
 */
public class MimeTypes
{
    public final static String  TEXT            = "text/plain";
    public final static String  BINARY          = "application/octet-stream";

    public final static String  XML             = "text/xml";
    public final static String  DEPRECATED_XML  = "application/xml";

    public final static String  HTML            = "text/html";
    public final static String  HTML_POST       = "application/x-www-form-urlencoded";

    public final static String  JAVASCRIPT      = "application/javascript";
    public final static String  JSON            = "application/json";
}
