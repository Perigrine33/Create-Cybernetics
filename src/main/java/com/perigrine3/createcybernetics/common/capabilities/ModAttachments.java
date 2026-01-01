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

public final class ModAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CreateCybernetics.MODID);

    public static final AttachmentType<PlayerCyberwareData> CYBERWARE =
            AttachmentType.builder(PlayerCyberwareData::new)
                    .serialize(new IAttachmentSerializer<CompoundTag, PlayerCyberwareData>() {
                        @Override
                        public PlayerCyberwareData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                            PlayerCyberwareData data = new PlayerCyberwareData();
                            data.deserializeNBT(tag, provider);
                            return data;
                        }

                        @Override
                        public @Nullable CompoundTag write(PlayerCyberwareData data, HolderLookup.Provider provider) {
                            return data.serializeNBT(provider);
                        }
                    })
                    .sync(new CyberwareSyncHandler())
                    .build();

    private static final class CyberwareSyncHandler implements AttachmentSyncHandler<PlayerCyberwareData> {

        @Override
        public void write(RegistryFriendlyByteBuf buf, PlayerCyberwareData attachment, boolean initialSync) {
            buf.writeNbt(attachment.serializeNBT(buf.registryAccess()));
        }

        @Override
        public @Nullable PlayerCyberwareData read(
                IAttachmentHolder holder,
                RegistryFriendlyByteBuf buf,
                @Nullable PlayerCyberwareData previousValue
        ) {
            PlayerCyberwareData out = (previousValue != null) ? previousValue : new PlayerCyberwareData();

            CompoundTag tag = buf.readNbt();
            if (tag != null) {
                out.deserializeNBT(tag, buf.registryAccess());
            }
            return out;
        }

        @Override
        public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer to) {
            return holder == to;
        }
    }

    public static void register(IEventBus bus) {
        ATTACHMENTS.register("cyberware", () -> CYBERWARE);
        ATTACHMENTS.register(bus);
    }

    private ModAttachments() {}
}
