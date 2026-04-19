package com.perigrine3.createcybernetics.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CyberentityAttachmentState {
    private final List<CyberentityAttachment> attachments = new ArrayList<>();

    public void clear() {
        attachments.clear();
    }

    public void add(CyberentityAttachment attachment) {
        attachments.add(attachment);
    }

    public boolean isEmpty() {
        return attachments.isEmpty();
    }

    public List<CyberentityAttachment> all() {
        return Collections.unmodifiableList(attachments);
    }
}