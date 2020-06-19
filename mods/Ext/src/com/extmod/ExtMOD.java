package com.extmod;

import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.client.WindowResizedEvent;
import outskirts.mod.Mod;
import outskirts.util.SystemUtils;
import outskirts.util.logging.Log;

public class ExtMOD extends Mod {

    {
        Events.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(WindowResizedEvent event) {

        Log.info("loaded. des: %s, loc: %s", getManifest().getDescription(),
                SystemUtils.getProgramLocation(ExtMOD.class));

    }
}
