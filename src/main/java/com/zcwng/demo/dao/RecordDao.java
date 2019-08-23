package com.zcwng.demo.dao;

import com.zcwng.demo.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordDao extends JpaRepository<Record,Long> {

    @Query("from Record r where r.to=?1 and r.used=false")
    List<Record> findUnread(long userid);
}
