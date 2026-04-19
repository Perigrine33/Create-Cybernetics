package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class ModMobAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CreateCybernetics.MODID);

    public static final AttachmentType<EntityCyberwareData> CYBERENTITY_CYBERWARE =
            AttachmentType.builder(EntityCyberwareData::new)
                    .serialize(new IAttachmentSerializer<CompoundTag, EntityCyberwareData>() {
                        @Override
                        public EntityCyberwareData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                            EntityCyberwareData data = new EntityCyberwareData();
                            data.deserializeNBT(tag, provider);
                            return data;
                        }

                        @Override
                        public @Nullable CompoundTag write(EntityCyberwareData data, HolderLookup.Provider provider) {
                            return data.serializeNBT(provider);
                        }
                    })
                    .sync(new CyberentitySyncHandler())
                    .build();

    private static final class CyberentitySyncHandler implements AttachmentSyncHandler<EntityCyberwareData> {

        @Override
        public void write(RegistryFriendlyByteBuf buf, EntityCyberwareData attachment, boolean initialSync) {
            buf.writeNbt(attachment.serializeNBT(buf.registryAccess()));
        }

        @Override
        public @Nullable EntityCyberwareData read(
                IAttachmentHolder holder,
                RegistryFriendlyByteBuf buf,
                @Nullable EntityCyberwareData previousValue
        ) {
            EntityCyberwareData out = previousValue != null ? previousValue : new EntityCyberwareData();
            CompoundTag tag = buf.readNbt();
            if (tag != null) {
                out.deserializeNBT(tag, buf.registryAccess());
            }
            return out;
        }

        @Override
        public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer to) {
            return true;
        }
    }

    public static void register(IEventBus bus) {
        ATTACHMENTS.register("cyberentity_cyberware", () -> CYBERENTITY_CYBERWARE);
        ATTACHMENTS.register(bus);
    }

    private ModMobAttachments() {}
}