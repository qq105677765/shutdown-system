package com.fawvw.shutdownsystem.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "failurelog_ca", uniqueConstraints = @UniqueConstraint(columnNames = { "start_time", "equipment_area" }))
@Data
public class FailureLogCA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime; // 开始时间
    @Column()
    private LocalDateTime endTime; // 结束时间
    @Column(name = "shift_number")
    private int ShiftNumber; // 班次号
    @Column(name = "equipment_area")
    private String equipmentArea; // 设备区域
    @Column(name = "section")
    private String section; // 工段（来自“设备区域”截取）
    @Column(name = "position")
    private String position; // 工位（来自“设备区域”截取）
    @Column(name = "alarm_info")
    private String alarmInfo; // 警报信息
    @Column()
    private Double duration; // 持续时间
    @Column()
    private int priority; // 优先级
    @Column(name = "record_date")
    LocalDate recordDate; // 记录时间（如果小于8:30算前一天）
    @Column(name = "notification_form")
    String notificationForm; // 通知形式
}
