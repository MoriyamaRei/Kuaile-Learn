import { h, hanzi, pinyin, speakBtn, levelBadge, progress, confetti } from '../ui.js';
import * as S from '../store.js';
import { words, theme, posName } from '../data.js';
import { go } from '../app.js';
import { icon, themeIcon } from '../icons.js';
import { deckInfo, deckWords } from './flash.js';

const greeting = () => { const hr = new Date().getHours(); return hr < 11 ? 'Selamat Pagi' : hr < 15 ? 'Selamat Siang' : hr < 19 ? 'Selamat Sore' : 'Selamat Malam'; };

export function homePage() {
  const done = S.doneToday(); const target = S.dailyTarget();
  const frac = Math.min(1, done / target); const reached = S.goalReached();
  const last = S.flashSettings().last; const lastDeck = last && deckWords(last).length ? deckInfo(last) : null;
  const w = S.wordOfDay();
  const key = 'kl-goal-shown';
  try { if (reached && localStorage.getItem(key) !== String(S.today())) { localStorage.setItem(key, String(S.today())); setTimeout(confetti, 300); } } catch { /* penyimpanan diblokir */ }
  const st = S.streak();

  const ring = h('div.ring', { style: { '--p': frac } }, h('div', h('b', `${Math.round(frac * 100)}%`), h('small', `${done} Soal`)));
  // Kata Hari Ini: di HP tampil sebagai kartu, di laptop ditaruh di sampul.
  const wordCard = (cls) => h(`div.${cls}`,
    h('div.row', levelBadge(w.level, true), h('span.small.grow.row', themeIcon(w.theme), theme(w.theme).name), speakBtn(w.hanzi, cls === 'cover-word' ? 'on-brand' : '')),
    h('div.row', { style: { marginTop: '10px', gap: '18px' } },
      hanzi(w.hanzi, 'big-hz'),
      h('div.grow', pinyin(w.pinyin, w.hanzi), h('div', { style: { fontWeight: 700, fontSize: '1.15rem' } }, w.main), h('div.small.dim', posName(w.pos)))),
    h('p.hz', { style: { marginTop: '10px', fontSize: '1.15rem' } }, w.example),
    h('a.pill', { href: `#/kata/${w.id}`, style: { marginTop: '10px' } }, 'Lihat Detail Kata'));

  return h('div',
    h('header.hero.cover-home',
      h('div.cover-grid',
        h('div.cover-main',
          h('div.cover-brand', h('img.cover-logo', { src: 'img/logo.svg', alt: '' }), h('b', 'Kuaile Mandarin Academy')),
          h('p', `你好！${greeting()}`), h('h1', 'Ayo Belajar Hanzi'),
          h('p.small', 'Kosakata HSK 1–4 · Tanpa akun, progres tersimpan di browser ini'),
          h('div.cover-actions',
            h('button.btn.on-cover', { onclick: () => go(lastDeck ? `flashcard/${last}` : 'belajar') }, icon('play'), lastDeck ? ' Lanjutkan Belajar' : ' Mulai Belajar'),
            h('button.btn.ghost-cover', { onclick: () => go('latihan') }, icon('quiz'), ' Latihan'))),
        h('div.cover-side', h('div.cover-label', 'Kata Hari Ini'), wordCard('cover-word')))),
    h('div.wrap.home-grid',
      h('div.card.pad.row.appear',
        ring,
        h('div.grow',
          h('h2', 'Target Hari Ini'),
          h('p.muted', reached ? 'Hebat! Target hari ini sudah tercapai.' : `${done} dari ${target} soal latihan. Semangat!`),
          h('div.row', { style: { marginTop: '8px' } }, h(`span.flame${st ? '' : '.off'}`, icon('flame')), h('b', { style: { fontSize: '1.2rem' } }, `${st}`), h('span.muted.small', 'Hari Berturut-turut')))),
      h('div.brand.tap.appear', { style: { animationDelay: '.05s' }, onclick: () => go(lastDeck ? `flashcard/${last}` : 'belajar') },
        h('div.row', h('div.tile-ico.on-brand', icon('cards')), h('div.grow', h('h2', lastDeck ? 'Lanjutkan Flashcard' : 'Buka Flashcard'),
          h('p.muted', lastDeck ? `${lastDeck.name} · Kartu ${(S.flashSettings().pos[last] || 0) + 1} dari ${deckWords(last).length}` : 'Pilih dek per level atau per tema')), icon('chevron'))),
      h('div.mobile-only.span2.stack',
        h('h3.section-title', 'Kata Hari Ini'),
        wordCard('card.pad.appear')),
      h('div.span2.stack',
        h('h3.section-title', 'Progres per Level'),
        h('div.card.pad.level-grid.appear', { style: { animationDelay: '.15s' } }, [1, 2, 3, 4].map((lv) => {
          const n = words.filter((x) => x.level === lv).length; const seen = S.seenCount(lv); const hf = S.masteredCount(lv);
          return h('div', h('div.row', levelBadge(lv), h('span.muted.small.grow', `${hf} Hafal`), h('b', { style: { color: 'var(--blue)' } }, `${seen} / ${n}`)),
            h('div', { style: { marginTop: '6px' } }, progress(seen / n)));
        }))),
      h('div.card.pad.tap.shortcut', { onclick: () => go('latihan') }, h('div.tile-ico', icon('quiz')), h('div', h('b', 'Latihan'), h('p.muted.small', 'Kuis kosakata, Kilat, Ujian, Tulis Hanzi'))),
      h('div.card.pad.tap.shortcut', { onclick: () => go('angka') }, h('div.tile-ico', icon('numbers')), h('div', h('b', 'Materi Angka'), h('p.muted.small', 'Teori, rumus, dan contoh membaca angka'))),
    ));
}
