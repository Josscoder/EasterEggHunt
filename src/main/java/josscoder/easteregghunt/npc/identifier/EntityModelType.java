package josscoder.easteregghunt.npc.identifier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EntityModelType {
    FLOWERS_EGG("joss:easter_egg");

    private final String id;
}
