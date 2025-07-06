package com.tomazi.streaming.presentation.mappers;

import com.tomazi.streaming.domain.entities.User;
import com.tomazi.streaming.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toResponse(User user);
}
