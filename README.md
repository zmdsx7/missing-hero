# Missing Hero

เกมสืบสวน 2D Pixel Art ใช้ **Java 17+ / Java Swing / Object-Oriented Programming**

## แผนที่ใหม่ (3 ฉากสำรวจ / 4 Stage)

1. **ห้องนอนชั้น 2:**
2. **ห้องนั่งเล่นชั้น 1:**
3. **บริเวณบ้านเพื่อนบ้าน / นอกบ้าน:**
4. **ห้องสอบสวน:** เลือกคำตอบจากหลักฐานและเข้าสู่ True Ending / Bad Ending

> เงื่อนไขการผ่าน: ห้องนอนต้องเก็บ 3 ชิ้นและจัดลังสำเร็จ; ห้องนั่งเล่นต้องเก็บ 6 ชิ้น เปิดลิ้นชัก และซ่อม CCTV; นอกบ้านต้องเก็บ 4 ชิ้น และสอบถามทั้งสี่ก่อนเรียงเหตุการณ์

## วิธีเปิดเล่นบน Mac/Windows

ติดตั้ง Java 17+ แล้วเปิด Terminal/CMD ในโฟลเดอร์ที่มี `MissingHero.jar` จากนั้นรัน

```bash
java -jar MissingHero.jar
```

หาก macOS แจ้งข้อจำกัดการเปิดไฟล์ที่ไม่ได้ลงนาม สามารถเปิดด้วย Terminal หลังจากตรวจสอบแหล่งที่มาของโค้ดแล้ว

## ปุ่มควบคุม

| ปุ่ม | ทำอะไร |
|---|---|
| WASD / ลูกศร | เดิน |
| SPACE / E | สำรวจ เก็บหลักฐาน พูดคุย และผ่านทางออก |
| F | เปิด/ปิดไฟฉาย บางหลักฐานต้องเปิดไฟฉายจึงเก็บได้ |
| J | สมุดหลักฐาน คลิกดูรูปขยาย |
| ESC | ปิดข้อความ ปริศนา สมุด |
| Mouse | จัดลัง ต่อสายไฟ กดปุ่ม และเรียงเหตุการณ์ |
| 0–9, Backspace, Enter | ป้อนรหัสลิ้นชัก |
| 1–4 | ตอบคำถามในห้องสอบสวน |
| R / Enter | เริ่มใหม่หลังฉากจบ |

เมื่อจะไปฉากถัดไป ให้สำรวจจนผ่านเงื่อนไข แล้วเดินไปที่เครื่องหมาย `>` บริเวณขวาล่างของแผนที่; ย้อนกลับที่ `<` ด้านซ้ายล่าง

## ภาพ

ไฟล์ PNG ทั้งหมดที่เกมใช้จริงอยู่ใน `src/main/resources/art/`:
- แผนที่: `bedroom.png`, `living.png`, `neighbor.png` (384 × 256 px)
- หลักฐานบนพื้น: `item_<id>.png` รวม 13 ภาพ (PNG พื้นหลังโปร่งใส ไม่มีกรอบกระดาษ)
- หลักฐานป๊อปอัป: `clue_<id>.png` รวม 13 ภาพ (คงกรอบกระดาษและคำอธิบาย)
- ตัวละคร: `npc_player.png`, `npc_child.png`, `npc_courier.png`, `npc_guard.png`, `npc_max.png`
- ภาพฉากจบ: `ending_true.png`, `ending_bad.png`

## โครงสร้าง OOP

- `GameState.java` กฎเกมและสถานะ: **Encapsulation** (private state + accessors)
- `GameState.WorldObject` คลาสนามธรรม: **Abstraction**
- `Evidence`, `Suspect`, `Passage`, `Device` สืบทอดจาก WorldObject: **Inheritance**
- `WorldObject.interact(GameState)` ที่แต่ละชนิด override: **Polymorphism**
- `GamePanel.java` หน้าต่าง Swing + UI + ปริศนา + วงแสง
- `Art.java` ระบบดึงและแคช PNG จาก resources ภายใน JAR
- `Main.java` จุดเริ่มต้นและคำสั่งสร้างภาพหน้าจอเพื่อทดสอบ

## Build

macOS/Linux:
```bash
bash build.sh
java -jar MissingHero.jar
```

Windows:
```bat
build.bat
java -jar MissingHero.jar
```

Maven:
```bash
mvn clean package
java -jar target/missing-hero-1.0.0.jar
```

## ทดสอบ

```bash
bash build.sh
mkdir -p out-test
javac --release 17 -encoding UTF-8 -cp out -d out-test src/test/java/com/missinghero/LogicSmokeTest.java
java -ea -cp out:out-test com.missinghero.LogicSmokeTest
```

## ที่มา

เนื้อเรื่องและการหักมุมจากต้นฉบับ โดยเลือกใช้หลักฐาน 13 ชิ้นจากชุดเดิม 19 ชิ้นดัดแปลงจากไฟล์ "เกมตามหาสุนัขที่หายไป" ที่ผู้จัดทำส่งมา ภาพสไตล์วาดมือเรียบง่ายจาก Missing Hero v2 ที่ผู้จัดทำเลือกรูปแบบไว้

**หมายเหตุ:** ยังไม่มีระบบบันทึกความคืบหน้าหรือเสียงประกอบ
