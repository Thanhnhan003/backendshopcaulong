package com.nguyenthanhnhan.backendshopcaulong.service.jwt;

import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import com.nguyenthanhnhan.backendshopcaulong.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    // kích hoạt tài khoản bằng email
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

    public Users findUserById(UUID userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
    }

    // thay đổi mật khẩu
    public String changePassword(String token, String oldPassword, String newPassword) {
        String email = jwtService.extractUsername(token);
        Users user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));

        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("mật khẩu cũ không đúng");
        }

        user.setPassword(encoder.encode(newPassword));
        repository.save(user);

        return "Đã thay đổi mật khẩu thành công";
    }

    // reset mật khẩu
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

    /// =======get all người dùng chỉ admin mới sài được
    // Trong class UserInfoService
    public List<Users> getAllUsers() {
        return repository.findAll();
    }

    public boolean isAdmin(String token) {
        String email = jwtService.extractUsername(token);
        Optional<Users> user = repository.findByEmail(email);
        return user.isPresent() && user.get().getRoles().contains("ADMIN");
    }

    /// =======cập nhập trạng thái người dùng
    public void updateEnabledStatus(UUID userId, boolean enabled) {
        Users user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        user.setEnabled(enabled);
        repository.save(user);
    }
    //Tỉnh tổng số lượng tài khoản ADMIN và USER
    public long countUsersByRole(String role) {
        List<Users> users = repository.findByRoles(role);
        return users.size();
    }

    public long countUsersWithRolesAdminAndUser() {
        long adminCount = countUsersByRole("ADMIN");
        long userCount = countUsersByRole("USER");
        return adminCount + userCount;
    }
    //get thông tin người dùng dựa vào token
    public Users getUserDetailsByToken(String token) {
        String email = jwtService.extractUsername(token);
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    }
}
