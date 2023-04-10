package josscoder.easteregghunt.session.data;

import josscoder.easteregghunt.mongodb.MongoDBProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Getter
public class UserSession {

    private final String xuid;
    private final List<Integer> easterEggsFound;
    private boolean rewardClaimed;

    public boolean hasEasterEggFound(int easterEgg) {
        return easterEggsFound.contains(easterEgg);
    }

    public void markEasterEggAsFound(int easterEgg) {
        CompletableFuture.runAsync(() -> {
            MongoDBProvider.getInstance().markEasterEggAsFound(easterEgg, xuid);
            easterEggsFound.add(easterEgg);
        });
    }

    public void markRewardAsClaimed() {
        CompletableFuture.runAsync(() -> {
            MongoDBProvider.getInstance().markRewardAsClaimed(xuid);
            rewardClaimed = true;
        });
    }

    public int getCountFoundEasterEggs() {
        return easterEggsFound.size();
    }
}
