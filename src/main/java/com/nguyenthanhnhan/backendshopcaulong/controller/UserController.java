package com.nguyenthanhnhan.backendshopcaulong.controller;

import com.nguyenthanhnhan.backendshopcaulong.dto.AuthRequest;
import com.nguyenthanhnhan.backendshopcaulong.dto.UserResponse;
import com.nguyenthanhnhan.backendshopcaulong.entity.Users;
import com.nguyenthanhnhan.backendshopcaulong.service.email.EmailService;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.JwtService;
import com.nguyenthanhnhan.backendshopcaulong.service.jwt.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserInfoService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder encoder;
    private static final String UPLOAD_DIR = "src/main/resources/static/dataImage/users/";

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadUserAvatar(@RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Ensure this is a valid assignment
            Optional<Users> userOptional = service.findByToken(token);
            if (userOptional.isPresent()) {
                Users user = userOptional.get();

                try {
                    // Generate a unique filename
                    String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR + filename);

                    // Ensure the directory exists
                    Files.createDirectories(filePath.getParent());

                    // Save the file
                    Files.write(filePath, file.getBytes());

                    // Save the avatar URL in the database
                    user.setAvatar(filename);
                    service.updateUser(user);

                    return ResponseEntity.ok("Avatar uploaded successfully.");
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload avatar.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not provided.");
        }
    }

    @GetMapping("/images/{avatar}")
    public ResponseEntity<byte[]> getImage(@PathVariable String avatar) throws IOException {
        // You should sanitize and validate the thumbnail parameter to prevent directory
        // traversal attacks.
        // In this example, it is assumed that thumbnail is just the name of the image
        // file.

        Resource resource = new ClassPathResource("static/dataImage/users/" + avatar);
        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Set the appropriate image media type

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@RequestBody Users userInfo) {
        try {
            String activationCode = UUID.randomUUID().toString();
            userInfo.setActivationCode(activationCode);
            userInfo.setEnabled(false); // Set enabled to false until activation

            service.addUser(userInfo);

            // Send activation email
            String activationLink = "http://localhost:8080/auth/activate?code=" + activationCode;
            emailService.sendActivationEmail(userInfo.getEmail(), activationLink);

            return ResponseEntity.ok("Đăng ký thành công! Hãy kiểm tra email của bạn để kích hoạt tài khoản của bạn.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("code") String code) {
        Optional<Users> userOptional = service.findByActivationCode(code);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setEnabled(true);
            user.setActivationCode(null); // Clear the activation code after activation
            service.updateUser(user);
            return ResponseEntity.ok("Tài khoản được kích hoạt thành công!!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mã kích hoạt không hợp lệ.");
        }
    }

    // @GetMapping("/user/userProfile")
    // @PreAuthorize("hasAuthority('USER')")
    // public String userProfile() {
    // return "Welcome to User Profile";
    // }

    // @GetMapping("/admin/adminProfile")
    // @PreAuthorize("hasAuthority('ADMIN')")
    // public String adminProfile() {
    // return "Welcome to Admin Profile";
    // }
    @PostMapping("/loginadmin")
    public ResponseEntity<Map<String, Object>> authenticateAndGetTokenAdmin(@RequestBody AuthRequest authRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Users> userOptional = service.findByEmail(authRequest.getEmail());
            if (userOptional.isPresent()) {
                Users user = userOptional.get();
                if (user.isEnabled()) {
                    if (encoder.matches(authRequest.getPassword(), user.getPassword())) {
                        if ("ADMIN".equals(user.getRoles())) {
                            String token = jwtService.generateToken(authRequest.getEmail(),
                                    user.getUserId(),
                                    user.getFullname(),
                                    user.getRoles());
                            response.put("token", token);
                            return ResponseEntity.ok(response);
                        } else {
                            response.put("message", "Chỉ người dùng có vai trò ADMIN mới có quyền truy cập.");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                    } else {
                        response.put("message", "Mật khẩu không chính xác!");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                    }
                } else {
                    response.put("message", "Người dùng chưa được kích hoạt");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else {
                response.put("message", "Không tìm thấy người dùng");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi");
            response.put("error", e.getMessage()); // Thêm thông báo lỗi chi tiết
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestHeader("Authorization") String token,
            @RequestBody Users updatedUser) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Optional<Users> userOptional = service.findByToken(token);
            if (userOptional.isPresent()) {
                Users user = userOptional.get();

                // Update user fields
                user.setFullname(updatedUser.getFullname() != null ? updatedUser.getFullname() : user.getFullname());
                user.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : user.getEmail());
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    user.setPassword(encoder.encode(updatedUser.getPassword()));
                }
                user.setRoles(updatedUser.getRoles() != null ? updatedUser.getRoles() : user.getRoles());
                user.setAvatar(updatedUser.getAvatar() != null ? updatedUser.getAvatar() : user.getAvatar());

                service.updateUser(user);
                return ResponseEntity.ok("User information updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not provided.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Users> userOptional = service.findByEmail(authRequest.getEmail());
            if (userOptional.isPresent()) {
                Users user = userOptional.get();
                if (user.isEnabled()) {
                    if (encoder.matches(authRequest.getPassword(), user.getPassword())) {
                        String token = jwtService.generateToken(authRequest.getEmail(),
                                user.getUserId(),
                                user.getFullname(),
                                user.getRoles());
                        response.put("token", token);
                        return ResponseEntity.ok(response);
                    } else {
                        response.put("message", "Mật khẩu không chính xác!");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                    }
                } else {
                    response.put("message", "Người dùng chưa được kích hoạt");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else {
                response.put("message", "Không tìm thấy người dùng");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi");
            response.put("error", e.getMessage()); // Thêm thông báo lỗi chi tiết
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/show/info")
    public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Optional<Users> user = service.findByToken(token);
            if (user.isPresent()) {
                Users userInfo = user.get();
                UserResponse response = new UserResponse(
                        userInfo.getUserId(),
                        userInfo.getFullname(),
                        userInfo.getEmail(),
                        userInfo.getRoles(),
                        userInfo.getAvatar(),
                        userInfo.getCreatedAt());
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> emailMap) {
        String email = emailMap.get("email");
        Optional<Users> userOptional = service.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            String newPassword = service.generateRandomPassword(10);
            user.setPassword(service.encodePassword(newPassword));
            service.updateUser(user);

            try {
                emailService.sendOrderConfirmationEmail(email, "Reset Password",
                        "Your new password is: " + newPassword);
                return ResponseEntity.ok("A new password has been sent to your email.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }
    }
}
