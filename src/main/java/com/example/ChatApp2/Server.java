package com.example.ChatApp2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.ChatApp2.User;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

    private static ArrayList<String> users = new ArrayList<>();
    private static ArrayList<MessagingThread> clients = new ArrayList<>();

    public static void main(String args[]) throws IOException {

        ServerSocket server = new ServerSocket(6000,10);
        System.out.println("Waiting for Connections!!");
        while(true){
            Socket clientSocket = server.accept();
            System.out.println("Client is connected");
            MessagingThread thread = new MessagingThread(clientSocket);
            clients.add(thread);
            thread.start();
        }
    }

    static class MessagingThread extends Thread{

        String user="s";
        BufferedReader in;
        PrintWriter out;

        //UserRepository userRepository;


        public MessagingThread(Socket clientSocket) throws IOException {

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(),true);

            user=in.readLine();
            users.add(user);
//            User usr = new User(user,true);
//            userRepository.save(usr);
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

        @Override
        public void run(){
            String line;
            try {
                while (true){
                    line=in.readLine();
                    if(line.equals("exit")){
                        clients.remove(this);
                        users.remove(user);
//                        userRepository.deactivateUserByName(user);
//                        userRepository.deleteDeactivatedUsers();
                        break;
                    }else {
                        sendToAll(user,line);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}