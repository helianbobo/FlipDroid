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

    protected int cycle = 0;

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

    public Window getNextWindow() throws  WindowException {
        lock.lock();
        Window currentWindow = null;
        try {
            increaseWindowIndex();
            //Log.d("SLIDING", "begins returning Window: current:" + (current) + "cycle:" + cycle);
            currentWindow = windows[current];
            if (currentWindow == null || currentWindow.isLastWindow) {
                currentWindow = getLastWindow();
                return currentWindow;
            }
            //Log.d("SLIDING", "current window is:" + currentWindow);
            int bufferStep = getBufferStep();
            for (int i = 0; i < bufferStep; i++) {
                createWindowIfNullOrOld((current + i) % slotNumber, cycle * slotNumber + current + i);
            }


            if (currentWindow.loading) {
                //Log.d("SLIDING", currentWindow + " is being loaded: current:" + (current) + "cycle:" + cycle);
                return currentWindow;
            } else if (currentWindow.isLoaded()) {
//                if (currentWindow.pageNumber / slotNumber <= cycle) {
//                    //Log.d("SLIDING", currentWindow + " loaded, return it: current:" + (current) + "cycle:" + cycle);
                    return currentWindow;
//                }
            } else {
                return currentWindow;
            }
        } finally {
            Log.d("SLIDING", currentWindow + " is being returned: current:" + (current) + "cycle:" + cycle);
            lock.unlock();
        }
    }

    protected abstract Window getLastWindow();

    protected abstract void createWindowIfNullOrOld(int arratPosition, int pageNumber);


    public Window getPreviousWindow() throws WindowException {
        lock.lock();
        Window currentWindow = null;
        try {
            decreaseWindowIndex();
            if (cycle == -1) {
                currentWindow = getFirstWindow();
                return currentWindow;
            }
            currentWindow = windows[current];

            int bufferStep = getBufferStep();
            for (int i = 0; i < bufferStep; i++) {
                if (current - i >= 0) {
                    int pageNumber = cycle * slotNumber + current - i - 1;
                    if (pageNumber >= 0)
                        createWindowIfNullOrOld(getNextIndex(-i - 1), cycle * slotNumber + current - i - 1);
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
            Log.d("SLIDING", currentWindow + " is being returned: current:" + (current) + "cycle:" + cycle);
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
