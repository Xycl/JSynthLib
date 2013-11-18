/*
 * DKnob.java
 *
 * (c) 2000 by Joakim Eriksson
 */

package com.dreamfabric;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * This class provides a component similar to a JSlider but with a round "user
 * interface", a knob.
 * @author Joakim Eriksson
 * @author Denis Queffeulou
 */
public class DKnob extends JComponent {
    private static final long serialVersionUID = 1L;

    public final static int DEFAULT_WIDTH = 60;
    public final static int DEFAULT_HEIGHT = 45;
    public final static int SIMPLE = 1;
    public final static int ROUND = 2;
    public final static int SIMPLE_MOUSE_DIRECTION = 3;

    private static final float START = 225;
    private static final float LENGTH = 270;
    private static final float START_ANG = (START / 360) * (float) Math.PI * 2;
    private static final float LENGTH_ANG = (LENGTH / 360) * (float) Math.PI
            * 2;
    private static final float LENGTH_ANG_DIV10 = (float) (LENGTH_ANG / 10.01);
    private static final float MULTIP = 180 / (float) Math.PI;
    private static final Color DEFAULT_FOCUS_COLOR = new Color(0x8080ff);
    private static final Dimension MIN_SIZE = new Dimension(21, 25);
    private static final Dimension PREF_SIZE = new Dimension(DEFAULT_WIDTH,
            DEFAULT_HEIGHT);
    private static final float MID_OFFSET = (float) 1. / 220;
    // Set the antialiasing to get the right look!
    private static final RenderingHints AALIAS = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    private int dragType = SIMPLE_MOUSE_DIRECTION;
    private int shadowX = 1;
    private int shadowY = 1;
    private float dragSpeed = 0.0075F;
    private float clickSpeed = 0.01F;
    private int size;
    private int middle;
    private EventListenerList changeListeners = new EventListenerList();
    private Arc2D hitArc = new Arc2D.Float(Arc2D.PIE);
    private float ang = START_ANG;
    private float val;
    private int dragpos = -1;
    private float startVal;
    private Color focusColor = DEFAULT_FOCUS_COLOR;
    private double lastAng;
    private int mWidth;
    private int mHeight;
    private String valueLabel;

    /**
     * Construct a knob of the default size.
     */
    public DKnob() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Construct a knob of a specific size.
     * @param width
     *            the width of the knob
     * @param height
     *            the height of the knob
     */
    public DKnob(final int width, final int height) {
        mWidth = width;
        mHeight = height;

        setPreferredSize(new Dimension(width - 19, height));

        hitArc.setAngleStart(235); // Degrees ??? Radians???

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                dragpos = event.getX() + event.getY();
                startVal = val;

                // Fix last angle
                int xpos = middle - event.getX();
                int ypos = middle - event.getY();
                lastAng = Math.atan2(xpos, ypos);

                requestFocus();
            }

