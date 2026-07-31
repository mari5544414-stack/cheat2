package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public class ExampleModClient implements ClientModInitializer {
    private static KeyMapping menuKey;

    public static class Feature {
        public final String name;
        public boolean enabled;
        public final String category;

        public Feature(String name, String category) {
            this.name = name;
            this.category = category;
            this.enabled = false;
        }
    }

    public static final List<Feature> FEATURES = new ArrayList<>();

    static {
        // Visuals
        FEATURES.add(new Feature("Animations", "Visuals"));
        FEATURES.add(new Feature("Aspect Ratio", "Visuals"));
        FEATURES.add(new Feature("Block Overlay", "Visuals"));
        FEATURES.add(new Feature("Arrow Nametag", "Visuals"));
        FEATURES.add(new Feature("Atmosphere", "Visuals"));
        FEATURES.add(new Feature("Body Glow", "Visuals"));
        FEATURES.add(new Feature("Crosshair", "Visuals"));
        FEATURES.add(new Feature("Full Bright", "Visuals"));
        FEATURES.add(new Feature("Hit Color", "Visuals"));
        FEATURES.add(new Feature("Motion Blur", "Visuals"));
        FEATURES.add(new Feature("Particles", "Visuals"));
        FEATURES.add(new Feature("Render Tweaks", "Visuals"));
        FEATURES.add(new Feature("Self Nametag", "Visuals"));
        FEATURES.add(new Feature("Target ESP", "Visuals"));
        FEATURES.add(new Feature("Trails", "Visuals"));
        FEATURES.add(new Feature("World Particles", "Visuals"));

        // HUD
        FEATURES.add(new Feature("Armor HUD", "HUD"));
        FEATURES.add(new Feature("Countdown HUD", "HUD"));
        FEATURES.add(new Feature("Effect Notify", "HUD"));
        FEATURES.add(new Feature("Hotbar", "HUD"));
        FEATURES.add(new Feature("Inventory HUD", "HUD"));
        FEATURES.add(new Feature("Potions", "HUD"));
        FEATURES.add(new Feature("Saturation HUD", "HUD"));
        FEATURES.add(new Feature("Target HUD", "HUD"));
        FEATURES.add(new Feature("TNT Timer", "HUD"));
        FEATURES.add(new Feature("Watermark", "HUD"));

        // Utilities
        FEATURES.add(new Feature("Chat Helper", "Utilities"));
        FEATURES.add(new Feature("Command Safe", "Utilities"));
        FEATURES.add(new Feature("Cool Downs", "Utilities"));
        FEATURES.add(new Feature("Fast Player", "Utilities"));
        FEATURES.add(new Feature("Fast Drop", "Utilities"));
        FEATURES.add(new Feature("Free Look", "Utilities"));
        FEATURES.add(new Feature("Healing Helper", "Utilities"));
        FEATURES.add(new Feature("Item Highliter", "Utilities"));
        FEATURES.add(new Feature("Lock Slot", "Utilities"));
        FEATURES.add(new Feature("Macro Helper", "Utilities"));
        FEATURES.add(new Feature("Optimization", "Utilities"));
        FEATURES.add(new Feature("PVP Safe", "Utilities"));
        FEATURES.add(new Feature("Sound Controller", "Utilities"));
        FEATURES.add(new Feature("Sprint", "Utilities"));
        FEATURES.add(new Feature("Zoom", "Utilities"));
    }

    public static Feature getFeature(String name) {
        for (Feature f : FEATURES) {
            if (f.name.equalsIgnoreCase(name)) return f;
        }
        return null;
    }

    @Override
    public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.pulsevisuals.menu",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.consumeClick()) {
                client.setScreen(new PulseScreen());
            }
            
            // Sprint implementation
            if (client.player != null && getFeature("Sprint").enabled) {
                if (client.player.input.hasForwardImpulse() && !client.player.horizontalCollision) {
                    client.player.setSprinting(true);
                }
            }
        });
    }

    public static class PulseScreen extends Screen {
        private String currentTab = "Visuals";
        private final List<FeatureButton> featureButtons = new ArrayList<>();

        public PulseScreen() {
            super(Component.literal("PulseVisuals"));
        }

        @Override
        protected void init() {
            featureButtons.clear();
            int startX = this.width / 2 - 200;
            int startY = this.height / 2 - 130;
            
            // Render Tabs
            this.addRenderableWidget(new TabButton(this.width / 2 - 150, startY - 30, 80, 20, "Visuals", btn -> {
                currentTab = "Visuals";
                rebuildWidgets();
            }));
            this.addRenderableWidget(new TabButton(this.width / 2 - 50, startY - 30, 80, 20, "HUD", btn -> {
                currentTab = "HUD";
                rebuildWidgets();
            }));
            this.addRenderableWidget(new TabButton(this.width / 2 + 50, startY - 30, 80, 20, "Utilities", btn -> {
                currentTab = "Utilities";
                rebuildWidgets();
            }));

            // Grid of Features
            int xOffset = 0;
            int yOffset = 0;
            for (Feature f : FEATURES) {
                if (f.category.equals(currentTab)) {
                    FeatureButton btn = new FeatureButton(startX + xOffset, startY + yOffset, 185, 22, f);
                    this.addRenderableWidget(btn);
                    featureButtons.add(btn);
                    xOffset += 195;
                    if (xOffset > 200) {
                        xOffset = 0;
                        yOffset += 28;
                    }
                }
            }
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            // Dark translucent beautiful background
            graphics.fill(this.width / 2 - 220, this.height / 2 - 180, this.width / 2 + 220, this.height / 2 + 180, 0xDD111115);
            
            // Panel border
            graphics.fill(this.width / 2 - 221, this.height / 2 - 181, this.width / 2 + 221, this.height / 2 - 180, 0xFF4A4B57);
            graphics.fill(this.width / 2 - 221, this.height / 2 + 180, this.width / 2 + 221, this.height / 2 + 181, 0xFF4A4B57);
            graphics.fill(this.width / 2 - 221, this.height / 2 - 181, this.width / 2 - 220, this.height / 2 + 181, 0xFF4A4B57);
            graphics.fill(this.width / 2 + 220, this.height / 2 - 181, this.width / 2 + 221, this.height / 2 + 181, 0xFF4A4B57);

            // Title render
            graphics.drawCenteredString(this.font, "PulseVisuals", this.width / 2, this.height / 2 - 165, 0xFFA088FF);

            super.render(graphics, mouseX, mouseY, delta);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return true;
        }

        public void rebuildWidgets() {
            this.clearWidgets();
            this.init();
        }

        private class TabButton extends AbstractWidget {
            private final java.util.function.Consumer<TabButton> onPress;

            public TabButton(int x, int y, int width, int height, String text, java.util.function.Consumer<TabButton> onPress) {
                super(x, y, width, height, Component.literal(text));
                this.onPress = onPress;
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                this.onPress.accept(this);
            }

            @Override
            protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
                boolean active = currentTab.equals(this.getMessage().getString());
                int color = active ? 0xFF352B5C : 0xAA222225;
                int textColor = active ? 0xFFFFFFFF : 0xFF8F8F9F;
                graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color);
                graphics.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, textColor);
            }

            @Override
            protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {}
        }

        private class FeatureButton extends AbstractWidget {
            private final Feature feature;

            public FeatureButton(int x, int y, int width, int height, Feature feature) {
                super(x, y, width, height, Component.literal(feature.name));
                this.feature = feature;
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                feature.enabled = !feature.enabled;
            }

            @Override
            protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
                int bgColor = this.isHovered() ? 0xAA2E2F38 : 0xAA1C1D24;
                graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);
                
                // Active status indicator (Switch)
                int switchColor = feature.enabled ? 0xFF704BFF : 0xFF4A4B57;
                int indicatorX = feature.enabled ? this.getX() + this.width - 20 : this.getX() + this.width - 32;
                
                // Draw Switch background
                graphics.fill(this.getX() + this.width - 34, this.getY() + 5, this.getX() + this.width - 8, this.getY() + 17, 0x55000000);
                // Draw Switch active thumb
                graphics.fill(indicatorX, this.getY() + 6, indicatorX + 10, this.getY() + 16, switchColor);

                graphics.drawString(font, this.getMessage(), this.getX() + 8, this.getY() + (this.height - 8) / 2, 0xFFE0E0FF, false);
            }

            @Override
            protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {}
        }
    }
}
