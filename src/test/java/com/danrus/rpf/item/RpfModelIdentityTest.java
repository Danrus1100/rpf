package com.danrus.rpf.item;

import com.danrus.rpf.core.item.RpfModelIdentity;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RpfModelIdentityTest {

    @Test
    @DisplayName("Equal identities have same hashCode")
    void equalIdentities_sameHashCode() {
        Identifier location = Identifier.fromNamespaceAndPath("test", "model");
        String packName = "pack";
        
        RpfModelIdentity id1 = new RpfModelIdentity(location, packName);
        RpfModelIdentity id2 = new RpfModelIdentity(location, packName);
        
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Different locations produce non-equal identities")
    void differentLocations_notEqual() {
        Identifier location1 = Identifier.fromNamespaceAndPath("test", "model1");
        Identifier location2 = Identifier.fromNamespaceAndPath("test", "model2");
        
        RpfModelIdentity id1 = new RpfModelIdentity(location1, "pack");
        RpfModelIdentity id2 = new RpfModelIdentity(location2, "pack");
        
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Different pack names produce non-equal identities")
    void differentPackNames_notEqual() {
        Identifier location = Identifier.fromNamespaceAndPath("test", "model");
        
        RpfModelIdentity id1 = new RpfModelIdentity(location, "pack1");
        RpfModelIdentity id2 = new RpfModelIdentity(location, "pack2");
        
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Record components are accessible")
    void recordComponents_accessible() {
        Identifier location = Identifier.fromNamespaceAndPath("namespace", "path");
        String packName = "testPack";
        
        RpfModelIdentity identity = new RpfModelIdentity(location, packName);
        
        assertEquals(location, identity.location());
        assertEquals(packName, identity.packName());
    }

    @Test
    @DisplayName("toString contains both components")
    void toString_containsComponents() {
        Identifier location = Identifier.fromNamespaceAndPath("namespace", "path");
        RpfModelIdentity identity = new RpfModelIdentity(location, "testPack");
        
        String str = identity.toString();
        
        assertTrue(str.contains("namespace:path"));
        assertTrue(str.contains("testPack"));
    }

    @Test
    @DisplayName("Identity is not equal to null")
    void identity_notEqualToNull() {
        RpfModelIdentity identity = new RpfModelIdentity(
            Identifier.fromNamespaceAndPath("test", "model"), "pack"
        );
        
        assertNotEquals(null, identity);
    }

    @Test
    @DisplayName("Identity is not equal to different type")
    void identity_notEqualToDifferentType() {
        RpfModelIdentity identity = new RpfModelIdentity(
            Identifier.fromNamespaceAndPath("test", "model"), "pack"
        );
        
        assertNotEquals("string", identity);
        assertNotEquals(42, identity);
    }

    @Test
    @DisplayName("Identity equals itself")
    void identity_equalsItself() {
        RpfModelIdentity identity = new RpfModelIdentity(
            Identifier.fromNamespaceAndPath("test", "model"), "pack"
        );
        
        assertEquals(identity, identity);
    }
}
