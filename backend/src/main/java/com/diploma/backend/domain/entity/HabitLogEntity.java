package com.diploma.backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "habit_logs")
@Getter
@Setter
@NoArgsConstructor
public class HabitLogEntity {

    @EmbeddedId
    private HabitLogId id = new HabitLogId();

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "updated_at")
    private Long updatedAt = 0L;

    
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HabitLogId implements Serializable {
        @Column(name = "habit_id")
        private String habitId;

        @Column(name = "user_id")
        private String userId;

        @Column(name = "date_str")
        private String dateStr;

        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HabitLogId that = (HabitLogId) o;
            return Objects.equals(habitId, that.habitId) &&
                    Objects.equals(userId, that.userId) &&
                    Objects.equals(dateStr, that.dateStr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(habitId, userId, dateStr);
        }
    }
}