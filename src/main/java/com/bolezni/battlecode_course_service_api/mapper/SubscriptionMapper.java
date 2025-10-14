package com.bolezni.battlecode_course_service_api.mapper;

import com.bolezni.battlecode_course_service_api.dto.subscriber.SubscriptionInfo;
import com.bolezni.battlecode_course_service_api.model.SubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "courseInfo", source = "course.id")
    SubscriptionInfo toSubscriber(SubscriptionEntity subscriber);
}
