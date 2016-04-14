package com.example.Joey.myapplication.backend;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
    static Logger Log = Logger.getLogger("com.example.Joey.myapplication.backend.MyServlet");

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Log.info("Got cron message, constructing email.");

        //Create a new Firebase instance and subscribe on child events.
        Firebase firebase = new Firebase("https://burning-inferno-4491.firebaseio.com/todoItems");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Build the email message contents using every field from Firebase.
                final StringBuilder newItemMessage = new StringBuilder();
                newItemMessage.append("Good Morning!  You have the following todo items:\n");
                for (DataSnapshot todoItem : dataSnapshot.getChildren()) {
                    for (DataSnapshot field : todoItem.getChildren()) {
                        newItemMessage.append(field.getKey())
                                .append(":")
                                .append(field.getValue().toString())
                                .append("\n");
                    }
                }

                //Now Send the email
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                try {
                    Message msg = new MimeMessage(session);
                    //Make sure you substitute your project-id in the email From field
                    msg.setFrom(new InternetAddress("jmorit@roc-dev.com",
                            "Todo Nagger"));
                    msg.addRecipient(Message.RecipientType.TO,
                            new InternetAddress("jmmt03@gmail.com", "Recipient"));
                    msg.setSubject("Good Morning!");
//                    msg.setText(newItemMessage.toString());
                    msg.setText("Testemailtje");
                    Transport.send(msg);
                } catch (MessagingException | UnsupportedEncodingException e) {
                    Log.warning(e.getMessage());
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
