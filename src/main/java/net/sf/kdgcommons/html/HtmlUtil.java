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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.kdgcommons.lang.StringUtil;


/**
 *  Static methods for working with HTML content, typically in a string.
 */
public class HtmlUtil
{
    /**
     *  A wrapper around <code>URLEncoder</code> that always encodes to UTF-8,
     *  replaces its checked exception with a RuntimeException (that should
     *  never be thrown), and encodes spaces as "%20" rather than "+".
     *  <p>
     *  If passed null, will return an empty string.
     */
    public static String urlEncode(String src)
    {
        if (src == null)
            return "";

        try
        {
            String encoded = URLEncoder.encode(src, "UTF-8");
            if (encoded.indexOf('+') >= 0)
                encoded = encoded.replace((CharSequence)"+", (CharSequence)"%20");
            return encoded;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("this JVM doesn't support UTF-8!", e);
        }
    }


    /**
     *  A wrapper around <code>URLDecoder</code> that always decodes as
     *  UTF-8, and replaces its checked exception with a RuntimeException
     *  (that should never be thrown).
     *  <p>
     *  If passed null, will return an empty string.
     */
    public static String urlDecode(String src)
    {
        if (src == null)
            return "";

        try
        {
            return URLDecoder.decode(src, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("this JVM doesn't support UTF-8!", e);
        }
    }


    /**
     *  Replaces characters in the passed string with entities:
     *  <ul>
     *  <li> &lt; replaced with &amp;lt;
     *  <li> &gt; replaced with &amp;gt;
     *  <li> &amp; replaced with &amp;amp;
     *  <li> " replaced with &amp;quot;
     *  <li> ' replaced with &amp;%39;
     *  </ul>
     *  If passed <code>null</code>, returns an empty string.
     */
    public static String escape(String src)
    {
        if (src == null)
            return "";

        StringBuilder sb = new StringBuilder(src.length() * 5 /4);
        for (int ii = 0 ; ii < src.length() ; ii++)
        {
            char c = src.charAt(ii);
            switch (c)
            {
                case '&' :
                    sb.append("&amp;");
                    break;
                case '<' :
                    sb.append("&lt;");
                    break;
                case '>' :
                    sb.append("&gt;");
                    break;
                case '\'' :
                    sb.append("&%39;");
                    break;
                case '\"' :
                    sb.append("&quot;");
                    break;
                default :
                    sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     *  Replaces entity references in the passed string with their character
     *  values. Supports named entity references from the HTML 4.01 spec
     *  (http://www.w3.org/TR/html401), along with numeric references. Any
     *  unrecognized references or invalid character sequences (ie, &amp;
     *  without a corresponding ;) are passed through unchanged.
     *  <p>
     *  If passed <code>null</code>, returns an empty string. If passed a
     *  string without any entity escapes, returns that string unchanged.
     */
    public static String unescape(String src)
    {
        if (src == null)
            return "";

        if (src.indexOf('&') < 0)
            return src;

        StringBuilder sb = new StringBuilder(src.length());
        for (int ii = 0 ; ii < src.length() ; )
        {
            int jj = src.indexOf('&', ii);
            int kk = (jj < 0) ? -1 : src.indexOf(';', jj);
            if ((jj < 0) || (kk < 0))
            {
                sb.append(src.substring(ii));
                break;
            }

            sb.append(src.substring(ii, jj));

            String entity = src.substring(jj + 1, kk);
            if (entityLookup.containsKey(entity))
            {
                sb.append(entityLookup.get(entity).charValue());
            }
            else if ((entity.length() > 1) && (entity.charAt(0) == '#'))
            {
                int radix = 10;
                entity = entity.substring(1);
                if ((entity.charAt(0) == 'x') || (entity.charAt(0) == 'X'))
                {
                    entity = entity.substring(1);
                    radix = 16;
                }
                char c = (char)(Integer.parseInt(entity, radix) & 0xFFFF);
                sb.append(c);
            }
            else
            {
                sb.append('&').append(entity).append(';');
            }

            ii = kk + 1;
        }
        return sb.toString();
    }


    /**
     *  Appends an escaped attribute (name='value') to the passed buffer,
     *  which is assumed to contain an in-process HTML element string.
     *  <p>
     *  If <code>value</code> is <code>null</code>, will append the
     *  attribute with an empty string value. See {@link
     *  #appendOptionalAttribute} if you want to skip null values.
     */
    public static void appendAttribute(StringBuilder buf, String name, Object value)
    {
        value = (value == null) ? "" : value;
        buf.append(" ")
           .append(name)
           .append("='")
           .append(escape(value.toString()))
           .append("'");
    }


    /**
     *  Appends an escaped attribute (name='value') to the passed buffer,
     *  which is assumed to contain an in-process HTML element string, iff
     *  the attribute value is neither <code>null</code> nor an empty string.
     */
    public static void appendOptionalAttribute(StringBuilder buf, String name, Object value)
    {
        if (value == null)
            return;

        String sValue = value.toString();
        if (sValue.length() > 0)
            appendAttribute(buf, name, sValue);
    }


    /**
     *  Constructs a query string out of a parameter map, URL-encoding values.
     *  This is often simpler than {@link URLBuilder}, but does not support
     *  multiple values per parameter.
     *
     *  @param  map         The map of parameter values. This must contain only
     *                      scalar values: both key and value are converted to
     *                      strings before being added to the query string.
     *  @param  ignoreEmpty If <code>true</code>, entries with empty or null
     *                      values are not added to the string; if
     *                      <code>false</code>, they're added in the form
     *                      "<code>name=</code>"
     *  @return The query string, consisting of "<code>name=value</code>" pairs
     *          separated by ampersands. There's no leading question mark.
     */
    public static String buildQueryString(Map<?,?> params, boolean ignoreEmpty)
    {
        StringBuilder buf = new StringBuilder(params.size() * 32);
        for (Map.Entry<?,?> param : params.entrySet())
        {
            String name = String.valueOf(param.getKey());
            String value = (param.getValue() == null)
                         ? ""
                         : String.valueOf(param.getValue());

            if (StringUtil.isEmpty(value) && ignoreEmpty)
                continue;

            buf.append(buf.length() > 0 ? "&" : "")
               .append(name)
               .append("=")
               .append(urlEncode(value));
        }
        return buf.toString();
    }


    /**
     *  Parses a query string into a parameter map, decoding values as needed.
     *  Does not support multiple parameter values; later values will overwrite
     *  earlier.
     *
     *  @param  query       The query string: zero or more <code>name=value</code>
     *                      pairs separated by ampersands, with or without a
     *                      leading question mark.
     *  @param  ignoreEmpty If <code>true</code>, ignores any entries without a
     *                      value (eg, "<code>name=</code>"; if <code>false</code>
     *                      these are added to the map with an empty string for
     *                      the value.
     *  @return A map of the name-value pairs. Caller is permitted to modify this
     *          map.
     *  @throws RuntimeException on any failure.
     */
    public static Map<String,String> parseQueryString(String query, boolean ignoreEmpty)
    {
        Map<String,String> result = new HashMap<String,String>();
        if ((query == null) || (query.length() == 0))
            return result;

        if (query.charAt(0) == '?')
            query = query.substring(1);
        // need to repeat test for empty string
        if (query.length() == 0)
            return result;

        for (String param : query.split("&"))
        {
            // why not use split again? because it doesn't handle a missing '='
            int delimIdx = param.indexOf('=');
            if (delimIdx < 0)
                throw new RuntimeException("unparsable parameter: " + param);

            String name = param.substring(0, delimIdx);
            String value = param.substring(delimIdx + 1);

            if ((value.length() > 0) || !ignoreEmpty)
                result.put(name, urlDecode(value));
        }

        return result;
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    // lookup table: entity names to their corresponding characters
    private static Map<String,Character> entityLookup = new HashMap<String,Character>();
    static
    {
        entityLookup.put("AElig",   Character.valueOf((char)198));
        entityLookup.put("Aacute",  Character.valueOf((char)193));
        entityLookup.put("Acirc",   Character.valueOf((char)194));
        entityLookup.put("Agrave",  Character.valueOf((char)192));
        entityLookup.put("Aring",   Character.valueOf((char)197));
        entityLookup.put("Atilde",  Character.valueOf((char)195));
        entityLookup.put("Auml",    Character.valueOf((char)196));
        entityLookup.put("Ccedil",  Character.valueOf((char)199));
        entityLookup.put("ETH",     Character.valueOf((char)208));
        entityLookup.put("Eacute",  Character.valueOf((char)201));
        entityLookup.put("Ecirc",   Character.valueOf((char)202));
        entityLookup.put("Egrave",  Character.valueOf((char)200));
        entityLookup.put("Euml",    Character.valueOf((char)203));
        entityLookup.put("Iacute",  Character.valueOf((char)205));
        entityLookup.put("Icirc",   Character.valueOf((char)206));
        entityLookup.put("Igrave",  Character.valueOf((char)204));
        entityLookup.put("Iuml",    Character.valueOf((char)207));
        entityLookup.put("Ntilde",  Character.valueOf((char)209));
        entityLookup.put("Oacute",  Character.valueOf((char)211));
        entityLookup.put("Ocirc",   Character.valueOf((char)212));
        entityLookup.put("Ograve",  Character.valueOf((char)210));
        entityLookup.put("Oslash",  Character.valueOf((char)216));
        entityLookup.put("Otilde",  Character.valueOf((char)213));
        entityLookup.put("Ouml",    Character.valueOf((char)214));
        entityLookup.put("THORN",   Character.valueOf((char)222));
        entityLookup.put("Uacute",  Character.valueOf((char)218));
        entityLookup.put("Ucirc",   Character.valueOf((char)219));
        entityLookup.put("Ugrave",  Character.valueOf((char)217));
        entityLookup.put("Uuml",    Character.valueOf((char)220));
        entityLookup.put("Yacute",  Character.valueOf((char)221));
        entityLookup.put("aacute",  Character.valueOf((char)225));
        entityLookup.put("acirc",   Character.valueOf((char)226));
        entityLookup.put("acute",   Character.valueOf((char)180));
        entityLookup.put("aelig",   Character.valueOf((char)230));
        entityLookup.put("agrave",  Character.valueOf((char)224));
        entityLookup.put("amp",     Character.valueOf('&'));
        entityLookup.put("apos",    Character.valueOf('\''));
        entityLookup.put("aring",   Character.valueOf((char)229));
        entityLookup.put("atilde",  Character.valueOf((char)227));
        entityLookup.put("auml",    Character.valueOf((char)228));
        entityLookup.put("brvbar",  Character.valueOf((char)166));
        entityLookup.put("ccedil",  Character.valueOf((char)231));
        entityLookup.put("cedil",   Character.valueOf((char)184));
        entityLookup.put("cent",    Character.valueOf((char)162));
        entityLookup.put("copy",    Character.valueOf((char)169));
        entityLookup.put("curren",  Character.valueOf((char)164));
        entityLookup.put("deg",     Character.valueOf((char)176));
        entityLookup.put("divide",  Character.valueOf((char)247));
        entityLookup.put("eacute",  Character.valueOf((char)233));
        entityLookup.put("ecirc",   Character.valueOf((char)234));
        entityLookup.put("egrave",  Character.valueOf((char)232));
        entityLookup.put("eth",     Character.valueOf((char)240));
        entityLookup.put("euml",    Character.valueOf((char)235));
        entityLookup.put("frac12",  Character.valueOf((char)189));
        entityLookup.put("frac14",  Character.valueOf((char)188));
        entityLookup.put("frac34",  Character.valueOf((char)190));
        entityLookup.put("gt",      Character.valueOf('>'));
        entityLookup.put("iacute",  Character.valueOf((char)237));
        entityLookup.put("icirc",   Character.valueOf((char)238));
        entityLookup.put("iexcl",   Character.valueOf((char)161));
        entityLookup.put("igrave",  Character.valueOf((char)236));
        entityLookup.put("iquest",  Character.valueOf((char)191));
        entityLookup.put("iuml",    Character.valueOf((char)239));
        entityLookup.put("laquo",   Character.valueOf((char)171));
        entityLookup.put("lt",      Character.valueOf('<'));
        entityLookup.put("macr",    Character.valueOf((char)175));
        entityLookup.put("micro",   Character.valueOf((char)181));
        entityLookup.put("middot",  Character.valueOf((char)183));
        entityLookup.put("nbsp",    Character.valueOf((char)160));
        entityLookup.put("not",     Character.valueOf((char)172));
        entityLookup.put("ntilde",  Character.valueOf((char)241));
        entityLookup.put("oacute",  Character.valueOf((char)243));
        entityLookup.put("ocirc",   Character.valueOf((char)244));
        entityLookup.put("ograve",  Character.valueOf((char)242));
        entityLookup.put("ordf",    Character.valueOf((char)170));
        entityLookup.put("ordm",    Character.valueOf((char)186));
        entityLookup.put("oslash",  Character.valueOf((char)248));
        entityLookup.put("otilde",  Character.valueOf((char)245));
        entityLookup.put("ouml",    Character.valueOf((char)246));
        entityLookup.put("para",    Character.valueOf((char)182));
        entityLookup.put("plusmn",  Character.valueOf((char)177));
        entityLookup.put("pound",   Character.valueOf((char)163));
        entityLookup.put("quot",    Character.valueOf('\"'));
        entityLookup.put("raquo",   Character.valueOf((char)187));
        entityLookup.put("reg",     Character.valueOf((char)174));
        entityLookup.put("sect",    Character.valueOf((char)167));
        entityLookup.put("shy",     Character.valueOf((char)173));
        entityLookup.put("sup1",    Character.valueOf((char)185));
        entityLookup.put("sup2",    Character.valueOf((char)178));
        entityLookup.put("sup3",    Character.valueOf((char)179));
        entityLookup.put("szlig",   Character.valueOf((char)223));
        entityLookup.put("thorn",   Character.valueOf((char)254));
        entityLookup.put("times",   Character.valueOf((char)215));
        entityLookup.put("uacute",  Character.valueOf((char)250));
        entityLookup.put("ucirc",   Character.valueOf((char)251));
        entityLookup.put("ugrave",  Character.valueOf((char)249));
        entityLookup.put("uml",     Character.valueOf((char)168));
        entityLookup.put("uuml",    Character.valueOf((char)252));
        entityLookup.put("yacute",  Character.valueOf((char)253));
        entityLookup.put("yen",     Character.valueOf((char)165));
        entityLookup.put("yuml",    Character.valueOf((char)255));
    }
  }
