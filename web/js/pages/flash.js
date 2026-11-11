import { h, hanzi, pinyin, speak, levelBadge, header, LEVEL_COLOR } from '../ui.js';
import * as S from '../store.js';
import { words, theme, themes, posName, shuffle, studyOrder } from '../data.js';
import { go, back, setFocus } from '../app.js';
import { icon, themeIcon } from '../icons.js';

// ---------- Dek ----------
const LEVEL_DECKS = [
  { key: 'semua', name: 'Semua Kata', ico: 'library' },
  { key: 'hsk1', name: 'HSK 1', lv: 1 }, { key: 'hsk2', name: 'HSK 2', lv: 2 },
  { key: 'hsk3', name: 'HSK 3', lv: 3 }, { key: 'hsk4', name: 'HSK 4', lv: 4 },
  { key: 'bintang', name: 'Berbintang', ico: 'star' },
];

export function deckInfo(key) {
  const d = LEVEL_DECKS.find((x) => x.key === key);
  if (d) return d;
  const t = key.startsWith('tema-') ? themes[key.slice(5)] : null;
  return t ? { key, name: t.name, theme: key.slice(5) } : LEVEL_DECKS[0];
}

export function deckWords(key) {
  const d = deckInfo(key);
  const all = studyOrder(false);
  if (d.lv) return all.filter((w) => w.level === d.lv);
  if (d.key === 'bintang') return all.filter((w) => S.card(w.id).star);
  if (d.theme) return all.filter((w) => w.theme === d.theme);
  return all;
}

/** Tab Belajar: pilih dek flashcard. */
export function learnPage() {
  const last = S.flashSettings().last;
  const tile = (d, i) => {
    const list = deckWords(d.key);
    const pos = S.flashSettings().pos[d.key] || 0;
    return h('button.card.deck-tile.tap.appear', { style: { animationDelay: `${0.03 * i}s`, '--c': d.lv ? LEVEL_COLOR[d.lv] : 'var(--blue)' }, onclick: () => go(`flashcard/${d.key}`), disabled: !list.length },
      h('div.deck-ico', d.lv ? h('b', d.lv) : d.theme ? themeIcon(d.theme) : icon(d.ico)),
      h('div.grow',
        h('b', d.name),
        h('div.muted.small', list.length ? (pos ? `Kartu ${pos + 1}/${list.length}` : `${list.length} kata`) : 'Belum ada kata')));
  };
  const lastDeck = last ? deckInfo(last) : null;
  return h('div',
    header('Belajar', 'Flashcard dan materi angka'),
    h('div.wrap.stack',
      lastDeck && deckWords(last).length ? h('div.brand.tap.appear', { onclick: () => go(`flashcard/${last}`) },
        h('div.row', h('div.tile-ico.on-brand', icon('cards')), h('div.grow', h('h2', 'Lanjutkan Flashcard'), h('p.muted', `${lastDeck.name} · Kartu ${(S.flashSettings().pos[last] || 0) + 1} dari ${deckWords(last).length}`)), icon('chevron'))) : null,
      h('h3.section-title', 'Materi'),
      h('div.card.pad.row.tap.appear', { onclick: () => go('angka') }, h('div.tile-ico', icon('numbers')),
        h('div.grow', h('b', 'Materi Angka'), h('p.muted.small', 'Teori, rumus, langkah, dan contoh membaca angka')), icon('chevron')),
      h('h3.section-title', 'Flashcard per Level'),
      h('p.muted.small', { style: { marginTop: '-6px' } }, 'Pengetahuan harian: flashcard tidak memengaruhi progres.'),
      h('div.deck-grid', LEVEL_DECKS.map(tile)),
      h('h3.section-title', 'Flashcard per Tema'),
      h('div.deck-grid', Object.keys(themes).map((k, i) => tile(deckInfo(`tema-${k}`), i)))));
}