            @Override
            public void mouseClicked(final MouseEvent event) {
                hitArc.setAngleExtent(-(LENGTH + 20));
                if (hitArc.contains(event.getX(), event.getY())) {
                    hitArc.setAngleExtent(MULTIP * (ang - START_ANG) - 10);
                    if (hitArc.contains(event.getX(), event.getY())) {
                        decValue();
                    } else {
                        incValue();
                    }
                }
            }
        });

        // Let the user control the knob with the mouse
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent event) {
                float speed = dragSpeed;
                if ((event.getModifiersEx() & (InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK)) != 0) {
                    speed /= 10;
                }
                if ((event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                    speed /= 10;
                }
                if ((event.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
                    speed /= 10;
                }

                if (dragType == SIMPLE) {
                    float f = speed * ((event.getX() + event.getY()) - dragpos);
                    setValue(startVal + f);
                } else if (dragType == SIMPLE_MOUSE_DIRECTION) {
                    float f = (speed * (event.getX() + event.getY() - dragpos));
                    setValue(startVal - f);
                } else if (dragType == ROUND) {
                    // Measure relative the middle of the button!
                    int xpos = middle - event.getX();
                    int ypos = middle - event.getY();
                    double ang = Math.atan2(xpos, ypos);
                    double diff = lastAng - ang;
                    setValue((float) (getValue() + (diff / LENGTH_ANG)));

                    lastAng = ang;
                }
            }
        });

        // Let the user control the knob with the keyboard
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent event) {
                switch (event.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
                    incValue();
                    break;
                case KeyEvent.VK_LEFT:
                    decValue();
                    break;
                }
            }
        });

        // Handle focus so that the knob gets the correct focus highlighting.
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent event) {
                repaint();
            }

            @Override
            public void focusLost(final FocusEvent event) {
                valueLabel = null;
                repaint();
            }
        });
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public Dimension getMinimumSize() {
        return MIN_SIZE;
    }

    /**
     * Paint the DKnob
     */
    @Override
    public void paint(final Graphics g) {
        if (isEnabled()) {
            // denis: i set the size because I can't deal with layout and resize
            // consequences
            int width = mWidth; // getWidth();
            int height = mHeight; // getHeight();
            // size = Math.min(width, height) - 22;
            // the drawing is wider than it is tall
            size = width / 2; // - 22;
            int oSizeDiv2 = size / 2;
            int oOffset = 3; // 10
            middle = oOffset + oSizeDiv2;

            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setBackground(getParent().getBackground());
                g2d.addRenderingHints(AALIAS);

                // For the size of the "mouse click" area
                hitArc.setFrame(4, 4, size + 12, size + 12);
            }

            // // Paint the "markers"
            // for(float a2 = START_ANG; a2 >= START_ANG - LENGTH_ANG; a2 -=
            // LENGTH_ANG_DIV10) {
            // int x = oOffset + oSizeDiv2 + (int)((6 + oSizeDiv2) *
            // Math.cos(a2));
            // int y = oOffset + oSizeDiv2 - (int)((6 + oSizeDiv2) *
            // Math.sin(a2));
            // g.drawLine(oOffset + oSizeDiv2, oOffset + oSizeDiv2, x, y);
            // }

            // Paint focus if in focus
            if (hasFocus()) {
                g.setColor(focusColor);
            } else {
                g.setColor(Color.white);
            }

            // g.fillOval(oOffset, oOffset, size, size);
            g.setColor(Color.gray);
            // g.fillOval(oOffset + 4 + shadowX, oOffset + 4 + shadowY, size -
            // 8, size - 8);

            g.setColor(Color.black);
            // g.drawArc(oOffset, oOffset, size, size, 315, 270);
            g.fillOval(oOffset + 4, oOffset + 4, size - 8, size - 8);
            g.setColor(Color.white);

            // center of knob is at (oOffset + oSizeDiv2, oOffset + oSizeDiv2)
            // make the line 2 pixels wide
            double oCos = Math.cos(ang);
            double oSin = Math.sin(ang);
            int dx = (int) (2 * oSin);
            int dy = (int) (2 * oCos);
            // compute 'right-hand' vertex of needle at center
            int x = oOffset + oSizeDiv2 + (int) ((oSizeDiv2 - 2) * oCos);
            int y = oOffset + oSizeDiv2 - (int) ((oSizeDiv2 - 2) * oSin);

            // denis: i prefer rectangular look for indicator needle
            int s2 = Math.max(size / 6, 6);
            g.setColor(Color.gray);
            // g.drawOval(oOffset + s2, oOffset + s2, size - s2 * 2 - 1, size -
            // s2 * 2 - 1);

            int xPoints[] =
                    new int[] {
                            oOffset + dx + oSizeDiv2, x + dx, x - dx,
                            oOffset - dx + oSizeDiv2 };
            int yPoints[] =
                    new int[] {
                            oOffset + dy + oSizeDiv2, y + dy, y - dy,
                            oOffset - dy + oSizeDiv2 };
            // denis: hightlight dead center
            if (val > (0.5 - MID_OFFSET) && val < (0.5 + MID_OFFSET)) {
                g.setColor(Color.white);
            } else {
                g.setColor(Color.lightGray);
            }
            g.fillPolygon(xPoints, yPoints, 4);
            // round tail of needle
            // g.fillOval(oOffset + oSizeDiv2 - 1, oOffset + oSizeDiv2 - 1, 2,
            // 2);

            // denis: display value
            // if(valueLabel != null) {
            // g.setColor(Color.white);
            // g.drawString(valueLabel, oSizeDiv2 - 5, height - 10);
            // }
        }
    }

    public int getDragType() {
        return dragType;
    }

    public void setDragType(final int type) {
        dragType = type;
    }

    /**
     * Get the knob value.
     * @return the value
     */
    public float getValue() {
        return val;
    }

    /**
     * Set the knob value. The value will be normalised such that
     * <code>v &gt;= 0.0 && v &lt;= 1.0</code>.
     * @param v
     *            the value
     */
    public void setValue(final float v) {
        if (v < 0) {
            val = 0;
        } else if (v > 1) {
            val = 1;
        } else {
            val = v;
        }
        ang = START_ANG - LENGTH_ANG * val;
        repaint();
        fireChangeEvent();
    }

    /**
     * Set the knob value label.
     * @param vl
     *            the value label (null to unset)
     */
    public void setValueLabel(final String vl) {
        valueLabel = vl;
    }

    /**
     * Add a listener for notifications that the knob value has changed.
     * @param listener
     *            the listener to add
     */
    public void addChangeListener(final ChangeListener listener) {
        changeListeners.add(ChangeListener.class, listener);
    }

    /**
     * Remove a listener for notifications that the knob value has changed.
     * @param listener
     *            the listener to remove
     */
    public void removeChangeListener(final ChangeListener listener) {
        changeListeners.remove(ChangeListener.class, listener);
    }

    /**
     * Fire a notification that the knob value has changed.
     */
    private void fireChangeEvent() {
        ChangeEvent event = null;
        Object[] listeners = changeListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (event == null) {
                    event = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(event);
            }
        }
    }

    /**
     * Increment the knob value.
     */
    private void incValue() {
        setValue(val + clickSpeed);
    }

    /**
     * Decrement the knob value.
     */
    private void decValue() {
        setValue(val - clickSpeed);
    }
}
