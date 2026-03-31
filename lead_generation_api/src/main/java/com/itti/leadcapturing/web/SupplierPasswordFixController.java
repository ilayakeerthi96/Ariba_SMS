package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.SupplierUser;
import com.itti.leadcapturing.repo.SupplierUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ✅ ONE-TIME USE: Fix supplier users whose passwords are stored as plain text.
 *
 * HOW TO USE:
 *   Step 1 — Check which users have plain text passwords:
 *     GET /api/supplier-password-fix/check
 *
 *   Step 2 — Fix a specific user by resetting their password:
 *     POST /api/supplier-password-fix/reset/{userId}
 *     Body: { "newPassword": "their_new_password" }
 *
 *   Step 3 — Fix ALL plain text passwords at once (bulk):
 *     POST /api/supplier-password-fix/fix-all
 *     Body: { "defaultPassword": "Supplier@123" }
 *     (Sets all plain-text-password users to this default; they must change it after login)
 */
@RestController
@RequestMapping("/api/supplier-password-fix")
@CrossOrigin(origins = "*")
@Slf4j
public class SupplierPasswordFixController {

    @Autowired
    private SupplierUserRepository supplierUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * GET /api/supplier-password-fix/check
     * Shows which supplier users have plain text (non-BCrypt) passwords.
     */
    @GetMapping("/check")
    @Transactional(readOnly = true)
    public ResponseEntity<?> checkPasswords() {
        List<SupplierUser> all = supplierUserRepository.findByIsDeletedFalse();

        List<Map<String, Object>> plainText  = new ArrayList<>();
        List<Map<String, Object>> bcrypt     = new ArrayList<>();

        for (SupplierUser u : all) {
            boolean isBcrypt = u.getPassword() != null
                    && u.getPassword().startsWith("$2");  // BCrypt hashes start with $2a$ or $2b$

            Map<String, Object> info = new HashMap<>();
            info.put("userId", u.getId());
            info.put("email",  u.getEmail());
            info.put("passwordPreview", u.getPassword() != null
                    ? u.getPassword().substring(0, Math.min(10, u.getPassword().length())) + "..."
                    : "NULL");

            if (isBcrypt) bcrypt.add(info);
            else          plainText.add(info);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers",        all.size());
        result.put("bcryptCount",       bcrypt.size());
        result.put("plainTextCount",    plainText.size());
        result.put("plainTextUsers",    plainText);
        result.put("message", plainText.isEmpty()
                ? "✅ All passwords are BCrypt encoded. No fix needed."
                : "⚠️ " + plainText.size() + " user(s) have plain text passwords. Use /reset/{userId} or /fix-all to fix.");

        log.info("Password check: {} BCrypt, {} plain text", bcrypt.size(), plainText.size());
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/supplier-password-fix/reset/{userId}
     * Reset a specific user's password and store it as BCrypt.
     *
     * Body: { "newPassword": "their_new_password" }
     */
    @PostMapping("/reset/{userId}")
    @Transactional
    public ResponseEntity<?> resetPassword(@PathVariable Long userId,
                                           @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "newPassword is required in request body"
            ));
        }

        SupplierUser user = supplierUserRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Supplier user not found with ID: " + userId
            ));
        }

        String encoded = passwordEncoder.encode(newPassword.trim());
        user.setPassword(encoded);
        supplierUserRepository.save(user);

        log.info("✅ Password reset for supplier user: {} (ID: {})", user.getEmail(), userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password reset successfully for " + user.getEmail(),
                "userId",  userId,
                "email",   user.getEmail()
        ));
    }

    /**
     * POST /api/supplier-password-fix/fix-all
     * Fix ALL supplier users whose passwords are plain text.
     * Sets them to a default password — users must change after login.
     *
     * Body: { "defaultPassword": "Supplier@123" }
     */
    @PostMapping("/fix-all")
    @Transactional
    public ResponseEntity<?> fixAll(@RequestBody Map<String, String> body) {
        String defaultPassword = body.get("defaultPassword");

        if (defaultPassword == null || defaultPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "defaultPassword is required in request body"
            ));
        }

        List<SupplierUser> all     = supplierUserRepository.findByIsDeletedFalse();
        List<String>       fixed   = new ArrayList<>();
        List<String>       skipped = new ArrayList<>();

        for (SupplierUser u : all) {
            boolean isBcrypt = u.getPassword() != null && u.getPassword().startsWith("$2");
            if (!isBcrypt) {
                u.setPassword(passwordEncoder.encode(defaultPassword.trim()));
                supplierUserRepository.save(u);
                fixed.add(u.getEmail());
                log.info("  ✅ Fixed password for: {}", u.getEmail());
            } else {
                skipped.add(u.getEmail());
            }
        }

        log.info("Password fix-all complete: {} fixed, {} skipped", fixed.size(), skipped.size());

        return ResponseEntity.ok(Map.of(
                "success",         true,
                "fixedCount",      fixed.size(),
                "skippedCount",    skipped.size(),
                "fixedUsers",      fixed,
                "message",         fixed.isEmpty()
                        ? "✅ No plain text passwords found. Nothing to fix."
                        : "✅ Fixed " + fixed.size() + " user(s). Default password set to: " + defaultPassword
        ));
    }
}