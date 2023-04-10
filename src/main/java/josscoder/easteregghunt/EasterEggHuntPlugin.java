package josscoder.easteregghunt;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.command.RegisterEggCommand;
import josscoder.easteregghunt.factory.EggFactory;
import josscoder.jnpc.JNPC;
import lombok.Getter;

public class EasterEggHuntPlugin extends PluginBase {

    @Getter
    private static EasterEggHuntPlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        JNPC.init(this);

        EggFactory.make(getConfig());

        getServer().getCommandMap().register("easteregghunt", new RegisterEggCommand());

        getLogger().info(TextFormat.GREEN + "this has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "this has been disabled");
    }
}