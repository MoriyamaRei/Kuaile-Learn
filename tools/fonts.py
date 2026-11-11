"""Siapkan font aplikasi (subset karakter yang dipakai saja supaya APK kecil).

Lokal : python tools/fonts.py --local   -> pakai Inter + Noto Sans CJK SC dari sistem (cadangan)
CI    : python tools/fonts.py --ci      -> unduh Plus Jakarta Sans + LXGW WenKai; kalau gagal, cadangan tetap dipakai
"""
import json, os, sys, urllib.request, io
from fontTools import subset
from fontTools.ttLib import TTFont, TTCollection

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FONT_DIR = os.path.join(ROOT, 'shared/src/commonMain/composeResources/font')
WORDS = os.path.join(ROOT, 'shared/src/commonMain/composeResources/files/words.json')
EXTRA = "读学习时间今天的词加油汉字拼音你好再见太棒了继续加油复习新完成目标连续天中文"

def hanzi_chars():
    d = json.load(open(WORDS, encoding='utf8'))
    s = set(EXTRA)
    for w in d['words']:
        s.update(w['h'])
        for x in w['s']:
            s.update(x['e'])
    s.update(chr(c) for c in range(0x20, 0x7F))
    s.update('，。！？：；、“”‘’（）《》……～·—0123456789')
    return ''.join(sorted(s))

LATIN = ''.join(chr(c) for c in list(range(0x20, 0x7F)) + list(range(0xA0, 0x250)) + list(range(0x2000, 0x2070)) + [0x20AC, 0x2192, 0x2190, 0x2022, 0x2026, 0x00D7, 0x2713])

def do_subset(font, text, out):
    opts = subset.Options()
    opts.layout_features = ['*']
    opts.name_IDs = ['*']
    opts.notdef_outline = True
    opts.glyph_names = False
    opts.flavor = None
    s = subset.Subsetter(opts)
    s.populate(text=text)
    s.subset(font)
    font.save(out)
    print('  ->', os.path.basename(out), os.path.getsize(out) // 1024, 'KB')

def local():
    hz = hanzi_chars()
    for w, f in [('regular', 'NotoSansCJK-Regular.ttc'), ('bold', 'NotoSansCJK-Bold.ttc')]:
        ttc = TTCollection('/usr/share/fonts/opentype/noto/' + f)
        do_subset(ttc.fonts[2], hz, os.path.join(FONT_DIR, f'hanzi_{w}.otf'))
    for w, f in [('regular', 'Regular'), ('medium', 'Medium'), ('semibold', 'SemiBold'), ('bold', 'Bold'), ('extrabold', 'ExtraBold')]:
        do_subset(TTFont(f'/usr/share/fonts/opentype/inter/Inter-{f}.otf'), LATIN, os.path.join(FONT_DIR, f'ui_{w}.otf'))

def fetch(url):
    req = urllib.request.Request(url, headers={'User-Agent': 'readit-build'})
    with urllib.request.urlopen(req, timeout=120) as r:
        return r.read()

def ci():
    hz = hanzi_chars()
    try:
        for w, name in [('regular', 'LXGWWenKai-Regular.ttf'), ('bold', 'LXGWWenKai-Medium.ttf')]:
            data = fetch('https://github.com/lxgw/LxgwWenKai/releases/latest/download/' + name)
            for old in ('otf', 'ttf'):
                p = os.path.join(FONT_DIR, f'hanzi_{w}.{old}')
                if os.path.exists(p): os.remove(p)
            do_subset(TTFont(io.BytesIO(data)), hz, os.path.join(FONT_DIR, f'hanzi_{w}.ttf'))
        print('LXGW WenKai OK')
    except Exception as e:
        print('LXGW WenKai gagal, pakai cadangan:', e)
    try:
        from fontTools.varLib import instancer
        data = fetch('https://github.com/google/fonts/raw/main/ofl/plusjakartasans/PlusJakartaSans%5Bwght%5D.ttf')
        for w, wt in [('regular', 400), ('medium', 500), ('semibold', 600), ('bold', 700), ('extrabold', 800)]:
            vf = TTFont(io.BytesIO(data))
            inst = instancer.instantiateVariableFont(vf, {'wght': wt})
            for old in ('otf', 'ttf'):
                p = os.path.join(FONT_DIR, f'ui_{w}.{old}')
                if os.path.exists(p): os.remove(p)
            do_subset(inst, LATIN, os.path.join(FONT_DIR, f'ui_{w}.ttf'))
        print('Plus Jakarta Sans OK')
    except Exception as e:
        print('Plus Jakarta Sans gagal, pakai cadangan:', e)

if __name__ == '__main__':
    os.makedirs(FONT_DIR, exist_ok=True)
    ci() if '--ci' in sys.argv else local()
