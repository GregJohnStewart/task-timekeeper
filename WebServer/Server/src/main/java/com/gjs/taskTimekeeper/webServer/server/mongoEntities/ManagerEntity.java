package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;
import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import io.quarkus.mongodb.panache.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "Users")
@Slf4j
public class ManagerEntity extends OurMongoEntity {
	@BsonIgnore
	public static final ObjectMapper MANAGER_MAPPER = ObjectMapperUtilities.getTimeManagerObjectMapper();
	
	private ObjectId userId;
	//TODO:: when mongo supports new java datetime objects, use real time manager object
	private byte[] timeManagerData;
	
	private Date lastUpdate;
	
	public Date updateManagerData(byte[] manager) {
		if(!Arrays.equals(this.timeManagerData, manager)) {
			this.setTimeManagerData(manager);
			this.setLastUpdate(new Date());
			this.update();
		}
		return this.getLastUpdate();
	}
	
	public Date updateManagerData(TimeManager manager) throws JsonProcessingException {
		return this.updateManagerData(MANAGER_MAPPER.writeValueAsBytes(manager));
	}
	
	/**
	 * Gets the manager entity for the given user.
	 * <p>
	 * TODO:: test
	 *
	 * @param userId The id of the user to get the manager entity for.
	 * @return
	 * @throws EntityNotFoundException
	 */
	public static ManagerEntity findByUserId(ObjectId userId) throws EntityNotFoundException {
		List<ManagerEntity> managerEntities = ManagerEntity.list("userId", userId);
		
		if(managerEntities.isEmpty()) {
			throw new EntityNotFoundException("No manager entity with the username found.");
		}
		
		if(managerEntities.size() > 1) {
			log.warn("Multiple entities exist for user {}", userId);
		}
		
		return managerEntities.get(0);
	}
	
	/**
	 * Gets the user's manager entity. In the case where they don't already have one, an empty time manager is created for them.
	 * //TODO:: test
	 *
	 * @param userId The id of the user to get the managerEntity for
	 * @return
	 */
	public static ManagerEntity getOrCreateNew(ObjectId userId) {
		ManagerEntity entity;
		
		try {
			entity = ManagerEntity.findByUserId(userId);
		} catch(EntityNotFoundException e) {
			try {
				entity = new ManagerEntity(
					userId,
					MANAGER_MAPPER.writeValueAsBytes(new TimeManager()),
					null
				);
			} catch(JsonProcessingException e2) {
				log.error("Failed to create new empty manager entity for user: ", e2);
				throw new WebServerException("Failed to create new empty manager entity for user: ", e2);
			}
			entity.persist();
		}
		return entity;
	}
}
