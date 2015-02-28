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

package net.sf.kdgcommons.swing.layout;

import junit.framework.*;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


/**
 *  Test case for <CODE>CompactGridLayout</CODE>. Creates 7 labels and arranges
 *  them in different combinations of rows and columns, verifying that they're
 *  laid out as expected.
 *  <P>
 *  This testcase varies slightly from the JUnit norm. First, it doesn't have
 *  an automatic <CODE>setUp()</CODE> method. The tests do have a lot of common
 *  setup code, but it all depends on having the layout manager created before
 *  the common code is run. We changed the name, therefore, to "createFrame".
 */
public class TestCompactGridLayout extends TestCase
{
//------------------------------------------------------------------------------
//  Boilerplate
//------------------------------------------------------------------------------

    public TestCompactGridLayout(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(TestCompactGridLayout.class);
        return suite;
    }

//------------------------------------------------------------------------------
//  Common test code
//------------------------------------------------------------------------------

    private JFrame      _frame;
    private JPanel      _panel;
    private JLabel[]    _labels = new JLabel[]
                        {
                            new JLabel("ABC"),
                            new JLabel("D"),
                            new JLabel("E F G H I J K L"),
                            new JLabel("M"),
                            new JLabel("NOPQR"),
                            new JLabel("STUVWXY"),
                            new JLabel("Z")
                        };

    // these are filled on the event thread after layout ... in particular, the
    // label dimensions are created with the actual runtime labels, allowing a
    // test to swap out the "standard" set defined above
    private Rectangle   _panelBounds;
    private Rectangle[] _labelBounds;
    private Dimension[] _labelMinSizes;
    private Dimension[] _labelPrfSizes;


    /**
     *  Creates the frame and shows it, then retrieves the bounds and minimum/
     *  preferred sizes of the components. The caller is responsible for creating
     *  the panel (with layout manager) before calling this method.
     */
    public void createFrameAndRecordSizes()
    throws Exception
    {
        _frame = new JFrame("TestCompactGridLayout");
        _frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        for (int ii = 0 ; ii < _labels.length ; ii++)
            _panel.add(_labels[ii]);
        _frame.setContentPane(_panel);

        SwingUtilities.invokeAndWait(new Runnable()
        {
            public void run()
            {
                _frame.pack();
                _frame.setVisible(true);

                _panelBounds = _panel.getBounds();

                _labelBounds   = new Rectangle[_labels.length];
                _labelMinSizes = new Dimension[_labels.length];
                _labelPrfSizes = new Dimension[_labels.length];
                for (int ii = 0 ; ii < _labels.length ; ii++)
                {
                    _labelBounds[ii] = _labels[ii].getBounds();
                    _labelMinSizes[ii] = _labels[ii].getMinimumSize();
                    _labelPrfSizes[ii] = _labels[ii].getPreferredSize();
                }
            }
        });
    }


    /**
     *  Common test termination code. This is called automatically by JUnit; it
     *  just disposes of the test frame and its components.
     */
    @Override
    public void tearDown()
    throws Exception
    {
        SwingUtilities.invokeAndWait(new Runnable()
        {
            public void run()
            {
                _frame.dispose();
            }
        });
    }


//------------------------------------------------------------------------------
//  Test methods go here
//------------------------------------------------------------------------------

