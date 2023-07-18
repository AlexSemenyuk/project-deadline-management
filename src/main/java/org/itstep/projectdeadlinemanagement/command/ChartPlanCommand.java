package org.itstep.projectdeadlinemanagement.command;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.TaskCondition;

import java.time.LocalDateTime;

public class ChartPlanCommand {
    private int id;
    private int day;
    private int number;
    private LocalDateTime currentStart;
    private Integer projectNumber;
    private String equipment;
    private String part;
    private Integer termNumber;
    private Integer operationTime;
    private Integer lotNumber;
    private LocalDateTime projectStart;
    private String condition;

    public ChartPlanCommand(int id, int day, int number, LocalDateTime currentStart,
                            Integer projectNumber, String equipment, String part,
                            Integer termNumber, Integer operationTime, Integer lotNumber,
                            LocalDateTime projectStart, String condition) {
        this.id = id;
        this.day = day;
        this.number = number;
        this.currentStart = currentStart;
        this.projectNumber = projectNumber;
        this.equipment = equipment;
        this.part = part;
        this.termNumber = termNumber;
        this.operationTime = operationTime;
        this.lotNumber = lotNumber;
        this.projectStart = projectStart;
        this.condition = condition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDateTime getCurrentStart() {
        return currentStart;
    }

    public void setCurrentStart(LocalDateTime currentStart) {
        this.currentStart = currentStart;
    }

    public Integer getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(Integer projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public Integer getTermNumber() {
        return termNumber;
    }

    public void setTermNumber(Integer termNumber) {
        this.termNumber = termNumber;
    }

    public Integer getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Integer operationTime) {
        this.operationTime = operationTime;
    }

    public Integer getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(Integer lotNumber) {
        this.lotNumber = lotNumber;
    }

    public LocalDateTime getProjectStart() {
        return projectStart;
    }

    public void setProjectStart(LocalDateTime projectStart) {
        this.projectStart = projectStart;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "ChartPlanCommand{" +
                "id=" + id +
                ", day=" + day +
                ", number=" + number +
                ", currentStart=" + currentStart +
                ", projectNumber=" + projectNumber +
                ", equipment='" + equipment + '\'' +
                ", part='" + part + '\'' +
                ", termNumber=" + termNumber +
                ", operationTime=" + operationTime +
                ", lotNumber=" + lotNumber +
                ", projectStart=" + projectStart +
                ", condition='" + condition + '\'' +
                '}';
    }
}

