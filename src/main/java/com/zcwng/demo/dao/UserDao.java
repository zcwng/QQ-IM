package com.zcwng.demo.dao;

import com.zcwng.demo.entity.Group;
import com.zcwng.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDao extends JpaRepository<User,Long> {
    User findByUsername(String username);

    /**
     * 谁的好友列表中有我
     * @param userid
     * @return
     */
    @Query("select g.user from Group g left join g.list u where u.id=?1")
    List<User> findByWhoFollwMe(long userid);
}
