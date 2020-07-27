package com.example.ChatApp2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client2 extends JFrame implements ActionListener {

    String username;
    PrintWriter pw;
    BufferedReader br;
    JTextArea chatmsg;
    JTextField chatip;
    JButton send,exit;
    Socket chatusers;

    public Client2(String userName, String serverName) throws IOException {
        super(userName);
        this.username=userName;
        chatusers = new Socket(serverName,6000);
        br = new BufferedReader(new InputStreamReader(chatusers.getInputStream()));
        pw = new PrintWriter(chatusers.getOutputStream(),true);
        pw.println(userName);
        buildInterface();
        new MessageThread().start();
    }

    private void buildInterface() {
        send = new JButton("Send");
        exit = new JButton("Exit");
        chatmsg = new JTextArea();
        chatmsg.setRows(30);
        chatmsg.setColumns(50);
        chatmsg.setEditable(false);
        chatip = new JTextField(50);
        JScrollPane sp = new JScrollPane(chatmsg,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp,"Center");
        JPanel bp = new JPanel(new FlowLayout());
        bp.add(chatip);
        bp.add(send);
        bp.add(exit);
        bp.setBackground(Color.LIGHT_GRAY);
        bp.setName("Instant Messenger");
        add(bp,"North");
        send.addActionListener(this);
        exit.addActionListener(this);
        setSize(500,300);
        setVisible(true);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == exit){
            pw.println("exit");
            System.exit(0);
        }else {
            pw.println(chatip.getText());
            chatip.setText(null);
        }
    }

    class MessageThread extends Thread{
        @Override
        public void run() {
            String line;
            try{
                while(true){
                    line = br.readLine();
                    chatmsg.append(line + "\n");
                }
            } catch (IOException e) {
            }
        }
    }

    public static void main(String args[]){
        String userName = JOptionPane.showInputDialog(null,"Please enter your name to begin:","Instant Chat Application",JOptionPane.PLAIN_MESSAGE);
        String serverName = "localhost";
        try{
            new Client(userName, serverName);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
