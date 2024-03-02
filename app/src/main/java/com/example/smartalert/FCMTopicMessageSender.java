package com.example.smartalert;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
public class FCMTopicMessageSender{

    private static final String FCM_SERVER_KEY = "AAAAbX9W_Og:APA91bEKd9ihy7mDo3NzHP_vPsMsHL4fsmeQse_bg4xtj4619Cagbf-ROCQz89QzsHh-9oJaeW6AlJFHrFlF0amzZNlWuiT5LUlIwR9n5WQW_cQnoWefNZXu1vTXmkPKbPXOwdDw3tJv";
    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    static String body;
    static String title;

    public static void sendTopicMessage(String topic,/* String title, String body, */String incidentType,
                                        String timestamp, String location) {

        new SendMessageTask().execute(topic,/* title, body,*/ incidentType, timestamp, location);
    }

    private static class SendMessageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String topic = params[0];

            String incident  =  params[1];
            String timestamp =  params[2];
            String location  =  params[3];

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
                writer.write(payload);
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

            return null;
        }
    }
}
