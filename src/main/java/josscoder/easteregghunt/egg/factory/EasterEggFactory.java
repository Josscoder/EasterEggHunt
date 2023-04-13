package josscoder.easteregghunt.egg.factory;

import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.EasterEggHuntPlugin;
import josscoder.easteregghunt.egg.factory.data.EasterEgg;
import josscoder.easteregghunt.npc.NPCCodec;
import josscoder.easteregghunt.utils.PositionUtils;
import josscoder.jnpc.entity.npc.NPC;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EasterEggFactory {

    public static final int MAX_EGGS = 12;

    private final Config config;
    private final ConfigSection eggsSection;

    @Getter
    private static EasterEggFactory instance;

    private boolean bunnyRegistered;

    private final List<EasterEgg> easterEggs = new ArrayList<>();

    public EasterEggFactory(Config config) {
        this.config = config;

        instance = this;

        loadBunny();

        this.eggsSection = config.getSection("eggs");
        loadEggs();
    }

    public static void make(Config config) {
        new EasterEggFactory(config);
    }

    private void loadBunny() {
        if (!config.exists("bunny")) {
            return;
        }

        Position position = PositionUtils.stringToPosition(config.getString("bunny"));
        setBunny(position, false);
    }

    public void setBunny(Position position, boolean serialize) {
        if (bunnyRegistered) {
            return;
        }

        if (serialize) {
            config.set("bunny", PositionUtils.positionToString(position));
            config.save();
        }

        NPC npc = NPCCodec.RABBIT_NPC.apply(position.getLocation());
        npc.showToWorldPlayers();

        bunnyRegistered = true;
    }

    private void loadEggs() {
        eggsSection.getKeys(false).forEach(key -> {
            Position position = PositionUtils.stringToPosition(eggsSection.getString(key));

            EasterEgg easterEgg = new EasterEgg(Integer.parseInt(key), position);
            registerEgg(easterEgg, false);
        });

        EasterEggHuntPlugin.getInstance().getLogger().info(TextFormat.AQUA + "all easter eggs were loaded!");
    }

    public void registerEgg(EasterEgg easterEgg, boolean serialize) {
        easterEggs.add(easterEgg);

        if (serialize) {
            eggsSection.set(String.valueOf(easterEgg.getId()), PositionUtils.positionToString(easterEgg.getPosition()));
            config.set("eggs", eggsSection);
            config.save();
        }

        NPC npc = NPCCodec.FLOWERS_EGG_NPC.apply(easterEgg);
        npc.showToWorldPlayers();
    }

    public EasterEgg registerAngGetEgg(Position position) {
        EasterEgg easterEgg = new EasterEgg(easterEggs.size() + 1, position);
        registerEgg(easterEgg, true);

        return easterEgg;
    }
}
