package josscoder.easteregghunt.animation;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.HeartParticle;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import josscoder.jnpc.entity.npc.NPC;
import josscoder.jnpc.settings.AttributeSettings;

public class DefaultEasterEggAnimation extends Animation {

    private NPC npc;

    public DefaultEasterEggAnimation(Location initLocation, Player player, int ticks) {
        super(initLocation, player, ticks);
    }

    @Override
    public void onEnable() {
        npc = NPC.create(AttributeSettings.builder()
                .customEntity(true)
                .minecraftId("joss:easter_egg")
                .location(initLocation)
                .controller((npc, player) -> {})
                .build()
        );
        npc.showToWorldPlayers();
    }

    @Override
    public void onDisplay() {
        Location location = npc.getAttributeSettings().getLocation();

        location.setY(location.getY() + .30);
        location.yaw = location.getYaw() + 9;

        npc.move(location);

        if ((ticks % 2) == 0) {
            player.getLevel().addParticle(new HeartParticle(location.subtract(0, 1)), player);
        }

        player.getLevel().addSound(player, Sound.BLOCK_TURTLE_EGG_CRACK);
    }

    @Override
    public void onCancel() {
        Location location = npc.getAttributeSettings().getLocation();

        player.getLevel().addSound(player, Sound.BLOCK_TURTLE_EGG_DROP);
        player.getLevel().addParticle(new HugeExplodeSeedParticle(location.add(0, 1.1)), player);

        npc.remove();
    }
}
