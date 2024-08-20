import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {
        try {

            deleteAllUsers();
            addUser("admin", "admin", "Password3*");
            addUser("user1", "user1", "Password1*");
            addUser("user2", "user2", "Password2*");

            updateUserPassword("admin","Password3*", "admin", "Password2*");
            //updateUserPassword("admin","Password3*", "admin", "Password2*");
            deleteUser("admin", "Password2*", "user2");
            updateUser("admin", "Password2*", "user1", "pepa", "user1", null);
            //updateUserPassword("user1","Password1*", "user1", "Password2*");
            //getAllUsers();

            //getUserTest();
            //getUserWithParameterTest();

            //updateUserPassword("admin","admin","user1", "user1");
            //deleteUserTest();
            //getAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(String authorizationUsername, String authorizationPassword, String username, String newName, String newUsername,  String newPassword) {
        try {
            String auth = authorizationUsername + ":" + authorizationPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users?username=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            String jsonInputString;
            if(newPassword != null) {
                jsonInputString = "{\"name\":\"" + newName + "\",\"username\":\"" + newUsername + "\",\"password\":\"" + newPassword + "\"}";
            } else {
                jsonInputString = "{\"name\":\"" + newName + "\",\"username\":\"" + newUsername + "\"}";
            }
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            getReponseAndPrint(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserPassword(String authorizationUsername, String authorizationPassword, String username ,String newPassword) throws Exception {
        try {
            String auth = authorizationUsername + ":" + authorizationPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users/password?username=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            String jsonInputString = "{\"password\":\"" + newPassword + "\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            getReponseAndPrint(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getAllUsers() throws Exception {
        URL url = new URL("http://localhost:8080/api/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        getReponseAndPrint(connection);
    }

    public static void getUser() throws Exception {
        URL url = new URL("http://localhost:8080/api/users/40");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        getReponseAndPrint(connection);
    }

    public static void getUserWithParameter() throws Exception {
        URL url = new URL("http://localhost:8080/api/users?id=40");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        getReponseAndPrint(connection);
    }

    public static void addUser(String name, String username, String password) throws Exception {
        URL url = new URL("http://localhost:8080/api/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        String jsonInputString = "{\"name\":\""+ name + "\",\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        getReponseAndPrint(connection);
    }

    public static void deleteUser(String authorizationUsername, String authorizationPassword, String username) throws Exception {
        try {
            String auth = authorizationUsername + ":" + authorizationPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users/delete?username=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", authHeader);
            getReponseAndPrint(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllUsers() throws Exception {
        try {

            URL url = new URL("http://localhost:8080/api/users/deleteall");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            getReponseAndPrint(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void getReponseAndPrint(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the response
        System.out.println(response.toString());
    }

}