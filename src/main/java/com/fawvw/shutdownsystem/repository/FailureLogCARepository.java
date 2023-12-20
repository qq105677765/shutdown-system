package com.fawvw.shutdownsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import com.fawvw.shutdownsystem.model.FailureLogCA;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface FailureLogCARepository extends JpaRepository<FailureLogCA, Long> {

    boolean existsByStartTimeAndEquipmentArea(LocalDateTime startTime, String equipmentArea);

    /**
     * 如果不存在，存入数据库
     * 
     * @param entitys
     */
    default void saveAllIfNotExists(List<FailureLogCA> entitys) {
        for (var entity : entitys) {
            boolean exist = existsByStartTimeAndEquipmentArea(entity.getStartTime(), entity.getEquipmentArea());
            if (!exist) {
                try {
                    save(entity);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 车间某天TOP累计停台
     * 
     * @param date  日期
     * @param limit 记录数量（前num名）
     * @return List<Map<String,Object>>
     */
    @Query(value = "SELECT sum(duration) as duration, section FROM (SELECT * FROM failurelog_ca WHERE record_date= :date ) AS a "
            +
            "GROUP BY record_date,section ORDER BY duration DESC LIMIT :limit", nativeQuery = true)
    List<Map<String, Object>> queryTopDuration(@Param("date") LocalDate currentDate, @Param("limit") int limit);

    /**
     * 获取时间区间内的停台时长趋势
     * 
     * @param starDate 开始时间
     * @param endDate  停止时间
     * @param ws       工段名,如果值为null则统计车间信息
     * @return List<Map<String,Object>>
     */
    @Query(value = "SELECT sum(duration)as duration, record_date " +
            "From (SELECT * FROM failurelog_ca WHERE record_date BETWEEN :startDate and :endDate and if(:ws IS NULL,1=1,section=':ws')) AS a "
            +
            "GROUP BY record_date ORDER BY record_date;", nativeQuery = true)
    List<Map<String, Object>> queryAccumTend(
            @Param("startDate") LocalDate starDate,
            @Param("endDate") LocalDate endDate,
            @Nullable @Param("ws") String ws);

    // @Query(value = "", nativeQuery = true)
    // List<Map<String, Object>> queryByEndTimeAndDuration();

    /**
     * 获取一个小时前大于10分钟的停台
     * 
     * @param starTime 时间区域开始
     * @param endTime  时间区间结束
     * @return
     */
    @Query(value = "SELECT equipment_area, duration ,start_time, end_time " +
            "FROM failurelog_ca WHERE end_time BETWEEN :st_time AND :ed_time AND duration > 10;", nativeQuery = true)
    List<Map<String, Object>> queryLast1hourmorethan10min(@Param("st_time") LocalDateTime starTime,
            @Param("ed_time") LocalDateTime endTime);

}