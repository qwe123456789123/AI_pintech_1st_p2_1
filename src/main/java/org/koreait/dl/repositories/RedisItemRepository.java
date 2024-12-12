package org.koreait.dl.repositories;

import org.koreait.dl.entities.RedisItem;
import org.springframework.data.repository.CrudRepository;

public interface RedisItemRepository extends CrudRepository<RedisItem, String>{
}
