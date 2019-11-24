package redstoneparadox.packup.mixin.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import redstoneparadox.packup.recipe.DummyRecipe;
import redstoneparadox.packup.recipe.RecipeTemplateFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {


    @Shadow
    public static Recipe<?> deserialize(Identifier identifier_1, JsonObject jsonObject_1) {
        return null;
    }
    @Shadow private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;

    private static HashMap<Identifier, Recipe<?>> filledTemplates = new HashMap<>();

    @Inject(method = "method_20705", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", shift = At.Shift.BEFORE))
    private void insertFilledTemplates(Map<Identifier, JsonObject> map_1, ResourceManager resourceManager_1, Profiler profiler_1, CallbackInfo ci) {
        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> mapped = new HashMap<>();

        for (Map.Entry<Identifier, Recipe<?>> entry: filledTemplates.entrySet()) {
            RecipeType<?> type = entry.getValue().getType();
            Map<Identifier, Recipe<?>> subMap;
            if (!mapped.containsKey(type)) mapped.put(type, new HashMap<>());
            subMap = mapped.get(type);
            subMap.put(entry.getKey(), entry.getValue());
        }

        for (RecipeType<?> type: recipes.keySet()) {
            Map<Identifier, Recipe<?>> recipesInnerMap = recipes.get(type);
            if (!mapped.containsKey(type)) mapped.put(type, new HashMap<>());
            Map<Identifier, Recipe<?>> mappedInnerMap = mapped.get(type);
            for (Identifier identifier: recipesInnerMap.keySet()) {
                mappedInnerMap.put(identifier, recipesInnerMap.get(identifier));
            }
        }
        mapped.remove(DummyRecipe.TYPE);

        recipes = ImmutableMap.copyOf(mapped);
        filledTemplates.clear();
    }

    @Inject(method = "deserialize", at = @At("HEAD"), cancellable = true)
    private static void interceptRecipeTemplate(Identifier identifier_1, JsonObject jsonObject_1, CallbackInfoReturnable<Recipe<?>> cir) {
        if (identifier_1.getNamespace().equals("packup") && !FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        if (jsonObject_1.has("replacements") && jsonObject_1.has("id")) {

            String type = jsonObject_1.get("type").getAsString();
            List<Pair<Identifier, JsonObject>> filled = new ArrayList<>();

            switch (type) {
                case "minecraft:crafting_shaped":
                    filled = RecipeTemplateFiller.fillShapedTemplate(identifier_1, jsonObject_1);
                    break;
                case "minecraft:crafting_shapeless":
                    filled = RecipeTemplateFiller.fillShapelessTemplate(identifier_1, jsonObject_1);
                    break;
                case "minecraft:smelting":
                case "minecraft:blasting":
                case "minecraft:campfire_cooking":
                case "minecraft:smoking":
                    filled = RecipeTemplateFiller.fillFurnaceTemplate(identifier_1, jsonObject_1);
                    break;
                case "minecraft:stonecutting":
                    filled = RecipeTemplateFiller.fillStoneCutterTemplate(identifier_1, jsonObject_1);
            }

            for (Pair<Identifier, JsonObject> pair : filled) {
                try {
                    Recipe<?> recipe = deserialize(pair.getLeft(), pair.getRight());
                    filledTemplates.put(pair.getLeft(), recipe);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            cir.setReturnValue(new DummyRecipe());
            cir.cancel();
        }
    }
}