    /**
     *  Test of a single column. This column should be as wide as the widest
     *  label, and each row should be the height of the contained label. We
     *  can't verify the first point, but we can verify the second, checking
     *  that the first few labels are in the expected positions (assumes that
     *  the layout manager will loop properly). We also check that the bounds
     *  of the labels are equal to their preferred sizes.
     */
    public void test1x7()
    throws Exception
    {
        _panel = new JPanel(new CompactGridLayout(1));
        createFrameAndRecordSizes();

        int expectedX = 0;
        int expectedY = 0;

        assertEquals("label0", expectedX, _labelBounds[0].x);
        assertEquals("label0", expectedY, _labelBounds[0].y);
        assertEquals("label0", _labelPrfSizes[0].width, _labelBounds[0].width);
        assertEquals("label0", _labelPrfSizes[0].height, _labelBounds[0].height);

        expectedY += _labelBounds[0].height;

        assertEquals("label1", expectedX, _labelBounds[1].x);
        assertEquals("label1", expectedY, _labelBounds[1].y);
        assertEquals("label1", _labelPrfSizes[1].width, _labelBounds[1].width);
        assertEquals("label1", _labelPrfSizes[1].height, _labelBounds[1].height);

        expectedY += _labelBounds[1].height;

        assertEquals("label2", expectedX, _labelBounds[2].x);
        assertEquals("label2", expectedY, _labelBounds[2].y);
        assertEquals("label2", _labelPrfSizes[2].width, _labelBounds[2].width);
        assertEquals("label2", _labelPrfSizes[2].height, _labelBounds[2].height);
    }


    /**
     *  Tests a two-column layout (which translates to 4 rows). In this case,
     *  the layout should be (0, 1) / (2, 3) / (4, 5) / (6, 7). Row 0 height
     *  is the maximum of (0, 1); column 0 width is the maximum of (0, 2, 4, 6).
     */
    public void test2x4()
    throws Exception
    {
        _panel = new JPanel(new CompactGridLayout(2));
        createFrameAndRecordSizes();

        int row0Height = Math.max(_labelPrfSizes[0].height,
                                  _labelPrfSizes[1].height);
        int row1Height = Math.max(_labelPrfSizes[2].height,
                                  _labelPrfSizes[3].height);
        int row2Height = Math.max(_labelPrfSizes[4].height,
                                  _labelPrfSizes[5].height);
        int row3Height = _labelPrfSizes[6].height;

        int col0Width  = Math.max(_labelPrfSizes[0].width,
                         Math.max(_labelPrfSizes[2].width,
                         Math.max(_labelPrfSizes[4].width,
                                  _labelPrfSizes[6].width)));
        int col1Width  = Math.max(_labelPrfSizes[1].width,
                         Math.max(_labelPrfSizes[3].width,
                                  _labelPrfSizes[5].width));

        assertEquals("panel", (col0Width + col1Width),
                              _panelBounds.width);
        assertEquals("panel", (row0Height + row1Height + row2Height + row3Height),
                              _panelBounds.height);

        int expectedX = 0;
        int expectedY = 0;

        assertEquals("label0", expectedX, _labelBounds[0].x);
        assertEquals("label0", expectedY, _labelBounds[0].y);
        assertEquals("label0", _labelPrfSizes[0].width, _labelBounds[0].width);
        assertEquals("label0", _labelPrfSizes[0].height, _labelBounds[0].height);

        expectedX += col0Width;

        assertEquals("label1", expectedX, _labelBounds[1].x);
        assertEquals("label1", expectedY, _labelBounds[1].y);
        assertEquals("label1", _labelPrfSizes[1].width, _labelBounds[1].width);
        assertEquals("label1", _labelPrfSizes[1].height, _labelBounds[1].height);

        expectedX = 0;
        expectedY += row1Height;

        assertEquals("label2", expectedX, _labelBounds[2].x);
        assertEquals("label2", expectedY, _labelBounds[2].y);
        assertEquals("label2", _labelPrfSizes[2].width, _labelBounds[2].width);
        assertEquals("label2", _labelPrfSizes[2].height, _labelBounds[2].height);

        expectedX += col0Width;

        assertEquals("label3", expectedX, _labelBounds[3].x);
        assertEquals("label3", expectedY, _labelBounds[3].y);
        assertEquals("label3", _labelPrfSizes[3].width, _labelBounds[3].width);
        assertEquals("label3", _labelPrfSizes[3].height, _labelBounds[3].height);

        expectedX = 0;
        expectedY += row2Height;

        assertEquals("label4", expectedX, _labelBounds[4].x);
        assertEquals("label4", expectedY, _labelBounds[4].y);
        assertEquals("label4", _labelPrfSizes[4].width, _labelBounds[4].width);
        assertEquals("label4", _labelPrfSizes[4].height, _labelBounds[4].height);
    }


