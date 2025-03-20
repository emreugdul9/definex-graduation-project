package com.definexjavaspringbootbootcamp.definexgraduationproject.mapper;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.TaskUpdateDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.task.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromDto(TaskUpdateDto taskUpdateDto, @MappingTarget Task task);
}
