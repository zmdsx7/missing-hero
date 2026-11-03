package com.missinghero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.text.BreakIterator;
import java.util.*;
import java.util.List;

/** Presentation/controller. Model is independent and tested without a GUI. */
public final class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    public static final int WIDTH = 1152, HEIGHT = 768;

    public final GameState state = new GameState();

    private final Set<Integer> keys = new HashSet<>();
    private final javax.swing.Timer timer;

    private long lastMove = 0, frame = 0;

    private final Font small = new Font("SansSerif", Font.PLAIN, 18),
            normal = new Font("SansSerif", Font.PLAIN, 22),
            bold = new Font("SansSerif", Font.BOLD, 25),
            large = new Font("SansSerif", Font.BOLD, 40);

    private static final Color INK = new Color(99, 65, 65),
            CREAM = new Color(255, 241, 188),
            WHITE = new Color(255, 250, 230),
            GOLD = new Color(236, 188, 129);

    private int selectedCrate = -1, selectedWire = -1;
    private int dragCrate = -1, dragX = -1, dragY = -1;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        timer = new javax.swing.Timer(40, e -> {
            frame++;
            drive();
            repaint();
        });

        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void drive() {
        if (!state.isWorldScene()
                || state.hasDialog()
                || state.puzzle() != GameState.Puzzle.NONE
                || state.journal()) {
            return;
        }

        if (System.nanoTime() - lastMove < 122_000_000L) {
            return;
        }

        int dx = 0, dy = 0;

        if (keys.contains(KeyEvent.VK_A) || keys.contains(KeyEvent.VK_LEFT)) {
            dx = -1;
        }

        if (keys.contains(KeyEvent.VK_D) || keys.contains(KeyEvent.VK_RIGHT)) {
            dx = 1;
        }

        if (keys.contains(KeyEvent.VK_W) || keys.contains(KeyEvent.VK_UP)) {
            dy = -1;
        }

        if (keys.contains(KeyEvent.VK_S) || keys.contains(KeyEvent.VK_DOWN)) {
            dy = 1;
        }

        if (dx != 0 || dy != 0) {
            state.move(dx, 0);

            if (dx == 0) {
                state.move(0, dy);
            }

            lastMove = System.nanoTime();
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        if (state.scene() == GameState.Scene.TITLE) {
            drawTitle(g);
        } else if (state.isWorldScene()) {
            drawWorld(g);
        } else if (state.scene() == GameState.Scene.INTERROGATION) {
            drawInterrogation(g);
        } else {
            drawEnding(g);
        }

        if (state.journal() && state.isWorldScene()) {
            drawJournal(g);
        }

        if (state.puzzle() != GameState.Puzzle.NONE) {
            drawPuzzle(g);
        }

        if (state.hasDialog()) {
            if (state.openClue() != null) {
                drawEvidencePopup(g);
            } else {
                drawMessage(g);
            }
        }

        g.dispose();
    }

    private void rect(Graphics2D g, int x, int y, int w, int h, Color color) {
        g.setColor(color);
        g.fillRect(x, y, w, h);
    }

    private void box(Graphics2D g, int x, int y, int w, int h, Color fill) {
        g.setColor(new Color(85, 68, 66, 90));
        g.fillRoundRect(x + 5, y + 7, w, h, 13, 13);

        g.setColor(fill);
        g.fillRoundRect(x, y, w, h, 13, 13);

        g.setStroke(new BasicStroke(3));
        g.setColor(INK);
        g.drawRoundRect(x, y, w, h, 13, 13);
    }

    private void label(Graphics2D g, String s, int x, int y, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(s, x, y);
    }

    private void centered(Graphics2D g, String s, int cx, int y, Font font, Color color) {
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();

        label(g, s, cx - fm.stringWidth(s) / 2, y, font, color);
    }

    private void button(Graphics2D g, String title, int x, int y, int w, int h) {
        box(g, x, y, w, h, new Color(246, 210, 163));

        centered(
                g,
                title,
                x + w / 2,
                y + (h + g.getFontMetrics(bold).getAscent()) / 2 - 4,
                bold,
                INK
        );
    }

    private void veil(Graphics2D g) {
        g.setColor(new Color(23, 20, 38, 174));
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void lines(
            Graphics2D g,
            String content,
            int x,
            int y,
            int width,
            int lineHeight,
            int maxLines,
            Font font,
            Color color
    ) {
        g.setFont(font);
        g.setColor(color);

        FontMetrics fm = g.getFontMetrics();

        BreakIterator b = BreakIterator.getLineInstance(Locale.forLanguageTag("th-TH"));
        b.setText(content);

        List<String> rows = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (int i = b.first(), j = b.next();
             j != BreakIterator.DONE;
             i = j, j = b.next()) {

            String token = content.substring(i, j);

            if (fm.stringWidth(sb + token) > width && sb.length() > 0) {
                rows.add(sb.toString().strip());
                sb.setLength(0);
            }

            if (fm.stringWidth(token) > width) {
                for (char ch : token.toCharArray()) {
                    if (fm.stringWidth(sb.toString() + ch) > width && sb.length() > 0) {
                        rows.add(sb.toString().strip());
                        sb.setLength(0);
                    }

                    sb.append(ch);
                }
            } else {
                sb.append(token);
            }
        }

        if (sb.length() > 0) {
            rows.add(sb.toString().strip());
        }

        for (int i = 0; i < Math.min(maxLines, rows.size()); i++) {
            g.drawString(rows.get(i), x, y + i * lineHeight);
        }
    }

    private void drawTitle(Graphics2D g) {
        Art.image(g, "bedroom", 0, 0, WIDTH, HEIGHT);

        veil(g);
        box(g, 170, 106, 812, 560, WHITE);

        centered(
                g,
                "MISSING HERO",
                576,
                204,
                new Font("SansSerif", Font.BOLD, 67),
                INK
        );

        centered(
                g,
                "เกมตามหาสุนัขที่หายไป",
                576,
                259,
                large,
                new Color(168, 99, 92)
        );

        Art.image(g, "suspects", 367, 293, 418, 261);

        button(g, "เริ่มเกม  /  ENTER", 361, 575, 429, 67);

        centered(
                g,
                "Project OOP",
                576,
                682,
                small,
                WHITE
        );
    }

    private static String assetFor(GameState.Scene s) {
        return switch (s) {
            case BEDROOM -> "bedroom";
            case LIVING -> "living";
            case NEIGHBOR -> "neighbor";
            default -> "bedroom";
        };
    }

    private static String actorFor(String id) {
        return switch (id) {
            case "child" -> "child";
            case "courier" -> "courier";
            case "guard" -> "guard";
            default -> "max";
        };
    }

    private void drawWorld(Graphics2D g) {
        Art.image(g, assetFor(state.scene()), 0, 0, WIDTH, HEIGHT);

        for (GameState.WorldObject spot : state.objects()) {
            if (spot.kind() == GameState.Kind.CLUE
                    && state.collected().contains(spot.id())) {
                continue;
            }

            if (spot.kind() == GameState.Kind.PUZZLE
                    && ((spot.id().equals("crates") && state.crateSolved())
                    || (spot.id().equals("drawer") && state.drawerSolved())
                    || (spot.id().equals("cctv") && state.cameraSolved()))) {
                continue;
            }

            int px = spot.x() * 48 + 7;
            int py = 96 + spot.y() * 48 - 12;

            switch (spot.kind()) {
                case CLUE -> Art.image(
                        g,
                        "item_" + spot.id(),
                        px - 9,
                        py - 12,
                        50,
                        47
                );

                case NPC -> {
                    int width = 76;
                    int height = 88;

                    if (spot.id().equals("max")) {
                        width = 86;
                        height = 80;
                    }

                    int centerX = spot.x() * 48 + 40;
                    int bottomY = 96 + spot.y() * 48 + 21;

                    Art.image(
                            g,
                            "npc_" + actorFor(spot.id()),
                            centerX - width / 2,
                            bottomY - height,
                            width,
                            height
                    );

                    centered(
                            g,
                            spot.name(),
                            centerX,
                            bottomY - height - 10,
                            new Font("SansSerif", Font.BOLD, 14),
                            WHITE
                    );
                }

                case EXIT -> {
                    box(g, px - 7, py - 9, 42, 39, new Color(246, 201, 142));

                    centered(
                            g,
                            spot.id().equals("back") ? "<" : ">",
                            px + 14,
                            py + 19,
                            bold,
                            INK
                    );
                }

                case PUZZLE -> {
                    box(g, px - 7, py - 10, 45, 40, new Color(216, 174, 138));

                    centered(g, "?", px + 15, py + 21, bold, INK);
                }
            }

            if (frame % 32 < 22 && spot.kind() == GameState.Kind.CLUE) {
                rect(g, px + 3, py - 8, 5, 5, GOLD);
                rect(g, px + 31, py + 6, 4, 4, GOLD);
            }
        }

        int cx = state.playerX() * 48 + 14;
        int cy = 96 + state.playerY() * 48 - 26;

        Art.image(g, "npc_player", cx - 12, cy - 41, 76, 88);

        drawDarkness(g, cx + 24, cy + 17);

        rect(g, 0, 0, WIDTH, 94, new Color(255, 239, 192, 245));
        rect(g, 0, 91, WIDTH, 3, INK);

        label(g, "MISSING HERO", 23, 36, bold, INK);
        label(g, state.sceneName(), 23, 75, normal, INK);
        label(g, "Stage " + state.stage() + "/4", 405, 43, bold, INK);
        label(g, "หลักฐาน " + state.collected().size() + " / 13", 567, 43, bold, INK);
        label(g, "คนที่สอบถาม " + state.interviewed().size() + "/4", 824, 43, normal, INK);

        rect(g, 0, 657, WIDTH, 111, new Color(255, 242, 198, 248));
        rect(g, 0, 654, WIDTH, 3, INK);

        GameState.WorldObject near = state.nearest();

        lines(
                g,
                near == null
                        ? "เดินสำรวจหาหลักฐานที่มีประกาย"
                        : "กด SPACE / E · " + near.name(),
                24,
                701,
                WIDTH - 55,
                28,
                2,
                normal,
                INK
        );

        label(
                g,
                "WASD เดิน    F ไฟฉาย "
                        + (state.flashlight() ? "ON" : "OFF")
                        + "    J สมุดหลักฐาน    ESC ปิดหน้าต่าง",
                25,
                750,
                small,
                INK
        );
    }

    private void drawDarkness(Graphics2D g, int cx, int cy) {
        Shape old = g.getClip();

        g.clipRect(0, 96, WIDTH, 560);

        float radius = state.flashlight()
                ? (state.scene() == GameState.Scene.NEIGHBOR ? 232f : 266f)
                : 130f;

        Color[] colors = state.flashlight()
                ? new Color[]{
                        new Color(12, 17, 37, 7),
                        new Color(14, 18, 33, 54),
                        new Color(12, 16, 35, 192),
                        new Color(8, 12, 27, 250)
                }
                : new Color[]{
                        new Color(13, 16, 31, 80),
                        new Color(12, 16, 35, 172),
                        new Color(12, 15, 30, 241),
                        new Color(8, 11, 22, 254)
                };

        g.setPaint(
                new RadialGradientPaint(
                        new Point2D.Float(cx, cy),
                        radius,
                        new float[]{0f, .38f, .75f, 1f},
                        colors
                )
        );

        g.fillRect(0, 96, WIDTH, 560);
        g.setClip(old);
    }

    private void drawMessage(Graphics2D g) {
        box(g, 74, 480, 1004, 251, WHITE);

        label(g, "✦  " + state.messageTitle(), 103, 530, bold, INK);

        lines(
                g,
                state.messageBody(),
                104,
                582,
                936,
                35,
                3,
                normal,
                INK
        );

        button(g, "ปิด / SPACE / E", 764, 657, 270, 51);
    }

    private void drawEvidencePopup(Graphics2D g) {
        veil(g);
        box(g, 75, 114, 1000, 540, WHITE);

        label(g, "หลักฐานที่ค้นพบ   ✦", 111, 162, large, INK);

        Art.image(g, "clue_" + state.openClue(), 113, 193, 420, 337);

        label(g, state.messageTitle(), 556, 236, bold, INK);

        lines(
                g,
                state.messageBody(),
                556,
                288,
                459,
                33,
                7,
                normal,
                INK
        );

        button(g, "เก็บเข้ากระเป๋า / ปิด", 628, 547, 354, 65);

        label(g, "กด SPACE, E, ENTER หรือคลิกปิด", 122, 626, small, INK);
    }

    private void drawJournal(Graphics2D g) {
        veil(g);
        box(g, 52, 109, 1048, 576, WHITE);

        label(
                g,
                "สมุดรวบรวมหลักฐาน   " + state.collected().size() + " / 13",
                79,
                160,
                large,
                INK
        );

        label(
                g,
                "คลิกหลักฐานที่เก็บได้เพื่อขยายภาพ    ·    J / ESC ปิด",
                80,
                192,
                small,
                INK
        );

        int i = 0;

        for (var item : GameState.CLUES.entrySet()) {
            int col = i % 4;
            int row = i / 4;
            int x = 77 + col * 255;
            int y = 212 + row * 82;

            boolean has = state.collected().contains(item.getKey());

            box(
                    g,
                    x,
                    y,
                    244,
                    72,
                    has ? new Color(222, 238, 205) : new Color(224, 211, 196)
            );

            if (has) {
                Art.image(g, "clue_" + item.getKey(), x + 7, y + 7, 56, 54);
            }

            lines(
                    g,
                    (has ? "" : "?  ") + item.getValue(),
                    x + 70,
                    y + 28,
                    163,
                    21,
                    2,
                    new Font("SansSerif", Font.PLAIN, 17),
                    INK
            );

            i++;
        }
    }

    private void drawPuzzle(Graphics2D g) {
        veil(g);
        box(g, 135, 123, 881, 548, WHITE);

        GameState.Puzzle p = state.puzzle();

        if (p == GameState.Puzzle.CRATES) {
            drawCratePuzzle(g);
        }

        if (p == GameState.Puzzle.DRAWER) {
            drawDrawerPuzzle(g);
        }

        if (p == GameState.Puzzle.CAMERA) {
            drawCameraPuzzle(g);
        }

        if (p == GameState.Puzzle.TIMELINE) {
            drawTimelinePuzzle(g);
        }

        label(g, "ESC ปิดปริศนา", 160, 644, small, INK);
    }

    private void drawCratePuzzle(Graphics2D g) {
        label(g, "Puzzle 1 · จัดลังเพื่อเปิดทางลงบันได", 165, 174, bold, INK);

        label(
                g,
                "ลากลัง หรือคลิกเลือกแล้วคลิกช่องสัญลักษณ์เดียวกัน",
                165,
                205,
                normal,
                INK
        );

        String[] names = {"☀", "★", "♥"};

        for (int i = 0; i < 3; i++) {
            if (!state.crates()[i]) {
                int x = 248 + i * 220;
                int y = 265;

                box(g, x, y, 125, 104, new Color(208, 159, 109));

                centered(g, names[i], x + 62, y + 65, large, WHITE);
            }

            int x = 248 + i * 220;
            int y = 457;

            box(
                    g,
                    x,
                    y,
                    125,
                    107,
                    state.crates()[i]
                            ? new Color(151, 195, 141)
                            : new Color(240, 218, 179)
            );

            centered(g, names[i], x + 62, y + 66, large, INK);
        }

        if (dragCrate >= 0 && dragX >= 0) {
            box(g, dragX - 45, dragY - 35, 90, 70, new Color(210, 161, 113));

            centered(
                    g,
                    names[dragCrate],
                    dragX,
                    dragY + 9,
                    bold,
                    INK
            );
        }

        label(g, "ย้ายครบทั้ง 3 ลัง แล้วจะเดินลงไปชั้นล่างได้", 244, 605, small, INK);
    }

    private void drawDrawerPuzzle(Graphics2D g) {
        label(g, "Puzzle 2 · ลิ้นชักรหัส 4 หลัก", 170, 175, bold, INK);

        label(
                g,
                "ใบส่งของบอกเวลาอะไร? ใส่เป็นตัวเลข 4 หลัก",
                170,
                207,
                normal,
                INK
        );

        box(g, 379, 229, 400, 76, new Color(237, 217, 183));

        centered(
                g,
                (state.codeInput() + "____").substring(0, 4),
                580,
                281,
                large,
                INK
        );

        for (int i = 0; i < 12; i++) {
            int col = i % 3;
            int row = i / 3;
            int x = 380 + col * 141;
            int y = 330 + row * 57;

            String text = i == 9
                    ? "ลบ"
                    : i == 10
                            ? "0"
                            : i == 11
                                    ? "ตกลง"
                                    : Integer.toString(i + 1);

            box(g, x, y, 120, 48, new Color(235, 204, 156));

            centered(g, text, x + 60, y + 31, normal, INK);
        }
    }

    private void drawCameraPuzzle(Graphics2D g) {
        label(g, "Puzzle 3 · ซ่อมสายไฟกล้องวงจรปิด", 169, 177, bold, INK);

        label(
                g,
                "คลิกสายด้านซ้าย แล้วคลิกขั้วด้านขวาที่เชื่อมกันถูกต้อง",
                169,
                211,
                normal,
                INK
        );

        Color[] colors = {
                new Color(224, 111, 106),
                new Color(122, 171, 216),
                new Color(142, 180, 116)
        };

        String[] left = {"แดง", "ฟ้า", "เขียว"};
        String[] right = {"ฟ้า", "เขียว", "แดง"};
        int[] pair = {2, 0, 1};

        for (int i = 0; i < 3; i++) {
            int y = 298 + i * 92;

            box(
                    g,
                    239,
                    y,
                    155,
                    58,
                    selectedWire == i ? CREAM : new Color(235, 215, 183)
            );

            label(g, left[i], 278, y + 37, bold, colors[i]);

            box(g, 743, y, 158, 58, new Color(235, 215, 183));

            label(g, right[i], 789, y + 37, bold, colors[pairIndex(i)]);
        }

        for (int i = 0; i < 3; i++) {
            if (state.wires()[i]) {
                g.setStroke(new BasicStroke(6));
                g.setColor(colors[i]);

                g.drawLine(
                        395,
                        327 + i * 92,
                        741,
                        327 + pair[i] * 92
                );
            }
        }
    }

    private static int pairIndex(int right) {
        return switch (right) {
            case 0 -> 1;
            case 1 -> 2;
            default -> 0;
        };
    }

    private void drawTimelinePuzzle(Graphics2D g) {
        label(g, "Final Puzzle · เรียงลำดับเหตุการณ์", 162, 174, bold, INK);

        label(
                g,
                "เลือกเหตุการณ์ตามลำดับเวลา ตั้งแต่ฮีโร่ยังอยู่ในบ้านจนพบ MAX",
                162,
                209,
                normal,
                INK
        );

        int i = 0;

        for (var entry : state.events().entrySet()) {
            int col = i % 2;
            int row = i / 2;
            int x = 170 + col * 409;
            int y = 254 + row * 101;

            box(
                    g,
                    x,
                    y,
                    384,
                    78,
                    state.timeline().contains(entry.getKey())
                            ? new Color(179, 218, 176)
                            : new Color(237, 218, 179)
            );

            lines(
                    g,
                    entry.getValue(),
                    x + 16,
                    y + 34,
                    348,
                    25,
                    2,
                    normal,
                    INK
            );

            i++;
        }

        label(
                g,
                "เรียงถูกแล้ว " + state.timeline().size() + " / 6 เหตุการณ์",
                179,
                609,
                bold,
                INK
        );
    }

    private void drawInterrogation(Graphics2D g) {
        Art.image(g, "neighbor", 0, 0, WIDTH, HEIGHT);

        veil(g);
        box(g, 120, 83, 909, 606, WHITE);

        label(g, "Stage 4 · ห้องสอบสวน", 157, 149, large, INK);

        lines(
                g,
                "จากหลักฐานทั้งหมด ใครเป็นผู้ที่พบและดูแล Hero หลังจากมันออกจากบ้าน?",
                156,
                194,
                833,
                30,
                2,
                normal,
                INK
        );

        String[] opts = {
                "1 · เด็กข้างบ้านพา Hero ไป",
                "2 · พนักงานส่งของลักพาตัว",
                "3 · รปภ. จับ Hero ไป",
                "4 · MAX เพื่อนสุนัขที่ช่วยดูแล Hero"
        };

        for (int i = 0; i < 4; i++) {
            box(
                    g,
                    164,
                    270 + i * 86,
                    827,
                    72,
                    new Color(
                            i == 3 ? 219 : 245,
                            i == 3 ? 232 : 211,
                            i == 3 ? 200 : 175
                    )
            );

            label(g, opts[i], 190, 318 + i * 86, bold, INK);
        }

        label(g, "คลิกตัวเลือก หรือกดเลข 1–4", 170, 655, normal, INK);
    }

    private void drawEnding(Graphics2D g) {
        rect(g, 0, 0, WIDTH, HEIGHT, CREAM);

        boolean win = state.scene() == GameState.Scene.TRUE_END;

        box(g, 152, 63, 848, 655, WHITE);

        Art.image(g, win ? "ending_true" : "ending_bad", 269, 128, 614, 384);

        centered(
                g,
                win ? "TRUE ENDING · FOUND HERO" : "BAD ENDING · LOST FOREVER",
                576,
                565,
                bold,
                INK
        );

        lines(
                g,
                state.endingExplanation(),
                208,
                600,
                740,
                26,
                2,
                small,
                INK
        );

        button(g, "เริ่มใหม่ · ENTER / R", 346, 654, 459, 51);
    }

    @Override
    public void keyPressed(KeyEvent ev) {
        int k = ev.getKeyCode();

        keys.add(k);

        if (k == KeyEvent.VK_ESCAPE) {
            if (state.hasDialog()) {
                state.closeDialog();
            } else if (state.puzzle() != GameState.Puzzle.NONE) {
                state.closePuzzle();
            } else if (state.journal()) {
                state.closeJournal();
            }

            repaint();
            return;
        }

        if (state.hasDialog()) {
            if (k == KeyEvent.VK_SPACE
                    || k == KeyEvent.VK_E
                    || k == KeyEvent.VK_ENTER) {
                state.closeDialog();
            }

            repaint();
            return;
        }

        if (state.puzzle() == GameState.Puzzle.DRAWER) {
            if (k >= KeyEvent.VK_0 && k <= KeyEvent.VK_9) {
                state.addDigit((char) ('0' + k - KeyEvent.VK_0));
            }

            if (k == KeyEvent.VK_BACK_SPACE) {
                state.backspaceDigit();
            }

            if (k == KeyEvent.VK_ENTER) {
                state.submitCode();
            }

            repaint();
            return;
        }

        if (state.puzzle() != GameState.Puzzle.NONE) {
            return;
        }

        if (k == KeyEvent.VK_ENTER
                || k == KeyEvent.VK_SPACE
                || k == KeyEvent.VK_E) {
            if (state.scene() == GameState.Scene.TITLE) {
                state.start();
            } else if (state.isWorldScene()) {
                state.interact();
            } else if (state.scene() == GameState.Scene.TRUE_END
                    || state.scene() == GameState.Scene.BAD_END) {
                state.start();
            }
        }

        if (k == KeyEvent.VK_J && state.isWorldScene()) {
            state.toggleJournal();
        }

        if (k == KeyEvent.VK_F && state.isWorldScene()) {
            state.toggleFlashlight();
        }

        if (k == KeyEvent.VK_R
                && (state.scene() == GameState.Scene.TRUE_END
                || state.scene() == GameState.Scene.BAD_END)) {
            state.start();
        }

        if (k >= KeyEvent.VK_1
                && k <= KeyEvent.VK_4
                && state.scene() == GameState.Scene.INTERROGATION) {
            state.finalAnswer(k - KeyEvent.VK_0);
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (state.puzzle() == GameState.Puzzle.CRATES) {
            for (int i = 0; i < 3; i++) {
                if (!state.crates()[i]
                        && hit(e, 248 + i * 220, 265, 125, 104)) {
                    dragCrate = i;
                    selectedCrate = i;
                    dragX = e.getX();
                    dragY = e.getY();
                    break;
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragCrate >= 0) {
            dragX = e.getX();
            dragY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragCrate >= 0) {
            for (int i = 0; i < 3; i++) {
                if (hit(e, 248 + i * 220, 457, 125, 107)) {
                    state.placeCrate(dragCrate, i);
                }
            }

            dragCrate = -1;
            dragX = -1;
            dragY = -1;

            repaint();
        }
    }

    private static boolean hit(MouseEvent e, int x, int y, int w, int h) {
        return e.getX() >= x
                && e.getX() < x + w
                && e.getY() >= y
                && e.getY() < y + h;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (state.hasDialog()) {
            if (state.openClue() != null
                    ? hit(e, 628, 547, 354, 65)
                    : hit(e, 764, 657, 270, 51)) {
                state.closeDialog();
            }

            repaint();
            requestFocusInWindow();
            return;
        }

        GameState.Puzzle puzzle = state.puzzle();

        if (puzzle != GameState.Puzzle.NONE) {
            if (puzzle == GameState.Puzzle.CRATES) {
                for (int i = 0; i < 3; i++) {
                    if (!state.crates()[i]
                            && hit(e, 248 + i * 220, 265, 125, 104)) {
                        selectedCrate = i;
                    }

                    if (selectedCrate >= 0
                            && hit(e, 248 + i * 220, 457, 125, 107)) {
                        state.placeCrate(selectedCrate, i);
                        selectedCrate = -1;
                    }
                }
            } else if (puzzle == GameState.Puzzle.DRAWER) {
                for (int i = 0; i < 12; i++) {
                    if (hit(e, 380 + (i % 3) * 141, 330 + (i / 3) * 57, 120, 48)) {
                        if (i == 9) {
                            state.backspaceDigit();
                        } else if (i == 11) {
                            state.submitCode();
                        } else {
                            state.addDigit((char) ('0' + (i == 10 ? 0 : i + 1)));
                        }
                    }
                }
            } else if (puzzle == GameState.Puzzle.CAMERA) {
                for (int i = 0; i < 3; i++) {
                    if (hit(e, 239, 298 + i * 92, 155, 58)) {
                        selectedWire = i;
                    }

                    if (selectedWire >= 0
                            && hit(e, 743, 298 + i * 92, 158, 58)) {
                        state.connectWire(selectedWire, i);
                        selectedWire = -1;
                    }
                }
            } else if (puzzle == GameState.Puzzle.TIMELINE) {
                int i = 0;

                for (var entry : state.events().entrySet()) {
                    if (hit(e, 170 + (i % 2) * 409, 254 + (i / 2) * 101, 384, 78)) {
                        state.chooseEvent(entry.getKey());
                        break;
                    }

                    i++;
                }
            }
        } else if (state.journal()) {
            int i = 0;

            for (String clue : GameState.CLUES.keySet()) {
                if (hit(e, 77 + (i % 4) * 255, 212 + (i / 4) * 82, 244, 72)) {
                    state.closeJournal();
                    state.showClue(clue);
                    break;
                }

                i++;
            }
        } else if (state.scene() == GameState.Scene.TITLE) {
            if (hit(e, 361, 575, 429, 67)) {
                state.start();
            }
        } else if (state.scene() == GameState.Scene.INTERROGATION) {
            for (int i = 0; i < 4; i++) {
                if (hit(e, 164, 270 + i * 86, 827, 72)) {
                    state.finalAnswer(i + 1);
                    break;
                }
            }
        } else if (state.scene() == GameState.Scene.TRUE_END
                || state.scene() == GameState.Scene.BAD_END) {
            if (hit(e, 346, 654, 459, 51)) {
                state.start();
            }
        }

        repaint();
        requestFocusInWindow();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}