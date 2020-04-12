package com.fenyx.ui;

import java.util.ArrayList;

/**
 *
 * @author DarkPartizaN
 */
public abstract class UI {

    protected int x, y, width, height;
    protected boolean visible, active, enabled, focused, was_focused;

    //Event system
    private long last_idle, idle_delay;
    private long last_event, event_delay;
    private long remove_delay, remove_start;
    private boolean should_remove = false;

    //Parent system
    private UI parent;
    private final ArrayList<UI> elements;

    public UI() {
        visible = true;
        active = true;
        enabled = true;

        elements = new ArrayList<>();

        last_event = System.currentTimeMillis();
        last_idle = System.currentTimeMillis();
    }

    public abstract void onShow();
    public abstract void onEnable();
    public abstract void onIdle();
    public abstract void onMove();
    public abstract void onResize();
    public abstract void onFocus();
    public abstract void onFocusLost();
    public abstract void onMouseMove();
    public abstract void onClick();
    public abstract void onKeyPressed();
    public abstract void onKeyReleased();
    public abstract void onDraw();
    public abstract void onDisable();
    public abstract void onHide();
    public abstract void onDestroy();

    public final void add(UI e) {
        if (elements.contains(e)) return;

        e.parent = this;
        e.setPosition(this.x + e.x, this.y + e.y);

        elements.add(e);
    }

    public final void remove(UI e) {
        if (elements.isEmpty()) return;
        if (!elements.contains(e))return;

        this.elements.remove(e);

        if (e.parent == this) e.parent = null;

        e.setPosition(e.x - this.x, e.y - this.y);
    }

    public final void removeLately(UI e, int delay) {
        e.remove_start = System.currentTimeMillis();
        e.remove_delay = delay;
        e.should_remove = true;
    }

    public final void setPosition(int x, int y) {
        int delta_x = x - getX();
        int delta_y = y - getY();

        this.x += delta_x;
        this.y += delta_y;

        onMove();

        elements.forEach((e) -> {
            e.setPosition(e.getX() + delta_x, e.getY() + delta_y);
        });
    }

    public final void centerElement() {
        if (parent == null) {
            setPosition(UIManager.UI_SCREEN_WIDTH / 2 - width / 2, UIManager.UI_SCREEN_HEIGHT / 2 - height / 2);
        } else {
            setPosition(parent.width / 2 - width / 2, parent.width / 2 - height / 2);
        }
    }

    public final void setX(int x) {
        setPosition(x, this.y);
    }

    public final void setY(int y) {
        setPosition(this.x, y);
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        onResize();
    }

    public final void setWidth(int width) {
        setSize(width, this.height);
    }

    public final void setHeight(int height) {
        setSize(this.width, height);
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final int getBoundWidth() {
        return x + width;
    }

    public final int getBoundHeight() {
        return y + height;
    }

    public final boolean hasParent() {
        return parent != null;
    }

    public final UI getParent() {
        return parent;
    }

    public ArrayList<UI> getElements() {
        return elements;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;

        if (visible) {
            onShow();
        }
        else {
            onHide();
        }
    }

    public final boolean isVisible() {
        return visible;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setActive(boolean active) {
        this.active = active;
    }

    public final boolean isActive() {
        return active;
    }

    public final boolean isFocused() {
        return focused;
    }

    public boolean canEvent() {
        if ((hasParent()) && ((!parent.isActive()) || (!parent.isVisible()))) return false;
        if ((!isActive()) || (!isVisible())) return false;

        if (System.currentTimeMillis() - last_event > event_delay) {
            last_event = System.currentTimeMillis();

            return true;
        }

        return false;
    }

    public final void setEventDelay(int delay) {
        event_delay = delay;
    }

    public final void setIdleDelay(int delay) {
        idle_delay = delay;
    }

    private boolean canIdle() {
        if (System.currentTimeMillis() - last_idle > idle_delay) {
            last_idle = System.currentTimeMillis();

            return true;
        }

        return false;
    }

    public final void update() {
        if (canIdle()) onIdle();

        focused = UIInput.mouseInRect(x, y, width, height);

        if (canEvent()) {
            if (focused) {
                if (UIInput.isKeyPressed(0)) {
                    onClick();
                }
                if (!was_focused) {
                    was_focused = true;
                    onFocus();
                }
            } else if (was_focused) {
                was_focused = false;
                onFocusLost();
            }

            if (UIInput.isKeyPressed(UIInput.KEY_ANY)) onKeyPressed();
            if (UIInput.mouseMoved()) onMouseMove();
        }

        this.elements.forEach((e) -> {
            if (!e.should_remove)
                e.update();
            else if (System.currentTimeMillis() == e.remove_start + e.remove_delay) //Remove lately hook
                remove(e);
        });
    }
}