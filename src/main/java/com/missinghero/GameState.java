package com.missinghero;

import java.util.*;

/** Independent, testable game rules. No external dependencies. */
public final class GameState {
    public enum Scene { TITLE, BEDROOM, DOWNSTAIRS, STREET, INTERROGATION, TRUE_END, BAD_END }
    public enum Kind { EVIDENCE, EXIT, CHARACTER, ENDPOINT }
    public record Spot(String id, int x, int y, String name, String description, Kind kind) { }

    public Scene scene = Scene.TITLE;
    public int x = 11, y = 10;
    public boolean flashlight = true;
    public boolean journal = false;
    public boolean evidenceChoice = false;
    public String messageTitle = "";
    public String messageBody = "";
    public String endingExplanation = "";
    public final LinkedHashSet<String> collected = new LinkedHashSet<>();
    public final LinkedHashSet<String> interviewed = new LinkedHashSet<>();

    public static final LinkedHashMap<String,String> CLUES = new LinkedHashMap<>();
    public static final LinkedHashMap<String,String> SUSPECTS = new LinkedHashMap<>();
    private final EnumMap<Scene,List<Spot>> spots = new EnumMap<>(Scene.class);
    public static final int TOTAL_CLUES = 10;

    static {
        CLUES.put("collar", "ปลอกคอของ Hero");
        CLUES.put("photo", "ภาพถ่าย Hero กับสุนัขอีกตัว");
        CLUES.put("toy", "ของเล่นชิ้นโปรด");
        CLUES.put("bowl", "ชามอาหารที่ยังเต็มอยู่");
        CLUES.put("window", "หน้าต่างชั้นล่างที่เปิดอยู่");
        CLUES.put("scratch", "รอยข่วนบนกรอบประตู");
        CLUES.put("paw", "รอยเท้าสองชุดบนทางเดิน");
        CLUES.put("drawing", "ภาพวาดจากเด็กข้างบ้าน");
        CLUES.put("tag", "ป้ายชื่อ MAX ใต้ต้นไม้");
        CLUES.put("receipt", "ใบส่งพัสดุของพนักงานส่งของ");
        SUSPECTS.put("child", "เด็กข้างบ้าน");
        SUSPECTS.put("courier", "พนักงานส่งของ");
        SUSPECTS.put("guard", "รปภ. หมู่บ้าน");
        SUSPECTS.put("max", "MAX สุนัขจากอีกซอย");
    }

