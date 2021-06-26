package com.live.room.server.dao;

import com.live.room.server.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface UserDao extends CrudRepository<UserEntity,String>{
}
