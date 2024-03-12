/*
Name: <Daniel Rojas>
Course: CNT 4714 Spring 2024
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 10, 2024
Class: <Enterprise Computing>
*/

import javax.swing.SwingUtilities;

public class Main{
      public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DB_GUI().setVisible(true));
}
}
  