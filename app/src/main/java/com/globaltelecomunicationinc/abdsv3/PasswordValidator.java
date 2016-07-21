package com.globaltelecomunicationinc.abdsv3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kyle St-Hill on 7/20/2016.
 */
public class PasswordValidator {
    private static PasswordValidator INSTANCE = new PasswordValidator();
    private static String pattern = null;

    /**
     * No one can make a direct instance
     * */
    private PasswordValidator()
    {
        //do nothing
    }

    /**
     * Force the user to build a validator using this way only
     * */
    public static PasswordValidator buildValidator( boolean forceSpecialChar,
                                                    boolean forceCapitalLetter,
                                                    boolean forceNumber,
                                                    int minLength,
                                                    int maxLength)
    {
        StringBuilder patternBuilder = new StringBuilder("((?=.*[a-z])");

        if (forceSpecialChar)
        {
            patternBuilder.append("(?=.*[@#$%])");
        }

        if (forceCapitalLetter)
        {
            patternBuilder.append("(?=.*[A-Z])");
        }

        if (forceNumber)
        {
            patternBuilder.append("(?=.*d)");
        }

        patternBuilder.append(".{" + minLength + "," + maxLength + "})");
        pattern = patternBuilder.toString();

        return INSTANCE;
    }

    /**
     * Here we will validate the password
     * */
    public static boolean validatePassword(final String password)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
