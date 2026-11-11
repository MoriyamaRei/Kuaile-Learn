"""Membuat font emoji kecil untuk versi web Compose (shared/src/wasmJsMain/composeResources/files/emoji.ttf).

Browser tidak memberi Compose akses ke emoji sistem, jadi emoji yang dipakai di kode (ditambah
beberapa emoji populer untuk teks dari server) diambil dari Noto Color Emoji (paket npm noto-color-emoji).

Pakai:  pip install fonttools
        npm pack noto-color-emoji && tar xzf noto-color-emoji-*.tgz
        python3 tools/emoji_font.py package/ttf/NotoColorEmoji.ttf
"""
import glob
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "shared/src/wasmJsMain/composeResources/files/emoji.ttf"


def main() -> int:
    src = sys.argv[1]
    cps = set()
    for f in glob.glob(str(ROOT / "shared/src/commonMain/kotlin/**/*.kt"), recursive=True):
        for ch in Path(f).read_text(encoding="utf-8"):
            o = ord(ch)
            if o >= 0x2190 and not (0x2E80 <= o <= 0x9FFF) and not (0xFF00 <= o <= 0xFFEF) and o not in range(0x2010, 0x2070):
                cps.add(o)
    cps |= set(range(0x1F600, 0x1F650)) | set(range(0x1F440, 0x1F450))
    cps |= {0x2764, 0x1F525, 0x1F389, 0x1F38A, 0x2B50, 0x1F31F, 0x2705, 0x274C, 0x1F4AF, 0x1F64F, 0x1F44D, 0x1F44F, 0x1F4AA,
            0x1F381, 0x1F4E2, 0x1F4C5, 0x1F3C6, 0x1F947, 0x1F948, 0x1F949, 0x1F680, 0x1F4DA, 0x1F4D6, 0x270F, 0x1F4DD,
            0x1F914, 0x1F973, 0x1F970, 0x1F929, 0x1F60D, 0x200D, 0xFE0F, 0x20E3}
    uni = ",".join("U+%04X" % c for c in sorted(cps))
    subprocess.run(["pyftsubset", src, "--unicodes=" + uni, "--layout-features=*", "--output-file=" + str(OUT)], check=True)
    print(f"{OUT} ({OUT.stat().st_size // 1024} KB, {len(cps)} kode)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
