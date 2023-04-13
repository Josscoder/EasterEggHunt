package josscoder.easteregghunt;

import cn.nukkit.Player;
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
import josscoder.easteregghunt.session.data.UserSession;
import josscoder.jnpc.JNPC;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EasterEggHuntPlugin extends PluginBase {

    @Getter
    private static EasterEggHuntPlugin instance;

    @Getter
    private List<String> commands = new ArrayList<>();

    @Getter
    private boolean popupMessage;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        initConfig();

        JNPC.init(this);

        initMongoDB();
        initFactories();

        if (popupMessage) {
            enablePopupMessageTask();
        }

        registerCommands();

        getServer().getPluginManager().registerEvents(new AccountHandlerListener(), this);

        getLogger().info(TextFormat.GREEN + "this has been enabled");
    }

    private void initConfig() {
        saveDefaultConfig();

        Config config = getConfig();

        commands = config.getStringList("commands");
        popupMessage = config.getBoolean("popup-message", true);
    }

    private void enablePopupMessageTask() {
        SessionFactory sessionFactory = SessionFactory.getInstance();

        getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            getServer().getDefaultLevel().getPlayers().values()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(Player::isOnline)
                    .forEach(player -> {
                        UserSession session = sessionFactory.getSession(player.getLoginChainData().getXUID());
                        if (session == null) {
                            return;
                        }

                        boolean rewardClaimed = session.isRewardClaimed();
                        if (rewardClaimed) {
                            return;
                        }

                        int eggsFound = session.getCountFoundEasterEggs();
                        int maxEggs = EasterEggFactory.MAX_EGGS;

                        if (eggsFound >= maxEggs) {
                            player.sendPopup(TextFormat.LIGHT_PURPLE + "Claim your reward with the Easter Bunny");
                        } else {
                            player.sendPopup(TextFormat.colorize(String.format("&bYou have found &e%s/%s &bEaster Eggs", eggsFound, maxEggs)));
                        }
                    });
        }, 20);
    }

    private void initMongoDB() {
        Config config = getConfig();

        String host = config.getString("mongodb.host", "localhost");
        int port = config.getInt("mongodb.port", 27017);
        String username = config.getString("mongodb.username");
        String database = config.getString("mongodb.database");
        String password = config.getString("mongodb.password", "password");

        MongoDBProvider.make(host, port, username, database, password);
    }

    private void initFactories() {
        SessionFactory.make();
        EasterEggFactory.make(getConfig());
    }

    private void registerCommands() {
        SimpleCommandMap commandMap = getServer().getCommandMap();
        commandMap.register("easteregghunt", new RegisterEggCommand());
        commandMap.register("easteregghunt", new RegisterBunnyCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "this has been disabled");
    }
}