    public GameState() {
        spots.put(Scene.BEDROOM, List.of(
            new Spot("collar",5,8,"ปลอกคอของ Hero", "ปลอกคอสีแดงตกอยู่ข้างเตียง... ไม่มีร่องรอยการต่อสู้ เหมือนมันหลุดออกตอน Hero วิ่งเล่น",Kind.EVIDENCE),
            new Spot("photo",13,5,"รูปถ่ายบนโต๊ะ", "รูปเก่าของ Hero ยืนคู่กับสุนัขสีน้ำตาล มีข้อความด้านหลังว่า 'Hero & MAX'",Kind.EVIDENCE),
            new Spot("toy",18,10,"ตุ๊กตากระดูก", "ของเล่นประจำตัวของ Hero ยังอยู่ที่เดิม ถ้ามีคนล่อลวงมันด้วยของเล่น ทำไมของชิ้นนี้จึงไม่หายไป?",Kind.EVIDENCE),
            new Spot("to_downstairs",22,5,"ลงไปชั้นล่าง", "บันไดนำไปสู่ห้องนั่งเล่นชั้นหนึ่ง",Kind.EXIT)
        ));
        spots.put(Scene.DOWNSTAIRS, List.of(
            new Spot("bowl",7,8,"ชามอาหาร", "อาหารยังเต็มชาม Hero ไม่ได้ออกไปเพราะหิวแน่ ๆ",Kind.EVIDENCE),
            new Spot("window",18,4,"หน้าต่างเปิด", "บานหน้าต่างแง้มออกไปทางสวน ไม่มีร่องรอยงัดแงะ เหมือน Hero เป็นฝ่ายออกไปเอง",Kind.EVIDENCE),
            new Spot("scratch",16,10,"รอยข่วนที่ประตู", "รอยข่วนเล็ก ๆ มุ่งออกจากบ้าน ไม่ใช่รอยของคนพยายามลากสุนัขเข้ามา",Kind.EVIDENCE),
            new Spot("to_street",22,10,"ออกไปนอกบ้าน", "ประตูหน้าบ้านเปิดออกสู่ถนนในหมู่บ้าน",Kind.EXIT)
        ));
        spots.put(Scene.STREET, List.of(
            new Spot("paw",5,8,"รอยเท้าสุนัข", "รอยเท้าสองชุดเดินเคียงข้างกันไปทางอีกซอย ไม่มีร่องรอยการลากหรือวิ่งหนี",Kind.EVIDENCE),
            new Spot("drawing",11,4,"ภาพวาดบนรั้ว", "ภาพวาดของเด็กข้างบ้าน: Hero วิ่งเล่นอย่างมีความสุขกับสุนัขสีน้ำตาล",Kind.EVIDENCE),
            new Spot("tag",18,10,"ป้ายชื่อโลหะ", "ป้ายชื่อสลักว่า MAX ตกอยู่ข้างทาง ชื่อเดียวกับที่อยู่หลังภาพถ่าย",Kind.EVIDENCE),
            new Spot("receipt",19,4,"กระดาษจัดส่ง", "พนักงานส่งของมาที่นี่ตอนบ่าย แต่ Hero หายตอนกลางคืน เวลาไม่ตรงกัน",Kind.EVIDENCE),
            new Spot("child",8,5,"เด็กข้างบ้าน", "หนูเห็น Hero วิ่งผ่านไปกับสุนัขชื่อ MAX นะ! ดูสนุกกันมากเลย",Kind.CHARACTER),
            new Spot("courier",14,5,"พนักงานส่งของ", "ผมแวะมาส่งของตอนบ่ายเท่านั้นครับ มีหลักฐานเวลาอยู่บนใบส่งของ",Kind.CHARACTER),
            new Spot("guard",8,11,"รปภ. หมู่บ้าน", "กล้องเห็นสุนัขสองตัวเดินไปทางซอยตะวันออก ไม่มีใครอุ้ม Hero ไปครับ",Kind.CHARACTER),
            new Spot("max",18,8,"MAX สุนัขจากอีกซอย", "MAX กระดิกหางเมื่อเห็นภาพถ่ายของ Hero แล้วหันหน้ามองไปทางซอยตะวันออก",Kind.CHARACTER),
            new Spot("to_final",22,3,"กระดานสรุปคดี", "บอร์ดที่ใช้เชื่อมโยงหลักฐานและสรุปว่าเกิดอะไรขึ้นกับ Hero",Kind.ENDPOINT)
        ));
    }

    public void start() {
        collected.clear(); interviewed.clear();
        scene=Scene.BEDROOM; x=11; y=10; flashlight=true; journal=false;
        evidenceChoice=false; endingExplanation="";
        tell("คืนที่ Hero หายไป", "คุณตื่นขึ้นมากลางดึก... Hero ไม่อยู่ในห้อง เหลือเพียงปลอกคอและความเงียบ สำรวจบ้านเพื่อหาความจริง!");
    }

