package com.adeli.adelispringboot.Users.service;


import com.adeli.adelispringboot.Users.dto.ResponseUsersDTO;
import com.adeli.adelispringboot.Users.dto.UserEditPasswordDto;
import com.adeli.adelispringboot.Users.dto.UserReqDto;
import com.adeli.adelispringboot.Users.dto.UserResDto;
import com.adeli.adelispringboot.Users.entity.*;
import com.adeli.adelispringboot.Users.mapper.UserMapper;
import com.adeli.adelispringboot.Users.repository.*;
import com.adeli.adelispringboot.authentication.service.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private IStatusUserRepo statusRepo;
	@Autowired
	private IRoleUserRepo roleRepo;
	@Autowired
	private IOldPasswordRepo oldPasswordRepo;
	@Autowired
	private ITypeAccountRepository typeAccountRepo;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private ResourceBundleMessageSource messageSource;

	@Override
	@Transactional
	public Map<String, Object> add(Users u) {
		Users user = new Users(u.getUserId(), u.getEmail(), u.getPassword());

		StatusUser status = statusRepo.findByName(EStatusUser.USER_ENABLED);
		user.setStatus(status);
		user.setTokenAuth(null);
		user.setCreatedDate(LocalDateTime.now());
		user.setFirstName(u.getFirstName());
		user.setLastName(u.getLastName());
		user.setTelephone(u.getTelephone());
		user.setMontant(u.getMontant());
		user.setRoles(u.getRoles());
		user.setTypeAccount(u.getTypeAccount());
		user.setCreatedDate(LocalDateTime.now());
		userRepo.save(user);
		Map<String, Object> userAndPasswordNotEncoded = new HashMap<>();
		userAndPasswordNotEncoded.put("user", user);
		userAndPasswordNotEncoded.put("password", u.getPassword());
		return userAndPasswordNotEncoded;
	}

