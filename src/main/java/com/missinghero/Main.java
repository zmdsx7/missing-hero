package com.missinghero;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public final class Main {
    private Main() { }
    public static void main(String[] args) throws Exception {
        // Headless preview generation for regression checks and gallery images.
        if(args.length>=2 && args[0].equals("--screenshot")) {
            System.setProperty("java.awt.headless", "true");
            GamePanel panel=new GamePanel();
            if(args.length>=3) {
                panel.state.start(); panel.state.dismissDialog();
                if(args[2].equals("street")) {panel.state.scene=GameState.Scene.STREET; panel.state.x=14;panel.state.y=7;}
                if(args[2].equals("end")) {panel.state.scene=GameState.Scene.TRUE_END; panel.state.endingExplanation="ภาพถ่าย + หน้าต่าง + รอยเท้า + ป้าย MAX เชื่อมโยงกันได้: Hero ออกไปหาเพื่อนด้วยความสมัครใจ";}
            }
            panel.setSize(GamePanel.WIDTH,GamePanel.HEIGHT);
            BufferedImage img=new BufferedImage(GamePanel.WIDTH,GamePanel.HEIGHT,BufferedImage.TYPE_INT_RGB);
            Graphics2D g=img.createGraphics(); panel.paint(g); g.dispose();
            ImageIO.write(img,"png",new File(args[1]));
            panel.stop(); return;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame=new JFrame("Missing Hero | A Pixel Mystery");
            GamePanel game=new GamePanel();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}