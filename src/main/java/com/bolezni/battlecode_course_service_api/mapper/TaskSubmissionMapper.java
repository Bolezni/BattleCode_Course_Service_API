package com.bolezni.battlecode_course_service_api.mapper;

import com.bolezni.battlecode_course_service_api.dto.task.TaskSubmissionInfo;
import com.bolezni.battlecode_course_service_api.model.TaskSubmissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PeerReviewMapper.class})
public interface TaskSubmissionMapper {

    @Mapping(target = "taskSubmissionId", source = "id")
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "reviews", source = "reviews")
    TaskSubmissionInfo mapToTaskSubmissionInfo(TaskSubmissionEntity entity);
}
