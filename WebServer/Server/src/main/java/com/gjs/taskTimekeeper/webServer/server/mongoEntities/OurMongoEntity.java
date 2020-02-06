package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.Objects;

/**
 * Wrapper for the panache entity to implement hashcode/ equals
 */
public abstract class OurMongoEntity extends PanacheMongoEntity {
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        final User other = (User) o;
        if (!other.canEqual((Object) this)) return false;
        if(this.id != null) {
            if (!this.id.equals(other.id)) return false;
        } else {
            if(other.id != null) return false;
        }
        return true;
    }
}
