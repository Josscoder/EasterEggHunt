package josscoder.easteregghunt.factory;

import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.EasterEggHuntPlugin;
import josscoder.easteregghunt.factory.data.EasterEgg;
import josscoder.easteregghunt.utils.PositionUtils;
import josscoder.jnpc.entity.npc.NPC;
import josscoder.jnpc.settings.AttributeSettings;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EggFactory {

    private final Config config;
    private final ConfigSection eggsSection;

    @Getter
    private static EggFactory instance;

    @Getter
    private final List<EasterEgg> easterEggs = new ArrayList<>();

    public EggFactory(Config config) {
        this.config = config;
        this.eggsSection = config.getSection("eggs");

        instance = this;

        loadEggs();
    }

    public static void make(Config config) {
        new EggFactory(config);
    }

    private void loadEggs() {
        eggsSection.getKeys(false).forEach(key -> {
            Position position = PositionUtils.stringToPosition(eggsSection.getString(key));

            EasterEgg easterEgg = new EasterEgg(Integer.parseInt(key), position);
            registerEgg(easterEgg, false);
        });

        EasterEggHuntPlugin.getInstance().getLogger().info(TextFormat.AQUA + String.format("%s egg(s) were loaded!", easterEggs.size()));
    }

    public void registerEgg(EasterEgg easterEgg, boolean serialize) {
        easterEggs.add(easterEgg);

        if (serialize) {
            eggsSection.set(String.valueOf(easterEgg.getId()), PositionUtils.positionToString(easterEgg.getPosition()));
            config.set("eggs", eggsSection);
            config.save();
        }

        NPC.create(AttributeSettings.builder()
                        .customEntity(true)
                        .minecraftId("joss:easter_egg")
                        .location(easterEgg.getPosition().getLocation())
                        .controller(((npc, player) -> {

                        }))
                        .build()
        );
    }

    public EasterEgg registerAngGetEgg(Position position) {
        EasterEgg easterEgg = new EasterEgg(easterEggs.size() + 1, position);
        registerEgg(easterEgg, true);

        return easterEgg;
    }
}
