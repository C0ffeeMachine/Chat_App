package com.example.ChatApp2;

import com.example.ChatApp2.User;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;


public class Server {

    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<MessagingThread> clients = new ArrayList<>();


    public static void main(String args[]) throws Exception {

        ServerSocket server = new ServerSocket(6000,10);
        System.out.println("Waiting for Connections!!");
        try{
            DbOperations.createUsersTable("user");
            DbOperations.createChatTable("chat_backup");
        }catch (Exception e){

        }

        while(true){
            Socket clientSocket = server.accept();
            System.out.println("Client is connected");

            MessagingThread thread = new MessagingThread(clientSocket);
            clients.add(thread);
            thread.start();
        }
    }

     static class MessagingThread extends Thread{

        String user="";
        BufferedReader in;
        PrintWriter out;

        public MessagingThread(Socket clientSocket) throws Exception {

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(),true);

            user=in.readLine();
            users.add(user);
            DbOperations.addUserInDB(user);
        }

        public static void sendToAll(String user, String message){
            for (MessagingThread c:clients){
                if(!c.getUser().equals(user))
                    c.sendMessage(user, message);
                else
                    c.sendToMe(user, message);
            }
        }

        public void sendMessage(String chatUser, String msg){
            out.println(chatUser+": "+msg);
        }

        public void sendToMe(String user, String message){
            out.println("You: "+message);
        }

        public String getUser(){
            return user;
        }

         public void saveInDB(String chatUser, String msg) throws SQLException {
             String msg_id = chatUser + "_" + System.currentTimeMillis();
             DbOperations.chatBackUp(user, msg_id, msg);
         }

        @Override
        public void run(){
            String line;
            try {
                while (true){
                    line=in.readLine();
                    if(line.equals("exit")){
                        clients.remove(this);
                        users.remove(user);
                        break;
                    }else {
                        sendToAll(user,line);
                        saveInDB(user, line);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}