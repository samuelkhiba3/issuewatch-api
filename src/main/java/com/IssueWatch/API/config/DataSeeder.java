package com.IssueWatch.API.config;

import com.IssueWatch.API.entities.Role;
import com.IssueWatch.API.enums.RoleName;
import com.IssueWatch.API.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        seedRole(RoleName.USER);
        seedRole(RoleName.SUPPORT);
        seedRole(RoleName.ADMIN);
    }

    private void seedRole(RoleName roleName) {
        boolean roleExists = roleRepository.findByName(roleName).isPresent();

        if (!roleExists) {
            roleRepository.save(new Role(roleName));
        }
    }
}