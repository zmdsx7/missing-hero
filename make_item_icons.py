"""Create small transparent in-world clue markers from the detailed popup cards.
No separate images from the web; these icons are based on the included artwork.
Run only when deliberately regenerating item_*.png files.
"""
from pathlib import Path
from PIL import Image
base=Path(__file__).parent/'src/main/resources/art'
for path in sorted(base.glob('clue_*.png')):
    source=Image.open(path).convert('RGBA')
    # Keep the center illustration, not the outer cream inventory-card border and label.
    w,h=source.size
    source=source.crop((int(.19*w),int(.14*h),int(.81*w),int(.79*h)))
    pixels=source.load()
    for y in range(source.height):
        for x in range(source.width):
            r,g,b,a=pixels[x,y]
            # Cream background that appears on the pop-up clue cards.
            if r>=244 and g>=232 and b>=193 and (r-g)<=25:
                pixels[x,y]=(r,g,b,0)
    bbox=source.getbbox()
    if bbox: source=source.crop(bbox)
    width,height=source.size
    scale=min(56/width,56/height)
    resized=source.resize((max(1,round(width*scale)),max(1,round(height*scale))),Image.Resampling.NEAREST)
    icon=Image.new('RGBA',(64,64),(0,0,0,0))
    icon.alpha_composite(resized,((64-resized.width)//2,(64-resized.height)//2))
    icon.save(base/('item_'+path.name[5:]))
print('created',len(list(base.glob('item_*.png'))),'transparent floor icons')
