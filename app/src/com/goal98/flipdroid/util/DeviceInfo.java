package com.goal98.flipdroid.util;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/26/11
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeviceInfo {
    private int displayWidth;
    private int displayHeight;
    private int width;
    private int height;

    public int getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public DeviceInfo(){

    }

    public boolean isLargeScreen(){
        return height >= 800;
    }

    public boolean isSmallScreen(){
        return height == 320;
    }
}
