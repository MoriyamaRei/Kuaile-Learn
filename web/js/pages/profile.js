import { h, hanzi, pinyin, header, segmented, progress, toast, confirmDialog, speak } from '../ui.js';
import * as S from '../store.js';
import { words } from '../data.js';
import { go } from '../app.js';
import { icon } from '../icons.js';

export function profilePage() {
  const t = S.totals();
  const st = S.streak();
  const root = h('div');
  function draw() {
    const s = S.state.settings;
    const days = Array.from({ length: 14 }, (_, i) => S.today() - 13 + i);
    const max = Math.max(5, ...days.map((d) => (S.state.days[d]?.quiz || 0)));
    root.replaceChildren(
      h('header.hero',
        h('div.row', { style: { gap: '16px' } },
          h('img.logo-mark', { src: 'img/logo.svg', alt: '' }),
          h('div.grow', h('h1', 'Profil Belajar'), h('p', 'Progres tersimpan di browser ini'),
            h('div.row', { style: { marginTop: '8px' } },
              h('span.pill', { style: { background: 'rgba(255,255,255,.2)', color: '#fff' } }, icon('flame'), ` ${st}`),
              h('span.pill', { style: { background: 'rgba(255,255,255,.2)', color: '#fff' } }, `Rekor ${S.bestStreak()} Hari`))))),
      h('div.wrap.stack.profile-grid',
        h('div.grid4.span2',
          tile('book', S.seenCount(), 'Dipelajari'), tile('trophy', S.masteredCount(), 'Hafal'), tile('target', `${t.acc}%`, 'Akurasi'), tile('bolt', S.state.best.kilat, 'Rekor Kilat')),
        h('div.card.pad.stack',
          h('b', 'Aktivitas 14 Hari Terakhir'),
          h('div.chart', days.map((d) => { const v = (S.state.days[d]?.quiz || 0); return h('div.col', { title: `${v}` }, h('i', { style: { height: `${(v / max) * 100}%` } })); })),
          h('p.muted.small', 'Soal latihan yang dikerjakan per hari.')),
        h('div.card.pad.stack',
          h('b', 'Progres per Level'),
          ...[1, 2, 3, 4].map((lv) => { const n = words.filter((w) => w.level === lv).length; const seen = S.seenCount(lv);
            return h('div', h('div.row', h('span.grow', `HSK ${lv}`), h('b', `${seen}/${n}`)), progress(seen / n)); })),
        mostWrong(),
        h('h3.section-title.span2', 'Pengaturan'),
        h('div.card.pad.stack',
          h('div.row', h('div.grow', h('b', 'Target Soal Latihan per Hari')), h('b', { style: { color: 'var(--blue)', fontSize: '1.3rem' } }, `${s.newPerDay}`)),
          h('input', { type: 'range', min: 10, max: 100, step: 10, value: s.newPerDay, onchange: (e) => { S.setSettings({ newPerDay: +e.target.value }); draw(); } }),
          h('b', 'Tampilan'), segmented(['Ikuti Perangkat', 'Terang', 'Gelap'], s.dark, (i) => { S.setSettings({ dark: i }); draw(); }),
          h('b', 'Kecepatan Suara'), segmented(['Pelan', 'Normal'], s.voice === 0 ? 0 : 1, (i) => { S.setSettings({ voice: i === 0 ? 0 : 1 }); speak('你好，欢迎'); draw(); })),
        h('div.card.pad.stack',
          h('b', 'Cadangan Progres'),
          h('p.muted.small', 'Progres hanya tersimpan di browser ini. Simpan cadangan supaya bisa dipindah ke perangkat atau browser lain.'),
          h('div.grid2',
            h('button.btn.ghost', { onclick: exportFile }, icon('download'), ' Simpan Cadangan'),
            h('label.btn.ghost', { style: { cursor: 'pointer' } }, icon('upload'), ' Pulihkan', h('input', { type: 'file', accept: '.json,application/json', style: { display: 'none' }, onchange: importFile }))),
          h('button.btn.danger', { onclick: () => confirmDialog('Hapus Semua Progres?', 'Semua kartu, statistik, dan pengaturan di browser ini akan dihapus. Tindakan ini tidak bisa dibatalkan.', 'Hapus', () => { S.resetAll(); toast('Progres dihapus.'); draw(); }, true) }, 'Hapus Semua Progres')),
        h('p.muted.small.span2', { style: { textAlign: 'center', margin: '8px 0 24px' } }, 'Kuaile Mandarin Academy · Kosakata HSK 1–4')));
  }
  const tile = (e, v, l) => h('div.card.tile', h('div.tile-ico', icon(e)), h('div.num', `${v}`), h('div.lab', l));
  function mostWrong() {
    const list = S.mostWrong(10);
    return h('div.card.pad.stack.span2', h('b', 'Kata yang Paling Sering Salah'),
      list.length ? list.map(({ w, c }) => h('a.row.tap', { href: `#/kata/${w.id}` }, hanzi(w.hanzi, 'ex-hz'), h('div.grow', pinyin(w.pinyin, w.hanzi), h('div.muted.small', w.main)), h('span.pill', `${c.wrong}× Salah`)))
        : h('p.muted.small', 'Belum ada kata yang salah. Pertahankan!'));
  }
  function exportFile() {
    const blob = new Blob([S.exportJson()], { type: 'application/json' });
    const a = h('a', { href: URL.createObjectURL(blob), download: `kuaile-learn-cadangan-${new Date().toISOString().slice(0, 10)}.json` });
    document.body.append(a); a.click(); a.remove();
    toast('Cadangan disimpan.');
  }
  function importFile(e) {
    const f = e.target.files[0]; if (!f) return;
    f.text().then((txt) => { toast(S.importJson(txt) ? 'Progres berhasil dipulihkan.' : 'File cadangan tidak dikenali.'); draw(); });
  }
  draw();
  return root;
}

export { go };
