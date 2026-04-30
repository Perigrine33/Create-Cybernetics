package com.perigrine3.createcybernetics.compat.playeranimator;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class PlayerAnimatorMixinPlugin implements IMixinConfigPlugin {

    private static final String PLAYER_ANIMATOR_FIRST_PERSON_MODE =
            "dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode";

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return classExists(PLAYER_ANIMATOR_FIRST_PERSON_MODE);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static boolean classExists(String name) {
        try {
            Class.forName(name, false, PlayerAnimatorMixinPlugin.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}