    public boolean hasDialog() { return !messageBody.isEmpty(); }
    public void dismissDialog() { messageTitle=""; messageBody=""; }
    public void tell(String title, String body) { messageTitle=title; messageBody=body; }
    public boolean isWorldScene() { return scene == Scene.BEDROOM || scene == Scene.DOWNSTAIRS || scene == Scene.STREET; }
    public List<Spot> spots() { return spots.getOrDefault(scene, List.of()); }
    public boolean isNear(Spot s) { return Math.abs(x-s.x())+Math.abs(y-s.y())<=2; }
    public Spot nearestSpot() {
        Spot nearest=null; int distance=99;
        for (Spot s: spots()) {
            int d=Math.abs(x-s.x())+Math.abs(y-s.y());
            if(d<=2 && d<distance) {nearest=s; distance=d;}
        }
        return nearest;
    }
    public boolean canWalk(int xx, int yy) {
        if(!isWorldScene() || xx<1 || xx>22 || yy<3 || yy>12) return false;
        // Keep important furniture walkable around its border while respecting large props.
        if(scene==Scene.BEDROOM && (xx>=2 && xx<=7 && yy>=4 && yy<=6)) return false;
        if(scene==Scene.DOWNSTAIRS && (xx>=9 && xx<=13 && yy>=6 && yy<=7)) return false;
        if(scene==Scene.STREET && (xx>=11 && xx<=13 && yy>=9 && yy<=11)) return false;
        return true;
    }
    public void move(int dx,int dy) {
        if(journal || hasDialog() || !isWorldScene()) return;
        int xx=x+dx, yy=y+dy;
        if(canWalk(xx,yy)) {x=xx; y=yy;}
    }
    public void interact() {
        if (hasDialog()) {dismissDialog(); return;}
        if(journal) {journal=false;return;}
        Spot s=nearestSpot();
        if(s==null) { tell("สำรวจรอบตัว", "เดินเข้าไปใกล้วัตถุที่มีประกาย หรือผู้ต้องสงสัย แล้วกด SPACE / E เพื่อสำรวจ"); return;}
        switch(s.kind()) {
            case EVIDENCE -> {
                if(collected.add(s.id())) tell("+ ได้รับหลักฐาน: "+s.name(), s.description());
                else tell("ตรวจสอบแล้ว: "+s.name(), "คุณเก็บเบาะแสนี้ลงในสมุดแล้ว กด J เพื่อเปิดสมุดหลักฐาน");
            }
            case CHARACTER -> {
                interviewed.add(s.id());
                tell("สอบถาม: "+s.name(),s.description());
            }
            case EXIT -> transit(s);
            case ENDPOINT -> {
                if (collected.size()<7 || interviewed.size()<4)
                    tell("ยังสรุปคดีไม่ได้", "ต้องมีหลักฐานอย่างน้อย 7 / 10 ชิ้น และสอบถามผู้ต้องสงสัยครบ 4 / 4 ราย ก่อนเข้าห้องสรุปคดี ตอนนี้มี "+collected.size()+" ชิ้น และสอบถาม "+interviewed.size()+" ราย");
                else {scene=Scene.INTERROGATION; journal=false; evidenceChoice=false; dismissDialog();}
            }
        }
    }
    private void transit(Spot s) {
        if(s.id().equals("to_downstairs")) {
            if(collected.size()<2) {tell("ควรสำรวจห้องก่อน", "เก็บหลักฐานในห้องนอนอย่างน้อย 2 ชิ้นก่อนลงไปชั้นล่าง");return;}
            scene=Scene.DOWNSTAIRS; x=3; y=10; tell("ชั้นล่าง · FIRST FLOOR", "บ้านเงียบผิดปกติ มองหาของใช้ของ Hero และทางที่มันอาจออกไปได้");
        } else if(s.id().equals("to_street")) {
            if(collected.size()<4) {tell("ยังไม่พร้อมออกไป", "สำรวจบ้านให้พบหลักฐานอย่างน้อย 4 ชิ้นก่อนออกไปนอกบ้าน");return;}
            scene=Scene.STREET; x=3; y=11; tell("ถนนในหมู่บ้าน · OUTSIDE", "คืนนี้มืดมาก ใช้ปุ่ม F เพื่อเปิดหรือปิดไฟฉาย แล้วลองพูดคุยกับทั้ง 4 ผู้ต้องสงสัย");
        }
    }
    public void finalAnswer(int choice) {
        if(scene!=Scene.INTERROGATION) return;
        if(!evidenceChoice) {
            if(choice==5) {evidenceChoice=true; return;}
            if(choice>=1 && choice<=4) {
                String[] reasons={"คุณกล่าวหาเด็กข้างบ้านโดยไม่มีหลักฐานที่ยืนยันการลักพาตัว", "คุณกล่าวหาพนักงานส่งของ ทั้งที่เวลาส่งของไม่ตรงกับเหตุการณ์", "คุณกล่าวหา รปภ. แต่บันทึกกล้องไม่พบคนร้าย", "คุณเข้าใจว่า MAX เป็นผู้ร้าย ทั้งที่เขาเป็นเพื่อนของ Hero"};
                end(false,reasons[choice-1]);
            }
        } else if(choice>=1 && choice<=3) {
            if(choice==1 && collected.containsAll(List.of("photo","window","paw","tag")))
                end(true,"ภาพถ่าย + หน้าต่าง + รอยเท้า + ป้ายชื่อ MAX เชื่อมโยงกันได้: Hero ออกไปพบเพื่อนด้วยความสมัครใจ");
            else
                end(false,choice==1 ? "ยังขาดหลักฐานสำคัญที่จะยืนยันเส้นทางของ Hero" : "ชุดหลักฐานนี้ไม่เพียงพอ และยังเชื่อมโยงเหตุการณ์ไม่ตรงกับความจริง");
        }
    }
    private void end(boolean good,String explanation) {
        endingExplanation=explanation;
        scene=good?Scene.TRUE_END:Scene.BAD_END;
        evidenceChoice=false; dismissDialog(); journal=false;
    }
    public String sceneName() {
        return switch(scene) {
            case TITLE -> "START";
            case BEDROOM -> "01 / BEDROOM";
            case DOWNSTAIRS -> "02 / FIRST FLOOR";
            case STREET -> "03 / OUTSIDE";
            case INTERROGATION -> "04 / THE TRUTH";
            case TRUE_END -> "TRUE ENDING";
            case BAD_END -> "BAD ENDING";
        };
    }
}