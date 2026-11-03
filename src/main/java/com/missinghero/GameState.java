package com.missinghero;

import java.util.*;

/**
 * Game rules without Swing; objects demonstrate abstraction,
 * inheritance and polymorphism.
 */
public final class GameState {

    public enum Scene {
        TITLE, BEDROOM, LIVING, NEIGHBOR, INTERROGATION, TRUE_END, BAD_END
    }

    public enum Puzzle {
        NONE, CRATES, DRAWER, CAMERA, TIMELINE
    }

    public enum Kind {
        CLUE, NPC, EXIT, PUZZLE
    }

    /** An interactive world object. */
    public static abstract class WorldObject {

        private final String id;
        private final String name;
        private final int x;
        private final int y;

        protected WorldObject(String id, int x, int y, String name) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.name = name;
        }

        public final String id() {
            return id;
        }

        public final int x() {
            return x;
        }

        public final int y() {
            return y;
        }

        public final String name() {
            return name;
        }

        public abstract Kind kind();

        public abstract void interact(GameState game);
    }

    public static final class Evidence extends WorldObject {

        private final String description;
        private final boolean needsLight;

        Evidence(
                String id,
                int x,
                int y,
                String name,
                String description,
                boolean needsLight
        ) {
            super(id, x, y, name);
            this.description = description;
            this.needsLight = needsLight;
        }

        public Kind kind() {
            return Kind.CLUE;
        }

        public void interact(GameState game) {
            if (needsLight && !game.flashlight) {
                game.say(
                        "มองไม่ชัด",
                        "กด F เพื่อเปิดไฟฉายและส่องดูบริเวณนี้ให้ชัดก่อน"
                );
                return;
            }

            game.collected.add(id());
            game.openClue = id();
            game.say(name(), description);
        }
    }

    public static final class Suspect extends WorldObject {

        private final String speech;

        Suspect(
                String id,
                int x,
                int y,
                String name,
                String speech
        ) {
            super(id, x, y, name);
            this.speech = speech;
        }

        public Kind kind() {
            return Kind.NPC;
        }

        public void interact(GameState game) {
            game.interviewed.add(id());
            game.say("สอบถาม: " + name(), speech);
        }
    }

    public static final class Passage extends WorldObject {

        private final Scene destination;
        private final boolean backward;

        Passage(
                String id,
                int x,
                int y,
                String name,
                Scene destination,
                boolean backward
        ) {
            super(id, x, y, name);
            this.destination = destination;
            this.backward = backward;
        }

        public Kind kind() {
            return Kind.EXIT;
        }

        public void interact(GameState game) {
            game.pass(this);
        }
    }

    public static final class Device extends WorldObject {

        private final Puzzle puzzle;

        Device(
                String id,
                int x,
                int y,
                String name,
                Puzzle puzzle
        ) {
            super(id, x, y, name);
            this.puzzle = puzzle;
        }

        public Kind kind() {
            return Kind.PUZZLE;
        }

        public void interact(GameState game) {
            game.openPuzzle(puzzle);
        }
    }

    private final EnumMap<Scene, List<WorldObject>> map =
            new EnumMap<>(Scene.class);

    public static final LinkedHashMap<String, String> CLUES =
            new LinkedHashMap<>();

    public static final int TOTAL_CLUES = 13;

    private static final Scene[] ORDER = {
            Scene.BEDROOM,
            Scene.LIVING,
            Scene.NEIGHBOR
    };

    private static final String[] SCENE_NAME = {
            "TITLE",
            "01 · ห้องนอน ชั้น 2",
            "02 · ห้องนั่งเล่น (รวมหลักฐานในบ้าน)",
            "03 · หน้าบ้านเพื่อนบ้าน / จุดสอบถาม",
            "FINAL · ห้องสอบสวน",
            "TRUE ENDING",
            "BAD ENDING"
    };

    private static final List<String> ORDER_CORRECT = List.of(
            "food",
            "courier",
            "guard",
            "leave",
            "walk",
            "friend"
    );

    private static final LinkedHashMap<String, String> EVENTS =
            new LinkedHashMap<>();

    static {
        clue("collar", "ปลอกคอของฮีโร่");
        clue("door_scratch", "รอยข่วนที่ประตูห้องนอน");
        clue("ball", "ลูกบอลชิ้นโปรด");

        clue("bowl", "ชามอาหารของฮีโร่");
        clue("window", "หน้าต่างชั้น 1 เปิดอยู่");
        clue("photo", "รูปถ่ายของฮีโร่กับเพื่อน");
        clue("parcel", "พัสดุและขนมสุนัข");
        clue("receipt", "ใบส่งของเวลา 07:35");
        clue("camera", "ภาพบันทึกจากกล้องวงจรปิด");

        clue("paw_hero", "รอยเท้าของฮีโร่");
        clue("paw_other", "รอยเท้าของสุนัขอีกตัว");
        clue("max_collar", "ปลอกคอชื่อ MAX");
        clue("alley_photo", "รูปถ่ายใกล้ทางเข้าซอย");

        EVENTS.put("walk", "5 · รอยเท้ามุ่งออกตามถนน");
        EVENTS.put("friend", "6 · ฮีโร่พบเพื่อนสุนัขในซอย");
        EVENTS.put("food", "1 · ฮีโร่กินอาหารอยู่ในบ้าน");
        EVENTS.put("guard", "3 · รปภ. ขับรถผ่านหน้าบ้าน");
        EVENTS.put("leave", "4 · ฮีโร่ออกไปทางที่เปิดอยู่");
        EVENTS.put("courier", "2 · พนักงานส่งของมาถึงบ้าน");
    }

    private static void clue(String id, String name) {
        CLUES.put(id, name);
    }

    private void evidence(
            Scene s,
            String id,
            int x,
            int y,
            String description,
            boolean light
    ) {
        map.get(s).add(
                new Evidence(
                        id,
                        x,
                        y,
                        CLUES.get(id),
                        description,
                        light
                )
        );
    }

    private void suspect(
            Scene s,
            String id,
            int x,
            int y,
            String name,
            String dialog
    ) {
        map.get(s).add(
                new Suspect(id, x, y, name, dialog)
        );
    }

    private void device(
            Scene s,
            String id,
            int x,
            int y,
            String name,
            Puzzle p
    ) {
        map.get(s).add(
                new Device(id, x, y, name, p)
        );
    }

    public GameState() {

        // Three walkable maps only: bedroom, living room, and a shared outdoor area.
        // All 19 original clues stay in the game even though the maps are consolidated.
        for (Scene s : ORDER) {
            map.put(s, new ArrayList<>());
        }

        evidence(
                Scene.BEDROOM,
                "collar",
                5,
                8,
                "ปลอกคอหลวมตกใกล้เตียงนอน อาจหลุดเองหรือมีคนถอดออก ยังสรุปไม่ได้",
                false
        );

        evidence(
                Scene.BEDROOM,
                "door_scratch",
                22,
                8,
                "ใต้ประตูที่แง้มอยู่มีรอยเล็บเล็ก ๆ เหมือนฮีโร่ข่วนจากด้านใน",
                true
        );

        evidence(
                Scene.BEDROOM,
                "ball",
                11,
                10,
                "ลูกบอลมีรอยกัดใหม่",
                false
        );

        device(
                Scene.BEDROOM,
                "crates",
                18,
                10,
                "กล่องขวางบันได · จัดวางกล่อง",
                Puzzle.CRATES
        );

        // Living room also contains all previously separate toy-room,
        // storage and porch clues.
        evidence(
                Scene.LIVING,
                "bowl",
                19,
                5,
                "ชามอาหารเหลืออยู่บางส่วนและมีรอยเท้าสดใหม่ ฮีโร่น่าจะอยู่ในบ้านตอนเช้า",
                false
        );

        evidence(
                Scene.LIVING,
                "window",
                18,
                4,
                "หน้าต่างเปิดแง้มและมีรอยเล็บคล้ายสุนัข อาจเป็นเส้นทางที่ฮีโร่ออกไป",
                true
        );

        evidence(
                Scene.LIVING,
                "photo",
                15,
                3,
                "รูปถ่ายฮีโร่เล่นกับเพื่อนสุนัข ด้านหลังเขียนว่า Best Friends",
                false
        );

        evidence(
                Scene.LIVING,
                "parcel",
                4,
                4,
                "พัสดุและขนมสุนัขของคนส่งของวางอยู่ในห้องนั่งเล่น ทำให้เขาดูน่าสงสัย",
                false
        );

        evidence(
                Scene.LIVING,
                "receipt",
                5,
                4,
                "ใบส่งของมีเวลา 07:35 น. สังเกตตัวเลขเพื่อนำไปไขรหัสประตูในห้องนี้",
                false
        );

        evidence(
                Scene.LIVING,
                "camera",
                1,
                3,
                "เปิดกล้องวงจรปิดจากจอในห้องนั่งเล่น ภาพแรกเห็นเงาคนผ่านหน้าบ้าน ต้องซ่อมวงจรก่อนจึงรู้ว่าเป็น รปภ.",
                false
        );

        device(
                Scene.LIVING,
                "drawer",
                16,
                10,
                "ประตูห้องนั่งเล่น · ใส่รหัสเพื่อหากุญแจ",
                Puzzle.DRAWER
        );

        device(
                Scene.LIVING,
                "cctv",
                1,
                4,
                "จอกล้องวงจรปิดในห้องนั่งเล่น · ต่อสายไฟ",
                Puzzle.CAMERA
        );

        // One outdoor map combines tracks, neighbor, delivery,
        // security, alley clues and all four witnesses.
        evidence(
                Scene.NEIGHBOR,
                "paw_hero",
                4,
                8,
                "รอยเท้าฮีโร่เริ่มจากบ้านแล้วออกมาตามทางเดิน ไม่พบรอยถูกอุ้ม",
                false
        );

        evidence(
                Scene.NEIGHBOR,
                "paw_other",
                9,
                8,
                "มีรอยเท้าสุนัขอีกชุดเดินคู่กันอยู่หน้าบ้านเพื่อนบ้าน",
                false
        );

        evidence(
                Scene.NEIGHBOR,
                "max_collar",
                19,
                7,
                "พบปลอกคอสลักชื่อ MAX ใกล้ทางเข้าซอย ตรงกับชื่อเพื่อนในรูปถ่าย",
                false
        );

        evidence(
                Scene.NEIGHBOR,
                "alley_photo",
                4,
                10,
                "รูปถ่ายฮีโร่กับ MAX ใกล้ทางเข้าซอย ด้านหลังเขียนว่า See you tomorrow.",
                false
        );

        suspect(
                Scene.NEIGHBOR,
                "child",
                9,
                10,
                "เด็กข้างบ้าน",
                "หนูชอบเล่นกับฮีโร่มาก แต่หนูไม่ได้พาไปนะ!"
        );

        suspect(
                Scene.NEIGHBOR,
                "courier",
                14,
                10,
                "พนักงานส่งของ",
                "ผมส่งของตอน 07:35 เท่านั้น ลองตรวจใบส่งของกับกล้องสิครับ"
        );

        suspect(
                Scene.NEIGHBOR,
                "guard",
                19,
                10,
                "รปภ. หมู่บ้าน",
                "ผมขับรถลาดตระเวนผ่านบ้าน และเห็นสุนัขสองตัวเดินไปทางซอย"
        );

        suspect(
                Scene.NEIGHBOR,
                "max",
                22,
                6,
                "MAX · เพื่อนสุนัข",
                "MAX กระดิกหางและดูเป็นมิตร เขาช่วยดูแลฮีโร่หลังจากมันออกจากบ้าน"
        );

        map.get(Scene.BEDROOM).add(
                new Passage(
                        "next",
                        22,
                        11,
                        "ลงบันไดไปห้องนั่งเล่น",
                        Scene.LIVING,
                        false
                )
        );

        map.get(Scene.LIVING).add(
                new Passage(
                        "back",
                        2,
                        11,
                        "กลับห้องนอน",
                        Scene.BEDROOM,
                        true
                )
        );

        map.get(Scene.LIVING).add(
                new Passage(
                        "next",
                        22,
                        11,
                        "ออกไปหน้าบ้านเพื่อนบ้าน",
                        Scene.NEIGHBOR,
                        false
                )
        );

        map.get(Scene.NEIGHBOR).add(
                new Passage(
                        "back",
                        1,
                        11,
                        "กลับเข้าห้องนั่งเล่น",
                        Scene.LIVING,
                        true
                )
        );

        device(
                Scene.NEIGHBOR,
                "timeline",
                22,
                11,
                "กระดานเรียงลำดับเหตุการณ์",
                Puzzle.TIMELINE
        );
    }

    private Scene scene = Scene.TITLE;

    private int playerX = 11;
    private int playerY = 10;

    private boolean flashlight = true;
    private boolean journal = false;

    private boolean crateSolved = false;
    private boolean drawerSolved = false;
    private boolean cameraSolved = false;
    private boolean timelineSolved = false;

    private String openClue = null;
    private String messageTitle = "";
    private String messageBody = "";
    private String endingExplanation = "";
    private String codeInput = "";

    private Puzzle puzzle = Puzzle.NONE;

    private final LinkedHashSet<String> collected = new LinkedHashSet<>();
    private final LinkedHashSet<String> interviewed = new LinkedHashSet<>();

    private final boolean[] cratePlaced = new boolean[3];
    private final boolean[] wireConnected = new boolean[3];

    private final ArrayList<String> timeline = new ArrayList<>();

    private boolean waitingSummary = false;

    public Scene scene() {
        return scene;
    }

    public int playerX() {
        return playerX;
    }

    public int playerY() {
        return playerY;
    }

    public boolean flashlight() {
        return flashlight;
    }

    public void toggleFlashlight() {
        flashlight = !flashlight;
    }

    public boolean journal() {
        return journal;
    }

    public void toggleJournal() {
        if (puzzle == Puzzle.NONE && !hasDialog() && isWorldScene()) {
            journal = !journal;
        }
    }

    public void closeJournal() {
        journal = false;
    }

    public String openClue() {
        return openClue;
    }

    public String messageTitle() {
        return messageTitle;
    }

    public String messageBody() {
        return messageBody;
    }

    public String endingExplanation() {
        return endingExplanation;
    }

    public Puzzle puzzle() {
        return puzzle;
    }

    public String codeInput() {
        return codeInput;
    }

    public boolean crateSolved() {
        return crateSolved;
    }

    public boolean drawerSolved() {
        return drawerSolved;
    }

    public boolean cameraSolved() {
        return cameraSolved;
    }

    public boolean timelineSolved() {
        return timelineSolved;
    }

    public boolean[] crates() {
        return cratePlaced.clone();
    }

    public boolean[] wires() {
        return wireConnected.clone();
    }

    public List<String> timeline() {
        return List.copyOf(timeline);
    }

    public Map<String, String> events() {
        return Collections.unmodifiableMap(EVENTS);
    }

    public Set<String> collected() {
        return Collections.unmodifiableSet(collected);
    }

    public Set<String> interviewed() {
        return Collections.unmodifiableSet(interviewed);
    }

    public List<WorldObject> objects() {
        return Collections.unmodifiableList(
                map.getOrDefault(scene, List.of())
        );
    }

    public boolean isWorldScene() {
        return map.containsKey(scene);
    }

    public String sceneName() {
        return SCENE_NAME[scene.ordinal()];
    }

    public int stage() {
        return switch (scene) {
            case BEDROOM -> 1;
            case LIVING -> 2;
            case NEIGHBOR -> 3;
            default -> 4;
        };
    }

    public void start() {
        scene = Scene.BEDROOM;

        playerX = 11;
        playerY = 10;

        flashlight = true;
        journal = false;

        collected.clear();
        interviewed.clear();

        crateSolved = false;
        drawerSolved = false;
        cameraSolved = false;
        timelineSolved = false;

        Arrays.fill(cratePlaced, false);
        Arrays.fill(wireConnected, false);

        timeline.clear();

        puzzle = Puzzle.NONE;
        openClue = null;
        codeInput = "";

        waitingSummary = false;
        endingExplanation = "";

        say(
                "เช้าวันที่ Hero หายไป",
                "คุณตื่นขึ้นมาแล้วไม่พบฮีโร่ในห้อง ไม่มีร่องรอยการต่อสู้ สำรวจหลักฐานและหาทางลงชั้นล่าง"
        );
    }

    public boolean hasDialog() {
        return !messageBody.isEmpty();
    }

    private void say(String title, String body) {
        messageTitle = title;
        messageBody = body;
    }

    public void closeDialog() {
        if (openClue != null) {
            openClue = null;
        }

        messageTitle = "";
        messageBody = "";

        if (waitingSummary) {
            waitingSummary = false;
            scene = Scene.INTERROGATION;
        }
    }

    public void closePuzzle() {
        puzzle = Puzzle.NONE;
    }

    public void showClue(String id) {
        if (collected.contains(id)) {
            for (Scene sc : ORDER) {
                for (WorldObject o : map.get(sc)) {
                    if (o.id().equals(id) && o instanceof Evidence e) {
                        openClue = id;
                        say(e.name(), e.description);
                        return;
                    }
                }
            }
        }
    }

    public boolean isNear(WorldObject o) {
        return Math.abs(o.x() - playerX)
                + Math.abs(o.y() - playerY) <= 2;
    }

    public WorldObject nearest() {
        WorldObject target = null;
        int distance = 99;

        for (WorldObject o : objects()) {

            if (o.kind() == Kind.CLUE && collected.contains(o.id())) {
                continue;
            }

            if (o.kind() == Kind.NPC && interviewed.contains(o.id())) {
                continue;
            }

            if (o.id().equals("crates") && crateSolved) {
                continue;
            }

            if (o.id().equals("drawer") && drawerSolved) {
                continue;
            }

            if (o.id().equals("cctv") && cameraSolved) {
                continue;
            }

            int d = Math.abs(o.x() - playerX)
                    + Math.abs(o.y() - playerY);

            if (d <= 2 && d < distance) {
                target = o;
                distance = d;
            }
        }

        return target;
    }

    public boolean canWalk(int x, int y) {
        return isWorldScene()
                && x >= 1
                && x <= 22
                && y >= 3
                && y <= 11;
    }

    void placeForTest(int x, int y) {
        playerX = x;
        playerY = y;
    }

    void previewScene(Scene s) {
        scene = s;
        playerX = 11;
        playerY = 7;
    }

    public void move(int dx, int dy) {
        if (!isWorldScene()
                || journal
                || puzzle != Puzzle.NONE
                || hasDialog()) {
            return;
        }

        int xx = playerX + dx;
        int yy = playerY + dy;

        if (canWalk(xx, yy)) {
            playerX = xx;
            playerY = yy;
        }
    }

    public void interact() {
        if (hasDialog()) {
            closeDialog();
            return;
        }

        if (puzzle != Puzzle.NONE || !isWorldScene()) {
            return;
        }

        if (journal) {
            journal = false;
            return;
        }

        WorldObject o = nearest();

        if (o == null) {
            say(
                    "สำรวจบริเวณนี้",
                    "เดินเข้าไปใกล้วัตถุที่มีประกาย แล้วกด SPACE หรือ E เพื่อสำรวจ"
            );
            return;
        }

        o.interact(this);
    }

    private long sceneClues(Scene scene) {
        return map.get(scene)
                .stream()
                .filter(o -> o.kind() == Kind.CLUE)
                .count();
    }

    private long foundIn(Scene scene) {
        return map.get(scene)
                .stream()
                .filter(o -> o.kind() == Kind.CLUE
                        && collected.contains(o.id()))
                .count();
    }

    private void pass(Passage p) {
        if (!p.backward) {

            if (foundIn(scene) < sceneClues(scene)) {
                say(
                        "ยังสำรวจไม่ครบ",
                        "พื้นที่นี้พบหลักฐาน "
                                + foundIn(scene)
                                + " / "
                                + sceneClues(scene)
                                + " ชิ้น สำรวจให้ครบก่อนผ่านประตู"
                );
                return;
            }

            if (scene == Scene.BEDROOM && !crateSolved) {
                say(
                        "บันไดยังถูกขวาง",
                        "ต้องแก้ปริศนาจัดลัง 3 ใบก่อนลงชั้นล่าง"
                );
                return;
            }

            if (scene == Scene.LIVING && !drawerSolved) {
                say(
                        "ต้องหากุญแจบ้านก่อน",
                        "ใบส่งของในห้องนั่งเล่นมีตัวเลขที่ใช้เปิดประตู"
                );
                return;
            }

            if (scene == Scene.LIVING && !cameraSolved) {
                say(
                        "ต้องตรวจกล้องก่อน",
                        "กล้องอยู่ในห้องนั่งเล่น ลองต่อสายไฟให้ภาพชัดขึ้น"
                );
                return;
            }
        }

        scene = p.destination;

        playerX = p.backward ? 20 : 4;
        playerY = 10;

        journal = false;
        openClue = null;

        if (!p.backward) {
            say(
                    sceneName(),
                    "สำรวจหลักฐานที่มีประกาย กด J เปิดสมุด F เปิดไฟฉาย และ SPACE / E เพื่อโต้ตอบ"
            );
        }
    }

    private void openPuzzle(Puzzle p) {
        if (p == Puzzle.CRATES && foundIn(Scene.BEDROOM) < 3) {
            say(
                    "ยังไม่ถึงเวลา",
                    "ตรวจสอบหลักฐานในห้องนอนทั้ง 3 ชิ้นก่อนย้ายกล่อง"
            );
            return;
        }

        if (p == Puzzle.DRAWER && !collected.contains("receipt")) {
            say(
                    "ไม่มีรหัส",
                    "หาใบส่งของที่ระบุเวลาก่อน"
            );
            return;
        }

        if (p == Puzzle.CAMERA && !collected.contains("camera")) {
            say(
                    "ตรวจกล้องก่อน",
                    "ตรวจสอบภาพจากกล้องที่ชำรุดก่อน"
            );
            return;
        }

        if (p == Puzzle.TIMELINE
                && (collected.size() != TOTAL_CLUES
                || interviewed.size() != 4)) {
            say(
                    "ข้อมูลยังไม่ครบ",
                    "ต้องเก็บหลักฐาน "
                            + collected.size()
                            + " / "
                            + TOTAL_CLUES
                            + " และสอบถามทั้ง 4 คนในบริเวณนี้ก่อนเริ่มเรียงเหตุการณ์"
            );
            return;
        }

        puzzle = p;
        codeInput = "";
    }

    public void placeCrate(int item, int target) {
        if (puzzle != Puzzle.CRATES
                || item < 0
                || item > 2
                || target < 0
                || target > 2) {
            return;
        }

        if (item == target) {
            cratePlaced[item] = true;

            if (cratePlaced[0] && cratePlaced[1] && cratePlaced[2]) {
                crateSolved = true;
                puzzle = Puzzle.NONE;

                say(
                        "เปิดทางสำเร็จ!",
                        "ลังทั้งสามถูกย้ายเข้าที่ ลูกบอลของฮีโร่กลิ้งลงบันไดไป... ตามลงไปกันเถอะ!"
                );
            }
        }
    }

    public void addDigit(char digit) {
        if (puzzle == Puzzle.DRAWER
                && Character.isDigit(digit)
                && codeInput.length() < 4) {
            codeInput += digit;
        }
    }

    public void backspaceDigit() {
        if (puzzle == Puzzle.DRAWER && !codeInput.isEmpty()) {
            codeInput = codeInput.substring(0, codeInput.length() - 1);
        }
    }

    public void submitCode() {
        if (puzzle != Puzzle.DRAWER) {
            return;
        }

        if ("0735".equals(codeInput)) {
            drawerSolved = true;
            puzzle = Puzzle.NONE;

            say(
                    "เจอกุญแจบ้าน!",
                    "รหัสจากใบส่งของ 07:35 เปิดประตูได้ สำหรับออกไปด้านนอก"
            );
        } else {
            codeInput = "";

            say(
                    "รหัสไม่ถูกต้อง",
                    "ลองตรวจดูตัวเลขเวลาในใบส่งของอีกครั้ง (ใช้ตัวเลข 4 หลัก)"
            );
        }
    }

    public void connectWire(int source, int destination) {
        if (puzzle != Puzzle.CAMERA
                || source < 0
                || source >= 3
                || destination < 0
                || destination >= 3) {
            return;
        }

        int[] matches = {2, 0, 1};

        if (matches[source] == destination) {
            wireConnected[source] = true;

            if (wireConnected[0]
                    && wireConnected[1]
                    && wireConnected[2]) {
                cameraSolved = true;
                puzzle = Puzzle.NONE;

                say(
                        "ภาพกล้องชัดขึ้น",
                        "ภาพบันทึกเผยว่าบุคคลที่ผ่านหน้าบ้านคือ รปภ. ซึ่งกำลังขับรถลาดตระเวน ไม่ใช่ผู้ลักพาตัว"
                );
            }
        } else {
            say(
                    "สายไฟไม่ตรงคู่",
                    "สีและวงจรยังไม่ถูกต้อง ลองเชื่อมขั้วอื่น"
            );
        }
    }

    public void chooseEvent(String key) {
        if (puzzle != Puzzle.TIMELINE
                || !EVENTS.containsKey(key)
                || timeline.contains(key)) {
            return;
        }

        if (key.equals(ORDER_CORRECT.get(timeline.size()))) {
            timeline.add(key);

            if (timeline.size() == ORDER_CORRECT.size()) {
                timelineSolved = true;
                puzzle = Puzzle.NONE;
                waitingSummary = true;

                say(
                        "The clues don't point to someone taking Hero…",
                        "They point to Hero leaving on his own. ตอนนี้คุณเข้าใจลำดับเหตุการณ์แล้ว กด SPACE เพื่อเข้าสู่ห้องสอบสวน"
                );
            }
        } else {
            timeline.clear();

            say(
                    "ลำดับยังไม่ถูกต้อง",
                    "ลองเรียงเหตุการณ์ตั้งแต่ฮีโร่อยู่ในบ้าน จนถึงตอนที่มันพบเพื่อนในซอยอีกครั้ง"
            );
        }
    }

    public void finalAnswer(int choice) {
        if (scene != Scene.INTERROGATION) {
            return;
        }

        if (choice == 4
                && timelineSolved
                && collected.size() == TOTAL_CLUES) {
            scene = Scene.TRUE_END;

            endingExplanation =
                    "Hero was never taken. He was just looking for his friend. "
                            + "MAX ไม่ได้ลักพาตัว แต่ช่วยดูแลฮีโร่จนคุณตามพบ";

        } else if (choice >= 1 && choice <= 4) {
            scene = Scene.BAD_END;

            endingExplanation =
                    "You couldn't find Hero… "
                            + "คุณด่วนสรุปผู้ต้องสงสัยผิดหรือยังไม่เชื่อมโยงหลักฐานให้ครบ";
        }

        journal = false;
        openClue = null;
        messageBody = "";
    }
}