//	@Override
//	@Transactional
//	public UserResDto addUser(UserReqDto userReqDto) {
//		Users user = userMapper.userReqDtoToUsers(userReqDto);
//		Users savedUser = userRepo.saveAndFlush(user);
//		UserResDto userResDto = userMapper.userToUserResDto(savedUser);
//		return userResDto;
//	}

	@Override
	@Transactional
	public Map<String, Object> modify(Users u) {
		userRepo.save(u);
		Map<String, Object> userAndPasswordNotEncoded = new HashMap<>();
		userAndPasswordNotEncoded.put("user", u);
		userAndPasswordNotEncoded.put("password", u.getPassword());
		return userAndPasswordNotEncoded;
	}

	@Override
	public Users getById(Long id) {
		Users user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
				messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));

		return user;
	}

	@Override
	public Users getByInternalReference(Long internalReference) {
		return null;
	}

	@Override
	@Transactional
	public Optional<Users> getByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public Optional<Users> getByPinCode(int pinCode) {
		return Optional.empty();
	}

	@Override
	public Optional<Users> getByTelephone(String tel) {
		Optional<Users> user = userRepo.findByTelephone(tel);
		return user;
	}

	@Override
	public Users getByOtpCode(String code) {
		return userRepo.findByOtpCode(code)
				.orElseThrow(() -> new ResourceNotFoundException("User code OTP " + code + " not found"));
	}

	@Override
	@Transactional
	public Users editUser(Long userId, Users u) {
		Users user = getById(userId);
		user.setEmail(u.getEmail());
		user.setTelephone(u.getTelephone());
		user.setFirstName(u.getFirstName());
		user.setLastName(u.getLastName());
		user.setTelephone(u.getTelephone());
		user.setTypeAccount(u.getTypeAccount());
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public Users editToken(Long id, String token) {
		Users user = getById(id);
		user.setTokenAuth(token);
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public void editTokenFcm(Long id, String tokeFcm) {
		Users user = getById(id);
		user.setNotificationKey(tokeFcm);
		userRepo.save(user);
	}


	@Override
	@Transactional
	public Users editStatus(Long id, Long statusId) {
		Users user = getById(id);
		StatusUser status = statusRepo.findById(statusId)
				.orElseThrow(() -> new ResourceNotFoundException("Status id " + statusId + " not found"));
		user.setStatus(status);
		if (user.getStatus().equals(status)) {
			return user;
		}
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public Users editEmail(Long id, String email) {
		Users user = getById(id);
		user.setEmail(email);
		return userRepo.save(user);
	}

	@Override
	public Optional<Users> getUserByUniqueConstraints(Users u) {
		Optional<Users> user = userRepo.findById(u.getUserId());
		if(user.isPresent()) {
			return user;
		}
		if(u.getEmail() != null) {
			user = userRepo.findByEmail(u.getEmail());
			if(user.isPresent()) {
				return user;
			}
		}
		if(u.getTelephone() != null) {
			user = userRepo.findByTelephone("+" + u.getTelephone());
			if(user.isPresent()) {
				return user;
			}
		}
		return Optional.empty();
	}

	public boolean existsByEmail(String email, Long id) {
		if (email == null || email.isEmpty()) {
			return false;
		}
		Optional<Users> user = userRepo.findByEmail(email);
		return checkOwnerIdentity(id, user);
	}

	@Override
	public boolean existsByPinCode(int code) {
		return false;
	}

	@Override
	public boolean existsByInternalReference(Long internalReference) {
		return false;
	}

	@Override
	public boolean existsByTelephone(String tel, Long id) {
		if (tel == null || tel.isEmpty()) {
			return false;
		}
		Optional<Users> user = userRepo.findByTelephone(tel);
		return checkOwnerIdentity(id, user);
	}

	@Override
	@Transactional
	public Users checkUserAndGenerateCode(String login) {
		Users user;

		user = userRepo.findByEmail(login).orElseThrow(() -> new ResourceNotFoundException(
				messageSource.getMessage("messages.user_not_found", null, LocaleContextHolder.getLocale())));
		String token = jwtUtils.generateJwtToken(user.getEmail(), jwtUtils.getExpirationEmailResetPassword(),
				jwtUtils.getSecretBearerToken(),true);
		user.setTokenAuth(token);

		return userRepo.save(user);
	}

	@Override
	@Transactional
	public Users resetPassword(Users user, String password) {
		user.setPassword(encoder.encode(password));
		OldPassword oldPassword = oldPasswordRepo.save(new OldPassword(encoder.encode(password)));
		user.getOldPasswords().add(oldPassword);
		user.setOtpCode(null);
		user.setOtpCodeCreatedAT(null);
		user.setTokenAuth(null);
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public String editPassword(Users user, UserEditPasswordDto u) {
		OldPassword oldPassword = oldPasswordRepo.save(new OldPassword(encoder.encode(u.getPassword())));
		user.getOldPasswords().add(oldPassword);
		user.setPassword(encoder.encode(u.getPassword()));
		user.setTokenAuth(null);
		userRepo.save(user);
		return "Mot de passe changé avec succès";
	}

	@Override
	@Transactional
	public Users editProfil(Users user, Long updateRoleId, Long addRoleId) {
		Set<RoleUser> rolesList = user.getRoles();
		RoleUser addRoleUser = roleRepo.findById(addRoleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role id " + addRoleId + " not found"));
		if (updateRoleId != 0) {
			RoleUser updateRoleUser = roleRepo.findById(updateRoleId)
					.orElseThrow(() -> new ResourceNotFoundException("Role id " + updateRoleId + " not found"));
			if (rolesList.contains(updateRoleUser)) {
				rolesList.remove(updateRoleUser);
				rolesList.add(addRoleUser);
			} else {
				throw new ResourceNotFoundException("User doesn't have profil " + updateRoleUser.getName().toString());
			}
		} else {
			rolesList.add(addRoleUser);
		}
		return userRepo.save(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {
		userRepo.deleteById(userId);
	}

	private boolean checkOwnerIdentity(Long id, Optional<Users> user) {
		boolean taken = false;
		if (!user.isPresent()) {
			return taken;
		}
		if (id != null) {
			if (id.equals(user.get().getUserId())) {
				return taken;
			} else {
				taken = true;
				return taken;
			}
		} else {
			taken = true;
			return taken;
		}
	}

	private List<RoleUser> getRolesManagers() {
		List<ERole> rolesNames = new ArrayList<>();
		rolesNames.add(ERole.ROLE_SUPERADMIN);
		rolesNames.add(ERole.ROLE_USER);
		return rolesNames.stream()
				.map(roleName -> roleRepo.findByName(roleName)
						.orElseThrow(() -> new ResourceNotFoundException("Role name " + roleName + " not found")))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Users getNewCodeVerificationAccount(String login) {
		Optional<Users> user = getByEmail(login);
		if (user.isPresent()) {
			String otpCode = String.valueOf(jwtUtils.generateOtpCode());
			user.get().setOtpCode(otpCode);
			user.get().setOtpCodeCreatedAT(LocalDateTime.now());

		} else {
			user = userRepo.findByEmail(login);
			String token = jwtUtils.generateJwtToken(user.get().getEmail(), jwtUtils.getExpirationEmailVerifToken(),
					jwtUtils.getSecretBearerToken(),true);
			user.get().setTokenAuth(token);
		}
		return userRepo.save(user.get());
	}

	@Override
	@Transactional
	public Page<ResponseUsersDTO> getUsers(int page, int size, String sort, String order) {
		List<Users> usersList = userRepo.findAll();
		ResponseUsersDTO responseUsersDTO;
		List<ResponseUsersDTO> responseUsersDTOList = new ArrayList<>();

		for (Users user: usersList){
			responseUsersDTO = new ResponseUsersDTO();
			responseUsersDTO.setStatus(user.getStatus());
			responseUsersDTO.setUserId(user.getUserId());
			responseUsersDTO.setEmail(user.getEmail());
			responseUsersDTO.setRoleNames(user.getRoleNames());
			responseUsersDTO.setFirstName(user.getFirstName());
			responseUsersDTO.setMontant(user.getMontant());
			responseUsersDTO.setLastName(user.getLastName());
			responseUsersDTO.setRoles(user.getRoles());
			responseUsersDTO.setTelephone(user.getTelephone());
			responseUsersDTO.setTypeAccount(user.getTypeAccount());
			responseUsersDTO.setOtpCode(user.getOtpCode());
			responseUsersDTO.setCreatedDate(user.getCreatedDate());
			responseUsersDTO.setDateLastLogin(user.getDateLastLogin());
			responseUsersDTOList.add(responseUsersDTO);
		}
		Page<ResponseUsersDTO> responseUsersDTOPage = new PageImpl<>(responseUsersDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), responseUsersDTOList.size());
		return responseUsersDTOPage;
	}

	@Override
	@Transactional
	public Page<UserResDto> getAllUsers(int page, int size, String sort, String order) {
		Page<Users> usersList = userRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

		Page<UserResDto> responseUsersDTOPage = new PageImpl<>(usersList.stream().map(user -> userMapper.userToUserResDto(user)).collect(Collectors.toList()), PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), userRepo.findAll().size());
		return responseUsersDTOPage;
	}

	@Override
	public Page<Users> get20Users(int page, int size, String sort, String order) {
		StatusUser status = statusRepo.findByName(EStatusUser.USER_ENABLED);
		return userRepo.findTop20ByStatus(status , PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));
	}

	@Override
	public List<Users> getUsers() {
		return userRepo.findAll();
	}

	@Override
	public void updateDateLastLoginUser(Long id_user) {
		 Users u = getById(id_user);
		 u.setDateLastLogin(LocalDateTime.now());
		 userRepo.save(u);
	}


	@Override
	public Users updateAuthToken(Long id, String token) {
		Optional<Users> users = userRepo.findById(id);
		if(!users.isPresent() || users.get().isDelete()) {
			throw new ResourceNotFoundException("User  not found");
		}
		users.get().setTokenAuth(token);
		return userRepo.save(users.get());
	}

	@Override
	public Users updateOtpCode(Long id, String code) {
		Optional<Users> users = userRepo.findById(id);
		if(!users.isPresent() || users.get().isDelete()) {
			throw new ResourceNotFoundException("User  not found");
		}
		users.get().setOtpCode(code);
		users.get().setOtpCodeCreatedAT(LocalDateTime.now());
		return userRepo.save(users.get());
	}
	@Override
	public void updateFistLogin(Long user_id) {
		Users u = userRepo.findById(user_id).get();
		if(u.getOldPasswords().size() <=1 && u.getCreatedDate().isAfter(LocalDateTime.of(2022, Month.NOVEMBER,01, 23, 01, 01))) {
			u.setFirstConnection(true);
		} else  {
			u.setFirstConnection(false);
		}
		userRepo.save(u);
	}

   @Override
  public Users lockAndUnlockUsers(Long id_user, boolean status) {
		Users u = getById(id_user);
		if(status == true) {
			StatusUser statusUser = statusRepo.findByName(EStatusUser.USER_ENABLED);
			u.setStatus(statusUser);
		} else {
			StatusUser statusUser = statusRepo.findByName(EStatusUser.USER_DISABLED);
			u.setStatus(statusUser);
		}
		return  userRepo.save(u);
	}

	@Override
	public Page<ResponseUsersDTO> filtres(String statusName, String typeAccount, Double montant, String firstName, String lastName, int page, int size, String sort, String order) {

		Specification<Users> specification = ((root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (firstName != null && !firstName.isEmpty()){
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName + "%"));
			}

			if (lastName != null && !lastName.isEmpty()){
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName + "%"));
			}

			if (montant != null && !montant.toString().isEmpty()){
				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("montant")), montant));
			}

			if (typeAccount != null && !typeAccount.isEmpty()){
				Optional<TypeAccount> typeVoucher = typeAccountRepo.findByName(ETypeAccount.valueOf(typeAccount));
				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("typeAccount")), typeVoucher.map(TypeAccount::getId).orElse(null)));
			}

			if (statusName != null && !statusName.isEmpty()){
				StatusUser status = statusRepo.findByName(EStatusUser.valueOf(statusName.toUpperCase()));
				predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), status));
			}
			return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
		});

		Page<Users> users = userRepo.findAll(specification, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)));

		ResponseUsersDTO responseUsersDTO;
		List<ResponseUsersDTO> responseUsersDTOList = new ArrayList<>();

		for (Users user: users){
			responseUsersDTO = new ResponseUsersDTO();
			responseUsersDTO.setStatus(user.getStatus());
			responseUsersDTO.setUserId(user.getUserId());
			responseUsersDTO.setEmail(user.getEmail());
			responseUsersDTO.setRoleNames(user.getRoleNames());
			responseUsersDTO.setMontant(user.getMontant());
			responseUsersDTO.setFirstName(user.getFirstName());
			responseUsersDTO.setLastName(user.getLastName());
			responseUsersDTO.setRoles(user.getRoles());
			responseUsersDTO.setTelephone(user.getTelephone());
			responseUsersDTO.setTypeAccount(user.getTypeAccount());
			responseUsersDTO.setOtpCode(user.getOtpCode());
			responseUsersDTO.setCreatedDate(user.getCreatedDate());
			responseUsersDTO.setDateLastLogin(user.getDateLastLogin());
			responseUsersDTOList.add(responseUsersDTO);

		}
		return new PageImpl<>(responseUsersDTOList, PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort)), users.getTotalElements());

	}
}
