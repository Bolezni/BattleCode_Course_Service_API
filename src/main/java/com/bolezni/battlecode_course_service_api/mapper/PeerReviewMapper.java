package com.bolezni.battlecode_course_service_api.mapper;

import com.bolezni.battlecode_course_service_api.dto.review.PeerReviewInfo;
import com.bolezni.battlecode_course_service_api.model.PeerReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ReviewComplaintMapper.class})
public interface PeerReviewMapper {

    @Mapping(target = "peerReviewId", source = "id")
    @Mapping(target = "submissionId", source = "submission.id")
    @Mapping(target = "complaints", source = "complaints")
    PeerReviewInfo mapToPeerReviewInfo(PeerReviewEntity entity);
}
