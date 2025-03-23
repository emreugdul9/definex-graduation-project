package com.definexjavaspringbootbootcamp.definexgraduationproject.mapper;

import com.definexjavaspringbootbootcamp.definexgraduationproject.dto.CreateProjectDto;
import com.definexjavaspringbootbootcamp.definexgraduationproject.entity.project.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProjectFromDto(CreateProjectDto projectDto, @MappingTarget Project project);

}
