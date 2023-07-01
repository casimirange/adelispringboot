
package com.adeli.adelispringboot.authentication.service;



import com.adeli.adelispringboot.Users.entity.Users;
import com.adeli.adelispringboot.Users.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private IUserRepo userRepo;

	@Override
//	@Transactional
	public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException{
		Users user = userRepo.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("l'utilisateur " + username + " n'a pas été trouvé"));
		return UserDetailsImpl.build(user);
	}

}
