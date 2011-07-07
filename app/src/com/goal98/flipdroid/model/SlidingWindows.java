package com.goal98.flipdroid.model;

import android.util.Log;
import com.goal98.flipdroid.exception.LastWindowException;
import com.goal98.flipdroid.exception.WindowException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/30/11
 * Time: 7:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SlidingWindows {
    protected int slotNumber;

    private int cycle = 0;

    protected Window[] windows;

    private int current = -1;

    public Lock lock = new ReentrantLock();
    private int step;


    public SlidingWindows(int worker) {
        this.slotNumber = worker;
        this.windows = new Window[worker];
    }

    public SlidingWindows(int worker, int step) {
        this(worker);
        this.step = step;
    }

    public int getBufferStep() {
        if (step != 0)
            return step;
        return (slotNumber + 1) / 2;
    }

    public Window getNextWindow() throws LastWindowException, WindowException {
        lock.lock();
        try {
            increaseWindowIndex();
            //Log.d("SLIDING", "begins returning Window: current:" + (current) + "cycle:" + cycle);
            Window currentWindow = windows[current];

            //Log.d("SLIDING", "current window is:" + currentWindow);
            int bufferStep = getBufferStep();
            for (int i = 0; i < bufferStep; i++) {
                createWindowIfNullOrOld((current + i) % slotNumber, cycle * slotNumber + current + i);
            }

            if (currentWindow.isLastWindow) {
                //Log.d("SLIDING", "last Window:" + currentWindow + ", current:" + (current) + "cycle:" + cycle);
                throw new LastWindowException();
            }
            if (currentWindow.loading) {
                //Log.d("SLIDING", currentWindow + " is being loaded: current:" + (current) + "cycle:" + cycle);
                return currentWindow;
            } else if (currentWindow.isLoaded()) {
                if (currentWindow.pageNumber / slotNumber <= cycle) {
                    //Log.d("SLIDING", currentWindow + " loaded, return it: current:" + (current) + "cycle:" + cycle);
                    return currentWindow;
                } else {
                    //Log.d("SLIDING", "cycle met:" + ": current: " + currentWindow + ", " + (currentWindow.pageNumber / slotNumber) + "cycle:" + cycle);
                    throw new LastWindowException();
                }
            } else {
                return currentWindow;
            }
        } finally {
            lock.unlock();
        }
    }

    protected abstract void createWindowIfNullOrOld(int arratPosition, int pageNumber);


    public Window getPreviousWindow() throws WindowException {
        lock.lock();
        try {
            decreaseWindowIndex();
            if(cycle == -1){
                return getFirstWindow();
            }
            Window currentWindow = windows[current];

            int bufferStep = getBufferStep();
            for (int i = 0; i < bufferStep; i++) {
                if (current - i >= 0) {
                    //Log.d("SLIDING", currentWindow + " is being loaded2: current:" + (current) + "cycle:" + cycle);
                    createWindowIfNullOrOld(getNextIndex(-i), cycle * slotNumber + current - i);
                }
            }

            if (currentWindow.isLoaded()) {
                return currentWindow;
            } else if (currentWindow.skip) {
                return currentWindow;
            } else {
                currentWindow.get();
                return currentWindow;
            }
        } finally {
            lock.unlock();
        }
    }

    protected abstract Window getFirstWindow();

    public void loadIfNot(Window window) {
        if (!window.isLoaded())
            window.startTask();
    }

    private int getNextIndex(int step) {
        step = step % slotNumber;
//        cycle += (step / numberOfWorker);
        if ((current + step) >= 0)
            return (current + step) % slotNumber;
        else
            return (current + step) + slotNumber;
    }

    public void increaseWindowIndex() {
        lock.lock();
        try {
            current++;
            if (current == slotNumber) {
                current = 0;
                cycle++;
            }
        } finally {
            lock.unlock();
        }
    }

    public void decreaseWindowIndex() {
        lock.lock();
        try {
            current--;
            if (current == -1) {
                current = slotNumber - 1;
                cycle--;
            }
        } finally {
            lock.unlock();
        }
    }


}
