package josscoder.easteregghunt.animation;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.scheduler.Task;

public abstract class Animation extends Task {

    protected final Location initLocation;
    protected final Player player;
    protected int ticks;

    protected Animation(Location initLocation, Player player, int ticks) {
        this.initLocation = initLocation.clone();
        this.player = player;
        this.ticks = ticks;

        onEnable();
    }

    public void onEnable() {

    }

    @Override
    public void onRun(int i) {
        if (isRunnable()) {
            onDisplay();
            ticks--;
        } else {
            cancel();
        }
    }

    public abstract void onDisplay();

    public boolean isRunnable() {
        return player != null && player.isOnline() && ticks >= 0;
    }
}
