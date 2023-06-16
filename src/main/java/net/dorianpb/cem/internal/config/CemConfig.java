package net.dorianpb.cem.internal.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.ConfigInstance;
import dev.isxander.yacl3.config.GsonConfigInstance;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
public final class CemConfig implements CemOptions {

    private static final ConfigInstance<CemConfig> CONFIG_INSTANCE            = GsonConfigInstance.createBuilder(CemConfig.class)
                                                                                                  .setPath(FabricLoader.getInstance()
                                                                                                                       .getConfigDir()
                                                                                                                       .resolve("cem.json"))
                                                                                                  .build();
    @ConfigEntry
    private              boolean                   use_optifine_folder        = CemOptions.INSTANCE.useOptifineFolder();
    @ConfigEntry
    private              boolean                   use_new_model_creation_fix = CemOptions.INSTANCE.useTransparentParts();
    @ConfigEntry
    private              boolean                   use_old_animations         = CemOptions.INSTANCE.useOldAnimations();

    static Screen createScreen(Screen parent) {
        CemConfig config = getConfig();
        return YetAnotherConfigLib.createBuilder()
                                  .title(Text.literal("Custom Entity Models"))
                                  .category(ConfigCategory.createBuilder()
                                                          .name(Text.literal("Custom Entity Models"))
                                                          .option(Option.<Boolean>createBuilder()
                                                                        .name(Text.translatable("config.cem.use_optifine_folder"))
                                                                        .description(OptionDescription.of(Text.translatable(
                                                                                "config.cem.use_optifine_folder.description")))
                                                                        .binding(config.use_optifine_folder,
                                                                                 config::useOptifineFolder,
                                                                                 val -> config.use_optifine_folder = val)
                                                                        .controller(opt -> new BooleanControllerBuilderImpl(opt).yesNoFormatter()
                                                                                                                                .coloured(true))
                                                                        .flag(OptionFlag.ASSET_RELOAD)
                                                                        .build())
                                                          .option(Option.<Boolean>createBuilder()
                                                                        .name(Text.translatable("config.cem.use_new_model_creation_fix"))
                                                                        .description(OptionDescription.of(Text.translatable(
                                                                                "config.cem.use_new_model_creation_fix.description")))
                                                                        .binding(config.use_new_model_creation_fix,
                                                                                 config::useTransparentParts,
                                                                                 val -> config.use_new_model_creation_fix = val)
                                                                        .controller(opt -> new BooleanControllerBuilderImpl(opt).yesNoFormatter()
                                                                                                                                .coloured(true))
                                                                        .flag(OptionFlag.ASSET_RELOAD)
                                                                        .build())
                                                          .option(Option.<Boolean>createBuilder()
                                                                        .name(Text.translatable("config.cem.use_old_animations"))
                                                                        .description(OptionDescription.of(Text.translatable(
                                                                                "config.cem.use_old_animations.description")))
                                                                        .binding(config.use_old_animations,
                                                                                 config::useOldAnimations,
                                                                                 val -> config.use_old_animations = val)
                                                                        .controller(opt -> new BooleanControllerBuilderImpl(opt).yesNoFormatter()
                                                                                                                                .coloured(true))
                                                                        .flag(OptionFlag.ASSET_RELOAD)
                                                                        .build())
                                                          .build())
                                  .save(CONFIG_INSTANCE::save)
                                  .build()
                                  .generateScreen(parent);
    }

    static CemConfig getConfig() {
        return CONFIG_INSTANCE.getConfig();
    }

    @Override
    public boolean useOptifineFolder() {
        return this.use_optifine_folder;
    }

    @Override
    public boolean useTransparentParts() {
        return this.use_new_model_creation_fix;
    }

    @Override
    public boolean useOldAnimations() {
        return this.use_old_animations;
    }
}