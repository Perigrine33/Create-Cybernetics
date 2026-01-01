package com.perigrine3.createcybernetics.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlayerAttachmentState {
    private final List<PlayerAttachment> attachments = new ArrayList<>();

    public void clear() {
        attachments.clear();
    }

    public void add(PlayerAttachment a) {
        attachments.add(a);
    }

    public boolean isEmpty() {
        return attachments.isEmpty();
    }

    public List<PlayerAttachment> all() {
        return Collections.unmodifiableList(attachments);
    }
}
