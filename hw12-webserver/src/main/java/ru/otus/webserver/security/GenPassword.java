package ru.otus.webserver.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.Scanner;

import static ru.otus.webserver.WebServerHomeWork.HASH_LOGIN_SERVICE_ADMIN_PASS;
import static ru.otus.webserver.WebServerHomeWork.HASH_LOGIN_SERVICE_SALT;


public class GenPassword {

    private static final SecureRandom RAND = new SecureRandom();
    private static final int ITERATIONS = 1024;
    private static final int KEY_LENGTH = 64;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
   //генерируем hash от пароля админа и SALT - пишем в файлы для дальнейшего использования
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        System.out.println("current dir: " + System.getProperty("user.dir"));
        System.out.println("Generate SALT");
        String salt = generateSalt(64).get();
        System.out.println("SALT = " + salt);
        System.out.println("Enter admin password");
        Scanner in = new Scanner(System.in);
        String pass = in.nextLine();

        File file = new File(HASH_LOGIN_SERVICE_ADMIN_PASS);
        String hashpass = hashPassword(pass,salt);
        writeTextFile(file, hashpass);
        System.out.println("Hash pass = " + hashpass);
        System.out.println("Hashpassword and Salt were created succesfuly");
    }

    public static Optional<String> generateSalt(final int length) throws IOException {

        if (length < 1) {
            System.err.println("error in generateSalt: length must be > 0");
            return Optional.empty();
        }

        byte[] salt = new byte[length];
        RAND.nextBytes(salt);

        if ( Base64.getEncoder().encodeToString(salt) != null) {
            File file = new File(HASH_LOGIN_SERVICE_SALT);
            writeTextFile(file, Base64.getEncoder().encodeToString(salt));

            return Optional.of(Base64.getEncoder().encodeToString(salt));
        }
        return Optional.empty();
    }

    public static String hashPassword (String password, String salt) throws NoSuchAlgorithmException {

        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

        Arrays.fill(chars, Character.MIN_VALUE);

        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] securePassword = fac.generateSecret(spec).getEncoded();

            if (Base64.getEncoder().encodeToString(securePassword) != null) {

                return Base64.getEncoder().encodeToString(securePassword);
            }
            return null;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException  ex) {
            System.err.println("Exception encountered in hashPassword()");

            return null;

        } finally {
            spec.clearPassword();
        }
    }

    private static void writeTextFile(File textFile,/* Optional<String>*/ String value) throws IOException {
        try (var Writer = new FileWriter(textFile)) {
            Writer.write( value);
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл : " + textFile);
            throw new RuntimeException(e);
        }

    }
}
