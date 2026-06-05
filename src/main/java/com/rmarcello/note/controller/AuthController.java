package com.rmarcello.note.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.rmarcello.note.dto.JwtResponse;
import com.rmarcello.note.dto.LoginRequest;
import com.rmarcello.note.security.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

            // Log successful login
            LOGGER.info("User {} logged in successfully from IP: {}", 
                       loginRequest.getUsername(), getClientIpAddress(request));

            return ResponseEntity.ok(new JwtResponse(jwt,
                                                   userDetails.getUsername(),
                                                   roles));
        } catch (BadCredentialsException e) {
            // Log failed login attempt
            LOGGER.warn("Failed login attempt for user {} from IP: {}", 
                       loginRequest.getUsername(), getClientIpAddress(request));
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Error: Invalid username or password");
        } catch (Exception e) {
            LOGGER.error("Authentication error for user {}: {}", 
                        loginRequest.getUsername(), e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Authentication failed");
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Log logout event
        LOGGER.info("User {} logged out from IP: {}", username, getClientIpAddress(request));
        
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("User logged out successfully!");
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}