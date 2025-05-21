package utils;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {

  //generate a BCrypt hash of the given plaintext password so now we can adding hashing feature to our system 
  public static String hashPassword(String plain) {
    //Look Abram it is 12 log rounds => work factor so it is 2^12 almost equal 4,096 iterations and that shows our interset to security
    return BCrypt.hashpw(plain, BCrypt.gensalt(12));
  }

  //verify a plaintext password against a stored BCrypt hash so this is simply boolean function so we do comparsion
  public static boolean verifyPassword(String plain, String hashed) {
    return BCrypt.checkpw(plain, hashed);
  }
}

