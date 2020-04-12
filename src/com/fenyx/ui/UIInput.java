package com.fenyx.ui;

import java.util.HashMap;

/**
 *
 * @author DarkPartizaN
 */
public class UIInput {

    public static final int KEY_NONE = -666;
    public static final int KEY_ANY = -999;

    private static final HashMap<Integer, Long> key_chain = new HashMap<>(); //Pressed buttons will be stored here
    private static int current_key;

    private static int mouse_pos_x, mouse_pos_y;
    private static int old_mouse_pos_x, old_mouse_pos_y;

    private static char current_char; //Current char input

    public static void pressKey(int key) {
        current_key = key;
        key_chain.put(key, System.currentTimeMillis());
    }

    public static void resetKey(int key) {
        key_chain.remove(key);
    }

    public static boolean isKeyPressed(int key) {
        if (key == KEY_ANY)
            return !key_chain.isEmpty();
        return (key_chain.containsKey(key));
    }

    public static int currentKey() {
        if (key_chain.isEmpty())
            return KEY_NONE;
        return current_key;
    }

    public static long keyTimeMs(int key) {
        if (isKeyPressed(key))
            return (System.currentTimeMillis() - key_chain.get(key));
        return 0;
    }

    public static float keyTime(int key) {
        if (isKeyPressed(key))
            return (keyTimeMs(key) / 1000f);
        return 0;
    }

    public static void resetKeys() {
        key_chain.clear();
    }

    public static void inputChar(char c) {
        current_char = c;
    }

    public static char getCurrentChar() {
        return current_char;
    }

    public static void resetCharInput() {
        current_char = 0;
    }

    public static void updateMousePos(int x, int y) {
        old_mouse_pos_x = mouse_pos_x;
        old_mouse_pos_y = mouse_pos_y;

        mouse_pos_x = x;
        mouse_pos_y = y;
    }

    public static boolean mouseMoved() {
        return ((old_mouse_pos_x != mouse_pos_x) || (old_mouse_pos_y != mouse_pos_y));
    }

    public static int getMouseX() {
        return (int) mouse_pos_x;
    }

    public static int getMouseY() {
        return (int) mouse_pos_y;
    }

    public static boolean mouseInRect(int x, int y, int w, int h) {
        return (mouse_pos_x > x && mouse_pos_x + 8 < x + w && mouse_pos_y > y && mouse_pos_y + 8 < y + h);
    }
}