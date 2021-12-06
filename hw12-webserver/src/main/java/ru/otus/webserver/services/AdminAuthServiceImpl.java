package ru.otus.webserver.services;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import static ru.otus.webserver.WebServerHomeWork.*;
import static ru.otus.webserver.security.GenPassword.hashPassword;

public class AdminAuthServiceImpl implements UserAuthService {
    private final String adminLogin;
    private final String key;
    private final String salt;


    public AdminAuthServiceImpl() {
        this.adminLogin = getLogin();
        this.key = getKey();
        this.salt = getSalt();
    }

    private String getLogin() {
        String loginAdmin;
        FileInputStream fileInputStream;
        // инициализируем объект Properties
        Properties prop = new Properties();
        try { // обращаемся к файлу и получаем данные
            fileInputStream = new FileInputStream(HASH_LOGIN_SERVICE_ADMIN_LOGIN);
            prop.load(fileInputStream);
            loginAdmin = prop.getProperty("login");
        } catch (IOException e) {
            System.out.println("Ошибка в программе: файла " + HASH_LOGIN_SERVICE_ADMIN_LOGIN + " не обнаружено");
            throw new RuntimeException(e);
        }
        return loginAdmin;
    }

    private String getKey() {
        File file = new File(HASH_LOGIN_SERVICE_ADMIN_PASS);
        return readFile(file);
    }

    private String getSalt() {
        File file = new File(HASH_LOGIN_SERVICE_SALT);
        return readFile(file);
    }

    private String readFile (File file){
        String strKey = new String();
        try (var bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                strKey = strKey + line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strKey;
    }

    @Override
    public boolean authenticate(String login, String password)  {
        //проверим совпадение "хешей"
        if (login !=null && login.equals(adminLogin)){
            try {
                String optEncrypted = hashPassword(password, salt);
                if (optEncrypted == null) return false;
                if (optEncrypted.equals(key)) {
                    return true;
                }
                return  false;
            }catch (NoSuchAlgorithmException ex){
                throw new RuntimeException(ex);
            }
        } else {
            return false;
        }
    }
}
