package com.adeli.adelispringboot.Users.mapper;

import com.adeli.adelispringboot.Users.dto.UserResDto;
import com.adeli.adelispringboot.Users.entity.RoleUser;
import com.adeli.adelispringboot.Users.entity.Users;
import java.util.ArrayList;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-14T11:55:48+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResDto userToUserResDto(Users users) {
        if ( users == null ) {
            return null;
        }

        UserResDto userResDto = new UserResDto();

        userResDto.setUserId( users.getUserId() );
        userResDto.setEmail( users.getEmail() );
        userResDto.setTelephone( users.getTelephone() );
        userResDto.setLastName( users.getLastName() );
        userResDto.setFirstName( users.getFirstName() );
        userResDto.setMontant( users.getMontant() );
        Set<RoleUser> set = users.getRoles();
        if ( set != null ) {
            userResDto.setRoles( new ArrayList<RoleUser>( set ) );
        }
        userResDto.setTypeAccount( users.getTypeAccount() );

        return userResDto;
    }
}
