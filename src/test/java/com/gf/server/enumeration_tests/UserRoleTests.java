package com.gf.server.enumeration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.gf.server.enumeration.UserRole;

@SpringBootTest
public class UserRoleTests {
    
    @Test
    void stringToEnumConvertsStringToEnum() {

        String adminString = "aDmIn";
        String trainerString = "TRAINER";
        String clientString = "client";
        String incorrectString = "j98203nf89";

        Assert.isTrue(UserRole.stringToEnum(adminString) == UserRole.ADMIN, "UserRole.stringToEnum failed to convert " + adminString);
        Assert.isTrue(UserRole.stringToEnum(trainerString) == UserRole.TRAINER, "UserRole.stringToEnum failed to convert " + trainerString);
        Assert.isTrue(UserRole.stringToEnum(clientString) == UserRole.CLIENT, "UserRole.stringToEnum failed to convert " + clientString);
        Assert.isTrue(UserRole.stringToEnum(incorrectString) == UserRole.CLIENT, "UserRole.stringToEnum failed to convert " + clientString);
    }
}
