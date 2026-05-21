package com.querypulse.backend.service;

import com.querypulse.backend.entity.User;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    List<User> getPendingUsers();

    List<User> getApprovedUsers();

    List<User> getRejectedUsers();

    String approveUser(UUID userId);

    String rejectUser(UUID userId,
                      String reason);
}