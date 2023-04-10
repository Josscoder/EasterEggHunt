package josscoder.easteregghunt;

import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.command.RegisterBunnyCommand;
import josscoder.easteregghunt.command.RegisterEggCommand;
import josscoder.easteregghunt.factory.EasterEggFactory;
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
        saveDefaultConfig();

        JNPC.init(this);

        EasterEggFactory.make(getConfig());

        SimpleCommandMap commandMap = getServer().getCommandMap();
        commandMap.register("easteregghunt", new RegisterEggCommand());
        commandMap.register("easteregghunt", new RegisterBunnyCommand());

        getLogger().info(TextFormat.GREEN + "this has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "this has been disabled");
    }
}