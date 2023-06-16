package net.dorianpb.cem.internal.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> (CemConfigFairy.getConfig() instanceof CemConfig)
                           ? CemConfig.createScreen(parent)
                           : new MissingConfigDependencyScreen(parent);
    }

    private static class MissingConfigDependencyScreen extends Screen {
        private final Screen parent;

        MissingConfigDependencyScreen(Screen parent) {
            super(Text.literal("My tutorial screen"));
            this.parent = parent;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context);
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public void close() {
            if(this.client != null) {
                this.client.setScreen(this.parent);
            }
        }

        @Override
        protected void init() {
            super.init();
            FabricLoader instance = FabricLoader.getInstance();
            StringBuilder missingmodids = new StringBuilder();

            instance.getModContainer("cem").get().getMetadata().getDependencies().forEach(modDependency -> {
                if(modDependency.getKind() == ModDependency.Kind.RECOMMENDS && !instance.isModLoaded(modDependency.getModId())) {
                    missingmodids.append("\n").append(modDependency.getModId());
                }
            });

            MultilineTextWidget textWidget = new MultilineTextWidget(Text.translatable("config.cem.missing_dependencies")
                                                                         .append(missingmodids.toString()), this.textRenderer);
            textWidget.setCentered(true);
            textWidget.setPosition((this.width - textWidget.getWidth()) / 2, this.height / 3);

            ButtonWidget back = ButtonWidget.builder(Text.translatable("gui.back"), button -> this.close()).build();

            back.setPosition((this.width - back.getWidth()) / 2, this.height - (2 * back.getHeight()));

            this.addDrawableChild(textWidget);
            this.addDrawableChild(back);

        }
    }
}