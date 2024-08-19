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

            addUserTest();
            //getUserTest();
            //getUserWithParameterTest();
            getAllUsersTest();
            //updateUserPassword();
            //deleteUserTest();
            getAllUsersTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateYourselfPasswordAsUser() throws Exception {
        try {
            String username = "tomas";
            String oldPassword = "tomasek";
            String newPassword = "tomasek";
            String auth = username + ":" + oldPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users/46/password");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            String jsonInputString = newPassword;
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            getReponseAndPrint(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserPasswordAsAdmin() throws Exception {
        try {
            String username = "moj";
            String oldPassword = "hes";
            String newPassword = "kol";
            String auth = username + ":" + oldPassword;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users/42/password");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            String jsonInputString = newPassword;
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            getReponseAndPrint(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAllUsersTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        getReponseAndPrint(connection);
    }

    public static void getUserTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/users/40");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        getReponseAndPrint(connection);
    }

    public static void getUserWithParameterTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/users?id=40");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        getReponseAndPrint(connection);
    }

    public static void addUserTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        String jsonInputString = "{\"name\":\"Tomas\",\"userName\":\"tom\",\"password\":\"tomasek\"}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        getReponseAndPrint(connection);
    }

    public static void deleteUserTest() throws Exception {
        try {

            String username = "moj";
            String password = "hes";
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;

            URL url = new URL("http://localhost:8080/api/users/15");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", authHeader);
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