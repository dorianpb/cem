package net.dorianpb.cem.internal.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionFlag;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import dev.isxander.yacl.gui.controllers.BooleanController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class CemConfig {
	public static final ConfigInstance<CemConfig> INSTANCE = new GsonConfigInstance<>(CemConfig.class, FabricLoader.getInstance().getConfigDir().resolve("cem.json"));
	private static final OptionFlag RELOAD_RESOURCES = MinecraftClient::reloadResources;

	@ConfigEntry private boolean useOptifineFolder = false;
	@ConfigEntry private boolean useNewModelCreationFix = true;
	@ConfigEntry private boolean useOldAnimations = false;
	
	public static CemConfig getConfig() {
		return INSTANCE.getConfig();
	}
	
	public static Screen createScreen(Screen parent) {
		CemConfig defaults = INSTANCE.getDefaults();
		CemConfig config = getConfig();
		return YetAnotherConfigLib.createBuilder()
				.title(Text.literal("Custom Entity Models"))
				.category(ConfigCategory.createBuilder()
						.name(Text.literal("Custom Entity Models"))
						.option(Option.createBuilder(boolean.class)
								.name(Text.translatable("config.cem.use_optifine_folder"))
								.tooltip(Text.translatable("config.cem.use_optifine_folder.description"))
								.binding(
										defaults.useOptifineFolder,
										() -> config.useOptifineFolder,
										val -> config.useOptifineFolder = val
								)
								.controller(opt -> new BooleanController(opt, BooleanController.YES_NO_FORMATTER, false))
								.flag(RELOAD_RESOURCES)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.translatable("config.cem.use_new_model_creation_fix"))
								.tooltip(Text.translatable("config.cem.use_new_model_creation_fix.description"))
								.binding(
										defaults.useNewModelCreationFix,
										() -> config.useNewModelCreationFix,
										val -> config.useNewModelCreationFix = val
								)
								.controller(opt -> new BooleanController(opt, BooleanController.YES_NO_FORMATTER, false))
								.flag(RELOAD_RESOURCES)
								.build())
						.option(Option.createBuilder(boolean.class)
								.name(Text.translatable("config.cem.use_old_animations"))
								.tooltip(Text.translatable("config.cem.use_old_animations.description"))
								.binding(
										defaults.useOldAnimations,
										() -> config.useOldAnimations,
										val -> config.useOldAnimations = val
								)
								.controller(opt -> new BooleanController(opt, BooleanController.YES_NO_FORMATTER, false))
								.flag(RELOAD_RESOURCES)
								.build())
						.build())
				.save(INSTANCE::save)
				.build()
				.generateScreen(parent);
	}

	public boolean useOptifineFolder() {
		return this.useOptifineFolder;
	}

	public boolean useTransparentParts() {
		return this.useNewModelCreationFix;
	}

	public boolean useOldAnimations() {
		return this.useOldAnimations;
	}
}