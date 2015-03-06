package model;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

/**
 * Created by Dan Shevlyuk
 */
public class AwesomeClock {
    private double hours;
    private double minutes;
    private double seconds;
    private Timer timer;
    private Calendar calendar;
    final private double secondsIncrement = 0.01;

    public AwesomeClock(ActionListener timeUpdatesListener) {
        calendar = Calendar.getInstance();
        seconds = calendar.get(Calendar.SECOND);
        minutes = calendar.get(Calendar.MINUTE) + seconds * (secondsIncrement / 60) * 100;
        hours = calendar.get(Calendar.HOUR) + minutes * (secondsIncrement / 60) * 100;

        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds += secondsIncrement;
                minutes += secondsIncrement / 60;
                hours += secondsIncrement / 3600;
                checkTime();
            }
        });
        timer.addActionListener(timeUpdatesListener);
    }

    private void checkTime() {
        if (seconds >= 60) {
            seconds = 0;
        }
        if (hours >= 12) {
            hours = 0;
        }
        if (minutes >= 60) {
            minutes = 0;
        }
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public double getHours() {
        return hours;
    }

    public double getMinutes() {
        return minutes;
    }

    public double getSeconds() {
        return seconds;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public void setMinutes(double minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}