package com.perigrine3.createcybernetics.api;

import java.util.Map;

public interface ICyberwareData {

    InstalledCyberware get(CyberwareSlot slot, int index);

    void set(CyberwareSlot slot, int index, InstalledCyberware cyberware);

    InstalledCyberware remove(CyberwareSlot slot, int index);

    Map<CyberwareSlot, InstalledCyberware[]> getAll();

    int getHumanity();

    void setHumanity(int value);

    void clear();

    void setDirty();
}
