"""Original, deliberately simple hand-drawn-looking pixel assets.
Run with Python 3 + Pillow to regenerate art; generated PNGs are already bundled.
All drawings are a set of small coloured shapes inspired by the user's proposal style.
"""
from PIL import Image, ImageDraw, ImageFont
from pathlib import Path
import math,random
random.seed(8)
ROOT=Path(__file__).parent/'src/main/resources/art';ROOT.mkdir(parents=True,exist_ok=True)
C={'paper':'#fff0b4','cream':'#fff4cf','line':'#925d51','wood':'#dfb17b','wall':'#eed6a1','blue':'#83aec5','green':'#87b59b','pink':'#db9492','brown':'#ad7964','dark':'#5b514f','sun':'#f2c17b'}
def rectangle(d,xy,fill,outline=C['line'],width=2):d.rectangle(xy,fill=fill,outline=outline,width=width)
def ell(d,xy,fill,outline=C['line'],width=2):d.ellipse(xy,fill=fill,outline=outline,width=width)
def lines(d,coords,fill=C['line'],width=2): d.line(coords,fill=fill,width=width,joint='curve')
# maps all hand-drawn beige stage backgrounds, 384x256 to keep intentional small pixel scale
def background(scene):
 im=Image.new('RGB',(384,256),C['paper']);d=ImageDraw.Draw(im)
 # comic strip walls and floor, subtle uneven hatching
 rectangle(d,(5,34,378,249),C['cream']);rectangle(d,(6,34,377,101),C['wall'])
 lines(d,[(6,101),(377,101)],width=3)
 for y in range(114,249,19):
  lines(d,[(7,y),(376,y)],'#deb68a',1)
 for x in range(17,374,39):
  lines(d,[(x,103),(x+3,249)],'#ead19f',1)
 def rug(x=133,y=175,w=121,h=47):
  rectangle(d,(x,y,x+w,y+h),'#dfc3a6');rectangle(d,(x+7,y+6,x+w-7,y+h-6),'#aac8b1')
 def window(x=129,y=45):
  rectangle(d,(x,y,x+60,y+44),'#835a5c');rectangle(d,(x+5,y+4,x+56,y+40),'#5d8daa');lines(d,[(x+30,y+5),(x+30,y+39)],'#e9d2aa',3);lines(d,[(x+6,y+22),(x+55,y+22)],'#e9d2aa',2)
  ell(d,(x+37,y+8,x+44,y+15),'#fff2bb',None)
 def box(x,y,color='#c59164',w=31,h=27):
  rectangle(d,(x,y,x+w,y+h),color);lines(d,[(x,y+7),(x+w,y+7)],'#a07155',1);rectangle(d,(x+9,y+12,x+19,y+16),'#ffeba9',None)
 def plant(x,y):
  rectangle(d,(x-8,y,x+9,y+11),'#c9886b')
  for xx,yy in [(-9,-10),(0,-19),(9,-16),(-5,-27)]:ell(d,(x+xx-8,y+yy-9,x+xx+7,y+yy+7),'#80ad7d','#71916b',1)
 def door(x,y):
  rectangle(d,(x,y,x+34,y+66),'#a47564');rectangle(d,(x+7,y+9,x+27,y+53),'#dfad80');ell(d,(x+25,y+35,x+29,y+39),'#ffe8a6')
 def table(x,y):
  rectangle(d,(x,y,x+84,y+38),'#bc876b');rectangle(d,(x+7,y+4,x+77,y+32),'#f3d09a');rectangle(d,(x+8,y+38,x+15,y+55),'#98715a');rectangle(d,(x+69,y+38,x+75,y+55),'#98715a')
 def shelves(x,y):
  rectangle(d,(x,y,x+70,y+80),'#ac7966')
  for yy in (y+24,y+48):lines(d,[(x+4,yy),(x+66,yy)],'#664b52',3)
  for i in range(24):
   bx=x+6+(i%8)*7;by=y+4+(i//8)*24
   rectangle(d,(bx,by,bx+5,by+15),['#83adc0','#d78c85','#91ac84','#eac18e'][i%4],None)
 def sign(x,y,text):
  rectangle(d,(x,y,x+79,y+25),'#fff2ce');d.text((x+5,y+7),text,fill=C['line'])
 if scene=='bedroom':
  rug(123,172,130,57);window(117,45)
  rectangle(d,(28,117,124,169),'#ae806b');rectangle(d,(34,113,117,158),'#d4b19b');rectangle(d,(37,117,111,145),'#f5dbbd');rectangle(d,(43,122,71,140),'#fff4de');rectangle(d,(76,120,109,141),'#fff4dc');rectangle(d,(41,145,117,159),'#a6bbc3')
  table(192,119);shelves(294,84);box(336,182)
  sign(12,45,'ROOM 2F');plant(28,219)
  rectangle(d,(335,206,376,247),'#c49380');lines(d,[(335,209),(374,209)],'#8e6456',3)
  # dislodged boxes at staircase
  box(338,176,'#d2a371',30,26);box(314,201,'#bc8c65',27,27)
 elif scene=='living':
  rug(128,167,116,58);window(296,45);table(148,149)
  rectangle(d,(32,124,118,181),'#93b4a5');rectangle(d,(40,132,108,158),'#b2cfb8');rectangle(d,(32,173,122,190),'#698f80')
  rectangle(d,(13,84,98,125),'#ce977d'); plant(334,210);sign(12,45,'LIVING')
 elif scene=='toyroom':
  rug(92,145,186,80);window(127,45)
  for x,y in ((50,125),(78,181),(285,145)):
   box(x,y,'#d9a276',39,30)
  shelves(287,92);ell(d,(173,191,190,208),'#ce8585');ell(d,(214,159,230,176),'#85b0bb');sign(12,45,'TOY ROOM')
 elif scene=='storage':
  for x,y in ((25,113),(72,163),(279,114),(321,179),(154,190)):
   box(x,y,'#d6a575',42,37)
  shelves(258,87);rectangle(d,(137,103,221,151),'#bb896a');rectangle(d,(144,111,213,140),'#f2cda0');rectangle(d,(174,124,192,130),'#92674c');sign(12,45,'STORAGE')
 elif scene=='porch':
  window(79,45);door(287,119);rug(139,179,97,49)
  rectangle(d,(135,91,228,120),'#9e8c88');rectangle(d,(140,95,221,115),'#576775');ell(d,(178,97,186,105),'#db6e5a');plant(340,201);sign(12,45,'FRONT DOOR')
 else:
  # neighbourhood / yard scenes, matching proposal's plain map + pastel houses
  rectangle(d,(5,34,378,249),'#c6e3bb');rectangle(d,(6,125,377,215),'#d4c4a0')
  for y in range(140,218,24):lines(d,[(9,y),(375,y)],'#b7a988',2)
  for x in range(8,378,60):
   ell(d,(x,38,x+42,90),'#8fbc8a','#638c76',2);rectangle(d,(x+18,79,x+24,104),'#a27b61')
  if scene in ('street','alley'):
   rectangle(d,(10,48,121,126),'#f2c4a4');lines(d,[(7,55),(62,24),(125,55)],'#aa7777',7);door(40,68);window(72,62)
   rectangle(d,(258,48,371,122),'#d4a6a0');lines(d,[(251,55),(309,21),(377,55)],'#9f7276',7);door(319,61)
  elif scene=='neighbor':
   rectangle(d,(29,50,175,136),'#efc4b5');lines(d,[(23,55),(102,21),(182,55)],'#a77a7a',7);door(82,67);window(129,58);plant(189,130)
  elif scene=='delivery':
   rectangle(d,(224,51,367,128),'#b5c8cf');lines(d,[(217,56),(294,29),(376,56)],'#8e8785',7);door(312,66);rectangle(d,(88,151,191,191),'#dca46f');ell(d,(93,186,113,204),'#615e67');ell(d,(166,186,186,204),'#615e67');sign(12,45,'DELIVERY')
  elif scene=='guard':
   rectangle(d,(224,55,366,131),'#9bb7c1');lines(d,[(215,58),(296,31),(378,58)],'#7b8189',6);door(317,70)
   rectangle(d,(30,154,157,194),'#7c99a8');ell(d,(48,180,70,203),'#55596a');ell(d,(120,180,143,203),'#55596a');sign(12,45,'GUARD')
  if scene=='alley':
   for x in (151,171,191,211):
    ell(d,(x,167,x+10,177),'#ad806f');ell(d,(x+2,163,x+5,166),'#ad806f',None)
   sign(144,44,'EAST ALLEY')
  elif scene=='street':sign(154,45,'FOLLOW PAWS')
  elif scene=='neighbor':sign(15,44,'NEIGHBOR')
  elif scene=='guard':sign(15,44,'WATCH POST')
 # pixel-comic framed bottom title strip
 rectangle(d,(6,231,378,249),'#fce8ab');d.text((13,235),scene.upper().replace('ROOM',' ROOM'),fill='#8b625a')
 im.save(ROOT/f'{scene}.png')
for key in ['bedroom','living','toyroom','storage','porch','street','neighbor','delivery','guard','alley']:
 background(key)
# pop-up drawings transparent white cards in yellow with loose outlines, like user's original screenshots
clues={
'collar':('collar','HERO'), 'door_scratch':('scratch','DOOR'), 'ball':('ball','BALL'),
'bowl':('bowl','FOOD'), 'window':('window','OPEN'), 'toys':('ball','TOYS'),
'photo':('dogs','BEST FRIENDS'), 'parcel':('parcel','SNACKS'), 'receipt':('receipt','07:35'),
'camera':('camera','CCTV'), 'paw_hero':('paw','HERO'), 'paw_other':('paw','???'),
'neighbor_toy':('ball','NEIGHBOR'), 'drawing':('drawing','I LOVE HERO'),
'delivery_snack':('parcel','TREATS'), 'patrol':('book','PATROL'),
'car_scratch':('car','OLD MARK'), 'max_collar':('collar','MAX'), 'alley_photo':('dogs','SEE YOU')}
F=ImageFont.load_default()
def dog(d,x,y,coat):
 ell(d,(x+11,y+22,x+68,y+58),coat);ell(d,(x,y+10,x+40,y+43),coat)
 d.polygon([(x+7,y+13),(x+4,y-1),(x+24,y+10)],fill=coat)
 d.polygon([(x+27,y+10),(x+39,y+1),(x+39,y+22)],fill=coat)
 ell(d,(x+14,y+23,x+18,y+27),'#392f38',None);ell(d,(x+2,y+32,x+12,y+37),'#564044',None)
 for xx in (x+24,x+55): rectangle(d,(xx,y+48,xx+36//4,y+66),coat)
 lines(d,[(x+68,y+31),(x+80,y+17)],coat,5)
def clue_art(key,kind,word):
 im=Image.new('RGBA',(256,200),'#fff1bb');d=ImageDraw.Draw(im)
 rectangle(d,(10,10,246,190),'#fff9df',width=2)
 # small paper corner doodles
 for x in (24,229):ell(d,(x-2,20,x+1,23),'#cb8973',None)
 if kind=='collar':
  ell(d,(63,47,197,130),'#ca8072');ell(d,(78,61,182,116),'#fff9df');rectangle(d,(125,115,148,148),'#f2d197');ell(d,(127,120,149,143),'#f4c477');d.text((132,125),word[:3],fill='#75544c')
 elif kind=='scratch':
  rectangle(d,(64,36,193,157),'#c9947d');
  for x in (105,122,140,160):lines(d,[(x,67),(x-12,124)],'#8b5653',3)
 elif kind=='ball':
  ell(d,(78,48,178,148),'#e9a19a');ell(d,(95,64,160,130),'#f5c3aa');lines(d,[(106,53),(117,144)],'#a4656b',3);lines(d,[(81,105),(173,90)],'#a4656b',3)
 elif kind=='bowl':
  ell(d,(48,83,208,146),'#6cacc5');rectangle(d,(58,97,198,140),'#78b8ce');ell(d,(60,75,196,113),'#dcbd91');
  for x,y in ((80,84),(105,87),(133,91),(158,81),(181,89)):
   ell(d,(x,y,x+9,y+8),'#8d6655',None)
 elif kind=='window':
  rectangle(d,(57,37,196,151),'#b88067');rectangle(d,(66,46,187,143),'#76b6cd');lines(d,[(122,49),(122,143)],'#fce4b6',6);lines(d,[(69,99),(183,99)],'#fce4b6',5);lines(d,[(134,44),(189,53)],'#fff8d8',3)
 elif kind=='dogs':
  rectangle(d,(26,37,229,164),'#f3dfb2');dog(d,43,69,'#d3a07d');dog(d,143,63,'#937a71');
  d.text((77,149),word,fill='#976c65')
 elif kind=='parcel':
  rectangle(d,(55,62,149,140),'#d9a279');lines(d,[(59,80),(146,80)],'#b77d67',3);lines(d,[(100,62),(100,143)],'#efd2a6',9)
  rectangle(d,(158,94,208,145),'#e49e8e');d.text((164,107),'DOG',fill='#fff2d4')
 elif kind=='receipt' or kind=='book':
  rectangle(d,(67,32,191,165),'#f2e1bd');
  for i in range(6):lines(d,[(82,67+14*i),(170,67+14*i)],'#be8f77',2)
  d.text((103,45),'07:35' if kind=='receipt' else 'LOG',fill='#775b52')
 elif kind=='camera':
  rectangle(d,(51,75,192,132),'#9aa9b3');ell(d,(92,81,147,130),'#6d859a');ell(d,(110,96,132,119),'#344758');rectangle(d,(72,136,170,143),'#8c8d8d')
 elif kind=='paw':
  ell(d,(88,98,167,154),'#9b7162');
  for x,y in ((78,56),(108,42),(143,47),(172,61)):ell(d,(x,y,x+26,y+33),'#9b7162')
 elif kind=='drawing':
  rectangle(d,(40,31,214,171),'#fffdfa');d.text((62,48),'I WISH HERO',fill='#bd767a');dog(d,91,74,'#c18e76');
 elif kind=='car':
  rectangle(d,(37,91,219,136),'#9ba9ba');ell(d,(59,128,88,157),'#5f5d6b');ell(d,(161,128,190,157),'#5f5d6b');
  for x in (126,141,155):lines(d,[(x,98),(x-10,120)],'#76525a',2)
 d.text((17,177),key.upper().replace('_',' '),fill='#986861')
 im.save(ROOT/f'clue_{key}.png')
for key,(kind,word) in clues.items():clue_art(key,kind,word)
# top-down player avatar & small simple NPC avatars matching sketch references
for role in ('player','child','courier','guard','max'):
 im=Image.new('RGBA',(46,52));d=ImageDraw.Draw(im)
 if role=='max':dog(d,0,0,'#a67c68')
 else:
  coat={'player':'#84aca6','child':'#d99baa','courier':'#d9a064','guard':'#657da1'}[role]
  ell(d,(7,7,35,29),'#f2c4a3');rectangle(d,(8,2,35,13),'#6d5254')
  rectangle(d,(10,29,33,43),coat);rectangle(d,(10,44,18,50),'#6f6b76');rectangle(d,(27,44,35,50),'#6f6b76')
  ell(d,(16,21,19,24),'#554045',None);ell(d,(28,21,31,24),'#554045',None)
  if role=='courier': rectangle(d,(8,1,38,8),'#627faf')
  if role=='guard': rectangle(d,(7,1,38,10),'#415e7b');ell(d,(22,30,26,34),'#edda97')
 im.save(ROOT/f'npc_{role}.png')
print('Generated',len(list(ROOT.glob('*.png'))),'PNG assets')
