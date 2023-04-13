package josscoder.easteregghunt.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import josscoder.easteregghunt.mongodb.MongoDBProvider;
import josscoder.easteregghunt.session.SessionFactory;
import josscoder.easteregghunt.session.data.UserSession;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AccountHandlerListener implements Listener {

    private final MongoDBProvider mongoDBProvider;
    private final SessionFactory sessionFactory;

    public AccountHandlerListener() {
        mongoDBProvider = MongoDBProvider.getInstance();
        sessionFactory = SessionFactory.getInstance();
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        String xuid = event.getPlayer().getLoginChainData().getXUID();

        CompletableFuture.runAsync(() -> {
            Document document = mongoDBProvider.getOrCreateUserDoc(xuid);

            List<Integer> easterEggsFound = mongoDBProvider.getEasterEggsFound(document);
            boolean rewardClaimed = mongoDBProvider.hasRewardClaimed(document);

            UserSession userSession = new UserSession(xuid, easterEggsFound, rewardClaimed);
            sessionFactory.store(userSession);
        });
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        String xuid = event.getPlayer().getLoginChainData().getXUID();
        sessionFactory.remove(xuid);
    }
}
