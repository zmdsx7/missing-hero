# Missing Hero — A Pixel Mystery

เกมสืบสวน 2D Pixel Art ที่พัฒนาด้วย **Java + Swing + OOP** โดยใช้ภาพ Pixel Art ที่วาดด้วย Java2D ภายในโปรแกรมเอง ไม่ต้องดาวน์โหลดภาพหรือไลบรารีภายนอก

## เรื่องย่อ

คืนหนึ่ง Hero สุนัขที่รักหายตัวไปอย่างลึกลับ คุณต้องค้นหาความจริงจากหลักฐานในห้องนอน ชั้นล่าง และหมู่บ้าน สอบถามผู้ต้องสงสัย 4 ราย แล้วตัดสินใจว่าอะไรเกิดขึ้นกับ Hero เรื่องราวประกอบด้วย **True Ending** และ **Bad Ending**

แนวคิดของเกมพัฒนาจากเอกสาร *Missing Hero.pdf* และ *Proposal Where Is My Dog* ของผู้จัดทำ: เปิดเผยเรื่องราวผ่านการสำรวจและการเชื่อมโยงหลักฐาน โดยผู้เล่นต้องไม่รีบด่วนกล่าวหาใคร

## ดาวน์โหลด / วิธีเล่น

**ต้องมี Java Runtime 17 หรือสูงกว่า** (แนะนำ Java 21) ทดสอบคอมไพล์กับ OpenJDK 21 โดยกำหนด bytecode Java 17

เปิด Terminal / CMD ในโฟลเดอร์ที่มี `MissingHero.jar` แล้วพิมพ์:

```bash
java -jar MissingHero.jar
```

**Mac:** ดับเบิลคลิก `Play-Mac.command` (ครั้งแรกอาจต้องคลิกขวา > Open หรือเปิด Terminal ด้วยคำสั่งด้านบน)

**Windows:** ดับเบิลคลิก `Play-Windows.bat` (ต้องติดตั้ง Java และตั้ง PATH ก่อน)

**ไม่ต้องต่ออินเทอร์เน็ตหลังโหลดไฟล์เกม** และไม่ต้องเปิด VS Code หรือเซิร์ฟเวอร์

## การควบคุม

| ปุ่ม | การทำงาน |
|---|---|
| `W A S D` หรือ `ลูกศร` | เดินในแผนที่ |
| `SPACE` หรือ `E` | เก็บหลักฐาน, อ่านข้อมูล, สนทนา, เข้าประตู, ปิดกล่องข้อความ |
| `F` | เปิด/ปิดไฟฉายในบริเวณกลางคืน |
| `J` | เปิด/ปิดสมุดหลักฐาน |
| `1`–`5` | เลือกคำตอบระหว่างสรุปคดี |
| `R` | เริ่มใหม่เมื่อถึงตอนจบ |
| `ESC` | ปิดสมุด/ข้อความ |

## โครงสร้างเรื่องและเป้าหมาย

1. **Bedroom:** เก็บหลักฐานอย่างน้อย 2 ชิ้น แล้วลงบันได
2. **First Floor:** เก็บหลักฐานรวมอย่างน้อย 4 ชิ้น แล้วออกประตูหน้าบ้าน
3. **Outside:** เก็บหลักฐานรวมอย่างน้อย 7 จาก 10 ชิ้น และสอบถามทั้ง 4 ราย เข้าไปที่กระดานสรุปคดี
4. **The Truth:** เลือกข้อสรุปของตัวเอง จากนั้นเลือกชุดหลักฐานที่สมเหตุสมผล — ถ้าสรุปว่ามีการลักพาตัวโดยไม่มีหลักฐานจะพบ Bad Ending

> ใบ้ปริศนา: ใน True Ending ต้องมีข้อมูลจาก `รูปถ่าย`, `หน้าต่างเปิด`, `รอยเท้า`, และ `ป้ายชื่อ MAX`

## โครงสร้างโปรเจกต์

```text
src/main/java/com/missinghero/
  Main.java        - เปิดหน้าต่าง Java Swing และสร้างภาพตัวอย่าง
  GamePanel.java   - GUI, keyboard/mouse controls, journal, dialogue, puzzles
  GameState.java   - เกม/ฉาก, ตัวละคร, หลักฐาน, ประตู, เงื่อนไขตอนจบ
  PixelWorld.java  - วาดฉากและสไปรต์ Pixel Art ด้วย Java2D
src/test/java/com/missinghero/
  LogicSmokeTest.java - ทดสอบ flow และ endings โดยไม่ใช้ไลบรารีทดสอบ
(ภาพ screenshots อยู่ในไฟล์ ZIP ส่งงานแยกต่างหาก)
pom.xml              - Maven project (Java 17 bytecode)
build.sh / build.bat - สคริปต์ build แบบไม่ต้องใช้ Maven
```

## สร้าง JAR ใหม่จาก Source Code

### วิธีที่ 1: Java SDK (ไม่ต้องใช้ Maven)

บน Mac / Linux:

```bash
bash build.sh
java -jar MissingHero.jar
```

บน Windows:

```bat
build.bat
java -jar MissingHero.jar
```

### วิธีที่ 2: Maven

```bash
mvn clean package
java -jar target/missing-hero-1.0.0.jar
```

## ทดสอบ logic

```bash
bash build.sh
mkdir -p out-test
javac --release 17 -encoding UTF-8 -cp out -d out-test src/test/java/com/missinghero/LogicSmokeTest.java
java -ea -cp out:out-test com.missinghero.LogicSmokeTest
```

## GitHub Repository

Source Code ที่อัปโหลดขึ้น GitHub: https://github.com/zmdsx7/missing-hero

หาก Clone จาก GitHub ให้รัน `bash build.sh` (macOS / Linux) หรือ `build.bat` (Windows) เพื่อสร้างไฟล์ `MissingHero.jar` ก่อนเปิดเกม โดยไฟล์เกมที่คอมไพล์แล้วส่งแยกเป็น JAR ตามข้อกำหนดของงาน

ภาพหน้าจอสำหรับตัวอย่างอยู่ในชุดไฟล์ส่งงานที่จัดเตรียมแยกต่างหาก

## ขอบเขตการพัฒนา

เกมนี้เป็น **เวอร์ชันเล่นได้จริง (single-player prototype)** ที่ประกอบด้วยสามฉากเดินสำรวจ, กลไกไฟฉายและสมุดหลักฐาน, บทสนทนาผู้ต้องสงสัย, กระดานสรุปคดี และตอนจบสองแบบ โดยเป็น Pixel Art ต้นฉบับซึ่งวาดด้วยโค้ด Java2D ไม่ใช่การนำภาพตัวอย่างจากเอกสารมาใช้เป็นเกมแอสเซตโดยตรง

ไม่มีระบบเซฟเกม เสียงประกอบ หรือเกมหลายผู้เล่นในเวอร์ชันนี้