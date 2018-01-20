package ch.voulgarakis.icsc2018.recruitment.events;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class SkillEntityListener {
    @PrePersist
    void onPrePersist(Object entity) {
    }

    @PostPersist
    void onPostPersist(Object entity) {
    }

    @PostLoad
    void onPostLoad(Object entity) {
    }

    @PreUpdate
    void onPreUpdate(Object entity) {
    }

    @PostUpdate
    void onPostUpdate(Object entity) {
    }

    @PreRemove
    void onPreRemove(Object entity) {
    }

    @PostRemove
    void onPostRemove(Object entity) {
    }
}