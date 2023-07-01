package com.adeli.adelispringboot.Users.mapper;

import com.adeli.adelispringboot.Users.dto.ResponseUsersDTO;
import com.adeli.adelispringboot.Users.dto.UserReqDto;
import com.adeli.adelispringboot.Users.dto.UserResDto;
import com.adeli.adelispringboot.Users.entity.TypeAccount;
import com.adeli.adelispringboot.Users.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );
    UserResDto userToUserResDto(Users users);
//    @Mapping(source = "typeAccount", target = )
//    Users userReqDtoToUsers(UserReqDto userReqDto);
}
