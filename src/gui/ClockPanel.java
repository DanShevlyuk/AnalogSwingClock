package gui;

import model.AwesomeClock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by Dan Shevlyuk
 */
public class ClockPanel extends JPanel {
    JPanel contentPanel;

    //model instance
    AwesomeClock clock;

    final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    //clock face params
    int clockFaceX;
    int clockFaceY;
    int clockFaceSize;

    //preferences
    boolean numbers;
    boolean roughRendering;
    boolean ticks;
    boolean whiteClockFace;
    boolean running;
    Color secondsArrowColor;
    Color clockFaceColor;

    public ClockPanel() {
        setDefaultPreferences();
        clock = new AwesomeClock(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        contentPanel = new JPanel();
        setSize(300, 300);
        setMinimumSize(new Dimension(100, 100));
        addMouseListener(new MouseClickedListener());
        setVisible(true);
        clock.start();
    }

    private class MouseClickedListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                setWhiteClockFace(!isWhiteClockFace());
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                setRunning(!isRunning());
            }

            repaint();
        }
    }

    /*
     * Default clock with black icon, white clock face,
     * red seconds arrow and beautiful(antialiasing on)
     */
    private void setDefaultPreferences() {
        whiteClockFace = true;
        clockFaceColor = Color.white;
        numbers = true;
        ticks = false;
        roughRendering = false;
        secondsArrowColor = Color.red;
        running = true;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D gg = (Graphics2D) g;
        setRenderingHints(gg);
        int iconHalf = drawIcon(gg);
        drawClockFace(gg, iconHalf);
        drawClockFaceComponents(gg, iconHalf);
    }

    //region drawing methods
    private int drawIcon(Graphics2D gg) {
        gg.setColor(getClockFaceComponentsColor());
        int iconHalf = this.getWidth() >= this.getHeight() ? this.getHeight() / 2 : this.getWidth() / 2;
        gg.fillRoundRect(0, 0, 2 * iconHalf, 2 * iconHalf, iconHalf, iconHalf);

        return iconHalf;
    }

    private void drawClockFace(Graphics2D gg, final int iconHalf) {
        gg.setColor(clockFaceColor);
        clockFaceX = iconHalf / 12;
        clockFaceY = iconHalf / 12;
        clockFaceSize  = iconHalf * 2 - iconHalf / 6;
        gg.fillOval(clockFaceX, clockFaceY, clockFaceSize, clockFaceSize);
    }

    private void drawClockFaceComponents(Graphics2D gg, final int iconHalf) {
        gg.setColor(getClockFaceComponentsColor());
        gg.fillOval(clockFaceX + clockFaceSize / 2 - iconHalf / 28,
                clockFaceY + clockFaceSize / 2 - iconHalf / 28, iconHalf / 14, iconHalf / 14);

        Point clockFaceCenter = new Point(clockFaceX + clockFaceSize/2, clockFaceY + clockFaceSize/2);

        if (numbers) {
            drawNumbers(gg, clockFaceCenter, clockFaceSize);
        }
        if (ticks) {
            drawTicks(gg, clockFaceCenter, clockFaceSize);
        }
        drawArrows(gg, clockFaceCenter, clockFaceSize);

        gg.setColor(secondsArrowColor);
        gg.fillOval(clockFaceX + clockFaceSize / 2 - iconHalf / 38,
                clockFaceY + clockFaceSize / 2 - iconHalf / 38, iconHalf / 19, iconHalf / 19);
    }

    private void drawArrows(Graphics2D gg, final Point clockFaceCenter, final int clockFaceSize) {
        gg.setStroke(new BasicStroke(clockFaceSize/43,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        gg.setColor(getClockFaceComponentsColor());
        //minutes
        Point pointOnCircle = getPointOnACircle(- clock.getMinutes() * 6, clockFaceCenter, clockFaceSize/2 - clockFaceSize/15);
        gg.drawLine(clockFaceX + clockFaceSize/2, clockFaceY + clockFaceSize/2, pointOnCircle.x, pointOnCircle.y);

        //hours
        pointOnCircle = getPointOnACircle(- clock.getHours() * 30, clockFaceCenter, clockFaceSize / 3);
        gg.drawLine(clockFaceX + clockFaceSize/2, clockFaceY + clockFaceSize/2, pointOnCircle.x, pointOnCircle.y);

        //seconds
        gg.setColor(secondsArrowColor);
        gg.setStroke(new BasicStroke(clockFaceSize/120, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        pointOnCircle = getPointOnACircle(- clock.getSeconds() * 6, clockFaceCenter, clockFaceSize/2 - clockFaceSize/15);
        Point pointOnCircle2 = getPointOnACircle(- clock.getSeconds() * 6 + 180, clockFaceCenter, clockFaceSize/10);
        gg.drawLine(clockFaceX + clockFaceSize/2, clockFaceY + clockFaceSize/2, pointOnCircle.x, pointOnCircle.y);
        gg.drawLine(clockFaceX + clockFaceSize/2, clockFaceY + clockFaceSize/2, pointOnCircle2.x, pointOnCircle2.y);
    }

    private void drawNumbers(Graphics2D gg, final Point clockFaceCenter, final int clockFaceSize) {
        gg.setColor(getClockFaceComponentsColor());
        gg.setStroke(new BasicStroke(clockFaceSize/15,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        gg.setFont(new Font("Seravek", 1, clockFaceSize/15));
        for (int i = 1; i <= 12; i++) {
            Point numberPlace = getPointOnACircle(- i * 30, clockFaceCenter, clockFaceSize/2 - clockFaceSize/15);
            gg.drawString(i + "", numberPlace.x - gg.getFont().getSize()/3, numberPlace.y + gg.getFont().getSize()/3);
        }
    }

    private void drawTicks(Graphics2D gg, final Point clockFaceCenter, final int clockFaceSize) {
        gg.setStroke(new BasicStroke(clockFaceSize/120, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        gg.setColor(getClockFaceComponentsColor());
        for (int i = 1; i <= 12; i++) {
            Point firstPoint = getPointOnACircle(- i * 30, clockFaceCenter, clockFaceSize/2);
            Point secondPoint = getPointOnACircle(- i * 30, clockFaceCenter, clockFaceSize/2 - clockFaceSize/50);
            gg.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
        }
    }

    //endregion

    private void setRenderingHints(Graphics2D gg) {
        if (roughRendering) {
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        } else {
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
    }

    /*
     * Method returns point on a circle with given radius, center
     * and angle from pi/2
     *
     * @param angle - angle between pi/2 and needed point
     * @param center - coordinates of circle center
     * @param circleSize - circle radius
     *
     * @returns new {@code Point} on a circle
     */
    private Point getPointOnACircle(double angle, Point center, int circleSize) {
        int x = center.x;
        int y = center.y + circleSize;
        double angle1 = angle * Math.PI / 180;
        double dx = x - center.x;
        double dy = y - center.y;
        double dxn = dx * Math.cos(angle1) - dy * Math.sin(angle1);
        double dyn = dx * Math.sin(angle1) - dy * Math.cos(angle1);
        x = (int)(center.x + dxn);
        y = (int)(center.y + dyn);
        return new Point(x, y);
    }

    //region getters and setters
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean isRunning) {
        boolean oldValue = this.isRunning();
        if (!this.running && isRunning) {
            clock.start();
        } else  if (this.running && !isRunning) {
            clock.stop();
        }
        this.running = isRunning;
        propertyChangeSupport.firePropertyChange("running", oldValue, this.running);
    }

    public boolean isWhiteClockFace() {
        return whiteClockFace;
    }

    public void setWhiteClockFace(boolean whiteClockFace) {
        boolean oldValue = this.whiteClockFace;
        if (whiteClockFace) {
            clockFaceColor = Color.white;
        } else {
            clockFaceColor = Color.black;
        }
        this.whiteClockFace = whiteClockFace;
        propertyChangeSupport.firePropertyChange("whiteClockFace", oldValue, this.whiteClockFace);
    }

    public boolean isTicks() {
        return ticks;
    }

    public void setTicks(boolean ticks) {
        boolean oldValue = this.ticks;
        this.ticks = ticks;
        propertyChangeSupport.firePropertyChange("ticks", oldValue, this.ticks);
    }

    public boolean isNumbers() {
        return numbers;
    }

    public void setNumbers(boolean numbers) {
        boolean oldValue = this.numbers;
        this.numbers = numbers;
        propertyChangeSupport.firePropertyChange("numbers", oldValue, this.numbers);
    }

    public boolean isRoughRendering() {
        return roughRendering;
    }

    public void setRoughRendering(boolean roughRendering) {
        boolean oldValue = this.roughRendering;
        this.roughRendering = roughRendering;
        propertyChangeSupport.firePropertyChange("roughRendering", oldValue, this.roughRendering);
    }

    public Color getSecondsArrowColor() {
        return secondsArrowColor;
    }

    private Color getClockFaceComponentsColor() {
        if (whiteClockFace) {
            return Color.black;
        } else {
            return Color.white;
        }
    }

    public void setSecondsArrowColor(Color secondsArrowColor) {
        Color oldValue = this.secondsArrowColor;
        this.secondsArrowColor = secondsArrowColor;
        propertyChangeSupport.firePropertyChange("secondsArrowColor", oldValue, this.secondsArrowColor);
    }

    public void setClockSeconds(int seconds) {
        int oldValue = getClockSeconds();
        clock.setSeconds(seconds);
        propertyChangeSupport.firePropertyChange("seconds", oldValue, getClockSeconds());
    }

    public int getClockSeconds() {
        return (int)clock.getSeconds();
    }

    public void setClockMinutes(int minutes) {
        int oldValue = getClockMinutes();
        clock.setMinutes(minutes);
        propertyChangeSupport.firePropertyChange("minutes", oldValue, getClockMinutes());
    }

    public int getClockMinutes() {
        return (int)clock.getMinutes();
    }

    public void setClockHours(int hours) {
        int oldValue = getClockHours();
        clock.setHours(hours);
        propertyChangeSupport.firePropertyChange("hours", oldValue, getClockHours());
    }

    public int getClockHours() {
        return (int)clock.getHours();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeSupportListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    //endregion

    public static void main(String[] args) {
        ClockPanel clockFrame = new ClockPanel();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(clockFrame);
        frame.setSize(400, 400);
        frame.setVisible(true);
     }
}
