package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import io.quarkus.mongodb.panache.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection="Users")
public class ManagerEntity extends OurMongoEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerEntity.class);

    private ObjectId userId;
    private byte[] timeManagerData;

    private Date lastUpdate;

    /**
     * Gets the manager entity for the given user.
     *
     * TODO:: test
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
}
