package com.example.smartalert;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FCMTopicMessageSender{

    private static final String FCM_SERVER_KEY = "AAAAbX9W_Og:APA91bEKd9ihy7mDo3NzHP_vPsMsHL4fsmeQse_bg4xtj4619Cagbf-ROCQz89QzsHh-9oJaeW6AlJFHrFlF0amzZNlWuiT5LUlIwR9n5WQW_cQnoWefNZXu1vTXmkPKbPXOwdDw3tJv";
    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    static String body;
    static String title;

    public static void sendTopicMessage(String topic,/* String title, String body, */String incidentType,
                                        String timestamp, String location, List<String> userTokens) {


        new SendMessageTask().execute(topic,/* title, body,*/ incidentType, timestamp, location, userTokens);
    }

    private static class SendMessageTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            String topic = (String) params[0];

            String incident  = (String) params[1];
            String timestamp = (String) params[2];
            String location  = (String) params[3];

            List<String> usersTokens = (List<String>) params[4];



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

                //String icon = "alert"; // Replace with your icon resource name
            //String importance = "high"; // Adjust as needed

            //String sound = "sound2"; // Replace with your custom sound file name
            //String instructions = "Please stay home and safe. Reduce your movements";
            //String body1 = "Location: "+ location + "\nTimestamp: " + timestamp + "\nInstructions: " + instructions;



            try {

                // Create connection to FCM API URL
                URL url = new URL(FCM_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + FCM_SERVER_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON payload for the message
                String payload = String.format("{\"to\": \"/topics/%s\"," +
                                "\"notification\": {" +
                                "\"title\": \"%s\"," +
                                "\"body\": \"%s\"," +
                                "\"icon\": \"ambulance_lights\"" + // Assuming this is a custom icon name
                                "}}",
                        topic, title, body);

                //String registrationToken= "e5KAhq4gSf2_2CY4OZ7mUK:APA91bEKPhFGeAuY6eG9ZMtWhoR9QY_nTNmhJQRZlx-5XHZoYp4o8RfrSS7lVHnNwY_FVGps5rX86zXIx1QmOdQIAhOkfeaomJFpv6HDCQQ0WUucLmJBcqgMt4nZWr8SQ5WmaKiv8azL";
                //String registrationToken2= "eiPVsJeAQs6DDJcMxXcx5d:APA91bFk3qFnD68lMnuIXy44djlLb_a66XTRj6qbmqcAtY-o9GMRoIYt-6nY8K6JuNvDitqOdUXHKFY4dcGWbq4T6fmb2IvxpYiPLf0PQvB6yAZlguJdKDfJqsyBKWgIqOibklBNRp_Q";

                // List of registration tokens
                //List<String> registrationTokens = Arrays.asList(registrationToken, registrationToken2);



                /*String payload1 = String.format("{\"to\": \"%s\"," +
                                "\"notification\": {" +
                                "\"title\": \"%s\"," +
                                "\"body\": \"%s\"," +
                                "\"icon\": \"ambulance_lights\"" + // Assuming this is a custom icon name
                                "}}",
                        registrationToken, title, body);*/

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




                /*String payload = String.format(
                        "{\"to\": \"/topics/%s\", " +
                                "\"data\": {" +
                                "\"location\": \"%s\", " +
                                "\"timestamp\": \"%s\", " +
                                "\"instructions\": \"%s\"" +
                                "}, " +
                                "\"notification\": {" +
                                "\"title\": \"%s\", " +
                                "\"body\": \"%s\", " +
                               // "\"sound\": \"%s\"" + // Specify the custom sound here
                                "\"icon\": \"%s\"" +
                                "}, " +
                                "\"android_importance\": \"%s\"" +
                                "}",
                      topic, location, timestamp, instructions, title, body, icon, importance);*/


                // Send the message
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(payload2);
                writer.flush();

                // Check the response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Message sent successfully
                    Log.d("FCMTopicMessageSender", "FCM message sent successfully to topic: " + topic);
                } else {
                    // Error sending message
                    Log.e("FCMTopicMessageSender", "Failed to send FCM message to topic: " + topic);
                }

                // Close the connection
                conn.disconnect();
            } catch (Exception e) {
                // Handle any exceptions
                Log.e("FCMTopicMessageSender", "Exception while sending FCM message: " + e.getMessage());
            }

            /*try {
                // Construct request URL
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "key=YOUR_SERVER_KEY");
                conn.setDoOutput(true);

                // Construct JSON payload
                StringBuilder jsonPayload = new StringBuilder();
                jsonPayload.append("{");
                jsonPayload.append("\"registration_ids\": [");
                List<String> tokens = new ArrayList<>();
                tokens.add("e5KAhq4gSf2_2CY4OZ7mUK:APA91bEKPhFGeAuY6eG9ZMtWhoR9QY_nTNmhJQRZlx-5XHZoYp4o8RfrSS7lVHnNwY_FVGps5rX86zXIx1QmOdQIAhOkfeaomJFpv6HDCQQ0WUucLmJBcqgMt4nZWr8SQ5WmaKiv8azL");
                for (String token : tokens) {
                    jsonPayload.append("\"").append(token).append("\",");
                }
                jsonPayload.deleteCharAt(jsonPayload.length() - 1); // Remove the last comma
                jsonPayload.append("],");
                jsonPayload.append("\"notification\": {");
                jsonPayload.append("\"title\": \"").append(title).append("\",");
                jsonPayload.append("\"body\": \"").append(body).append("\"");
                jsonPayload.append("}");
                jsonPayload.append("}");

                // Send request
                OutputStream os = conn.getOutputStream();
                os.write(jsonPayload.toString().getBytes());
                os.flush();
                os.close();

                // Handle response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Notification sent successfully.");
                } else {
                    System.out.println("Failed to send notification. Response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            return null;
        }
    }
}