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

package net.sf.kdgcommons.html;

import net.sf.kdgcommons.lang.StringBuilderUtil;
import net.sf.kdgcommons.lang.StringUtil;


/**
 *  A utility class for building HTML URLs that allows progressive build-out
 *  of the URL and deals with encoding and formatting.
 *  <p>
 *  Although this class supports various types of URLs, it exists primarily to
 *  build HTTP and HTTPS URLs, and to be convertable to a <code>java.net.URL</code>.
 *  To that end, it assumes that URLs will follow the format
 *  <code>protocol://host:port/path?query</code>, where <code>:port</code> is
 *  optional.
 *  <p>
 *  If you omit the hostname, this class will build "relative" URLs, which are
 *  useful for inserting into <code>href</code> attributes. If you omit the
 *  path, it will use the default path "/".
 *  <p>
 *  All public methods follow the Builder pattern, so that you can string
 *  calls together.
 */
public final class URLBuilder
{
    private StringBuilder _path = new StringBuilder(256);
    private StringBuilder _query = new StringBuilder(128);


    /**
     *  Creates a builder where the path is "/". Not terribly useful, except
     *  for testing.
     */
    public URLBuilder()
    {
        _path.append("/");
    }


    /**
     *  Creates a builder that specifies an explicit path (which may or may
     *  not contain hostname and protocol components). A blank path will be
     *  replaced by "/", but otherwise the passed path is unchanged.
     */
    public URLBuilder(String path)
    {
        if (StringUtil.isBlank(path))
            path = "/";
        _path.append(path);
    }


    /**
     *  Creates a builder from basic components.
     *
     *  @param  protocol    The protocol, sans colon and separator (eg, "http",
     *                      not "http://"). If <code>null</code>, will default
     *                      to HTTP.
     *  @param  host        The hostname, with optional port. May be <code>
     *                      null</code>, to create local URLs (in which case the
     *                      protocol will be omitted).
     *  @param  path        The context path. This is considered an absolute path
     *                      from the host's root, so will be prepended with a '/'
     *                      if it is not already; <code>null</code> or empty paths
     *                      will be replaced by "/". Since this path may contain
     *                      multiple slash-separated components, it will not be
     *                      URL-escaped; see {@link #appendPath} if you need to
     *                      escape path components.
     */
    public URLBuilder(String protocol, String host, String path)
    {
        if (StringUtil.isBlank(path))
            path = "/";

        if (host != null)
        {
            _path.append((protocol == null) ? "http" : protocol.toLowerCase())
             .append("://")
             .append(host);
        }

        _path.append(path.startsWith("/") ? "" : "/")
             .append(path);
    }


    /**
     *  Appends an element to the path, escaping any reserved characters.
     */
    public URLBuilder appendPath(String pathElement)
    {
        if (StringBuilderUtil.lastChar(_path) != '/')
            _path.append("/");
        _path.append(HtmlUtil.urlEncode(pathElement));
        return this;
    }


    /**
     *  Appends a query parameter, escaping as needed. A <code>null</code>
     *  value is treated as an empty string (so will appear as name=).
     */
    public URLBuilder appendParameter(String name, String value)
    {
        if (_query.length() > 0)
            _query.append("&");
        _query.append(HtmlUtil.urlEncode(name))
              .append("=")
              .append(HtmlUtil.urlEncode(value));
        return this;
    }


    /**
     *  Appends a query parameter, escaping as needed. If passed value is
     *  <code>null</code>, does not append anything.
     */
    public URLBuilder appendOptionalParameter(String name, String value)
    {
        return (value != null)
             ? appendParameter(name, value)
             : this;
    }


    /**
     *  Returns the string value of this URL. This method will rebuild the URL
     *  on each call, reflecting the state of the builder as-of the call.
     */
    @Override
    public String toString()
    {
        return (_query.length() > 0)
             ? _path.toString() + "?" + _query.toString()
             : _path.toString();
    }
}
