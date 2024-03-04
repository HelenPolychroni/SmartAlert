package com.example.smartalert;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FCMRegTokenMessageSender {

    private static final String FCM_SERVER_KEY = "AAAAbX9W_Og:APA91bEKd9ihy7mDo3NzHP_vPsMsHL4fsmeQse_bg4xtj4619Cagbf-ROCQz89QzsHh-9oJaeW6AlJFHrFlF0amzZNlWuiT5LUlIwR9n5WQW_cQnoWefNZXu1vTXmkPKbPXOwdDw3tJv";
    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    static String body;
    static String title;

    public static void sendMessage(String incidentType,String timestamp, String location, List<String> userTokens) {


        new SendMessageTask().execute(incidentType, timestamp, location, userTokens);
    }

    private static class SendMessageTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {

            String incident  = (String) params[0];
            String timestamp = (String) params[1];
            String location  = (String) params[2];

            List<String> usersTokens = (List<String>) params[3];
            System.out.println("List with user tokens: " + usersTokens);

            String instructions;

            switch (incident) {
                case "Fire":
                    title = "Smart Alert: Fire Hazard!";
                    instructions = "Please evacuate the area and follow safety instructions" +
                                " from authorities."; // Use a face mask and protective clothing if necessary.";
                    body = "Civil Protection Greece " + timestamp + ".\nFire incident near your area." +
                                "\nLocation: " + location + "\n" + instructions +
                    // here comes the greek part
                    "\nΓενική Γραμματεία Πολιτικής Προστασίας " + timestamp +
                            ".\nΠεριστατικό φωτιάς κοντά στην περιοχή σας." +
                            "\nΤοποθεσία: " + location +
                            "\nΠαρακαλούμε να απομακρυνθείτε από την περιοχή και να ακολουθήσετε τις οδηγίες των αρχών ασφαλείας.";
                         //   + "Χρησιμοποιήστε μάσκα για το πρόσωπο και προστατευτικά ρούχα εάν είναι απαραίτητο.";
                    break;

                case "Flood":
                    title = "Smart Alert: Flood Warning!";
                    instructions = "Please stay in a high place and avoid areas at risk of flooding.";
                            //" Do not attempt to cross streams or flooded roads.";
                    body = "Civil Protection Greece " + timestamp + ".\nFlood incident near your area." +
                            "\nLocation: " + location + "\n" + instructions +
                            "\nΓενική Γραμματεία Πολιτικής Προστασίας " + timestamp + ".\nΠεριστατικό πλημμύρας κοντά στην περιοχή σας." +
                            "\nΤοποθεσία: " + location + "\nΠαρακαλούμε να μείνετε σε υψηλό σημείο και να αποφύγετε τις περιοχές με υψηλό κίνδυνο πλημμύρας.";
                            //" Μην προσπαθήσετε να διασχίσετε χειμάρρους ή πλημμυρισμένους δρόμους.";
                    break;


                case "Earthquake":
                    title = "Smart Alert: Earthquake Alert!";
                    instructions = "Remain calm and take cover under stable shelter.";
                    //+"Move away from windows and hazards of falling debris.";
                    body = "Civil Protection Greece " + timestamp + ".\nEarthquake incident near your area." +
                            "\nLocation: " + location + "\n" + instructions +
                            "\nΓενική Γραμματεία Πολιτικής Προστασίας " + timestamp + ".\nΠεριστατικό σεισμού κοντά στην περιοχή σας." +
                            "\nΤοποθεσία: " + location + "\nΠαραμείνετε ήρεμοι και προστατευτείτε κάτω από σταθερό κάλυμμα.";
                            //" Απομακρυνθείτε από τα παράθυρα και τους κινδύνους πτώσης.";
                    break;
            }


            try {
                // Create connection to FCM API URL
                URL url = new URL(FCM_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + FCM_SERVER_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                StringBuilder tokenList = new StringBuilder();
                for (String token : usersTokens) {
                    tokenList.append("\"").append(token).append("\",");
                }
                // Remove the trailing comma
                tokenList.deleteCharAt(tokenList.length() - 1);

                String payload2 =  String.format("{\"registration_ids\": [%s]," +
                                "\"notification\": {" +
                                "\"title\": \"%s\"," +
                                "\"body\": \"%s\"" +
                                "}}",
                        tokenList.toString(), title, body);


                // Send the message
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(payload2);
                writer.flush();

                // Check the response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Message sent successfully
                    Log.d("FCMRegTokenMessageSender", "FCM message sent successfully to devices reg tokens");
                } else {
                    // Error sending message
                    Log.e("FCMRegTokenMessageSender", "Failed to send FCM message to devices reg tokens");
                }

                // Close the connection
                conn.disconnect();
            } catch (Exception e) {
                // Handle any exceptions
                Log.e("FCMRegTokenMessageSender", "Exception while sending FCM message: " + e.getMessage());
            }

            return null;
        }
    }
}