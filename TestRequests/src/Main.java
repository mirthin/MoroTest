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
            Long admin = 259L;
            Long user1 = 260L;
            Long user2 = 261L;


            deleteAllUsers();
            addUser("admin", "admin", "AdminPass1_");
            addUser("user1", "user1", "AdminPass1_");
            addUser("user2", "user2", "AdminPass1_");



            //should end with error 400 (Password has to be entered)
            addUser("user4", "user4", null);

            updateUserPassword("admin","AdminPass1_", admin, "AdminPass1_");
            updateUserPassword("user1","AdminPass1_", admin, "AdminPass1_");
            updateUserPassword("user1","AdminPass1_", user1, "AdminPass1_");


            //should not have permission
            //updateUserPassword("admin","AdminPass1_ne", user1, "User1pass_new");

            //should not have permission
            updateUserWithoutAuthorization("admin", "AdminPass1_new", user1, "pepa", "user1", null);

            updateUser("admin", "AdminPass1_", user1, "pepa", "user1", null);

            //getUser("user1");

            deleteUser("admin", "AdminPass1_", user2);
            //deleteUser("admin", "AdminPass1_new", "user2");
            //deleteUser("user1", "User1pass_new", "user1");

            getAllUsers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(String authorizationUsername, String authorizationPassword, Long id, String newName, String newUsername,  String newPassword) {
        try {
            String auth = authorizationUsername + ":" + authorizationPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users/" + id);
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

    public static void updateUserPassword(String authorizationUsername, String authorizationPassword, Long id ,String newPassword) throws Exception {
        try {
            String auth = authorizationUsername + ":" + authorizationPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users/password/" + id);
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

    public static void getUser(Long id) throws Exception {
        URL url = new URL("http://localhost:8080/api/users/username/" + id);
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
        String jsonInputString;
        if(password != null) {
            jsonInputString = "{\"name\":\""+ name + "\",\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        } else {
            jsonInputString = "{\"name\":\""+ name + "\",\"username\":\"" + username + "\"}";
        }

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        getReponseAndPrint(connection);
    }

    public static void deleteUser(String authorizationUsername, String authorizationPassword, Long id) throws Exception {
        try {
            String auth = authorizationUsername + ":" + authorizationPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users/delete/" + id);
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

    public static void updateUserWithoutAuthorization(String authorizationUsername, String authorizationPassword, Long id, String newName, String newUsername,  String newPassword) {
        try {

            URL url = new URL("http://localhost:8080/api/users/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
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



    private static void getReponseAndPrint(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in;
        if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

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