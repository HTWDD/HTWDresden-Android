package de.htwdd.htwdresden.classes;

/**
 * Hilfsklasse um die Anzahl der laufenden Requests zu zählen
 */
public class QueueCount {
    private long countQueue = 0;
    public boolean update;

    public synchronized void incrementCountQueue(){
        countQueue++;
    }

    public synchronized void decrementCountQueue(){
        countQueue--;
    }

    public synchronized long getCountQueue() {
        return countQueue;
    }
}
