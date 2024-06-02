package com.nguyenthanhnhan.backendshopcaulong.service.jwt;

import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import com.nguyenthanhnhan.backendshopcaulong.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> userDetail = repository.findByEmail(email);
        if (userDetail.isPresent()) {
            Users user = userDetail.get();
            if (!user.isEnabled()) {
                throw new UsernameNotFoundException("Người dùng chưa được kích hoạt");
            }
            return new UserInfoDetails(user);
        } else {
            throw new UsernameNotFoundException("Không tìm thấy người dùng " + email);
        }
    }

    public Optional<Users> findByActivationCode(String activationCode) {
        return repository.findByActivationCode(activationCode);
    }
    
    public String addUser(Users userInfo) {
        if (repository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }
        if (userInfo.getRoles() == null) {
            userInfo.setRoles("USER");
        }
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "Đăng ký người dùng thành công!";
    }
    
    public Optional<Users> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<Users> findByToken(String token) {
        String email = jwtService.extractUsername(token);
        Optional<Users> user = repository.findByEmail(email);
        return user.filter(u -> u.getRoles().contains("USER") || u.getRoles().contains("ADMIN"));
    }

    //reset
    public String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    public void updateUser(Users user) {
        repository.save(user);
    }

    public String encodePassword(String password) {
        return encoder.encode(password);
    }
}
