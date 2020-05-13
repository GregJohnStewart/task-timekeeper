package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import io.quarkus.mongodb.panache.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@MongoEntity(collection="Users")
public class ManagerEntity extends OurMongoEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerEntity.class);

    private ObjectId userId;
    private byte[] timeManagerData;

    private Date lastUpdate;

    /**
     * Gets the manager entity for the given user.
     * @param userId The id of the user to get the manager entity for.
     * @return
     * @throws EntityNotFoundException
     */
    public static ManagerEntity findByUserId(ObjectId userId) throws EntityNotFoundException {
        List<ManagerEntity> managerEntities = ManagerEntity.list("userId", userId);

        if(managerEntities.isEmpty()){
            throw new EntityNotFoundException("No manager entity with the username found.");
        }

        if(managerEntities.size() > 1){
            LOGGER.warn("Multiple entities exist for user {}", userId);
        }

        return managerEntities.get(0);
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ManagerEntity)) return false;
        final ManagerEntity other = (ManagerEntity) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$userId = this.getUserId();
        final Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        if (!java.util.Arrays.equals(this.getTimeManagerData(), other.getTimeManagerData())) return false;
        final Object this$lastUpdate = this.getLastUpdate();
        final Object other$lastUpdate = other.getLastUpdate();
        if (this$lastUpdate == null ? other$lastUpdate != null : !this$lastUpdate.equals(other$lastUpdate))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ManagerEntity;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        result = result * PRIME + java.util.Arrays.hashCode(this.getTimeManagerData());
        final Object $lastUpdate = this.getLastUpdate();
        result = result * PRIME + ($lastUpdate == null ? 43 : $lastUpdate.hashCode());
        return result;
    }


    //TODO:: has w/ user id
}
