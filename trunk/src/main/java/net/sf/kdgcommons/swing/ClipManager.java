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

package net.sf.kdgcommons.swing;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


/**
 *  Provides a simple mechanism for interacting with the system clipboard,
 *  where you want to copy/paste a restricted set of data types and don't
 *  want to hook up with a component. Particularly useful for application-
 *  level actions.
 *  
 *  @since 1.1.0
 */
public class ClipManager
{
    /**
     *  Puts the specified string on the system clipboard.
     */
    public static void putString(String str)
    {
        Toolkit.getDefaultToolkit()
               .getSystemClipboard()
               .setContents(new StringClip(str), new ClipCallback());
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  An object that's notified when the clipboard contents changes. We
     *  don't really care about that notification, but the clipboard requires
     *  a callback.
     */
    private static class ClipCallback
    implements ClipboardOwner
    {
        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {
            // nothing happening here
        }
    }


    /**
     *  Used to package a string for the clipboard.
     */
    private static class StringClip
    implements Transferable
    {
        private String _str;
        private DataFlavor _myFlavor;

        public StringClip(String str)
        {
            _str = str;
            _myFlavor = DataFlavor.stringFlavor;
        }

        public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException
        {
            if (!flavor.equals(_myFlavor))
                return null;

            return _str;
        }

        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[] { _myFlavor };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return flavor.equals(_myFlavor);
        }
    }
}
