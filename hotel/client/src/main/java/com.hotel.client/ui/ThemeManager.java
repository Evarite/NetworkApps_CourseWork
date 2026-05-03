package com.hotel.client;

import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Кіруе пераключэннем светлай/цёмнай тэмы.
 * Рэгіструе ўсе сцэны і сінхронна перагружае CSS.
 */
public class ThemeManager {

    private static final ThemeManager INSTANCE = new ThemeManager();

    private boolean dark = false;

    private String lightCss;
    private String darkCss;

    private final List<Scene> scenes = new ArrayList<>();

    private ThemeManager() {
        lightCss = Objects.requireNonNull(
                getClass().getResource("/com/hotel/client/css/light.css"),
                "light.css не знойдзены ў рэсурсах"
        ).toExternalForm();

        darkCss = Objects.requireNonNull(
                getClass().getResource("/com/hotel/client/css/dark.css"),
                "dark.css не знойдзены ў рэсурсах"
        ).toExternalForm();
    }

    public static ThemeManager getInstance() { return INSTANCE; }

    public boolean isDark() { return dark; }

    /** Рэгіструе сцэну і адразу прымяняе бягучую тэму. */
    public void register(Scene scene) {
        scenes.add(scene);
        applyTo(scene);
    }

    /** Адрэгіструе сцэну (напрыклад, пры закрыцці акна). */
    public void unregister(Scene scene) {
        scenes.remove(scene);
    }

    /** Пераключае тэму для ўсіх зарэгістраваных сцэн. */
    public void toggle() {
        dark = !dark;
        scenes.forEach(this::applyTo);
    }

    public void applyTo(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(lightCss);
        if (dark) scene.getStylesheets().add(darkCss);
    }

    public String themeIcon() { return dark ? "☀" : "🌙"; }
}
