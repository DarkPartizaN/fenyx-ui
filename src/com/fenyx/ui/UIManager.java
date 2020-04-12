package com.fenyx.ui;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author DarkPartizaN
 */
public class UIManager {

    public static int UI_SCREEN_WIDTH, UI_SCREEN_HEIGHT;

    private static final HashMap<Integer, UILayer> layers = new HashMap<>();
    private static boolean state = false;
    private static int last_layer = 0; //Protection from 'genius' people, who decide to add a crap bunch of random layer numbers

    //!CALL IT AT FIRST TO CORRECT INITIALIZE UI!
    public static void init(int width, int height) {
        UI_SCREEN_WIDTH = width;
        UI_SCREEN_HEIGHT = height;

        //Create default layer
        layers.put(0, new UILayer());
    }

    //!And this before your main loop started
    public static void setActive(boolean active) {
        state = active;
    }

    public static void add(UI ui) {
        add(ui, 0);
    }

    public static void add(UI ui, int layer) {
        if (layers.get(layer) == null) layers.put(layer, new UILayer());
        if (last_layer < layer) last_layer = layer; //HACKHACK: now, it 'OutOfBounds'-proof :)

        layers.get(layer).uis.add(ui);
    }

    public static void remove(UI ui) {
         for (UILayer layer : layers.values()) {
             if (layer.uis.contains(ui)) {
                 layer.remove(ui);
                 break; //NOTE: we search for first match, cause there's no doublers of UI object in normal case though
             }
         }
    }

    public static void remove(UI ui, int layer) {
        //NOTE: use UI.setVisible(boolean) method instead, if you wanna to re-use this UI object lately
        layers.get(layer).remove(ui);
    }

    public static void removeAllUI(int layer) {
        //NOTE: use UI.setVisible(boolean) method instead, if you wanna to re-use this UI object lately
        layers.get(layer).removeAll();
    }

    public static void removeAllLayers() {
        layers.values().forEach((layer) -> {
            layer.removeAll();
        });

        layers.clear();
    }

    public static void clearLayer(int layer) {
        layers.get(layer).uis.clear(); //Not destroying UIs, safe for re-use them, just clear layer from UI objects
    }

    public static void update() {
        if (!state) return;

        layers.values().forEach((layer) -> {
            layer.update();
        });
    }

    public static void draw() {
        if (!state) return;

        //HACKHACK: we don't need sort, cause hash key is number of layer :crazy.gif:
        for (int i = 0; i <= last_layer; i++) {
            if (layers.get(i) == null) continue;

            layers.get(i).draw();
        }
    }

    //Simple class for layer representation
    private static class UILayer {

        private final ArrayList<UI> uis = new ArrayList<>();

        public void remove(UI ui) {
            ui.onDestroy();
            uis.remove(ui);
        }

        public void removeAll() {
            for (UI ui : uis) remove(ui);
            uis.clear();
        }

        public void update() {
            uis.forEach((ui) -> {
                ui.update();
            });
        }

        public void draw() {
            uis.forEach((ui) -> {
                ui.onDraw();
            });
        }
    }
}