"""Menambahkan pinyin ke setiap contoh kalimat di words.json (kunci "ep" pada tiap arti).

Pinyin per karakter diambil dari pypinyin (memakai kamus frasa, jadi huruf berbunyi ganda
seperti 了/的/得/长 umumnya tepat). Karakter lalu dikelompokkan per kata memakai kosakata
aplikasi sendiri (cocok terpanjang), sehingga hasilnya seperti "Wǒ ài chī mǐfàn."
Untuk kata kepala satu karakter, bacaannya mengikuti pinyin kata itu di data.

Pakai:  pip install pypinyin jieba
        python3 tools/example_pinyin.py
"""
import json
import re
import sys
from pathlib import Path

import unicodedata

from pypinyin import Style, pinyin
from pypinyin.constants import PHRASES_DICT
import jieba

jieba.setLogLevel(60)

ROOT = Path(__file__).resolve().parent.parent
FILES = [ROOT / "shared/src/commonMain/composeResources/files/words.json", ROOT / "web/data/words.json"]

HAN = re.compile(r"[㐀-鿿]")
# Tanda baca Tionghoa → Latin untuk baris pinyin.
PUNCT = {"，": ",", "。": ".", "？": "?", "！": "!", "：": ":", "；": ";", "、": ",", "“": "\"", "”": "\"",
         "‘": "'", "’": "'", "（": "(", "）": ")", "《": "\"", "》": "\"", "…": "…", "—": "—", "·": "·", "～": "~"}


def clean(p: str) -> str:
    """Buang bacaan alternatif dalam kurung, misalnya "zhè(zhèr)" → "zhè"."""
    return re.sub(r"\(.*?\)|（.*?）", "", p or "").strip()


def plain(p: str) -> str:
    """Pinyin tanpa nada, huruf kecil, tanpa spasi/apostrof (untuk membandingkan bacaan)."""
    d = unicodedata.normalize("NFD", p.lower())
    return "".join(c for c in d if not unicodedata.combining(c) and c.isalpha())


def norm(p: str) -> str:
    return unicodedata.normalize("NFC", p.lower()).replace(" ", "").replace("'", "")


def choose(cands: list, guess: str) -> str:
    """Pilih bacaan kosakata yang paling cocok dengan tebakan pypinyin (sama persis, lalu sama tanpa nada)."""
    for c in cands:
        if norm(c) == norm(guess):
            return c
    for c in cands:
        if plain(c) == plain(guess):
            return c
    return ""


PARTICLES = set("得的地了又在着过")
# Kata berakhiran 儿 yang dibaca ér penuh (bukan erhua).
NOT_ERHUA = {"女儿", "婴儿", "孤儿", "幼儿", "健儿", "胎儿", "男儿", "少儿"}


def split_vocab(tok: str, vocab: dict) -> list:
    """Frasa jieba yang bukan kosakata (我爱你, 坐在, 跑得快) dipecah menjadi kosakata dan huruf tunggal.
    Kata dua huruf tanpa partikel (电话, 秋天) dibiarkan utuh."""
    if len(tok) < 2 or tok in vocab or not all(HAN.match(c) for c in tok):
        return [tok]
    if len(tok) == 2 and not (set(tok) & PARTICLES):
        return [tok]
    parts, i = [], 0
    while i < len(tok):
        for k in range(len(tok) - i, 0, -1):
            piece = tok[i:i + k]
            if k == 1 or (piece in vocab and piece != tok):
                parts.append(piece); i += k
                break
    return parts


def build(sentence: str, vocab: dict, head: str, head_py: str) -> str:
    chars = list(sentence)
    syl = [pinyin(c, style=Style.TONE, errors=lambda x: list(x))[0][0] for c in chars]
    ctx = pinyin(sentence, style=Style.TONE, errors=lambda x: list(x))
    if len(ctx) == len(chars):
        syl = [p[0] for p in ctx]
    # Pemotongan kata dengan jieba (kosakata aplikasi sudah ditambahkan sebagai kata).
    tokens = []
    i = 0
    for tok in jieba.cut(sentence, HMM=False):
        for part in split_vocab(tok, vocab):
            tokens.append((part, i))
            i += len(part)
    out = []  # (pinyin kata, berspasi seperti kata, adalah angka/huruf)
    for tok, at in tokens:
        if not all(HAN.match(c) for c in tok):
            # Bukan hanzi: tanda baca atau huruf/angka, per karakter.
            for c in tok:
                p = PUNCT.get(c, c)
                if not p.strip():
                    continue
                if p.isalnum() and out and out[-1][2]:
                    out[-1] = (out[-1][0] + p, True, True)  # angka/huruf berurutan jadi satu kata
                else:
                    out.append((p, p.isalnum(), p.isalnum()))
            continue
        guess = "".join(syl[at:at + len(tok)])
        if tok == "得" and not sentence[at + 1:at + 2] == "到":
            py = "de"
        elif tok == "儿" and out and out[-1][1] and not out[-1][2]:
            out[-1] = (out[-1][0] + "r", True, False)  # erhua: 点儿 → diǎnr
            continue
        elif tok.endswith("儿") and len(tok) > 1 and tok not in vocab and tok not in NOT_ERHUA:
            pre, pg = tok[:-1], "".join(syl[at:at + len(tok) - 1])
            py = ((choose(vocab[pre], pg) or pg) if pre in vocab and len(pre) > 1 else pg) + "r"
        elif len(tok) == 1 and tok == head and head_py and " " not in head_py.strip():
            py = head_py.strip()
        elif tok == "不":
            # 不 sebelum nada 4 dibaca bú (不要 → bú yào).
            nxt = syl[at + 1] if at + 1 < len(syl) else ""
            py = "bú" if any(c in nxt for c in "àèìòùǜ") else "bù"
        elif len(tok) == 1:
            py = guess  # satu huruf: bacaan sesuai konteks dari pypinyin (termasuk 一 → yí/yì)
        elif tok in vocab:
            # Kata berhuruf banyak: bacaan dari data aplikasi.
            py = choose(vocab[tok], guess) or (vocab[tok][0] if vocab[tok] else guess)
        elif tok in PHRASES_DICT:
            py = "".join(x[0] for x in PHRASES_DICT[tok])
        else:
            py = guess
        out.append((py, True, False))
    text = ""
    for p, han, _ in out:
        if han:
            if text and not text.endswith((" ", "\"", "(", "“")):
                text += " "
            text += p
        elif p in ",.?!:;)…~":
            text = text.rstrip() + p + " "
        else:
            text += p
    text = re.sub(r"\s+", " ", text).strip()
    # Huruf kapital di awal kalimat dan setelah . ? !
    text = re.sub(r"(^|[.?!]\s+)([a-zāáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜü])", lambda m: m.group(1) + m.group(2).upper(), text)
    return text


def main() -> int:
    data = json.loads(FILES[0].read_text(encoding="utf-8"))
    vocab = {}
    for w in data["words"]:
        if all(HAN.match(c) for c in w["h"]):
            vocab.setdefault(w["h"], [])
            py = clean(w.get("py", ""))
            if py and py not in vocab[w["h"]]:
                vocab[w["h"]].append(py)
    for h in vocab:
        jieba.add_word(h, freq=200000)
    n = 0
    for w in data["words"]:
        for s in w["s"]:
            if s.get("e"):
                s["ep"] = build(s["e"], vocab, w["h"], clean(w.get("py", "")))
                n += 1
    text = json.dumps(data, ensure_ascii=False, separators=(",", ":"))
    for f in FILES:
        f.write_text(text, encoding="utf-8")
    print(f"{n} contoh kalimat diberi pinyin.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
