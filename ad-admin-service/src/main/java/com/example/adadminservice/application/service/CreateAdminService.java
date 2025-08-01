package com.example.adadminservice.application.service;

import com.example.adadminservice.application.in.dto.CreateAdminRequest;
import com.example.adadminservice.application.in.dto.CreateAdminResponse;
import com.example.adadminservice.application.in.CreateAdminUseCase;
import com.example.adadminservice.common.RandomPasswordGenerator;
import com.example.adadminservice.domain.repository.AdminRepository;
import com.example.adadminservice.application.out.mail.MailSender;
import com.example.adadminservice.domain.model.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAdminService implements CreateAdminUseCase {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSender emailSender;
    private final RandomPasswordGenerator randomPasswordGenerator;

    @Override
    @Transactional
    public CreateAdminResponse create(CreateAdminRequest request) {
        if(adminRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        String randomPassword = randomPasswordGenerator.generate(12);
        String encodedPassword = passwordEncoder.encode(randomPassword);

        Admin admin = Admin.create(
                request.email(),
                encodedPassword,
                request.firstName(),
                request.lastName(),
                request.department()
        );

        Admin saved = adminRepository.save(admin);

        emailSender.send(request.email(), "임시 비밀번호 안내입니다.", randomPassword);

        return new CreateAdminResponse(saved.getId(), saved.getEmail());
    }
}
