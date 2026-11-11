// Membuat web/js/icons.js dari paket @mdi/js (Material Design Icons).
// Pakai: npm pack @mdi/js@7.4.47 && tar xzf mdi-js-7.4.47.tgz  (di folder sementara)
//       node tools/web_icons.js <folder sementara> web/js/icons.js
const mdi = require(process.argv[2] + '/package/commonjs/mdi.js');
const want = {
  home: 'mdiHome', cards: 'mdiCardsPlaying', quiz: 'mdiHelpBoxMultiple', book: 'mdiBookOpenPageVariant', person: 'mdiAccount',
  menu: 'mdiMenu', close: 'mdiClose', back: 'mdiArrowLeft', chevron: 'mdiChevronRight', speaker: 'mdiVolumeHigh',
  star: 'mdiStar', starOutline: 'mdiStarOutline', shuffle: 'mdiShuffleVariant', eye: 'mdiEye', eyeOff: 'mdiEyeOff',
  flame: 'mdiFire', trophy: 'mdiTrophy', target: 'mdiBullseyeArrow', bolt: 'mdiLightningBolt', cap: 'mdiSchool',
  numbers: 'mdiNumeric', calc: 'mdiCalculatorVariant', pen: 'mdiDraw', puzzle: 'mdiPuzzle', bulb: 'mdiLightbulbOn',
  search: 'mdiMagnify', filter: 'mdiTuneVariant', trash: 'mdiDeleteOutline', undo: 'mdiUndoVariant', check: 'mdiCheckCircle',
  sparkle: 'mdiCreation', music: 'mdiMusicNote', letters: 'mdiAlphabeticalVariant', translate: 'mdiTranslate', clock: 'mdiClockOutline',
  chart: 'mdiChartBar', download: 'mdiTrayArrowDown', upload: 'mdiTrayArrowUp', refresh: 'mdiRefresh', plus: 'mdiPlus',
  sprout: 'mdiSprout', party: 'mdiPartyPopper', thumb: 'mdiThumbUp', muscle: 'mdiArmFlex', handwave: 'mdiHandWave',
  library: 'mdiBookshelf', cardText: 'mdiCardTextOutline', sun: 'mdiWhiteBalanceSunny', moon: 'mdiWeatherNight', phone: 'mdiCellphone',
  backup: 'mdiCloudUploadOutline', warn: 'mdiAlertCircleOutline', play: 'mdiPlay', dice: 'mdiDice5', exam: 'mdiClipboardCheckOutline',
  // Tema kosakata
  t_keluarga: 'mdiAccountGroup', t_profesi: 'mdiBriefcase', t_tubuh: 'mdiHeartPulse', t_makanan: 'mdiNoodles', t_waktu: 'mdiClockTimeFour',
  t_tempat: 'mdiMapMarker', t_sekolah: 'mdiSchool', t_uang: 'mdiCashMultiple', t_rumah: 'mdiSofa', t_transport: 'mdiAirplane',
  t_alam: 'mdiPineTree', t_hobi: 'mdiPalette', t_perasaan: 'mdiEmoticonHappy', t_komunikasi: 'mdiChat', t_angka: 'mdiNumeric',
  t_masyarakat: 'mdiCityVariant', t_kerja: 'mdiRun', t_sifat: 'mdiShimmer', t_keterangan: 'mdiSpeedometer', t_ganti: 'mdiAccountQuestion',
  c_UMUM: 'mdiNumeric', c_HARGA: 'mdiCurrencyCny', c_DISKON: 'mdiTag', c_PERSEN: 'mdiPercent', c_JAM: 'mdiClockTimeFour',
  c_TANGGAL: 'mdiCalendarMonth', c_TELEPON: 'mdiPhone', c_UKURAN: 'mdiRuler', c_KIRA: 'mdiApproximatelyEqual', c_CAMPUR: 'mdiDice5',
  t_sambung: 'mdiLinkVariant', t_depan: 'mdiArrowRightBold', t_bantu: 'mdiWrench', t_partikel: 'mdiCircleMedium', t_konsep: 'mdiLightbulb',
};
const out = {}; const miss = [];
for (const [k, v] of Object.entries(want)) { if (mdi[v]) out[k] = mdi[v]; else miss.push(v); }
if (miss.length) { console.error('HILANG: ' + miss.join(', ')); process.exit(1); }
const body = Object.entries(out).map(([k, v]) => `  ${k}: '${v}',`).join('\n');
require('fs').writeFileSync(process.argv[3], `// Ikon web: satu gaya, dari Material Design Icons oleh Pictogrammers (Pictogrammers Free License,
// https://pictogrammers.com/docs/general/license/). Dipilih dari paket @mdi/js; tiap ikon berupa path SVG 24×24.

export const PATHS = {
${body}
};

/** Ikon SVG yang mengikuti warna teks (currentColor). */
export function icon(name, cls = '') {
  const el = document.createElement('span');
  el.className = 'ico' + (cls ? ' ' + cls.split('.').join(' ') : '');
  el.setAttribute('aria-hidden', 'true');
  el.innerHTML = \`<svg viewBox="0 0 24 24" fill="currentColor"><path d="\${PATHS[name] || ''}"/></svg>\`;
  return el;
}

/** Ikon tema kosakata (menggantikan emoji tema). */
export const themeIcon = (key, cls = '') => icon(PATHS['t_' + key] ? 't_' + key : 'library', cls);

/** Ikon kategori materi angka. */
export const catIcon = (id, cls = '') => icon(PATHS['c_' + id] ? 'c_' + id : 'numbers', cls);
`);
console.log(Object.keys(out).length + ' ikon');