/** Flashcard sederhana: hanzi dulu, tombol untuk pinyin dan arti, tombol untuk kata berikutnya. Tidak mengubah progres. */
export function flashPage(key = 'semua') {
  const d = deckInfo(key);
  const base = deckWords(d.key);
  let list = base;
  let shuffled = false;
  let idx = Math.min(S.flashSettings().pos[d.key] || 0, Math.max(0, base.length - 1));
  let open = false;
  const root = h('div');
  setFocus(true);
  S.setFlash({ last: d.key });

  if (!base.length) {
    root.append(header(d.name, null, { back }), h('div.wrap', h('div.empty', h('div.big', icon('star')), h('h3', 'Belum Ada Kata'), h('p', 'Tandai kata dengan bintang di Kamus atau di kartu, lalu buka dek ini lagi.'))));
    return { el: root, cleanup: () => setFocus(false) };
  }

  const fs = () => S.flashSettings();
  const counter = h('b.flash-count');
  const toggle = (label, on, fn) => h(`button.chip${on ? '.on' : ''}`, { onclick: (e) => { fn(); e.currentTarget.classList.toggle('on'); } }, label);
  const slot = h('div.flash-slot');
  const prevBtn = h('button.btn.ghost.flash-prev', { 'aria-label': 'Kata sebelumnya', onclick: () => move(-1) }, '‹');
  // Dua pilihan yang selalu ada: buka pinyin & arti dulu, atau langsung ke hanzi berikutnya.
  const revealBtn = h('button.btn.ghost.flash-reveal', { onclick: () => (open ? hide() : reveal()) });
  const nextBtn = h('button.btn.flash-next', { onclick: () => move(1) }, 'Next ›');

  const top = h('div.wrap.flash-top',
    h('div.topline',
      h('button.icon-btn', { onclick: () => back(), 'aria-label': 'Tutup' }, icon('close')),
      h('div.grow', h('b', d.name), h('div.muted.small', `${list.length} kata`)),
      counter),
    h('div.chips.flash-opts',
      toggle([icon('speaker'), ' Suara Otomatis'], fs().auto, () => { S.setFlash({ auto: !fs().auto }); if (fs().auto) speak(list[idx].hanzi); }),
      toggle([icon('shuffle'), ' Acak'], false, () => {
        shuffled = !shuffled; const cur = list[idx];
        if (shuffled) { list = [cur, ...shuffle(base.filter((w) => w !== cur))]; idx = 0; } else { list = base; idx = base.indexOf(cur); }
        paint();
      })));

  function paint() {
    const w = list[idx];
    const star = h('button.icon-btn.sm', { 'aria-label': 'Bintang', title: 'Bintang', onclick: (e) => { e.stopPropagation(); S.toggleStar(w.id); star.replaceChildren(icon(S.card(w.id).star ? 'star' : 'starOutline')); } }, icon(S.card(w.id).star ? 'star' : 'starOutline'));
    const card = h(`div.fc${open ? '.open' : ''}`, { style: { '--c': LEVEL_COLOR[w.level] }, onclick: () => { if (!open) reveal(); } },
      h('div.fc-top', levelBadge(w.level), h('span.muted.small.grow.row', themeIcon(w.theme), theme(w.theme).name), star,
        h('button.icon-btn.sm', { 'aria-label': 'Dengarkan', onclick: (e) => { e.stopPropagation(); speak(w.hanzi); } }, icon('speaker'))),
      h('div.fc-body', hanzi(w.hanzi, 'fc-hz'),
        open ? h('div.fc-reveal',
          h('div.fc-py', pinyin(w.pinyin, w.hanzi)),
          h('h2.fc-mean', w.main),
          h('p.muted.small', posName(w.pos)),
          w.others.length ? h('p.muted.small.fc-others', 'Arti lain: ' + w.others.join(' · ')) : null,
          w.example ? h('div.ex', { onclick: (e) => { e.stopPropagation(); speak(w.example); } }, h('span.hz', w.example), icon('speaker')) : null,
          h('a.fc-detail', { href: `#/kata/${w.id}` }, 'Lihat Detail Kata ›')) : null));
    slot.replaceChildren(card);
    counter.textContent = `${idx + 1}/${list.length}`;
    revealBtn.textContent = open ? 'Tutup Arti' : 'Pinyin & Arti';
    prevBtn.disabled = idx === 0;
    if (!shuffled) S.setFlash({ pos: { ...fs().pos, [d.key]: idx } });
  }
  function hide() { open = false; paint(); }
  function reveal() { open = true; paint(); if (!fs().auto) speak(list[idx].hanzi); }
  function move(k) {
    idx = (idx + k + list.length) % list.length; open = false; paint();
    window.scrollTo({ top: 0 });
    if (fs().auto) speak(list[idx].hanzi);
  }

  const onKey = (e) => {
    if (e.target.matches('input, textarea')) return;
    if (e.key === 'ArrowRight') move(1); else if (e.key === 'ArrowLeft') move(-1);
    else if (e.key === ' ' || e.key === 'Enter') { e.preventDefault(); if (open) hide(); else reveal(); }
  };
  document.addEventListener('keydown', onKey);

  root.append(top, h('div.wrap', slot, h('div.flash-nav', prevBtn, revealBtn, nextBtn)));
  paint();
  if (fs().auto) speak(list[idx].hanzi);
  return { el: root, cleanup: () => { setFocus(false); document.removeEventListener('keydown', onKey); } };
}
