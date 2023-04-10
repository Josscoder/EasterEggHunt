package josscoder.easteregghunt;

import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import josscoder.easteregghunt.command.RegisterBunnyCommand;
import josscoder.easteregghunt.command.RegisterEggCommand;
import josscoder.easteregghunt.egg.factory.EasterEggFactory;
import josscoder.easteregghunt.listener.AccountHandlerListener;
import josscoder.easteregghunt.mongodb.MongoDBProvider;
import josscoder.easteregghunt.session.SessionFactory;
import josscoder.jnpc.JNPC;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EasterEggHuntPlugin extends PluginBase {

    @Getter
    private static EasterEggHuntPlugin instance;

    @Getter
    private List<String> commands = new ArrayList<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        JNPC.init(this);

        Config config = getConfig();

        commands = config.getStringList("commands");

        MongoDBProvider.make(config.getString("mongodb.host", "localhost"),
                config.getInt("mongodb.port", 27017),
                config.getString("mongodb.username"),
                config.getString("mongodb.database"),
                config.getString("mongodb.password", "password")
        );
        SessionFactory.make();
        EasterEggFactory.make(config);

        SimpleCommandMap commandMap = getServer().getCommandMap();
        commandMap.register("easteregghunt", new RegisterEggCommand());
        commandMap.register("easteregghunt", new RegisterBunnyCommand());

        getServer().getPluginManager().registerEvents(new AccountHandlerListener(), this);

        getLogger().info(TextFormat.GREEN + "this has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "this has been disabled");
    }
}