package josscoder.easteregghunt.animation;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.HeartParticle;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.npc.NPCCodec;
import josscoder.jnpc.entity.npc.NPC;

public class DefaultEasterEggAnimation extends Animation {

    private NPC npc;

    public DefaultEasterEggAnimation(Location initLocation, Player player, int ticks) {
        super(initLocation, player, ticks);
    }

    @Override
    public void onEnable() {
        npc = NPCCodec.ANIMATION_EGG_NPC.apply(initLocation);
        npc.show(player);
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

        player.sendTitle("", TextFormat.colorize("&6&lYou found an egg"));
    }
}
