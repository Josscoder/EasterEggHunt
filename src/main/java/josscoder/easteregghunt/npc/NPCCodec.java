package josscoder.easteregghunt.npc;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.passive.EntityRabbit;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.EasterEggHuntPlugin;
import josscoder.easteregghunt.animation.DefaultEasterEggAnimation;
import josscoder.easteregghunt.egg.factory.EasterEggFactory;
import josscoder.easteregghunt.egg.factory.data.EasterEgg;
import josscoder.easteregghunt.npc.identifier.EntityModelType;
import josscoder.easteregghunt.session.SessionFactory;
import josscoder.easteregghunt.session.data.UserSession;
import josscoder.jnpc.entity.line.PlaceholderLine;
import josscoder.jnpc.entity.npc.NPC;
import josscoder.jnpc.settings.AttributeSettings;

import java.util.function.Function;

public class NPCCodec {

    public static final Function<EasterEgg, NPC> FLOWERS_EGG_NPC = easterEgg -> NPC.create(AttributeSettings.builder()
            .customEntity(true)
            .minecraftId(EntityModelType.FLOWERS_EGG.getId())
            .location(easterEgg.getPosition().getLocation())
            .controller((clickedNPC, player) -> {
                UserSession session = SessionFactory.getInstance().getSession(player.getLoginChainData().getXUID());
                if (session == null) {
                    return;
                }

                if (session.hasEasterEggFound(easterEgg.getId())) {
                    player.sendMessage(TextFormat.RED + "You have already found this egg!");
                    return;
                }

                session.markEasterEggAsFound(easterEgg.getId()).whenComplete(((unused, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return;
                    }

                    DefaultEasterEggAnimation animation = new DefaultEasterEggAnimation(clickedNPC.getAttributeSettings().getLocation(), player, 30);
                    Server.getInstance().getScheduler().scheduleRepeatingTask(animation, 1);

                    int maxEggs = EasterEggFactory.MAX_EGGS;
                    int eggsFound = session.getCountFoundEasterEggs();

                    player.sendMessage(TextFormat.colorize(String.format("&aYou have found &e%s/%s &aeggs!", eggsFound, maxEggs)));

                    if (eggsFound >= maxEggs) {
                        player.sendMessage(TextFormat.AQUA + "You found all the eggs, give them to the Easter bunny!");
                    }
                }));
            })
            .build()
    );

    public static final Function<Location, NPC> ANIMATION_EGG_NPC = location -> NPC.create(AttributeSettings.builder()
            .customEntity(true)
            .minecraftId(EntityModelType.FLOWERS_EGG.getId())
            .location(location)
            .controller((npc, player) -> {})
            .build()
    );

    public static final Function<Location, NPC> RABBIT_NPC = location -> {
        int maxEggs = EasterEggFactory.MAX_EGGS;

        NPC npc = NPC.create(AttributeSettings.builder()
                .networkId(EntityRabbit.NETWORK_ID)
                .keepLooking(true)
                .scale(2f)
                .location(location)
                .controller(((clickedNPC, player) -> {
                    UserSession session = SessionFactory.getInstance().getSession(player.getLoginChainData().getXUID());
                    if (session == null) {
                        return;
                    }

                    if (session.getEasterEggsFound().size() < maxEggs) {
                        player.sendMessage(TextFormat.RED + String.format("I need my %s eggs!", maxEggs));
                        return;
                    }

                    if (session.isRewardClaimed()) {
                        player.sendMessage(TextFormat.RED + "You have already claimed the reward");
                        return;
                    }

                    session.markRewardAsClaimed();

                    EasterEggHuntPlugin.getInstance().getCommands().forEach(command -> {
                        command = command.replace("%p", player.getName().replace(" ", "\\ "));
                        Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command);
                    });
                }))
                .build()
        );

        String message = String.format("Help me find my &6&l%s eggs\n&rand bring them back for a nice reward!", maxEggs);
        Function<Player, String> header = player -> TextFormat.colorize(String.format("&b&lHello %s, I'm the Easter Bunny", player.getName()) + "\n" + message);

        npc.getTagSettings()
                .height(2.5f)
                .addLine(new PlaceholderLine(header, 1))
                .adjust();

        return npc;
    };
}
