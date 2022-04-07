package com.techwolfx.knockffa.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

public class TempBlock {

    @Getter
    private final long placeTime;
    @Getter
    private final Location location;
    @Getter
    private final Material firstMaterial;
    @Getter @Setter
    public boolean expired = false;

    public TempBlock(Location location, Material firstMaterial) {
        this.firstMaterial = firstMaterial;
        this.location = location;
        this.placeTime = System.currentTimeMillis();
    }

}
