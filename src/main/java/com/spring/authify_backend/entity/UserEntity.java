package com.spring.authify_backend.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb1_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String userId;
	private String name;
	
	@Column(unique = true)
	private String email;
	private String password;
	private String verifyOtp;
	private Boolean isAccountVerified;
	private Long verifyOtpExpireAt;
	private String resetOtp;
	private Long resetOtpExpireAt;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Timestamp created_At;
	
	@UpdateTimestamp
	private Timestamp updated_At;
	
}