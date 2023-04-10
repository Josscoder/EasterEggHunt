package josscoder.easteregghunt.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import josscoder.easteregghunt.mongodb.indentifier.Fields;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class MongoDBProvider {

    @Getter
    private static MongoDBProvider instance;

    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> usersCollection;

    public MongoDBProvider(String host, int port, String username, String databaseId, String password) {
        MongoCredential credential = MongoCredential.createCredential(username, databaseId, password.toCharArray());

        ServerAddress serverAddress = new ServerAddress(host, port);

        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(serverAddress)))
                .build();

        client = MongoClients.create(settings);
        database = client.getDatabase(databaseId);

        usersCollection = database.getCollection("users");

        instance = this;
    }

    public static void make(String host, int port, String username, String databaseId, String password) {
        new MongoDBProvider(host, port, username, databaseId, password);
    }

    public Document getOrCreateUserDoc(String xuid) {
        Document userDoc = usersCollection.find(Filters.eq(Fields.XUID.id(), xuid)).first();
        if (userDoc == null) {
            userDoc = new Document(Fields.XUID.id(), xuid)
                    .append(Fields.CLAIMED.id(), false)
                    .append(Fields.EASTER_EGGS_FOUND.id(), new ArrayList<>());
            usersCollection.insertOne(userDoc);
        }

        return userDoc;
    }

    public List<Integer> getEasterEggsFound(Document document) {
        return document.getList(Fields.EASTER_EGGS_FOUND.id(), Integer.class, new ArrayList<>());
    }

    private void updateField(String xuid, Bson field) {
        usersCollection.updateOne(Filters.eq(Fields.XUID.id(), xuid), field);
    }

    public void markEasterEggAsFound(Integer easterEgg, String xuid) {
        updateField(xuid, Updates.addToSet(Fields.EASTER_EGGS_FOUND.id(), easterEgg));
    }

    public void markRewardAsClaimed(String xuid) {
        updateField(xuid, Updates.set(Fields.CLAIMED.id(), true));
    }

    public boolean hasRewardClaimed(Document document) {
        return document.getBoolean(Fields.CLAIMED.id(), false);
    }
}
