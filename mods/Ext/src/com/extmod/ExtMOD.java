package com.extmod;

import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.client.WindowResizedEvent;
import outskirts.mod.Mod;
import outskirts.util.SystemUtil;
import outskirts.util.logging.Log;

public class ExtMOD extends Mod {

    {
        Events.EVENT_BUS.register(this);
        System.out.println("init<>");
    }

    @EventHandler
    public void init(WindowResizedEvent event) {

        Log.info("loaded. des: {}, loc: {}", getManifest().getDescription(),
                SystemUtil.getProgramLocation(ExtMOD.class));

    }
}
