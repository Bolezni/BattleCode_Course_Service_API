package com.bolezni.battlecode_course_service_api.mapper;

import com.bolezni.battlecode_course_service_api.dto.review.ReviewComplaintInfo;
import com.bolezni.battlecode_course_service_api.model.ReviewComplaintEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewComplaintMapper {

    @Mapping(source = "review.id", target = "reviewId")
    @Mapping(source = "id", target = "reviewComplainId")
    ReviewComplaintInfo mapToReviewComplaintInfo(ReviewComplaintEntity entity);
}
