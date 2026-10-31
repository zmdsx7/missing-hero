package com.missinghero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.BreakIterator;
import java.util.*;

/** Swing controller + high-resolution UI over a low-resolution pixel world. */
public final class GamePanel extends JPanel implements KeyListener, MouseListener {
    public static final int WIDTH=1152, HEIGHT=768;
    private static final int SCALE=3;
    public final GameState state=new GameState();
    private final BufferedImage world=new BufferedImage(PixelWorld.W,PixelWorld.H,BufferedImage.TYPE_INT_RGB);
    private final javax.swing.Timer clock;
    private final Set<Integer> pressed=new HashSet<>();
    private long tick=0, lastMove=0;
    private final Font ui=new Font("SansSerif",Font.PLAIN,23);
    private final Font small=new Font("SansSerif",Font.PLAIN,18);
    private final Font normalBold=new Font("SansSerif",Font.BOLD,25);
    private final Font heading=new Font("SansSerif",Font.BOLD,42);
    private final Font title=new Font("SansSerif",Font.BOLD,78);
    private final Font mono=new Font(Font.MONOSPACED,Font.BOLD,20);

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setFocusable(true);setBackground(new Color(21,27,43));
        addKeyListener(this);addMouseListener(this);
        clock=new javax.swing.Timer(33,e->{tick++;driveMovement();repaint();});clock.start();
    }
    public void stop(){clock.stop();}
    private void driveMovement(){
        if(!state.isWorldScene() || state.hasDialog() || state.journal) return;
        long now=System.nanoTime();
        if(now-lastMove<117_000_000L) return;
        int dx=0,dy=0;
        if(pressed.contains(KeyEvent.VK_A)||pressed.contains(KeyEvent.VK_LEFT))dx--;
        if(pressed.contains(KeyEvent.VK_D)||pressed.contains(KeyEvent.VK_RIGHT))dx++;
        if(pressed.contains(KeyEvent.VK_W)||pressed.contains(KeyEvent.VK_UP))dy--;
        if(pressed.contains(KeyEvent.VK_S)||pressed.contains(KeyEvent.VK_DOWN))dy++;
        if(dx!=0 || dy!=0) {state.move(dx,0);if(dx==0)state.move(0,dy);lastMove=now;}
    }
    @Override protected void paintComponent(Graphics gfx){
        super.paintComponent(gfx);
        Graphics2D g=(Graphics2D)gfx.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Graphics2D pixel=world.createGraphics();
        pixel.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        PixelWorld.render(pixel,state,tick);pixel.dispose();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(world,0,0,WIDTH,HEIGHT,null);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if(state.scene==GameState.Scene.TITLE) drawTitle(g);
        else if(state.isWorldScene()) drawWorldUi(g);
        else if(state.scene==GameState.Scene.INTERROGATION) drawInterrogation(g);
        else drawEnding(g);
        if(state.journal && state.isWorldScene()) drawJournal(g);
        if(state.hasDialog()) drawDialog(g);
        g.dispose();
    }
    private static Color col(int color){return new Color(color);}
    private static void fill(Graphics2D g,int x,int y,int w,int h,int color){g.setColor(col(color));g.fillRect(x,y,w,h);}
    private static void rounded(Graphics2D g,int x,int y,int w,int h,int radius,int color){g.setColor(col(color));g.fillRoundRect(x,y,w,h,radius,radius);}
    private static void translucent(Graphics2D g,int x,int y,int w,int h,int rad,int rgba){g.setColor(new Color(rgba,true));g.fillRoundRect(x,y,w,h,rad,rad);}
    private static void outline(Graphics2D g,int x,int y,int w,int h,int r,int color){g.setColor(col(color));g.setStroke(new BasicStroke(2));g.drawRoundRect(x,y,w,h,r,r);}
    private void text(Graphics2D g,String s,int x,int y,Font f,int color){g.setFont(f);g.setColor(col(color));g.drawString(s,x,y);}
    private void button(Graphics2D g,String label,int x,int y,int w,int h,boolean primary){
        translucent(g,x+6,y+7,w,h,12,0x990b1523);
        rounded(g,x,y,w,h,12,primary?0xe3b985:0x45576b);
        outline(g,x,y,w,h,12,primary?0xf9deb4:0x7e9b9e);
        g.setFont(normalBold);FontMetrics fm=g.getFontMetrics();
        text(g,label,x+(w-fm.stringWidth(label))/2,y+(h+fm.getAscent()-fm.getDescent())/2-2,normalBold,primary?0x293143:0xf6ead8);
    }
    private void drawTitle(Graphics2D g){
        translucent(g,62,65,736,630,28,0xd51b2435);
        outline(g,62,65,736,630,28,0x9f8979);
        text(g,"A PIXEL MYSTERY  /  JAVA EDITION",108,123,mono,0xf1c990);
        text(g,"MISSING",99,226,title,0xffe9c9);
        text(g,"HERO",99,307,title,0xe8ae8b);
        fill(g,104,332,569,3,0xae8b7d);
        text(g,"ฮีโร่หายไปไหน ?",107,397,heading,0xffdfbd);
        wrap(g,"สุนัขที่คุณรักหายตัวไปในคืนหนึ่ง สำรวจบ้านและหมู่บ้านมืด ๆ รวบรวมหลักฐาน สอบถามผู้ต้องสงสัยทั้ง 4 และค้นพบความจริงที่อาจไม่เหมือนสิ่งที่คิด",107,446,614,31,3,ui,0xe5e3d5);
        button(g,"เริ่มเกม  ·  ENTER / SPACE",105,562,553,76,true);
        text(g,"WASD  เดิน     SPACE / E  สำรวจ     F  ไฟฉาย     J  สมุดหลักฐาน",104,674,small,0xc7c6c2);
        text(g,"ORIGINAL PIXEL ART",860,702,mono,0xffdfbd);
    }
    private void drawWorldUi(Graphics2D g){
        translucent(g,0,0,WIDTH,98,0,0xf11d293b);
        fill(g,0,95,WIDTH,3,0xcaa67a);
        text(g,"MISSING HERO",28,45,normalBold,0xf9e5c4);
        text(g,state.sceneName(),29,76,mono,0xb6d2cc);
        text(g,"หลักฐาน "+state.collected.size()+" / "+GameState.TOTAL_CLUES,360,55,normalBold,0xf9deae);
        text(g,"สอบถาม "+state.interviewed.size()+" / 4",585,55,normalBold,0xbdd0cd);
        text(g,"F: "+(state.flashlight?"ไฟฉาย ON":"ไฟฉาย OFF"),807,54,small,0xf4d39b);
        buttonTiny(g,"J · NOTES",1009,20,120,53);
        if(!state.hasDialog()) {
            GameState.Spot nearby=state.nearestSpot();
            translucent(g,20,665,WIDTH-40,79,14,0xdd1a293a);
            outline(g,20,665,WIDTH-40,79,14,0x647a7b);
            if(nearby!=null) {
                text(g,"SPACE / E",42,713,normalBold,0xffd7a1);
                text(g,nearby.name(),232,711,normalBold,0xf5eee0);
                text(g,"WASD / ARROWS เดิน    ·    F ไฟฉาย    ·    J หลักฐาน",590,710,small,0xb3c4c2);
            } else {
                text(g,"เดินไปใกล้วัตถุที่มีประกายเพื่อสำรวจ",45,713,normalBold,0xe9dfc8);
                text(g,"WASD / ARROWS เดิน    ·    F ไฟฉาย    ·    J หลักฐาน",602,710,small,0xb3c4c2);
            }
        }
    }
    private void buttonTiny(Graphics2D g,String str,int x,int y,int w,int h){
        rounded(g,x,y,w,h,9,0x465c66);outline(g,x,y,w,h,9,0xa2b5a7);
        text(g,str,x+10,y+33,small,0xf4dec0);
    }
    private void drawDialog(Graphics2D g){
        translucent(g,38,496,WIDTH-76,253,20,0xf5223145);
        outline(g,38,496,WIDTH-76,253,20,0xd7b58e);
        text(g,"✦  "+state.messageTitle,73,549,normalBold,0xffdb9f);
        wrap(g,state.messageBody,74,604,WIDTH-155,34,3,ui,0xf9ebd6);
        text(g,"กด SPACE / E / ENTER เพื่อไปต่อ",76,724,small,0xabc5c5);
    }
    private void drawJournal(Graphics2D g){
        translucent(g,0,96,WIDTH,672,0,0xc914202d);
        rounded(g,90,125,972,545,24,0xe9dac0);
        outline(g,90,125,972,545,24,0xa48769);
        rounded(g,112,149,928,71,13,0x414b59);
        text(g,"สมุดสืบสวน  /  EVIDENCE JOURNAL",136,194,normalBold,0xffe2b4);
        text(g,"[ J / ESC ] ปิด",855,192,small,0xeff0da);
        text(g,"หลักฐานที่ค้นพบ",140,263,normalBold,0x354253);
        int i=0;
        for(var entry:GameState.CLUES.entrySet()) {
            int x=i<5?142:600, y=305+(i%5)*57;
            rounded(g,x-8,y-27,430,46,8,state.collected.contains(entry.getKey())?0xd0e1cd:0xe0d1c0);
            String status=state.collected.contains(entry.getKey())?"✓  ":"○  ";
            text(g,status+entry.getValue(),x,y+3,small,state.collected.contains(entry.getKey())?0x2a5a51:0x89776c);
            i++;
        }
        text(g,"ผู้ต้องสงสัยที่สอบถาม: "+state.interviewed.size()+" / 4",146,637,small,0x394b5b);
    }
    private void drawInterrogation(Graphics2D g){
        translucent(g,64,93,1024,635,20,0xeb202b3d);
        outline(g,64,93,1024,635,20,0xd7b692);
        text(g,"04  /  THE TRUTH",108,145,mono,0xd9ba8f);
        text(g,state.evidenceChoice?"เลือกหลักฐานที่เชื่อมโยงความจริง":"ใครอยู่เบื้องหลังการหายตัวของ Hero?",105,214,heading,0xffdfb3);
        String[] options=state.evidenceChoice ? new String[]{
            "1  ภาพถ่าย + หน้าต่าง + รอยเท้า + ป้าย MAX",
            "2  ใบส่งพัสดุ + เวลาลาดตระเวน รปภ.",
            "3  ชามอาหาร + ภาพวาดเด็กข้างบ้าน"
        } : new String[]{
            "1  เด็กข้างบ้านเป็นคนพา Hero ไป",
            "2  พนักงานส่งของลักพาตัว Hero",
            "3  รปภ. แอบพา Hero ออกจากหมู่บ้าน",
            "4  สุนัข MAX เป็นตัวการที่ลักพา Hero",
            "5  ไม่มีคนร้าย — Hero ออกไปเอง"
        };
        int oy=254;
        for(int i=0;i<options.length;i++) {
            int y=oy+i*76;
            rounded(g,108,y,934,64,9,i==options.length-1&&!state.evidenceChoice?0x527267:0x34465a);
            outline(g,108,y,934,64,9,i==options.length-1&&!state.evidenceChoice?0xb2c6a6:0x607789);
            text(g,options[i],134,y+41,normalBold,0xfaedd6);
        }
        text(g,"กดเลข 1–"+options.length+" เพื่อเลือกคำตอบ     ·     J ดูสมุดหลักฐานไม่ได้ระหว่างสรุปคดี",108,707,small,0xd9c4ac);
    }
    private void drawEnding(Graphics2D g){
        boolean good=state.scene==GameState.Scene.TRUE_END;
        translucent(g,103,153,947,557,23,0xda1c2a3b);
        outline(g,103,153,947,557,23,good?0xf3c993:0xbd8e98);
        text(g,good?"TRUE ENDING  ·  FOUND HERO":"BAD ENDING  ·  LOST FOREVER",154,213,mono,good?0xf1d09a:0xe2a5a9);
        text(g,good?"พบ Hero แล้ว!":"คุณยังไม่พบความจริง",153,299,heading,0xffe8d0);
        wrap(g,state.endingExplanation,154,361,821,34,3,ui,0xe6ddd1);
        wrap(g,good?"คุณตามรอยเท้าไปอีกซอย และพบ Hero กำลังนอนอยู่ข้าง MAX ทั้งคู่ปลอดภัยดี ในที่สุด Hero ก็กลับบ้าน":"การด่วนกล่าวหาโดยไม่เชื่อมโยงหลักฐานทำให้คุณหลงทาง Hero ค่อย ๆ หายไปในความมืดของหมู่บ้าน",154,475,824,34,3,ui,0xc8d6cf);
        button(g,"เล่นอีกครั้ง  ·  R / ENTER",335,606,478,76,true);
    }
    /** Thai-aware line breaking; falls back to character-level wrapping for long fragments. */
    private void wrap(Graphics2D g,String s,int x,int y,int maxWidth,int lineHeight,int maxLines,Font font,int color){
        g.setFont(font);g.setColor(col(color));FontMetrics fm=g.getFontMetrics();
        BreakIterator breaker=BreakIterator.getLineInstance(new Locale("th","TH"));
        breaker.setText(s);
        ArrayList<String> lines=new ArrayList<>();StringBuilder current=new StringBuilder();
        int from=breaker.first();
        for(int to=breaker.next();to!=BreakIterator.DONE;from=to,to=breaker.next()) {
            String token=s.substring(from,to);
            if(fm.stringWidth(current+token)>maxWidth && !current.isEmpty()) {
                lines.add(current.toString().strip());current.setLength(0);
            }
            if(fm.stringWidth(token)>maxWidth) {
                for(int k=0;k<token.length();k++) {
                    if(fm.stringWidth(current.toString()+token.charAt(k))>maxWidth && !current.isEmpty()){
                        lines.add(current.toString());current.setLength(0);
                    }
                    current.append(token.charAt(k));
                }
            } else current.append(token);
        }
        if(!current.isEmpty()) lines.add(current.toString().strip());
        for(int i=0;i<Math.min(maxLines,lines.size());i++) g.drawString(lines.get(i),x,y+i*lineHeight);
    }
    @Override public void keyPressed(KeyEvent ev){
        int k=ev.getKeyCode();pressed.add(k);
        if(k==KeyEvent.VK_ESCAPE) {if(state.journal)state.journal=false;else if(state.hasDialog())state.dismissDialog();repaint();return;}
        if(k==KeyEvent.VK_ENTER || k==KeyEvent.VK_SPACE || k==KeyEvent.VK_E) {
            if(state.scene==GameState.Scene.TITLE) state.start();
            else if(state.isWorldScene()) state.interact();
            else if(state.scene==GameState.Scene.TRUE_END || state.scene==GameState.Scene.BAD_END)state.start();
        }
        if(k==KeyEvent.VK_J && state.isWorldScene() && !state.hasDialog())state.journal=!state.journal;
        if(k==KeyEvent.VK_F && state.isWorldScene())state.flashlight=!state.flashlight;
        if(k==KeyEvent.VK_R && (state.scene==GameState.Scene.TRUE_END || state.scene==GameState.Scene.BAD_END))state.start();
        if(k>=KeyEvent.VK_1 && k<=KeyEvent.VK_5 && state.scene==GameState.Scene.INTERROGATION)state.finalAnswer(k-KeyEvent.VK_0);
        repaint();
    }
    @Override public void keyReleased(KeyEvent e){pressed.remove(e.getKeyCode());}
    @Override public void keyTyped(KeyEvent e){}
    @Override public void mouseClicked(MouseEvent e){
        int mx=e.getX(),my=e.getY();
        if(state.scene==GameState.Scene.TITLE && mx>=105&&mx<=658&&my>=562&&my<=638) state.start();
        else if(state.scene==GameState.Scene.INTERROGATION && mx>=108&&mx<=1042){
            int count=state.evidenceChoice?3:5;
            for(int i=0;i<count;i++)if(my>=254+i*76&&my<=318+i*76) {state.finalAnswer(i+1);break;}
        } else if((state.scene==GameState.Scene.TRUE_END||state.scene==GameState.Scene.BAD_END)&&mx>=335&&mx<=813&&my>=606&&my<=682)state.start();
        else if(state.isWorldScene()) {
            if(state.hasDialog())state.dismissDialog();
            else if(state.journal){state.journal=false;}
            else if(mx>=1009&&my<=80)state.journal=true;
        }
        requestFocusInWindow();repaint();
    }
    @Override public void mousePressed(MouseEvent e){} @Override public void mouseReleased(MouseEvent e){}
    @Override public void mouseEntered(MouseEvent e){} @Override public void mouseExited(MouseEvent e){}
}