    /**
     *  A 3-column layout: (0, 1, 2) / (3, 4, 5) / (6, , ), that also tests
     *  gaps and insets.
     */
    public void test3x3()
    throws Exception
    {
        final int hGap = 7;
        final int vGap = 3;
        final int top  = 13;
        final int left = 11;
        final int bottom = 21;
        final int right = 17;

        _panel = new JPanel(new CompactGridLayout(3, hGap, vGap));
        _panel.setBorder(new EmptyBorder(top, left, bottom, right));

        createFrameAndRecordSizes();

        int row0Height = Math.max(_labelPrfSizes[0].height,
                         Math.max(_labelPrfSizes[1].height,
                                  _labelPrfSizes[2].height));
        int row1Height = Math.max(_labelPrfSizes[3].height,
                         Math.max(_labelPrfSizes[4].height,
                                  _labelPrfSizes[5].height));
        int row2Height = _labelPrfSizes[6].height;
        int col0Width  = Math.max(_labelPrfSizes[0].width,
                         Math.max(_labelPrfSizes[3].width,
                                  _labelPrfSizes[6].width));
        int col1Width  = Math.max(_labelPrfSizes[1].width,
                                  _labelPrfSizes[4].width);
        int col2Width  = Math.max(_labelPrfSizes[2].width,
                                  _labelPrfSizes[5].width);

        assertEquals("panel", (left + right
                                    + col0Width + col1Width + col2Width
                                    + 2 * hGap),
                              _panelBounds.width);
        assertEquals("panel", (top + bottom
                                   + row0Height + row1Height + row2Height
                                   + 2 * vGap),
                              _panelBounds.height);

        int expectedX = left;
        int expectedY = top;

        assertEquals("label0", expectedX, _labelBounds[0].x);
        assertEquals("label0", expectedY, _labelBounds[0].y);
        assertEquals("label0", _labelPrfSizes[0].width, _labelBounds[0].width);
        assertEquals("label0", _labelPrfSizes[0].height, _labelBounds[0].height);

        expectedX += col0Width + hGap;

        assertEquals("label1", expectedX, _labelBounds[1].x);
        assertEquals("label1", expectedY, _labelBounds[1].y);
        assertEquals("label1", _labelPrfSizes[1].width, _labelBounds[1].width);
        assertEquals("label1", _labelPrfSizes[1].height, _labelBounds[1].height);

        expectedX += col1Width + hGap;

        assertEquals("label2", expectedX, _labelBounds[2].x);
        assertEquals("label2", expectedY, _labelBounds[2].y);
        assertEquals("label2", _labelPrfSizes[2].width, _labelBounds[2].width);
        assertEquals("label2", _labelPrfSizes[2].height, _labelBounds[2].height);

        expectedX = left;
        expectedY += row0Height + vGap;

        assertEquals("label3", expectedX, _labelBounds[3].x);
        assertEquals("label3", expectedY, _labelBounds[3].y);
        assertEquals("label3", _labelPrfSizes[3].width, _labelBounds[3].width);
        assertEquals("label3", _labelPrfSizes[3].height, _labelBounds[3].height);

        expectedX += col0Width + hGap;

        assertEquals("label4", expectedX, _labelBounds[4].x);
        assertEquals("label4", expectedY, _labelBounds[4].y);
        assertEquals("label4", _labelPrfSizes[4].width, _labelBounds[4].width);
        assertEquals("label4", _labelPrfSizes[4].height, _labelBounds[4].height);

        expectedX = left;
        expectedY += row1Height + vGap;

        assertEquals("label6", expectedX, _labelBounds[6].x);
        assertEquals("label6", expectedY, _labelBounds[6].y);
        assertEquals("label6", _labelPrfSizes[6].width, _labelBounds[6].width);
        assertEquals("label6", _labelPrfSizes[6].height, _labelBounds[6].height);
    }
}
