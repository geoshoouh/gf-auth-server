package com.gf.server.enumeration;

import org.springframework.lang.NonNull;

public enum UserRole {  
    
    ADMIN,
    TRAINER,
    CLIENT;

    public static UserRole stringToEnum(@NonNull String roleString) {
        
        String roleStringLowerCase = roleString.toLowerCase();

        UserRole retVal;

        switch (roleStringLowerCase) {
        case "admin":
            retVal = UserRole.ADMIN;
            break;
        case "trainer":
            retVal = UserRole.TRAINER;
            break;
        case "client":
            retVal = UserRole.CLIENT;
            break;
        default:
            retVal = UserRole.CLIENT;
        }

        return retVal;
    }
}
