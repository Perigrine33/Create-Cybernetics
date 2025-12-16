package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class ModAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CreateCybernetics.MODID);

    public static final AttachmentType<PlayerCyberwareData> CYBERWARE =
            AttachmentType.builder(PlayerCyberwareData::new).serialize(new IAttachmentSerializer<CompoundTag, PlayerCyberwareData>() {

                @Override
                public PlayerCyberwareData read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
                    PlayerCyberwareData data = new PlayerCyberwareData();
                    data.deserializeNBT(compoundTag);
                    return data;
                }

                @Override
                public @Nullable CompoundTag write(PlayerCyberwareData playerCyberwareData, HolderLookup.Provider provider) {
                    return playerCyberwareData.serializeNBT();
                }
            })

                    .copyOnDeath() // keep cyberware on death? I plan to make this toggleable in the config... once I know how configs work... :[
                    .build();

    public static void register(IEventBus bus) {
        ATTACHMENTS.register("cyberware", () -> CYBERWARE);
        ATTACHMENTS.register(bus);
    }

    private ModAttachments() {

    }
}
