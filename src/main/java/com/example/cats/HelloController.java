package com.example.cats;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

public class HelloController {

    public CheckBox csbP2;
    public TextField txIp2;
    public TextField txPort2;
    public Label lbUzenet;
    public Label p1Port;

    public VBox vbCats;
    public Pane pane;

    public ImageView iv2;
    public ImageView iv1;

    public Image[] icon = new Image[5];
    public  Label[] lbCat = new Label[5];

    public DatagramSocket socket = null;

    public int p1i = 1, p1x = 128, p1y = 128;
    public int p2i = 2, p2x = 572, p2y = 572;

    public void initialize() {
        for (int i = 0; i < 5; i++) {
            int ii = i;
            icon[i] = new Image(getClass().getResourceAsStream("cat" + i + ".png"));
            if (i > 0) {
                lbCat[i] = new Label("");
                lbCat[i].setGraphic(new ImageView(icon[i]));
                lbCat[i].setOnMousePressed(e -> selectCat(ii));
                vbCats.getChildren().add(lbCat[i]);
            }
        }
        selectCat(1);

        try {
            socket = new DatagramSocket(678);
            p1Port.setText("Player1 on port " + socket.getLocalPort());
        } catch (SocketException e) { e.printStackTrace(); }
        Thread fogadoSzal = new Thread(new Runnable() {
            @Override
            public void run() {
                fogad();
            }
        });
        fogadoSzal.setDaemon(true);
        fogadoSzal.start();
        Platform.runLater(() -> pane.requestFocus());
    }

    public void selectCat(int id) {
        lbCat[p1i].setStyle("");
        p1i = id;
        lbCat[p1i].setStyle("-fx-background-color: lightgrey;");
        iv1.setImage(icon[p1i]);
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.W && p1y > 80) p1y -= 16;
        if (keyEvent.getCode() == KeyCode.S && p1y < 620) p1y += 16;
        if (keyEvent.getCode() == KeyCode.A && p1x > 80) p1x -= 16;
        if (keyEvent.getCode() == KeyCode.D && p1x < 620) p1x += 16;
        iv1.setLayoutX(p1x - 64);
        iv1.setLayoutY(p1y - 64);
        if (csbP2.isSelected()) {
            kuld(String.format("%d;%d;%d", p1i, p1x, p1y), txIp2.getText(), txPort2.getText());
        }
    }

    public void kuld(String uzenet, String ip, String port) {
        try {
            byte[] data = uzenet.getBytes("utf-8");
            InetAddress ip4 = Inet4Address.getByName(ip);
            int prt = Integer.parseInt(port);
            DatagramPacket packet = new DatagramPacket(data, data.length, ip4, prt);
            socket.send(packet);
            lbUzenet.setText("Elküldve " + uzenet);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void fogad() {
        byte[] data = new byte[256];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while (true) {
            try {
                socket.receive(packet);
                String uzenet = new String(data, 0, packet.getLength(), "utf-8");
                String ip = packet.getAddress().getHostName();
                String port = packet.getPort()+"";
                Platform.runLater(() -> onFogadUzenet(uzenet, ip, port));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onFogadUzenet(String uzenet, String ip, String port) {
        if (csbP2.isSelected() && ip.equals(txIp2.getText())) {
            String[] s = uzenet.split(";");
            p2i = Integer.parseInt(s[0]);
            p2x = Integer.parseInt(s[1]);
            p2y = Integer.parseInt(s[2]);

            iv2.setLayoutX(p2x - 64);
            iv2.setLayoutY(p2y - 64);
            iv2.setImage(icon[p2i]);
        }
    }

    public void onP2Click() {
        if (csbP2.isSelected()) {
            txPort2.setDisable(true);
            txIp2.setDisable(true);
        } else {
            txPort2.setDisable(false);
            txIp2.setDisable(false);
        }
        pane.requestFocus();
    }
}

