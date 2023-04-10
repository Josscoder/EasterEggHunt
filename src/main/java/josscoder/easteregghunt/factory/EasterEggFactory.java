package josscoder.easteregghunt.factory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.passive.EntityRabbit;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.EasterEggHuntPlugin;
import josscoder.easteregghunt.animation.DefaultEasterEggAnimation;
import josscoder.easteregghunt.factory.data.EasterEgg;
import josscoder.easteregghunt.utils.PositionUtils;
import josscoder.jnpc.entity.line.PlaceholderLine;
import josscoder.jnpc.entity.npc.NPC;
import josscoder.jnpc.settings.AttributeSettings;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
public class EasterEggFactory {

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

        NPC npc = NPC.create(AttributeSettings.builder()
                .networkId(EntityRabbit.NETWORK_ID)
                .keepLooking(true)
                .scale(2f)
                .location(position.getLocation())
                .controller(((clickedNPC, player) -> {

                }))
                .build()
        );

        String message = "Help me find my &6&l12 eggs\n&rand bring them back for a nice reward!";
        Function<Player, String> header = player -> TextFormat.colorize(String.format("&b&lHello %s, I'm the Easter Bunny", player.getName()) + "\n" + message);

        npc.getTagSettings()
                .height(2.5f)
                .addLine(new PlaceholderLine(header, 1))
                .adjust();

        npc.showToWorldPlayers();

        bunnyRegistered = true;
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

        NPC npc = NPC.create(AttributeSettings.builder()
                .customEntity(true)
                .minecraftId("joss:easter_egg")
                .location(easterEgg.getPosition().getLocation())
                .controller((clickedNPC, player) -> Server.getInstance().getScheduler().scheduleRepeatingTask(
                        new DefaultEasterEggAnimation(clickedNPC.getAttributeSettings().getLocation(), player, 30),
                        1
                ))
                .build()
        );
        npc.showToWorldPlayers();
    }

    public EasterEgg registerAngGetEgg(Position position) {
        EasterEgg easterEgg = new EasterEgg(easterEggs.size() + 1, position);
        registerEgg(easterEgg, true);

        return easterEgg;
    }
}
