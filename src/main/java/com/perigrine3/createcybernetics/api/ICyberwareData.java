package com.perigrine3.createcybernetics.api;

import java.util.List;

public interface ICyberwareData {

    List<InstalledCyberware> getInstalled();

    boolean install(InstalledCyberware cyberware);

    InstalledCyberware removeFromSlot(String slot);

    void clear();

    void setDirty();
}
