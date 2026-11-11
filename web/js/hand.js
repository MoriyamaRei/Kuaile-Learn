// Pengenal tulisan hanzi tanpa urutan goresan (algoritme sama dengan aplikasi):
// tulisan dan cetakan hanzi dinormalkan ke kotak 64×64, ditipiskan, lalu dibandingkan jarak chamfer dua arah.
import { allChars, shuffle } from './data.js';

const N = 64, RS = 130, CAP = 8, SURE = 2.0, MAYBE = 2.7;
const cache = new Map();
let canvas = null;

function distance(mask) {
  const d = new Int32Array(N * N).fill(999);
  const q = new Int32Array(N * N); let h = 0, t = 0;
  for (let i = 0; i < mask.length; i++) if (mask[i]) { d[i] = 0; q[t++] = i; }
  while (h < t) {
    const i = q[h++]; const y = (i / N) | 0, x = i % N;
    for (let dy = -1; dy <= 1; dy++) for (let dx = -1; dx <= 1; dx++) {
      if (!dx && !dy) continue;
      const yy = y + dy, xx = x + dx;
      if (yy < 0 || yy >= N || xx < 0 || xx >= N) continue;
      const j = yy * N + xx;
      if (d[j] > d[i] + 1) { d[j] = d[i] + 1; q[t++] = j; }
    }
  }
  return d;
}

function thin(img, w, h) {
  const at = (x, y) => (x < 0 || y < 0 || x >= w || y >= h ? 0 : img[y * w + x]);
  let changed = true;
  while (changed) {
    changed = false;
    for (let step = 0; step < 2; step++) {
      const del = [];
      for (let y = 0; y < h; y++) for (let x = 0; x < w; x++) {
        if (img[y * w + x] !== 1) continue;
        const p2 = at(x, y - 1), p3 = at(x + 1, y - 1), p4 = at(x + 1, y), p5 = at(x + 1, y + 1);
        const p6 = at(x, y + 1), p7 = at(x - 1, y + 1), p8 = at(x - 1, y), p9 = at(x - 1, y - 1);
        const b = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
        if (b < 2 || b > 6) continue;
        const seq = [p2, p3, p4, p5, p6, p7, p8, p9, p2];
        let a = 0; for (let k = 0; k < 8; k++) if (seq[k] === 0 && seq[k + 1] === 1) a++;
        if (a !== 1) continue;
        const ok = step === 0 ? (p2 * p4 * p6 === 0 && p4 * p6 * p8 === 0) : (p2 * p4 * p8 === 0 && p2 * p6 * p8 === 0);
        if (ok) del.push(y * w + x);
      }
      if (del.length) { changed = true; for (const i of del) img[i] = 0; }
    }
  }
}

function gridFrom(xs, ys) {
  const mask = new Uint8Array(N * N);
  if (xs.length) {
    let x0 = Infinity, x1 = -Infinity, y0 = Infinity, y1 = -Infinity;
    for (let i = 0; i < xs.length; i++) { x0 = Math.min(x0, xs[i]); x1 = Math.max(x1, xs[i]); y0 = Math.min(y0, ys[i]); y1 = Math.max(y1, ys[i]); }
    const s = Math.max(x1 - x0, y1 - y0, 1); const m = (N - 8) / s;
    const cx = (x0 + x1) / 2, cy = (y0 + y1) / 2;
    for (let i = 0; i < xs.length; i++) {
      const gx = Math.min(N - 1, Math.max(0, ((xs[i] - cx) * m + N / 2) | 0));
      const gy = Math.min(N - 1, Math.max(0, ((ys[i] - cy) * m + N / 2) | 0));
      mask[gy * N + gx] = 1;
    }
  }
  let count = 0; for (const v of mask) count += v;
  return { mask, dist: distance(mask), count };
}

function target(c) {
  let g = cache.get(c);
  if (g) return g;
  canvas = canvas || Object.assign(document.createElement('canvas'), { width: RS, height: RS });
  const ctx = canvas.getContext('2d', { willReadFrequently: true });
  ctx.clearRect(0, 0, RS, RS);
  ctx.fillStyle = '#000'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
  ctx.font = `${RS * 0.8}px WenKai`;
  ctx.fillText(c, RS / 2, RS / 2);
  const data = ctx.getImageData(0, 0, RS, RS).data;
  const px = new Uint8Array(RS * RS);
  for (let i = 0; i < RS * RS; i++) px[i] = data[i * 4 + 3] > 128 ? 1 : 0;
  thin(px, RS, RS);
  const xs = [], ys = [];
  for (let y = 0; y < RS; y++) for (let x = 0; x < RS; x++) if (px[y * RS + x]) { xs.push(x); ys.push(y); }
  g = gridFrom(xs, ys);
  cache.set(c, g);
  return g;
}

/** Goresan → titik rapat → kotak 64. strokes = [[{x,y}…]…] */
function userGrid(strokes) {
  const all = strokes.flat();
  if (all.length < 2) return null;
  let x0 = Infinity, x1 = -Infinity, y0 = Infinity, y1 = -Infinity;
  for (const p of all) { x0 = Math.min(x0, p.x); x1 = Math.max(x1, p.x); y0 = Math.min(y0, p.y); y1 = Math.max(y1, p.y); }
  const step = Math.max(x1 - x0, y1 - y0, 1) / 200;
  const xs = [], ys = [];
  for (const s of strokes) {
    if (s.length === 1) { xs.push(s[0].x); ys.push(s[0].y); }
    for (let i = 1; i < s.length; i++) {
      const a = s[i - 1], b = s[i];
      const dx = b.x - a.x, dy = b.y - a.y;
      const n = Math.max(1, (Math.hypot(dx, dy) / step) | 0);
      for (let k = 0; k <= n; k++) { xs.push(a.x + dx * k / n); ys.push(a.y + dy * k / n); }
    }
  }
  return gridFrom(xs, ys);
}

function cost(t, u) {
  if (!t.count || !u.count) return 99;
  let a = 0, b = 0;
  for (let i = 0; i < N * N; i++) {
    if (u.mask[i]) a += Math.min(t.dist[i], CAP);
    if (t.mask[i]) b += Math.min(u.dist[i], CAP);
  }
  return ((a / u.count) + (b / t.count)) / 2;
}

/** Menilai tulisan untuk satu hanzi target: 'benar', 'hampir', atau 'salah'. */
export function judge(c, strokes) {
  const u = userGrid(strokes);
  if (!u) return 'salah';
  const cg = cost(target(c), u);
  if (cg <= SURE) return 'benar';
  if (cg > MAYBE) return cg <= MAYBE + 0.5 ? 'hampir' : 'salah';
  const others = shuffle(allChars().filter((x) => x !== c)).slice(0, 40);
  const better = others.filter((x) => cost(target(x), u) < cg).length;
  return better <= 1 ? 'benar' : 'hampir';
}

/** Kandidat hanzi paling mirip untuk pencarian lewat tulisan. */
export function rank(strokes, limit = 10) {
  const u = userGrid(strokes);
  if (!u) return [];
  return allChars().map((c) => [c, cost(target(c), u)]).sort((a, b) => a[1] - b[1]).slice(0, limit).map((x) => x[0]);
}

/** Menyiapkan cetakan semua hanzi sedikit demi sedikit supaya halaman tidak macet. */
export async function prepare(onProgress = () => {}) {
  const chars = allChars().filter((c) => !cache.has(c));
  for (let i = 0; i < chars.length; i++) {
    target(chars[i]);
    if (i % 40 === 0) { onProgress(i / chars.length); await new Promise((r) => setTimeout(r, 0)); }
  }
  onProgress(1);
}
