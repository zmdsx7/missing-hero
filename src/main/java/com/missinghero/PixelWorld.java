package com.missinghero;

import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/** Original, procedurally drawn pixel-art backgrounds and character sprites. */
final class PixelWorld {
    static final int W=384, H=256, TOP=32;
    private PixelWorld() {}
    static Color c(int hex) {return new Color(hex);}
    static void r(Graphics2D g,int x,int y,int w,int h,int hex) {g.setColor(c(hex));g.fillRect(x,y,w,h);}
    static void line(Graphics2D g,int x,int y,int x2,int y2,int hex) {g.setColor(c(hex)); g.drawLine(x,y,x2,y2);}
    static void dot(Graphics2D g,int x,int y,int hex) {r(g,x,y,2,2,hex);}
    static void flower(Graphics2D g,int x,int y,int a) {r(g,x,y,2,4,0x376553);r(g,x-2,y-2,6,3,a);r(g,x,y-4,2,7,0xffdba7);}

    static void render(Graphics2D g,GameState state,long ticks) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        switch(state.scene) {
            case TITLE -> title(g,ticks);
            case BEDROOM -> bedroom(g,ticks);
            case DOWNSTAIRS -> downstairs(g,ticks);
            case STREET -> street(g,ticks);
            case INTERROGATION -> board(g,ticks);
            case TRUE_END -> ending(g,true,ticks);
            case BAD_END -> ending(g,false,ticks);
        }
        if(state.isWorldScene()) {
            for(GameState.Spot spot : state.spots()) {
                if(spot.kind()==GameState.Kind.EVIDENCE) {
                    if(!state.collected.contains(spot.id())) {
                        evidenceIcon(g,spot.id(),spot.x()*16+8,TOP+spot.y()*16+8);
                        if((ticks/20)%2==0) {
                            dot(g,spot.x()*16+3,TOP+spot.y()*16+2,0xffd18a);
                            dot(g,spot.x()*16+13,TOP+spot.y()*16+4,0xffd18a);
                        }
                    }
                } else if(spot.kind()==GameState.Kind.CHARACTER) {
                    npc(g,spot.id(),spot.x()*16+8,TOP+spot.y()*16+8,(int)(ticks/20));
                } else if(spot.kind()==GameState.Kind.EXIT || spot.kind()==GameState.Kind.ENDPOINT) {
                    if((ticks/24)%2==0) arrow(g,spot.x()*16+8,TOP+spot.y()*16+1);
                }
            }
            player(g,state.x*16+8,TOP+state.y*16+9,(int)(ticks/8));
            if(state.scene==GameState.Scene.STREET) darkness(g,state.x*16+8,TOP+state.y*16+8,state.flashlight);
        }
    }

    private static void wood(Graphics2D g,int base) {
        r(g,0,TOP,W,H-TOP,base);
        for(int y=TOP;y<H;y+=14) {
            r(g,0,y,W,1,0x523b48);
            int shift=((y-TOP)/14)%2*44;
            for(int x=-shift;x<W;x+=88) r(g,x,y+1,1,13,0x7b5c61);
            for(int x=12;x<W;x+=57) r(g,x,(y+5)%H,10,1,0x856a65);
        }
    }
    private static void wall(Graphics2D g,int main,int edge) {
        r(g,0,TOP,W,58,main);
        for(int x=0;x<W;x+=16) {r(g,x,TOP,1,57,edge);r(g,x+3,TOP+15,10,1,edge);}
        r(g,0,TOP+55,W,4,0x403446);r(g,0,TOP+59,W,2,0xb58a74);
    }
    private static void window(Graphics2D g,int x,int y,int w,int h) {
        r(g,x-3,y-4,w+6,h+8,0x382c49);r(g,x,y,w,h,0x766a82);
        r(g,x+3,y+3,w-6,h-6,0x1e3d63); r(g,x+5,y+5,w-10,h-10,0x254967);
        r(g,x+w/2-1,y,3,h,0xc59378);r(g,x,y+h/2,w,2,0xc59378);
        r(g,x+8,y+6,2,2,0xffdca0);r(g,x+w-10,y+11,2,2,0xcadfff);
        r(g,x-4,y+h+2,w+8,3,0xd5a07e);
    }
    private static void plant(Graphics2D g,int x,int y) {
        r(g,x-5,y-3,11,9,0x5d4552);r(g,x-3,y-1,7,6,0xcb805f);
        r(g,x-2,y-11,5,12,0x315b52);r(g,x-9,y-14,9,10,0x3d7b59);
        r(g,x,y-19,10,11,0x5d9460);r(g,x-6,y-22,11,9,0x4f805c);
        r(g,x-5,y-16,3,2,0x9bae75);
    }
    private static void carpet(Graphics2D g,int x,int y,int w,int h,int inner,int border) {
        r(g,x,y,w,h,border);r(g,x+4,y+4,w-8,h-8,inner);
        for(int i=x+7;i<x+w-7;i+=16) {r(g,i,y+2,3,2,0xe9b98b);r(g,i,y+h-4,3,2,0xe9b98b);}
    }
    private static void bedroom(Graphics2D g,long t) {
        wood(g,0x6b4e55);wall(g,0x6f5769,0x7e6371);
        window(g,127,39,59,36);
        // constellation outside
        carpet(g,104,159,148,62,0x31535b,0xb78c74);
        // bed (left)
        r(g,26,97,98,59,0x2c2a3c);r(g,29,94,93,49,0x956a61);
        r(g,33,98,83,33,0xc8a58e);r(g,36,101,31,23,0xf2d5b8);
        r(g,71,101,42,24,0xf1d2ad);r(g,33,124,84,23,0x889299);
        r(g,39,126,70,17,0x9aa6aa);r(g,26,145,98,8,0x4a3c4b);
        r(g,39,152,7,10,0x3d3340);r(g,107,152,7,10,0x3d3340);
        // chest and bookshelf
        r(g,275,95,88,57,0x3c3043);r(g,281,100,76,51,0xa16b63);
        for(int row=0;row<3;row++) {
            r(g,287,105+row*15,64,3,0x533f4b);
            for(int k=0;k<8;k++) r(g,289+k*7,109+row*15,5,10, new int[]{0x75a2a0,0xd6b17e,0xbd6d70,0x7f9b80}[((row+k)%4)]);
        }
        // desk and portrait
        r(g,194,101,54,35,0x3b3246); r(g,200,107,42,25,0xb77b69);
        r(g,206,113,30,13,0x6f5c6e); r(g,209,115,24,9,0x29475d);
        r(g,201,137,49,7,0x463b43); r(g,205,144,5,17,0x463b43);r(g,237,144,5,17,0x463b43);
        r(g,213,111,16,12,0x4c394b);r(g,215,113,12,8,0xe0aa7e);
        // chair and cup
        r(g,239,161,22,4,0x523b49);r(g,243,155,14,10,0xa47766);
        r(g,236,112,6,7,0xefe3bd);
        // lamps and picture
        r(g,67,43,24,32,0x3a2e47);r(g,71,47,16,24,0xb28d80);r(g,74,53,10,12,0x44545b);
        r(g,12,78,9,24,0x362f42);r(g,9,77,15,6,0xe9ad76);
        r(g,12,84,8,5,0xffdf95);
        // stairs at right
        for(int i=0;i<5;i++) r(g,343,172+i*12,41,11,i%2==0?0x7f6264:0x99716b);
        r(g,336,161,10,90,0x3b3443);
        plant(g,307,187); plant(g,90,197);
        r(g,0,TOP,W,3,0x2c2738);
    }
    private static void downstairs(Graphics2D g,long t) {
        wood(g,0x997564); wall(g,0xcaa183,0xb48d7f);window(g,287,39,65,37);
        carpet(g,39,155,145,70,0x688780,0xdbc18d);
        // kitchen cabinets
        r(g,9,98,117,26,0x624d55);r(g,13,102,110,24,0xe0b99a);
        for(int x=18;x<120;x+=24) {r(g,x,107,19,15,0x96705e);r(g,x+8,114,3,3,0xf0d3a4);}
        r(g,11,94,117,5,0xd8d4b8);r(g,30,88,23,6,0x6e7370);
        // sink
        r(g,82,84,31,10,0x647d84);r(g,85,87,25,4,0xbfd0ca);
        // dining table, obstacle region
        r(g,146,134,82,36,0x554653);r(g,152,128,68,35,0x9f765f);
        r(g,155,132,61,27,0xc89c78);r(g,160,161,7,16,0x564750);r(g,206,161,7,16,0x564750);
        r(g,149,120,19,9,0x4c4351);r(g,148,113,21,10,0x8b6659);
        r(g,196,172,23,10,0x5c4751);r(g,199,176,17,7,0xa07865);
        r(g,178,139,18,7,0xe9d1aa);r(g,181,137,12,5,0x718978);
        // couch
        r(g,254,142,94,43,0x394553);r(g,259,143,85,30,0x7a9895);
        r(g,265,150,33,22,0x9bb3a7);r(g,302,150,36,22,0x9bb3a7);
        r(g,252,175,99,17,0x536669);
        // entry door right
        r(g,343,188,30,60,0x403e4a);r(g,347,192,22,55,0xa77865);r(g,365,218,3,3,0xf3d58f);
        r(g,342,247,38,3,0xe3b48b);
        plant(g,234,109);plant(g,365,123);plant(g,22,191);
        // frames
        r(g,177,43,36,28,0x665161);r(g,181,47,28,20,0x76948d);r(g,185,55,20,8,0xe0b68e);
    }
    private static void street(Graphics2D g,long t) {
        r(g,0,TOP,W,H-TOP,0x182d43);
        // stars and moon
        Random rnd=new Random(21);
        for(int i=0;i<74;i++) {int x=rnd.nextInt(W), y=TOP+rnd.nextInt(55); dot(g,x,y,(i%4==0)?0xfbdc9c:0x6b8eab);}
        r(g,319,45,18,18,0xe9d9a5);r(g,323,43,13,16,0xfbe9b2);r(g,327,42,13,17,0x182d43);
        // distant hills
        for(int x=0;x<W;x+=15) {int h=(int)(8+6*Math.sin(x/30.0)); r(g,x,100-h,18,20+h,0x203f4c);}
        // stone lane, sidewalk
        r(g,0,117,W,139,0x28564e);r(g,0,147,W,72,0x42556a);r(g,0,143,W,4,0xa38a7d);r(g,0,218,W,6,0xbaa38d);
        rnd=new Random(73);
        for(int i=0;i<95;i++) {int x=rnd.nextInt(W),y=147+rnd.nextInt(72);r(g,x,y,5+rnd.nextInt(7),1,0x536678);}
        for(int i=0;i<14;i++) {int x=i*31+6; r(g,x,220,17,3,0x567a68);}
        // left home
        house(g,0,66,120,76,0xa77c7b,0x493a58);
        // right shops
        house(g,260,65,124,76,0x80948c,0x464965);
        // trees and shrubs
        tree(g,127,107); tree(g,242,106);
        for(int x=4;x<W;x+=23) {
            if(x>111&&x<262) continue;
            r(g,x,130,21,14,0x2e6255);r(g,x+4,128,14,10,0x457c5c);
            dot(g,x+8,131,0x90aa75);
        }
        // Street lamps, glow halos
        lamp(g,146,94,t);lamp(g,242,118,t);
        // path at end of road
        r(g,366,158,18,54,0x6c6475);r(g,369,161,13,49,0x7c7280);
        // community investigation board
        r(g,345,72,34,49,0x45394f);r(g,348,74,29,38,0xb28c69);r(g,351,77,23,28,0xe6cd9f);
        r(g,355,81,14,2,0xb66e70);r(g,354,88,17,2,0x8a9c9c);r(g,354,94,11,2,0xc8a77b);
        r(g,351,112,3,18,0x604956);r(g,373,112,3,18,0x604956);
        // small flowerbeds
        for(int i=0;i<6;i++) flower(g,24+i*11,133,i%2==0?0xd59ea9:0xd9bd81);
        // center planter obstacle
        r(g,174,172,52,56,0x3f4755);r(g,178,174,44,50,0x56715e);
        for(int i=0;i<3;i++) plant(g,185+i*15,195);
    }
    private static void house(Graphics2D g,int x,int y,int w,int h,int main,int roof) {
        r(g,x,y+22,w,h-22,main);
        int cell=Math.max(1,w/8);
        for(int j=0;j<20;j++) {int xx=x+j*cell, yy=y+16-Math.abs(j-6)*2;r(g,xx,yy,cell+1,9,roof);}
        r(g,x,y+17,w,8,roof);
        for(int xx=x+18;xx<x+w-22;xx+=43) {r(g,xx,y+39,25,25,0x483f55);r(g,xx+4,y+43,17,17,0xb7a47e);r(g,xx+5,y+44,15,15,0x7c9cab);r(g,xx+12,y+43,2,17,0xe7c298);}
        r(g,x,y+h-5,w,5,0x584759);
    }
    private static void tree(Graphics2D g,int x,int y) {
        r(g,x-4,y-5,8,50,0x564b50);r(g,x-15,y-43,29,38,0x2b5f53);
        r(g,x-22,y-34,43,24,0x346d59);r(g,x-15,y-52,29,18,0x3e765e);
        r(g,x-5,y-60,19,22,0x4c8968);r(g,x-19,y-31,12,6,0x5c9873);
    }
    private static void lamp(Graphics2D g,int x,int y,long t) {
        r(g,x,y-24,3,72,0x3f3a51);r(g,x-7,y-25,17,5,0x453b4e);
        r(g,x-4,y-21,11,13,0xeaca8c);r(g,x-1,y-19,5,10,0xffe3a2);
        r(g,x-9,y-8,21,3,0x4d4355);
        if((t/17)%2==0) {dot(g,x-8,y-13,0xffdb8d);dot(g,x+8,y-18,0xffdb8d);}
    }
    private static void evidenceIcon(Graphics2D g,String id,int x,int y) {
        // Bespoke miniature clue sprites with readable silhouettes
        switch(id) {
            case "collar" -> {r(g,x-6,y-4,12,9,0xb75f62);r(g,x-3,y-2,6,5,0x6b3645);r(g,x,y+5,3,2,0xe7cb85);}
            case "photo", "drawing" -> {r(g,x-6,y-6,13,12,0xe9d5a4);r(g,x-4,y-4,9,6,0x76a3a1);r(g,x-1,y,5,4,0x7c695e);}
            case "toy" -> {r(g,x-7,y-1,14,5,0xe8d4ae);r(g,x-8,y-3,4,3,0xe8d4ae);r(g,x+5,y-3,4,3,0xe8d4ae);}
            case "bowl" -> {r(g,x-7,y-3,14,7,0x5993ba);r(g,x-5,y-5,10,3,0x9c7051);r(g,x-5,y+4,10,2,0x335c80);}
            case "window" -> {r(g,x-6,y-6,12,12,0xcead85);r(g,x-4,y-4,8,8,0x496d85);r(g,x-1,y-4,2,8,0xe7c99d);}
            case "scratch" -> {for(int i=0;i<3;i++)line(g,x-4+i*4,y+4,x+i*4,y-4,0xe7d6b6);}
            case "paw" -> {r(g,x-3,y-1,7,6,0xe6c5ac); for(int i=0;i<4;i++) dot(g,x-6+i*4,y-5,0xe6c5ac);}
            case "tag" -> {r(g,x-4,y-6,9,10,0xd8b968);r(g,x-2,y-2,5,4,0x645265);r(g,x-1,y-8,2,4,0xe4d2a5);}
            case "receipt" -> {r(g,x-5,y-7,10,14,0xf1e5cd);for(int i=0;i<3;i++) r(g,x-3,y-3+i*4,6,1,0x7e8290);}
            default -> r(g,x-3,y-3,6,6,0xf1d6a5);
        }
    }
    private static void arrow(Graphics2D g,int x,int y) {r(g,x-1,y-5,3,7,0xf5d38c);r(g,x-4,y,9,3,0xf5d38c);r(g,x-2,y+3,5,2,0xf5d38c);}

    static void player(Graphics2D g,int cx,int cy,int anim) {
        int x=cx-8,y=cy-15;
        // drop shadow
        r(g,x+3,y+24,13,3,0x363340);
        r(g,x+4,y+21,5,5,0x32344b);r(g,x+11,y+21,5,5,0x32344b);
        r(g,x+3,y+12,14,11,0x2c364a);r(g,x+5,y+13,10,9,0x7a9b97);
        r(g,x+1,y+14,3,9,0xdda784);r(g,x+17,y+14,3,9,0xdda784);
        r(g,x+5,y+5,11,9,0xebbc96);
        r(g,x+4,y+3,12,6,0x4b3b50);r(g,x+2,y+7,3,5,0x4b3b50);
        r(g,x+6,y+10,2,2,0x3d3444);r(g,x+13,y+10,2,2,0x3d3444);
        r(g,x+5,y+17,11,2,0xe8b69b);r(g,x+14,y+14,3,3,0xe0bf7f);
        if(anim%2==0) {r(g,x+4,y+24,5,2,0x373348);r(g,x+12,y+23,5,3,0x373348);}
    }
    private static void npc(Graphics2D g,String id,int cx,int cy,int t) {
        if(id.equals("max")) {dog(g,cx,cy,0xb17d5b);return;}
        int x=cx-8,y=cy-15;
        r(g,x+3,y+25,14,2,0x2c3446);r(g,x+5,y+21,4,6,0x3c3950);r(g,x+12,y+21,5,6,0x3c3950);
        int clothes=id.equals("courier")?0xe5a165:id.equals("guard")?0x476b97:0xd896ae;
        r(g,x+4,y+12,13,10,clothes);r(g,x+1,y+13,3,9,0xc58d72);r(g,x+17,y+13,3,9,0xc58d72);
        r(g,x+5,y+4,11,10,0xe3aa86);
        r(g,x+4,y+2,14,6,id.equals("guard")?0x293e63:id.equals("courier")?0x405989:0x624450);
        if(id.equals("courier")) {r(g,x+1,y,18,3,0x405989);r(g,x+18,y+13,6,8,0x916348);}
        if(id.equals("guard")) {r(g,x+14,y+16,3,2,0xd5c379);r(g,x+17,y+16,6,3,0xd0b985);}
        if(id.equals("child")) {r(g,x+7,y+11,2,2,0x403348);r(g,x+14,y+11,2,2,0x403348);}
    }
    static void dog(Graphics2D g,int cx,int cy,int fur) {
        int x=cx-15,y=cy-14;
        r(g,x+6,y+9,22,11,0x4a3440);r(g,x+8,y+9,17,10,fur);
        r(g,x+11,y+18,4,7,0x6d4d44);r(g,x+22,y+18,4,7,0x6d4d44);
        r(g,x+0,y+4,12,13,0x513846);r(g,x+2,y+6,10,10,fur);
        r(g,x+3,y+4,4,6,0x6a4d48);r(g,x+9,y+6,3,5,0x6a4d48);
        r(g,x-1,y+11,8,5,0xc5a08a);r(g,x+2,y+12,2,2,0x292c3d);
        r(g,x+7,y+10,2,2,0x2d2d3b);r(g,x+11,y+16,4,2,0xead58a);
        r(g,x+28,y+10,4,4,fur);r(g,x+30,y+6,4,6,fur);
    }
    private static void darkness(Graphics2D g,int x,int y,boolean flashlight) {
        if(!flashlight) {r(g,0,TOP,W,H-TOP,0x0c1328); g.setColor(new Color(9,15,35,155));g.fillRect(0,TOP,W,H-TOP);return;}
        // Soft, limited flashlight cone painted over the original pixel artwork.
        Graphics2D gg=(Graphics2D)g.create();
        gg.setComposite(AlphaComposite.SrcOver);
        float[] fractions={0.0f,0.35f,0.69f,1.0f};
        Color[] colors={new Color(8,12,28,7),new Color(8,12,28,42),new Color(8,12,28,170),new Color(8,12,28,235)};
        gg.setPaint(new RadialGradientPaint(new Point2D.Float(x,y),132f,fractions,colors));
        gg.fillRect(0,TOP,W,H-TOP);gg.dispose();
        // Tiny illuminated stars around clue points remain subtle under the spotlight.
    }
    private static void title(Graphics2D g,long t) {
        r(g,0,0,W,H,0x17283e);
        Random rnd=new Random(103);
        for(int i=0;i<115;i++) {int px=rnd.nextInt(W),py=rnd.nextInt(170);r(g,px,py,1+(i%4==0?1:0),1,i%5==0?0xe4cead:0x7385ab);}
        r(g,279,32,40,40,0xf1d6a4);r(g,285,28,34,40,0xf4e6bf);r(g,300,26,29,46,0x17283e);
        for(int x=0;x<W;x+=8) {
            int h=(int)(33+14*Math.sin(x/31.0));r(g,x,158-h,10,h+98,0x253a50);
            int h2=(int)(25+17*Math.sin(x/47.0));r(g,x,183-h2,10,h2+73,0x2d4c51);
        }
        // house silhouette
        r(g,57,130,93,94,0x654c5b);r(g,47,125,111,11,0x463b55);
        for(int j=0;j<10;j++)r(g,49+j*11,122-Math.abs(j-5)*5,11,10,0x463b55);
        r(g,87,159,27,43,0x302b41);r(g,59,143,19,24,0xe7ba7f);
        r(g,129,149,13,23,0xe7ba7f);r(g,0,224,W,32,0x263b4a);
        for(int x=0;x<W;x+=21) flower(g,x,233,x%3==0?0xc38b9b:0xb2b682);
        dog(g,246,216,0x9a715c);
        // hero's collar
        r(g,247,217,5,2,0xefc477);
    }
    private static void board(Graphics2D g,long t) {
        r(g,0,0,W,H,0x293448);r(g,0,25,W,H-25,0x746256);
        for(int x=0;x<W;x+=28)r(g,x,32,1,H-32,0x856d60);
        r(g,34,28,319,216,0x554753);r(g,41,35,305,198,0xb6977a);
        r(g,47,40,293,184,0xc9ab86);
        // pinned photos with red connecting thread
        String[] titles={"HERO", "WINDOW", "PAWS", "MAX", "SUSPECTS"};
        int[][] p={{166,56},{75,108},{160,152},{265,114},{273,48}};
        for(int i=0;i<p.length;i++) {
            int x=p[i][0],y=p[i][1];
            r(g,x,y,45,33,0xeee0bf);r(g,x+3,y+3,39,23,0x678b8b);
            r(g,x+10,y+8,22,14,0x9f795e);r(g,x+22,y+8,6,6,0x483c52);
            r(g,x+22,y,3,3,0xd95f69);
        }
        g.setColor(c(0xb45c63));g.drawLine(189,92,97,125);g.drawLine(189,92,184,168);g.drawLine(184,168,285,129);g.drawLine(285,129,290,64);
        for(int i=0;i<13;i++) {dot(g,65+i*21,207,0xb98867);}
    }
    private static void ending(Graphics2D g,boolean good,long t) {
        r(g,0,0,W,H,good?0x4b7e89:0x1d2e47);
        for(int y=0;y<176;y+=4) r(g,0,y,W,4,good?(y<72?0x659fa7:(y<132?0x78ada5:0xa1bb9e)):0x293853);
        r(g,0,159,W,97,good?0x7c9d73:0x334e56);
        r(g,0,194,W,62,good?0xb69d79:0x49505a);
        for(int x=0;x<W;x+=23) {r(g,x,153,18,20,good?0x537f68:0x2c5657);r(g,x+3,149,13,12,good?0x6b9573:0x3a6861);}
        if(good) {r(g,282,34,32,32,0xffe0a0);r(g,291,30,26,32,0xffeab6);}
        else {r(g,282,34,23,23,0xbfb6ac);r(g,293,26,23,25,0x1d2e47);}
        house(g,0,99,132,70,good?0xc5a27e:0x6d6979,0x55475c);
        player(g,156,183,0);
        if(good) {dog(g,212,185,0xa77f5e);dog(g,253,189,0x74564b);r(g,172,121,8,7,0xe89a9a);r(g,180,117,9,7,0xe89a9a);}
        else {r(g,211,178,2,7,0x71818b);r(g,224,169,2,4,0x71818b);}
    }
}