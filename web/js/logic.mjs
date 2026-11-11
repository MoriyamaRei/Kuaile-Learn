//region block: polyfills
if (typeof Math.imul === 'undefined') {
  Math.imul = function imul(a, b) {
    return (a & 4.29490176E9) * (b & 65535) + (a & 65535) * (b | 0) | 0;
  };
}
if (typeof ArrayBuffer.isView === 'undefined') {
  ArrayBuffer.isView = function (a) {
    return a != null && a.__proto__ != null && a.__proto__.__proto__ === Int8Array.prototype.__proto__;
  };
}
if (typeof Array.prototype.fill === 'undefined') {
  // Polyfill from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/fill#Polyfill
  Object.defineProperty(Array.prototype, 'fill', {value: function (value) {
    // Steps 1-2.
    if (this == null) {
      throw new TypeError('this is null or not defined');
    }
    var O = Object(this); // Steps 3-5.
    var len = O.length >>> 0; // Steps 6-7.
    var start = arguments[1];
    var relativeStart = start >> 0; // Step 8.
    var k = relativeStart < 0 ? Math.max(len + relativeStart, 0) : Math.min(relativeStart, len); // Steps 9-10.
    var end = arguments[2];
    var relativeEnd = end === undefined ? len : end >> 0; // Step 11.
    var finalValue = relativeEnd < 0 ? Math.max(len + relativeEnd, 0) : Math.min(relativeEnd, len); // Step 12.
    while (k < finalValue) {
      O[k] = value;
      k++;
    }
    ; // Step 13.
    return O;
  }});
}
[Int8Array, Int16Array, Uint16Array, Int32Array, Float32Array, Float64Array].forEach(function (TypedArray) {
  if (typeof TypedArray.prototype.fill === 'undefined') {
    Object.defineProperty(TypedArray.prototype, 'fill', {value: Array.prototype.fill});
  }
});
if (typeof Math.clz32 === 'undefined') {
  Math.clz32 = function (log, LN2) {
    return function (x) {
      var asUint = x >>> 0;
      if (asUint === 0) {
        return 32;
      }
      return 31 - (log(asUint) / LN2 | 0) | 0; // the "| 0" acts like math.floor
    };
  }(Math.log, Math.LN2);
}
if (typeof String.prototype.startsWith === 'undefined') {
  Object.defineProperty(String.prototype, 'startsWith', {value: function (searchString, position) {
    position = position || 0;
    return this.lastIndexOf(searchString, position) === position;
  }});
}
//endregion
//region block: imports
var imul_0 = Math.imul;
var isView = ArrayBuffer.isView;
var clz32 = Math.clz32;
//endregion
//region block: pre-declaration
class CharSequence {}
class Number_0 {}
class Unit {
  toString() {
    return 'kotlin.Unit';
  }
}
class _no_name_provided__qut3iv {
  constructor($this_asIterable) {
    this.r1_1 = $this_asIterable;
  }
  r() {
    // Inline function 'kotlin.sequences.asIterable.<anonymous>' call
    return this.r1_1.r();
  }
}
class Companion {
  constructor() {
    Companion_instance = this;
    this.v1_1 = _Char___init__impl__6a9atx(0);
    this.w1_1 = _Char___init__impl__6a9atx(65535);
    this.x1_1 = _Char___init__impl__6a9atx(55296);
    this.y1_1 = _Char___init__impl__6a9atx(56319);
    this.z1_1 = _Char___init__impl__6a9atx(56320);
    this.a2_1 = _Char___init__impl__6a9atx(57343);
    this.b2_1 = _Char___init__impl__6a9atx(55296);
    this.c2_1 = _Char___init__impl__6a9atx(57343);
    this.d2_1 = 2;
    this.e2_1 = 16;
  }
}
class Char {
  constructor(value) {
    Companion_getInstance();
    this.u1_1 = value;
  }
  f2(other) {
    return Char__compareTo_impl_ypi4mb(this.u1_1, other);
  }
  g2(other) {
    return Char__compareTo_impl_ypi4mb_0(this, other);
  }
  toString() {
    return toString(this.u1_1);
  }
  equals(other) {
    return Char__equals_impl_x6719k(this.u1_1, other);
  }
  hashCode() {
    return Char__hashCode_impl_otmys(this.u1_1);
  }
}
class Collection {}
class KtList {}
class KtSet {}
class Entry {}
class KtMap {}
class Companion_0 {}
class Enum {
  constructor(name, ordinal) {
    this.o2_1 = name;
    this.p2_1 = ordinal;
  }
  q2(other) {
    return compareTo(this.p2_1, other.p2_1);
  }
  g2(other) {
    return this.q2(other instanceof Enum ? other : THROW_CCE());
  }
  equals(other) {
    return this === other;
  }
  hashCode() {
    return identityHashCode(this);
  }
  toString() {
    return this.o2_1;
  }
}
class Companion_1 {
  constructor() {
    Companion_instance_1 = this;
    this.r2_1 = new Long(0, -2147483648);
    this.s2_1 = new Long(-1, 2147483647);
    this.t2_1 = 8;
    this.u2_1 = 64;
  }
}
class Long extends Number_0 {
  constructor(low, high) {
    Companion_getInstance_1();
    super();
    this.n1_1 = low;
    this.o1_1 = high;
  }
  p1(other) {
    return compare(this, other);
  }
  g2(other) {
    return this.p1(other instanceof Long ? other : THROW_CCE());
  }
  v2(other) {
    return add(this, other);
  }
  w2(other) {
    return subtract(this, other);
  }
  x2(other) {
    return multiply(this, other);
  }
  y2(other) {
    return divide(this, other);
  }
  z2(other) {
    return modulo(this, other);
  }
  a3() {
    return this.b3().v2(new Long(1, 0));
  }
  c3(other) {
    return new LongRange(this, other);
  }
  d3(bitCount) {
    return shiftLeft(this, bitCount);
  }
  e3(bitCount) {
    return shiftRightUnsigned(this, bitCount);
  }
  f3(other) {
    return new Long(this.n1_1 & other.n1_1, this.o1_1 & other.o1_1);
  }
  g3(other) {
    return new Long(this.n1_1 ^ other.n1_1, this.o1_1 ^ other.o1_1);
  }
  b3() {
    return new Long(~this.n1_1, ~this.o1_1);
  }
  h3() {
    return this.n1_1;
  }
  i3() {
    return toNumber(this);
  }
  toString() {
    return toStringImpl(this, 10);
  }
  equals(other) {
    var tmp;
    if (other instanceof Long) {
      tmp = equalsLong(this, other);
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return hashCode_0(this);
  }
  valueOf() {
    return this.i3();
  }
}
class FunctionAdapter {}
class arrayIterator$1 {
  constructor($array) {
    this.l3_1 = $array;
    this.k3_1 = 0;
  }
  s() {
    return !(this.k3_1 === this.l3_1.length);
  }
  t() {
    var tmp;
    if (!(this.k3_1 === this.l3_1.length)) {
      var tmp1 = this.k3_1;
      this.k3_1 = tmp1 + 1 | 0;
      tmp = this.l3_1[tmp1];
    } else {
      throw NoSuchElementException.l('' + this.k3_1);
    }
    return tmp;
  }
}
class Digit {
  constructor() {
    Digit_instance = this;
    var tmp = this;
    // Inline function 'kotlin.intArrayOf' call
    tmp.f4_1 = new Int32Array([48, 1632, 1776, 1984, 2406, 2534, 2662, 2790, 2918, 3046, 3174, 3302, 3430, 3558, 3664, 3792, 3872, 4160, 4240, 6112, 6160, 6470, 6608, 6784, 6800, 6992, 7088, 7232, 7248, 42528, 43216, 43264, 43472, 43504, 43600, 44016, 65296]);
  }
}
class Letter {
  constructor() {
    Letter_instance = this;
    var toBase64 = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    var fromBase64 = new Int32Array(128);
    var inductionVariable = 0;
    var last = charSequenceLength(toBase64) - 1 | 0;
    if (inductionVariable <= last)
      do {
        var i = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        // Inline function 'kotlin.code' call
        var this_0 = charSequenceGet(toBase64, i);
        fromBase64[Char__toInt_impl_vasixd(this_0)] = i;
      }
       while (inductionVariable <= last);
    var rangeStartDiff = 'hCgBpCQGYHZH5BRpBPPPPPPRMP5BPPlCPP6BkEPPPPcPXPzBvBrB3BOiDoBHwD+E3DauCnFmBmB2D6E1BlBTiBmBlBP5BhBiBrBvBjBqBnBPRtBiCmCtBlB0BmB5BiB7BmBgEmChBZgCoEoGVpBSfRhBPqKQ2BwBYoFgB4CJuTiEvBuCuDrF5DgEgFlJ1DgFmBQtBsBRGsB+BPiBlD1EIjDPRPPPQPPPPPGQSQS/DxENVNU+B9zCwBwBPPCkDPNnBPqDYY1R8B7FkFgTgwGgwUwmBgKwBuBScmEP/BPPPPPPrBP8B7F1B/ErBqC6B7BiBmBfQsBUwCw/KwqIwLwETPcPjQgJxFgBlBsD';
    var diff = decodeVarLenBase64(rangeStartDiff, fromBase64, 222);
    var start = new Int32Array(diff.length);
    var inductionVariable_0 = 0;
    var last_0 = diff.length - 1 | 0;
    if (inductionVariable_0 <= last_0)
      do {
        var i_0 = inductionVariable_0;
        inductionVariable_0 = inductionVariable_0 + 1 | 0;
        if (i_0 === 0) {
          start[i_0] = diff[i_0];
        } else {
          start[i_0] = start[i_0 - 1 | 0] + diff[i_0] | 0;
        }
      }
       while (inductionVariable_0 <= last_0);
    this.g4_1 = start;
    var rangeLength = 'aaMBXHYH5BRpBPPPPPPRMP5BPPlCPPzBDOOPPcPXPzBvBjB3BOhDmBBpB7DoDYxB+EiBP1DoExBkBQhBekBPmBgBhBctBiBMWOOXhCsBpBkBUV3Ba4BkB0DlCgBXgBtD4FSdBfPhBPpKP0BvBXjEQ2CGsT8DhBtCqDpFvD1D3E0IrD2EkBJrBDOBsB+BPiBlB1EIjDPPPPPPPPPPPGPPMNLsBNPNPKCvBvBPPCkDPBmBPhDXXgD4B6FzEgDguG9vUtkB9JcuBSckEP/BPPPPPPBPf4FrBjEhBpC3B5BKaWPrBOwCk/KsCuLqDHPbPxPsFtEaaqDL';
    this.h4_1 = decodeVarLenBase64(rangeLength, fromBase64, 222);
    var rangeCategory = 'GFjgggUHGGFFZZZmzpz5qB6s6020B60ptltB6smt2sB60mz22B1+vv+8BZZ5s2850BW5q1ymtB506smzBF3q1q1qB1q1q1+Bgii4wDTm74g3KiggxqM60q1q1Bq1o1q1BF1qlrqrBZ2q5wprBGFZWWZGHFsjiooLowgmOowjkwCkgoiIk7ligGogiioBkwkiYkzj2oNoi+sbkwj04DghhkQ8wgiYkgoioDsgnkwC4gikQ//v+85BkwvoIsgoyI4yguI0whiwEowri4CoghsJowgqYowgm4DkwgsY/nwnzPowhmYkg6wI8yggZswikwHgxgmIoxgqYkwgk4DkxgmIkgoioBsgssoBgzgyI8g9gL8g9kI0wgwJoxgkoC0wgioFkw/wI0w53iF4gioYowjmgBHGq1qkgwBF1q1q8qBHwghuIwghyKk0goQkwgoQk3goQHGFHkyg0pBgxj6IoinkxDswno7Ikwhz9Bo0gioB8z48Rwli0xN0mpjoX8w78pDwltoqKHFGGwwgsIHFH3q1q16BFHWFZ1q10q1B2qlwq1B1q10q1B2q1yq1B6q1gq1Biq1qhxBir1qp1Bqt1q1qB1g1q1+B//3q16B///q1qBH/qlqq9Bholqq9B1i00a1q10qD1op1HkwmigEigiy6Cptogq1Bixo1kDq7/j00B2qgoBWGFm1lz50B6s5q1+BGWhggzhwBFFhgk4//Bo2jigE8wguI8wguI8wgugUog1qoB4qjmIwwi2KgkYHHH4lBgiFWkgIWoghssMmz5smrBZ3q1y50B5sm7gzBtz1smzB5smz50BqzqtmzB5sgzqzBF2/9//5BowgoIwmnkzPkwgk4C8ys65BkgoqI0wgy6FghquZo2giY0ghiIsgh24B4ghsQ8QF/v1q1OFs0O8iCHHF1qggz/B8wg6Iznv+//B08QgohsjK0QGFk7hsQ4gB';
    this.i4_1 = decodeVarLenBase64(rangeCategory, fromBase64, 222);
  }
}
class OtherLowercase {
  constructor() {
    OtherLowercase_instance = this;
    var tmp = this;
    // Inline function 'kotlin.intArrayOf' call
    tmp.j4_1 = new Int32Array([170, 186, 688, 704, 736, 837, 890, 7468, 7544, 7579, 8305, 8319, 8336, 8560, 9424, 11388, 42652, 42864, 43000, 43868]);
    var tmp_0 = this;
    // Inline function 'kotlin.intArrayOf' call
    tmp_0.k4_1 = new Int32Array([1, 1, 9, 2, 5, 1, 1, 63, 1, 37, 1, 1, 13, 16, 26, 2, 2, 1, 2, 4]);
  }
}
class Comparator {}
class AbstractCollection {
  static m4() {
    return createThis(this);
  }
  h2(element) {
    var tmp$ret$0;
    $l$block_0: {
      // Inline function 'kotlin.collections.any' call
      var tmp;
      if (isInterface(this, Collection)) {
        tmp = this.b1();
      } else {
        tmp = false;
      }
      if (tmp) {
        tmp$ret$0 = false;
        break $l$block_0;
      }
      var tmp0_iterator = this.r();
      while (tmp0_iterator.s()) {
        var element_0 = tmp0_iterator.t();
        // Inline function 'kotlin.collections.AbstractCollection.contains.<anonymous>' call
        if (equals(element_0, element)) {
          tmp$ret$0 = true;
          break $l$block_0;
        }
      }
      tmp$ret$0 = false;
    }
    return tmp$ret$0;
  }
  i2(elements) {
    var tmp$ret$0;
    $l$block_0: {
      // Inline function 'kotlin.collections.all' call
      var tmp;
      if (isInterface(elements, Collection)) {
        tmp = elements.b1();
      } else {
        tmp = false;
      }
      if (tmp) {
        tmp$ret$0 = true;
        break $l$block_0;
      }
      var tmp0_iterator = elements.r();
      while (tmp0_iterator.s()) {
        var element = tmp0_iterator.t();
        // Inline function 'kotlin.collections.AbstractCollection.containsAll.<anonymous>' call
        if (!this.h2(element)) {
          tmp$ret$0 = false;
          break $l$block_0;
        }
      }
      tmp$ret$0 = true;
    }
    return tmp$ret$0;
  }
  b1() {
    return this.y() === 0;
  }
  toString() {
    return joinToString_0(this, ', ', '[', ']', VOID, VOID, AbstractCollection$toString$lambda(this));
  }
  toArray() {
    return collectionToArray(this);
  }
}
class AbstractMutableCollection extends AbstractCollection {
  static l4() {
    return this.m4();
  }
  a1(elements) {
    this.n4();
    var modified = false;
    var tmp0_iterator = elements.r();
    while (tmp0_iterator.s()) {
      var element = tmp0_iterator.t();
      if (this.h(element))
        modified = true;
    }
    return modified;
  }
  toJSON() {
    return this.toArray();
  }
  n4() {
  }
}
class IteratorImpl {
  constructor($outer, $box) {
    boxApply(this, $box);
    this.q4_1 = $outer;
    this.o4_1 = 0;
    this.p4_1 = -1;
  }
  s() {
    return this.o4_1 < this.q4_1.y();
  }
  t() {
    if (!this.s())
      throw NoSuchElementException.r4();
    var tmp = this;
    var tmp1 = this.o4_1;
    this.o4_1 = tmp1 + 1 | 0;
    tmp.p4_1 = tmp1;
    return this.q4_1.d1(this.p4_1);
  }
}
class ListIteratorImpl extends IteratorImpl {
  constructor($outer, index, $box) {
    if ($box === VOID)
      $box = {};
    $box.v4_1 = $outer;
    super($outer, $box);
    Companion_instance_5.x4(index, this.v4_1.y());
    this.o4_1 = index;
  }
}
class AbstractMutableList extends AbstractMutableCollection {
  static z4() {
    var $this = this.l4();
    $this.y4_1 = 0;
    return $this;
  }
  h(element) {
    this.n4();
    this.a5(this.y(), element);
    return true;
  }
  r() {
    return new IteratorImpl(this);
  }
  h2(element) {
    return this.b5(element) >= 0;
  }
  b5(element) {
    var tmp$ret$1;
    $l$block: {
      // Inline function 'kotlin.collections.indexOfFirst' call
      var index = 0;
      var tmp0_iterator = this.r();
      while (tmp0_iterator.s()) {
        var item = tmp0_iterator.t();
        // Inline function 'kotlin.collections.AbstractMutableList.indexOf.<anonymous>' call
        if (equals(item, element)) {
          tmp$ret$1 = index;
          break $l$block;
        }
        index = index + 1 | 0;
      }
      tmp$ret$1 = -1;
    }
    return tmp$ret$1;
  }
  f1(index) {
    return new ListIteratorImpl(this, index);
  }
  equals(other) {
    if (other === this)
      return true;
    if (!(!(other == null) ? isInterface(other, KtList) : false))
      return false;
    return Companion_instance_5.c5(this, other);
  }
  hashCode() {
    return Companion_instance_5.d5(this);
  }
}
class AbstractMap {
  static l5() {
    var $this = createThis(this);
    $this.j5_1 = null;
    $this.k5_1 = null;
    return $this;
  }
  l2(key) {
    return !(implFindEntry(this, key) == null);
  }
  n5(entry) {
    if (!(!(entry == null) ? isInterface(entry, Entry) : false))
      return false;
    var key = entry.j2();
    var value = entry.k2();
    // Inline function 'kotlin.collections.get' call
    var ourValue = (isInterface(this, KtMap) ? this : THROW_CCE()).m2(key);
    if (!equals(value, ourValue)) {
      return false;
    }
    var tmp;
    if (ourValue == null) {
      // Inline function 'kotlin.collections.containsKey' call
      tmp = !(isInterface(this, KtMap) ? this : THROW_CCE()).l2(key);
    } else {
      tmp = false;
    }
    if (tmp) {
      return false;
    }
    return true;
  }
  equals(other) {
    if (other === this)
      return true;
    if (!(!(other == null) ? isInterface(other, KtMap) : false))
      return false;
    if (!(this.y() === other.y()))
      return false;
    var tmp$ret$0;
    $l$block_0: {
      // Inline function 'kotlin.collections.all' call
      var this_0 = other.n2();
      var tmp;
      if (isInterface(this_0, Collection)) {
        tmp = this_0.b1();
      } else {
        tmp = false;
      }
      if (tmp) {
        tmp$ret$0 = true;
        break $l$block_0;
      }
      var tmp0_iterator = this_0.r();
      while (tmp0_iterator.s()) {
        var element = tmp0_iterator.t();
        // Inline function 'kotlin.collections.AbstractMap.equals.<anonymous>' call
        if (!this.n5(element)) {
          tmp$ret$0 = false;
          break $l$block_0;
        }
      }
      tmp$ret$0 = true;
    }
    return tmp$ret$0;
  }
  m2(key) {
    var tmp0_safe_receiver = implFindEntry(this, key);
    return tmp0_safe_receiver == null ? null : tmp0_safe_receiver.k2();
  }
  hashCode() {
    return hashCode(this.n2());
  }
  y() {
    return this.n2().y();
  }
  toString() {
    var tmp = this.n2();
    return joinToString_0(tmp, ', ', '{', '}', VOID, VOID, AbstractMap$toString$lambda(this));
  }
  oa(entry) {
    return toString_3(this, entry.j2()) + '=' + toString_3(this, entry.k2());
  }
}
class AbstractMutableMap extends AbstractMap {
  static i5() {
    var $this = this.l5();
    $this.g5_1 = null;
    $this.h5_1 = null;
    return $this;
  }
}
class AbstractMutableSet extends AbstractMutableCollection {
  static o5() {
    return this.l4();
  }
  equals(other) {
    if (other === this)
      return true;
    if (!(!(other == null) ? isInterface(other, KtSet) : false))
      return false;
    return Companion_instance_7.p5(this, other);
  }
  hashCode() {
    return Companion_instance_7.q5(this);
  }
}
class Companion_2 {
  constructor() {
    Companion_instance_2 = this;
    var tmp = this;
    // Inline function 'kotlin.also' call
    var this_0 = ArrayList.z(0);
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.collections.Companion.Empty.<anonymous>' call
    this_0.w_1 = true;
    tmp.r5_1 = this_0;
  }
}
class RandomAccess {}
class ArrayList extends AbstractMutableList {
  static e4(array) {
    Companion_getInstance_2();
    var $this = this.z4();
    $this.v_1 = array;
    $this.w_1 = false;
    return $this;
  }
  static h1() {
    Companion_getInstance_2();
    // Inline function 'kotlin.emptyArray' call
    var tmp$ret$0 = [];
    return this.e4(tmp$ret$0);
  }
  static z(initialCapacity) {
    Companion_getInstance_2();
    // Inline function 'kotlin.emptyArray' call
    var tmp$ret$0 = [];
    var $this = this.e4(tmp$ret$0);
    // Inline function 'kotlin.require' call
    // Inline function 'kotlin.contracts.contract' call
    if (!(initialCapacity >= 0)) {
      // Inline function 'kotlin.collections.ArrayList.<init>.<anonymous>' call
      var message = 'Negative initial capacity: ' + initialCapacity;
      throw IllegalArgumentException.p(toString_1(message));
    }
    return $this;
  }
  static x(elements) {
    Companion_getInstance_2();
    // Inline function 'kotlin.collections.toTypedArray' call
    var tmp$ret$0 = copyToArray(elements);
    return this.e4(tmp$ret$0);
  }
  t5() {
    this.n4();
    this.w_1 = true;
    return this.y() > 0 ? this : Companion_getInstance_2().r5_1;
  }
  y() {
    return this.v_1.length;
  }
  d1(index) {
    var tmp = this.v_1[rangeCheck(this, index)];
    return (tmp == null ? true : !(tmp == null)) ? tmp : THROW_CCE();
  }
  g1(index, element) {
    this.n4();
    rangeCheck(this, index);
    // Inline function 'kotlin.apply' call
    var this_0 = this.v_1[index];
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.collections.ArrayList.set.<anonymous>' call
    this.v_1[index] = element;
    var tmp = this_0;
    return (tmp == null ? true : !(tmp == null)) ? tmp : THROW_CCE();
  }
  h(element) {
    this.n4();
    // Inline function 'kotlin.js.asDynamic' call
    this.v_1.push(element);
    this.y4_1 = this.y4_1 + 1 | 0;
    return true;
  }
  a5(index, element) {
    this.n4();
    // Inline function 'kotlin.js.asDynamic' call
    this.v_1.splice(insertionRangeCheck(this, index), 0, element);
    this.y4_1 = this.y4_1 + 1 | 0;
  }
  a1(elements) {
    this.n4();
    if (elements.b1())
      return false;
    var offset = increaseLength(this, elements.y());
    // Inline function 'kotlin.collections.forEachIndexed' call
    var index = 0;
    var tmp0_iterator = elements.r();
    while (tmp0_iterator.s()) {
      var item = tmp0_iterator.t();
      // Inline function 'kotlin.collections.ArrayList.addAll.<anonymous>' call
      var tmp1 = index;
      index = tmp1 + 1 | 0;
      var index_0 = checkIndexOverflow(tmp1);
      this.v_1[offset + index_0 | 0] = item;
    }
    this.y4_1 = this.y4_1 + 1 | 0;
    return true;
  }
  b5(element) {
    return indexOf(this.v_1, element);
  }
  toString() {
    return arrayToString(this.v_1);
  }
  u5() {
    return [].slice.call(this.v_1);
  }
  toArray() {
    return this.u5();
  }
  n4() {
    if (this.w_1)
      throw UnsupportedOperationException.y5();
  }
}
class HashMap extends AbstractMutableMap {
  static f6(internalMap) {
    var $this = this.i5();
    init_kotlin_collections_HashMap($this);
    $this.d6_1 = internalMap;
    return $this;
  }
  static g6() {
    return this.f6(InternalHashMap.r6());
  }
  l2(key) {
    return this.d6_1.s6(key);
  }
  n2() {
    var tmp0_elvis_lhs = this.e6_1;
    var tmp;
    if (tmp0_elvis_lhs == null) {
      // Inline function 'kotlin.also' call
      var this_0 = HashMapEntrySet.u6(this.d6_1);
      // Inline function 'kotlin.contracts.contract' call
      // Inline function 'kotlin.collections.HashMap.<get-entries>.<anonymous>' call
      this.e6_1 = this_0;
      tmp = this_0;
    } else {
      tmp = tmp0_elvis_lhs;
    }
    return tmp;
  }
  m2(key) {
    return this.d6_1.m2(key);
  }
  m5(key, value) {
    return this.d6_1.m5(key, value);
  }
  y() {
    return this.d6_1.y();
  }
}
class HashMapEntrySetBase extends AbstractMutableSet {
  static w6(backing) {
    var $this = this.o5();
    $this.v6_1 = backing;
    return $this;
  }
  y() {
    return this.v6_1.y();
  }
  b1() {
    return this.v6_1.y() === 0;
  }
  y6(element) {
    return this.v6_1.a7(element);
  }
  h2(element) {
    if (!(!(element == null) ? isInterface(element, Entry) : false))
      return false;
    return this.y6((!(element == null) ? isInterface(element, Entry) : false) ? element : THROW_CCE());
  }
  z6(element) {
    throw UnsupportedOperationException.y5();
  }
  h(element) {
    return this.z6((!(element == null) ? isInterface(element, Entry) : false) ? element : THROW_CCE());
  }
  a1(elements) {
    throw UnsupportedOperationException.y5();
  }
  i2(elements) {
    return this.v6_1.b7(elements);
  }
  n4() {
    return this.v6_1.c7();
  }
}
class HashMapEntrySet extends HashMapEntrySetBase {
  static u6(backing) {
    return this.w6(backing);
  }
  r() {
    return this.v6_1.x6();
  }
}
class HashSet extends AbstractMutableSet {
  static e7(map) {
    var $this = this.o5();
    init_kotlin_collections_HashSet($this);
    $this.d7_1 = map;
    return $this;
  }
  static f7() {
    return this.e7(InternalHashMap.r6());
  }
  static g7(initialCapacity, loadFactor) {
    return this.e7(InternalHashMap.h7(initialCapacity, loadFactor));
  }
  static i7(initialCapacity) {
    return this.g7(initialCapacity, 1.0);
  }
  h(element) {
    return this.d7_1.m5(element, true) == null;
  }
  h2(element) {
    return this.d7_1.s6(element);
  }
  b1() {
    return this.d7_1.y() === 0;
  }
  r() {
    return this.d7_1.j7();
  }
  y() {
    return this.d7_1.y();
  }
}
class Companion_3 {
  constructor() {
    this.o7_1 = -1640531527;
    this.p7_1 = 8;
    this.q7_1 = 2;
    this.r7_1 = -1;
  }
}
class Itr {
  constructor(map) {
    this.s7_1 = map;
    this.t7_1 = 0;
    this.u7_1 = -1;
    this.v7_1 = this.s7_1.o6_1;
    this.w7();
  }
  w7() {
    while (this.t7_1 < this.s7_1.m6_1 && this.s7_1.j6_1[this.t7_1] < 0) {
      this.t7_1 = this.t7_1 + 1 | 0;
    }
  }
  s() {
    return this.t7_1 < this.s7_1.m6_1;
  }
  x7() {
    if (!(this.s7_1.o6_1 === this.v7_1))
      throw ConcurrentModificationException.b8();
  }
}
class KeysItr extends Itr {
  t() {
    this.x7();
    if (this.t7_1 >= this.s7_1.m6_1)
      throw NoSuchElementException.r4();
    var tmp = this;
    var tmp1 = this.t7_1;
    this.t7_1 = tmp1 + 1 | 0;
    tmp.u7_1 = tmp1;
    var result = this.s7_1.h6_1[this.u7_1];
    this.w7();
    return result;
  }
}
class EntriesItr extends Itr {
  t() {
    this.x7();
    if (this.t7_1 >= this.s7_1.m6_1)
      throw NoSuchElementException.r4();
    var tmp = this;
    var tmp1 = this.t7_1;
    this.t7_1 = tmp1 + 1 | 0;
    tmp.u7_1 = tmp1;
    var result = new EntryRef(this.s7_1, this.u7_1);
    this.w7();
    return result;
  }
  k8() {
    if (this.t7_1 >= this.s7_1.m6_1)
      throw NoSuchElementException.r4();
    var tmp = this;
    var tmp1 = this.t7_1;
    this.t7_1 = tmp1 + 1 | 0;
    tmp.u7_1 = tmp1;
    // Inline function 'kotlin.hashCode' call
    var tmp0_safe_receiver = this.s7_1.h6_1[this.u7_1];
    var tmp1_elvis_lhs = tmp0_safe_receiver == null ? null : hashCode(tmp0_safe_receiver);
    var tmp_0 = tmp1_elvis_lhs == null ? 0 : tmp1_elvis_lhs;
    // Inline function 'kotlin.hashCode' call
    var tmp0_safe_receiver_0 = ensureNotNull(this.s7_1.i6_1)[this.u7_1];
    var tmp1_elvis_lhs_0 = tmp0_safe_receiver_0 == null ? null : hashCode(tmp0_safe_receiver_0);
    var result = tmp_0 ^ (tmp1_elvis_lhs_0 == null ? 0 : tmp1_elvis_lhs_0);
    this.w7();
    return result;
  }
  l8(sb) {
    if (this.t7_1 >= this.s7_1.m6_1)
      throw NoSuchElementException.r4();
    var tmp = this;
    var tmp1 = this.t7_1;
    this.t7_1 = tmp1 + 1 | 0;
    tmp.u7_1 = tmp1;
    var key = this.s7_1.h6_1[this.u7_1];
    if (equals(key, this.s7_1)) {
      sb.n8('(this Map)');
    } else {
      sb.m8(key);
    }
    sb.o8(_Char___init__impl__6a9atx(61));
    var value = ensureNotNull(this.s7_1.i6_1)[this.u7_1];
    if (equals(value, this.s7_1)) {
      sb.n8('(this Map)');
    } else {
      sb.m8(value);
    }
    this.w7();
  }
}
class EntryRef {
  constructor(map, index) {
    this.p8_1 = map;
    this.q8_1 = index;
  }
  j2() {
    return this.p8_1.h6_1[this.q8_1];
  }
  k2() {
    return ensureNotNull(this.p8_1.i6_1)[this.q8_1];
  }
  equals(other) {
    var tmp;
    var tmp_0;
    if (!(other == null) ? isInterface(other, Entry) : false) {
      tmp_0 = equals(other.j2(), this.j2());
    } else {
      tmp_0 = false;
    }
    if (tmp_0) {
      tmp = equals(other.k2(), this.k2());
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    // Inline function 'kotlin.hashCode' call
    var tmp0_safe_receiver = this.j2();
    var tmp1_elvis_lhs = tmp0_safe_receiver == null ? null : hashCode(tmp0_safe_receiver);
    var tmp = tmp1_elvis_lhs == null ? 0 : tmp1_elvis_lhs;
    // Inline function 'kotlin.hashCode' call
    var tmp0_safe_receiver_0 = this.k2();
    var tmp1_elvis_lhs_0 = tmp0_safe_receiver_0 == null ? null : hashCode(tmp0_safe_receiver_0);
    return tmp ^ (tmp1_elvis_lhs_0 == null ? 0 : tmp1_elvis_lhs_0);
  }
  toString() {
    return toString_0(this.j2()) + '=' + toString_0(this.k2());
  }
}
class InternalMap {}
function containsAllEntries(m) {
  var tmp$ret$0;
  $l$block_0: {
    // Inline function 'kotlin.collections.all' call
    var tmp;
    if (isInterface(m, Collection)) {
      tmp = m.b1();
    } else {
      tmp = false;
    }
    if (tmp) {
      tmp$ret$0 = true;
      break $l$block_0;
    }
    var tmp0_iterator = m.r();
    while (tmp0_iterator.s()) {
      var element = tmp0_iterator.t();
      // Inline function 'kotlin.collections.InternalMap.containsAllEntries.<anonymous>' call
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      var entry = element;
      var tmp_0;
      if (!(entry == null) ? isInterface(entry, Entry) : false) {
        tmp_0 = this.v8(entry);
      } else {
        tmp_0 = false;
      }
      if (!tmp_0) {
        tmp$ret$0 = false;
        break $l$block_0;
      }
    }
    tmp$ret$0 = true;
  }
  return tmp$ret$0;
}
class InternalHashMap {
  static r8(keysArray, valuesArray, presenceArray, hashArray, maxProbeDistance, length) {
    var $this = createThis(this);
    $this.h6_1 = keysArray;
    $this.i6_1 = valuesArray;
    $this.j6_1 = presenceArray;
    $this.k6_1 = hashArray;
    $this.l6_1 = maxProbeDistance;
    $this.m6_1 = length;
    $this.n6_1 = computeShift(Companion_instance_3, _get_hashSize__tftcho($this));
    $this.o6_1 = 0;
    $this.p6_1 = 0;
    $this.q6_1 = false;
    return $this;
  }
  y() {
    return this.p6_1;
  }
  static r6() {
    return this.s8(8);
  }
  static s8(initialCapacity) {
    return this.r8(arrayOfUninitializedElements(initialCapacity), null, new Int32Array(initialCapacity), new Int32Array(computeHashSize(Companion_instance_3, initialCapacity)), 2, 0);
  }
  static h7(initialCapacity, loadFactor) {
    var $this = this.s8(initialCapacity);
    // Inline function 'kotlin.require' call
    // Inline function 'kotlin.contracts.contract' call
    if (!(loadFactor > 0)) {
      // Inline function 'kotlin.collections.InternalHashMap.<init>.<anonymous>' call
      var message = 'Non-positive load factor: ' + loadFactor;
      throw IllegalArgumentException.p(toString_1(message));
    }
    return $this;
  }
  t8() {
    this.c7();
    this.q6_1 = true;
  }
  m2(key) {
    var index = findKey(this, key);
    if (index < 0)
      return null;
    return ensureNotNull(this.i6_1)[index];
  }
  s6(key) {
    return findKey(this, key) >= 0;
  }
  m5(key, value) {
    var index = addKey(this, key);
    var valuesArray = allocateValuesArray(this);
    if (index < 0) {
      var oldValue = valuesArray[(-index | 0) - 1 | 0];
      valuesArray[(-index | 0) - 1 | 0] = value;
      return oldValue;
    } else {
      valuesArray[index] = value;
      return null;
    }
  }
  equals(other) {
    var tmp;
    if (other === this) {
      tmp = true;
    } else {
      var tmp_0;
      if (!(other == null) ? isInterface(other, KtMap) : false) {
        tmp_0 = contentEquals(this, other);
      } else {
        tmp_0 = false;
      }
      tmp = tmp_0;
    }
    return tmp;
  }
  hashCode() {
    var result = 0;
    var it = this.x6();
    while (it.s()) {
      result = result + it.k8() | 0;
    }
    return result;
  }
  toString() {
    var sb = StringBuilder.u8(2 + imul_0(this.p6_1, 3) | 0);
    sb.n8('{');
    var i = 0;
    var it = this.x6();
    while (it.s()) {
      if (i > 0) {
        sb.n8(', ');
      }
      it.l8(sb);
      i = i + 1 | 0;
    }
    sb.n8('}');
    return sb.toString();
  }
  c7() {
    if (this.q6_1)
      throw UnsupportedOperationException.y5();
  }
  a7(entry) {
    var index = findKey(this, entry.j2());
    if (index < 0)
      return false;
    return equals(ensureNotNull(this.i6_1)[index], entry.k2());
  }
  v8(entry) {
    return this.a7(isInterface(entry, Entry) ? entry : THROW_CCE());
  }
  j7() {
    return new KeysItr(this);
  }
  x6() {
    return new EntriesItr(this);
  }
}
class EmptyHolder {
  constructor() {
    EmptyHolder_instance = this;
    var tmp = this;
    // Inline function 'kotlin.also' call
    var this_0 = InternalHashMap.s8(0);
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.collections.EmptyHolder.value.<anonymous>' call
    this_0.t8();
    tmp.w8_1 = LinkedHashMap.d9(this_0);
  }
}
class LinkedHashMap extends HashMap {
  static e9() {
    var $this = this.g6();
    init_kotlin_collections_LinkedHashMap($this);
    return $this;
  }
  static d9(internalMap) {
    var $this = this.f6(internalMap);
    init_kotlin_collections_LinkedHashMap($this);
    return $this;
  }
  t5() {
    this.d6_1.t8();
    var tmp;
    if (this.y() > 0) {
      tmp = this;
    } else {
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      tmp = EmptyHolder_getInstance().w8_1;
    }
    return tmp;
  }
}
class LinkedHashSet extends HashSet {
  static e1() {
    var $this = this.f7();
    init_kotlin_collections_LinkedHashSet($this);
    return $this;
  }
  static f9(initialCapacity, loadFactor) {
    var $this = this.g7(initialCapacity, loadFactor);
    init_kotlin_collections_LinkedHashSet($this);
    return $this;
  }
  static g(initialCapacity) {
    return this.f9(initialCapacity, 1.0);
  }
  n4() {
    return this.d7_1.c7();
  }
}
class Exception extends Error {
  static g9() {
    var $this = createThis(this);
    init_kotlin_Exception($this);
    setPropertiesToThrowableInstance($this);
    return $this;
  }
  static d4(message) {
    var $this = createThis(this);
    init_kotlin_Exception($this);
    setPropertiesToThrowableInstance($this, message);
    return $this;
  }
}
class RuntimeException extends Exception {
  static k9() {
    var $this = this.g9();
    init_kotlin_RuntimeException($this);
    return $this;
  }
  static m7(message) {
    var $this = this.d4(message);
    init_kotlin_RuntimeException($this);
    return $this;
  }
}
class IllegalArgumentException extends RuntimeException {
  static j9() {
    var $this = this.k9();
    init_kotlin_IllegalArgumentException($this);
    return $this;
  }
  static p(message) {
    var $this = this.m7(message);
    init_kotlin_IllegalArgumentException($this);
    return $this;
  }
}
class IndexOutOfBoundsException extends RuntimeException {
  static l9() {
    var $this = this.k9();
    init_kotlin_IndexOutOfBoundsException($this);
    return $this;
  }
  static l1(message) {
    var $this = this.m7(message);
    init_kotlin_IndexOutOfBoundsException($this);
    return $this;
  }
}
class IllegalStateException extends RuntimeException {
  static m9() {
    var $this = this.k9();
    init_kotlin_IllegalStateException($this);
    return $this;
  }
  static p3(message) {
    var $this = this.m7(message);
    init_kotlin_IllegalStateException($this);
    return $this;
  }
}
class UnsupportedOperationException extends RuntimeException {
  static y5() {
    var $this = this.k9();
    init_kotlin_UnsupportedOperationException($this);
    return $this;
  }
}
class NoSuchElementException extends RuntimeException {
  static r4() {
    var $this = this.k9();
    init_kotlin_NoSuchElementException($this);
    return $this;
  }
  static l(message) {
    var $this = this.m7(message);
    init_kotlin_NoSuchElementException($this);
    return $this;
  }
}
class NumberFormatException extends IllegalArgumentException {
  static r9() {
    var $this = this.j9();
    init_kotlin_NumberFormatException($this);
    return $this;
  }
  static s9(message) {
    var $this = this.p(message);
    init_kotlin_NumberFormatException($this);
    return $this;
  }
}
class ConcurrentModificationException extends RuntimeException {
  static b8() {
    var $this = this.k9();
    init_kotlin_ConcurrentModificationException($this);
    return $this;
  }
}
class ArithmeticException extends RuntimeException {
  static w9() {
    var $this = this.k9();
    init_kotlin_ArithmeticException($this);
    return $this;
  }
  static x9(message) {
    var $this = this.m7(message);
    init_kotlin_ArithmeticException($this);
    return $this;
  }
}
class NullPointerException extends RuntimeException {
  static t3() {
    var $this = this.k9();
    init_kotlin_NullPointerException($this);
    return $this;
  }
}
class NoWhenBranchMatchedException extends RuntimeException {
  static x3() {
    var $this = this.k9();
    init_kotlin_NoWhenBranchMatchedException($this);
    return $this;
  }
}
class ClassCastException extends RuntimeException {
  static b4() {
    var $this = this.k9();
    init_kotlin_ClassCastException($this);
    return $this;
  }
}
class StringBuilder {
  static y9(content) {
    var $this = createThis(this);
    $this.d_1 = !(content === undefined) ? content : '';
    return $this;
  }
  static u8(capacity) {
    return this.e();
  }
  static s1(content) {
    return this.y9(toString_1(content));
  }
  static e() {
    return this.y9('');
  }
  a() {
    // Inline function 'kotlin.js.asDynamic' call
    return this.d_1.length;
  }
  b(index) {
    // Inline function 'kotlin.text.getOrElse' call
    var this_0 = this.d_1;
    // Inline function 'kotlin.contracts.contract' call
    var tmp;
    if (0 <= index ? index <= (charSequenceLength(this_0) - 1 | 0) : false) {
      tmp = charSequenceGet(this_0, index);
    } else {
      throw IndexOutOfBoundsException.l1('index: ' + index + ', length: ' + this.a() + '}');
    }
    return tmp;
  }
  c(startIndex, endIndex) {
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    return this.d_1.substring(startIndex, endIndex);
  }
  o8(value) {
    this.d_1 = this.d_1 + toString(value);
    return this;
  }
  q(value) {
    this.d_1 = this.d_1 + toString_0(value);
    return this;
  }
  t1() {
    var reversed = '';
    var index = this.d_1.length - 1 | 0;
    while (index >= 0) {
      var tmp = this.d_1;
      var tmp0 = index;
      index = tmp0 - 1 | 0;
      var low = charSequenceGet(tmp, tmp0);
      if (isLowSurrogate(low) && index >= 0) {
        var tmp_0 = this.d_1;
        var tmp1 = index;
        index = tmp1 - 1 | 0;
        var high = charSequenceGet(tmp_0, tmp1);
        if (isHighSurrogate(high)) {
          reversed = reversed + new Char(high) + toString(low);
        } else {
          reversed = reversed + new Char(low) + toString(high);
        }
      } else {
        reversed = reversed + toString(low);
      }
    }
    this.d_1 = reversed;
    return this;
  }
  m8(value) {
    this.d_1 = this.d_1 + toString_0(value);
    return this;
  }
  z9(value) {
    return this.n8(value.toString());
  }
  n8(value) {
    var tmp = this;
    var tmp_0 = this.d_1;
    tmp.d_1 = tmp_0 + (value == null ? 'null' : value);
    return this;
  }
  toString() {
    return this.d_1;
  }
  aa() {
    this.d_1 = '';
    return this;
  }
}
class Companion_4 {
  constructor() {
    Companion_instance_4 = this;
    this.ba_1 = new RegExp('[\\\\^$*+?.()|[\\]{}]', 'g');
    this.ca_1 = new RegExp('[\\\\$]', 'g');
    this.da_1 = new RegExp('\\$', 'g');
  }
  ea(literal) {
    // Inline function 'kotlin.text.nativeReplace' call
    var pattern = this.ba_1;
    // Inline function 'kotlin.js.asDynamic' call
    return literal.replace(pattern, '\\$&');
  }
  fa(literal) {
    // Inline function 'kotlin.text.nativeReplace' call
    var pattern = this.da_1;
    // Inline function 'kotlin.js.asDynamic' call
    return literal.replace(pattern, '$$$$');
  }
}
class sam$kotlin_Comparator$0 {
  constructor(function_0) {
    this.ga_1 = function_0;
  }
  ha(a, b) {
    return this.ga_1(a, b);
  }
  compare(a, b) {
    return this.ha(a, b);
  }
  j3() {
    return this.ga_1;
  }
  equals(other) {
    var tmp;
    if (!(other == null) ? isInterface(other, Comparator) : false) {
      var tmp_0;
      if (!(other == null) ? isInterface(other, FunctionAdapter) : false) {
        tmp_0 = equals(this.j3(), other.j3());
      } else {
        tmp_0 = false;
      }
      tmp = tmp_0;
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return hashCode(this.j3());
  }
}
class IteratorImpl_0 {
  constructor($outer, $box) {
    boxApply(this, $box);
    this.ja_1 = $outer;
    this.ia_1 = 0;
  }
  s() {
    return this.ia_1 < this.ja_1.y();
  }
  t() {
    if (!this.s())
      throw NoSuchElementException.r4();
    var tmp1 = this.ia_1;
    this.ia_1 = tmp1 + 1 | 0;
    return this.ja_1.d1(tmp1);
  }
}
class ListIteratorImpl_0 extends IteratorImpl_0 {
  constructor($outer, index, $box) {
    if ($box === VOID)
      $box = {};
    $box.ma_1 = $outer;
    super($outer, $box);
    Companion_instance_5.x4(index, this.ma_1.y());
    this.ia_1 = index;
  }
}
class Companion_5 {
  constructor() {
    this.w4_1 = 2147483639;
  }
  s5(index, size) {
    if (index < 0 || index >= size) {
      throw IndexOutOfBoundsException.l1('index: ' + index + ', size: ' + size);
    }
  }
  x4(index, size) {
    if (index < 0 || index > size) {
      throw IndexOutOfBoundsException.l1('index: ' + index + ', size: ' + size);
    }
  }
  n7(oldCapacity, minCapacity) {
    var newCapacity = oldCapacity + (oldCapacity >> 1) | 0;
    if ((newCapacity - minCapacity | 0) < 0)
      newCapacity = minCapacity;
    if ((newCapacity - 2147483639 | 0) > 0)
      newCapacity = minCapacity > 2147483639 ? 2147483647 : 2147483639;
    return newCapacity;
  }
  d5(c) {
    var hashCode_0 = 1;
    var tmp0_iterator = c.r();
    while (tmp0_iterator.s()) {
      var e = tmp0_iterator.t();
      var tmp = imul_0(31, hashCode_0);
      var tmp2_elvis_lhs = e == null ? null : hashCode(e);
      hashCode_0 = tmp + (tmp2_elvis_lhs == null ? 0 : tmp2_elvis_lhs) | 0;
    }
    return hashCode_0;
  }
  c5(c, other) {
    if (!(c.y() === other.y()))
      return false;
    var otherIterator = other.r();
    var tmp0_iterator = c.r();
    while (tmp0_iterator.s()) {
      var elem = tmp0_iterator.t();
      var elemOther = otherIterator.t();
      if (!equals(elem, elemOther)) {
        return false;
      }
    }
    return true;
  }
}
class AbstractList extends AbstractCollection {
  static na() {
    return this.m4();
  }
  r() {
    return new IteratorImpl_0(this);
  }
  f1(index) {
    return new ListIteratorImpl_0(this, index);
  }
  equals(other) {
    if (other === this)
      return true;
    if (!(!(other == null) ? isInterface(other, KtList) : false))
      return false;
    return Companion_instance_5.c5(this, other);
  }
  hashCode() {
    return Companion_instance_5.d5(this);
  }
}
class Companion_6 {}
class Companion_7 {
  q5(c) {
    var hashCode_0 = 0;
    var tmp0_iterator = c.r();
    while (tmp0_iterator.s()) {
      var element = tmp0_iterator.t();
      var tmp = hashCode_0;
      var tmp2_elvis_lhs = element == null ? null : hashCode(element);
      hashCode_0 = tmp + (tmp2_elvis_lhs == null ? 0 : tmp2_elvis_lhs) | 0;
    }
    return hashCode_0;
  }
  p5(c, other) {
    if (!(c.y() === other.y()))
      return false;
    // Inline function 'kotlin.collections.containsAll' call
    return c.i2(other);
  }
}
class EmptyList {
  constructor() {
    EmptyList_instance = this;
    this.pa_1 = new Long(-1478467534, -1720727600);
  }
  equals(other) {
    var tmp;
    if (!(other == null) ? isInterface(other, KtList) : false) {
      tmp = other.b1();
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return 1;
  }
  toString() {
    return '[]';
  }
  y() {
    return 0;
  }
  b1() {
    return true;
  }
  qa(elements) {
    return elements.b1();
  }
  i2(elements) {
    return this.qa(elements);
  }
  d1(index) {
    throw IndexOutOfBoundsException.l1("Empty list doesn't contain element at index " + index + '.');
  }
  r() {
    return EmptyIterator_instance;
  }
  f1(index) {
    if (!(index === 0))
      throw IndexOutOfBoundsException.l1('Index: ' + index);
    return EmptyIterator_instance;
  }
}
class EmptyIterator {
  s() {
    return false;
  }
  t() {
    throw NoSuchElementException.r4();
  }
}
class ArrayAsCollection {
  constructor(values, isVarargs) {
    this.ra_1 = values;
    this.sa_1 = isVarargs;
  }
  y() {
    return this.ra_1.length;
  }
  b1() {
    // Inline function 'kotlin.collections.isEmpty' call
    return this.ra_1.length === 0;
  }
  ta(element) {
    return contains_0(this.ra_1, element);
  }
  ua(elements) {
    var tmp$ret$0;
    $l$block_0: {
      // Inline function 'kotlin.collections.all' call
      var tmp;
      if (isInterface(elements, Collection)) {
        tmp = elements.b1();
      } else {
        tmp = false;
      }
      if (tmp) {
        tmp$ret$0 = true;
        break $l$block_0;
      }
      var tmp0_iterator = elements.r();
      while (tmp0_iterator.s()) {
        var element = tmp0_iterator.t();
        // Inline function 'kotlin.collections.ArrayAsCollection.containsAll.<anonymous>' call
        if (!this.ta(element)) {
          tmp$ret$0 = false;
          break $l$block_0;
        }
      }
      tmp$ret$0 = true;
    }
    return tmp$ret$0;
  }
  i2(elements) {
    return this.ua(elements);
  }
  r() {
    return arrayIterator(this.ra_1);
  }
}
class IntIterator {
  t() {
    return this.va();
  }
}
class LongIterator {
  t() {
    return this.wa();
  }
}
class EmptySet {
  constructor() {
    EmptySet_instance = this;
    this.xa_1 = new Long(1993859828, 793161749);
  }
  equals(other) {
    var tmp;
    if (!(other == null) ? isInterface(other, KtSet) : false) {
      tmp = other.b1();
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return 0;
  }
  toString() {
    return '[]';
  }
  y() {
    return 0;
  }
  b1() {
    return true;
  }
  ya(element) {
    return false;
  }
  h2(element) {
    if (!false)
      return false;
    var tmp;
    if (false) {
      tmp = element;
    } else {
      tmp = THROW_CCE();
    }
    return this.ya(tmp);
  }
  qa(elements) {
    return elements.b1();
  }
  i2(elements) {
    return this.qa(elements);
  }
  r() {
    return EmptyIterator_instance;
  }
}
class EnumEntriesList extends AbstractList {
  static ab(entries) {
    var $this = this.na();
    $this.za_1 = entries;
    return $this;
  }
  y() {
    return this.za_1.length;
  }
  d1(index) {
    Companion_instance_5.s5(index, this.za_1.length);
    return this.za_1[index];
  }
  bb(element) {
    if (element === null)
      return false;
    var target = getOrNull(this.za_1, element.p2_1);
    return target === element;
  }
  h2(element) {
    if (!(element instanceof Enum))
      return false;
    return this.bb(element instanceof Enum ? element : THROW_CCE());
  }
}
class Random {
  static eb() {
    Default_getInstance();
    return createThis(this);
  }
  va() {
    return this.fb(32);
  }
  c1(until) {
    return this.gb(0, until);
  }
  gb(from, until) {
    checkRangeBounds(from, until);
    var n = until - from | 0;
    if (n > 0 || n === -2147483648) {
      var tmp;
      if ((n & (-n | 0)) === n) {
        var bitCount = fastLog2(n);
        tmp = this.fb(bitCount);
      } else {
        var v;
        do {
          var bits = this.va() >>> 1 | 0;
          v = bits % n | 0;
        }
         while (((bits - v | 0) + (n - 1 | 0) | 0) < 0);
        tmp = v;
      }
      var rnd = tmp;
      return from + rnd | 0;
    } else {
      while (true) {
        var rnd_0 = this.va();
        if (from <= rnd_0 ? rnd_0 < until : false)
          return rnd_0;
      }
    }
  }
  wa() {
    // Inline function 'kotlin.Long.plus' call
    var this_0 = toLong(this.va()).d3(32);
    var other = this.va();
    return this_0.v2(toLong(other));
  }
  hb(from, until) {
    checkRangeBounds_0(from, until);
    var n = until.w2(from);
    if (n.p1(new Long(0, 0)) > 0) {
      var rnd;
      if (n.f3(n.a3()).equals(n)) {
        var nLow = n.h3();
        var nHigh = n.e3(32).h3();
        var tmp;
        if (!(nLow === 0)) {
          var bitCount = fastLog2(nLow);
          tmp = toLong(this.fb(bitCount)).f3(new Long(-1, 0));
        } else if (nHigh === 1) {
          tmp = toLong(this.va()).f3(new Long(-1, 0));
        } else {
          var bitCount_0 = fastLog2(nHigh);
          tmp = toLong(this.fb(bitCount_0)).d3(32).v2(toLong(this.va()).f3(new Long(-1, 0)));
        }
        rnd = tmp;
      } else {
        var v;
        $l$1: do {
          $l$0: do {
            var bits = this.wa().e3(1);
            v = bits.z2(n);
          }
           while (false);
          var tmp_0 = bits.w2(v);
          // Inline function 'kotlin.Long.minus' call
          var tmp$ret$0 = n.w2(toLong(1));
        }
         while (tmp_0.v2(tmp$ret$0).p1(new Long(0, 0)) < 0);
        rnd = v;
      }
      return from.v2(rnd);
    } else {
      while (true) {
        var rnd_0 = this.wa();
        if (from.p1(rnd_0) <= 0 ? rnd_0.p1(until) < 0 : false)
          return rnd_0;
      }
    }
  }
  ib() {
    return !(this.fb(1) === 0);
  }
  jb() {
    return this.fb(24) / 16777216;
  }
}
class Default extends Random {
  static db() {
    Default_instance = null;
    var $this = this.eb();
    Default_instance = $this;
    $this.cb_1 = defaultPlatformRandom();
    return $this;
  }
  fb(bitCount) {
    return this.cb_1.fb(bitCount);
  }
  va() {
    return this.cb_1.va();
  }
  c1(until) {
    return this.cb_1.c1(until);
  }
  gb(from, until) {
    return this.cb_1.gb(from, until);
  }
  wa() {
    return this.cb_1.wa();
  }
  hb(from, until) {
    return this.cb_1.hb(from, until);
  }
  ib() {
    return this.cb_1.ib();
  }
  jb() {
    return this.cb_1.jb();
  }
}
class Companion_8 {
  constructor() {
    Companion_instance_8 = this;
    this.rb_1 = new Long(0, 0);
  }
}
class XorWowRandom extends Random {
  static sb(x, y, z, w, v, addend) {
    Companion_getInstance_8();
    var $this = this.eb();
    $this.kb_1 = x;
    $this.lb_1 = y;
    $this.mb_1 = z;
    $this.nb_1 = w;
    $this.ob_1 = v;
    $this.pb_1 = addend;
    // Inline function 'kotlin.require' call
    // Inline function 'kotlin.contracts.contract' call
    if (!!(($this.kb_1 | $this.lb_1 | $this.mb_1 | $this.nb_1 | $this.ob_1) === 0)) {
      // Inline function 'kotlin.random.XorWowRandom.<anonymous>' call
      var message = 'Initial state must have at least one non-zero element.';
      throw IllegalArgumentException.p(toString_1(message));
    }
    // Inline function 'kotlin.repeat' call
    // Inline function 'kotlin.contracts.contract' call
    var inductionVariable = 0;
    if (inductionVariable < 64)
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        // Inline function 'kotlin.random.XorWowRandom.<anonymous>' call
        $this.va();
      }
       while (inductionVariable < 64);
    return $this;
  }
  static qb(seed1, seed2) {
    Companion_getInstance_8();
    return this.sb(seed1, seed2, 0, 0, ~seed1, seed1 << 10 ^ (seed2 >>> 4 | 0));
  }
  va() {
    var t = this.kb_1;
    t = t ^ (t >>> 2 | 0);
    this.kb_1 = this.lb_1;
    this.lb_1 = this.mb_1;
    this.mb_1 = this.nb_1;
    var v0 = this.ob_1;
    this.nb_1 = v0;
    t = t ^ t << 1 ^ v0 ^ v0 << 4;
    this.ob_1 = t;
    this.pb_1 = this.pb_1 + 362437 | 0;
    return t + this.pb_1 | 0;
  }
  fb(bitCount) {
    return takeUpperBits(this.va(), bitCount);
  }
}
class Companion_9 {
  constructor() {
    Companion_instance_9 = this;
    this.m1_1 = new IntRange(1, 0);
  }
}
class IntProgression {
  constructor(start, endInclusive, step) {
    if (step === 0)
      throw IllegalArgumentException.p('Step must be non-zero.');
    if (step === -2147483648)
      throw IllegalArgumentException.p('Step must be greater than Int.MIN_VALUE to avoid overflow on negation.');
    this.xb_1 = start;
    this.yb_1 = getProgressionLastElement(start, endInclusive, step);
    this.zb_1 = step;
  }
  r() {
    return new IntProgressionIterator(this.xb_1, this.yb_1, this.zb_1);
  }
  b1() {
    return this.zb_1 > 0 ? this.xb_1 > this.yb_1 : this.xb_1 < this.yb_1;
  }
  equals(other) {
    var tmp;
    if (other instanceof IntProgression) {
      tmp = this.b1() && other.b1() || (this.xb_1 === other.xb_1 && this.yb_1 === other.yb_1 && this.zb_1 === other.zb_1);
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return this.b1() ? -1 : imul_0(31, imul_0(31, this.xb_1) + this.yb_1 | 0) + this.zb_1 | 0;
  }
  toString() {
    return this.zb_1 > 0 ? '' + this.xb_1 + '..' + this.yb_1 + ' step ' + this.zb_1 : '' + this.xb_1 + ' downTo ' + this.yb_1 + ' step ' + (-this.zb_1 | 0);
  }
}
class IntRange extends IntProgression {
  constructor(start, endInclusive) {
    Companion_getInstance_9();
    super(start, endInclusive, 1);
  }
  wb() {
    return this.xb_1;
  }
  ac() {
    return this.yb_1;
  }
  b1() {
    return this.xb_1 > this.yb_1;
  }
  equals(other) {
    var tmp;
    if (other instanceof IntRange) {
      tmp = this.b1() && other.b1() || (this.xb_1 === other.xb_1 && this.yb_1 === other.yb_1);
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return this.b1() ? -1 : imul_0(31, this.xb_1) + this.yb_1 | 0;
  }
  toString() {
    return '' + this.xb_1 + '..' + this.yb_1;
  }
}
class Companion_10 {
  constructor() {
    Companion_instance_10 = this;
    this.bc_1 = new LongRange(new Long(1, 0), new Long(0, 0));
  }
}
class LongProgression {
  constructor(start, endInclusive, step) {
    if (step.equals(new Long(0, 0)))
      throw IllegalArgumentException.p('Step must be non-zero.');
    if (step.equals(new Long(0, -2147483648)))
      throw IllegalArgumentException.p('Step must be greater than Long.MIN_VALUE to avoid overflow on negation.');
    this.fc_1 = start;
    this.gc_1 = getProgressionLastElement_0(start, endInclusive, step);
    this.hc_1 = step;
  }
  r() {
    return new LongProgressionIterator(this.fc_1, this.gc_1, this.hc_1);
  }
  b1() {
    return this.hc_1.p1(new Long(0, 0)) > 0 ? this.fc_1.p1(this.gc_1) > 0 : this.fc_1.p1(this.gc_1) < 0;
  }
  equals(other) {
    var tmp;
    if (other instanceof LongProgression) {
      tmp = this.b1() && other.b1() || (this.fc_1.equals(other.fc_1) && this.gc_1.equals(other.gc_1) && this.hc_1.equals(other.hc_1));
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return this.b1() ? -1 : numberToLong(31).x2(numberToLong(31).x2(this.fc_1.g3(this.fc_1.e3(32))).v2(this.gc_1.g3(this.gc_1.e3(32)))).v2(this.hc_1.g3(this.hc_1.e3(32))).h3();
  }
  toString() {
    return this.hc_1.p1(new Long(0, 0)) > 0 ? this.fc_1.toString() + '..' + this.gc_1.toString() + ' step ' + this.hc_1.toString() : this.fc_1.toString() + ' downTo ' + this.gc_1.toString() + ' step ' + this.hc_1.a3().toString();
  }
}
class LongRange extends LongProgression {
  constructor(start, endInclusive) {
    Companion_getInstance_10();
    super(start, endInclusive, new Long(1, 0));
  }
  b1() {
    return this.fc_1.p1(this.gc_1) > 0;
  }
  equals(other) {
    var tmp;
    if (other instanceof LongRange) {
      tmp = this.b1() && other.b1() || (this.fc_1.equals(other.fc_1) && this.gc_1.equals(other.gc_1));
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return this.b1() ? -1 : numberToLong(31).x2(this.fc_1.g3(this.fc_1.e3(32))).v2(this.gc_1.g3(this.gc_1.e3(32))).h3();
  }
  toString() {
    return this.fc_1.toString() + '..' + this.gc_1.toString();
  }
}
class IntProgressionIterator extends IntIterator {
  constructor(first, last, step) {
    super();
    this.ic_1 = step;
    this.jc_1 = last;
    this.kc_1 = this.ic_1 > 0 ? first <= last : first >= last;
    this.lc_1 = this.kc_1 ? first : this.jc_1;
  }
  s() {
    return this.kc_1;
  }
  va() {
    var value = this.lc_1;
    if (value === this.jc_1) {
      if (!this.kc_1)
        throw NoSuchElementException.r4();
      this.kc_1 = false;
    } else {
      this.lc_1 = this.lc_1 + this.ic_1 | 0;
    }
    return value;
  }
}
class LongProgressionIterator extends LongIterator {
  constructor(first, last, step) {
    super();
    this.mc_1 = step;
    this.nc_1 = last;
    this.oc_1 = this.mc_1.p1(new Long(0, 0)) > 0 ? first.p1(last) <= 0 : first.p1(last) >= 0;
    this.pc_1 = this.oc_1 ? first : this.nc_1;
  }
  s() {
    return this.oc_1;
  }
  wa() {
    var value = this.pc_1;
    if (value.equals(this.nc_1)) {
      if (!this.oc_1)
        throw NoSuchElementException.r4();
      this.oc_1 = false;
    } else {
      this.pc_1 = this.pc_1.v2(this.mc_1);
    }
    return value;
  }
}
class Companion_11 {
  q1(rangeStart, rangeEnd, step) {
    return new IntProgression(rangeStart, rangeEnd, step);
  }
}
class Companion_12 {}
class DelimitedRangesSequence$iterator$1 {
  constructor(this$0) {
    this.vc_1 = this$0;
    this.qc_1 = -1;
    this.rc_1 = coerceIn(this$0.xc_1, 0, charSequenceLength(this$0.wc_1));
    this.sc_1 = this.rc_1;
    this.tc_1 = null;
    this.uc_1 = 0;
  }
  t() {
    if (this.qc_1 === -1) {
      calcNext(this);
    }
    if (this.qc_1 === 0)
      throw NoSuchElementException.r4();
    var tmp = this.tc_1;
    var result = tmp instanceof IntRange ? tmp : THROW_CCE();
    this.tc_1 = null;
    this.qc_1 = -1;
    return result;
  }
  s() {
    if (this.qc_1 === -1) {
      calcNext(this);
    }
    return this.qc_1 === 1;
  }
}
class DelimitedRangesSequence {
  constructor(input, startIndex, limit, getNextMatch) {
    this.wc_1 = input;
    this.xc_1 = startIndex;
    this.yc_1 = limit;
    this.zc_1 = getNextMatch;
  }
  r() {
    return new DelimitedRangesSequence$iterator$1(this);
  }
}
class Companion_13 {}
class Failure {
  constructor(exception) {
    this.ed_1 = exception;
  }
  equals(other) {
    var tmp;
    if (other instanceof Failure) {
      tmp = equals(this.ed_1, other.ed_1);
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return hashCode(this.ed_1);
  }
  toString() {
    return 'Failure(' + this.ed_1.toString() + ')';
  }
}
class Pair {
  constructor(first, second) {
    this.ad_1 = first;
    this.bd_1 = second;
  }
  toString() {
    return '(' + toString_0(this.ad_1) + ', ' + toString_0(this.bd_1) + ')';
  }
  cd() {
    return this.ad_1;
  }
  dd() {
    return this.bd_1;
  }
  hashCode() {
    var result = this.ad_1 == null ? 0 : hashCode(this.ad_1);
    result = imul_0(result, 31) + (this.bd_1 == null ? 0 : hashCode(this.bd_1)) | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof Pair))
      return false;
    var tmp0_other_with_cast = other instanceof Pair ? other : THROW_CCE();
    if (!equals(this.ad_1, tmp0_other_with_cast.ad_1))
      return false;
    if (!equals(this.bd_1, tmp0_other_with_cast.bd_1))
      return false;
    return true;
  }
}
class Triple {
  constructor(first, second, third) {
    this.fd_1 = first;
    this.gd_1 = second;
    this.hd_1 = third;
  }
  toString() {
    return '(' + toString_0(this.fd_1) + ', ' + toString_0(this.gd_1) + ', ' + toString_0(this.hd_1) + ')';
  }
  cd() {
    return this.fd_1;
  }
  dd() {
    return this.gd_1;
  }
  id() {
    return this.hd_1;
  }
  hashCode() {
    var result = this.fd_1 == null ? 0 : hashCode(this.fd_1);
    result = imul_0(result, 31) + (this.gd_1 == null ? 0 : hashCode(this.gd_1)) | 0;
    result = imul_0(result, 31) + (this.hd_1 == null ? 0 : hashCode(this.hd_1)) | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof Triple))
      return false;
    var tmp0_other_with_cast = other instanceof Triple ? other : THROW_CCE();
    if (!equals(this.fd_1, tmp0_other_with_cast.fd_1))
      return false;
    if (!equals(this.gd_1, tmp0_other_with_cast.gd_1))
      return false;
    if (!equals(this.hd_1, tmp0_other_with_cast.hd_1))
      return false;
    return true;
  }
}
class LessonRule {
  constructor(title, body, examples, table, formula, steps, stepsTitle) {
    examples = examples === VOID ? emptyList() : examples;
    table = table === VOID ? emptyList() : table;
    formula = formula === VOID ? null : formula;
    steps = steps === VOID ? emptyList() : steps;
    stepsTitle = stepsTitle === VOID ? null : stepsTitle;
    this.jd_1 = title;
    this.kd_1 = body;
    this.ld_1 = examples;
    this.md_1 = table;
    this.nd_1 = formula;
    this.od_1 = steps;
    this.pd_1 = stepsTitle;
  }
  toString() {
    return 'LessonRule(title=' + this.jd_1 + ', body=' + this.kd_1 + ', examples=' + toString_1(this.ld_1) + ', table=' + toString_1(this.md_1) + ', formula=' + this.nd_1 + ', steps=' + toString_1(this.od_1) + ', stepsTitle=' + this.pd_1 + ')';
  }
  hashCode() {
    var result = getStringHashCode(this.jd_1);
    result = imul_0(result, 31) + getStringHashCode(this.kd_1) | 0;
    result = imul_0(result, 31) + hashCode(this.ld_1) | 0;
    result = imul_0(result, 31) + hashCode(this.md_1) | 0;
    result = imul_0(result, 31) + (this.nd_1 == null ? 0 : getStringHashCode(this.nd_1)) | 0;
    result = imul_0(result, 31) + hashCode(this.od_1) | 0;
    result = imul_0(result, 31) + (this.pd_1 == null ? 0 : getStringHashCode(this.pd_1)) | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof LessonRule))
      return false;
    var tmp0_other_with_cast = other instanceof LessonRule ? other : THROW_CCE();
    if (!(this.jd_1 === tmp0_other_with_cast.jd_1))
      return false;
    if (!(this.kd_1 === tmp0_other_with_cast.kd_1))
      return false;
    if (!equals(this.ld_1, tmp0_other_with_cast.ld_1))
      return false;
    if (!equals(this.md_1, tmp0_other_with_cast.md_1))
      return false;
    if (!(this.nd_1 == tmp0_other_with_cast.nd_1))
      return false;
    if (!equals(this.od_1, tmp0_other_with_cast.od_1))
      return false;
    if (!(this.pd_1 == tmp0_other_with_cast.pd_1))
      return false;
    return true;
  }
}
class NumberLessons {
  constructor() {
    NumberLessons_instance = this;
    this.pe_1 = listOf_0(['\uD83C\uDF40 Angka 8 (\u516B b\u0101) dianggap paling beruntung karena bunyinya mirip \u53D1 (f\u0101, "makmur"). Pembukaan Olimpiade Beijing dimulai 08-08-2008 pukul 8:08.', '\uD83D\uDEAB Angka 4 (\u56DB s\xEC) sering dihindari karena bunyinya mirip \u6B7B (s\u01D0, "mati"). Banyak gedung tidak punya lantai 4.', '\uD83D\uDC95 520 (w\u01D4 \xE8r l\xEDng) terdengar seperti \u6211\u7231\u4F60 ("aku cinta kamu"). Tanggal 20 Mei jadi "hari kasih sayang" versi internet Tiongkok.', '\u270B Orang Tiongkok bisa menunjukkan angka 6\u201310 dengan satu tangan: 6 = jempol dan kelingking, 8 = jempol dan telunjuk, 10 = jari menyilang.', '\uD83D\uDE04 666 (li\xF9 li\xF9 li\xF9) di internet Tiongkok artinya "keren banget", karena \u6E9C (li\xF9) berarti lancar atau jago.', '\uD83E\uDDE7 Angpao biasanya diisi jumlah genap seperti 200 atau 888. Jumlah dengan angka 4 dihindari.', '\uD83D\uDCF1 Saat menyebut nomor telepon, orang Tiongkok membaca 1 sebagai \u5E7A (y\u0101o) agar tidak tertukar dengan \u4E03 (q\u012B).', '\uD83C\uDF82 Umur 9 (\u4E5D ji\u01D4) bunyinya mirip \u4E45 (ji\u01D4, "lama/abadi"), jadi 9 juga dianggap angka baik, terutama di pernikahan.', '\uD83D\uDED2 Tanggal 11 November (\u53CC\u5341\u4E00, "Double 11") adalah festival belanja daring terbesar di dunia, penuh promo \u6EE1\u51CF dan \u6253\u6298.']);
  }
  qe(cat) {
    var tmp;
    switch (cat.p2_1) {
      case 0:
      case 9:
        tmp = listOf_0([new LessonRule('Angka Dasar', '0\u201310 adalah dasarnya. Puluhan = angka + \u5341 + angka.', listOf_0([ex(this, ChineseNumber_getInstance().xd(10), '10'), ex(this, ChineseNumber_getInstance().xd(25), '25'), ex(this, ChineseNumber_getInstance().xd(99), '99')])), new LessonRule('Hitungan Per 4 Digit', 'Bahasa Indonesia mengelompokkan per 3 digit (ribu, juta). Mandarin per 4 digit: \u4E07 = 10.000 dan \u4EBF = 100.000.000.', listOf_0([ex(this, ChineseNumber_getInstance().xd(10000), '10.000'), ex(this, ChineseNumber_getInstance().xd(100000), '100.000 (sepuluh \u4E07)'), ex(this, ChineseNumber_getInstance().xd(1000000), '1.000.000 (seratus \u4E07)'), ex(this, ChineseNumber_getInstance().xd(100000000), '100.000.000')])), new LessonRule('\u96F6 untuk Digit Kosong', 'Kalau ada digit 0 di tengah, tulis \u96F6 sekali saja, walau nolnya lebih dari satu. Nol di akhir tidak dibaca.', listOf_0([ex(this, ChineseNumber_getInstance().xd(105), '105'), ex(this, ChineseNumber_getInstance().xd(1005), '1.005'), ex(this, ChineseNumber_getInstance().xd(1050), '1.050'), ex(this, ChineseNumber_getInstance().xd(30508), '30.508')])), new LessonRule('\u4E24 atau \u4E8C', 'Angka 2 sebelum \u5343, \u4E07, \u4EBF (dan \u767E di awal) dibaca \u4E24. Di puluhan dan satuan tetap \u4E8C.', listOf_0([ex(this, ChineseNumber_getInstance().xd(2000), '2.000'), ex(this, ChineseNumber_getInstance().xd(20000), '20.000'), ex(this, ChineseNumber_getInstance().xd(22), '22'), ex(this, ChineseNumber_getInstance().xd(12), '12')])), new LessonRule('\u5341 atau \u4E00\u5341', 'Di awal angka cukup \u5341 (\u5341\u4E94 = 15). Di tengah angka harus lengkap \u4E00\u5341.', listOf_0([ex(this, ChineseNumber_getInstance().xd(15), '15'), ex(this, ChineseNumber_getInstance().xd(115), '115'), ex(this, ChineseNumber_getInstance().xd(110000), '110.000')])), new LessonRule('Nada \u4E00 Berubah', '\u4E00 dibaca y\xEC sebelum \u767E/\u5343 dan y\xED sebelum \u4E07/\u4EBF. Di akhir angka tetap y\u012B.', listOf_0([ex(this, ChineseNumber_getInstance().xd(100), '100'), ex(this, ChineseNumber_getInstance().xd(10000), '10.000'), ex(this, ChineseNumber_getInstance().xd(101), '101')]))]);
        break;
      case 1:
        tmp = listOf_0([new LessonRule('Lisan: \u5757, \u6BDB, \u5206', 'Saat bicara: \u5757 = yuan, \u6BDB = 0,1 yuan, \u5206 = 0,01 yuan. Satuan terakhir boleh dihilangkan.', listOf_0([ex(this, ChineseNumber_getInstance().ie(12, 5, 0), '\xA512,50'), ex(this, ChineseNumber_getInstance().ie(12, 3, 5), '\xA512,35'), ex(this, ChineseNumber_getInstance().ie(0, 5, 0), '\xA50,50')])), new LessonRule('Tertulis: \u5143, \u89D2, \u5206', 'Di label harga dan kuitansi dipakai \u5143 dan \u89D2.', listOf_0([ex(this, ChineseNumber_getInstance().le(12, 5, 0), '\xA512,50'), ex(this, ChineseNumber_getInstance().le(3, 0, 5), '\xA53,05')])), new LessonRule('Hati-hati: \u5757\u4E94 dan \u5757\u96F6\u4E94', '\u4E00\u5757\u4E94 = \xA51,50, sedangkan \u4E00\u5757\u96F6\u4E94\u5206 = \xA51,05. \u96F6 menandai \u6BDB yang kosong.', listOf_0([ex(this, ChineseNumber_getInstance().ie(1, 5, 0), '\xA51,50'), ex(this, ChineseNumber_getInstance().ie(1, 0, 5), '\xA51,05')])), new LessonRule('\u4E24\u5757, Bukan \u4E8C\u5757', 'Sebelum kata satuan, angka 2 menjadi \u4E24.', listOf_0([ex(this, ChineseNumber_getInstance().ie(2, 0, 0), '\xA52'), ex(this, ChineseNumber_getInstance().ie(0, 2, 0), '\xA50,20'), ex(this, ChineseNumber_getInstance().ie(22, 0, 0), '\xA522')]))]);
        break;
      case 2:
        tmp = listOf_0([new LessonRule('\u6253\u6298 = Bayar Sekian Persen', 'Angka di depan \u6298 adalah yang DIBAYAR. \u6253\u516B\u6298 = bayar 80% (diskon 20%), bukan diskon 80%!', listOf_0([ex(this, Companion_getInstance_14().rd([to('\u6253', 'd\u01CE'), to('\u516B', 'b\u0101'), to('\u6298', 'zh\xE9')]), 'Bayar 80%'), ex(this, Companion_getInstance_14().rd([to('\u6253', 'd\u01CE'), to('\u4E94', 'w\u01D4'), to('\u6298', 'zh\xE9')]), 'Bayar 50%')])), new LessonRule('Dua Digit: X\u4E94\u6298', '\u516B\u4E94\u6298 = bayar 85%, \u4E5D\u4E94\u6298 = bayar 95% (diskon 5%).', listOf_0([ex(this, Companion_getInstance_14().rd([to('\u6253', 'd\u01CE'), to('\u516B', 'b\u0101'), to('\u4E94', 'w\u01D4'), to('\u6298', 'zh\xE9')]), 'Bayar 85%'), ex(this, Companion_getInstance_14().rd([to('\u6253', 'd\u01CE'), to('\u4E5D', 'ji\u01D4'), to('\u4E94', 'w\u01D4'), to('\u6298', 'zh\xE9')]), 'Bayar 95%')])), new LessonRule('\u534A\u4EF7 dan \u539F\u4EF7', '\u534A\u4EF7 = setengah harga. \u539F\u4EF7 = harga asli sebelum diskon.', listOf_0([ex(this, Companion_getInstance_14().rd([to('\u534A', 'b\xE0n'), to('\u4EF7', 'ji\xE0')]), 'Setengah harga'), ex(this, Companion_getInstance_14().rd([to('\u539F', 'yu\xE1n'), to('\u4EF7', 'ji\xE0')]), 'Harga asli')])), new LessonRule('\u6EE1\u51CF', '\u6EE1200\u51CF50 = setiap belanja minimal \xA5200, potong \xA550. Kalau belanjanya kurang dari \xA5200, tidak ada potongan.', listOf(ex(this, Companion_getInstance_14().rd([to('\u6EE1', 'm\u01CEn')]).ae(ChineseNumber_getInstance().xd(200)).ae(Companion_getInstance_14().rd([to('\u51CF', 'ji\u01CEn')])).ae(ChineseNumber_getInstance().xd(50)), 'Belanja \u2265 \xA5200, potong \xA550')))]);
        break;
      case 3:
        tmp = listOf_0([new LessonRule('Persen: \u767E\u5206\u4E4B\u2026', '"Per seratus" diucapkan dulu, baru angkanya.', listOf_0([ex(this, ChineseNumber_getInstance().ce(new Long(30, 0)), '30%'), ex(this, ChineseNumber_getInstance().ce(new Long(100, 0)), '100%'), ex(this, ChineseNumber_getInstance().re(new Long(0, 0), '5'), '0,5%')])), new LessonRule('Pecahan: Penyebut Dulu', '\u4E09\u5206\u4E4B\u4E00 = 1/3: "dari 3 bagian, ambil 1". Urutannya kebalikan bahasa Indonesia.', listOf_0([ex(this, ChineseNumber_getInstance().be(1, 3), '1/3'), ex(this, ChineseNumber_getInstance().be(3, 4), '3/4'), ex(this, ChineseNumber_getInstance().be(1, 2), '1/2')])), new LessonRule('Desimal: \u70B9', 'Koma dibaca \u70B9, angka di belakangnya dibaca satu per satu.', listOf_0([ex(this, ChineseNumber_getInstance().de(new Long(3, 0), '14'), '3,14'), ex(this, ChineseNumber_getInstance().de(new Long(0, 0), '5'), '0,5'), ex(this, ChineseNumber_getInstance().de(new Long(12, 0), '05'), '12,05')]))]);
        break;
      case 4:
        tmp = listOf_0([new LessonRule('\u70B9 = Jam', 'Jam bulat cukup angka + \u70B9. Jam 2 dibaca \u4E24\u70B9.', listOf_0([ex(this, ChineseNumber_getInstance().me(8, 0), '8:00'), ex(this, ChineseNumber_getInstance().me(2, 0), '2:00'), ex(this, ChineseNumber_getInstance().me(12, 0), '12:00')])), new LessonRule('\u534A dan \u523B', '\u534A = :30. \u523B = 15 menit: \u4E00\u523B = :15, \u4E09\u523B = :45.', listOf_0([ex(this, ChineseNumber_getInstance().oe(8, 30, 1), '8:30'), ex(this, ChineseNumber_getInstance().oe(9, 15, 1), '9:15'), ex(this, ChineseNumber_getInstance().oe(9, 45, 1), '9:45')])), new LessonRule('\u5206 dan \u96F6', 'Menit biasa = angka + \u5206. Menit 1\u20139 diberi \u96F6.', listOf_0([ex(this, ChineseNumber_getInstance().me(10, 20), '10:20'), ex(this, ChineseNumber_getInstance().me(8, 5), '8:05')])), new LessonRule('\u5DEE = Kurang', '\u5DEE\u4E94\u5206\u4E5D\u70B9 = kurang 5 menit jam 9 = 8:55.', listOf_0([ex(this, ChineseNumber_getInstance().oe(8, 55, 2), '8:55'), ex(this, ChineseNumber_getInstance().oe(9, 45, 2), '9:45')])), new LessonRule('Keterangan Waktu', '\u51CC\u6668 dini hari, \u4E0A\u5348 pagi, \u4E2D\u5348 siang, \u4E0B\u5348 sore, \u665A\u4E0A malam.', listOf_0([ex(this, Companion_getInstance_14().rd([to('\u4E0B', 'xi\xE0'), to('\u5348', 'w\u01D4')]).ae(ChineseNumber_getInstance().me(3, 0)), '15:00'), ex(this, Companion_getInstance_14().rd([to('\u665A', 'w\u01CEn'), to('\u4E0A', 'shang')]).ae(ChineseNumber_getInstance().me(8, 0)), '20:00')]))]);
        break;
      case 5:
        tmp = listOf_0([new LessonRule('Urutan: Tahun \u2192 Bulan \u2192 Tanggal', 'Kebalikan bahasa Indonesia. Tahun dibaca per digit.', listOf_0([ex(this, ChineseNumber_getInstance().ne(2026, 11, 4), '4 November 2026'), ex(this, ChineseNumber_getInstance().se(1998, 2, 14, false), '14 Februari 1998')])), new LessonRule('\u53F7 dan \u65E5', '\u53F7 dipakai saat bicara, \u65E5 dalam tulisan resmi. Artinya sama.', listOf(ex(this, ChineseNumber_getInstance().xd(4).ae(Companion_getInstance_14().rd([to('\u53F7', 'h\xE0o')])), 'Tanggal 4'))), new LessonRule('Hari: \u661F\u671F', '\u661F\u671F + angka 1\u20136. Minggu = \u661F\u671F\u5929 atau \u661F\u671F\u65E5. Bisa juga \u5468\u4E00 atau \u793C\u62DC\u4E00.', listOf_0([ex(this, ChineseNumber_getInstance().ee(1), 'Senin'), ex(this, ChineseNumber_getInstance().ee(6), 'Sabtu'), ex(this, ChineseNumber_getInstance().ee(7), 'Minggu')])), new LessonRule('Hari Relatif', '\u524D\u5929 2 hari lalu, \u6628\u5929 kemarin, \u4ECA\u5929 hari ini, \u660E\u5929 besok, \u540E\u5929 lusa, \u5927\u540E\u5929 3 hari lagi.', listOf(ex(this, Companion_getInstance_14().rd([to('\u540E', 'h\xF2u'), to('\u5929', 'ti\u0101n')]), 'Lusa')))]);
        break;
      case 6:
        tmp = listOf_0([new LessonRule('Dibaca Per Digit', 'Nomor telepon, kamar, bus, dan tahun dibaca satu per satu.', listOf_0([ex(this, ChineseNumber_getInstance().te('302'), 'Kamar 302'), ex(this, ChineseNumber_getInstance().te('2026'), 'Tahun 2026')])), new LessonRule('1 = \u5E7A (y\u0101o)', 'Di nomor telepon, 1 dibaca y\u0101o supaya tidak tertukar dengan \u4E03 (q\u012B).', listOf_0([ex(this, ChineseNumber_getInstance().fe('110', true, '\u96F6'), '110 (polisi)'), ex(this, ChineseNumber_getInstance().fe('120', true, '\u96F6'), '120 (ambulans)'), ex(this, ChineseNumber_getInstance().fe('119', true, '\u96F6'), '119 (pemadam)')]))]);
        break;
      case 7:
        tmp = listOf_0([new LessonRule('Umur: \u5C81', 'Umur = angka + \u5C81. \u5E74 artinya tahun kalender, bukan umur.', listOf_0([ex(this, ChineseNumber_getInstance().ue(25, '\u5C81', 'su\xEC', 4), '25 tahun'), ex(this, ChineseNumber_getInstance().ue(2, '\u5C81', 'su\xEC', 4), '2 tahun')])), new LessonRule('Kata Satuan: \u4E2A', 'Benda dan orang dihitung dengan kata satuan. 2 menjadi \u4E24.', listOf_0([ex(this, ChineseNumber_getInstance().ue(2, '\u4E2A', 'g\xE8', 4).ae(Companion_getInstance_14().rd([to('\u4EBA', 'r\xE9n')])), '2 orang'), ex(this, ChineseNumber_getInstance().ue(1, '\u4E2A', 'g\xE8', 4).ae(Companion_getInstance_14().rd([to('\u4EBA', 'r\xE9n')])), '1 orang')])), new LessonRule('Tinggi: \u4E00\u7C73\u4E03\u4E94', 'Meter + sisanya per digit. \u4E00\u7C73\u4E03 = 1,70 m.', listOf_0([ex(this, Companion_getInstance_14().rd([to('\u4E00', 'y\xEC'), to('\u7C73', 'm\u01D0'), to('\u4E03', 'q\u012B')]), '1,70 m'), ex(this, Companion_getInstance_14().rd([to('\u4E00', 'y\xEC'), to('\u7C73', 'm\u01D0'), to('\u516D', 'li\xF9'), to('\u4E94', 'w\u01D4')]), '1,65 m')])), new LessonRule('Berat: \u65A4 dan \u516C\u65A4', '\u516C\u65A4 = kilogram. Di pasar dipakai \u65A4 = \xBD kg.', listOf_0([ex(this, ChineseNumber_getInstance().ue(2, '\u65A4', 'j\u012Bn', 1), '1 kg'), ex(this, ChineseNumber_getInstance().ue(1, '\u516C', 'g\u014Dng', 1).ae(Companion_getInstance_14().rd([to('\u65A4', 'j\u012Bn')])), '1 kg')])), new LessonRule('Lantai: \u697C', 'Lantai = angka + \u697C. Karena urutan, 2 tetap \u4E8C\u697C.', listOf(ex(this, ChineseNumber_getInstance().xd(2).ae(Companion_getInstance_14().rd([to('\u697C', 'l\xF3u')])), 'Lantai 2')))]);
        break;
      case 8:
        tmp = listOf_0([new LessonRule('\u591A = Lebih', 'Angka + \u591A = "lebih".', listOf_0([ex(this, Companion_getInstance_14().rd([to('\u5341', 'sh\xED'), to('\u591A', 'du\u014D'), to('\u4E2A', 'g\xE8')]), 'Belasan (11\u201319)'), ex(this, ChineseNumber_getInstance().xd(100).ae(Companion_getInstance_14().rd([to('\u591A', 'du\u014D')])), 'Seratus lebih')])), new LessonRule('\u51E0 = Beberapa', '\u51E0 sendiri = beberapa (di bawah 10). \u51E0\u5341 = puluhan, \u51E0\u767E = ratusan.', listOf_0([ex(this, Companion_getInstance_14().rd([to('\u51E0', 'j\u01D0'), to('\u4E2A', 'g\xE8')]), 'Beberapa'), ex(this, Companion_getInstance_14().rd([to('\u51E0', 'j\u01D0'), to('\u5341', 'sh\xED')]), 'Puluhan')])), new LessonRule('Dua Angka Berurutan', '\u4E09\u56DB\u4E2A = 3 atau 4. \u4E8C\u4E09\u5341 = sekitar 20\u201330.', listOf_0([ex(this, Companion_getInstance_14().rd([to('\u4E09', 's\u0101n'), to('\u56DB', 's\xEC'), to('\u4E2A', 'g\xE8')]), '3 atau 4'), ex(this, Companion_getInstance_14().rd([to('\u4E8C', '\xE8r'), to('\u4E09', 's\u0101n'), to('\u5341', 'sh\xED')]), '20\u201330')]))]);
        break;
      default:
        noWhenBranchMatchedException();
        break;
    }
    return tmp;
  }
  ve(cat) {
    var c = cat.equals(NumCat_CAMPUR_getInstance()) ? NumCat_UMUM_getInstance() : cat;
    return plus(plus(plus(intro(this, c), theory(this, c)), this.qe(c)), traps(this, c));
  }
}
class NumCat extends Enum {
  constructor(name, ordinal, title, sub, emoji) {
    super(name, ordinal);
    this.ye_1 = title;
    this.ze_1 = sub;
    this.af_1 = emoji;
  }
}
class Keys extends Enum {
  constructor(name, ordinal, extra) {
    super(name, ordinal);
    this.df_1 = extra;
  }
}
class NumOption {
  constructor(text, pinyin, chinese) {
    pinyin = pinyin === VOID ? null : pinyin;
    chinese = chinese === VOID ? false : chinese;
    this.ef_1 = text;
    this.ff_1 = pinyin;
    this.gf_1 = chinese;
  }
  toString() {
    return 'NumOption(text=' + this.ef_1 + ', pinyin=' + this.ff_1 + ', chinese=' + this.gf_1 + ')';
  }
  hashCode() {
    var result = getStringHashCode(this.ef_1);
    result = imul_0(result, 31) + (this.ff_1 == null ? 0 : getStringHashCode(this.ff_1)) | 0;
    result = imul_0(result, 31) + getBooleanHashCode(this.gf_1) | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof NumOption))
      return false;
    var tmp0_other_with_cast = other instanceof NumOption ? other : THROW_CCE();
    if (!(this.ef_1 === tmp0_other_with_cast.ef_1))
      return false;
    if (!(this.ff_1 == tmp0_other_with_cast.ff_1))
      return false;
    if (!(this.gf_1 === tmp0_other_with_cast.gf_1))
      return false;
    return true;
  }
}
class NumQuestion {
  constructor(cat, instruction, hanzi, pinyin, display, options, answer, accepted, answerText, keys, unit, explain, story) {
    hanzi = hanzi === VOID ? null : hanzi;
    pinyin = pinyin === VOID ? null : pinyin;
    display = display === VOID ? null : display;
    options = options === VOID ? emptyList() : options;
    answer = answer === VOID ? -1 : answer;
    accepted = accepted === VOID ? emptySet() : accepted;
    answerText = answerText === VOID ? '' : answerText;
    keys = keys === VOID ? Keys_NUMBER_getInstance() : keys;
    unit = unit === VOID ? null : unit;
    explain = explain === VOID ? '' : explain;
    story = story === VOID ? false : story;
    this.hf_1 = cat;
    this.if_1 = instruction;
    this.jf_1 = hanzi;
    this.kf_1 = pinyin;
    this.lf_1 = display;
    this.mf_1 = options;
    this.nf_1 = answer;
    this.of_1 = accepted;
    this.pf_1 = answerText;
    this.qf_1 = keys;
    this.rf_1 = unit;
    this.sf_1 = explain;
    this.tf_1 = story;
  }
  uf() {
    // Inline function 'kotlin.collections.isNotEmpty' call
    return !this.mf_1.b1();
  }
  toString() {
    return 'NumQuestion(cat=' + this.hf_1.toString() + ', instruction=' + this.if_1 + ', hanzi=' + this.jf_1 + ', pinyin=' + this.kf_1 + ', display=' + this.lf_1 + ', options=' + toString_1(this.mf_1) + ', answer=' + this.nf_1 + ', accepted=' + toString_1(this.of_1) + ', answerText=' + this.pf_1 + ', keys=' + this.qf_1.toString() + ', unit=' + this.rf_1 + ', explain=' + this.sf_1 + ', story=' + this.tf_1 + ')';
  }
  hashCode() {
    var result = this.hf_1.hashCode();
    result = imul_0(result, 31) + getStringHashCode(this.if_1) | 0;
    result = imul_0(result, 31) + (this.jf_1 == null ? 0 : getStringHashCode(this.jf_1)) | 0;
    result = imul_0(result, 31) + (this.kf_1 == null ? 0 : getStringHashCode(this.kf_1)) | 0;
    result = imul_0(result, 31) + (this.lf_1 == null ? 0 : getStringHashCode(this.lf_1)) | 0;
    result = imul_0(result, 31) + hashCode(this.mf_1) | 0;
    result = imul_0(result, 31) + this.nf_1 | 0;
    result = imul_0(result, 31) + hashCode(this.of_1) | 0;
    result = imul_0(result, 31) + getStringHashCode(this.pf_1) | 0;
    result = imul_0(result, 31) + this.qf_1.hashCode() | 0;
    result = imul_0(result, 31) + (this.rf_1 == null ? 0 : getStringHashCode(this.rf_1)) | 0;
    result = imul_0(result, 31) + getStringHashCode(this.sf_1) | 0;
    result = imul_0(result, 31) + getBooleanHashCode(this.tf_1) | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof NumQuestion))
      return false;
    var tmp0_other_with_cast = other instanceof NumQuestion ? other : THROW_CCE();
    if (!this.hf_1.equals(tmp0_other_with_cast.hf_1))
      return false;
    if (!(this.if_1 === tmp0_other_with_cast.if_1))
      return false;
    if (!(this.jf_1 == tmp0_other_with_cast.jf_1))
      return false;
    if (!(this.kf_1 == tmp0_other_with_cast.kf_1))
      return false;
    if (!(this.lf_1 == tmp0_other_with_cast.lf_1))
      return false;
    if (!equals(this.mf_1, tmp0_other_with_cast.mf_1))
      return false;
    if (!(this.nf_1 === tmp0_other_with_cast.nf_1))
      return false;
    if (!equals(this.of_1, tmp0_other_with_cast.of_1))
      return false;
    if (!(this.pf_1 === tmp0_other_with_cast.pf_1))
      return false;
    if (!this.qf_1.equals(tmp0_other_with_cast.qf_1))
      return false;
    if (!(this.rf_1 == tmp0_other_with_cast.rf_1))
      return false;
    if (!(this.sf_1 === tmp0_other_with_cast.sf_1))
      return false;
    if (!(this.tf_1 === tmp0_other_with_cast.tf_1))
      return false;
    return true;
  }
}
class Item {
  constructor(h, p, id, mw, mwp, mwId) {
    this.eg_1 = h;
    this.fg_1 = p;
    this.gg_1 = id;
    this.hg_1 = mw;
    this.ig_1 = mwp;
    this.jg_1 = mwId;
  }
  toString() {
    return 'Item(h=' + this.eg_1 + ', p=' + this.fg_1 + ', id=' + this.gg_1 + ', mw=' + this.hg_1 + ', mwp=' + this.ig_1 + ', mwId=' + this.jg_1 + ')';
  }
  hashCode() {
    var result = getStringHashCode(this.eg_1);
    result = imul_0(result, 31) + getStringHashCode(this.fg_1) | 0;
    result = imul_0(result, 31) + getStringHashCode(this.gg_1) | 0;
    result = imul_0(result, 31) + getStringHashCode(this.hg_1) | 0;
    result = imul_0(result, 31) + getStringHashCode(this.ig_1) | 0;
    result = imul_0(result, 31) + getStringHashCode(this.jg_1) | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof Item))
      return false;
    var tmp0_other_with_cast = other instanceof Item ? other : THROW_CCE();
    if (!(this.eg_1 === tmp0_other_with_cast.eg_1))
      return false;
    if (!(this.fg_1 === tmp0_other_with_cast.fg_1))
      return false;
    if (!(this.gg_1 === tmp0_other_with_cast.gg_1))
      return false;
    if (!(this.hg_1 === tmp0_other_with_cast.hg_1))
      return false;
    if (!(this.ig_1 === tmp0_other_with_cast.ig_1))
      return false;
    if (!(this.jg_1 === tmp0_other_with_cast.jg_1))
      return false;
    return true;
  }
}
class Approx {
  constructor(sp, meaning) {
    this.ng_1 = sp;
    this.og_1 = meaning;
  }
  toString() {
    return 'Approx(sp=' + this.ng_1.toString() + ', meaning=' + this.og_1 + ')';
  }
  hashCode() {
    var result = this.ng_1.hashCode();
    result = imul_0(result, 31) + getStringHashCode(this.og_1) | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof Approx))
      return false;
    var tmp0_other_with_cast = other instanceof Approx ? other : THROW_CCE();
    if (!this.ng_1.equals(tmp0_other_with_cast.ng_1))
      return false;
    if (!(this.og_1 === tmp0_other_with_cast.og_1))
      return false;
    return true;
  }
}
class NumberQuiz {
  constructor() {
    NumberQuiz_instance = this;
    this.wf_1 = Default_getInstance();
    this.xf_1 = listOf_0([to('0 \u2013 10', (new Long(0, 0)).c3(new Long(10, 0))), to('11 \u2013 100', (new Long(11, 0)).c3(new Long(100, 0))), to('101 \u2013 1.000', (new Long(101, 0)).c3(new Long(1000, 0))), to('1.001 \u2013 10.000', (new Long(1001, 0)).c3(new Long(10000, 0))), to('10.001 \u2013 100.000', (new Long(10001, 0)).c3(new Long(100000, 0))), to('100.001 \u2013 1 juta', (new Long(100001, 0)).c3(new Long(1000000, 0))), to('1 juta \u2013 100 juta', (new Long(1000001, 0)).c3(new Long(100000000, 0))), to('100 juta \u2013 1 triliun', (new Long(100000001, 0)).c3(new Long(-727379969, 232)))]);
    this.yf_1 = new Long(-727379969, 232);
    this.zf_1 = listOf_0([new Item('\u82F9\u679C', 'p\xEDng gu\u01D2', 'apel', '\u65A4', 'j\u012Bn', 'jin (\xBD kg)'), new Item('\u9999\u8549', 'xi\u0101ng ji\u0101o', 'pisang', '\u65A4', 'j\u012Bn', 'jin (\xBD kg)'), new Item('\u5496\u5561', 'k\u0101 f\u0113i', 'kopi', '\u676F', 'b\u0113i', 'gelas'), new Item('\u9762\u5305', 'mi\xE0n b\u0101o', 'roti', '\u4E2A', 'g\xE8', 'buah'), new Item('\u725B\u5976', 'ni\xFA n\u01CEi', 'susu', '\u74F6', 'p\xEDng', 'botol'), new Item('\u672C\u5B50', 'b\u011Bn zi', 'buku tulis', '\u672C', 'b\u011Bn', 'buah')]);
    this.ag_1 = listOf_0(['Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni', 'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember']);
    this.bg_1 = listOf_0(['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu', 'Minggu']);
  }
  cg(raw) {
    // Inline function 'kotlin.text.trim' call
    var tmp$ret$0 = toString_1(trim(isCharSequence(raw) ? raw : THROW_CCE()));
    var s = replace(replace(replace(replace(tmp$ret$0, ' ', ''), '\xA5', ''), '%', ''), 'yuan', '');
    var tmp$ret$13;
    $l$block: {
      // Inline function 'kotlin.getOrDefault' call
      // Inline function 'kotlin.runCatching' call
      var tmp;
      try {
        // Inline function 'kotlin.Companion.success' call
        // Inline function 'com.nesxha.readit.data.NumberQuiz.norm.<anonymous>' call
        var tmp_0;
        if (contains_1(s, _Char___init__impl__6a9atx(58))) {
          // Inline function 'kotlin.let' call
          // Inline function 'kotlin.contracts.contract' call
          // Inline function 'com.nesxha.readit.data.NumberQuiz.norm.<anonymous>.<anonymous>' call
          var destruct = split(s, charArrayOf([_Char___init__impl__6a9atx(58)]));
          // Inline function 'kotlin.collections.component1' call
          var h = destruct.d1(0);
          // Inline function 'kotlin.collections.component2' call
          var m = destruct.d1(1);
          tmp_0 = '' + toInt(h) + ':' + padStart(toInt(m).toString(), 2, _Char___init__impl__6a9atx(48));
        } else if (contains_1(s, _Char___init__impl__6a9atx(47))) {
          var tmp_1 = split(s, charArrayOf([_Char___init__impl__6a9atx(47)]));
          tmp_0 = joinToString_0(tmp_1, '/', VOID, VOID, VOID, VOID, NumberQuiz$norm$lambda);
        } else {
          var t = replace(s, '.', '');
          var tmp_2;
          if (contains_1(t, _Char___init__impl__6a9atx(44))) {
            // Inline function 'kotlin.text.ifEmpty' call
            var this_0 = substringBefore(t, _Char___init__impl__6a9atx(44));
            // Inline function 'kotlin.contracts.contract' call
            var tmp_3;
            // Inline function 'kotlin.text.isEmpty' call
            if (charSequenceLength(this_0) === 0) {
              // Inline function 'com.nesxha.readit.data.NumberQuiz.norm.<anonymous>.<anonymous>' call
              tmp_3 = '0';
            } else {
              tmp_3 = this_0;
            }
            var tmp$ret$7 = tmp_3;
            var a = toLong_0(tmp$ret$7);
            var b = trimEnd(substringAfter(t, _Char___init__impl__6a9atx(44)), charArrayOf([_Char___init__impl__6a9atx(48)]));
            var tmp_4;
            // Inline function 'kotlin.text.isEmpty' call
            if (charSequenceLength(b) === 0) {
              tmp_4 = a.toString();
            } else {
              tmp_4 = a.toString() + ',' + b;
            }
            tmp_2 = tmp_4;
          } else {
            tmp_2 = toLong_0(t).toString();
          }
          tmp_0 = tmp_2;
        }
        var value = tmp_0;
        tmp = _Result___init__impl__xyqfz8(value);
      } catch ($p) {
        var tmp_5;
        if ($p instanceof Error) {
          var e = $p;
          // Inline function 'kotlin.Companion.failure' call
          tmp_5 = _Result___init__impl__xyqfz8(createFailure(e));
        } else {
          throw $p;
        }
        tmp = tmp_5;
      }
      var this_1 = tmp;
      if (_Result___get_isFailure__impl__jpiriv(this_1)) {
        tmp$ret$13 = s;
        break $l$block;
      }
      var tmp_6 = _Result___get_value__impl__bjfvqg(this_1);
      tmp$ret$13 = (tmp_6 == null ? true : !(tmp_6 == null)) ? tmp_6 : THROW_CCE();
    }
    return tmp$ret$13;
  }
  pg(cat, difficulty, n, range) {
    var out = ArrayList.h1();
    var guard = 0;
    $l$loop_2: while (true) {
      var tmp;
      if (out.y() < n) {
        var _unary__edvuaz = guard;
        guard = _unary__edvuaz + 1 | 0;
        tmp = _unary__edvuaz < imul_0(n, 20);
      } else {
        tmp = false;
      }
      if (!tmp) {
        break $l$loop_2;
      }
      var tmp_0;
      if (cat.equals(NumCat_CAMPUR_getInstance())) {
        // Inline function 'kotlin.collections.filter' call
        // Inline function 'kotlin.collections.filterTo' call
        var this_0 = get_entries();
        var destination = ArrayList.h1();
        var tmp0_iterator = this_0.r();
        while (tmp0_iterator.s()) {
          var element = tmp0_iterator.t();
          // Inline function 'com.nesxha.readit.data.NumberQuiz.build.<anonymous>' call
          if (!element.equals(NumCat_CAMPUR_getInstance())) {
            destination.h(element);
          }
        }
        tmp_0 = random(destination, this.wf_1);
      } else {
        tmp_0 = cat;
      }
      var c = tmp_0;
      // Inline function 'kotlin.Result.getOrNull' call
      // Inline function 'kotlin.runCatching' call
      var tmp_1;
      try {
        // Inline function 'kotlin.Companion.success' call
        // Inline function 'com.nesxha.readit.data.NumberQuiz.build.<anonymous>' call
        var value = c.equals(NumCat_UMUM_getInstance()) && !(range == null) ? umum(this, difficulty, range) : this.qg(c, difficulty);
        tmp_1 = _Result___init__impl__xyqfz8(value);
      } catch ($p) {
        var tmp_2;
        if ($p instanceof Error) {
          var e = $p;
          // Inline function 'kotlin.Companion.failure' call
          tmp_2 = _Result___init__impl__xyqfz8(createFailure(e));
        } else {
          throw $p;
        }
        tmp_1 = tmp_2;
      }
      var this_1 = tmp_1;
      var tmp_3;
      if (_Result___get_isFailure__impl__jpiriv(this_1)) {
        tmp_3 = null;
      } else {
        var tmp_4 = _Result___get_value__impl__bjfvqg(this_1);
        tmp_3 = (tmp_4 == null ? true : !(tmp_4 == null)) ? tmp_4 : THROW_CCE();
      }
      var tmp0_elvis_lhs = tmp_3;
      var tmp_5;
      if (tmp0_elvis_lhs == null) {
        continue $l$loop_2;
      } else {
        tmp_5 = tmp0_elvis_lhs;
      }
      var q = tmp_5;
      var tmp_6;
      if (q.uf()) {
        var tmp_7;
        if (q.mf_1.y() < 4) {
          tmp_7 = true;
        } else {
          // Inline function 'kotlin.collections.map' call
          var this_2 = q.mf_1;
          // Inline function 'kotlin.collections.mapTo' call
          var destination_0 = ArrayList.z(collectionSizeOrDefault(this_2, 10));
          var tmp0_iterator_0 = this_2.r();
          while (tmp0_iterator_0.s()) {
            var item = tmp0_iterator_0.t();
            // Inline function 'com.nesxha.readit.data.NumberQuiz.build.<anonymous>' call
            var tmp$ret$8 = item.ef_1;
            destination_0.h(tmp$ret$8);
          }
          tmp_7 = toSet_0(destination_0).y() < 4;
        }
        tmp_6 = tmp_7;
      } else {
        tmp_6 = false;
      }
      if (tmp_6)
        continue $l$loop_2;
      var tmp$ret$11;
      $l$block_0: {
        // Inline function 'kotlin.collections.any' call
        var this_3 = takeLast(out, 5);
        var tmp_8;
        if (isInterface(this_3, Collection)) {
          tmp_8 = this_3.b1();
        } else {
          tmp_8 = false;
        }
        if (tmp_8) {
          tmp$ret$11 = false;
          break $l$block_0;
        }
        var tmp0_iterator_1 = this_3.r();
        while (tmp0_iterator_1.s()) {
          var element_0 = tmp0_iterator_1.t();
          // Inline function 'com.nesxha.readit.data.NumberQuiz.build.<anonymous>' call
          if (element_0.jf_1 == q.jf_1 && element_0.lf_1 == q.lf_1 && !(element_0.jf_1 == null)) {
            tmp$ret$11 = true;
            break $l$block_0;
          }
        }
        tmp$ret$11 = false;
      }
      if (tmp$ret$11)
        continue $l$loop_2;
      // Inline function 'kotlin.collections.plusAssign' call
      out.h(q);
    }
    return out;
  }
  qg(cat, d) {
    var tmp;
    switch (cat.p2_1) {
      case 0:
        tmp = umum$default(this, d);
        break;
      case 1:
        tmp = harga(this, d);
        break;
      case 2:
        tmp = diskon(this, d);
        break;
      case 3:
        tmp = persen(this, d);
        break;
      case 4:
        tmp = jam(this, d);
        break;
      case 5:
        tmp = tanggal(this, d);
        break;
      case 6:
        tmp = telepon(this, d);
        break;
      case 7:
        tmp = ukuran(this, d);
        break;
      case 8:
        tmp = kira(this, d);
        break;
      case 9:
        // Inline function 'kotlin.collections.filter' call

        // Inline function 'kotlin.collections.filterTo' call

        var this_0 = get_entries();
        var destination = ArrayList.h1();
        var tmp0_iterator = this_0.r();
        while (tmp0_iterator.s()) {
          var element = tmp0_iterator.t();
          // Inline function 'com.nesxha.readit.data.NumberQuiz.make.<anonymous>' call
          if (!element.equals(NumCat_CAMPUR_getInstance())) {
            destination.h(element);
          }
        }

        tmp = this.qg(random(destination, this.wf_1), d);
        break;
      default:
        noWhenBranchMatchedException();
        break;
    }
    return tmp;
  }
}
class Syl {
  constructor(h, p) {
    this.lg_1 = h;
    this.mg_1 = p;
  }
  toString() {
    return 'Syl(h=' + this.lg_1 + ', p=' + this.mg_1 + ')';
  }
  hashCode() {
    var result = getStringHashCode(this.lg_1);
    result = imul_0(result, 31) + getStringHashCode(this.mg_1) | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof Syl))
      return false;
    var tmp0_other_with_cast = other instanceof Syl ? other : THROW_CCE();
    if (!(this.lg_1 === tmp0_other_with_cast.lg_1))
      return false;
    if (!(this.mg_1 === tmp0_other_with_cast.mg_1))
      return false;
    return true;
  }
}
class Companion_14 {
  constructor() {
    Companion_instance_14 = this;
    this.qd_1 = new Spoken(emptyList());
  }
  rd(pairs) {
    // Inline function 'kotlin.collections.map' call
    // Inline function 'kotlin.collections.mapTo' call
    var destination = ArrayList.z(pairs.length);
    var inductionVariable = 0;
    var last = pairs.length;
    while (inductionVariable < last) {
      var item = pairs[inductionVariable];
      inductionVariable = inductionVariable + 1 | 0;
      // Inline function 'com.nesxha.readit.util.Companion.of.<anonymous>' call
      var tmp$ret$0 = new Syl(item.ad_1, item.bd_1);
      destination.h(tmp$ret$0);
    }
    return new Spoken(destination);
  }
}
class Spoken {
  constructor(syls) {
    Companion_getInstance_14();
    this.zd_1 = syls;
  }
  je() {
    return joinToString_0(this.zd_1, '', VOID, VOID, VOID, VOID, Spoken$_get_hanzi_$lambda_g0dx40);
  }
  ke() {
    return joinToString_0(this.zd_1, ' ', VOID, VOID, VOID, VOID, Spoken$_get_pinyin_$lambda_wqxeyv);
  }
  ae(o) {
    return new Spoken(plus(this.zd_1, o.zd_1));
  }
  toString() {
    return this.je() + ' (' + this.ke() + ')';
  }
  equals(other) {
    var tmp;
    if (other instanceof Spoken) {
      tmp = other.je() === this.je();
    } else {
      tmp = false;
    }
    return tmp;
  }
  hashCode() {
    return getStringHashCode(this.je());
  }
}
class ChineseNumber {
  constructor() {
    ChineseNumber_instance = this;
    var tmp = this;
    // Inline function 'kotlin.arrayOf' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp.sd_1 = ['\u96F6', '\u4E00', '\u4E8C', '\u4E09', '\u56DB', '\u4E94', '\u516D', '\u4E03', '\u516B', '\u4E5D'];
    var tmp_0 = this;
    // Inline function 'kotlin.arrayOf' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp_0.td_1 = ['l\xEDng', 'y\u012B', '\xE8r', 's\u0101n', 's\xEC', 'w\u01D4', 'li\xF9', 'q\u012B', 'b\u0101', 'ji\u01D4'];
    this.ud_1 = new Syl('\u4E24', 'li\u01CEng');
    this.vd_1 = new Syl('\u96F6', 'l\xEDng');
    var tmp_1 = this;
    // Inline function 'kotlin.arrayOf' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp_1.wd_1 = ['\u4E00', '\u4E8C', '\u4E09', '\u56DB', '\u4E94', '\u516D'];
  }
  yd(d) {
    return new Syl(this.sd_1[d], this.td_1[d]);
  }
  he(n) {
    // Inline function 'kotlin.require' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.require' call
    // Inline function 'kotlin.contracts.contract' call
    if (!(n.p1(new Long(0, 0)) >= 0 && n.p1(new Long(-727379968, 232)) < 0)) {
      // Inline function 'kotlin.require.<anonymous>' call
      var message = 'Failed requirement.';
      throw IllegalArgumentException.p(toString_1(message));
    }
    if (n.equals(new Long(0, 0)))
      return new Spoken(listOf(this.vd_1));
    var out = ArrayList.h1();
    var parts = listOf_0([to(n.y2(new Long(100000000, 0)), new Syl('\u4EBF', 'y\xEC')), to(n.y2(new Long(10000, 0)).z2(new Long(10000, 0)), new Syl('\u4E07', 'w\xE0n')), to(n.z2(new Long(10000, 0)), null)]);
    var started = false;
    var gap = false;
    var _iterator__ex2g4s = parts.r();
    $l$loop: while (_iterator__ex2g4s.s()) {
      var _destruct__k2r9zo = _iterator__ex2g4s.t();
      var v = _destruct__k2r9zo.cd();
      var big = _destruct__k2r9zo.dd();
      var sec = v.h3();
      if (sec === 0) {
        if (started)
          gap = true;
        continue $l$loop;
      }
      if (started && (gap || sec < 1000)) {
        // Inline function 'kotlin.collections.plusAssign' call
        var element = this.vd_1;
        out.h(element);
      }
      section(this, sec, !started, !(big == null), out);
      if (!(big == null)) {
        if (sec === 1) {
          out.g1(out.y() - 1 | 0, new Syl('\u4E00', 'y\xED'));
        }
        // Inline function 'kotlin.collections.plusAssign' call
        out.h(big);
      }
      started = true;
      gap = false;
    }
    return new Spoken(out);
  }
  xd(n) {
    return this.he(toLong(n));
  }
  ue(n, unitH, unitP, tone) {
    var unit = Companion_getInstance_14().rd([to(unitH, unitP)]);
    switch (n) {
      case 1:
        return Companion_getInstance_14().rd([to('\u4E00', tone === 4 || tone === 0 ? 'y\xED' : 'y\xEC')]).ae(unit);
      case 2:
        return (new Spoken(listOf(this.ud_1))).ae(unit);
      default:
        return this.xd(n).ae(unit);
    }
  }
  fe(s, yao, zero) {
    // Inline function 'kotlin.text.map' call
    // Inline function 'kotlin.text.filter' call
    // Inline function 'kotlin.text.filterTo' call
    var destination = StringBuilder.e();
    var inductionVariable = 0;
    var last = charSequenceLength(s);
    if (inductionVariable < last)
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        var element = charSequenceGet(s, index);
        // Inline function 'com.nesxha.readit.util.ChineseNumber.digits.<anonymous>' call
        if (isDigit(element)) {
          destination.o8(element);
        }
      }
       while (inductionVariable < last);
    var this_0 = destination.toString();
    // Inline function 'kotlin.text.mapTo' call
    var destination_0 = ArrayList.z(charSequenceLength(this_0));
    var inductionVariable_0 = 0;
    while (inductionVariable_0 < charSequenceLength(this_0)) {
      var item = charSequenceGet(this_0, inductionVariable_0);
      inductionVariable_0 = inductionVariable_0 + 1 | 0;
      // Inline function 'com.nesxha.readit.util.ChineseNumber.digits.<anonymous>' call
      var d = Char__minus_impl_a2frrh(item, _Char___init__impl__6a9atx(48));
      var tmp$ret$3 = d === 0 ? new Syl(zero, 'l\xEDng') : d === 1 && yao ? new Syl('\u5E7A', 'y\u0101o') : ChineseNumber_getInstance().yd(d);
      destination_0.h(tmp$ret$3);
    }
    return new Spoken(destination_0);
  }
  te(s, yao, zero, $super) {
    yao = yao === VOID ? false : yao;
    zero = zero === VOID ? '\u3007' : zero;
    return $super === VOID ? this.fe(s, yao, zero) : $super.fe.call(this, s, yao, zero);
  }
  de(intPart, frac) {
    var sp = this.he(intPart);
    // Inline function 'kotlin.text.isNotEmpty' call
    if (charSequenceLength(frac) > 0) {
      var tmp = sp.ae(Companion_getInstance_14().rd([to('\u70B9', 'di\u01CEn')]));
      // Inline function 'kotlin.text.map' call
      // Inline function 'kotlin.text.mapTo' call
      var destination = ArrayList.z(charSequenceLength(frac));
      var inductionVariable = 0;
      while (inductionVariable < charSequenceLength(frac)) {
        var item = charSequenceGet(frac, inductionVariable);
        inductionVariable = inductionVariable + 1 | 0;
        // Inline function 'com.nesxha.readit.util.ChineseNumber.decimal.<anonymous>' call
        var tmp$ret$1 = ChineseNumber_getInstance().yd(Char__minus_impl_a2frrh(item, _Char___init__impl__6a9atx(48)));
        destination.h(tmp$ret$1);
      }
      sp = tmp.ae(new Spoken(destination));
    }
    return sp;
  }
  re(intPart, frac) {
    return Companion_getInstance_14().rd([to('\u767E', 'b\u01CEi'), to('\u5206', 'f\u0113n'), to('\u4E4B', 'zh\u012B')]).ae(this.de(intPart, frac));
  }
  ce(intPart, frac, $super) {
    frac = frac === VOID ? '' : frac;
    return $super === VOID ? this.re(intPart, frac) : $super.re.call(this, intPart, frac);
  }
  be(num, den) {
    return this.xd(den).ae(Companion_getInstance_14().rd([to('\u5206', 'f\u0113n'), to('\u4E4B', 'zh\u012B')])).ae(this.xd(num));
  }
  ie(yuan, jiao, fen) {
    var sp = Companion_getInstance_14().qd_1;
    if (yuan > 0)
      sp = sp.ae(this.ue(yuan, '\u5757', 'ku\xE0i', 4));
    if (yuan > 0 && jiao > 0 && fen === 0)
      sp = sp.ae(this.xd(jiao));
    else if (jiao > 0 && fen > 0)
      sp = sp.ae(this.ue(jiao, '\u6BDB', 'm\xE1o', 2)).ae(this.xd(fen));
    else if (jiao > 0)
      sp = sp.ae(this.ue(jiao, '\u6BDB', 'm\xE1o', 2));
    else if (yuan > 0 && fen > 0)
      sp = sp.ae(new Spoken(listOf(this.vd_1))).ae(this.ue(fen, '\u5206', 'f\u0113n', 1));
    else if (fen > 0)
      sp = sp.ae(this.ue(fen, '\u5206', 'f\u0113n', 1));
    if (yuan === 0 && jiao === 0 && fen === 0)
      sp = (new Spoken(listOf(this.vd_1))).ae(Companion_getInstance_14().rd([to('\u5757', 'ku\xE0i')]));
    return sp;
  }
  le(yuan, jiao, fen) {
    var sp = yuan > 0 ? this.xd(yuan).ae(Companion_getInstance_14().rd([to('\u5143', 'yu\xE1n')])) : Companion_getInstance_14().qd_1;
    if (jiao > 0)
      sp = sp.ae(this.xd(jiao)).ae(Companion_getInstance_14().rd([to('\u89D2', 'ji\u01CEo')]));
    if (fen > 0) {
      if (yuan > 0 && jiao === 0)
        sp = sp.ae(new Spoken(listOf(this.vd_1)));
      sp = sp.ae(this.xd(fen)).ae(Companion_getInstance_14().rd([to('\u5206', 'f\u0113n')]));
    }
    return sp;
  }
  oe(h, m, style) {
    return m === 0 ? time$hour(h) : style === 1 && m === 30 ? time$hour(h).ae(Companion_getInstance_14().rd([to('\u534A', 'b\xE0n')])) : style === 1 && m === 15 ? time$hour(h).ae(Companion_getInstance_14().rd([to('\u4E00', 'y\xED'), to('\u523B', 'k\xE8')])) : style === 1 && m === 45 ? time$hour(h).ae(Companion_getInstance_14().rd([to('\u4E09', 's\u0101n'), to('\u523B', 'k\xE8')])) : style === 2 && m === 45 ? Companion_getInstance_14().rd([to('\u5DEE', 'ch\xE0'), to('\u4E00', 'y\xED'), to('\u523B', 'k\xE8')]).ae(time$hour((h % 12 | 0) + 1 | 0)) : style === 2 && m >= 50 ? Companion_getInstance_14().rd([to('\u5DEE', 'ch\xE0')]).ae(this.xd(60 - m | 0)).ae(Companion_getInstance_14().rd([to('\u5206', 'f\u0113n')])).ae(time$hour((h % 12 | 0) + 1 | 0)) : m < 10 ? time$hour(h).ae(new Spoken(listOf(this.vd_1))).ae(this.xd(m)).ae(Companion_getInstance_14().rd([to('\u5206', 'f\u0113n')])) : time$hour(h).ae(this.xd(m)).ae(Companion_getInstance_14().rd([to('\u5206', 'f\u0113n')]));
  }
  me(h, m, style, $super) {
    style = style === VOID ? 0 : style;
    return $super === VOID ? this.oe(h, m, style) : $super.oe.call(this, h, m, style);
  }
  kg(d, sunday) {
    var base = Companion_getInstance_14().rd([to('\u661F', 'x\u012Bng'), to('\u671F', 'q\u012B')]);
    return d === 7 ? base.ae(Companion_getInstance_14().rd([to(sunday, sunday === '\u5929' ? 'ti\u0101n' : 'r\xEC')])) : base.ae(new Spoken(listOf(this.yd(d))));
  }
  ee(d, sunday, $super) {
    sunday = sunday === VOID ? '\u5929' : sunday;
    return $super === VOID ? this.kg(d, sunday) : $super.kg.call(this, d, sunday);
  }
  se(y, mo, d, hao) {
    return this.te(y.toString()).ae(Companion_getInstance_14().rd([to('\u5E74', 'ni\xE1n')])).ae(this.xd(mo)).ae(Companion_getInstance_14().rd([to('\u6708', 'yu\xE8')])).ae(this.xd(d)).ae(Companion_getInstance_14().rd([to(hao ? '\u53F7' : '\u65E5', hao ? 'h\xE0o' : 'r\xEC')]));
  }
  ne(y, mo, d, hao, $super) {
    hao = hao === VOID ? true : hao;
    return $super === VOID ? this.se(y, mo, d, hao) : $super.se.call(this, y, mo, d, hao);
  }
  ge(n) {
    if (n.p1(new Long(0, 0)) < 0 || n.p1(new Long(-727379968, 232)) >= 0)
      return emptyList();
    if (n.equals(new Long(0, 0)))
      return listOf('0 dibaca \u96F6 (l\xEDng).');
    var out = ArrayList.h1();
    var a = n.y2(new Long(100000000, 0));
    var b = n.y2(new Long(10000, 0)).z2(new Long(10000, 0));
    var c = n.z2(new Long(10000, 0));
    // Inline function 'kotlin.collections.buildList' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.collections.buildListInternal' call
    // Inline function 'kotlin.apply' call
    var this_0 = ArrayList.h1();
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.nesxha.readit.util.ChineseNumber.steps.<anonymous>' call
    if (a.p1(new Long(0, 0)) > 0) {
      this_0.h(a.toString());
    }
    if (a.p1(new Long(0, 0)) > 0 || b.p1(new Long(0, 0)) > 0) {
      this_0.h(a.p1(new Long(0, 0)) > 0 ? padStart(b.toString(), 4, _Char___init__impl__6a9atx(48)) : b.toString());
    }
    this_0.h(a.p1(new Long(0, 0)) > 0 || b.p1(new Long(0, 0)) > 0 ? padStart(c.toString(), 4, _Char___init__impl__6a9atx(48)) : c.toString());
    var groups = this_0.t5();
    if (n.p1(new Long(10000, 0)) >= 0) {
      // Inline function 'kotlin.collections.plusAssign' call
      var element = 'Pisahkan per 4 digit dari kanan: ' + joinToString_0(groups, ' | ') + '.';
      out.h(element);
      var names = groups.y() === 3 ? listOf_0(['\u4EBF', '\u4E07', 'sisa']) : listOf_0(['\u4E07', 'sisa']);
      // Inline function 'kotlin.collections.plusAssign' call
      var tmp = zip(groups, names);
      var element_0 = 'Setiap kelompok punya nama: ' + joinToString_0(tmp, ', ', VOID, VOID, VOID, VOID, ChineseNumber$steps$lambda) + '.';
      out.h(element_0);
    }
    if (a.p1(new Long(0, 0)) > 0) {
      steps$part(out, a, 'kelompok \u4EBF', true);
      // Inline function 'kotlin.collections.plusAssign' call
      var element_1 = 'Tambahkan \u4EBF: ' + this.he(a).je() + '\u4EBF.';
      out.h(element_1);
    }
    if (b.p1(new Long(0, 0)) > 0 || (a.p1(new Long(0, 0)) > 0 && b.equals(new Long(0, 0)))) {
      if (b.p1(new Long(0, 0)) > 0) {
        steps$part(out, b, 'kelompok \u4E07', a.equals(new Long(0, 0)));
        // Inline function 'kotlin.collections.plusAssign' call
        var element_2 = 'Tambahkan \u4E07: ' + this.he(b).je() + '\u4E07.';
        out.h(element_2);
      } else if (c.p1(new Long(0, 0)) > 0) {
        // Inline function 'kotlin.collections.plusAssign' call
        var element_3 = 'Kelompok \u4E07 = 0000, dilewati; nanti cukup satu \u96F6 sebelum kelompok terakhir.';
        out.h(element_3);
      }
    }
    if (c.p1(new Long(0, 0)) > 0) {
      steps$part(out, c, n.p1(new Long(10000, 0)) >= 0 ? 'kelompok terakhir' : 'angka', n.p1(new Long(10000, 0)) < 0);
    } else if (n.p1(new Long(10000, 0)) >= 0) {
      // Inline function 'kotlin.collections.plusAssign' call
      var element_4 = 'Kelompok terakhir = 0000, tidak dibaca.';
      out.h(element_4);
    }
    // Inline function 'kotlin.collections.plusAssign' call
    var element_5 = 'Gabungkan: ' + this.he(n).je() + ' (' + this.he(n).ke() + ').';
    out.h(element_5);
    return out;
  }
  dg(n) {
    var neg = n.p1(new Long(0, 0)) < 0;
    var d = (neg ? n.a3() : n).toString();
    var sb = StringBuilder.e();
    var inductionVariable = 0;
    var last = charSequenceLength(d) - 1 | 0;
    if (inductionVariable <= last)
      do {
        var i = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        if (i > 0 && ((d.length - i | 0) % 3 | 0) === 0) {
          sb.o8(_Char___init__impl__6a9atx(46));
        }
        sb.o8(charSequenceGet(d, i));
      }
       while (inductionVariable <= last);
    return (neg ? '-' : '') + sb.toString();
  }
  rg(v, maxFrac) {
    var mul = new Long(1, 0);
    // Inline function 'kotlin.repeat' call
    // Inline function 'kotlin.contracts.contract' call
    var inductionVariable = 0;
    if (inductionVariable < maxFrac)
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        // Inline function 'com.nesxha.readit.util.ChineseNumber.fmtDec.<anonymous>' call
        // Inline function 'kotlin.Long.times' call
        mul = mul.x2(toLong(10));
      }
       while (inductionVariable < maxFrac);
    // Inline function 'kotlin.math.floor' call
    // Inline function 'kotlin.math.abs' call
    var x = Math.abs(v) * mul.i3() + 0.5;
    var tmp$ret$2 = Math.floor(x);
    var scaled = numberToLong(tmp$ret$2);
    var whole = scaled.y2(mul);
    var frac = padStart(scaled.z2(mul).toString(), maxFrac, _Char___init__impl__6a9atx(48));
    var s0 = (v < 0 && !scaled.equals(new Long(0, 0)) ? '-' : '') + whole.toString() + (maxFrac > 0 ? '.' + frac : '');
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.nesxha.readit.util.ChineseNumber.fmtDec.<anonymous>' call
    var s = contains_1(s0, _Char___init__impl__6a9atx(46)) ? trimEnd(trimEnd(s0, charArrayOf([_Char___init__impl__6a9atx(48)])), charArrayOf([_Char___init__impl__6a9atx(46)])) : s0;
    var _destruct__k2r9zo = contains_1(s, _Char___init__impl__6a9atx(46)) ? to(substringBefore(s, _Char___init__impl__6a9atx(46)), substringAfter(s, _Char___init__impl__6a9atx(46))) : to(s, '');
    var a = _destruct__k2r9zo.cd();
    var b = _destruct__k2r9zo.dd();
    var tmp = this.dg(toLong_0(a));
    var tmp_0;
    // Inline function 'kotlin.text.isNotEmpty' call
    if (charSequenceLength(b) > 0) {
      tmp_0 = ',' + b;
    } else {
      tmp_0 = '';
    }
    return tmp + tmp_0;
  }
  vf(v, maxFrac, $super) {
    maxFrac = maxFrac === VOID ? 2 : maxFrac;
    return $super === VOID ? this.rg(v, maxFrac) : $super.rg.call(this, v, maxFrac);
  }
}
class PySeg {
  constructor(text, tone) {
    this.sg_1 = text;
    this.tg_1 = tone;
  }
  toString() {
    return 'PySeg(text=' + this.sg_1 + ', tone=' + this.tg_1 + ')';
  }
  hashCode() {
    var result = getStringHashCode(this.sg_1);
    result = imul_0(result, 31) + this.tg_1 | 0;
    return result;
  }
  equals(other) {
    if (this === other)
      return true;
    if (!(other instanceof PySeg))
      return false;
    var tmp0_other_with_cast = other instanceof PySeg ? other : THROW_CCE();
    if (!(this.sg_1 === tmp0_other_with_cast.sg_1))
      return false;
    if (!(this.tg_1 === tmp0_other_with_cast.tg_1))
      return false;
    return true;
  }
}
class Pinyin {
  constructor() {
    Pinyin_instance = this;
    this.ug_1 = 'aeiou\xFCv';
    this.vg_1 = 'bpmfdtnlgkhjqxrzcsyw';
    var tmp = this;
    // Inline function 'kotlin.collections.buildMap' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.collections.buildMapInternal' call
    // Inline function 'kotlin.apply' call
    var this_0 = LinkedHashMap.e9();
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.nesxha.readit.util.Pinyin.TONED.<anonymous>' call
    var rows = listOf_0(['a\u0101\xE1\u01CE\xE0', 'e\u0113\xE9\u011B\xE8', 'i\u012B\xED\u01D0\xEC', 'o\u014D\xF3\u01D2\xF2', 'u\u016B\xFA\u01D4\xF9', '\xFC\u01D6\u01D8\u01DA\u01DC', 'A\u0100\xC1\u01CD\xC0', 'E\u0112\xC9\u011A\xC8', 'I\u012A\xCD\u01CF\xCC', 'O\u014C\xD3\u01D1\xD2', 'U\u016A\xDA\u01D3\xD9', '\xDC\u01D5\u01D7\u01D9\u01DB']);
    var _iterator__ex2g4s = rows.r();
    while (_iterator__ex2g4s.s()) {
      var r = _iterator__ex2g4s.t();
      // Inline function 'kotlin.text.lowercaseChar' call
      // Inline function 'kotlin.text.lowercase' call
      var this_1 = charSequenceGet(r, 0);
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      var tmp$ret$2 = toString(this_1).toLowerCase();
      var b = charSequenceGet(tmp$ret$2, 0);
      var inductionVariable = 1;
      var last = r.length;
      if (inductionVariable < last)
        do {
          var i = inductionVariable;
          inductionVariable = inductionVariable + 1 | 0;
          this_0.m5(new Char(charSequenceGet(r, i)), to(new Char(b), i));
        }
         while (inductionVariable < last);
    }
    this_0.m5(new Char(_Char___init__impl__6a9atx(220)), to(new Char(_Char___init__impl__6a9atx(252)), 0));
    this_0.m5(new Char(_Char___init__impl__6a9atx(324)), to(new Char(_Char___init__impl__6a9atx(110)), 2));
    this_0.m5(new Char(_Char___init__impl__6a9atx(328)), to(new Char(_Char___init__impl__6a9atx(110)), 3));
    this_0.m5(new Char(_Char___init__impl__6a9atx(505)), to(new Char(_Char___init__impl__6a9atx(110)), 4));
    this_0.m5(new Char(_Char___init__impl__6a9atx(7743)), to(new Char(_Char___init__impl__6a9atx(109)), 2));
    tmp.wg_1 = this_0.t5();
  }
  xg(c) {
    var tmp0_safe_receiver = this.wg_1.m2(new Char(c));
    var tmp1_elvis_lhs = tmp0_safe_receiver == null ? null : tmp0_safe_receiver.bd_1;
    return tmp1_elvis_lhs == null ? 0 : tmp1_elvis_lhs;
  }
  yg(py) {
    // Inline function 'kotlin.text.buildString' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.apply' call
    var this_0 = StringBuilder.e();
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.nesxha.readit.util.Pinyin.plain.<anonymous>' call
    var inductionVariable = 0;
    var last = py.length;
    while (inductionVariable < last) {
      var c = charSequenceGet(py, inductionVariable);
      inductionVariable = inductionVariable + 1 | 0;
      if (isLetter(c)) {
        var b = base(Pinyin_getInstance(), c);
        this_0.o8(b === _Char___init__impl__6a9atx(252) ? _Char___init__impl__6a9atx(117) : b);
      } else if (c === _Char___init__impl__6a9atx(32)) {
        this_0.o8(_Char___init__impl__6a9atx(32));
      }
    }
    return this_0.toString();
  }
  zg(s) {
    var tmp$ret$4;
    $l$block: {
      // Inline function 'kotlin.collections.firstOrNull' call
      // Inline function 'kotlin.text.map' call
      // Inline function 'kotlin.text.mapTo' call
      var destination = ArrayList.z(charSequenceLength(s));
      var inductionVariable = 0;
      while (inductionVariable < charSequenceLength(s)) {
        var item = charSequenceGet(s, inductionVariable);
        inductionVariable = inductionVariable + 1 | 0;
        // Inline function 'com.nesxha.readit.util.Pinyin.toneOfSyllable.<anonymous>' call
        var tmp$ret$0 = Pinyin_getInstance().xg(item);
        destination.h(tmp$ret$0);
      }
      var tmp0_iterator = destination.r();
      while (tmp0_iterator.s()) {
        var element = tmp0_iterator.t();
        // Inline function 'com.nesxha.readit.util.Pinyin.toneOfSyllable.<anonymous>' call
        if (element > 0) {
          tmp$ret$4 = element;
          break $l$block;
        }
      }
      tmp$ret$4 = null;
    }
    var tmp0_elvis_lhs = tmp$ret$4;
    return tmp0_elvis_lhs == null ? 0 : tmp0_elvis_lhs;
  }
  ah(py) {
    var out = ArrayList.h1();
    var cur = StringBuilder.e();
    var hasV = false;
    var i = 0;
    $l$loop: while (i < py.length) {
      var c = charSequenceGet(py, i);
      if (!isLetter(c)) {
        // Inline function 'kotlin.text.isNotEmpty' call
        if (charSequenceLength(cur) > 0) {
          // Inline function 'kotlin.collections.plusAssign' call
          var element = cur.toString();
          out.h(element);
        }
        // Inline function 'kotlin.collections.plusAssign' call
        var element_0 = toString(c);
        out.h(element_0);
        cur.aa();
        hasV = false;
        i = i + 1 | 0;
        continue $l$loop;
      }
      var b = base(this, c);
      if (hasV && contains_1('bpmfdtnlgkhjqxrzcsyw', b)) {
        var len = contains_1('zcs', b) && (i + 1 | 0) < py.length && base(this, charSequenceGet(py, i + 1 | 0)) === _Char___init__impl__6a9atx(104) ? 2 : 1;
        var nxt = getOrNull_0(py, i + len | 0);
        var tmp;
        var tmp_0 = nxt;
        if (!((tmp_0 == null ? null : new Char(tmp_0)) == null)) {
          tmp = isVowel(this, nxt);
        } else {
          tmp = false;
        }
        if (tmp) {
          // Inline function 'kotlin.collections.plusAssign' call
          var element_1 = cur.toString();
          out.h(element_1);
          cur.aa();
          hasV = false;
        }
      }
      cur.o8(c);
      if (isVowel(this, c))
        hasV = true;
      i = i + 1 | 0;
    }
    // Inline function 'kotlin.text.isNotEmpty' call
    if (charSequenceLength(cur) > 0) {
      // Inline function 'kotlin.collections.plusAssign' call
      var element_2 = cur.toString();
      out.h(element_2);
    }
    return out;
  }
  bh(py, hanzi) {
    var tmp$ret$0;
    $l$block: {
      // Inline function 'kotlin.collections.count' call
      var this_0 = this.ah(py);
      var tmp;
      if (isInterface(this_0, Collection)) {
        tmp = this_0.b1();
      } else {
        tmp = false;
      }
      if (tmp) {
        tmp$ret$0 = 0;
        break $l$block;
      }
      var count = 0;
      var tmp0_iterator = this_0.r();
      while (tmp0_iterator.s()) {
        var element = tmp0_iterator.t();
        // Inline function 'com.nesxha.readit.util.Pinyin.aligned.<anonymous>' call
        var tmp$ret$2;
        $l$block_0: {
          // Inline function 'kotlin.text.any' call
          var inductionVariable = 0;
          while (inductionVariable < charSequenceLength(element)) {
            var element_0 = charSequenceGet(element, inductionVariable);
            inductionVariable = inductionVariable + 1 | 0;
            // Inline function 'com.nesxha.readit.util.Pinyin.aligned.<anonymous>.<anonymous>' call
            if (isLetter(element_0)) {
              tmp$ret$2 = true;
              break $l$block_0;
            }
          }
          tmp$ret$2 = false;
        }
        if (tmp$ret$2) {
          count = count + 1 | 0;
          checkCountOverflow(count);
        }
      }
      tmp$ret$0 = count;
    }
    var syl = tmp$ret$0;
    var n = hanziCount(this, hanzi);
    // Inline function 'kotlin.text.count' call
    var count_0 = 0;
    var inductionVariable_0 = 0;
    while (inductionVariable_0 < charSequenceLength(hanzi)) {
      var element_1 = charSequenceGet(hanzi, inductionVariable_0);
      inductionVariable_0 = inductionVariable_0 + 1 | 0;
      // Inline function 'com.nesxha.readit.util.Pinyin.aligned.<anonymous>' call
      if (element_1 === _Char___init__impl__6a9atx(20799)) {
        count_0 = count_0 + 1 | 0;
      }
    }
    var er = count_0;
    return n === 0 || syl === n || (er > 0 && syl === (n - er | 0));
  }
  ch(py, hanzi) {
    var parts = this.ah(py);
    if (!this.bh(py, hanzi)) {
      var tmp$ret$4;
      $l$block: {
        // Inline function 'kotlin.collections.firstOrNull' call
        // Inline function 'kotlin.text.map' call
        // Inline function 'kotlin.text.mapTo' call
        var destination = ArrayList.z(charSequenceLength(py));
        var inductionVariable = 0;
        while (inductionVariable < charSequenceLength(py)) {
          var item = charSequenceGet(py, inductionVariable);
          inductionVariable = inductionVariable + 1 | 0;
          // Inline function 'com.nesxha.readit.util.Pinyin.segments.<anonymous>' call
          var tmp$ret$0 = Pinyin_getInstance().xg(item);
          destination.h(tmp$ret$0);
        }
        var tmp0_iterator = destination.r();
        while (tmp0_iterator.s()) {
          var element = tmp0_iterator.t();
          // Inline function 'com.nesxha.readit.util.Pinyin.segments.<anonymous>' call
          if (element > 0) {
            tmp$ret$4 = element;
            break $l$block;
          }
        }
        tmp$ret$4 = null;
      }
      var tmp0_elvis_lhs = tmp$ret$4;
      var t = tmp0_elvis_lhs == null ? 0 : tmp0_elvis_lhs;
      return listOf(new PySeg(py, t));
    }
    // Inline function 'kotlin.collections.map' call
    // Inline function 'kotlin.collections.mapTo' call
    var destination_0 = ArrayList.z(collectionSizeOrDefault(parts, 10));
    var tmp0_iterator_0 = parts.r();
    while (tmp0_iterator_0.s()) {
      var item_0 = tmp0_iterator_0.t();
      // Inline function 'com.nesxha.readit.util.Pinyin.segments.<anonymous>' call
      var tmp;
      var tmp$ret$6;
      $l$block_0: {
        // Inline function 'kotlin.text.any' call
        var inductionVariable_0 = 0;
        while (inductionVariable_0 < charSequenceLength(item_0)) {
          var element_0 = charSequenceGet(item_0, inductionVariable_0);
          inductionVariable_0 = inductionVariable_0 + 1 | 0;
          // Inline function 'com.nesxha.readit.util.Pinyin.segments.<anonymous>.<anonymous>' call
          if (isLetter(element_0)) {
            tmp$ret$6 = true;
            break $l$block_0;
          }
        }
        tmp$ret$6 = false;
      }
      if (tmp$ret$6) {
        tmp = new PySeg(item_0, Pinyin_getInstance().zg(item_0));
      } else {
        tmp = new PySeg(item_0, -1);
      }
      var tmp$ret$7 = tmp;
      destination_0.h(tmp$ret$7);
    }
    return destination_0;
  }
  dh(syllable, tone) {
    // Inline function 'kotlin.text.buildString' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.apply' call
    var this_0 = StringBuilder.e();
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.nesxha.readit.util.Pinyin.withTone.<anonymous>' call
    var inductionVariable = 0;
    var last = syllable.length;
    while (inductionVariable < last) {
      var c = charSequenceGet(syllable, inductionVariable);
      inductionVariable = inductionVariable + 1 | 0;
      this_0.o8(base(Pinyin_getInstance(), c));
    }
    var plain = this_0.toString();
    if (tone === 0)
      return plain;
    // Inline function 'kotlin.run' call
    // Inline function 'kotlin.contracts.contract' call
    var tmp$ret$2;
    $l$block_1: {
      // Inline function 'com.nesxha.readit.util.Pinyin.withTone.<anonymous>' call
      var a = indexOf_1(plain, _Char___init__impl__6a9atx(97));
      if (a >= 0) {
        tmp$ret$2 = a;
        break $l$block_1;
      }
      var e = indexOf_1(plain, _Char___init__impl__6a9atx(101));
      if (e >= 0) {
        tmp$ret$2 = e;
        break $l$block_1;
      }
      var ou = indexOf_2(plain, 'ou');
      if (ou >= 0) {
        tmp$ret$2 = ou;
        break $l$block_1;
      }
      var tmp$ret$4;
      $l$block_2: {
        // Inline function 'kotlin.text.indexOfLast' call
        var inductionVariable_0 = charSequenceLength(plain) - 1 | 0;
        if (0 <= inductionVariable_0)
          do {
            var index = inductionVariable_0;
            inductionVariable_0 = inductionVariable_0 + -1 | 0;
            // Inline function 'com.nesxha.readit.util.Pinyin.withTone.<anonymous>.<anonymous>' call
            var it = charSequenceGet(plain, index);
            if (contains_1('aeiou\xFCv', it)) {
              tmp$ret$4 = index;
              break $l$block_2;
            }
          }
           while (0 <= inductionVariable_0);
        tmp$ret$4 = -1;
      }
      tmp$ret$2 = tmp$ret$4;
    }
    var idx = tmp$ret$2;
    if (idx < 0)
      return plain;
    var ch = charSequenceGet(plain, idx);
    var tmp$ret$7;
    $l$block_3: {
      // Inline function 'kotlin.collections.firstOrNull' call
      var tmp0_iterator = this.wg_1.n2().r();
      while (tmp0_iterator.s()) {
        var element = tmp0_iterator.t();
        // Inline function 'com.nesxha.readit.util.Pinyin.withTone.<anonymous>' call
        if (equals(element.k2().ad_1, new Char(ch)) && element.k2().bd_1 === coerceIn(tone, 1, 4) && isLowerCase(element.j2().u1_1)) {
          tmp$ret$7 = element;
          break $l$block_3;
        }
      }
      tmp$ret$7 = null;
    }
    var tmp0_safe_receiver = tmp$ret$7;
    var tmp;
    if (tmp0_safe_receiver == null) {
      tmp = null;
    } else {
      var tmp_0 = tmp0_safe_receiver.j2();
      tmp = tmp_0 == null ? null : tmp_0.u1_1;
    }
    var tmp1_elvis_lhs = tmp;
    var tmp_1;
    var tmp_2 = tmp1_elvis_lhs;
    if ((tmp_2 == null ? null : new Char(tmp_2)) == null) {
      tmp_1 = ch;
    } else {
      tmp_1 = tmp1_elvis_lhs;
    }
    var marked = toString(tmp_1);
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp_3 = plain.substring(0, idx) + marked;
    // Inline function 'kotlin.text.substring' call
    var startIndex = idx + 1 | 0;
    // Inline function 'kotlin.js.asDynamic' call
    var res = tmp_3 + plain.substring(startIndex);
    var tmp_4;
    var tmp2_safe_receiver = firstOrNull(syllable);
    var tmp_5;
    var tmp_6 = tmp2_safe_receiver;
    if ((tmp_6 == null ? null : new Char(tmp_6)) == null) {
      tmp_5 = null;
    } else {
      tmp_5 = isUpperCase(tmp2_safe_receiver);
    }
    if (tmp_5 === true) {
      // Inline function 'kotlin.text.replaceFirstChar' call
      var tmp_7;
      // Inline function 'kotlin.text.isNotEmpty' call
      if (charSequenceLength(res) > 0) {
        // Inline function 'kotlin.text.plus' call
        // Inline function 'com.nesxha.readit.util.Pinyin.withTone.<anonymous>' call
        var it_0 = charSequenceGet(res, 0);
        var this_1 = uppercaseChar(it_0);
        // Inline function 'kotlin.text.substring' call
        // Inline function 'kotlin.js.asDynamic' call
        var other = res.substring(1);
        tmp_7 = toString(this_1) + other;
      } else {
        tmp_7 = res;
      }
      tmp_4 = tmp_7;
    } else {
      tmp_4 = res;
    }
    return tmp_4;
  }
}
//endregion
var Unit_instance;
function Unit_getInstance() {
  return Unit_instance;
}
function joinToString(_this__u8e3s4, separator, prefix, postfix, limit, truncated, transform) {
  separator = separator === VOID ? ', ' : separator;
  prefix = prefix === VOID ? '' : prefix;
  postfix = postfix === VOID ? '' : postfix;
  limit = limit === VOID ? -1 : limit;
  truncated = truncated === VOID ? '...' : truncated;
  transform = transform === VOID ? null : transform;
  return joinTo(_this__u8e3s4, StringBuilder.e(), separator, prefix, postfix, limit, truncated, transform).toString();
}
function toSet(_this__u8e3s4) {
  switch (_this__u8e3s4.length) {
    case 0:
      return emptySet();
    case 1:
      return setOf(_this__u8e3s4[0]);
    default:
      return toCollection(_this__u8e3s4, LinkedHashSet.g(mapCapacity(_this__u8e3s4.length)));
  }
}
function toCollection(_this__u8e3s4, destination) {
  var inductionVariable = 0;
  var last = _this__u8e3s4.length;
  while (inductionVariable < last) {
    var item = _this__u8e3s4[inductionVariable];
    inductionVariable = inductionVariable + 1 | 0;
    destination.h(item);
  }
  return destination;
}
function contains(_this__u8e3s4, element) {
  return indexOf_0(_this__u8e3s4, element) >= 0;
}
function single(_this__u8e3s4) {
  var tmp;
  switch (_this__u8e3s4.length) {
    case 0:
      throw NoSuchElementException.l('Array is empty.');
    case 1:
      tmp = _this__u8e3s4[0];
      break;
    default:
      throw IllegalArgumentException.p('Array has more than one element.');
  }
  return tmp;
}
function indexOf(_this__u8e3s4, element) {
  if (element == null) {
    var inductionVariable = 0;
    var last = _this__u8e3s4.length - 1 | 0;
    if (inductionVariable <= last)
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        if (_this__u8e3s4[index] == null) {
          return index;
        }
      }
       while (inductionVariable <= last);
  } else {
    var inductionVariable_0 = 0;
    var last_0 = _this__u8e3s4.length - 1 | 0;
    if (inductionVariable_0 <= last_0)
      do {
        var index_0 = inductionVariable_0;
        inductionVariable_0 = inductionVariable_0 + 1 | 0;
        if (equals(element, _this__u8e3s4[index_0])) {
          return index_0;
        }
      }
       while (inductionVariable_0 <= last_0);
  }
  return -1;
}
function joinTo(_this__u8e3s4, buffer, separator, prefix, postfix, limit, truncated, transform) {
  separator = separator === VOID ? ', ' : separator;
  prefix = prefix === VOID ? '' : prefix;
  postfix = postfix === VOID ? '' : postfix;
  limit = limit === VOID ? -1 : limit;
  truncated = truncated === VOID ? '...' : truncated;
  transform = transform === VOID ? null : transform;
  buffer.q(prefix);
  var count = 0;
  var inductionVariable = 0;
  var last = _this__u8e3s4.length;
  $l$loop: while (inductionVariable < last) {
    var element = _this__u8e3s4[inductionVariable];
    inductionVariable = inductionVariable + 1 | 0;
    count = count + 1 | 0;
    if (count > 1) {
      buffer.q(separator);
    }
    if (limit < 0 || count <= limit) {
      appendElement(buffer, element, transform);
    } else
      break $l$loop;
  }
  if (limit >= 0 && count > limit) {
    buffer.q(truncated);
  }
  buffer.q(postfix);
  return buffer;
}
function indexOf_0(_this__u8e3s4, element) {
  var inductionVariable = 0;
  var last = _this__u8e3s4.length - 1 | 0;
  if (inductionVariable <= last)
    do {
      var index = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      if (element === _this__u8e3s4[index]) {
        return index;
      }
    }
     while (inductionVariable <= last);
  return -1;
}
function contains_0(_this__u8e3s4, element) {
  return indexOf(_this__u8e3s4, element) >= 0;
}
function getOrNull(_this__u8e3s4, index) {
  return (0 <= index ? index <= (_this__u8e3s4.length - 1 | 0) : false) ? _this__u8e3s4[index] : null;
}
function joinToString_0(_this__u8e3s4, separator, prefix, postfix, limit, truncated, transform) {
  separator = separator === VOID ? ', ' : separator;
  prefix = prefix === VOID ? '' : prefix;
  postfix = postfix === VOID ? '' : postfix;
  limit = limit === VOID ? -1 : limit;
  truncated = truncated === VOID ? '...' : truncated;
  transform = transform === VOID ? null : transform;
  return joinTo_0(_this__u8e3s4, StringBuilder.e(), separator, prefix, postfix, limit, truncated, transform).toString();
}
function joinTo_0(_this__u8e3s4, buffer, separator, prefix, postfix, limit, truncated, transform) {
  separator = separator === VOID ? ', ' : separator;
  prefix = prefix === VOID ? '' : prefix;
  postfix = postfix === VOID ? '' : postfix;
  limit = limit === VOID ? -1 : limit;
  truncated = truncated === VOID ? '...' : truncated;
  transform = transform === VOID ? null : transform;
  buffer.q(prefix);
  var count = 0;
  var tmp0_iterator = _this__u8e3s4.r();
  $l$loop: while (tmp0_iterator.s()) {
    var element = tmp0_iterator.t();
    count = count + 1 | 0;
    if (count > 1) {
      buffer.q(separator);
    }
    if (limit < 0 || count <= limit) {
      appendElement(buffer, element, transform);
    } else
      break $l$loop;
  }
  if (limit >= 0 && count > limit) {
    buffer.q(truncated);
  }
  buffer.q(postfix);
  return buffer;
}
function plus(_this__u8e3s4, elements) {
  if (isInterface(elements, Collection)) {
    var result = ArrayList.z(_this__u8e3s4.y() + elements.y() | 0);
    result.a1(_this__u8e3s4);
    result.a1(elements);
    return result;
  } else {
    var result_0 = ArrayList.x(_this__u8e3s4);
    addAll(result_0, elements);
    return result_0;
  }
}
function random(_this__u8e3s4, random) {
  if (_this__u8e3s4.b1())
    throw NoSuchElementException.l('Collection is empty.');
  return elementAt(_this__u8e3s4, random.c1(_this__u8e3s4.y()));
}
function toSet_0(_this__u8e3s4) {
  if (isInterface(_this__u8e3s4, Collection)) {
    var tmp;
    switch (_this__u8e3s4.y()) {
      case 0:
        tmp = emptySet();
        break;
      case 1:
        var tmp_0;
        if (isInterface(_this__u8e3s4, KtList)) {
          tmp_0 = _this__u8e3s4.d1(0);
        } else {
          tmp_0 = _this__u8e3s4.r().t();
        }

        tmp = setOf(tmp_0);
        break;
      default:
        tmp = toCollection_0(_this__u8e3s4, LinkedHashSet.g(mapCapacity(_this__u8e3s4.y())));
        break;
    }
    return tmp;
  }
  return optimizeReadOnlySet(toCollection_0(_this__u8e3s4, LinkedHashSet.e1()));
}
function takeLast(_this__u8e3s4, n) {
  // Inline function 'kotlin.require' call
  // Inline function 'kotlin.contracts.contract' call
  if (!(n >= 0)) {
    // Inline function 'kotlin.collections.takeLast.<anonymous>' call
    var message = 'Requested element count ' + n + ' is less than zero.';
    throw IllegalArgumentException.p(toString_1(message));
  }
  if (n === 0)
    return emptyList();
  var size = _this__u8e3s4.y();
  if (n >= size)
    return toList(_this__u8e3s4);
  if (n === 1)
    return listOf(last(_this__u8e3s4));
  var list = ArrayList.z(n);
  if (isInterface(_this__u8e3s4, RandomAccess)) {
    var inductionVariable = size - n | 0;
    if (inductionVariable < size)
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        list.h(_this__u8e3s4.d1(index));
      }
       while (inductionVariable < size);
  } else {
    // Inline function 'kotlin.collections.iterator' call
    var tmp1_iterator = _this__u8e3s4.f1(size - n | 0);
    while (tmp1_iterator.s()) {
      var item = tmp1_iterator.t();
      list.h(item);
    }
  }
  return list;
}
function take(_this__u8e3s4, n) {
  // Inline function 'kotlin.require' call
  // Inline function 'kotlin.contracts.contract' call
  if (!(n >= 0)) {
    // Inline function 'kotlin.collections.take.<anonymous>' call
    var message = 'Requested element count ' + n + ' is less than zero.';
    throw IllegalArgumentException.p(toString_1(message));
  }
  if (n === 0)
    return emptyList();
  if (isInterface(_this__u8e3s4, Collection)) {
    if (n >= _this__u8e3s4.y())
      return toList(_this__u8e3s4);
    if (n === 1)
      return listOf(first(_this__u8e3s4));
  }
  var count = 0;
  var list = ArrayList.z(n);
  var tmp0_iterator = _this__u8e3s4.r();
  $l$loop: while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    list.h(item);
    count = count + 1 | 0;
    if (count === n)
      break $l$loop;
  }
  return optimizeReadOnlyList(list);
}
function plus_0(_this__u8e3s4, element) {
  var result = ArrayList.z(_this__u8e3s4.y() + 1 | 0);
  result.a1(_this__u8e3s4);
  result.h(element);
  return result;
}
function zip(_this__u8e3s4, other) {
  // Inline function 'kotlin.collections.zip' call
  var first = _this__u8e3s4.r();
  var second = other.r();
  // Inline function 'kotlin.comparisons.minOf' call
  var a = collectionSizeOrDefault(_this__u8e3s4, 10);
  var b = collectionSizeOrDefault(other, 10);
  var tmp$ret$0 = Math.min(a, b);
  var list = ArrayList.z(tmp$ret$0);
  while (first.s() && second.s()) {
    // Inline function 'kotlin.collections.zip.<anonymous>' call
    var t1 = first.t();
    var t2 = second.t();
    var tmp$ret$1 = to(t1, t2);
    list.h(tmp$ret$1);
  }
  return list;
}
function toMutableList(_this__u8e3s4) {
  return ArrayList.x(_this__u8e3s4);
}
function minus(_this__u8e3s4, element) {
  var result = ArrayList.z(collectionSizeOrDefault(_this__u8e3s4, 10));
  var removed = false;
  // Inline function 'kotlin.collections.filterTo' call
  var tmp0_iterator = _this__u8e3s4.r();
  while (tmp0_iterator.s()) {
    var element_0 = tmp0_iterator.t();
    // Inline function 'kotlin.collections.minus.<anonymous>' call
    var tmp;
    if (!removed && equals(element_0, element)) {
      removed = true;
      tmp = false;
    } else {
      tmp = true;
    }
    if (tmp) {
      result.h(element_0);
    }
  }
  return result;
}
function shuffle(_this__u8e3s4, random) {
  var inductionVariable = get_lastIndex(_this__u8e3s4);
  if (1 <= inductionVariable)
    do {
      var i = inductionVariable;
      inductionVariable = inductionVariable + -1 | 0;
      var j = random.c1(i + 1 | 0);
      _this__u8e3s4.g1(j, _this__u8e3s4.g1(i, _this__u8e3s4.d1(j)));
    }
     while (1 <= inductionVariable);
}
function toMutableList_0(_this__u8e3s4) {
  if (isInterface(_this__u8e3s4, Collection))
    return toMutableList(_this__u8e3s4);
  return toCollection_0(_this__u8e3s4, ArrayList.h1());
}
function elementAt(_this__u8e3s4, index) {
  if (isInterface(_this__u8e3s4, KtList))
    return _this__u8e3s4.d1(index);
  return elementAtOrElse(_this__u8e3s4, index, elementAt$lambda(index));
}
function toCollection_0(_this__u8e3s4, destination) {
  var tmp0_iterator = _this__u8e3s4.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    destination.h(item);
  }
  return destination;
}
function toList(_this__u8e3s4) {
  if (isInterface(_this__u8e3s4, Collection)) {
    var tmp;
    switch (_this__u8e3s4.y()) {
      case 0:
        tmp = emptyList();
        break;
      case 1:
        var tmp_0;
        if (isInterface(_this__u8e3s4, KtList)) {
          tmp_0 = _this__u8e3s4.d1(0);
        } else {
          tmp_0 = _this__u8e3s4.r().t();
        }

        tmp = listOf(tmp_0);
        break;
      default:
        tmp = toMutableList(_this__u8e3s4);
        break;
    }
    return tmp;
  }
  return optimizeReadOnlyList(toMutableList_0(_this__u8e3s4));
}
function last(_this__u8e3s4) {
  if (_this__u8e3s4.b1())
    throw NoSuchElementException.l('List is empty.');
  return _this__u8e3s4.d1(get_lastIndex(_this__u8e3s4));
}
function first(_this__u8e3s4) {
  if (isInterface(_this__u8e3s4, KtList))
    return first_0(_this__u8e3s4);
  else {
    var iterator = _this__u8e3s4.r();
    if (!iterator.s())
      throw NoSuchElementException.l('Collection is empty.');
    return iterator.t();
  }
}
function elementAtOrElse(_this__u8e3s4, index, defaultValue) {
  // Inline function 'kotlin.contracts.contract' call
  if (isInterface(_this__u8e3s4, KtList)) {
    // Inline function 'kotlin.collections.getOrElse' call
    // Inline function 'kotlin.contracts.contract' call
    return (0 <= index ? index < _this__u8e3s4.y() : false) ? _this__u8e3s4.d1(index) : defaultValue(index);
  }
  if (index < 0)
    return defaultValue(index);
  var iterator = _this__u8e3s4.r();
  var count = 0;
  while (iterator.s()) {
    var element = iterator.t();
    var tmp0 = count;
    count = tmp0 + 1 | 0;
    if (index === tmp0)
      return element;
  }
  return defaultValue(index);
}
function first_0(_this__u8e3s4) {
  if (_this__u8e3s4.b1())
    throw NoSuchElementException.l('List is empty.');
  return _this__u8e3s4.d1(0);
}
function single_0(_this__u8e3s4) {
  if (isInterface(_this__u8e3s4, KtList))
    return single_1(_this__u8e3s4);
  else {
    var iterator = _this__u8e3s4.r();
    if (!iterator.s())
      throw NoSuchElementException.l('Collection is empty.');
    var single = iterator.t();
    if (iterator.s())
      throw IllegalArgumentException.p('Collection has more than one element.');
    return single;
  }
}
function single_1(_this__u8e3s4) {
  var tmp;
  switch (_this__u8e3s4.y()) {
    case 0:
      throw NoSuchElementException.l('List is empty.');
    case 1:
      tmp = _this__u8e3s4.d1(0);
      break;
    default:
      throw IllegalArgumentException.p('List has more than one element.');
  }
  return tmp;
}
function elementAt$lambda($index) {
  return function (it) {
    throw IndexOutOfBoundsException.l1("Collection doesn't contain element at index " + $index + '.');
  };
}
function until(_this__u8e3s4, to) {
  if (to <= -2147483648)
    return Companion_getInstance_9().m1_1;
  return numberRangeToNumber(_this__u8e3s4, to - 1 | 0);
}
function coerceAtLeast(_this__u8e3s4, minimumValue) {
  return _this__u8e3s4.p1(minimumValue) < 0 ? minimumValue : _this__u8e3s4;
}
function coerceAtMost(_this__u8e3s4, maximumValue) {
  return _this__u8e3s4.p1(maximumValue) > 0 ? maximumValue : _this__u8e3s4;
}
function coerceAtMost_0(_this__u8e3s4, maximumValue) {
  return _this__u8e3s4 > maximumValue ? maximumValue : _this__u8e3s4;
}
function coerceIn(_this__u8e3s4, minimumValue, maximumValue) {
  if (minimumValue > maximumValue)
    throw IllegalArgumentException.p('Cannot coerce value to an empty range: maximum ' + maximumValue + ' is less than minimum ' + minimumValue + '.');
  if (_this__u8e3s4 < minimumValue)
    return minimumValue;
  if (_this__u8e3s4 > maximumValue)
    return maximumValue;
  return _this__u8e3s4;
}
function downTo(_this__u8e3s4, to) {
  return Companion_instance_11.q1(_this__u8e3s4, to, -1);
}
function coerceAtLeast_0(_this__u8e3s4, minimumValue) {
  return _this__u8e3s4 < minimumValue ? minimumValue : _this__u8e3s4;
}
function asIterable(_this__u8e3s4) {
  // Inline function 'kotlin.collections.Iterable' call
  return new _no_name_provided__qut3iv(_this__u8e3s4);
}
function chunked(_this__u8e3s4, size) {
  return windowed(_this__u8e3s4, size, size, true);
}
function getOrNull_0(_this__u8e3s4, index) {
  return (0 <= index ? index <= (charSequenceLength(_this__u8e3s4) - 1 | 0) : false) ? charSequenceGet(_this__u8e3s4, index) : null;
}
function firstOrNull(_this__u8e3s4) {
  var tmp;
  // Inline function 'kotlin.text.isEmpty' call
  if (charSequenceLength(_this__u8e3s4) === 0) {
    tmp = null;
  } else {
    tmp = charSequenceGet(_this__u8e3s4, 0);
  }
  return tmp;
}
function windowed(_this__u8e3s4, size, step, partialWindows) {
  step = step === VOID ? 1 : step;
  partialWindows = partialWindows === VOID ? false : partialWindows;
  return windowed_0(_this__u8e3s4, size, step, partialWindows, windowed$lambda);
}
function reversed(_this__u8e3s4) {
  return StringBuilder.s1(_this__u8e3s4).t1();
}
function windowed_0(_this__u8e3s4, size, step, partialWindows, transform) {
  step = step === VOID ? 1 : step;
  partialWindows = partialWindows === VOID ? false : partialWindows;
  checkWindowSizeStep(size, step);
  var thisSize = charSequenceLength(_this__u8e3s4);
  var resultCapacity = (thisSize / step | 0) + ((thisSize % step | 0) === 0 ? 0 : 1) | 0;
  var result = ArrayList.z(resultCapacity);
  var index = 0;
  $l$loop: while (0 <= index ? index < thisSize : false) {
    var end = index + size | 0;
    var tmp;
    if (end < 0 || end > thisSize) {
      var tmp_0;
      if (partialWindows) {
        tmp_0 = thisSize;
      } else {
        break $l$loop;
      }
      tmp = tmp_0;
    } else {
      tmp = end;
    }
    var coercedEnd = tmp;
    result.h(transform(charSequenceSubSequence(_this__u8e3s4, index, coercedEnd)));
    index = index + step | 0;
  }
  return result;
}
function windowed$lambda(it) {
  return toString_1(it);
}
function _Char___init__impl__6a9atx(value) {
  return value;
}
function _get_value__a43j40($this) {
  return $this;
}
function _Char___init__impl__6a9atx_0(code) {
  // Inline function 'kotlin.UShort.toInt' call
  var tmp$ret$0 = _UShort___get_data__impl__g0245(code) & 65535;
  return _Char___init__impl__6a9atx(tmp$ret$0);
}
function Char__compareTo_impl_ypi4mb($this, other) {
  return _get_value__a43j40($this) - _get_value__a43j40(other) | 0;
}
function Char__compareTo_impl_ypi4mb_0($this, other) {
  return Char__compareTo_impl_ypi4mb($this.u1_1, other instanceof Char ? other.u1_1 : THROW_CCE());
}
function Char__plus_impl_qi7pgj($this, other) {
  return numberToChar(_get_value__a43j40($this) + other | 0);
}
function Char__minus_impl_a2frrh($this, other) {
  return _get_value__a43j40($this) - _get_value__a43j40(other) | 0;
}
function Char__toInt_impl_vasixd($this) {
  return _get_value__a43j40($this);
}
function toString($this) {
  // Inline function 'kotlin.js.unsafeCast' call
  return String.fromCharCode(_get_value__a43j40($this));
}
function Char__equals_impl_x6719k($this, other) {
  if (!(other instanceof Char))
    return false;
  return _get_value__a43j40($this) === _get_value__a43j40(other.u1_1);
}
function Char__hashCode_impl_otmys($this) {
  return _get_value__a43j40($this);
}
var Companion_instance;
function Companion_getInstance() {
  if (Companion_instance === VOID)
    new Companion();
  return Companion_instance;
}
var Companion_instance_0;
function Companion_getInstance_0() {
  return Companion_instance_0;
}
function toString_0(_this__u8e3s4) {
  var tmp1_elvis_lhs = _this__u8e3s4 == null ? null : toString_1(_this__u8e3s4);
  return tmp1_elvis_lhs == null ? 'null' : tmp1_elvis_lhs;
}
var Companion_instance_1;
function Companion_getInstance_1() {
  if (Companion_instance_1 === VOID)
    new Companion_1();
  return Companion_instance_1;
}
function implement(interfaces) {
  var maxSize = 1;
  var masks = [];
  var inductionVariable = 0;
  var last = interfaces.length;
  while (inductionVariable < last) {
    var i = interfaces[inductionVariable];
    inductionVariable = inductionVariable + 1 | 0;
    var currentSize = maxSize;
    var tmp1_elvis_lhs = i.prototype.$imask$;
    var imask = tmp1_elvis_lhs == null ? i.$imask$ : tmp1_elvis_lhs;
    if (!(imask == null)) {
      masks.push(imask);
      currentSize = imask.length;
    }
    var iid = i.$metadata$.iid;
    var tmp;
    if (iid == null) {
      tmp = null;
    } else {
      // Inline function 'kotlin.let' call
      // Inline function 'kotlin.contracts.contract' call
      // Inline function 'kotlin.js.implement.<anonymous>' call
      tmp = bitMaskWith(iid);
    }
    var iidImask = tmp;
    if (!(iidImask == null)) {
      masks.push(iidImask);
      currentSize = Math.max(currentSize, iidImask.length);
    }
    if (currentSize > maxSize) {
      maxSize = currentSize;
    }
  }
  return compositeBitMask(maxSize, masks);
}
function bitMaskWith(activeBit) {
  var numberIndex = activeBit >> 5;
  var intArray = new Int32Array(numberIndex + 1 | 0);
  var positionInNumber = activeBit & 31;
  var numberWithSettledBit = 1 << positionInNumber;
  intArray[numberIndex] = intArray[numberIndex] | numberWithSettledBit;
  return intArray;
}
function compositeBitMask(capacity, masks) {
  var tmp = 0;
  var tmp_0 = new Int32Array(capacity);
  while (tmp < capacity) {
    var tmp_1 = tmp;
    var result = 0;
    var inductionVariable = 0;
    var last = masks.length;
    while (inductionVariable < last) {
      var mask = masks[inductionVariable];
      inductionVariable = inductionVariable + 1 | 0;
      if (tmp_1 < mask.length) {
        result = result | mask[tmp_1];
      }
    }
    tmp_0[tmp_1] = result;
    tmp = tmp + 1 | 0;
  }
  return tmp_0;
}
function isBitSet(_this__u8e3s4, possibleActiveBit) {
  var numberIndex = possibleActiveBit >> 5;
  if (numberIndex > _this__u8e3s4.length)
    return false;
  var positionInNumber = possibleActiveBit & 31;
  var numberWithSettledBit = 1 << positionInNumber;
  return !((_this__u8e3s4[numberIndex] & numberWithSettledBit) === 0);
}
function fillArrayVal(array, initValue) {
  var inductionVariable = 0;
  var last = array.length - 1 | 0;
  if (inductionVariable <= last)
    do {
      var i = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      array[i] = initValue;
    }
     while (!(i === last));
  return array;
}
function arrayIterator(array) {
  return new arrayIterator$1(array);
}
function charArray(size) {
  // Inline function 'kotlin.js.unsafeCast' call
  // Inline function 'withType' call
  var type = 'CharArray';
  var array = new Uint16Array(size);
  array.$type$ = type;
  return array;
}
function charArrayOf(arr) {
  // Inline function 'kotlin.js.unsafeCast' call
  // Inline function 'withType' call
  var type = 'CharArray';
  var array = new Uint16Array(arr);
  array.$type$ = type;
  return array;
}
function get_buf() {
  _init_properties_bitUtils_kt__nfcg4k();
  return buf;
}
var buf;
function get_bufFloat64() {
  _init_properties_bitUtils_kt__nfcg4k();
  return bufFloat64;
}
var bufFloat64;
var bufFloat32;
function get_bufInt32() {
  _init_properties_bitUtils_kt__nfcg4k();
  return bufInt32;
}
var bufInt32;
function get_lowIndex() {
  _init_properties_bitUtils_kt__nfcg4k();
  return lowIndex;
}
var lowIndex;
function get_highIndex() {
  _init_properties_bitUtils_kt__nfcg4k();
  return highIndex;
}
var highIndex;
function getNumberHashCode(obj) {
  _init_properties_bitUtils_kt__nfcg4k();
  // Inline function 'kotlin.js.unsafeCast' call
  // Inline function 'kotlin.js.jsBitwiseOr' call
  // Inline function 'kotlin.js.asDynamic' call
  if ((obj | 0) === obj) {
    return numberToInt(obj);
  }
  get_bufFloat64()[0] = obj;
  return imul_0(get_bufInt32()[get_highIndex()], 31) + get_bufInt32()[get_lowIndex()] | 0;
}
var properties_initialized_bitUtils_kt_i2bo3e;
function _init_properties_bitUtils_kt__nfcg4k() {
  if (!properties_initialized_bitUtils_kt_i2bo3e) {
    properties_initialized_bitUtils_kt_i2bo3e = true;
    buf = new ArrayBuffer(8);
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    bufFloat64 = new Float64Array(get_buf());
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    bufFloat32 = new Float32Array(get_buf());
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    bufInt32 = new Int32Array(get_buf());
    // Inline function 'kotlin.run' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.js.lowIndex.<anonymous>' call
    get_bufFloat64()[0] = -1.0;
    lowIndex = !(get_bufInt32()[0] === 0) ? 1 : 0;
    highIndex = 1 - get_lowIndex() | 0;
  }
}
function charSequenceGet(a, index) {
  var tmp;
  if (isString(a)) {
    // Inline function 'kotlin.Char' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    var code = a.charCodeAt(index);
    var tmp_0;
    // Inline function 'kotlin.code' call
    var this_0 = _Char___init__impl__6a9atx(0);
    if (code < Char__toInt_impl_vasixd(this_0)) {
      tmp_0 = true;
    } else {
      // Inline function 'kotlin.code' call
      var this_1 = _Char___init__impl__6a9atx(65535);
      tmp_0 = code > Char__toInt_impl_vasixd(this_1);
    }
    if (tmp_0) {
      throw IllegalArgumentException.p('Invalid Char code: ' + code);
    }
    tmp = numberToChar(code);
  } else {
    tmp = a.b(index);
  }
  return tmp;
}
function isString(a) {
  return typeof a === 'string';
}
function charSequenceLength(a) {
  var tmp;
  if (isString(a)) {
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp = a.length;
  } else {
    tmp = a.a();
  }
  return tmp;
}
function charSequenceSubSequence(a, startIndex, endIndex) {
  var tmp;
  if (isString(a)) {
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp = a.substring(startIndex, endIndex);
  } else {
    tmp = a.c(startIndex, endIndex);
  }
  return tmp;
}
function arrayToString(array) {
  return joinToString(array, ', ', '[', ']', VOID, VOID, arrayToString$lambda);
}
function arrayToString$lambda(it) {
  return toString_1(it);
}
function compareTo(a, b) {
  var tmp;
  switch (typeof a) {
    case 'number':
      var tmp_0;
      if (typeof b === 'number') {
        tmp_0 = doubleCompareTo(a, b);
      } else {
        if (b instanceof Long) {
          tmp_0 = doubleCompareTo(a, b.i3());
        } else {
          tmp_0 = primitiveCompareTo(a, b);
        }
      }

      tmp = tmp_0;
      break;
    case 'string':
    case 'boolean':
      tmp = primitiveCompareTo(a, b);
      break;
    default:
      tmp = compareToDoNotIntrinsicify(a, b);
      break;
  }
  return tmp;
}
function doubleCompareTo(a, b) {
  var tmp;
  if (a < b) {
    tmp = -1;
  } else if (a > b) {
    tmp = 1;
  } else if (a === b) {
    var tmp_0;
    if (a !== 0) {
      tmp_0 = 0;
    } else {
      // Inline function 'kotlin.js.asDynamic' call
      var ia = 1 / a;
      var tmp_1;
      // Inline function 'kotlin.js.asDynamic' call
      if (ia === 1 / b) {
        tmp_1 = 0;
      } else {
        if (ia < 0) {
          tmp_1 = -1;
        } else {
          tmp_1 = 1;
        }
      }
      tmp_0 = tmp_1;
    }
    tmp = tmp_0;
  } else if (a !== a) {
    tmp = b !== b ? 0 : 1;
  } else {
    tmp = -1;
  }
  return tmp;
}
function primitiveCompareTo(a, b) {
  return a < b ? -1 : a > b ? 1 : 0;
}
function compareToDoNotIntrinsicify(a, b) {
  return a.g2(b);
}
function identityHashCode(obj) {
  return getObjectHashCode(obj);
}
function getObjectHashCode(obj) {
  // Inline function 'kotlin.js.jsIn' call
  if (!('kotlinHashCodeValue$' in obj)) {
    var hash = calculateRandomHash();
    var descriptor = new Object();
    descriptor.value = hash;
    descriptor.enumerable = false;
    Object.defineProperty(obj, 'kotlinHashCodeValue$', descriptor);
  }
  // Inline function 'kotlin.js.unsafeCast' call
  return obj['kotlinHashCodeValue$'];
}
function calculateRandomHash() {
  // Inline function 'kotlin.js.jsBitwiseOr' call
  return Math.random() * 4.294967296E9 | 0;
}
function defineProp(obj, name, getter, setter) {
  return Object.defineProperty(obj, name, {configurable: true, get: getter, set: setter});
}
function toString_1(o) {
  var tmp;
  if (o == null) {
    tmp = 'null';
  } else if (isArrayish(o)) {
    tmp = '[...]';
  } else if (!(typeof o.toString === 'function')) {
    tmp = anyToString(o);
  } else {
    // Inline function 'kotlin.js.unsafeCast' call
    tmp = o.toString();
  }
  return tmp;
}
function anyToString(o) {
  return Object.prototype.toString.call(o);
}
function hashCode(obj) {
  if (obj == null)
    return 0;
  var typeOf = typeof obj;
  var tmp;
  switch (typeOf) {
    case 'object':
      tmp = 'function' === typeof obj.hashCode ? obj.hashCode() : getObjectHashCode(obj);
      break;
    case 'function':
      tmp = getObjectHashCode(obj);
      break;
    case 'number':
      tmp = getNumberHashCode(obj);
      break;
    case 'boolean':
      // Inline function 'kotlin.js.unsafeCast' call

      tmp = getBooleanHashCode(obj);
      break;
    case 'string':
      tmp = getStringHashCode(String(obj));
      break;
    case 'bigint':
      tmp = getBigIntHashCode(obj);
      break;
    case 'symbol':
      tmp = getSymbolHashCode(obj);
      break;
    default:
      tmp = function () {
        throw new Error('Unexpected typeof `' + typeOf + '`');
      }();
      break;
  }
  return tmp;
}
function getBooleanHashCode(value) {
  return value ? 1231 : 1237;
}
function getStringHashCode(str) {
  var hash = 0;
  var length = str.length;
  var inductionVariable = 0;
  var last = length - 1 | 0;
  if (inductionVariable <= last)
    do {
      var i = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      // Inline function 'kotlin.js.asDynamic' call
      var code = str.charCodeAt(i);
      hash = imul_0(hash, 31) + code | 0;
    }
     while (!(i === last));
  return hash;
}
function getBigIntHashCode(value) {
  var shiftNumber = BigInt(32);
  var MASK = BigInt(4.294967295E9);
  var bigNumber = value < 0 ? -value : value;
  var hashCode = 0;
  var signum = value < 0 ? -1 : 1;
  while (bigNumber != 0) {
    // Inline function 'kotlin.js.unsafeCast' call
    var chunk = Number(bigNumber & MASK);
    hashCode = imul_0(31, hashCode) + chunk | 0;
    bigNumber = bigNumber >> shiftNumber;
  }
  return imul_0(hashCode, signum);
}
function getSymbolHashCode(value) {
  var hashCodeMap = symbolIsSharable(value) ? getSymbolMap() : getSymbolWeakMap();
  var cachedHashCode = hashCodeMap.get(value);
  if (cachedHashCode !== VOID)
    return cachedHashCode;
  var hash = calculateRandomHash();
  hashCodeMap.set(value, hash);
  return hash;
}
function symbolIsSharable(symbol) {
  return Symbol.keyFor(symbol) != VOID;
}
function getSymbolMap() {
  if (symbolMap === VOID) {
    symbolMap = new Map();
  }
  return symbolMap;
}
function getSymbolWeakMap() {
  if (symbolWeakMap === VOID) {
    symbolWeakMap = new WeakMap();
  }
  return symbolWeakMap;
}
var symbolMap;
var symbolWeakMap;
function equals(obj1, obj2) {
  if (obj1 == null) {
    return obj2 == null;
  }
  if (obj2 == null) {
    return false;
  }
  if (typeof obj1 === 'object' && typeof obj1.equals === 'function') {
    return obj1.equals(obj2);
  }
  if (obj1 !== obj1) {
    return obj2 !== obj2;
  }
  if (typeof obj1 === 'number' && typeof obj2 === 'number') {
    var tmp;
    if (obj1 === obj2) {
      var tmp_0;
      if (obj1 !== 0) {
        tmp_0 = true;
      } else {
        // Inline function 'kotlin.js.asDynamic' call
        var tmp_1 = 1 / obj1;
        // Inline function 'kotlin.js.asDynamic' call
        tmp_0 = tmp_1 === 1 / obj2;
      }
      tmp = tmp_0;
    } else {
      tmp = false;
    }
    return tmp;
  }
  return obj1 === obj2;
}
function boxIntrinsic(x) {
  var message = 'Should be lowered';
  throw IllegalStateException.p3(toString_1(message));
}
function unboxIntrinsic(x) {
  var message = 'Should be lowered';
  throw IllegalStateException.p3(toString_1(message));
}
function captureStack(instance, constructorFunction) {
  if (Error.captureStackTrace != null) {
    Error.captureStackTrace(instance, constructorFunction);
  } else {
    // Inline function 'kotlin.js.asDynamic' call
    instance.stack = (new Error()).stack;
  }
}
function protoOf(constructor) {
  return constructor.prototype;
}
function createThis(ctor, box) {
  var self_0 = Object.create(ctor.prototype);
  boxApply(self_0, box);
  return self_0;
}
function boxApply(self_0, box) {
  if (box !== VOID)
    Object.assign(self_0, box);
}
function setPropertiesToThrowableInstance(this_, message, cause) {
  var errorInfo = calculateErrorInfo(Object.getPrototypeOf(this_));
  if ((errorInfo & 1) === 0) {
    var tmp;
    if (message == null) {
      var tmp_0;
      if (!(message === null)) {
        var tmp1_elvis_lhs = cause == null ? null : cause.toString();
        tmp_0 = tmp1_elvis_lhs == null ? VOID : tmp1_elvis_lhs;
      } else {
        tmp_0 = VOID;
      }
      tmp = tmp_0;
    } else {
      tmp = message;
    }
    this_.message = tmp;
  }
  if ((errorInfo & 2) === 0) {
    this_.cause = cause;
  }
  this_.name = Object.getPrototypeOf(this_).constructor.name;
}
function ensureNotNull(v) {
  var tmp;
  if (v == null) {
    THROW_NPE();
  } else {
    tmp = v;
  }
  return tmp;
}
function THROW_NPE() {
  throw NullPointerException.t3();
}
function noWhenBranchMatchedException() {
  throw NoWhenBranchMatchedException.x3();
}
function THROW_CCE() {
  throw ClassCastException.b4();
}
function get_ZERO() {
  _init_properties_longJs_kt__elc2w5();
  return ZERO;
}
var ZERO;
function get_ONE() {
  _init_properties_longJs_kt__elc2w5();
  return ONE;
}
var ONE;
function get_NEG_ONE() {
  _init_properties_longJs_kt__elc2w5();
  return NEG_ONE;
}
var NEG_ONE;
function get_MAX_VALUE() {
  _init_properties_longJs_kt__elc2w5();
  return MAX_VALUE;
}
var MAX_VALUE;
function get_MIN_VALUE() {
  _init_properties_longJs_kt__elc2w5();
  return MIN_VALUE;
}
var MIN_VALUE;
function get_TWO_PWR_24_() {
  _init_properties_longJs_kt__elc2w5();
  return TWO_PWR_24_;
}
var TWO_PWR_24_;
function compare(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  if (equalsLong(_this__u8e3s4, other)) {
    return 0;
  }
  var thisNeg = isNegative(_this__u8e3s4);
  var otherNeg = isNegative(other);
  return thisNeg && !otherNeg ? -1 : !thisNeg && otherNeg ? 1 : isNegative(subtract(_this__u8e3s4, other)) ? -1 : 1;
}
function add(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  var a48 = _this__u8e3s4.o1_1 >>> 16 | 0;
  var a32 = _this__u8e3s4.o1_1 & 65535;
  var a16 = _this__u8e3s4.n1_1 >>> 16 | 0;
  var a00 = _this__u8e3s4.n1_1 & 65535;
  var b48 = other.o1_1 >>> 16 | 0;
  var b32 = other.o1_1 & 65535;
  var b16 = other.n1_1 >>> 16 | 0;
  var b00 = other.n1_1 & 65535;
  var c48 = 0;
  var c32 = 0;
  var c16 = 0;
  var c00 = 0;
  c00 = c00 + (a00 + b00 | 0) | 0;
  c16 = c16 + (c00 >>> 16 | 0) | 0;
  c00 = c00 & 65535;
  c16 = c16 + (a16 + b16 | 0) | 0;
  c32 = c32 + (c16 >>> 16 | 0) | 0;
  c16 = c16 & 65535;
  c32 = c32 + (a32 + b32 | 0) | 0;
  c48 = c48 + (c32 >>> 16 | 0) | 0;
  c32 = c32 & 65535;
  c48 = c48 + (a48 + b48 | 0) | 0;
  c48 = c48 & 65535;
  return new Long(c16 << 16 | c00, c48 << 16 | c32);
}
function subtract(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  return add(_this__u8e3s4, other.a3());
}
function multiply(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  if (isZero(_this__u8e3s4)) {
    return get_ZERO();
  } else if (isZero(other)) {
    return get_ZERO();
  }
  if (equalsLong(_this__u8e3s4, get_MIN_VALUE())) {
    return isOdd(other) ? get_MIN_VALUE() : get_ZERO();
  } else if (equalsLong(other, get_MIN_VALUE())) {
    return isOdd(_this__u8e3s4) ? get_MIN_VALUE() : get_ZERO();
  }
  if (isNegative(_this__u8e3s4)) {
    var tmp;
    if (isNegative(other)) {
      tmp = multiply(negate(_this__u8e3s4), negate(other));
    } else {
      tmp = negate(multiply(negate(_this__u8e3s4), other));
    }
    return tmp;
  } else if (isNegative(other)) {
    return negate(multiply(_this__u8e3s4, negate(other)));
  }
  if (lessThan(_this__u8e3s4, get_TWO_PWR_24_()) && lessThan(other, get_TWO_PWR_24_())) {
    return fromNumber(toNumber(_this__u8e3s4) * toNumber(other));
  }
  var a48 = _this__u8e3s4.o1_1 >>> 16 | 0;
  var a32 = _this__u8e3s4.o1_1 & 65535;
  var a16 = _this__u8e3s4.n1_1 >>> 16 | 0;
  var a00 = _this__u8e3s4.n1_1 & 65535;
  var b48 = other.o1_1 >>> 16 | 0;
  var b32 = other.o1_1 & 65535;
  var b16 = other.n1_1 >>> 16 | 0;
  var b00 = other.n1_1 & 65535;
  var c48 = 0;
  var c32 = 0;
  var c16 = 0;
  var c00 = 0;
  c00 = c00 + imul_0(a00, b00) | 0;
  c16 = c16 + (c00 >>> 16 | 0) | 0;
  c00 = c00 & 65535;
  c16 = c16 + imul_0(a16, b00) | 0;
  c32 = c32 + (c16 >>> 16 | 0) | 0;
  c16 = c16 & 65535;
  c16 = c16 + imul_0(a00, b16) | 0;
  c32 = c32 + (c16 >>> 16 | 0) | 0;
  c16 = c16 & 65535;
  c32 = c32 + imul_0(a32, b00) | 0;
  c48 = c48 + (c32 >>> 16 | 0) | 0;
  c32 = c32 & 65535;
  c32 = c32 + imul_0(a16, b16) | 0;
  c48 = c48 + (c32 >>> 16 | 0) | 0;
  c32 = c32 & 65535;
  c32 = c32 + imul_0(a00, b32) | 0;
  c48 = c48 + (c32 >>> 16 | 0) | 0;
  c32 = c32 & 65535;
  c48 = c48 + (((imul_0(a48, b00) + imul_0(a32, b16) | 0) + imul_0(a16, b32) | 0) + imul_0(a00, b48) | 0) | 0;
  c48 = c48 & 65535;
  return new Long(c16 << 16 | c00, c48 << 16 | c32);
}
function divide(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  if (isZero(other)) {
    throw Exception.d4('division by zero');
  } else if (isZero(_this__u8e3s4)) {
    return get_ZERO();
  }
  if (equalsLong(_this__u8e3s4, get_MIN_VALUE())) {
    if (equalsLong(other, get_ONE()) || equalsLong(other, get_NEG_ONE())) {
      return get_MIN_VALUE();
    } else if (equalsLong(other, get_MIN_VALUE())) {
      return get_ONE();
    } else {
      var halfThis = shiftRight(_this__u8e3s4, 1);
      var approx = shiftLeft(halfThis.y2(other), 1);
      if (equalsLong(approx, get_ZERO())) {
        return isNegative(other) ? get_ONE() : get_NEG_ONE();
      } else {
        var rem = subtract(_this__u8e3s4, multiply(other, approx));
        return add(approx, rem.y2(other));
      }
    }
  } else if (equalsLong(other, get_MIN_VALUE())) {
    return get_ZERO();
  }
  if (isNegative(_this__u8e3s4)) {
    var tmp;
    if (isNegative(other)) {
      tmp = negate(_this__u8e3s4).y2(negate(other));
    } else {
      tmp = negate(negate(_this__u8e3s4).y2(other));
    }
    return tmp;
  } else if (isNegative(other)) {
    return negate(_this__u8e3s4.y2(negate(other)));
  }
  var res = get_ZERO();
  var rem_0 = _this__u8e3s4;
  while (greaterThanOrEqual(rem_0, other)) {
    var approxDouble = toNumber(rem_0) / toNumber(other);
    var approx2 = Math.max(1.0, Math.floor(approxDouble));
    var log2 = Math.ceil(Math.log(approx2) / Math.LN2);
    var delta = log2 <= 48 ? 1.0 : Math.pow(2.0, log2 - 48);
    var approxRes = fromNumber(approx2);
    var approxRem = multiply(approxRes, other);
    while (isNegative(approxRem) || greaterThan(approxRem, rem_0)) {
      approx2 = approx2 - delta;
      approxRes = fromNumber(approx2);
      approxRem = multiply(approxRes, other);
    }
    if (isZero(approxRes)) {
      approxRes = get_ONE();
    }
    res = add(res, approxRes);
    rem_0 = subtract(rem_0, approxRem);
  }
  return res;
}
function modulo(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  return subtract(_this__u8e3s4, multiply(_this__u8e3s4.y2(other), other));
}
function shiftLeft(_this__u8e3s4, numBits) {
  _init_properties_longJs_kt__elc2w5();
  var numBits_0 = numBits & 63;
  if (numBits_0 === 0) {
    return _this__u8e3s4;
  } else {
    if (numBits_0 < 32) {
      return new Long(_this__u8e3s4.n1_1 << numBits_0, _this__u8e3s4.o1_1 << numBits_0 | (_this__u8e3s4.n1_1 >>> (32 - numBits_0 | 0) | 0));
    } else {
      return new Long(0, _this__u8e3s4.n1_1 << (numBits_0 - 32 | 0));
    }
  }
}
function shiftRight(_this__u8e3s4, numBits) {
  _init_properties_longJs_kt__elc2w5();
  var numBits_0 = numBits & 63;
  if (numBits_0 === 0) {
    return _this__u8e3s4;
  } else {
    if (numBits_0 < 32) {
      return new Long(_this__u8e3s4.n1_1 >>> numBits_0 | 0 | _this__u8e3s4.o1_1 << (32 - numBits_0 | 0), _this__u8e3s4.o1_1 >> numBits_0);
    } else {
      return new Long(_this__u8e3s4.o1_1 >> (numBits_0 - 32 | 0), _this__u8e3s4.o1_1 >= 0 ? 0 : -1);
    }
  }
}
function shiftRightUnsigned(_this__u8e3s4, numBits) {
  _init_properties_longJs_kt__elc2w5();
  var numBits_0 = numBits & 63;
  if (numBits_0 === 0) {
    return _this__u8e3s4;
  } else {
    if (numBits_0 < 32) {
      return new Long(_this__u8e3s4.n1_1 >>> numBits_0 | 0 | _this__u8e3s4.o1_1 << (32 - numBits_0 | 0), _this__u8e3s4.o1_1 >>> numBits_0 | 0);
    } else {
      var tmp;
      if (numBits_0 === 32) {
        tmp = new Long(_this__u8e3s4.o1_1, 0);
      } else {
        tmp = new Long(_this__u8e3s4.o1_1 >>> (numBits_0 - 32 | 0) | 0, 0);
      }
      return tmp;
    }
  }
}
function toNumber(_this__u8e3s4) {
  _init_properties_longJs_kt__elc2w5();
  return _this__u8e3s4.o1_1 * 4.294967296E9 + getLowBitsUnsigned(_this__u8e3s4);
}
function toStringImpl(_this__u8e3s4, radix) {
  _init_properties_longJs_kt__elc2w5();
  if (radix < 2 || 36 < radix) {
    throw Exception.d4('radix out of range: ' + radix);
  }
  if (isZero(_this__u8e3s4)) {
    return '0';
  }
  if (isNegative(_this__u8e3s4)) {
    if (equalsLong(_this__u8e3s4, get_MIN_VALUE())) {
      var radixLong = fromInt(radix);
      var div = _this__u8e3s4.y2(radixLong);
      var rem = subtract(multiply(div, radixLong), _this__u8e3s4).h3();
      var tmp = toStringImpl(div, radix);
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      return tmp + rem.toString(radix);
    } else {
      return '-' + toStringImpl(negate(_this__u8e3s4), radix);
    }
  }
  var digitsPerTime = radix === 2 ? 31 : radix <= 10 ? 9 : radix <= 21 ? 7 : radix <= 35 ? 6 : 5;
  var radixToPower = fromNumber(Math.pow(radix, digitsPerTime));
  var rem_0 = _this__u8e3s4;
  var result = '';
  while (true) {
    var remDiv = rem_0.y2(radixToPower);
    var intval = subtract(rem_0, multiply(remDiv, radixToPower)).h3();
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    var digits = intval.toString(radix);
    rem_0 = remDiv;
    if (isZero(rem_0)) {
      return digits + result;
    } else {
      while (digits.length < digitsPerTime) {
        digits = '0' + digits;
      }
      result = digits + result;
    }
  }
}
function equalsLong(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  return _this__u8e3s4.o1_1 === other.o1_1 && _this__u8e3s4.n1_1 === other.n1_1;
}
function hashCode_0(l) {
  _init_properties_longJs_kt__elc2w5();
  return l.n1_1 ^ l.o1_1;
}
function fromInt(value) {
  _init_properties_longJs_kt__elc2w5();
  return new Long(value, value < 0 ? -1 : 0);
}
function isNegative(_this__u8e3s4) {
  _init_properties_longJs_kt__elc2w5();
  return _this__u8e3s4.o1_1 < 0;
}
function isZero(_this__u8e3s4) {
  _init_properties_longJs_kt__elc2w5();
  return _this__u8e3s4.o1_1 === 0 && _this__u8e3s4.n1_1 === 0;
}
function isOdd(_this__u8e3s4) {
  _init_properties_longJs_kt__elc2w5();
  return (_this__u8e3s4.n1_1 & 1) === 1;
}
function negate(_this__u8e3s4) {
  _init_properties_longJs_kt__elc2w5();
  return _this__u8e3s4.a3();
}
function lessThan(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  return compare(_this__u8e3s4, other) < 0;
}
function fromNumber(value) {
  _init_properties_longJs_kt__elc2w5();
  if (isNaN_0(value)) {
    return get_ZERO();
  } else if (value <= -9.223372036854776E18) {
    return get_MIN_VALUE();
  } else if (value + 1 >= 9.223372036854776E18) {
    return get_MAX_VALUE();
  } else if (value < 0) {
    return negate(fromNumber(-value));
  } else {
    var twoPwr32 = 4.294967296E9;
    // Inline function 'kotlin.js.jsBitwiseOr' call
    var tmp = value % twoPwr32 | 0;
    // Inline function 'kotlin.js.jsBitwiseOr' call
    var tmp$ret$1 = value / twoPwr32 | 0;
    return new Long(tmp, tmp$ret$1);
  }
}
function greaterThan(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  return compare(_this__u8e3s4, other) > 0;
}
function greaterThanOrEqual(_this__u8e3s4, other) {
  _init_properties_longJs_kt__elc2w5();
  return compare(_this__u8e3s4, other) >= 0;
}
function getLowBitsUnsigned(_this__u8e3s4) {
  _init_properties_longJs_kt__elc2w5();
  return _this__u8e3s4.n1_1 >= 0 ? _this__u8e3s4.n1_1 : 4.294967296E9 + _this__u8e3s4.n1_1;
}
var properties_initialized_longJs_kt_4syf89;
function _init_properties_longJs_kt__elc2w5() {
  if (!properties_initialized_longJs_kt_4syf89) {
    properties_initialized_longJs_kt_4syf89 = true;
    ZERO = fromInt(0);
    ONE = fromInt(1);
    NEG_ONE = fromInt(-1);
    MAX_VALUE = new Long(-1, 2147483647);
    MIN_VALUE = new Long(0, -2147483648);
    TWO_PWR_24_ = fromInt(16777216);
  }
}
function createMetadata(kind, name, defaultConstructor, associatedObjectKey, associatedObjects, suspendArity) {
  var undef = VOID;
  var iid = kind === 'interface' ? generateInterfaceId() : VOID;
  return {kind: kind, simpleName: name, associatedObjectKey: associatedObjectKey, associatedObjects: associatedObjects, suspendArity: suspendArity, $kClass$: undef, defaultConstructor: defaultConstructor, iid: iid};
}
function generateInterfaceId() {
  if (globalInterfaceId === VOID) {
    globalInterfaceId = 0;
  }
  // Inline function 'kotlin.js.unsafeCast' call
  globalInterfaceId = globalInterfaceId + 1 | 0;
  // Inline function 'kotlin.js.unsafeCast' call
  return globalInterfaceId;
}
var globalInterfaceId;
function initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
  if (!(parent == null)) {
    ctor.prototype = Object.create(parent.prototype);
    ctor.prototype.constructor = ctor;
  }
  var metadata = createMetadata(kind, name, defaultConstructor, associatedObjectKey, associatedObjects, suspendArity);
  ctor.$metadata$ = metadata;
  if (!(interfaces == null)) {
    var receiver = !equals(metadata.iid, VOID) ? ctor : ctor.prototype;
    receiver.$imask$ = implement(interfaces);
  }
}
function initMetadataForClass(ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
  var kind = 'class';
  initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects);
}
function initMetadataForObject(ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
  var kind = 'object';
  initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects);
}
function initMetadataForInterface(ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
  var kind = 'interface';
  initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects);
}
function initMetadataForLambda(ctor, parent, interfaces, suspendArity) {
  initMetadataForClass(ctor, 'Lambda', VOID, parent, interfaces, suspendArity, VOID, VOID);
}
function initMetadataForCoroutine(ctor, parent, interfaces, suspendArity) {
  initMetadataForClass(ctor, 'Coroutine', VOID, parent, interfaces, suspendArity, VOID, VOID);
}
function initMetadataForFunctionReference(ctor, parent, interfaces, suspendArity) {
  initMetadataForClass(ctor, 'FunctionReference', VOID, parent, interfaces, suspendArity, VOID, VOID);
}
function initMetadataForCompanion(ctor, parent, interfaces, suspendArity) {
  initMetadataForObject(ctor, 'Companion', VOID, parent, interfaces, suspendArity, VOID, VOID);
}
function numberToInt(a) {
  var tmp;
  if (a instanceof Long) {
    tmp = a.h3();
  } else {
    tmp = doubleToInt(a);
  }
  return tmp;
}
function doubleToInt(a) {
  var tmp;
  if (a > 2147483647) {
    tmp = 2147483647;
  } else if (a < -2147483648) {
    tmp = -2147483648;
  } else {
    // Inline function 'kotlin.js.jsBitwiseOr' call
    tmp = a | 0;
  }
  return tmp;
}
function toShort(a) {
  // Inline function 'kotlin.js.unsafeCast' call
  return a << 16 >> 16;
}
function numberToLong(a) {
  var tmp;
  if (a instanceof Long) {
    tmp = a;
  } else {
    tmp = fromNumber(a);
  }
  return tmp;
}
function numberToChar(a) {
  // Inline function 'kotlin.toUShort' call
  var this_0 = numberToInt(a);
  var tmp$ret$0 = _UShort___init__impl__jigrne(toShort(this_0));
  return _Char___init__impl__6a9atx_0(tmp$ret$0);
}
function toLong(a) {
  return fromInt(a);
}
function numberRangeToNumber(start, endInclusive) {
  return new IntRange(start, endInclusive);
}
function isArrayish(o) {
  return isJsArray(o) || isView(o);
}
function isJsArray(obj) {
  // Inline function 'kotlin.js.unsafeCast' call
  return Array.isArray(obj);
}
function isInterface(obj, iface) {
  return isInterfaceImpl(obj, iface.$metadata$.iid);
}
function isInterfaceImpl(obj, iface) {
  // Inline function 'kotlin.js.unsafeCast' call
  var tmp0_elvis_lhs = obj.$imask$;
  var tmp;
  if (tmp0_elvis_lhs == null) {
    return false;
  } else {
    tmp = tmp0_elvis_lhs;
  }
  var mask = tmp;
  return isBitSet(mask, iface);
}
function isCharSequence(value) {
  return typeof value === 'string' || isInterface(value, CharSequence);
}
function calculateErrorInfo(proto) {
  var tmp0_safe_receiver = proto.constructor;
  var metadata = tmp0_safe_receiver == null ? null : tmp0_safe_receiver.$metadata$;
  var tmp2_safe_receiver = metadata == null ? null : metadata.errorInfo;
  if (tmp2_safe_receiver == null)
    null;
  else {
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    return tmp2_safe_receiver;
  }
  var result = 0;
  if (hasProp(proto, 'message'))
    result = result | 1;
  if (hasProp(proto, 'cause'))
    result = result | 2;
  if (!(result === 3)) {
    var parentProto = getPrototypeOf(proto);
    if (parentProto != Error.prototype) {
      result = result | calculateErrorInfo(parentProto);
    }
  }
  if (!(metadata == null)) {
    metadata.errorInfo = result;
  }
  return result;
}
function hasProp(proto, propName) {
  return proto.hasOwnProperty(propName);
}
function getPrototypeOf(obj) {
  return Object.getPrototypeOf(obj);
}
function get_VOID() {
  _init_properties_void_kt__3zg9as();
  return VOID;
}
var VOID;
var properties_initialized_void_kt_e4ret2;
function _init_properties_void_kt__3zg9as() {
  if (!properties_initialized_void_kt_e4ret2) {
    properties_initialized_void_kt_e4ret2 = true;
    VOID = void 0;
  }
}
function asList(_this__u8e3s4) {
  // Inline function 'kotlin.js.unsafeCast' call
  // Inline function 'kotlin.js.asDynamic' call
  return ArrayList.e4(_this__u8e3s4);
}
function copyOf(_this__u8e3s4, newSize) {
  // Inline function 'kotlin.require' call
  // Inline function 'kotlin.contracts.contract' call
  if (!(newSize >= 0)) {
    // Inline function 'kotlin.collections.copyOf.<anonymous>' call
    var message = 'Invalid new array size: ' + newSize + '.';
    throw IllegalArgumentException.p(toString_1(message));
  }
  return fillFrom(_this__u8e3s4, new Int32Array(newSize));
}
function copyOf_0(_this__u8e3s4, newSize) {
  // Inline function 'kotlin.require' call
  // Inline function 'kotlin.contracts.contract' call
  if (!(newSize >= 0)) {
    // Inline function 'kotlin.collections.copyOf.<anonymous>' call
    var message = 'Invalid new array size: ' + newSize + '.';
    throw IllegalArgumentException.p(toString_1(message));
  }
  return arrayCopyResize(_this__u8e3s4, newSize, null);
}
function decodeVarLenBase64(base64, fromBase64, resultLength) {
  var result = new Int32Array(resultLength);
  var index = 0;
  var int = 0;
  var shift = 0;
  var inductionVariable = 0;
  var last = base64.length;
  while (inductionVariable < last) {
    var char = charSequenceGet(base64, inductionVariable);
    inductionVariable = inductionVariable + 1 | 0;
    // Inline function 'kotlin.code' call
    var sixBit = fromBase64[Char__toInt_impl_vasixd(char)];
    int = int | (sixBit & 31) << shift;
    if (sixBit < 32) {
      var tmp1 = index;
      index = tmp1 + 1 | 0;
      result[tmp1] = int;
      int = 0;
      shift = 0;
    } else {
      shift = shift + 5 | 0;
    }
  }
  return result;
}
function isDigitImpl(_this__u8e3s4) {
  return digitToIntImpl(_this__u8e3s4) >= 0;
}
function digitToIntImpl(_this__u8e3s4) {
  // Inline function 'kotlin.code' call
  var ch = Char__toInt_impl_vasixd(_this__u8e3s4);
  var index = binarySearchRange(Digit_getInstance().f4_1, ch);
  var diff = ch - Digit_getInstance().f4_1[index] | 0;
  return diff < 10 ? diff : -1;
}
function binarySearchRange(array, needle) {
  var bottom = 0;
  var top = array.length - 1 | 0;
  var middle = -1;
  var value = 0;
  while (bottom <= top) {
    middle = (bottom + top | 0) / 2 | 0;
    value = array[middle];
    if (needle > value)
      bottom = middle + 1 | 0;
    else if (needle === value)
      return middle;
    else
      top = middle - 1 | 0;
  }
  return middle - (needle < value ? 1 : 0) | 0;
}
var Digit_instance;
function Digit_getInstance() {
  if (Digit_instance === VOID)
    new Digit();
  return Digit_instance;
}
function isLetterImpl(_this__u8e3s4) {
  return !(getLetterType(_this__u8e3s4) === 0);
}
function isLowerCaseImpl(_this__u8e3s4) {
  var tmp;
  if (getLetterType(_this__u8e3s4) === 1) {
    tmp = true;
  } else {
    // Inline function 'kotlin.code' call
    var tmp$ret$0 = Char__toInt_impl_vasixd(_this__u8e3s4);
    tmp = isOtherLowercase(tmp$ret$0);
  }
  return tmp;
}
function isUpperCaseImpl(_this__u8e3s4) {
  var tmp;
  if (getLetterType(_this__u8e3s4) === 2) {
    tmp = true;
  } else {
    // Inline function 'kotlin.code' call
    var tmp$ret$0 = Char__toInt_impl_vasixd(_this__u8e3s4);
    tmp = isOtherUppercase(tmp$ret$0);
  }
  return tmp;
}
function getLetterType(_this__u8e3s4) {
  // Inline function 'kotlin.code' call
  var ch = Char__toInt_impl_vasixd(_this__u8e3s4);
  var index = binarySearchRange(Letter_getInstance().g4_1, ch);
  var rangeStart = Letter_getInstance().g4_1[index];
  var rangeEnd = (rangeStart + Letter_getInstance().h4_1[index] | 0) - 1 | 0;
  var code = Letter_getInstance().i4_1[index];
  if (ch > rangeEnd) {
    return 0;
  }
  var lastTwoBits = code & 3;
  if (lastTwoBits === 0) {
    var shift = 2;
    var threshold = rangeStart;
    var inductionVariable = 0;
    if (inductionVariable <= 1)
      do {
        var i = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        threshold = threshold + (code >> shift & 127) | 0;
        if (threshold > ch) {
          return 3;
        }
        shift = shift + 7 | 0;
        threshold = threshold + (code >> shift & 127) | 0;
        if (threshold > ch) {
          return 0;
        }
        shift = shift + 7 | 0;
      }
       while (inductionVariable <= 1);
    return 3;
  }
  if (code <= 7) {
    return lastTwoBits;
  }
  var distance = ch - rangeStart | 0;
  var shift_0 = code <= 31 ? distance % 2 | 0 : distance;
  return code >> imul_0(2, shift_0) & 3;
}
var Letter_instance;
function Letter_getInstance() {
  if (Letter_instance === VOID)
    new Letter();
  return Letter_instance;
}
function isOtherLowercase(_this__u8e3s4) {
  var index = binarySearchRange(OtherLowercase_getInstance().j4_1, _this__u8e3s4);
  return index >= 0 && _this__u8e3s4 < (OtherLowercase_getInstance().j4_1[index] + OtherLowercase_getInstance().k4_1[index] | 0);
}
var OtherLowercase_instance;
function OtherLowercase_getInstance() {
  if (OtherLowercase_instance === VOID)
    new OtherLowercase();
  return OtherLowercase_instance;
}
function isOtherUppercase(_this__u8e3s4) {
  return (8544 <= _this__u8e3s4 ? _this__u8e3s4 <= 8559 : false) || (9398 <= _this__u8e3s4 ? _this__u8e3s4 <= 9423 : false);
}
function isWhitespaceImpl(_this__u8e3s4) {
  // Inline function 'kotlin.code' call
  var ch = Char__toInt_impl_vasixd(_this__u8e3s4);
  return (9 <= ch ? ch <= 13 : false) || (28 <= ch ? ch <= 32 : false) || ch === 160 || (ch > 4096 && (ch === 5760 || (8192 <= ch ? ch <= 8202 : false) || ch === 8232 || ch === 8233 || ch === 8239 || ch === 8287 || ch === 12288));
}
function isNaN_0(_this__u8e3s4) {
  return !(_this__u8e3s4 === _this__u8e3s4);
}
function takeHighestOneBit(_this__u8e3s4) {
  var tmp;
  if (_this__u8e3s4 === 0) {
    tmp = 0;
  } else {
    // Inline function 'kotlin.countLeadingZeroBits' call
    tmp = 1 << (31 - clz32(_this__u8e3s4) | 0);
  }
  return tmp;
}
function collectionToArray(collection) {
  return collectionToArrayCommonImpl(collection);
}
function listOf(element) {
  return arrayListOf([element]);
}
function setOf(element) {
  return hashSetOf([element]);
}
function mapCapacity(expectedSize) {
  return expectedSize;
}
function checkCountOverflow(count) {
  if (count < 0) {
    throwCountOverflow();
  }
  return count;
}
function checkIndexOverflow(index) {
  if (index < 0) {
    throwIndexOverflow();
  }
  return index;
}
function copyToArray(collection) {
  var tmp;
  // Inline function 'kotlin.js.asDynamic' call
  if (collection.toArray !== undefined) {
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp = collection.toArray();
  } else {
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp = collectionToArray(collection);
  }
  return tmp;
}
function arrayOfUninitializedElements(capacity) {
  // Inline function 'kotlin.require' call
  // Inline function 'kotlin.contracts.contract' call
  if (!(capacity >= 0)) {
    // Inline function 'kotlin.collections.arrayOfUninitializedElements.<anonymous>' call
    var message = 'capacity must be non-negative.';
    throw IllegalArgumentException.p(toString_1(message));
  }
  // Inline function 'kotlin.js.unsafeCast' call
  // Inline function 'kotlin.arrayOfNulls' call
  // Inline function 'kotlin.js.asDynamic' call
  return fillArrayVal(Array(capacity), null);
}
function resetRange(_this__u8e3s4, fromIndex, toIndex) {
  // Inline function 'kotlin.js.nativeFill' call
  // Inline function 'kotlin.js.asDynamic' call
  _this__u8e3s4.fill(null, fromIndex, toIndex);
}
function copyOfUninitializedElements(_this__u8e3s4, newSize) {
  // Inline function 'kotlin.js.unsafeCast' call
  // Inline function 'kotlin.js.asDynamic' call
  return copyOf_0(_this__u8e3s4, newSize);
}
var Companion_instance_2;
function Companion_getInstance_2() {
  if (Companion_instance_2 === VOID)
    new Companion_2();
  return Companion_instance_2;
}
function increaseLength($this, amount) {
  var previous = $this.y();
  // Inline function 'kotlin.js.asDynamic' call
  $this.v_1.length = $this.y() + amount | 0;
  return previous;
}
function rangeCheck($this, index) {
  // Inline function 'kotlin.apply' call
  // Inline function 'kotlin.contracts.contract' call
  // Inline function 'kotlin.collections.ArrayList.rangeCheck.<anonymous>' call
  Companion_instance_5.s5(index, $this.y());
  return index;
}
function insertionRangeCheck($this, index) {
  // Inline function 'kotlin.apply' call
  // Inline function 'kotlin.contracts.contract' call
  // Inline function 'kotlin.collections.ArrayList.insertionRangeCheck.<anonymous>' call
  Companion_instance_5.x4(index, $this.y());
  return index;
}
function init_kotlin_collections_HashMap(_this__u8e3s4) {
  _this__u8e3s4.e6_1 = null;
}
function init_kotlin_collections_HashSet(_this__u8e3s4) {
}
function computeHashSize($this, capacity) {
  return takeHighestOneBit(imul_0(coerceAtLeast_0(capacity, 1), 3));
}
function computeShift($this, hashSize) {
  // Inline function 'kotlin.countLeadingZeroBits' call
  return clz32(hashSize) + 1 | 0;
}
function _get_capacity__a9k9f3($this) {
  return $this.h6_1.length;
}
function _get_hashSize__tftcho($this) {
  return $this.k6_1.length;
}
function registerModification($this) {
  $this.o6_1 = $this.o6_1 + 1 | 0;
}
function ensureExtraCapacity($this, n) {
  if (shouldCompact($this, n)) {
    compact($this, true);
  } else {
    ensureCapacity($this, $this.m6_1 + n | 0);
  }
}
function shouldCompact($this, extraCapacity) {
  var spareCapacity = _get_capacity__a9k9f3($this) - $this.m6_1 | 0;
  var gaps = $this.m6_1 - $this.y() | 0;
  return spareCapacity < extraCapacity && (gaps + spareCapacity | 0) >= extraCapacity && gaps >= (_get_capacity__a9k9f3($this) / 4 | 0);
}
function ensureCapacity($this, minCapacity) {
  if (minCapacity < 0)
    throw RuntimeException.m7('too many elements');
  if (minCapacity > _get_capacity__a9k9f3($this)) {
    var newSize = Companion_instance_5.n7(_get_capacity__a9k9f3($this), minCapacity);
    $this.h6_1 = copyOfUninitializedElements($this.h6_1, newSize);
    var tmp = $this;
    var tmp0_safe_receiver = $this.i6_1;
    tmp.i6_1 = tmp0_safe_receiver == null ? null : copyOfUninitializedElements(tmp0_safe_receiver, newSize);
    $this.j6_1 = copyOf($this.j6_1, newSize);
    var newHashSize = computeHashSize(Companion_instance_3, newSize);
    if (newHashSize > _get_hashSize__tftcho($this)) {
      rehash($this, newHashSize);
    }
  }
}
function allocateValuesArray($this) {
  var curValuesArray = $this.i6_1;
  if (!(curValuesArray == null))
    return curValuesArray;
  var newValuesArray = arrayOfUninitializedElements(_get_capacity__a9k9f3($this));
  $this.i6_1 = newValuesArray;
  return newValuesArray;
}
function hash($this, key) {
  return key == null ? 0 : imul_0(hashCode(key), -1640531527) >>> $this.n6_1 | 0;
}
function compact($this, updateHashArray) {
  var i = 0;
  var j = 0;
  var valuesArray = $this.i6_1;
  while (i < $this.m6_1) {
    var hash = $this.j6_1[i];
    if (hash >= 0) {
      $this.h6_1[j] = $this.h6_1[i];
      if (!(valuesArray == null)) {
        valuesArray[j] = valuesArray[i];
      }
      if (updateHashArray) {
        $this.j6_1[j] = hash;
        $this.k6_1[hash] = j + 1 | 0;
      }
      j = j + 1 | 0;
    }
    i = i + 1 | 0;
  }
  resetRange($this.h6_1, j, $this.m6_1);
  if (valuesArray == null)
    null;
  else {
    resetRange(valuesArray, j, $this.m6_1);
  }
  $this.m6_1 = j;
}
function rehash($this, newHashSize) {
  registerModification($this);
  if ($this.m6_1 > $this.p6_1) {
    compact($this, false);
  }
  $this.k6_1 = new Int32Array(newHashSize);
  $this.n6_1 = computeShift(Companion_instance_3, newHashSize);
  var i = 0;
  while (i < $this.m6_1) {
    var tmp0 = i;
    i = tmp0 + 1 | 0;
    if (!putRehash($this, tmp0)) {
      throw IllegalStateException.p3('This cannot happen with fixed magic multiplier and grow-only hash array. Have object hashCodes changed?');
    }
  }
}
function putRehash($this, i) {
  var hash_0 = hash($this, $this.h6_1[i]);
  var probesLeft = $this.l6_1;
  while (true) {
    var index = $this.k6_1[hash_0];
    if (index === 0) {
      $this.k6_1[hash_0] = i + 1 | 0;
      $this.j6_1[i] = hash_0;
      return true;
    }
    probesLeft = probesLeft - 1 | 0;
    if (probesLeft < 0)
      return false;
    var tmp0 = hash_0;
    hash_0 = tmp0 - 1 | 0;
    if (tmp0 === 0)
      hash_0 = _get_hashSize__tftcho($this) - 1 | 0;
  }
}
function findKey($this, key) {
  var hash_0 = hash($this, key);
  var probesLeft = $this.l6_1;
  while (true) {
    var index = $this.k6_1[hash_0];
    if (index === 0)
      return -1;
    if (index > 0 && equals($this.h6_1[index - 1 | 0], key))
      return index - 1 | 0;
    probesLeft = probesLeft - 1 | 0;
    if (probesLeft < 0)
      return -1;
    var tmp0 = hash_0;
    hash_0 = tmp0 - 1 | 0;
    if (tmp0 === 0)
      hash_0 = _get_hashSize__tftcho($this) - 1 | 0;
  }
}
function addKey($this, key) {
  $this.c7();
  retry: while (true) {
    var hash_0 = hash($this, key);
    var tentativeMaxProbeDistance = coerceAtMost_0(imul_0($this.l6_1, 2), _get_hashSize__tftcho($this) / 2 | 0);
    var probeDistance = 0;
    while (true) {
      var index = $this.k6_1[hash_0];
      if (index <= 0) {
        if ($this.m6_1 >= _get_capacity__a9k9f3($this)) {
          ensureExtraCapacity($this, 1);
          continue retry;
        }
        var tmp1 = $this.m6_1;
        $this.m6_1 = tmp1 + 1 | 0;
        var putIndex = tmp1;
        $this.h6_1[putIndex] = key;
        $this.j6_1[putIndex] = hash_0;
        $this.k6_1[hash_0] = putIndex + 1 | 0;
        $this.p6_1 = $this.p6_1 + 1 | 0;
        registerModification($this);
        if (probeDistance > $this.l6_1)
          $this.l6_1 = probeDistance;
        return putIndex;
      }
      if (equals($this.h6_1[index - 1 | 0], key)) {
        return -index | 0;
      }
      probeDistance = probeDistance + 1 | 0;
      if (probeDistance > tentativeMaxProbeDistance) {
        rehash($this, imul_0(_get_hashSize__tftcho($this), 2));
        continue retry;
      }
      var tmp4 = hash_0;
      hash_0 = tmp4 - 1 | 0;
      if (tmp4 === 0)
        hash_0 = _get_hashSize__tftcho($this) - 1 | 0;
    }
  }
}
function contentEquals($this, other) {
  return $this.p6_1 === other.y() && $this.b7(other.n2());
}
var Companion_instance_3;
function Companion_getInstance_3() {
  return Companion_instance_3;
}
var EmptyHolder_instance;
function EmptyHolder_getInstance() {
  if (EmptyHolder_instance === VOID)
    new EmptyHolder();
  return EmptyHolder_instance;
}
function init_kotlin_collections_LinkedHashMap(_this__u8e3s4) {
}
function init_kotlin_collections_LinkedHashSet(_this__u8e3s4) {
}
function init_kotlin_Exception(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.c4_1);
}
function init_kotlin_IllegalArgumentException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.o_1);
}
function init_kotlin_IndexOutOfBoundsException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.k1_1);
}
function init_kotlin_IllegalStateException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.o3_1);
}
function init_kotlin_UnsupportedOperationException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.x5_1);
}
function init_kotlin_RuntimeException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.l7_1);
}
function init_kotlin_NoSuchElementException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.k_1);
}
function init_kotlin_NumberFormatException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.q9_1);
}
function init_kotlin_ConcurrentModificationException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.a8_1);
}
function init_kotlin_ArithmeticException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.v9_1);
}
function init_kotlin_NullPointerException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.s3_1);
}
function init_kotlin_NoWhenBranchMatchedException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.w3_1);
}
function init_kotlin_ClassCastException(_this__u8e3s4) {
  captureStack(_this__u8e3s4, _this__u8e3s4.a4_1);
}
function fillFrom(src, dst) {
  var srcLen = src.length;
  var dstLen = dst.length;
  var index = 0;
  // Inline function 'kotlin.js.unsafeCast' call
  var arr = dst;
  while (index < srcLen && index < dstLen) {
    var tmp = index;
    var tmp0 = index;
    index = tmp0 + 1 | 0;
    arr[tmp] = src[tmp0];
  }
  return dst;
}
function arrayCopyResize(source, newSize, defaultValue) {
  // Inline function 'kotlin.js.unsafeCast' call
  var result = source.slice(0, newSize);
  // Inline function 'kotlin.copyArrayType' call
  if (source.$type$ !== undefined) {
    result.$type$ = source.$type$;
  }
  var index = source.length;
  if (newSize > index) {
    // Inline function 'kotlin.js.asDynamic' call
    result.length = newSize;
    while (index < newSize) {
      var tmp0 = index;
      index = tmp0 + 1 | 0;
      result[tmp0] = defaultValue;
    }
  }
  return result;
}
var INV_2_26;
var INV_2_53;
function defaultPlatformRandom() {
  _init_properties_PlatformRandom_kt__6kjv62();
  // Inline function 'kotlin.js.unsafeCast' call
  var tmp$ret$0 = Math.random() * Math.pow(2, 32) | 0;
  return Random_0(tmp$ret$0);
}
var properties_initialized_PlatformRandom_kt_uibhw8;
function _init_properties_PlatformRandom_kt__6kjv62() {
  if (!properties_initialized_PlatformRandom_kt_uibhw8) {
    properties_initialized_PlatformRandom_kt_uibhw8 = true;
    // Inline function 'kotlin.math.pow' call
    INV_2_26 = Math.pow(2.0, -26);
    // Inline function 'kotlin.math.pow' call
    INV_2_53 = Math.pow(2.0, -53);
  }
}
function uppercaseChar(_this__u8e3s4) {
  // Inline function 'kotlin.text.uppercase' call
  // Inline function 'kotlin.js.unsafeCast' call
  // Inline function 'kotlin.js.asDynamic' call
  var uppercase = toString(_this__u8e3s4).toUpperCase();
  return uppercase.length > 1 ? _this__u8e3s4 : charSequenceGet(uppercase, 0);
}
function isLowSurrogate(_this__u8e3s4) {
  return _Char___init__impl__6a9atx(56320) <= _this__u8e3s4 ? _this__u8e3s4 <= _Char___init__impl__6a9atx(57343) : false;
}
function isHighSurrogate(_this__u8e3s4) {
  return _Char___init__impl__6a9atx(55296) <= _this__u8e3s4 ? _this__u8e3s4 <= _Char___init__impl__6a9atx(56319) : false;
}
function isDigit(_this__u8e3s4) {
  if (_Char___init__impl__6a9atx(48) <= _this__u8e3s4 ? _this__u8e3s4 <= _Char___init__impl__6a9atx(57) : false) {
    return true;
  }
  if (Char__compareTo_impl_ypi4mb(_this__u8e3s4, _Char___init__impl__6a9atx(128)) < 0) {
    return false;
  }
  return isDigitImpl(_this__u8e3s4);
}
function isLetter(_this__u8e3s4) {
  if ((_Char___init__impl__6a9atx(97) <= _this__u8e3s4 ? _this__u8e3s4 <= _Char___init__impl__6a9atx(122) : false) || (_Char___init__impl__6a9atx(65) <= _this__u8e3s4 ? _this__u8e3s4 <= _Char___init__impl__6a9atx(90) : false)) {
    return true;
  }
  if (Char__compareTo_impl_ypi4mb(_this__u8e3s4, _Char___init__impl__6a9atx(128)) < 0) {
    return false;
  }
  return isLetterImpl(_this__u8e3s4);
}
function isLowerCase(_this__u8e3s4) {
  if (_Char___init__impl__6a9atx(97) <= _this__u8e3s4 ? _this__u8e3s4 <= _Char___init__impl__6a9atx(122) : false) {
    return true;
  }
  if (Char__compareTo_impl_ypi4mb(_this__u8e3s4, _Char___init__impl__6a9atx(128)) < 0) {
    return false;
  }
  return isLowerCaseImpl(_this__u8e3s4);
}
function isUpperCase(_this__u8e3s4) {
  if (_Char___init__impl__6a9atx(65) <= _this__u8e3s4 ? _this__u8e3s4 <= _Char___init__impl__6a9atx(90) : false) {
    return true;
  }
  if (Char__compareTo_impl_ypi4mb(_this__u8e3s4, _Char___init__impl__6a9atx(128)) < 0) {
    return false;
  }
  return isUpperCaseImpl(_this__u8e3s4);
}
function isWhitespace(_this__u8e3s4) {
  return isWhitespaceImpl(_this__u8e3s4);
}
function toString_2(_this__u8e3s4, radix) {
  // Inline function 'kotlin.js.asDynamic' call
  return _this__u8e3s4.toString(checkRadix(radix));
}
function checkRadix(radix) {
  if (!(2 <= radix ? radix <= 36 : false)) {
    throw IllegalArgumentException.p('radix ' + radix + ' was not in valid range 2..36');
  }
  return radix;
}
function toInt(_this__u8e3s4) {
  var tmp0_elvis_lhs = toIntOrNull(_this__u8e3s4);
  var tmp;
  if (tmp0_elvis_lhs == null) {
    numberFormatError(_this__u8e3s4);
  } else {
    tmp = tmp0_elvis_lhs;
  }
  return tmp;
}
function toLong_0(_this__u8e3s4) {
  var tmp0_elvis_lhs = toLongOrNull(_this__u8e3s4);
  var tmp;
  if (tmp0_elvis_lhs == null) {
    numberFormatError(_this__u8e3s4);
  } else {
    tmp = tmp0_elvis_lhs;
  }
  return tmp;
}
function digitOf(char, radix) {
  // Inline function 'kotlin.let' call
  // Inline function 'kotlin.contracts.contract' call
  // Inline function 'kotlin.text.digitOf.<anonymous>' call
  var it = Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(48)) >= 0 && Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(57)) <= 0 ? Char__minus_impl_a2frrh(char, _Char___init__impl__6a9atx(48)) : Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(65)) >= 0 && Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(90)) <= 0 ? Char__minus_impl_a2frrh(char, _Char___init__impl__6a9atx(65)) + 10 | 0 : Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(97)) >= 0 && Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(122)) <= 0 ? Char__minus_impl_a2frrh(char, _Char___init__impl__6a9atx(97)) + 10 | 0 : Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(128)) < 0 ? -1 : Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(65313)) >= 0 && Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(65338)) <= 0 ? Char__minus_impl_a2frrh(char, _Char___init__impl__6a9atx(65313)) + 10 | 0 : Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(65345)) >= 0 && Char__compareTo_impl_ypi4mb(char, _Char___init__impl__6a9atx(65370)) <= 0 ? Char__minus_impl_a2frrh(char, _Char___init__impl__6a9atx(65345)) + 10 | 0 : digitToIntImpl(char);
  return it >= radix ? -1 : it;
}
var Companion_instance_4;
function Companion_getInstance_4() {
  if (Companion_instance_4 === VOID)
    new Companion_4();
  return Companion_instance_4;
}
var STRING_CASE_INSENSITIVE_ORDER;
function compareTo_0(_this__u8e3s4, other, ignoreCase) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  _init_properties_stringJs_kt__bg7zye();
  if (ignoreCase) {
    var n1 = _this__u8e3s4.length;
    var n2 = other.length;
    // Inline function 'kotlin.comparisons.minOf' call
    var min = Math.min(n1, n2);
    if (min === 0)
      return n1 - n2 | 0;
    var inductionVariable = 0;
    if (inductionVariable < min)
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        var thisChar = charSequenceGet(_this__u8e3s4, index);
        var otherChar = charSequenceGet(other, index);
        if (!(thisChar === otherChar)) {
          thisChar = uppercaseChar(thisChar);
          otherChar = uppercaseChar(otherChar);
          if (!(thisChar === otherChar)) {
            // Inline function 'kotlin.text.lowercaseChar' call
            // Inline function 'kotlin.text.lowercase' call
            var this_0 = thisChar;
            // Inline function 'kotlin.js.unsafeCast' call
            // Inline function 'kotlin.js.asDynamic' call
            var tmp$ret$3 = toString(this_0).toLowerCase();
            thisChar = charSequenceGet(tmp$ret$3, 0);
            // Inline function 'kotlin.text.lowercaseChar' call
            // Inline function 'kotlin.text.lowercase' call
            var this_1 = otherChar;
            // Inline function 'kotlin.js.unsafeCast' call
            // Inline function 'kotlin.js.asDynamic' call
            var tmp$ret$7 = toString(this_1).toLowerCase();
            otherChar = charSequenceGet(tmp$ret$7, 0);
            if (!(thisChar === otherChar)) {
              return Char__compareTo_impl_ypi4mb(thisChar, otherChar);
            }
          }
        }
      }
       while (inductionVariable < min);
    return n1 - n2 | 0;
  } else {
    return compareTo(_this__u8e3s4, other);
  }
}
function concatToString(_this__u8e3s4) {
  _init_properties_stringJs_kt__bg7zye();
  var result = '';
  var inductionVariable = 0;
  var last = _this__u8e3s4.length;
  while (inductionVariable < last) {
    var char = _this__u8e3s4[inductionVariable];
    inductionVariable = inductionVariable + 1 | 0;
    result = result + toString(char);
  }
  return result;
}
function toCharArray(_this__u8e3s4) {
  _init_properties_stringJs_kt__bg7zye();
  var tmp = 0;
  var tmp_0 = _this__u8e3s4.length;
  var tmp_1 = charArray(tmp_0);
  while (tmp < tmp_0) {
    var tmp_2 = tmp;
    tmp_1[tmp_2] = charSequenceGet(_this__u8e3s4, tmp_2);
    tmp = tmp + 1 | 0;
  }
  return tmp_1;
}
function STRING_CASE_INSENSITIVE_ORDER$lambda(a, b) {
  _init_properties_stringJs_kt__bg7zye();
  return compareTo_0(a, b, true);
}
var properties_initialized_stringJs_kt_nta8o4;
function _init_properties_stringJs_kt__bg7zye() {
  if (!properties_initialized_stringJs_kt_nta8o4) {
    properties_initialized_stringJs_kt_nta8o4 = true;
    var tmp = STRING_CASE_INSENSITIVE_ORDER$lambda;
    STRING_CASE_INSENSITIVE_ORDER = new sam$kotlin_Comparator$0(tmp);
  }
}
function replace(_this__u8e3s4, oldValue, newValue, ignoreCase) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  // Inline function 'kotlin.text.nativeReplace' call
  var pattern = new RegExp(Companion_getInstance_4().ea(oldValue), ignoreCase ? 'gui' : 'gu');
  var replacement = Companion_getInstance_4().fa(newValue);
  // Inline function 'kotlin.js.asDynamic' call
  return _this__u8e3s4.replace(pattern, replacement);
}
function startsWith(_this__u8e3s4, prefix, ignoreCase) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  if (!ignoreCase) {
    // Inline function 'kotlin.text.nativeStartsWith' call
    // Inline function 'kotlin.js.asDynamic' call
    return _this__u8e3s4.startsWith(prefix, 0);
  } else
    return regionMatches(_this__u8e3s4, 0, prefix, 0, prefix.length, ignoreCase);
}
function replaceFirst(_this__u8e3s4, oldValue, newValue, ignoreCase) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  // Inline function 'kotlin.text.nativeReplace' call
  var pattern = new RegExp(Companion_getInstance_4().ea(oldValue), ignoreCase ? 'ui' : 'u');
  var replacement = Companion_getInstance_4().fa(newValue);
  // Inline function 'kotlin.js.asDynamic' call
  return _this__u8e3s4.replace(pattern, replacement);
}
function regionMatches(_this__u8e3s4, thisOffset, other, otherOffset, length, ignoreCase) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  return regionMatchesImpl(_this__u8e3s4, thisOffset, other, otherOffset, length, ignoreCase);
}
function AbstractCollection$toString$lambda(this$0) {
  return function (it) {
    return it === this$0 ? '(this Collection)' : toString_0(it);
  };
}
var Companion_instance_5;
function Companion_getInstance_5() {
  return Companion_instance_5;
}
function toString_3($this, o) {
  return o === $this ? '(this Map)' : toString_0(o);
}
function implFindEntry($this, key) {
  var tmp$ret$1;
  $l$block: {
    // Inline function 'kotlin.collections.firstOrNull' call
    var tmp0_iterator = $this.n2().r();
    while (tmp0_iterator.s()) {
      var element = tmp0_iterator.t();
      // Inline function 'kotlin.collections.AbstractMap.implFindEntry.<anonymous>' call
      if (equals(element.j2(), key)) {
        tmp$ret$1 = element;
        break $l$block;
      }
    }
    tmp$ret$1 = null;
  }
  return tmp$ret$1;
}
var Companion_instance_6;
function Companion_getInstance_6() {
  return Companion_instance_6;
}
function AbstractMap$toString$lambda(this$0) {
  return function (it) {
    return this$0.oa(it);
  };
}
var Companion_instance_7;
function Companion_getInstance_7() {
  return Companion_instance_7;
}
function collectionToArrayCommonImpl(collection) {
  if (collection.b1()) {
    // Inline function 'kotlin.emptyArray' call
    return [];
  }
  // Inline function 'kotlin.arrayOfNulls' call
  var size = collection.y();
  var destination = fillArrayVal(Array(size), null);
  var iterator = collection.r();
  var index = 0;
  while (iterator.s()) {
    var tmp0 = index;
    index = tmp0 + 1 | 0;
    destination[tmp0] = iterator.t();
  }
  return destination;
}
function emptyList() {
  return EmptyList_getInstance();
}
function listOf_0(elements) {
  return elements.length > 0 ? asList(elements) : emptyList();
}
function shuffled(_this__u8e3s4, random) {
  // Inline function 'kotlin.apply' call
  var this_0 = toMutableList_0(_this__u8e3s4);
  // Inline function 'kotlin.contracts.contract' call
  // Inline function 'kotlin.collections.shuffled.<anonymous>' call
  shuffle(this_0, random);
  return this_0;
}
var EmptyList_instance;
function EmptyList_getInstance() {
  if (EmptyList_instance === VOID)
    new EmptyList();
  return EmptyList_instance;
}
var EmptyIterator_instance;
function EmptyIterator_getInstance() {
  return EmptyIterator_instance;
}
function arrayListOf(elements) {
  return elements.length === 0 ? ArrayList.h1() : ArrayList.x(new ArrayAsCollection(elements, true));
}
function optimizeReadOnlyList(_this__u8e3s4) {
  switch (_this__u8e3s4.y()) {
    case 0:
      return emptyList();
    case 1:
      return listOf(_this__u8e3s4.d1(0));
    default:
      return _this__u8e3s4;
  }
}
function get_lastIndex(_this__u8e3s4) {
  return _this__u8e3s4.y() - 1 | 0;
}
function throwCountOverflow() {
  throw ArithmeticException.x9('Count overflow has happened.');
}
function throwIndexOverflow() {
  throw ArithmeticException.x9('Index overflow has happened.');
}
function collectionSizeOrDefault(_this__u8e3s4, default_0) {
  var tmp;
  if (isInterface(_this__u8e3s4, Collection)) {
    tmp = _this__u8e3s4.y();
  } else {
    tmp = default_0;
  }
  return tmp;
}
function addAll(_this__u8e3s4, elements) {
  if (isInterface(elements, Collection))
    return _this__u8e3s4.a1(elements);
  else {
    var result = false;
    var tmp1_iterator = elements.r();
    while (tmp1_iterator.s()) {
      var item = tmp1_iterator.t();
      if (_this__u8e3s4.h(item))
        result = true;
    }
    return result;
  }
}
function emptySet() {
  return EmptySet_getInstance();
}
function setOf_0(elements) {
  return toSet(elements);
}
function hashSetOf(elements) {
  return toCollection(elements, HashSet.i7(mapCapacity(elements.length)));
}
function optimizeReadOnlySet(_this__u8e3s4) {
  switch (_this__u8e3s4.y()) {
    case 0:
      return emptySet();
    case 1:
      return setOf(_this__u8e3s4.r().t());
    default:
      return _this__u8e3s4;
  }
}
var EmptySet_instance;
function EmptySet_getInstance() {
  if (EmptySet_instance === VOID)
    new EmptySet();
  return EmptySet_instance;
}
function checkWindowSizeStep(size, step) {
  // Inline function 'kotlin.require' call
  // Inline function 'kotlin.contracts.contract' call
  if (!(size > 0 && step > 0)) {
    // Inline function 'kotlin.collections.checkWindowSizeStep.<anonymous>' call
    var message = !(size === step) ? 'Both size ' + size + ' and step ' + step + ' must be greater than zero.' : 'size ' + size + ' must be greater than zero.';
    throw IllegalArgumentException.p(toString_1(message));
  }
}
function enumEntries(entries) {
  return EnumEntriesList.ab(entries);
}
function getProgressionLastElement(start, end, step) {
  var tmp;
  if (step > 0) {
    tmp = start >= end ? end : end - differenceModulo(end, start, step) | 0;
  } else if (step < 0) {
    tmp = start <= end ? end : end + differenceModulo(start, end, -step | 0) | 0;
  } else {
    throw IllegalArgumentException.p('Step is zero.');
  }
  return tmp;
}
function getProgressionLastElement_0(start, end, step) {
  var tmp;
  if (step.p1(new Long(0, 0)) > 0) {
    tmp = start.p1(end) >= 0 ? end : end.w2(differenceModulo_0(end, start, step));
  } else if (step.p1(new Long(0, 0)) < 0) {
    tmp = start.p1(end) <= 0 ? end : end.v2(differenceModulo_0(start, end, step.a3()));
  } else {
    throw IllegalArgumentException.p('Step is zero.');
  }
  return tmp;
}
function differenceModulo(a, b, c) {
  return mod(mod(a, c) - mod(b, c) | 0, c);
}
function differenceModulo_0(a, b, c) {
  return mod_0(mod_0(a, c).w2(mod_0(b, c)), c);
}
function mod(a, b) {
  var mod = a % b | 0;
  return mod >= 0 ? mod : mod + b | 0;
}
function mod_0(a, b) {
  var mod = a.z2(b);
  return mod.p1(new Long(0, 0)) >= 0 ? mod : mod.v2(b);
}
var Default_instance;
function Default_getInstance() {
  if (Default_instance === VOID)
    Default.db();
  return Default_instance;
}
function checkRangeBounds(from, until) {
  // Inline function 'kotlin.contracts.contract' call
  var tmp;
  if (!(until > from)) {
    // Inline function 'kotlin.random.checkRangeBounds.<anonymous>' call
    var message = boundsErrorMessage(from, until);
    throw IllegalArgumentException.p(toString_1(message));
  }
  return tmp;
}
function fastLog2(value) {
  // Inline function 'kotlin.countLeadingZeroBits' call
  return 31 - clz32(value) | 0;
}
function checkRangeBounds_0(from, until) {
  // Inline function 'kotlin.contracts.contract' call
  var tmp;
  if (!(until.p1(from) > 0)) {
    // Inline function 'kotlin.random.checkRangeBounds.<anonymous>' call
    var message = boundsErrorMessage(from, until);
    throw IllegalArgumentException.p(toString_1(message));
  }
  return tmp;
}
function boundsErrorMessage(from, until) {
  return 'Random range is empty: [' + toString_1(from) + ', ' + toString_1(until) + ').';
}
function Random_0(seed) {
  return XorWowRandom.qb(seed, seed >> 31);
}
function takeUpperBits(_this__u8e3s4, bitCount) {
  return (_this__u8e3s4 >>> (32 - bitCount | 0) | 0) & (-bitCount | 0) >> 31;
}
var Companion_instance_8;
function Companion_getInstance_8() {
  if (Companion_instance_8 === VOID)
    new Companion_8();
  return Companion_instance_8;
}
var Companion_instance_9;
function Companion_getInstance_9() {
  if (Companion_instance_9 === VOID)
    new Companion_9();
  return Companion_instance_9;
}
var Companion_instance_10;
function Companion_getInstance_10() {
  if (Companion_instance_10 === VOID)
    new Companion_10();
  return Companion_instance_10;
}
var Companion_instance_11;
function Companion_getInstance_11() {
  return Companion_instance_11;
}
var Companion_instance_12;
function Companion_getInstance_12() {
  return Companion_instance_12;
}
function appendElement(_this__u8e3s4, element, transform) {
  if (!(transform == null)) {
    _this__u8e3s4.q(transform(element));
  } else {
    if (element == null ? true : isCharSequence(element)) {
      _this__u8e3s4.q(element);
    } else {
      if (element instanceof Char) {
        _this__u8e3s4.o8(element.u1_1);
      } else {
        _this__u8e3s4.q(toString_0(element));
      }
    }
  }
}
function equals_0(_this__u8e3s4, other, ignoreCase) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  if (_this__u8e3s4 === other)
    return true;
  if (!ignoreCase)
    return false;
  var thisUpper = uppercaseChar(_this__u8e3s4);
  var otherUpper = uppercaseChar(other);
  var tmp;
  if (thisUpper === otherUpper) {
    tmp = true;
  } else {
    // Inline function 'kotlin.text.lowercaseChar' call
    // Inline function 'kotlin.text.lowercase' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp$ret$2 = toString(thisUpper).toLowerCase();
    var tmp_0 = charSequenceGet(tmp$ret$2, 0);
    // Inline function 'kotlin.text.lowercaseChar' call
    // Inline function 'kotlin.text.lowercase' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp$ret$6 = toString(otherUpper).toLowerCase();
    tmp = tmp_0 === charSequenceGet(tmp$ret$6, 0);
  }
  return tmp;
}
function toLongOrNull(_this__u8e3s4) {
  return toLongOrNull_0(_this__u8e3s4, 10);
}
function toIntOrNull(_this__u8e3s4) {
  return toIntOrNull_0(_this__u8e3s4, 10);
}
function numberFormatError(input) {
  throw NumberFormatException.s9("Invalid number format: '" + input + "'");
}
function toLongOrNull_0(_this__u8e3s4, radix) {
  checkRadix(radix);
  var length = _this__u8e3s4.length;
  if (length === 0)
    return null;
  var start;
  var isNegative;
  var limit;
  var firstChar = charSequenceGet(_this__u8e3s4, 0);
  if (Char__compareTo_impl_ypi4mb(firstChar, _Char___init__impl__6a9atx(48)) < 0) {
    if (length === 1)
      return null;
    start = 1;
    if (firstChar === _Char___init__impl__6a9atx(45)) {
      isNegative = true;
      limit = new Long(0, -2147483648);
    } else if (firstChar === _Char___init__impl__6a9atx(43)) {
      isNegative = false;
      limit = new Long(1, -2147483648);
    } else
      return null;
  } else {
    start = 0;
    isNegative = false;
    limit = new Long(1, -2147483648);
  }
  // Inline function 'kotlin.Long.div' call
  var limitForMaxRadix = (new Long(1, -2147483648)).y2(toLong(36));
  var limitBeforeMul = limitForMaxRadix;
  var result = new Long(0, 0);
  var inductionVariable = start;
  if (inductionVariable < length)
    do {
      var i = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      var digit = digitOf(charSequenceGet(_this__u8e3s4, i), radix);
      if (digit < 0)
        return null;
      if (result.p1(limitBeforeMul) < 0) {
        if (limitBeforeMul.equals(limitForMaxRadix)) {
          // Inline function 'kotlin.Long.div' call
          limitBeforeMul = limit.y2(toLong(radix));
          if (result.p1(limitBeforeMul) < 0) {
            return null;
          }
        } else {
          return null;
        }
      }
      // Inline function 'kotlin.Long.times' call
      result = result.x2(toLong(radix));
      var tmp = result;
      // Inline function 'kotlin.Long.plus' call
      var tmp$ret$3 = limit.v2(toLong(digit));
      if (tmp.p1(tmp$ret$3) < 0)
        return null;
      // Inline function 'kotlin.Long.minus' call
      result = result.w2(toLong(digit));
    }
     while (inductionVariable < length);
  return isNegative ? result : result.a3();
}
function toIntOrNull_0(_this__u8e3s4, radix) {
  checkRadix(radix);
  var length = _this__u8e3s4.length;
  if (length === 0)
    return null;
  var start;
  var isNegative;
  var limit;
  var firstChar = charSequenceGet(_this__u8e3s4, 0);
  if (Char__compareTo_impl_ypi4mb(firstChar, _Char___init__impl__6a9atx(48)) < 0) {
    if (length === 1)
      return null;
    start = 1;
    if (firstChar === _Char___init__impl__6a9atx(45)) {
      isNegative = true;
      limit = -2147483648;
    } else if (firstChar === _Char___init__impl__6a9atx(43)) {
      isNegative = false;
      limit = -2147483647;
    } else
      return null;
  } else {
    start = 0;
    isNegative = false;
    limit = -2147483647;
  }
  var limitForMaxRadix = -59652323;
  var limitBeforeMul = limitForMaxRadix;
  var result = 0;
  var inductionVariable = start;
  if (inductionVariable < length)
    do {
      var i = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      var digit = digitOf(charSequenceGet(_this__u8e3s4, i), radix);
      if (digit < 0)
        return null;
      if (result < limitBeforeMul) {
        if (limitBeforeMul === limitForMaxRadix) {
          limitBeforeMul = limit / radix | 0;
          if (result < limitBeforeMul) {
            return null;
          }
        } else {
          return null;
        }
      }
      result = imul_0(result, radix);
      if (result < (limit + digit | 0))
        return null;
      result = result - digit | 0;
    }
     while (inductionVariable < length);
  return isNegative ? result : -result | 0;
}
function contains_1(_this__u8e3s4, char, ignoreCase) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  return indexOf_1(_this__u8e3s4, char, VOID, ignoreCase) >= 0;
}
function padStart(_this__u8e3s4, length, padChar) {
  padChar = padChar === VOID ? _Char___init__impl__6a9atx(32) : padChar;
  return toString_1(padStart_0(isCharSequence(_this__u8e3s4) ? _this__u8e3s4 : THROW_CCE(), length, padChar));
}
function split(_this__u8e3s4, delimiters, ignoreCase, limit) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  limit = limit === VOID ? 0 : limit;
  if (delimiters.length === 1) {
    return split_1(_this__u8e3s4, toString(delimiters[0]), ignoreCase, limit);
  }
  // Inline function 'kotlin.collections.map' call
  var this_0 = asIterable(rangesDelimitedBy(_this__u8e3s4, delimiters, VOID, ignoreCase, limit));
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'kotlin.text.split.<anonymous>' call
    var tmp$ret$0 = substring(_this__u8e3s4, item);
    destination.h(tmp$ret$0);
  }
  return destination;
}
function substringBefore(_this__u8e3s4, delimiter, missingDelimiterValue) {
  missingDelimiterValue = missingDelimiterValue === VOID ? _this__u8e3s4 : missingDelimiterValue;
  var index = indexOf_1(_this__u8e3s4, delimiter);
  var tmp;
  if (index === -1) {
    tmp = missingDelimiterValue;
  } else {
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp = _this__u8e3s4.substring(0, index);
  }
  return tmp;
}
function trimEnd(_this__u8e3s4, chars) {
  // Inline function 'kotlin.text.trimEnd' call
  var tmp$ret$1;
  $l$block: {
    // Inline function 'kotlin.text.trimEnd' call
    var this_0 = isCharSequence(_this__u8e3s4) ? _this__u8e3s4 : THROW_CCE();
    var inductionVariable = charSequenceLength(this_0) - 1 | 0;
    if (0 <= inductionVariable)
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + -1 | 0;
        // Inline function 'kotlin.text.trimEnd.<anonymous>' call
        var it = charSequenceGet(this_0, index);
        if (!contains(chars, it)) {
          tmp$ret$1 = charSequenceSubSequence(this_0, 0, index + 1 | 0);
          break $l$block;
        }
      }
       while (0 <= inductionVariable);
    tmp$ret$1 = '';
  }
  return toString_1(tmp$ret$1);
}
function substringAfter(_this__u8e3s4, delimiter, missingDelimiterValue) {
  missingDelimiterValue = missingDelimiterValue === VOID ? _this__u8e3s4 : missingDelimiterValue;
  var index = indexOf_1(_this__u8e3s4, delimiter);
  var tmp;
  if (index === -1) {
    tmp = missingDelimiterValue;
  } else {
    // Inline function 'kotlin.text.substring' call
    var startIndex = index + 1 | 0;
    var endIndex = _this__u8e3s4.length;
    // Inline function 'kotlin.js.asDynamic' call
    tmp = _this__u8e3s4.substring(startIndex, endIndex);
  }
  return tmp;
}
function contains_2(_this__u8e3s4, other, ignoreCase) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  var tmp;
  if (typeof other === 'string') {
    tmp = indexOf_2(_this__u8e3s4, other, VOID, ignoreCase) >= 0;
  } else {
    tmp = indexOf_3(_this__u8e3s4, other, 0, charSequenceLength(_this__u8e3s4), ignoreCase) >= 0;
  }
  return tmp;
}
function split_0(_this__u8e3s4, delimiters, ignoreCase, limit) {
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  limit = limit === VOID ? 0 : limit;
  if (delimiters.length === 1) {
    var delimiter = delimiters[0];
    // Inline function 'kotlin.text.isEmpty' call
    if (!(charSequenceLength(delimiter) === 0)) {
      return split_1(_this__u8e3s4, delimiter, ignoreCase, limit);
    }
  }
  // Inline function 'kotlin.collections.map' call
  var this_0 = asIterable(rangesDelimitedBy_0(_this__u8e3s4, delimiters, VOID, ignoreCase, limit));
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'kotlin.text.split.<anonymous>' call
    var tmp$ret$1 = substring(_this__u8e3s4, item);
    destination.h(tmp$ret$1);
  }
  return destination;
}
function trimStart(_this__u8e3s4, chars) {
  // Inline function 'kotlin.text.trimStart' call
  var tmp$ret$1;
  $l$block: {
    // Inline function 'kotlin.text.trimStart' call
    var this_0 = isCharSequence(_this__u8e3s4) ? _this__u8e3s4 : THROW_CCE();
    var inductionVariable = 0;
    var last = charSequenceLength(this_0) - 1 | 0;
    if (inductionVariable <= last)
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        // Inline function 'kotlin.text.trimStart.<anonymous>' call
        var it = charSequenceGet(this_0, index);
        if (!contains(chars, it)) {
          tmp$ret$1 = charSequenceSubSequence(this_0, index, charSequenceLength(this_0));
          break $l$block;
        }
      }
       while (inductionVariable <= last);
    tmp$ret$1 = '';
  }
  return toString_1(tmp$ret$1);
}
function indexOf_1(_this__u8e3s4, char, startIndex, ignoreCase) {
  startIndex = startIndex === VOID ? 0 : startIndex;
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  var tmp;
  var tmp_0;
  if (ignoreCase) {
    tmp_0 = true;
  } else {
    tmp_0 = !(typeof _this__u8e3s4 === 'string');
  }
  if (tmp_0) {
    // Inline function 'kotlin.charArrayOf' call
    var tmp$ret$0 = charArrayOf([char]);
    tmp = indexOfAny(_this__u8e3s4, tmp$ret$0, startIndex, ignoreCase);
  } else {
    // Inline function 'kotlin.text.nativeIndexOf' call
    // Inline function 'kotlin.text.nativeIndexOf' call
    var str = toString(char);
    // Inline function 'kotlin.js.asDynamic' call
    tmp = _this__u8e3s4.indexOf(str, startIndex);
  }
  return tmp;
}
function indexOf_2(_this__u8e3s4, string, startIndex, ignoreCase) {
  startIndex = startIndex === VOID ? 0 : startIndex;
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  var tmp;
  var tmp_0;
  if (ignoreCase) {
    tmp_0 = true;
  } else {
    tmp_0 = !(typeof _this__u8e3s4 === 'string');
  }
  if (tmp_0) {
    tmp = indexOf_3(_this__u8e3s4, string, startIndex, charSequenceLength(_this__u8e3s4), ignoreCase);
  } else {
    // Inline function 'kotlin.text.nativeIndexOf' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp = _this__u8e3s4.indexOf(string, startIndex);
  }
  return tmp;
}
function regionMatchesImpl(_this__u8e3s4, thisOffset, other, otherOffset, length, ignoreCase) {
  if (otherOffset < 0 || thisOffset < 0 || thisOffset > (charSequenceLength(_this__u8e3s4) - length | 0) || otherOffset > (charSequenceLength(other) - length | 0)) {
    return false;
  }
  var inductionVariable = 0;
  if (inductionVariable < length)
    do {
      var index = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      if (!equals_0(charSequenceGet(_this__u8e3s4, thisOffset + index | 0), charSequenceGet(other, otherOffset + index | 0), ignoreCase))
        return false;
    }
     while (inductionVariable < length);
  return true;
}
function trim(_this__u8e3s4) {
  // Inline function 'kotlin.text.trim' call
  var startIndex = 0;
  var endIndex = charSequenceLength(_this__u8e3s4) - 1 | 0;
  var startFound = false;
  $l$loop: while (startIndex <= endIndex) {
    var index = !startFound ? startIndex : endIndex;
    var p0 = charSequenceGet(_this__u8e3s4, index);
    var match = isWhitespace(p0);
    if (!startFound) {
      if (!match)
        startFound = true;
      else
        startIndex = startIndex + 1 | 0;
    } else {
      if (!match)
        break $l$loop;
      else
        endIndex = endIndex - 1 | 0;
    }
  }
  return charSequenceSubSequence(_this__u8e3s4, startIndex, endIndex + 1 | 0);
}
function padStart_0(_this__u8e3s4, length, padChar) {
  padChar = padChar === VOID ? _Char___init__impl__6a9atx(32) : padChar;
  if (length < 0)
    throw IllegalArgumentException.p('Desired length ' + length + ' is less than zero.');
  if (length <= charSequenceLength(_this__u8e3s4))
    return charSequenceSubSequence(_this__u8e3s4, 0, charSequenceLength(_this__u8e3s4));
  var sb = StringBuilder.u8(length);
  var inductionVariable = 1;
  var last = length - charSequenceLength(_this__u8e3s4) | 0;
  if (inductionVariable <= last)
    do {
      var i = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      sb.o8(padChar);
    }
     while (!(i === last));
  sb.q(_this__u8e3s4);
  return sb;
}
function split_1(_this__u8e3s4, delimiter, ignoreCase, limit) {
  requireNonNegativeLimit(limit);
  var currentOffset = 0;
  var nextIndex = indexOf_2(_this__u8e3s4, delimiter, currentOffset, ignoreCase);
  if (nextIndex === -1 || limit === 1) {
    return listOf(toString_1(_this__u8e3s4));
  }
  var isLimited = limit > 0;
  var result = ArrayList.z(isLimited ? coerceAtMost_0(limit, 10) : 10);
  $l$loop: do {
    // Inline function 'kotlin.text.substring' call
    var startIndex = currentOffset;
    var endIndex = nextIndex;
    var tmp$ret$0 = toString_1(charSequenceSubSequence(_this__u8e3s4, startIndex, endIndex));
    result.h(tmp$ret$0);
    currentOffset = nextIndex + delimiter.length | 0;
    if (isLimited && result.y() === (limit - 1 | 0))
      break $l$loop;
    nextIndex = indexOf_2(_this__u8e3s4, delimiter, currentOffset, ignoreCase);
  }
   while (!(nextIndex === -1));
  // Inline function 'kotlin.text.substring' call
  var startIndex_0 = currentOffset;
  var endIndex_0 = charSequenceLength(_this__u8e3s4);
  var tmp$ret$1 = toString_1(charSequenceSubSequence(_this__u8e3s4, startIndex_0, endIndex_0));
  result.h(tmp$ret$1);
  return result;
}
function substring(_this__u8e3s4, range) {
  return toString_1(charSequenceSubSequence(_this__u8e3s4, range.wb(), range.ac() + 1 | 0));
}
function rangesDelimitedBy(_this__u8e3s4, delimiters, startIndex, ignoreCase, limit) {
  startIndex = startIndex === VOID ? 0 : startIndex;
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  limit = limit === VOID ? 0 : limit;
  requireNonNegativeLimit(limit);
  return new DelimitedRangesSequence(_this__u8e3s4, startIndex, limit, rangesDelimitedBy$lambda(delimiters, ignoreCase));
}
function indexOf_3(_this__u8e3s4, other, startIndex, endIndex, ignoreCase, last) {
  last = last === VOID ? false : last;
  var indices = !last ? numberRangeToNumber(coerceAtLeast_0(startIndex, 0), coerceAtMost_0(endIndex, charSequenceLength(_this__u8e3s4))) : downTo(coerceAtMost_0(startIndex, get_lastIndex_0(_this__u8e3s4)), coerceAtLeast_0(endIndex, 0));
  var tmp;
  if (typeof _this__u8e3s4 === 'string') {
    tmp = typeof other === 'string';
  } else {
    tmp = false;
  }
  if (tmp) {
    var inductionVariable = indices.xb_1;
    var last_0 = indices.yb_1;
    var step = indices.zb_1;
    if (step > 0 && inductionVariable <= last_0 || (step < 0 && last_0 <= inductionVariable))
      do {
        var index = inductionVariable;
        inductionVariable = inductionVariable + step | 0;
        if (regionMatches(other, 0, _this__u8e3s4, index, charSequenceLength(other), ignoreCase))
          return index;
      }
       while (!(index === last_0));
  } else {
    var inductionVariable_0 = indices.xb_1;
    var last_1 = indices.yb_1;
    var step_0 = indices.zb_1;
    if (step_0 > 0 && inductionVariable_0 <= last_1 || (step_0 < 0 && last_1 <= inductionVariable_0))
      do {
        var index_0 = inductionVariable_0;
        inductionVariable_0 = inductionVariable_0 + step_0 | 0;
        if (regionMatchesImpl(other, 0, _this__u8e3s4, index_0, charSequenceLength(other), ignoreCase))
          return index_0;
      }
       while (!(index_0 === last_1));
  }
  return -1;
}
function rangesDelimitedBy_0(_this__u8e3s4, delimiters, startIndex, ignoreCase, limit) {
  startIndex = startIndex === VOID ? 0 : startIndex;
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  limit = limit === VOID ? 0 : limit;
  requireNonNegativeLimit(limit);
  var delimitersList = asList(delimiters);
  return new DelimitedRangesSequence(_this__u8e3s4, startIndex, limit, rangesDelimitedBy$lambda_0(delimitersList, ignoreCase));
}
function indexOfAny(_this__u8e3s4, chars, startIndex, ignoreCase) {
  startIndex = startIndex === VOID ? 0 : startIndex;
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  var tmp;
  if (!ignoreCase && chars.length === 1) {
    tmp = typeof _this__u8e3s4 === 'string';
  } else {
    tmp = false;
  }
  if (tmp) {
    var char = single(chars);
    // Inline function 'kotlin.text.nativeIndexOf' call
    // Inline function 'kotlin.text.nativeIndexOf' call
    var str = toString(char);
    // Inline function 'kotlin.js.asDynamic' call
    return _this__u8e3s4.indexOf(str, startIndex);
  }
  var inductionVariable = coerceAtLeast_0(startIndex, 0);
  var last = get_lastIndex_0(_this__u8e3s4);
  if (inductionVariable <= last)
    do {
      var index = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      var charAtIndex = charSequenceGet(_this__u8e3s4, index);
      var tmp$ret$4;
      $l$block: {
        // Inline function 'kotlin.collections.any' call
        var inductionVariable_0 = 0;
        var last_0 = chars.length;
        while (inductionVariable_0 < last_0) {
          var element = chars[inductionVariable_0];
          inductionVariable_0 = inductionVariable_0 + 1 | 0;
          // Inline function 'kotlin.text.indexOfAny.<anonymous>' call
          if (equals_0(element, charAtIndex, ignoreCase)) {
            tmp$ret$4 = true;
            break $l$block;
          }
        }
        tmp$ret$4 = false;
      }
      if (tmp$ret$4)
        return index;
    }
     while (!(index === last));
  return -1;
}
function requireNonNegativeLimit(limit) {
  // Inline function 'kotlin.contracts.contract' call
  var tmp;
  if (!(limit >= 0)) {
    // Inline function 'kotlin.text.requireNonNegativeLimit.<anonymous>' call
    var message = 'Limit must be non-negative, but was ' + limit;
    throw IllegalArgumentException.p(toString_1(message));
  }
  return tmp;
}
function calcNext($this) {
  if ($this.sc_1 < 0) {
    $this.qc_1 = 0;
    $this.tc_1 = null;
  } else {
    var tmp;
    var tmp_0;
    if ($this.vc_1.yc_1 > 0) {
      $this.uc_1 = $this.uc_1 + 1 | 0;
      tmp_0 = $this.uc_1 >= $this.vc_1.yc_1;
    } else {
      tmp_0 = false;
    }
    if (tmp_0) {
      tmp = true;
    } else {
      tmp = $this.sc_1 > charSequenceLength($this.vc_1.wc_1);
    }
    if (tmp) {
      $this.tc_1 = numberRangeToNumber($this.rc_1, get_lastIndex_0($this.vc_1.wc_1));
      $this.sc_1 = -1;
    } else {
      var match = $this.vc_1.zc_1($this.vc_1.wc_1, $this.sc_1);
      if (match == null) {
        $this.tc_1 = numberRangeToNumber($this.rc_1, get_lastIndex_0($this.vc_1.wc_1));
        $this.sc_1 = -1;
      } else {
        var index = match.cd();
        var length = match.dd();
        $this.tc_1 = until($this.rc_1, index);
        $this.rc_1 = index + length | 0;
        $this.sc_1 = $this.rc_1 + (length === 0 ? 1 : 0) | 0;
      }
    }
    $this.qc_1 = 1;
  }
}
function get_lastIndex_0(_this__u8e3s4) {
  return charSequenceLength(_this__u8e3s4) - 1 | 0;
}
function findAnyOf(_this__u8e3s4, strings, startIndex, ignoreCase, last) {
  if (!ignoreCase && strings.y() === 1) {
    var string = single_0(strings);
    var index = !last ? indexOf_2(_this__u8e3s4, string, startIndex) : lastIndexOf(_this__u8e3s4, string, startIndex);
    return index < 0 ? null : to(index, string);
  }
  var indices = !last ? numberRangeToNumber(coerceAtLeast_0(startIndex, 0), charSequenceLength(_this__u8e3s4)) : downTo(coerceAtMost_0(startIndex, get_lastIndex_0(_this__u8e3s4)), 0);
  if (typeof _this__u8e3s4 === 'string') {
    var inductionVariable = indices.xb_1;
    var last_0 = indices.yb_1;
    var step = indices.zb_1;
    if (step > 0 && inductionVariable <= last_0 || (step < 0 && last_0 <= inductionVariable))
      do {
        var index_0 = inductionVariable;
        inductionVariable = inductionVariable + step | 0;
        var tmp$ret$1;
        $l$block: {
          // Inline function 'kotlin.collections.firstOrNull' call
          var tmp0_iterator = strings.r();
          while (tmp0_iterator.s()) {
            var element = tmp0_iterator.t();
            // Inline function 'kotlin.text.findAnyOf.<anonymous>' call
            if (regionMatches(element, 0, _this__u8e3s4, index_0, element.length, ignoreCase)) {
              tmp$ret$1 = element;
              break $l$block;
            }
          }
          tmp$ret$1 = null;
        }
        var matchingString = tmp$ret$1;
        if (!(matchingString == null))
          return to(index_0, matchingString);
      }
       while (!(index_0 === last_0));
  } else {
    var inductionVariable_0 = indices.xb_1;
    var last_1 = indices.yb_1;
    var step_0 = indices.zb_1;
    if (step_0 > 0 && inductionVariable_0 <= last_1 || (step_0 < 0 && last_1 <= inductionVariable_0))
      do {
        var index_1 = inductionVariable_0;
        inductionVariable_0 = inductionVariable_0 + step_0 | 0;
        var tmp$ret$3;
        $l$block_0: {
          // Inline function 'kotlin.collections.firstOrNull' call
          var tmp0_iterator_0 = strings.r();
          while (tmp0_iterator_0.s()) {
            var element_0 = tmp0_iterator_0.t();
            // Inline function 'kotlin.text.findAnyOf.<anonymous>' call
            if (regionMatchesImpl(element_0, 0, _this__u8e3s4, index_1, element_0.length, ignoreCase)) {
              tmp$ret$3 = element_0;
              break $l$block_0;
            }
          }
          tmp$ret$3 = null;
        }
        var matchingString_0 = tmp$ret$3;
        if (!(matchingString_0 == null))
          return to(index_1, matchingString_0);
      }
       while (!(index_1 === last_1));
  }
  return null;
}
function lastIndexOf(_this__u8e3s4, string, startIndex, ignoreCase) {
  startIndex = startIndex === VOID ? get_lastIndex_0(_this__u8e3s4) : startIndex;
  ignoreCase = ignoreCase === VOID ? false : ignoreCase;
  var tmp;
  var tmp_0;
  if (ignoreCase) {
    tmp_0 = true;
  } else {
    tmp_0 = !(typeof _this__u8e3s4 === 'string');
  }
  if (tmp_0) {
    tmp = indexOf_3(_this__u8e3s4, string, startIndex, 0, ignoreCase, true);
  } else {
    // Inline function 'kotlin.text.nativeLastIndexOf' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp = _this__u8e3s4.lastIndexOf(string, startIndex);
  }
  return tmp;
}
function rangesDelimitedBy$lambda($delimiters, $ignoreCase) {
  return function ($this$$receiver, currentIndex) {
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'kotlin.text.rangesDelimitedBy.<anonymous>.<anonymous>' call
    var it = indexOfAny($this$$receiver, $delimiters, currentIndex, $ignoreCase);
    return it < 0 ? null : to(it, 1);
  };
}
function rangesDelimitedBy$lambda_0($delimitersList, $ignoreCase) {
  return function ($this$$receiver, currentIndex) {
    var tmp0_safe_receiver = findAnyOf($this$$receiver, $delimitersList, currentIndex, $ignoreCase, false);
    var tmp;
    if (tmp0_safe_receiver == null) {
      tmp = null;
    } else {
      // Inline function 'kotlin.let' call
      // Inline function 'kotlin.contracts.contract' call
      // Inline function 'kotlin.text.rangesDelimitedBy.<anonymous>.<anonymous>' call
      tmp = to(tmp0_safe_receiver.ad_1, tmp0_safe_receiver.bd_1.length);
    }
    return tmp;
  };
}
function _Result___init__impl__xyqfz8(value) {
  return value;
}
function _Result___get_value__impl__bjfvqg($this) {
  return $this;
}
function _Result___get_isFailure__impl__jpiriv($this) {
  var tmp = _Result___get_value__impl__bjfvqg($this);
  return tmp instanceof Failure;
}
var Companion_instance_13;
function Companion_getInstance_13() {
  return Companion_instance_13;
}
function createFailure(exception) {
  return new Failure(exception);
}
function to(_this__u8e3s4, that) {
  return new Pair(_this__u8e3s4, that);
}
function _UShort___init__impl__jigrne(data) {
  return data;
}
function _UShort___get_data__impl__g0245($this) {
  return $this;
}
function ex($this, sp, meaning) {
  return to(sp, meaning);
}
function sp($this, p) {
  return Companion_getInstance_14().rd(p.slice());
}
function intro($this, cat) {
  var tmp;
  switch (cat.p2_1) {
    case 0:
    case 9:
      // Inline function 'kotlin.collections.map' call

      var this_0 = numberRangeToNumber(0, 10);
      // Inline function 'kotlin.collections.mapTo' call

      var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
      var inductionVariable = this_0.xb_1;
      var last = this_0.yb_1;
      if (inductionVariable <= last)
        do {
          var item = inductionVariable;
          inductionVariable = inductionVariable + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberLessons.intro.<anonymous>' call
          var it = item;
          var tmp$ret$0 = to(ChineseNumber_getInstance().xd(it), '' + it);
          destination.h(tmp$ret$0);
        }
         while (!(item === last));
      var tmp_0 = new LessonRule('Angka 0\u201310', 'Hafalkan sebelas angka ini dulu. Semua angka lain dirangkai dari sini. Ketuk kartu untuk mendengar.', VOID, destination);
      var tmp_1 = new LessonRule('Satuan Besar', 'Perhatikan padanannya dalam bahasa Indonesia, terutama \u4E07 dan \u4EBF.', VOID, listOf_0([to(ChineseNumber_getInstance().xd(10), '10'), to(sp($this, [to('\u767E', 'b\u01CEi')]), '100'), to(sp($this, [to('\u5343', 'qi\u0101n')]), '1.000'), to(sp($this, [to('\u4E07', 'w\xE0n')]), '10.000'), to(ChineseNumber_getInstance().xd(100000), '100.000'), to(sp($this, [to('\u767E', 'b\u01CEi'), to('\u4E07', 'w\xE0n')]), '1 juta'), to(sp($this, [to('\u5343', 'qi\u0101n'), to('\u4E07', 'w\xE0n')]), '10 juta'), to(sp($this, [to('\u4EBF', 'y\xEC')]), '100 juta'), to(sp($this, [to('\u5341', 'sh\xED'), to('\u4EBF', 'y\xEC')]), '1 miliar')]));
      // Inline function 'kotlin.collections.map' call

      var this_1 = listOf_0([20, 30, 40, 50, 60, 70, 80, 90, 21, 35, 48, 99]);
      // Inline function 'kotlin.collections.mapTo' call

      var destination_0 = ArrayList.z(collectionSizeOrDefault(this_1, 10));
      var tmp0_iterator = this_1.r();
      while (tmp0_iterator.s()) {
        var item_0 = tmp0_iterator.t();
        // Inline function 'com.nesxha.readit.data.NumberLessons.intro.<anonymous>' call
        var tmp$ret$3 = to(ChineseNumber_getInstance().xd(item_0), '' + item_0);
        destination_0.h(tmp$ret$3);
      }

      tmp = listOf_0([tmp_0, tmp_1, new LessonRule('Pola Puluhan', 'Puluhan = angka + \u5341. Puluhan + satuan = angka + \u5341 + angka.', VOID, destination_0)]);
      break;
    case 1:
      tmp = listOf_0([new LessonRule('Satuan Uang', 'Mata uang Tiongkok adalah \u4EBA\u6C11\u5E01 (r\xE9nm\xEDnb\xEC, RMB), simbolnya \xA5.', VOID, listOf_0([to(sp($this, [to('\u5757', 'ku\xE0i')]), 'Yuan (lisan)'), to(sp($this, [to('\u5143', 'yu\xE1n')]), 'Yuan (tertulis)'), to(sp($this, [to('\u6BDB', 'm\xE1o')]), '0,1 yuan (lisan)'), to(sp($this, [to('\u89D2', 'ji\u01CEo')]), '0,1 yuan (tertulis)'), to(sp($this, [to('\u5206', 'f\u0113n')]), '0,01 yuan'), to(sp($this, [to('\u4EBA', 'r\xE9n'), to('\u6C11', 'm\xEDn'), to('\u5E01', 'b\xEC')]), 'Rupiah-nya Tiongkok')])), new LessonRule('Bertanya Harga', 'Kalimat yang paling sering dipakai saat belanja.', listOf_0([to(sp($this, [to('\u591A', 'du\u014D'), to('\u5C11', 'sh\u01CEo'), to('\u94B1', 'qi\xE1n')]), 'Berapa harganya?'), to(sp($this, [to('\u592A', 't\xE0i'), to('\u8D35', 'gu\xEC'), to('\u4E86', 'le')]), 'Terlalu mahal!'), to(sp($this, [to('\u4FBF', 'pi\xE1n'), to('\u5B9C', 'yi'), to('\u4E00', 'y\xEC'), to('\u70B9', 'di\u01CEn'), to('\u513F', 'r')]), 'Lebih murah sedikit, dong')]))]);
      break;
    case 2:
      // Inline function 'kotlin.collections.map' call

      var this_2 = listOf_0([9, 8, 7, 6, 5]);
      // Inline function 'kotlin.collections.mapTo' call

      var destination_1 = ArrayList.z(collectionSizeOrDefault(this_2, 10));
      var tmp0_iterator_0 = this_2.r();
      while (tmp0_iterator_0.s()) {
        var item_1 = tmp0_iterator_0.t();
        // Inline function 'com.nesxha.readit.data.NumberLessons.intro.<anonymous>' call
        var tmp$ret$6 = to(sp(NumberLessons_getInstance(), [to('\u6253', 'd\u01CE')]).ae(new Spoken(listOf(ChineseNumber_getInstance().yd(item_1)))).ae(sp(NumberLessons_getInstance(), [to('\u6298', 'zh\xE9')])), 'Bayar ' + imul_0(item_1, 10) + '% \xB7 hemat ' + (100 - imul_0(item_1, 10) | 0) + '%');
        destination_1.h(tmp$ret$6);
      }

      var tmp_2 = destination_1;
      // Inline function 'kotlin.collections.map' call

      var this_3 = listOf_0([95, 85, 75]);
      // Inline function 'kotlin.collections.mapTo' call

      var destination_2 = ArrayList.z(collectionSizeOrDefault(this_3, 10));
      var tmp0_iterator_1 = this_3.r();
      while (tmp0_iterator_1.s()) {
        var item_2 = tmp0_iterator_1.t();
        // Inline function 'com.nesxha.readit.data.NumberLessons.intro.<anonymous>' call
        var tmp$ret$9 = to(sp(NumberLessons_getInstance(), [to('\u6253', 'd\u01CE')]).ae(new Spoken(listOf_0([ChineseNumber_getInstance().yd(item_2 / 10 | 0), ChineseNumber_getInstance().yd(item_2 % 10 | 0)]))).ae(sp(NumberLessons_getInstance(), [to('\u6298', 'zh\xE9')])), 'Bayar ' + item_2 + '% \xB7 hemat ' + (100 - item_2 | 0) + '%');
        destination_2.h(tmp$ret$9);
      }

      tmp = listOf_0([new LessonRule('Tabel \u6298', 'Angka \u6298 = persen yang dibayar. Sisanya adalah potongan.', VOID, plus(plus(tmp_2, destination_2), listOf(to(sp($this, [to('\u534A', 'b\xE0n'), to('\u4EF7', 'ji\xE0')]), 'Bayar 50%')))), new LessonRule('Kata Promo Lain', 'Sering muncul di toko dan aplikasi belanja.', VOID, listOf_0([to(sp($this, [to('\u4F18', 'y\u014Du'), to('\u60E0', 'hu\xEC')]), 'Promo / potongan'), to(sp($this, [to('\u5305', 'b\u0101o'), to('\u90AE', 'y\xF3u')]), 'Gratis ongkir'), to(sp($this, [to('\u4E70', 'm\u01CEi'), to('\u4E00', 'y\u012B'), to('\u9001', 's\xF2ng'), to('\u4E00', 'y\u012B')]), 'Beli 1 gratis 1'), to(sp($this, [to('\u7279', 't\xE8'), to('\u4EF7', 'ji\xE0')]), 'Harga spesial')]))]);
      break;
    case 3:
      tmp = listOf(new LessonRule('Yang Sering Dipakai', 'Ketuk untuk mendengar.', VOID, listOf_0([to(ChineseNumber_getInstance().be(1, 2), '1/2'), to(ChineseNumber_getInstance().be(1, 3), '1/3'), to(ChineseNumber_getInstance().be(2, 3), '2/3'), to(ChineseNumber_getInstance().be(1, 4), '1/4'), to(ChineseNumber_getInstance().be(3, 4), '3/4'), to(ChineseNumber_getInstance().ce(new Long(50, 0)), '50%'), to(ChineseNumber_getInstance().ce(new Long(100, 0)), '100%'), to(ChineseNumber_getInstance().de(new Long(0, 0), '5'), '0,5')])));
      break;
    case 4:
      tmp = listOf_0([new LessonRule('Kata Penting', 'Kosakata dasar untuk menyebut waktu.', VOID, listOf_0([to(sp($this, [to('\u70B9', 'di\u01CEn')]), 'Jam (pukul)'), to(sp($this, [to('\u5206', 'f\u0113n')]), 'Menit'), to(sp($this, [to('\u4E00', 'y\xED'), to('\u523B', 'k\xE8')]), ':15'), to(sp($this, [to('\u534A', 'b\xE0n')]), ':30'), to(sp($this, [to('\u4E09', 's\u0101n'), to('\u523B', 'k\xE8')]), ':45'), to(sp($this, [to('\u5DEE', 'ch\xE0')]), 'Kurang'), to(sp($this, [to('\u949F', 'zh\u014Dng')]), 'Tepat (\u4E09\u70B9\u949F = jam 3 tepat)'), to(sp($this, [to('\u51E0', 'j\u01D0'), to('\u70B9', 'di\u01CEn')]), 'Jam berapa?')])), new LessonRule('Bagian Hari', 'Diletakkan di depan jam: \u4E0B\u5348\u4E09\u70B9 = jam 3 sore.', VOID, listOf_0([to(sp($this, [to('\u51CC', 'l\xEDng'), to('\u6668', 'ch\xE9n')]), 'Dini hari'), to(sp($this, [to('\u65E9', 'z\u01CEo'), to('\u4E0A', 'shang')]), 'Pagi (awal)'), to(sp($this, [to('\u4E0A', 'sh\xE0ng'), to('\u5348', 'w\u01D4')]), 'Pagi\u2013siang'), to(sp($this, [to('\u4E2D', 'zh\u014Dng'), to('\u5348', 'w\u01D4')]), 'Tengah hari'), to(sp($this, [to('\u4E0B', 'xi\xE0'), to('\u5348', 'w\u01D4')]), 'Siang\u2013sore'), to(sp($this, [to('\u665A', 'w\u01CEn'), to('\u4E0A', 'shang')]), 'Malam')]))]);
      break;
    case 5:
      // Inline function 'kotlin.collections.map' call

      var this_4 = numberRangeToNumber(1, 7);
      // Inline function 'kotlin.collections.mapTo' call

      var destination_3 = ArrayList.z(collectionSizeOrDefault(this_4, 10));
      var inductionVariable_0 = this_4.xb_1;
      var last_0 = this_4.yb_1;
      if (inductionVariable_0 <= last_0)
        do {
          var item_3 = inductionVariable_0;
          inductionVariable_0 = inductionVariable_0 + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberLessons.intro.<anonymous>' call
          var it_0 = item_3;
          var tmp$ret$12 = to(ChineseNumber_getInstance().ee(it_0), listOf_0(['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu', 'Minggu']).d1(it_0 - 1 | 0));
          destination_3.h(tmp$ret$12);
        }
         while (!(item_3 === last_0));
      var tmp_3 = new LessonRule('Nama Hari', '\u661F\u671F + angka. Minggu memakai \u5929 atau \u65E5.', VOID, destination_3);
      // Inline function 'kotlin.collections.map' call

      var this_5 = numberRangeToNumber(1, 12);
      // Inline function 'kotlin.collections.mapTo' call

      var destination_4 = ArrayList.z(collectionSizeOrDefault(this_5, 10));
      var inductionVariable_1 = this_5.xb_1;
      var last_1 = this_5.yb_1;
      if (inductionVariable_1 <= last_1)
        do {
          var item_4 = inductionVariable_1;
          inductionVariable_1 = inductionVariable_1 + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberLessons.intro.<anonymous>' call
          var it_1 = item_4;
          var tmp$ret$15 = to(ChineseNumber_getInstance().xd(it_1).ae(sp(NumberLessons_getInstance(), [to('\u6708', 'yu\xE8')])), listOf_0(['Januari', 'Februari', 'Maret', 'April', 'Mei', 'Juni', 'Juli', 'Agustus', 'September', 'Oktober', 'November', 'Desember']).d1(it_1 - 1 | 0));
          destination_4.h(tmp$ret$15);
        }
         while (!(item_4 === last_1));
      tmp = listOf_0([tmp_3, new LessonRule('Nama Bulan', 'Angka + \u6708.', VOID, destination_4), new LessonRule('Hari Relatif', 'Untuk membicarakan kemarin, besok, dan seterusnya.', VOID, listOf_0([to(sp($this, [to('\u524D', 'qi\xE1n'), to('\u5929', 'ti\u0101n')]), '2 hari lalu'), to(sp($this, [to('\u6628', 'zu\xF3'), to('\u5929', 'ti\u0101n')]), 'Kemarin'), to(sp($this, [to('\u4ECA', 'j\u012Bn'), to('\u5929', 'ti\u0101n')]), 'Hari ini'), to(sp($this, [to('\u660E', 'm\xEDng'), to('\u5929', 'ti\u0101n')]), 'Besok'), to(sp($this, [to('\u540E', 'h\xF2u'), to('\u5929', 'ti\u0101n')]), 'Lusa'), to(sp($this, [to('\u5927', 'd\xE0'), to('\u540E', 'h\xF2u'), to('\u5929', 'ti\u0101n')]), '3 hari lagi')]))]);
      break;
    case 6:
      // Inline function 'kotlin.collections.map' call

      var this_6 = numberRangeToNumber(0, 9);
      // Inline function 'kotlin.collections.mapTo' call

      var destination_5 = ArrayList.z(collectionSizeOrDefault(this_6, 10));
      var inductionVariable_2 = this_6.xb_1;
      var last_2 = this_6.yb_1;
      if (inductionVariable_2 <= last_2)
        do {
          var item_5 = inductionVariable_2;
          inductionVariable_2 = inductionVariable_2 + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberLessons.intro.<anonymous>' call
          var d = item_5;
          var tmp$ret$18 = to(ChineseNumber_getInstance().fe('' + d, true, '\u96F6'), '' + d);
          destination_5.h(tmp$ret$18);
        }
         while (!(item_5 === last_2));
      tmp = listOf_0([new LessonRule('Digit Saat Menyebut Nomor', 'Perhatikan angka 1 yang dibaca \u5E7A (y\u0101o).', VOID, destination_5), new LessonRule('Bertanya Nomor', 'Kalimat yang sering dipakai.', listOf_0([to(sp($this, [to('\u4F60', 'n\u01D0'), to('\u7684', 'de'), to('\u7535', 'di\xE0n'), to('\u8BDD', 'hu\xE0'), to('\u53F7', 'h\xE0o'), to('\u7801', 'm\u01CE'), to('\u662F', 'sh\xEC'), to('\u591A', 'du\u014D'), to('\u5C11', 'shao')]), 'Nomor teleponmu berapa?'), to(sp($this, [to('\u4F60', 'n\u01D0'), to('\u4F4F', 'zh\xF9'), to('\u51E0', 'j\u01D0'), to('\u53F7', 'h\xE0o'), to('\u623F', 'f\xE1ng'), to('\u95F4', 'ji\u0101n')]), 'Kamu tinggal di kamar nomor berapa?')]))]);
      break;
    case 7:
      tmp = listOf_0([new LessonRule('Kata Satuan Umum', 'Di Mandarin, angka hampir selalu diikuti kata satuan sebelum bendanya.', VOID, listOf_0([to(sp($this, [to('\u4E2A', 'g\xE8')]), 'Umum (orang, benda)'), to(sp($this, [to('\u672C', 'b\u011Bn')]), 'Buku'), to(sp($this, [to('\u5F20', 'zh\u0101ng')]), 'Kertas, tiket, meja'), to(sp($this, [to('\u676F', 'b\u0113i')]), 'Gelas'), to(sp($this, [to('\u74F6', 'p\xEDng')]), 'Botol'), to(sp($this, [to('\u4EF6', 'ji\xE0n')]), 'Pakaian, urusan'), to(sp($this, [to('\u53EA', 'zh\u012B')]), 'Hewan kecil'), to(sp($this, [to('\u8F86', 'li\xE0ng')]), 'Kendaraan'), to(sp($this, [to('\u53CC', 'shu\u0101ng')]), 'Pasang (sepatu, sumpit)')])), new LessonRule('Satuan Ukuran', 'Ukuran yang sering dipakai sehari-hari.', VOID, listOf_0([to(sp($this, [to('\u5C81', 'su\xEC')]), 'Tahun (umur)'), to(sp($this, [to('\u7C73', 'm\u01D0')]), 'Meter'), to(sp($this, [to('\u516C', 'g\u014Dng'), to('\u91CC', 'l\u01D0')]), 'Kilometer'), to(sp($this, [to('\u516C', 'g\u014Dng'), to('\u65A4', 'j\u012Bn')]), 'Kilogram'), to(sp($this, [to('\u65A4', 'j\u012Bn')]), '\xBD kilogram'), to(sp($this, [to('\u697C', 'l\xF3u')]), 'Lantai')]))]);
      break;
    case 8:
      tmp = listOf(new LessonRule('Kata Perkiraan', 'Untuk jumlah yang tidak pasti.', VOID, listOf_0([to(sp($this, [to('\u51E0', 'j\u01D0')]), 'Beberapa'), to(sp($this, [to('\u5341', 'sh\xED'), to('\u51E0', 'j\u01D0')]), 'Belasan'), to(sp($this, [to('\u51E0', 'j\u01D0'), to('\u5341', 'sh\xED')]), 'Puluhan'), to(sp($this, [to('\u51E0', 'j\u01D0'), to('\u767E', 'b\u01CEi')]), 'Ratusan'), to(sp($this, [to('\u51E0', 'j\u01D0'), to('\u5343', 'qi\u0101n')]), 'Ribuan'), to(sp($this, [to('\u591A', 'du\u014D')]), 'Lebih'), to(sp($this, [to('\u5DE6', 'zu\u01D2'), to('\u53F3', 'y\xF2u')]), 'Sekitar (di belakang angka)'), to(sp($this, [to('\u5927', 'd\xE0'), to('\u6982', 'g\xE0i')]), 'Kira-kira (di depan angka)')])));
      break;
    default:
      noWhenBranchMatchedException();
      break;
  }
  return tmp;
}
function theory($this, cat) {
  var tmp;
  switch (cat.p2_1) {
    case 0:
    case 9:
      var tmp1_steps = listOf_0(['3 ribuan \u2192 \u4E09\u5343', '4 ratusan \u2192 \u56DB\u767E', '0 puluhan \u2192 kosong di tengah, jadi \u96F6', '7 satuan \u2192 \u4E03', 'Hasil: \u4E09\u5343\u56DB\u767E\u96F6\u4E03 (s\u0101n qi\u0101n s\xEC b\u01CEi l\xEDng q\u012B)']);
      var tmp_0 = new LessonRule('Rumus Angka 1\u20139.999', 'Setiap posisi (ribuan, ratusan, puluhan, satuan) dibaca sebagai angka + nama posisinya. Posisi yang nol dilewati. Kalau ada posisi nol di antara posisi lain, tulis \u96F6 satu kali saja. Nol di akhir tidak dibaca sama sekali.', VOID, VOID, '[A]\u5343 [B]\u767E [C]\u5341 [D]\nA = ribuan \xB7 B = ratusan \xB7 C = puluhan \xB7 D = satuan', tmp1_steps, 'Contoh: 3.407');
      var tmp2_steps = ChineseNumber_getInstance().ge(new Long(30508, 0));
      var tmp_1 = new LessonRule('Rumus Angka Besar', 'Bahasa Indonesia memberi nama setiap 3 digit (ribu, juta, miliar). Mandarin memberi nama setiap 4 digit: \u4E07 untuk kelompok kedua dan \u4EBF untuk kelompok ketiga. Setiap kelompok dibaca dengan rumus 1\u20139.999 di atas, lalu diberi namanya.', VOID, VOID, '[A]\u4EBF [B]\u4E07 [C]\nA, B, C = kelompok 4 digit dari kanan', tmp2_steps, 'Contoh: 30.508');
      var tmp3_steps = ChineseNumber_getInstance().ge(new Long(-1990916689, 0));
      var tmp_2 = new LessonRule('Contoh Angka Sangat Besar', 'Cara yang sama berlaku sampai ratusan miliar.', VOID, VOID, VOID, tmp3_steps, 'Contoh: 2.304.050.607');
      var tmp4_table = listOf_0([to(ChineseNumber_getInstance().xd(1000000), '1 juta'), to(ChineseNumber_getInstance().xd(5000000), '5 juta'), to(ChineseNumber_getInstance().xd(10000000), '10 juta'), to(ChineseNumber_getInstance().xd(50000000), '50 juta'), to(ChineseNumber_getInstance().xd(1000000000), '1 miliar'), to(ChineseNumber_getInstance().he(new Long(-1474836480, 4)), '20 miliar')]);
      var tmp5_steps = listOf_0(['5 juta = 5 \xD7 100 = 500 kelompok \u4E07', '500 dibaca \u4E94\u767E', 'Hasil: \u4E94\u767E\u4E07 (w\u01D4 b\u01CEi w\xE0n)']);
      tmp = listOf_0([tmp_0, tmp_1, tmp_2, new LessonRule('Mengubah Juta dan Miliar', 'Karena 1 juta = 100 \xD7 10.000, maka 1 juta = \u4E00\u767E\u4E07. Jadi jumlah juta dikali 100, lalu diberi \u4E07. Untuk miliar: 1 miliar = 10 \xD7 100.000.000 = \u5341\u4EBF.', VOID, tmp4_table, 'N juta = (N \xD7 100)\u4E07\nN miliar = (N \xD7 10)\u4EBF', tmp5_steps, 'Contoh: 5 juta')]);
      break;
    case 1:
      var tmp6_steps = listOf_0(['36 yuan \u2192 \u4E09\u5341\u516D\u5757', '0 \u6BDB \u2192 kosong, jadi \u96F6', '5 \u5206 \u2192 \u4E94\u5206', 'Hasil: ' + ChineseNumber_getInstance().ie(36, 0, 5).je() + ' (' + ChineseNumber_getInstance().ie(36, 0, 5).ke() + ')']);
      var tmp_3 = new LessonRule('Rumus Harga Lisan', 'Yuan dibaca dulu dengan \u5757, lalu \u6BDB, lalu \u5206. Satuan paling akhir boleh tidak diucapkan. Kalau \u6BDB kosong tetapi ada \u5206, tulis \u96F6 di tengah. Angka 2 sebelum \u5757/\u6BDB menjadi \u4E24.', VOID, VOID, '[yuan]\u5757 [jiao]\u6BDB [fen]\u5206\n1 \u5757 = 10 \u6BDB = 100 \u5206', tmp6_steps, 'Contoh: \xA536,05');
      var tmp7_steps = listOf_0(['12 yuan \u2192 \u5341\u4E8C\u5143', '5 jiao \u2192 \u4E94\u89D2', 'Hasil: ' + ChineseNumber_getInstance().le(12, 5, 0).je()]);
      var tmp_4 = new LessonRule('Rumus Harga Tertulis', 'Di label harga, kuitansi, dan bank dipakai satuan resmi \u5143, \u89D2, \u5206. Semua satuan ditulis lengkap.', VOID, VOID, '[yuan]\u5143 [jiao]\u89D2 [fen]\u5206', tmp7_steps, 'Contoh: \xA512,50');
      var tmp8_steps = listOf_0(['\u4E24\u5757\u4E94 = \xA52,50 per \u65A4', 'Total = 2,50 \xD7 4 = \xA510 \u2192 \u5341\u5757', 'Kembalian = 20 \u2212 10 = \xA510 \u2192 \u627E\u4F60\u5341\u5757']);
      tmp = listOf_0([tmp_3, tmp_4, new LessonRule('Menghitung Total dan Kembalian', 'Baca dulu harga satuannya, ubah ke angka, baru dihitung.', VOID, VOID, 'Total = harga satuan \xD7 jumlah\nKembalian = uang dibayar \u2212 total', tmp8_steps, 'Contoh: \u4E00\u65A4\u82F9\u679C\u4E24\u5757\u4E94, beli 4 \u65A4, bayar \xA520')]);
      break;
    case 2:
      var tmp9_steps = listOf_0(['\u539F\u4EF7 = harga asli \xA5300', '\u6253\u516B\u6298 \u2192 bayar 8/10', '300 \xD7 8 \xF7 10 = \xA5240', 'Potongan = 300 \u2212 240 = \xA560']);
      var tmp_5 = new LessonRule('Rumus \u6253\u6298', 'Angka di depan \u6298 menunjukkan berapa persepuluh yang dibayar. Dua digit (X\u4E94\u6298) berarti per seratus.', VOID, VOID, '\u6253X\u6298 \u2192 Bayar = Harga \xD7 X \xF7 10\n\u6253XY\u6298 \u2192 Bayar = Harga \xD7 XY \xF7 100\nPotongan = Harga \u2212 Bayar', tmp9_steps, 'Contoh: \u539F\u4EF7\u4E09\u767E\u5757\uFF0C\u6253\u516B\u6298');
      var tmp10_steps = listOf_0(['100 \u2212 25 = 75 (persen yang dibayar)', '75 \u2192 dua digit, dibaca \u4E03\u4E94', 'Hasil: \u6253\u4E03\u4E94\u6298']);
      var tmp_6 = new LessonRule('Mengubah Diskon Persen ke \u6298', 'Kalau toko bilang diskon sekian persen, kurangkan dari 100 dulu.', VOID, VOID, '\u6298 = (100 \u2212 diskon%) \xF7 10', tmp10_steps, 'Contoh: diskon 25%');
      var tmp11_steps = listOf_0(['Batas minimal A = \xA5200, potongan B = \xA530', '250 \u2265 200, jadi dapat potongan', 'Bayar = 250 \u2212 30 = \xA5220']);
      tmp = listOf_0([tmp_5, tmp_6, new LessonRule('Rumus \u6EE1\u51CF', 'Potongan hanya berlaku kalau total belanja mencapai batas minimal.', VOID, VOID, '\u6EE1A\u51CFB:\nKalau Total \u2265 A \u2192 Bayar = Total \u2212 B\nKalau Total < A \u2192 Bayar = Total', tmp11_steps, 'Contoh: \u6EE1\u4E24\u767E\u51CF\u4E09\u5341, belanja \xA5250')]);
      break;
    case 3:
      var tmp12_steps = listOf_0(['\u767E\u5206\u4E4B \u2192 persen', '\u56DB\u5341\u4E94 \u2192 45', 'Hasil: 45%']);
      var tmp_7 = new LessonRule('Rumus Persen', '"Per seratus" (\u767E\u5206\u4E4B) diucapkan lebih dulu, angkanya di belakang.', VOID, VOID, '\u767E\u5206\u4E4B N = N%', tmp12_steps, 'Contoh: \u767E\u5206\u4E4B\u56DB\u5341\u4E94');
      var tmp13_steps = listOf_0(['Di depan \u5206\u4E4B: \u4E94 \u2192 penyebut 5', 'Di belakang \u5206\u4E4B: \u4E8C \u2192 pembilang 2', 'Hasil: 2/5']);
      var tmp_8 = new LessonRule('Rumus Pecahan', '\u5206\u4E4B berarti "dari bagian". Yang di depan \u5206\u4E4B adalah penyebut (jumlah bagian), yang di belakang adalah pembilang.', VOID, VOID, 'B \u5206\u4E4B A = A/B\nB = penyebut \xB7 A = pembilang', tmp13_steps, 'Contoh: \u4E94\u5206\u4E4B\u4E8C');
      var tmp14_steps = listOf_0(['\u5341\u4E8C \u2192 12', '\u70B9 \u2192 koma', '\u96F6\u4E94 \u2192 digit 0 lalu 5', 'Hasil: 12,05']);
      tmp = listOf_0([tmp_7, tmp_8, new LessonRule('Rumus Desimal', 'Bagian bulat dibaca seperti angka biasa. Setelah \u70B9 (koma), setiap digit dibaca satu per satu, termasuk nol.', VOID, VOID, 'A \u70B9 B C D = A,BCD\nB, C, D dibaca per digit', tmp14_steps, 'Contoh: \u5341\u4E8C\u70B9\u96F6\u4E94')]);
      break;
    case 4:
      var tmp15_steps = listOf_0(['Jam 7 \u2192 \u4E03\u70B9', 'Menit 5 < 10 \u2192 \u96F6\u4E94\u5206', 'Hasil: ' + ChineseNumber_getInstance().me(7, 5).je()]);
      var tmp_9 = new LessonRule('Rumus Jam', 'Jam disebut dulu dengan \u70B9, lalu menitnya dengan \u5206. Menit 1\u20139 diberi \u96F6. Menit :15, :30, :45 punya sebutan khusus.', VOID, VOID, '[jam]\u70B9 [menit]\u5206\n:15 = \u4E00\u523B \xB7 :30 = \u534A \xB7 :45 = \u4E09\u523B', tmp15_steps, 'Contoh: 7:05');
      var tmp16_steps = listOf_0(['H = 8, N = 10', 'Jam = 8 \u2212 1 = 7', 'Menit = 60 \u2212 10 = 50', 'Hasil: 7:50']);
      var tmp_10 = new LessonRule('Rumus \u5DEE (Kurang)', '\u5DEE menyebut berapa menit lagi menuju jam berikutnya. Untuk mengubahnya, kurangi jamnya 1 dan menitnya dari 60.', VOID, VOID, '\u5DEE N \u5206 H \u70B9 = (H \u2212 1) : (60 \u2212 N)\n\u5DEE\u4E00\u523B H \u70B9 = (H \u2212 1) : 45', tmp16_steps, 'Contoh: \u5DEE\u5341\u5206\u516B\u70B9');
      var tmp17_steps = listOf_0(['\u665A\u4E0A \u2192 malam, jadi tambah 12', '\u516B\u70B9\u534A \u2192 8:30', '8 + 12 = 20', 'Hasil: 20:30']);
      tmp = listOf_0([tmp_9, tmp_10, new LessonRule('Rumus Format 24 Jam', 'Bahasa Mandarin sehari-hari memakai jam 1\u201312 dengan keterangan waktu. Untuk siang/sore/malam, tambahkan 12.', VOID, VOID, '\u4E0A\u5348 / \u51CC\u6668 H\u70B9 = H:00\n\u4E0B\u5348 / \u665A\u4E0A H\u70B9 = (H + 12):00\n\u4E2D\u5348\u5341\u4E8C\u70B9 = 12:00', tmp17_steps, 'Contoh: \u665A\u4E0A\u516B\u70B9\u534A')]);
      break;
    case 5:
      var tmp18_steps = listOf_0(['1945 \u2192 per digit: \u4E00\u4E5D\u56DB\u4E94\u5E74', 'Agustus = bulan ke-8 \u2192 \u516B\u6708', 'Tanggal 17 \u2192 \u5341\u4E03\u53F7', 'Hasil: ' + ChineseNumber_getInstance().ne(1945, 8, 17).je()]);
      var tmp_11 = new LessonRule('Rumus Tanggal', 'Urutannya dari satuan terbesar ke terkecil: tahun, bulan, tanggal. Tahun dibaca per digit, bulan dan tanggal dibaca sebagai angka biasa.', VOID, VOID, '[tahun per digit]\u5E74 [bulan]\u6708 [tanggal]\u53F7', tmp18_steps, 'Contoh: 17 Agustus 1945');
      var tmp_12 = new LessonRule('Rumus Hari', 'Hari dihitung mulai Senin sebagai hari ke-1.', VOID, VOID, '\u661F\u671F N = hari ke-N (Senin = 1 \u2026 Sabtu = 6)\nMinggu = \u661F\u671F\u5929 / \u661F\u671F\u65E5');
      var tmp19_steps = listOf_0(['Hari ini \u661F\u671F\u4E94 = Jumat (hari ke-5)', '\u540E\u5929 = +2 hari', '5 + 2 = 7 \u2192 Minggu', 'Hasil: \u661F\u671F\u5929']);
      tmp = listOf_0([tmp_11, tmp_12, new LessonRule('Menghitung Hari', 'Untuk pertanyaan seperti "\u540E\u5929\u662F\u661F\u671F\u51E0\uFF1F", tambahkan selisih harinya ke hari ini.', VOID, VOID, '\u524D\u5929 = \u22122 \xB7 \u6628\u5929 = \u22121 \xB7 \u660E\u5929 = +1 \xB7 \u540E\u5929 = +2 \xB7 \u5927\u540E\u5929 = +3', tmp19_steps, 'Contoh: \u4ECA\u5929\u661F\u671F\u4E94\uFF0C\u540E\u5929\u662F\u661F\u671F\u51E0\uFF1F')]);
      break;
    case 6:
      var tmp20_steps = listOf_0(['Jeda: 138 | 0013 | 8000', '138 \u2192 ' + ChineseNumber_getInstance().fe('138', true, '\u96F6').ke(), '0013 \u2192 ' + ChineseNumber_getInstance().fe('0013', true, '\u96F6').ke(), '8000 \u2192 ' + ChineseNumber_getInstance().fe('8000', true, '\u96F6').ke()]);
      tmp = listOf(new LessonRule('Rumus Nomor', 'Setiap digit dibaca sendiri-sendiri. Angka 1 dibaca \u5E7A (y\u0101o). Nomor ponsel 11 digit biasanya dijeda 3-4-4.', VOID, VOID, 'Digit demi digit \xB7 1 = \u5E7A (y\u0101o) \xB7 0 = \u96F6', tmp20_steps, 'Contoh: 138 0013 8000'));
      break;
    case 7:
      var tmp21_steps = listOf_0(['2 sebelum kata satuan \u2192 \u4E24', 'Kata satuan buku \u2192 \u672C', 'Buku \u2192 \u4E66', 'Hasil: \u4E24\u672C\u4E66 (li\u01CEng b\u011Bn sh\u016B)']);
      var tmp_13 = new LessonRule('Rumus Kata Satuan', 'Di antara angka dan benda harus ada kata satuan. Angka 2 berubah menjadi \u4E24.', VOID, VOID, '[angka] + [kata satuan] + [benda]', tmp21_steps, 'Contoh: 2 buku');
      var tmp22_steps = listOf_0(['1 meter \u2192 \u4E00\u7C73', '68 \u2192 per digit: \u516D\u516B', 'Hasil: \u4E00\u7C73\u516D\u516B (y\xEC m\u01D0 li\xF9 b\u0101)']);
      var tmp_14 = new LessonRule('Rumus Tinggi Badan', 'Sebut meternya dengan \u7C73, lalu angka sentimeternya per digit tanpa satuan.', VOID, VOID, '\u4E00\u7C73 [d1][d2] = 1,d1d2 m', tmp22_steps, 'Contoh: 1,68 m');
      var tmp23_steps = listOf_0(['\u516D\u65A4 = 6 \u65A4', '6 \xF7 2 = 3', 'Hasil: 3 kg']);
      tmp = listOf_0([tmp_13, tmp_14, new LessonRule('Rumus \u65A4 dan Kilogram', '1 \u65A4 = 500 gram, jadi setengah kilogram.', VOID, VOID, 'kg = \u65A4 \xF7 2\n\u65A4 = kg \xD7 2', tmp23_steps, 'Contoh: \u516D\u65A4')]);
      break;
    case 8:
      var tmp24_steps = listOf_0(['\u4E8C\u5341 = 20', '\u591A = lebih', 'Hasil: lebih dari 20 orang (biasanya 21\u201329)']);
      tmp = listOf(new LessonRule('Rumus Perkiraan', 'Ada tiga pola utama untuk jumlah yang tidak pasti.', VOID, VOID, 'N\u591A = sedikit lebih dari N\nA B (berurutan) = A atau B\n\u51E0 + satuan = beberapa satuan', tmp24_steps, 'Contoh: \u4E8C\u5341\u591A\u4E2A\u4EBA'));
      break;
    default:
      noWhenBranchMatchedException();
      break;
  }
  return tmp;
}
function traps($this, cat) {
  switch (cat.p2_1) {
    case 0:
    case 9:
      return listOf(new LessonRule('Jebakan yang Sering Salah', 'Bandingkan pasangan berikut baik-baik.', listOf_0([to(ChineseNumber_getInstance().xd(105), '105 (pakai \u96F6)'), to(sp($this, [to('\u4E00', 'y\xEC'), to('\u767E', 'b\u01CEi'), to('\u4E94', 'w\u01D4')]), '150 dalam bahasa lisan!'), to(ChineseNumber_getInstance().xd(10000), '10.000 (bukan \u5341\u5343)'), to(ChineseNumber_getInstance().xd(2000), '2.000 (bukan \u4E8C\u5343)')])));
    case 1:
      return listOf(new LessonRule('Jebakan yang Sering Salah', 'Satu huruf bisa mengubah harga.', listOf_0([to(ChineseNumber_getInstance().ie(1, 5, 0), '\xA51,50'), to(ChineseNumber_getInstance().ie(1, 0, 5), '\xA51,05'), to(ChineseNumber_getInstance().ie(2, 0, 0), '\xA52 (bukan \u4E8C\u5757)')])));
    case 2:
      return listOf(new LessonRule('Jebakan yang Sering Salah', 'Jangan tertukar antara yang dibayar dan potongannya.', listOf_0([to(sp($this, [to('\u6253', 'd\u01CE'), to('\u4E09', 's\u0101n'), to('\u6298', 'zh\xE9')]), 'Bayar 30% (diskon 70%)'), to(sp($this, [to('\u6253', 'd\u01CE'), to('\u4E03', 'q\u012B'), to('\u6298', 'zh\xE9')]), 'Bayar 70% (diskon 30%)')])));
    case 4:
      return listOf(new LessonRule('Jebakan yang Sering Salah', '\u5DEE menghitung mundur dari jam berikutnya.', listOf_0([to(ChineseNumber_getInstance().oe(9, 45, 2), '9:45 (bukan 10:45)'), to(ChineseNumber_getInstance().me(2, 0), '2:00 (bukan \u4E8C\u70B9)')])));
    default:
      return emptyList();
  }
}
var NumberLessons_instance;
function NumberLessons_getInstance() {
  if (NumberLessons_instance === VOID)
    new NumberLessons();
  return NumberLessons_instance;
}
var NumCat_UMUM_instance;
var NumCat_HARGA_instance;
var NumCat_DISKON_instance;
var NumCat_PERSEN_instance;
var NumCat_JAM_instance;
var NumCat_TANGGAL_instance;
var NumCat_TELEPON_instance;
var NumCat_UKURAN_instance;
var NumCat_KIRA_instance;
var NumCat_CAMPUR_instance;
function values() {
  return [NumCat_UMUM_getInstance(), NumCat_HARGA_getInstance(), NumCat_DISKON_getInstance(), NumCat_PERSEN_getInstance(), NumCat_JAM_getInstance(), NumCat_TANGGAL_getInstance(), NumCat_TELEPON_getInstance(), NumCat_UKURAN_getInstance(), NumCat_KIRA_getInstance(), NumCat_CAMPUR_getInstance()];
}
function get_entries() {
  if ($ENTRIES == null)
    $ENTRIES = enumEntries(values());
  return $ENTRIES;
}
var NumCat_entriesInitialized;
function NumCat_initEntries() {
  if (NumCat_entriesInitialized)
    return Unit_instance;
  NumCat_entriesInitialized = true;
  NumCat_UMUM_instance = new NumCat('UMUM', 0, 'Angka Umum', '0 sampai 100 juta', '\uD83D\uDD22');
  NumCat_HARGA_instance = new NumCat('HARGA', 1, 'Harga', '\u5757, \u6BDB, \u5206 dan \u5143, \u89D2', '\uD83D\uDCB4');
  NumCat_DISKON_instance = new NumCat('DISKON', 2, 'Diskon', '\u6253\u516B\u6298, \u534A\u4EF7, \u6EE1\u51CF', '\uD83C\uDFF7\uFE0F');
  NumCat_PERSEN_instance = new NumCat('PERSEN', 3, 'Persen & Pecahan', '\u767E\u5206\u4E4B, \u5206\u4E4B, \u70B9', '\u2797');
  NumCat_JAM_instance = new NumCat('JAM', 4, 'Jam', '\u70B9, \u523B, \u534A, \u5DEE', '\u23F0');
  NumCat_TANGGAL_instance = new NumCat('TANGGAL', 5, 'Tanggal & Hari', '\u5E74, \u6708, \u53F7, \u661F\u671F', '\uD83D\uDCC5');
  NumCat_TELEPON_instance = new NumCat('TELEPON', 6, 'Telepon & Kamar', '\u5E7A dan dibaca per digit', '\uD83D\uDCDE');
  NumCat_UKURAN_instance = new NumCat('UKURAN', 7, 'Ukuran Sehari-hari', '\u5C81, \u7C73, \u65A4, \u697C, \u4E2A', '\uD83D\uDCCF');
  NumCat_KIRA_instance = new NumCat('KIRA', 8, 'Kira-kira', '\u591A, \u51E0, \u4E8C\u4E09\u5341', '\uD83E\uDD14');
  NumCat_CAMPUR_instance = new NumCat('CAMPUR', 9, 'Campuran', 'Semua kategori diacak', '\uD83C\uDFB2');
}
var $ENTRIES;
var Keys_NUMBER_instance;
var Keys_DECIMAL_instance;
var Keys_TIME_instance;
var Keys_DATE_instance;
var Keys_FRACTION_instance;
var Keys_PERCENT_instance;
var Keys_entriesInitialized;
function Keys_initEntries() {
  if (Keys_entriesInitialized)
    return Unit_instance;
  Keys_entriesInitialized = true;
  Keys_NUMBER_instance = new Keys('NUMBER', 0, '');
  Keys_DECIMAL_instance = new Keys('DECIMAL', 1, ',');
  Keys_TIME_instance = new Keys('TIME', 2, ':');
  Keys_DATE_instance = new Keys('DATE', 3, '/');
  Keys_FRACTION_instance = new Keys('FRACTION', 4, '/');
  Keys_PERCENT_instance = new Keys('PERCENT', 5, ',');
}
function dec($this, v) {
  return $this.cg(replace(ChineseNumber_getInstance().vf(v), '.', ''));
}
function choice($this, correct, wrong) {
  // Inline function 'kotlin.collections.distinctBy' call
  // Inline function 'kotlin.collections.filter' call
  // Inline function 'kotlin.collections.filterTo' call
  var destination = ArrayList.h1();
  var tmp0_iterator = wrong.r();
  while (tmp0_iterator.s()) {
    var element = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.data.NumberQuiz.choice.<anonymous>' call
    if (!(element.ef_1 === correct.ef_1)) {
      destination.h(element);
    }
  }
  var set = HashSet.f7();
  var list = ArrayList.h1();
  var tmp0_iterator_0 = destination.r();
  while (tmp0_iterator_0.s()) {
    var e = tmp0_iterator_0.t();
    // Inline function 'com.nesxha.readit.data.NumberQuiz.choice.<anonymous>' call
    var key = e.ef_1;
    if (set.h(key)) {
      list.h(e);
    }
  }
  var w = take(shuffled(list, $this.wf_1), 3);
  var all = shuffled(plus_0(w, correct), $this.wf_1);
  var tmp$ret$6;
  $l$block: {
    // Inline function 'kotlin.collections.indexOfFirst' call
    var index = 0;
    var tmp0_iterator_1 = all.r();
    while (tmp0_iterator_1.s()) {
      var item = tmp0_iterator_1.t();
      // Inline function 'com.nesxha.readit.data.NumberQuiz.choice.<anonymous>' call
      if (item.ef_1 === correct.ef_1) {
        tmp$ret$6 = index;
        break $l$block;
      }
      index = index + 1 | 0;
    }
    tmp$ret$6 = -1;
  }
  return to(all, tmp$ret$6);
}
function zh($this, sp) {
  return new NumOption(sp.je(), sp.ke(), true);
}
function tricky($this, lo, hi) {
  // Inline function 'kotlin.repeat' call
  // Inline function 'kotlin.contracts.contract' call
  var inductionVariable = 0;
  if (inductionVariable < 300)
    do {
      var index = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      // Inline function 'com.nesxha.readit.data.NumberQuiz.tricky.<anonymous>' call
      var tmp = NumberQuiz_getInstance().wf_1;
      var tmp_0 = toLong(lo.toString().length);
      // Inline function 'kotlin.Long.plus' call
      var tmp$ret$0 = toLong(hi.toString().length).v2(toLong(1));
      var len = tmp.hb(tmp_0, tmp$ret$0).h3();
      var sb = StringBuilder.e();
      var inductionVariable_0 = 0;
      if (inductionVariable_0 < len)
        do {
          var i = inductionVariable_0;
          inductionVariable_0 = inductionVariable_0 + 1 | 0;
          sb.z9(i === 0 ? NumberQuiz_getInstance().wf_1.gb(1, 10) : NumberQuiz_getInstance().wf_1.jb() < 0.4 ? 0 : NumberQuiz_getInstance().wf_1.gb(1, 10));
        }
         while (inductionVariable_0 < len);
      var n = toLong_0(sb.toString());
      if (lo.p1(n) <= 0 ? n.p1(hi) <= 0 : false)
        return n;
    }
     while (inductionVariable < 300);
  // Inline function 'kotlin.Long.plus' call
  var tmp$ret$1 = hi.v2(toLong(1));
  return $this.wf_1.hb(lo, tmp$ret$1);
}
function inRange($this, r) {
  var lo = coerceAtLeast(r.fc_1, new Long(0, 0));
  var hi = coerceAtMost(r.gc_1, new Long(-727379969, 232));
  if (hi.w2(lo).p1(new Long(1000, 0)) < 0 || $this.wf_1.c1(3) === 0) {
    // Inline function 'kotlin.Long.plus' call
    var tmp$ret$0 = hi.v2(toLong(1));
    return $this.wf_1.hb(lo, tmp$ret$0);
  }
  // Inline function 'kotlin.comparisons.maxOf' call
  var b = new Long(1, 0);
  var tmp$ret$1 = lo.p1(b) >= 0 ? lo : b;
  return tricky($this, tmp$ret$1, hi);
}
function umumRange($this, d) {
  switch (d) {
    case 0:
      return $this.wf_1.ib() ? $this.wf_1.hb(new Long(0, 0), new Long(11, 0)) : $this.wf_1.hb(new Long(11, 0), new Long(100, 0));
    case 1:
      return tricky($this, new Long(100, 0), new Long(9999, 0));
    default:
      return tricky($this, new Long(10000, 0), new Long(100000000, 0));
  }
}
function umumExplain($this, n, sp) {
  var h = sp.je();
  var parts = ArrayList.h1();
  if (n.p1(new Long(10000, 0)) >= 0) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element = 'Mandarin menghitung per 4 digit: \u4E07 = 10.000 dan \u4EBF = 100.000.000. Jadi ' + ChineseNumber_getInstance().dg(n) + ' = ' + h + '.';
    parts.h(element);
  }
  if (contains_1(h, _Char___init__impl__6a9atx(38646))) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_0 = '\u96F6 dipakai untuk digit kosong di tengah angka, cukup satu kali walau nolnya lebih dari satu.';
    parts.h(element_0);
  }
  if (contains_1(h, _Char___init__impl__6a9atx(20004))) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_1 = 'Angka 2 sebelum \u5343, \u4E07, \u4EBF (dan \u767E di awal) dibaca \u4E24, bukan \u4E8C.';
    parts.h(element_1);
  }
  if (startsWith(h, '\u5341')) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_2 = '10\u201319 di awal angka cukup ditulis \u5341\u2026, tanpa \u4E00.';
    parts.h(element_2);
  }
  if (contains_2(h, '\u4E00\u5341')) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_3 = 'Di tengah angka, belasan ditulis lengkap: \u4E00\u5341.';
    parts.h(element_3);
  }
  if (parts.b1()) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_4 = ChineseNumber_getInstance().dg(n) + ' dibaca ' + sp.ke() + '.';
    parts.h(element_4);
  }
  return joinToString_0(parts, ' ');
}
function umumWrong($this, n, sp) {
  var w = ArrayList.h1();
  var h = sp.je();
  if (contains_1(h, _Char___init__impl__6a9atx(38646))) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element = new NumOption(replace(h, '\u96F6', ''), VOID, true);
    w.h(element);
  }
  var tmp;
  if (n.p1(new Long(10000, 0)) >= 0) {
    // Inline function 'kotlin.Long.rem' call
    tmp = n.z2(toLong(1000)).equals(new Long(0, 0));
  } else {
    tmp = false;
  }
  if (tmp) {
    // Inline function 'kotlin.collections.plusAssign' call
    var tmp_0 = ChineseNumber_getInstance();
    // Inline function 'kotlin.Long.div' call
    var tmp$ret$1 = n.y2(toLong(1000));
    var element_0 = new NumOption(tmp_0.he(tmp$ret$1).je() + '\u5343', VOID, true);
    w.h(element_0);
  }
  if (contains_2(h, '\u4E00\u5341')) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_1 = new NumOption(replaceFirst(h, '\u4E00\u5341', '\u5341'), VOID, true);
    w.h(element_1);
  }
  if (contains_2(h, '\u4E8C\u5341')) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_2 = new NumOption(replaceFirst(h, '\u4E8C\u5341', '\u4E24\u5341'), VOID, true);
    w.h(element_2);
  }
  var tmp_1;
  if (n.p1(new Long(10, 0)) >= 0) {
    // Inline function 'kotlin.Long.times' call
    tmp_1 = n.x2(toLong(10)).p1(new Long(-727379969, 232)) <= 0;
  } else {
    tmp_1 = false;
  }
  if (tmp_1) {
    // Inline function 'kotlin.collections.plusAssign' call
    var tmp_2 = ChineseNumber_getInstance();
    // Inline function 'kotlin.Long.times' call
    var tmp$ret$3 = n.x2(toLong(10));
    var element_3 = zh($this, tmp_2.he(tmp$ret$3));
    w.h(element_3);
  }
  var tmp_3;
  // Inline function 'kotlin.Long.rem' call
  if (n.z2(toLong(10)).equals(new Long(0, 0))) {
    tmp_3 = n.p1(new Long(100, 0)) >= 0;
  } else {
    tmp_3 = false;
  }
  if (tmp_3) {
    // Inline function 'kotlin.collections.plusAssign' call
    var tmp_4 = ChineseNumber_getInstance();
    // Inline function 'kotlin.Long.div' call
    var tmp$ret$5 = n.y2(toLong(10));
    var element_4 = zh($this, tmp_4.he(tmp$ret$5));
    w.h(element_4);
  }
  // Inline function 'kotlin.repeat' call
  // Inline function 'kotlin.contracts.contract' call
  var inductionVariable = 0;
  if (inductionVariable < 6)
    do {
      var index = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      // Inline function 'com.nesxha.readit.data.NumberQuiz.umumWrong.<anonymous>' call
      var s = toCharArray(n.toString());
      var idx = NumberQuiz_getInstance().wf_1.c1(s.length);
      s[idx] = Char__plus_impl_qi7pgj(_Char___init__impl__6a9atx(48), (Char__minus_impl_a2frrh(s[idx], _Char___init__impl__6a9atx(48)) + NumberQuiz_getInstance().wf_1.gb(1, 9) | 0) % 10 | 0);
      if (s[0] === _Char___init__impl__6a9atx(48)) {
        s[0] = _Char___init__impl__6a9atx(49);
      }
      var m = toLong_0(concatToString(s));
      if (!m.equals(n)) {
        // Inline function 'kotlin.collections.plusAssign' call
        var element_5 = zh(NumberQuiz_getInstance(), ChineseNumber_getInstance().he(m));
        w.h(element_5);
      }
    }
     while (inductionVariable < 6);
  return w;
}
function umum($this, d, range) {
  var n = !(range == null) ? inRange($this, range) : umumRange($this, d);
  var sp = ChineseNumber_getInstance().he(n);
  var tmp;
  if ($this.wf_1.ib()) {
    tmp = new NumQuestion(NumCat_UMUM_getInstance(), 'Tulis angka ini dengan digit', sp.je(), sp.ke(), VOID, VOID, VOID, setOf(n.toString()), ChineseNumber_getInstance().dg(n), VOID, VOID, umumExplain($this, n, sp));
  } else {
    var _destruct__k2r9zo = choice($this, zh($this, sp), umumWrong($this, n, sp));
    var opts = _destruct__k2r9zo.cd();
    var ans = _destruct__k2r9zo.dd();
    tmp = new NumQuestion(NumCat_UMUM_getInstance(), 'Pilih tulisan Mandarin yang benar', VOID, VOID, ChineseNumber_getInstance().dg(n), opts, ans, VOID, sp.je(), VOID, VOID, umumExplain($this, n, sp));
  }
  return tmp;
}
function umum$default($this, d, range, $super) {
  range = range === VOID ? null : range;
  return umum($this, d, range);
}
function money($this, d) {
  var tmp;
  switch (d) {
    case 0:
      tmp = new Triple($this.wf_1.gb(1, 100), $this.wf_1.c1(3) === 0 ? 5 : 0, 0);
      break;
    case 1:
      tmp = $this.wf_1.c1(3) === 0 ? new Triple($this.wf_1.gb(1, 200), 0, $this.wf_1.gb(1, 10)) : new Triple($this.wf_1.gb(1, 200), $this.wf_1.gb(1, 10), 0);
      break;
    default:
      // Inline function 'kotlin.let' call

      // Inline function 'kotlin.contracts.contract' call

      // Inline function 'com.nesxha.readit.data.NumberQuiz.money.<anonymous>' call

      var it = new Triple($this.wf_1.gb(0, 1000), $this.wf_1.gb(0, 10), $this.wf_1.gb(0, 10));
      tmp = ((it.fd_1 + it.gd_1 | 0) + it.hd_1 | 0) === 0 ? new Triple(1, 5, 0) : it;
      break;
  }
  return tmp;
}
function yuanText($this, y, j, f) {
  return '\xA5' + ChineseNumber_getInstance().vf(y + j / 10.0 + f / 100.0);
}
function harga($this, d) {
  if (d >= 1 && $this.wf_1.c1(3) === 0)
    return hargaStory($this, d);
  var _destruct__k2r9zo = money($this, d);
  var y = _destruct__k2r9zo.cd();
  var j = _destruct__k2r9zo.dd();
  var f = _destruct__k2r9zo.id();
  var written = d === 2 && $this.wf_1.ib();
  var sp = written ? ChineseNumber_getInstance().le(y, j, f) : ChineseNumber_getInstance().ie(y, j, f);
  var value = y + j / 10.0 + f / 100.0;
  // Inline function 'kotlin.text.trim' call
  // Inline function 'kotlin.text.buildString' call
  // Inline function 'kotlin.contracts.contract' call
  // Inline function 'kotlin.apply' call
  var this_0 = StringBuilder.e();
  // Inline function 'kotlin.contracts.contract' call
  // Inline function 'com.nesxha.readit.data.NumberQuiz.harga.<anonymous>' call
  this_0.n8(written ? 'Di label harga tertulis: \u5143 = yuan, \u89D2 = 0,1 yuan, \u5206 = 0,01 yuan. ' : 'Lisan: \u5757 = yuan, \u6BDB = 0,1 yuan, \u5206 = 0,01 yuan. ');
  if (!written && j > 0 && f === 0 && y > 0) {
    this_0.n8('Satuan terakhir (\u6BDB) boleh dihilangkan: ' + sp.je() + ' = ' + yuanText(NumberQuiz_getInstance(), y, j, f) + '. ');
  }
  if (!written && j === 0 && f > 0 && y > 0) {
    this_0.n8('Ada \u96F6 karena \u6BDB-nya kosong. ');
  }
  if (startsWith(sp.je(), '\u4E24')) {
    this_0.n8('2 sebelum kata satuan dibaca \u4E24, bukan \u4E8C. ');
  }
  var this_1 = this_0.toString();
  var explain = toString_1(trim(isCharSequence(this_1) ? this_1 : THROW_CCE()));
  var tmp;
  if ($this.wf_1.ib()) {
    tmp = new NumQuestion(NumCat_HARGA_getInstance(), 'Berapa harganya dalam yuan?', sp.je(), sp.ke(), VOID, VOID, VOID, setOf(dec($this, value)), yuanText($this, y, j, f), Keys_DECIMAL_getInstance(), 'yuan', explain);
  } else {
    var wrong = ArrayList.h1();
    if (j > 0 && f === 0 && y > 0) {
      // Inline function 'kotlin.collections.plusAssign' call
      var element = zh($this, ChineseNumber_getInstance().ie(y, 0, j));
      wrong.h(element);
    }
    if (j === 0 && f > 0 && y > 0) {
      // Inline function 'kotlin.collections.plusAssign' call
      var element_0 = zh($this, ChineseNumber_getInstance().ie(y, f, 0));
      wrong.h(element_0);
    }
    if (y === 2) {
      // Inline function 'kotlin.collections.plusAssign' call
      var element_1 = new NumOption(replaceFirst(sp.je(), '\u4E24', '\u4E8C'), VOID, true);
      wrong.h(element_1);
    }
    // Inline function 'kotlin.collections.plusAssign' call
    var element_2 = zh($this, ChineseNumber_getInstance().ie(y + $this.wf_1.gb(1, 5) | 0, j, f));
    wrong.h(element_2);
    // Inline function 'kotlin.collections.plusAssign' call
    var tmp_0 = ChineseNumber_getInstance();
    // Inline function 'kotlin.comparisons.maxOf' call
    var b = imul_0(y, 10) % 1000 | 0;
    var tmp$ret$3 = Math.max(1, b);
    var element_3 = zh($this, tmp_0.ie(tmp$ret$3, j, f));
    wrong.h(element_3);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_4 = zh($this, ChineseNumber_getInstance().ie(y, (j + $this.wf_1.gb(1, 9) | 0) % 10 | 0, f));
    wrong.h(element_4);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_5 = zh($this, ChineseNumber_getInstance().ie(y, j, (f + $this.wf_1.gb(1, 9) | 0) % 10 | 0));
    wrong.h(element_5);
    var _destruct__k2r9zo_0 = choice($this, zh($this, ChineseNumber_getInstance().ie(y, j, f)), wrong);
    var opts = _destruct__k2r9zo_0.cd();
    var ans = _destruct__k2r9zo_0.dd();
    tmp = new NumQuestion(NumCat_HARGA_getInstance(), 'Bagaimana harga ini diucapkan?', VOID, VOID, yuanText($this, y, j, f), opts, ans, VOID, ChineseNumber_getInstance().ie(y, j, f).je(), VOID, VOID, explain);
  }
  return tmp;
}
function hargaStory($this, d) {
  var it = random($this.zf_1, $this.wf_1);
  var prices = listOf_0([new Triple(1, 5, 0), new Triple(2, 0, 0), new Triple(2, 5, 0), new Triple(3, 5, 0), new Triple(4, 5, 0), new Triple(6, 0, 0), new Triple(8, 0, 0), new Triple(12, 5, 0), new Triple(18, 0, 0), new Triple(25, 0, 0)]);
  var _destruct__k2r9zo = random(prices, $this.wf_1);
  var y = _destruct__k2r9zo.cd();
  var j = _destruct__k2r9zo.dd();
  var f = _destruct__k2r9zo.id();
  var price = y + j / 10.0;
  var tmp;
  if ($this.wf_1.ib()) {
    var k = $this.wf_1.gb(2, 7);
    var tmp_0 = Companion_getInstance_14().rd([to('\u4E00', it.hg_1 === '\u4E2A' ? 'y\xED' : 'y\xEC'), to(it.hg_1, it.ig_1)]);
    // Inline function 'kotlin.collections.map' call
    var this_0 = zip(chunked(it.eg_1, 1), split_0(it.fg_1, [' ']));
    // Inline function 'kotlin.collections.mapTo' call
    var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
    var tmp0_iterator = this_0.r();
    while (tmp0_iterator.s()) {
      var item = tmp0_iterator.t();
      // Inline function 'com.nesxha.readit.data.NumberQuiz.hargaStory.<anonymous>' call
      var h = item.cd();
      var p = item.dd();
      var tmp$ret$0 = new Syl(h, p);
      destination.h(tmp$ret$0);
    }
    var sp = tmp_0.ae(new Spoken(destination)).ae(ChineseNumber_getInstance().ie(y, j, f));
    var tmp0_cat = NumCat_HARGA_getInstance();
    var tmp1_instruction = 'Harga per ' + it.jg_1 + ' ' + it.gg_1 + ' seperti di atas. Kamu beli ' + k + ' ' + it.jg_1 + '. Total bayar berapa yuan?';
    var tmp2_hanzi = sp.je();
    var tmp3_pinyin = sp.ke();
    var tmp4_accepted = setOf(dec($this, price * k));
    var tmp5_answerText = '\xA5' + ChineseNumber_getInstance().vf(price * k);
    var tmp6_keys = Keys_DECIMAL_getInstance();
    var tmp7_explain = sp.je() + ' = \xA5' + ChineseNumber_getInstance().vf(price) + ' per ' + it.jg_1 + '. \xA5' + ChineseNumber_getInstance().vf(price) + ' \xD7 ' + k + ' = \xA5' + ChineseNumber_getInstance().vf(price * k) + '.';
    tmp = new NumQuestion(tmp0_cat, tmp1_instruction, tmp2_hanzi, tmp3_pinyin, VOID, VOID, VOID, tmp4_accepted, tmp5_answerText, tmp6_keys, 'yuan', tmp7_explain, true);
  } else {
    var priceCents = (imul_0(y, 100) + imul_0(j, 10) | 0) + f | 0;
    var tmp$ret$4;
    $l$block: {
      // Inline function 'kotlin.collections.first' call
      var tmp0_iterator_0 = listOf_0([10, 20, 50, 100]).r();
      while (tmp0_iterator_0.s()) {
        var element = tmp0_iterator_0.t();
        // Inline function 'com.nesxha.readit.data.NumberQuiz.hargaStory.<anonymous>' call
        if (imul_0(element, 100) > priceCents) {
          tmp$ret$4 = element;
          break $l$block;
        }
      }
      throw NoSuchElementException.l('Collection contains no element matching the predicate.');
    }
    var pay = tmp$ret$4;
    var changeCents = imul_0(pay, 100) - priceCents | 0;
    var change = changeCents / 100.0;
    var prompt = ChineseNumber_getInstance().ie(y, j, f);
    var say = Companion_getInstance_14().rd([to('\u627E', 'zh\u01CEo'), to('\u4F60', 'n\u01D0')]).ae(ChineseNumber_getInstance().ie(changeCents / 100 | 0, (changeCents / 10 | 0) % 10 | 0, changeCents % 10 | 0));
    var tmp8_cat = NumCat_HARGA_getInstance();
    var tmp9_instruction = 'Harga ' + it.gg_1 + ' tertulis di atas. Kamu membayar dengan uang \xA5' + pay + '. Berapa kembaliannya (yuan)?';
    var tmp10_hanzi = prompt.je();
    var tmp11_pinyin = prompt.ke();
    var tmp12_accepted = setOf(dec($this, change));
    var tmp13_answerText = '\xA5' + ChineseNumber_getInstance().vf(change);
    var tmp14_keys = Keys_DECIMAL_getInstance();
    var tmp15_explain = prompt.je() + ' = \xA5' + ChineseNumber_getInstance().vf(price) + '. \xA5' + pay + ' \u2212 \xA5' + ChineseNumber_getInstance().vf(price) + ' = \xA5' + ChineseNumber_getInstance().vf(change) + '. Penjual akan bilang: ' + say.je() + ' (' + say.ke() + ').';
    tmp = new NumQuestion(tmp8_cat, tmp9_instruction, tmp10_hanzi, tmp11_pinyin, VOID, VOID, VOID, tmp12_accepted, tmp13_answerText, tmp14_keys, 'yuan', tmp15_explain, true);
  }
  return tmp;
}
function zhe($this, x) {
  return x < 10 ? Companion_getInstance_14().rd([to('\u6253', 'd\u01CE')]).ae(new Spoken(listOf(ChineseNumber_getInstance().yd(x)))).ae(Companion_getInstance_14().rd([to('\u6298', 'zh\xE9')])) : Companion_getInstance_14().rd([to('\u6253', 'd\u01CE')]).ae(new Spoken(listOf_0([ChineseNumber_getInstance().yd(x / 10 | 0), ChineseNumber_getInstance().yd(x % 10 | 0)]))).ae(Companion_getInstance_14().rd([to('\u6298', 'zh\xE9')]));
}
function payPct($this, x) {
  return x < 10 ? imul_0(x, 10) : x;
}
function diskon($this, d) {
  var pick = $this.wf_1.c1(d === 0 ? 2 : 4);
  var x = d === 0 ? $this.wf_1.gb(5, 10) : $this.wf_1.ib() ? $this.wf_1.gb(5, 10) : random(listOf_0([75, 85, 95, 65]), $this.wf_1);
  var sp = zhe($this, x);
  var pct = payPct($this, x);
  var explain = sp.je() + ' artinya kamu membayar ' + pct + '% dari harga asli, jadi potongannya ' + (100 - pct | 0) + '%. Angka di depan \u6298 adalah yang DIBAYAR, bukan potongannya.';
  var tmp;
  if (d >= 2 && $this.wf_1.c1(3) === 0) {
    tmp = manJian($this);
  } else if (pick === 0) {
    tmp = new NumQuestion(NumCat_DISKON_getInstance(), 'Dengan diskon ini, kamu membayar berapa persen dari harga asli?', sp.je(), sp.ke(), VOID, VOID, VOID, setOf(pct.toString()), '' + pct + '%', Keys_PERCENT_getInstance(), '%', explain);
  } else if (pick === 1) {
    var wrong = listOf_0([new NumOption(zhe($this, x < 10 ? 10 - x | 0 : 100 - x | 0).je(), VOID, true), new NumOption('\u6253' + ChineseNumber_getInstance().xd(pct).je() + '\u6298', VOID, true), new NumOption(zhe($this, x < 10 ? (x % 9 | 0) + 1 | 0 : x - 10 | 0).je(), VOID, true), new NumOption(ChineseNumber_getInstance().xd(100 - pct | 0).je() + '\u6298\u6253', VOID, true)]);
    var _destruct__k2r9zo = choice($this, zh($this, sp), wrong);
    var opts = _destruct__k2r9zo.cd();
    var ans = _destruct__k2r9zo.dd();
    tmp = new NumQuestion(NumCat_DISKON_getInstance(), 'Toko memberi potongan ' + (100 - pct | 0) + '%. Tulisan di toko yang benar?', VOID, VOID, '\u2212' + (100 - pct | 0) + '%', opts, ans, VOID, sp.je(), VOID, VOID, explain);
  } else {
    var price = imul_0($this.wf_1.gb(2, 21), 20);
    var paid = imul_0(price, pct) / 100 | 0;
    var tag = Companion_getInstance_14().rd([to('\u539F', 'yu\xE1n'), to('\u4EF7', 'ji\xE0')]).ae(ChineseNumber_getInstance().ue(price, '\u5757', 'ku\xE0i', 4)).ae(Companion_getInstance_14().rd([to('\uFF0C', ',')])).ae(sp);
    var tmp0_cat = NumCat_DISKON_getInstance();
    var tmp1_hanzi = tag.je();
    var tmp2_pinyin = replace(tag.ke(), ' , ', ', ');
    var tmp3_accepted = setOf(paid.toString());
    var tmp4_answerText = '\xA5' + paid;
    var tmp5_keys = Keys_NUMBER_getInstance();
    var tmp6_explain = '\u539F\u4EF7 = harga asli \xA5' + price + '. ' + sp.je() + ' = bayar ' + pct + '%. \xA5' + price + ' \xD7 ' + pct + '% = \xA5' + paid + '.';
    tmp = new NumQuestion(tmp0_cat, 'Berapa yang harus kamu bayar (yuan)?', tmp1_hanzi, tmp2_pinyin, VOID, VOID, VOID, tmp3_accepted, tmp4_answerText, tmp5_keys, 'yuan', tmp6_explain, true);
  }
  return tmp;
}
function manJian($this) {
  var min = random(listOf_0([100, 200, 300, 500]), $this.wf_1);
  var cut = min / random(listOf_0([5, 4, 10]), $this.wf_1) | 0;
  var spend = $this.wf_1.c1(4) === 0 ? min - imul_0($this.wf_1.gb(1, 5), 10) | 0 : min + imul_0($this.wf_1.gb(0, 9), 10) | 0;
  var pay = spend >= min ? spend - cut | 0 : spend;
  var sp = Companion_getInstance_14().rd([to('\u6EE1', 'm\u01CEn')]).ae(ChineseNumber_getInstance().xd(min)).ae(Companion_getInstance_14().rd([to('\u51CF', 'ji\u01CEn')])).ae(ChineseNumber_getInstance().xd(cut));
  var tmp0_cat = NumCat_DISKON_getInstance();
  var tmp1_instruction = 'Promo ini berlaku di toko. Kamu belanja \xA5' + spend + '. Berapa yang dibayar (yuan)?';
  var tmp2_hanzi = sp.je();
  var tmp3_pinyin = sp.ke();
  var tmp4_accepted = setOf(pay.toString());
  var tmp5_answerText = '\xA5' + pay;
  var tmp6_explain = sp.je() + ': belanja minimal \xA5' + min + ' dapat potongan \xA5' + cut + '. ' + (spend >= min ? '\xA5' + spend + ' \u2265 \xA5' + min + ', jadi bayar \xA5' + spend + ' \u2212 \xA5' + cut + ' = \xA5' + pay + '.' : '\xA5' + spend + ' belum mencapai \xA5' + min + ', jadi tidak dapat potongan: bayar \xA5' + spend + '.');
  return new NumQuestion(tmp0_cat, tmp1_instruction, tmp2_hanzi, tmp3_pinyin, VOID, VOID, VOID, tmp4_accepted, tmp5_answerText, VOID, 'yuan', tmp6_explain, true);
}
function persen($this, d) {
  var tmp;
  switch ($this.wf_1.c1(3)) {
    case 0:
      var _destruct__k2r9zo;
      switch (d) {
        case 0:
          _destruct__k2r9zo = to(toLong(imul_0($this.wf_1.gb(1, 10), 10)), '');
          break;
        case 1:
          _destruct__k2r9zo = to($this.wf_1.hb(new Long(1, 0), new Long(101, 0)), '');
          break;
        default:
          _destruct__k2r9zo = $this.wf_1.ib() ? to(new Long(0, 0), '' + $this.wf_1.gb(1, 10)) : to($this.wf_1.hb(new Long(1, 0), new Long(100, 0)), '5');
          break;
      }

      var i = _destruct__k2r9zo.cd();
      var fr = _destruct__k2r9zo.dd();
      var sp = ChineseNumber_getInstance().re(i, fr);
      var tmp_0;
      // Inline function 'kotlin.text.isEmpty' call

      if (charSequenceLength(fr) === 0) {
        tmp_0 = i.toString() + '%';
      } else {
        tmp_0 = i.toString() + ',' + fr + '%';
      }

      var shown = tmp_0;
      var explain = '\u767E\u5206\u4E4B = "per seratus". Urutannya terbalik dari bahasa Indonesia: \u767E\u5206\u4E4B dulu, baru angkanya. ' + sp.je() + ' = ' + shown + '.';
      var tmp_1;
      if ($this.wf_1.ib()) {
        tmp_1 = new NumQuestion(NumCat_PERSEN_getInstance(), 'Tulis sebagai persen', sp.je(), sp.ke(), VOID, VOID, VOID, setOf($this.cg(shown)), shown, Keys_PERCENT_getInstance(), '%', explain);
      } else {
        var tmp_2 = new NumOption(ChineseNumber_getInstance().de(i, fr).je() + '\u5206\u4E4B\u767E', VOID, true);
        var tmp_3 = new NumOption('\u767E\u5206' + ChineseNumber_getInstance().de(i, fr).je(), VOID, true);
        var tmp_4 = ChineseNumber_getInstance();
        var tmp_5;
        if (i.p1(new Long(1, 0)) > 0) {
          // Inline function 'kotlin.Long.plus' call
          // Inline function 'kotlin.Long.div' call
          tmp_5 = i.y2(toLong(10)).v2(toLong(1));
        } else {
          // Inline function 'kotlin.Long.plus' call
          tmp_5 = i.v2(toLong(1));
        }
        var tmp_6 = zh($this, tmp_4.re(tmp_5, fr));
        var tmp_7 = ChineseNumber_getInstance();
        // Inline function 'kotlin.Long.times' call
        var tmp$ret$4 = i.x2(toLong(10));
        var wrong = listOf_0([tmp_2, tmp_3, tmp_6, zh($this, tmp_7.re(coerceAtMost(tmp$ret$4, new Long(999, 0)), ''))]);
        var _destruct__k2r9zo_0 = choice($this, zh($this, sp), wrong);
        var opts = _destruct__k2r9zo_0.cd();
        var ans = _destruct__k2r9zo_0.dd();
        tmp_1 = new NumQuestion(NumCat_PERSEN_getInstance(), 'Bagaimana persen ini dibaca?', VOID, VOID, shown, opts, ans, VOID, sp.je(), VOID, VOID, explain);
      }

      tmp = tmp_1;
      break;
    case 1:
      var den;
      switch (d) {
        case 0:
          den = random(listOf_0([2, 3, 4]), $this.wf_1);
          break;
        case 1:
          den = $this.wf_1.gb(2, 11);
          break;
        default:
          den = $this.wf_1.gb(5, 21);
          break;
      }

      var num = $this.wf_1.gb(1, den);
      var sp_0 = ChineseNumber_getInstance().be(num, den);
      var explain_0 = 'Pecahan dibaca PENYEBUT dulu: ' + ChineseNumber_getInstance().xd(den).je() + '\u5206\u4E4B' + ChineseNumber_getInstance().xd(num).je() + ' = ' + num + '/' + den + ' ("' + num + ' dari ' + den + ' bagian").';
      var tmp_8;
      if ($this.wf_1.ib()) {
        tmp_8 = new NumQuestion(NumCat_PERSEN_getInstance(), 'Tulis sebagai pecahan (pembilang/penyebut)', sp_0.je(), sp_0.ke(), VOID, VOID, VOID, setOf('' + num + '/' + den), '' + num + '/' + den, Keys_FRACTION_getInstance(), VOID, explain_0);
      } else {
        var tmp_9 = zh($this, ChineseNumber_getInstance().be(den, num));
        var tmp_10 = zh($this, ChineseNumber_getInstance().be(num, den + 1 | 0));
        var tmp_11 = ChineseNumber_getInstance();
        // Inline function 'kotlin.comparisons.maxOf' call
        var b = (num + 1 | 0) % den | 0;
        var tmp$ret$5 = Math.max(1, b);
        var wrong_0 = listOf_0([tmp_9, tmp_10, zh($this, tmp_11.be(tmp$ret$5, den)), new NumOption(ChineseNumber_getInstance().xd(num).je() + '\u5206\u4E4B' + ChineseNumber_getInstance().xd(den).je(), VOID, true)]);
        var _destruct__k2r9zo_1 = choice($this, zh($this, sp_0), wrong_0);
        var opts_0 = _destruct__k2r9zo_1.cd();
        var ans_0 = _destruct__k2r9zo_1.dd();
        tmp_8 = new NumQuestion(NumCat_PERSEN_getInstance(), 'Bagaimana pecahan ini dibaca?', VOID, VOID, '' + num + '/' + den, opts_0, ans_0, VOID, sp_0.je(), VOID, VOID, explain_0);
      }

      tmp = tmp_8;
      break;
    default:
      var i_0;
      switch (d) {
        case 0:
          i_0 = $this.wf_1.hb(new Long(0, 0), new Long(10, 0));
          break;
        case 1:
          i_0 = $this.wf_1.hb(new Long(0, 0), new Long(100, 0));
          break;
        default:
          i_0 = $this.wf_1.hb(new Long(0, 0), new Long(1000, 0));
          break;
      }

      var fr_0;
      switch (d) {
        case 0:
          fr_0 = '' + $this.wf_1.gb(1, 10);
          break;
        case 1:
          fr_0 = '' + $this.wf_1.gb(1, 10) + $this.wf_1.gb(1, 10);
          break;
        default:
          fr_0 = random(listOf_0(['0' + $this.wf_1.gb(1, 10), '' + $this.wf_1.gb(1, 10) + $this.wf_1.gb(0, 10) + $this.wf_1.gb(1, 10)]), $this.wf_1);
          break;
      }

      var sp_1 = ChineseNumber_getInstance().de(i_0, fr_0);
      var shown_0 = ChineseNumber_getInstance().dg(i_0) + ',' + fr_0;
      var explain_1 = 'Koma desimal dibaca \u70B9. Angka di belakang \u70B9 dibaca satu per satu: ' + sp_1.je() + ' = ' + shown_0 + '.';
      var tmp_12;
      if ($this.wf_1.ib()) {
        tmp_12 = new NumQuestion(NumCat_PERSEN_getInstance(), 'Tulis sebagai bilangan desimal', sp_1.je(), sp_1.ke(), VOID, VOID, VOID, setOf($this.cg(shown_0)), shown_0, Keys_DECIMAL_getInstance(), VOID, explain_1);
      } else {
        var tmp5_elvis_lhs = toLongOrNull(trimStart(fr_0, charArrayOf([_Char___init__impl__6a9atx(48)])));
        var frNum = tmp5_elvis_lhs == null ? new Long(0, 0) : tmp5_elvis_lhs;
        var tmp_13 = new NumOption(ChineseNumber_getInstance().he(i_0).je() + '\u70B9' + ChineseNumber_getInstance().he(frNum).je(), VOID, true);
        var tmp_14 = ChineseNumber_getInstance();
        // Inline function 'kotlin.text.reversed' call
        var this_0 = fr_0;
        var tmp$ret$6 = toString_1(reversed(isCharSequence(this_0) ? this_0 : THROW_CCE()));
        var tmp_15 = zh($this, tmp_14.de(i_0, tmp$ret$6));
        var tmp_16 = ChineseNumber_getInstance();
        // Inline function 'kotlin.Long.plus' call
        var tmp$ret$7 = i_0.v2(toLong(1));
        var wrong_1 = listOf_0([tmp_13, tmp_15, zh($this, tmp_16.de(tmp$ret$7, fr_0)), new NumOption(ChineseNumber_getInstance().he(frNum).je() + '\u70B9' + ChineseNumber_getInstance().he(i_0).je(), VOID, true)]);
        var _destruct__k2r9zo_2 = choice($this, zh($this, sp_1), wrong_1);
        var opts_1 = _destruct__k2r9zo_2.cd();
        var ans_1 = _destruct__k2r9zo_2.dd();
        tmp_12 = new NumQuestion(NumCat_PERSEN_getInstance(), 'Bagaimana bilangan ini dibaca?', VOID, VOID, shown_0, opts_1, ans_1, VOID, sp_1.je(), VOID, VOID, explain_1);
      }

      tmp = tmp_12;
      break;
  }
  return tmp;
}
function periodOf($this, h24) {
  return (1 <= h24 ? h24 <= 5 : false) ? to('\u51CC\u6668', 'l\xEDng ch\xE9n') : (6 <= h24 ? h24 <= 11 : false) ? to('\u4E0A\u5348', 'sh\xE0ng w\u01D4') : h24 === 12 ? to('\u4E2D\u5348', 'zh\u014Dng w\u01D4') : (13 <= h24 ? h24 <= 17 : false) ? to('\u4E0B\u5348', 'xi\xE0 w\u01D4') : to('\u665A\u4E0A', 'w\u01CEn shang');
}
function styleFor($this, m, prefer) {
  return prefer === 1 && setOf_0([15, 30, 45]).h2(m) ? 1 : prefer === 2 && (m === 45 || m >= 50) ? 2 : 0;
}
function jam($this, d) {
  var h = $this.wf_1.gb(1, 13);
  var m;
  switch (d) {
    case 0:
      m = random(listOf_0([0, 30]), $this.wf_1);
      break;
    case 1:
      m = random(listOf_0([15, 45, $this.wf_1.gb(1, 10), $this.wf_1.gb(10, 60)]), $this.wf_1);
      break;
    default:
      m = random(listOf_0([45, $this.wf_1.gb(50, 60), $this.wf_1.gb(1, 60)]), $this.wf_1);
      break;
  }
  var style;
  switch (d) {
    case 0:
      style = styleFor($this, m, 1);
      break;
    case 1:
      style = styleFor($this, m, 1);
      break;
    default:
      style = styleFor($this, m, 2);
      break;
  }
  var base = ChineseNumber_getInstance().oe(h, m, style);
  var tmp;
  if (d === 2 && !(style === 2) && $this.wf_1.ib()) {
    tmp = h === 12 ? 12 : $this.wf_1.ib() ? h : h + 12 | 0;
  } else {
    tmp = null;
  }
  var h24 = tmp;
  var tmp_0;
  if (!(h24 == null)) {
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.nesxha.readit.data.NumberQuiz.jam.<anonymous>' call
    var destruct = periodOf($this, h24);
    var ph = destruct.cd();
    var pp = destruct.dd();
    // Inline function 'kotlin.collections.map' call
    var this_0 = zip(chunked(ph, 1), split_0(pp, [' ']));
    // Inline function 'kotlin.collections.mapTo' call
    var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
    var tmp0_iterator = this_0.r();
    while (tmp0_iterator.s()) {
      var item = tmp0_iterator.t();
      // Inline function 'com.nesxha.readit.data.NumberQuiz.jam.<anonymous>.<anonymous>' call
      var a = item.cd();
      var b = item.dd();
      var tmp$ret$0 = new Syl(a, b);
      destination.h(tmp$ret$0);
    }
    tmp_0 = (new Spoken(destination)).ae(base);
  } else {
    tmp_0 = base;
  }
  var sp = tmp_0;
  var mm = padStart(m.toString(), 2, _Char___init__impl__6a9atx(48));
  var text = '' + (h24 == null ? h : h24) + ':' + mm;
  // Inline function 'kotlin.text.trim' call
  // Inline function 'kotlin.text.buildString' call
  // Inline function 'kotlin.contracts.contract' call
  // Inline function 'kotlin.apply' call
  var this_1 = StringBuilder.e();
  // Inline function 'kotlin.contracts.contract' call
  // Inline function 'com.nesxha.readit.data.NumberQuiz.jam.<anonymous>' call
  this_1.n8('\u70B9 = jam. ');
  if (contains_2(base.je(), '\u523B')) {
    this_1.n8('\u523B = 15 menit (\u4E00\u523B = :15, \u4E09\u523B = :45). ');
  }
  if (contains_2(base.je(), '\u534A')) {
    this_1.n8('\u534A = setengah, :30. ');
  }
  if (startsWith(base.je(), '\u5DEE')) {
    this_1.n8('\u5DEE = kurang: ' + base.je() + ' artinya kurang ' + (60 - m | 0) + ' menit menuju jam ' + ((h % 12 | 0) + 1 | 0) + ', jadi ' + h + ':' + mm + '. ');
  }
  if (style === 0 && (1 <= m ? m <= 9 : false)) {
    this_1.n8('Menit 1\u20139 diberi \u96F6 di depannya. ');
  }
  if (contains_2(base.je(), '\u4E24\u70B9')) {
    this_1.n8('Jam 2 dibaca \u4E24\u70B9, bukan \u4E8C\u70B9. ');
  }
  if (!(h24 == null)) {
    this_1.n8(periodOf(NumberQuiz_getInstance(), h24).ad_1 + ' = ' + periodName(NumberQuiz_getInstance(), periodOf(NumberQuiz_getInstance(), h24).ad_1) + ', jadi dalam format 24 jam = ' + text + '.');
  }
  var this_2 = this_1.toString();
  var explain = toString_1(trim(isCharSequence(this_2) ? this_2 : THROW_CCE()));
  var tmp_1;
  if ($this.wf_1.ib()) {
    tmp_1 = new NumQuestion(NumCat_JAM_getInstance(), !(h24 == null) ? 'Tulis jamnya dalam format 24 jam (jam:menit)' : 'Tulis jamnya (jam:menit)', sp.je(), sp.ke(), VOID, VOID, VOID, setOf($this.cg(text)), text, Keys_TIME_getInstance(), VOID, explain);
  } else {
    var others = listOf_0([to((h % 12 | 0) + 1 | 0, m), to(h === 1 ? 12 : h - 1 | 0, m), to(h, (m + 15 | 0) % 60 | 0), to(h, (m + 30 | 0) % 60 | 0)]);
    // Inline function 'kotlin.collections.map' call
    // Inline function 'kotlin.collections.filter' call
    // Inline function 'kotlin.collections.filterTo' call
    var destination_0 = ArrayList.h1();
    var tmp0_iterator_0 = others.r();
    while (tmp0_iterator_0.s()) {
      var element = tmp0_iterator_0.t();
      // Inline function 'com.nesxha.readit.data.NumberQuiz.jam.<anonymous>' call
      if (!element.equals(to(h, m))) {
        destination_0.h(element);
      }
    }
    // Inline function 'kotlin.collections.mapTo' call
    var destination_1 = ArrayList.z(collectionSizeOrDefault(destination_0, 10));
    var tmp0_iterator_1 = destination_0.r();
    while (tmp0_iterator_1.s()) {
      var item_0 = tmp0_iterator_1.t();
      // Inline function 'com.nesxha.readit.data.NumberQuiz.jam.<anonymous>' call
      var oh = item_0.cd();
      var om = item_0.dd();
      var tmp$ret$11 = zh(NumberQuiz_getInstance(), ChineseNumber_getInstance().oe(oh, om, styleFor(NumberQuiz_getInstance(), om, style)));
      destination_1.h(tmp$ret$11);
    }
    var wrong = toMutableList(destination_1);
    if (h === 2) {
      // Inline function 'kotlin.collections.plusAssign' call
      var element_0 = new NumOption(replace(base.je(), '\u4E24\u70B9', '\u4E8C\u70B9'), VOID, true);
      wrong.h(element_0);
    }
    var _destruct__k2r9zo = choice($this, zh($this, base), wrong);
    var opts = _destruct__k2r9zo.cd();
    var ans = _destruct__k2r9zo.dd();
    tmp_1 = new NumQuestion(NumCat_JAM_getInstance(), 'Bagaimana jam ini diucapkan?', VOID, VOID, '' + h + ':' + mm, opts, ans, VOID, base.je(), VOID, VOID, explain);
  }
  return tmp_1;
}
function periodName($this, p) {
  switch (p) {
    case '\u51CC\u6668':
      return 'dini hari';
    case '\u4E0A\u5348':
      return 'pagi';
    case '\u4E2D\u5348':
      return 'siang (sekitar jam 12)';
    case '\u4E0B\u5348':
      return 'sore/siang';
    default:
      return 'malam';
  }
}
function tanggal($this, d) {
  switch (d) {
    case 0:
      var tmp;
      if ($this.wf_1.ib()) {
        var w = $this.wf_1.gb(1, 8);
        var sp = ChineseNumber_getInstance().kg(w, $this.wf_1.ib() ? '\u5929' : '\u65E5');
        var tmp_0 = new NumOption($this.bg_1.d1(w - 1 | 0));
        // Inline function 'kotlin.collections.map' call
        // Inline function 'kotlin.collections.filterIndexed' call
        // Inline function 'kotlin.collections.filterIndexedTo' call
        var this_0 = $this.bg_1;
        var destination = ArrayList.h1();
        // Inline function 'kotlin.collections.forEachIndexed' call
        var index = 0;
        var tmp0_iterator = this_0.r();
        while (tmp0_iterator.s()) {
          var item = tmp0_iterator.t();
          // Inline function 'kotlin.collections.filterIndexedTo.<anonymous>' call
          var tmp1 = index;
          index = tmp1 + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberQuiz.tanggal.<anonymous>' call
          if (!(checkIndexOverflow(tmp1) === (w - 1 | 0))) {
            destination.h(item);
          }
        }
        // Inline function 'kotlin.collections.mapTo' call
        var destination_0 = ArrayList.z(collectionSizeOrDefault(destination, 10));
        var tmp0_iterator_0 = destination.r();
        while (tmp0_iterator_0.s()) {
          var item_0 = tmp0_iterator_0.t();
          // Inline function 'com.nesxha.readit.data.NumberQuiz.tanggal.<anonymous>' call
          var tmp$ret$3 = new NumOption(item_0);
          destination_0.h(tmp$ret$3);
        }
        var _destruct__k2r9zo = choice($this, tmp_0, destination_0);
        var opts = _destruct__k2r9zo.cd();
        var ans = _destruct__k2r9zo.dd();
        tmp = new NumQuestion(NumCat_TANGGAL_getInstance(), 'Hari apa ini?', sp.je(), sp.ke(), VOID, opts, ans, VOID, $this.bg_1.d1(w - 1 | 0), VOID, VOID, '\u661F\u671F + angka: \u661F\u671F\u4E00 = Senin \u2026 \u661F\u671F\u516D = Sabtu. Minggu bukan \u661F\u671F\u4E03, tetapi \u661F\u671F\u5929 atau \u661F\u671F\u65E5.');
      } else {
        var mo = $this.wf_1.gb(1, 13);
        var sp_0 = ChineseNumber_getInstance().xd(mo).ae(Companion_getInstance_14().rd([to('\u6708', 'yu\xE8')]));
        var tmp_1 = new NumOption($this.ag_1.d1(mo - 1 | 0));
        // Inline function 'kotlin.collections.map' call
        // Inline function 'kotlin.collections.filterIndexed' call
        // Inline function 'kotlin.collections.filterIndexedTo' call
        var this_1 = $this.ag_1;
        var destination_1 = ArrayList.h1();
        // Inline function 'kotlin.collections.forEachIndexed' call
        var index_0 = 0;
        var tmp0_iterator_1 = this_1.r();
        while (tmp0_iterator_1.s()) {
          var item_1 = tmp0_iterator_1.t();
          // Inline function 'kotlin.collections.filterIndexedTo.<anonymous>' call
          var tmp1_0 = index_0;
          index_0 = tmp1_0 + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberQuiz.tanggal.<anonymous>' call
          if (!(checkIndexOverflow(tmp1_0) === (mo - 1 | 0))) {
            destination_1.h(item_1);
          }
        }
        // Inline function 'kotlin.collections.mapTo' call
        var destination_2 = ArrayList.z(collectionSizeOrDefault(destination_1, 10));
        var tmp0_iterator_2 = destination_1.r();
        while (tmp0_iterator_2.s()) {
          var item_2 = tmp0_iterator_2.t();
          // Inline function 'com.nesxha.readit.data.NumberQuiz.tanggal.<anonymous>' call
          var tmp$ret$9 = new NumOption(item_2);
          destination_2.h(tmp$ret$9);
        }
        var _destruct__k2r9zo_0 = choice($this, tmp_1, destination_2);
        var opts_0 = _destruct__k2r9zo_0.cd();
        var ans_0 = _destruct__k2r9zo_0.dd();
        tmp = new NumQuestion(NumCat_TANGGAL_getInstance(), 'Bulan apa ini?', sp_0.je(), sp_0.ke(), VOID, opts_0, ans_0, VOID, $this.ag_1.d1(mo - 1 | 0), VOID, VOID, 'Nama bulan = angka + \u6708: \u4E00\u6708 = Januari, \u5341\u4E8C\u6708 = Desember.');
      }

      return tmp;
    case 1:
      var y = $this.wf_1.gb(1990, 2036);
      var mo_0 = $this.wf_1.gb(1, 13);
      var day = $this.wf_1.gb(1, 29);
      var sp_1 = ChineseNumber_getInstance().se(y, mo_0, day, $this.wf_1.ib());
      var text = '' + day + '/' + mo_0 + '/' + y;
      var explain = 'Urutan tanggal Mandarin: tahun \u2192 bulan \u2192 tanggal. Tahun dibaca per digit (' + ChineseNumber_getInstance().te(y.toString()).je() + '\u5E74). \u53F7 dipakai saat bicara, \u65E5 saat menulis.';
      var tmp_2;
      if ($this.wf_1.ib()) {
        tmp_2 = new NumQuestion(NumCat_TANGGAL_getInstance(), 'Tulis tanggalnya (tanggal/bulan/tahun)', sp_1.je(), sp_1.ke(), VOID, VOID, VOID, setOf($this.cg(text)), text, Keys_DATE_getInstance(), VOID, explain);
      } else {
        // Inline function 'kotlin.takeIf' call
        var this_2 = zh($this, ChineseNumber_getInstance().ne(y, coerceAtMost_0(day, 12), mo_0));
        // Inline function 'kotlin.contracts.contract' call
        var tmp_3;
        // Inline function 'com.nesxha.readit.data.NumberQuiz.tanggal.<anonymous>' call
        if (day <= 12 && !(day === mo_0)) {
          tmp_3 = this_2;
        } else {
          tmp_3 = null;
        }
        var tmp1_elvis_lhs = tmp_3;
        var wrong = listOf_0([tmp1_elvis_lhs == null ? zh($this, ChineseNumber_getInstance().ne(y, (mo_0 % 12 | 0) + 1 | 0, day)) : tmp1_elvis_lhs, new NumOption(ChineseNumber_getInstance().xd(y).je() + '\u5E74' + ChineseNumber_getInstance().xd(mo_0).je() + '\u6708' + ChineseNumber_getInstance().xd(day).je() + '\u53F7', VOID, true), new NumOption(ChineseNumber_getInstance().xd(mo_0).je() + '\u6708' + ChineseNumber_getInstance().xd(day).je() + '\u53F7' + ChineseNumber_getInstance().te(y.toString()).je() + '\u5E74', VOID, true), zh($this, ChineseNumber_getInstance().ne(y + 1 | 0, mo_0, day))]);
        var _destruct__k2r9zo_1 = choice($this, zh($this, ChineseNumber_getInstance().ne(y, mo_0, day)), wrong);
        var opts_1 = _destruct__k2r9zo_1.cd();
        var ans_1 = _destruct__k2r9zo_1.dd();
        tmp_2 = new NumQuestion(NumCat_TANGGAL_getInstance(), 'Bagaimana tanggal ini diucapkan?', VOID, VOID, '' + day + ' ' + $this.ag_1.d1(mo_0 - 1 | 0) + ' ' + y, opts_1, ans_1, VOID, ChineseNumber_getInstance().ne(y, mo_0, day).je(), VOID, VOID, explain);
      }

      return tmp_2;
    default:
      var rel = random(listOf_0([new Triple('\u524D\u5929', 'qi\xE1n ti\u0101n', -2), new Triple('\u6628\u5929', 'zu\xF3 ti\u0101n', -1), new Triple('\u660E\u5929', 'm\xEDng ti\u0101n', 1), new Triple('\u540E\u5929', 'h\xF2u ti\u0101n', 2), new Triple('\u5927\u540E\u5929', 'd\xE0 h\xF2u ti\u0101n', 3)]), $this.wf_1);
      var today = $this.wf_1.gb(1, 8);
      var target = (((((today - 1 | 0) + rel.hd_1 | 0) % 7 | 0) + 7 | 0) % 7 | 0) + 1 | 0;
      var tmp_4 = Companion_getInstance_14().rd([to('\u4ECA', 'j\u012Bn'), to('\u5929', 'ti\u0101n'), to('\u662F', 'sh\xEC')]).ae(ChineseNumber_getInstance().ee(today)).ae(Companion_getInstance_14().rd([to('\uFF0C', ',')]));
      // Inline function 'kotlin.collections.map' call

      var this_3 = zip(chunked(rel.fd_1, 1), split_0(rel.gd_1, [' ']));
      // Inline function 'kotlin.collections.mapTo' call

      var destination_3 = ArrayList.z(collectionSizeOrDefault(this_3, 10));
      var tmp0_iterator_3 = this_3.r();
      while (tmp0_iterator_3.s()) {
        var item_3 = tmp0_iterator_3.t();
        // Inline function 'com.nesxha.readit.data.NumberQuiz.tanggal.<anonymous>' call
        var h = item_3.cd();
        var p = item_3.dd();
        var tmp$ret$14 = new Syl(h, p);
        destination_3.h(tmp$ret$14);
      }

      var sp_2 = tmp_4.ae(new Spoken(destination_3)).ae(Companion_getInstance_14().rd([to('\u662F', 'sh\xEC'), to('\u661F', 'x\u012Bng'), to('\u671F', 'q\u012B'), to('\u51E0', 'j\u01D0'), to('\uFF1F', '?')]));
      var correct = zh($this, ChineseNumber_getInstance().ee(target));
      // Inline function 'kotlin.collections.map' call

      // Inline function 'kotlin.collections.filter' call

      // Inline function 'kotlin.collections.filterTo' call

      var this_4 = numberRangeToNumber(1, 7);
      var destination_4 = ArrayList.h1();
      var inductionVariable = this_4.xb_1;
      var last = this_4.yb_1;
      if (inductionVariable <= last)
        do {
          var element = inductionVariable;
          inductionVariable = inductionVariable + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberQuiz.tanggal.<anonymous>' call
          if (!(element === target)) {
            destination_4.h(element);
          }
        }
         while (!(element === last));
      // Inline function 'kotlin.collections.mapTo' call

      var destination_5 = ArrayList.z(collectionSizeOrDefault(destination_4, 10));
      var tmp0_iterator_4 = destination_4.r();
      while (tmp0_iterator_4.s()) {
        var item_4 = tmp0_iterator_4.t();
        // Inline function 'com.nesxha.readit.data.NumberQuiz.tanggal.<anonymous>' call
        var tmp$ret$20 = zh(NumberQuiz_getInstance(), ChineseNumber_getInstance().ee(item_4));
        destination_5.h(tmp$ret$20);
      }

      var wrong_0 = destination_5;
      var _destruct__k2r9zo_2 = choice($this, correct, wrong_0);
      var opts_2 = _destruct__k2r9zo_2.cd();
      var ans_2 = _destruct__k2r9zo_2.dd();
      return new NumQuestion(NumCat_TANGGAL_getInstance(), 'Jawab pertanyaannya', sp_2.je(), replace(replace(sp_2.ke(), ' , ', ', '), ' ?', '?'), VOID, opts_2, ans_2, VOID, correct.ef_1, VOID, VOID, '\u524D\u5929 = 2 hari lalu, \u6628\u5929 = kemarin, \u660E\u5929 = besok, \u540E\u5929 = lusa, \u5927\u540E\u5929 = 3 hari lagi. Hari ini ' + $this.bg_1.d1(today - 1 | 0) + ', jadi ' + rel.fd_1 + ' = ' + $this.bg_1.d1(target - 1 | 0) + ' (' + correct.ef_1 + ').');
  }
}
function telepon($this, d) {
  if (d === 2 && $this.wf_1.c1(3) === 0) {
    var _destruct__k2r9zo = random(listOf_0([to('110', 'Polisi'), to('119', 'Pemadam kebakaran'), to('120', 'Ambulans')]), $this.wf_1);
    var num = _destruct__k2r9zo.cd();
    var what = _destruct__k2r9zo.dd();
    var sp = ChineseNumber_getInstance().fe(num, true, '\u96F6');
    var tmp = new NumOption(what);
    // Inline function 'kotlin.collections.map' call
    var this_0 = listOf_0(['Polisi', 'Pemadam kebakaran', 'Ambulans', 'Informasi telepon (114)']);
    // Inline function 'kotlin.collections.mapTo' call
    var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
    var tmp0_iterator = this_0.r();
    while (tmp0_iterator.s()) {
      var item = tmp0_iterator.t();
      // Inline function 'com.nesxha.readit.data.NumberQuiz.telepon.<anonymous>' call
      var tmp$ret$0 = new NumOption(item);
      destination.h(tmp$ret$0);
    }
    var _destruct__k2r9zo_0 = choice($this, tmp, destination);
    var opts = _destruct__k2r9zo_0.cd();
    var ans = _destruct__k2r9zo_0.dd();
    return new NumQuestion(NumCat_TELEPON_getInstance(), 'Nomor darurat ini untuk apa?', sp.je(), sp.ke(), VOID, opts, ans, VOID, what, VOID, VOID, 'Nomor darurat di Tiongkok: 110 (\u5E7A\u5E7A\u96F6) polisi, 119 (\u5E7A\u5E7A\u4E5D) pemadam kebakaran, 120 (\u5E7A\u4E8C\u96F6) ambulans.');
  }
  var phone = d >= 1 && $this.wf_1.ib();
  var tmp_0;
  if (phone) {
    var tmp_1 = '1' + random(listOf_0(['3', '5', '8']), $this.wf_1);
    // Inline function 'kotlin.collections.map' call
    var this_1 = numberRangeToNumber(1, 9);
    // Inline function 'kotlin.collections.mapTo' call
    var destination_0 = ArrayList.z(collectionSizeOrDefault(this_1, 10));
    var inductionVariable = this_1.xb_1;
    var last = this_1.yb_1;
    if (inductionVariable <= last)
      do {
        var item_0 = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        // Inline function 'com.nesxha.readit.data.NumberQuiz.telepon.<anonymous>' call
        var tmp$ret$3 = NumberQuiz_getInstance().wf_1.gb(0, 10);
        destination_0.h(tmp$ret$3);
      }
       while (!(item_0 === last));
    tmp_0 = tmp_1 + joinToString_0(destination_0, '');
  } else {
    tmp_0 = '' + $this.wf_1.gb(1, 10) + '0' + $this.wf_1.gb(1, 10);
  }
  var number = tmp_0;
  var sp_0 = phone ? ChineseNumber_getInstance().fe(number, true, '\u96F6') : ChineseNumber_getInstance().te(number, VOID, '\u3007');
  var tmp_2;
  if (phone) {
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp_3 = number.substring(0, 3);
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp_4 = number.substring(3, 7);
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    tmp_2 = tmp_3 + ' ' + tmp_4 + ' ' + number.substring(7);
  } else {
    tmp_2 = number;
  }
  var shown = tmp_2;
  var explain = phone ? 'Nomor telepon dibaca per digit, dan angka 1 dibaca \u5E7A (y\u0101o) supaya tidak tertukar dengan \u4E03 (q\u012B).' : 'Nomor kamar, bus, dan tahun dibaca per digit: ' + sp_0.je() + ' = ' + number + ', bukan ' + ChineseNumber_getInstance().he(toLong_0(number)).je() + '.';
  var tmp_5;
  if ($this.wf_1.ib()) {
    var tmp_6 = NumCat_TELEPON_getInstance();
    var tmp_7 = phone ? 'Tulis nomor teleponnya' : 'Tulis nomor kamarnya';
    var tmp_8 = sp_0.je() + (phone ? '' : '\u623F\u95F4');
    var tmp_9 = sp_0.ke() + (phone ? '' : ' f\xE1ng ji\u0101n');
    // Inline function 'kotlin.text.ifEmpty' call
    var this_2 = trimStart(number, charArrayOf([_Char___init__impl__6a9atx(48)]));
    // Inline function 'kotlin.contracts.contract' call
    var tmp_10;
    // Inline function 'kotlin.text.isEmpty' call
    if (charSequenceLength(this_2) === 0) {
      // Inline function 'com.nesxha.readit.data.NumberQuiz.telepon.<anonymous>' call
      tmp_10 = '0';
    } else {
      tmp_10 = this_2;
    }
    var tmp$ret$14 = tmp_10;
    tmp_5 = new NumQuestion(tmp_6, tmp_7, tmp_8, tmp_9, VOID, VOID, VOID, setOf(tmp$ret$14), shown, VOID, VOID, explain);
  } else if (phone) {
    // Inline function 'kotlin.text.map' call
    // Inline function 'kotlin.text.mapTo' call
    var destination_1 = ArrayList.z(charSequenceLength(number));
    var inductionVariable_0 = 0;
    while (inductionVariable_0 < charSequenceLength(number)) {
      var item_1 = charSequenceGet(number, inductionVariable_0);
      inductionVariable_0 = inductionVariable_0 + 1 | 0;
      // Inline function 'com.nesxha.readit.data.NumberQuiz.telepon.<anonymous>' call
      var tmp$ret$15 = item_1 === _Char___init__impl__6a9atx(49) ? 'y\u012B' : ChineseNumber_getInstance().te(toString(item_1), VOID, '\u96F6').ke();
      destination_1.h(tmp$ret$15);
    }
    var plainYi = joinToString_0(destination_1, ' ');
    // Inline function 'kotlin.also' call
    var this_3 = toCharArray(number);
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.nesxha.readit.data.NumberQuiz.telepon.<anonymous>' call
    var i = NumberQuiz_getInstance().wf_1.gb(3, this_3.length - 1 | 0);
    var t = this_3[i];
    this_3[i] = this_3[i + 1 | 0];
    this_3[i + 1 | 0] = t;
    var swapped = concatToString(this_3);
    // Inline function 'kotlin.also' call
    var this_4 = toCharArray(number);
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.nesxha.readit.data.NumberQuiz.telepon.<anonymous>' call
    var i_0 = NumberQuiz_getInstance().wf_1.gb(3, this_4.length);
    this_4[i_0] = Char__plus_impl_qi7pgj(_Char___init__impl__6a9atx(48), (Char__minus_impl_a2frrh(this_4[i_0], _Char___init__impl__6a9atx(48)) + NumberQuiz_getInstance().wf_1.gb(1, 9) | 0) % 10 | 0);
    var other = concatToString(this_4);
    var wrong = listOf_0([new NumOption(plainYi), new NumOption(ChineseNumber_getInstance().fe(swapped, true, '\u96F6').ke()), new NumOption(ChineseNumber_getInstance().fe(other, true, '\u96F6').ke())]);
    var _destruct__k2r9zo_1 = choice($this, new NumOption(sp_0.ke()), wrong);
    var opts_0 = _destruct__k2r9zo_1.cd();
    var ans_0 = _destruct__k2r9zo_1.dd();
    tmp_5 = new NumQuestion(NumCat_TELEPON_getInstance(), 'Bagaimana nomor telepon ini dibaca?', VOID, VOID, shown, opts_0, ans_0, VOID, sp_0.ke(), VOID, VOID, explain);
  } else {
    var r = number;
    var tmp_11 = ChineseNumber_getInstance();
    // Inline function 'kotlin.text.reversed' call
    var tmp$ret$20 = toString_1(reversed(isCharSequence(r) ? r : THROW_CCE()));
    var tmp_12 = zh($this, tmp_11.te(tmp$ret$20));
    var tmp_13 = zh($this, ChineseNumber_getInstance().he(toLong_0(r)));
    var tmp_14 = ChineseNumber_getInstance();
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp_15 = r.substring(0, 1);
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp_16 = tmp_15 + r.substring(2);
    // Inline function 'kotlin.text.substring' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp$ret$26 = r.substring(1, 2);
    var wrong_0 = listOf_0([tmp_12, tmp_13, zh($this, tmp_14.te(tmp_16 + tmp$ret$26)), zh($this, ChineseNumber_getInstance().te(toString(charSequenceGet(r, 0)) + '00' + toString(charSequenceGet(r, 2))))]);
    var _destruct__k2r9zo_2 = choice($this, zh($this, ChineseNumber_getInstance().te(r)), wrong_0);
    var opts_1 = _destruct__k2r9zo_2.cd();
    var ans_1 = _destruct__k2r9zo_2.dd();
    tmp_5 = new NumQuestion(NumCat_TELEPON_getInstance(), 'Nomor kamar ini dibaca bagaimana?', VOID, VOID, 'Kamar ' + r, opts_1, ans_1, VOID, ChineseNumber_getInstance().te(r).je(), VOID, VOID, explain);
  }
  return tmp_5;
}
function ukuran($this, d) {
  var kind;
  switch (d) {
    case 0:
      kind = random(listOf_0([0, 1, 2]), $this.wf_1);
      break;
    case 1:
      kind = random(listOf_0([0, 2, 3, 4]), $this.wf_1);
      break;
    default:
      kind = random(listOf_0([3, 4, 5]), $this.wf_1);
      break;
  }
  var tmp;
  switch (kind) {
    case 0:
      var a = $this.wf_1.gb(2, 90);
      var sp = ChineseNumber_getInstance().ue(a, '\u5C81', 'su\xEC', 4);
      var ex = '\u5C81 = tahun (untuk umur). ' + sp.je() + ' = ' + a + ' tahun.' + (a === 2 ? ' Umur 2 = \u4E24\u5C81.' : '');
      var tmp_0;
      if ($this.wf_1.ib()) {
        tmp_0 = new NumQuestion(NumCat_UKURAN_getInstance(), 'Berapa umurnya (tahun)?', sp.je(), sp.ke(), VOID, VOID, VOID, setOf('' + a), '' + a + ' tahun', VOID, 'tahun', ex);
      } else {
        var tmp_1 = new NumOption(ChineseNumber_getInstance().xd(a).je() + '\u5E74', VOID, true);
        var tmp_2 = zh($this, ChineseNumber_getInstance().ue(a + 10 | 0, '\u5C81', 'su\xEC', 4));
        // Inline function 'kotlin.text.map' call
        var this_0 = a.toString();
        // Inline function 'kotlin.text.mapTo' call
        var destination = ArrayList.z(charSequenceLength(this_0));
        var inductionVariable = 0;
        while (inductionVariable < charSequenceLength(this_0)) {
          var item = charSequenceGet(this_0, inductionVariable);
          inductionVariable = inductionVariable + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberQuiz.ukuran.<anonymous>' call
          var tmp$ret$0 = ChineseNumber_getInstance().yd(Char__minus_impl_a2frrh(item, _Char___init__impl__6a9atx(48))).lg_1;
          destination.h(tmp$ret$0);
        }
        var tmp_3 = new NumOption(joinToString_0(destination, '') + '\u5C81', VOID, true);
        var tmp_4 = ChineseNumber_getInstance();
        // Inline function 'kotlin.comparisons.maxOf' call
        var b = a - 1 | 0;
        var tmp$ret$3 = Math.max(2, b);
        var wrong = listOf_0([tmp_1, tmp_2, tmp_3, zh($this, tmp_4.ue(tmp$ret$3, '\u5C81', 'su\xEC', 4))]);
        var _destruct__k2r9zo = choice($this, zh($this, sp), wrong);
        var opts = _destruct__k2r9zo.cd();
        var ans = _destruct__k2r9zo.dd();
        tmp_0 = new NumQuestion(NumCat_UKURAN_getInstance(), 'Umur ini dalam bahasa Mandarin?', VOID, VOID, '' + a + ' tahun', opts, ans, VOID, sp.je(), VOID, VOID, ex);
      }

      tmp = tmp_0;
      break;
    case 1:
      var k = $this.wf_1.gb(1, 21);
      var sp_0 = ChineseNumber_getInstance().ue(k, '\u4E2A', 'g\xE8', 4).ae(Companion_getInstance_14().rd([to('\u4EBA', 'r\xE9n')]));
      var ex_0 = 'Benda/orang dihitung dengan kata satuan, misalnya \u4E2A. Angka 2 sebelum kata satuan menjadi \u4E24: \u4E24\u4E2A\u4EBA.';
      // Inline function 'kotlin.takeIf' call

      var this_1 = new NumOption(ChineseNumber_getInstance().xd(k).je() + '\u4E2A\u4EBA', VOID, true);
      // Inline function 'kotlin.contracts.contract' call

      var tmp_5;
      // Inline function 'com.nesxha.readit.data.NumberQuiz.ukuran.<anonymous>' call

      if (k === 2) {
        tmp_5 = this_1;
      } else {
        tmp_5 = null;
      }

      var tmp2_elvis_lhs = tmp_5;
      var tmp_6 = tmp2_elvis_lhs == null ? zh($this, ChineseNumber_getInstance().ue(k + 1 | 0, '\u4E2A', 'g\xE8', 4).ae(Companion_getInstance_14().rd([to('\u4EBA', 'r\xE9n')]))) : tmp2_elvis_lhs;
      var tmp_7 = new NumOption(ChineseNumber_getInstance().xd(k).je() + '\u4EBA\u4E2A', VOID, true);
      var tmp_8 = zh($this, ChineseNumber_getInstance().ue(k + 2 | 0, '\u4E2A', 'g\xE8', 4).ae(Companion_getInstance_14().rd([to('\u4EBA', 'r\xE9n')])));
      var tmp_9 = ChineseNumber_getInstance();
      // Inline function 'kotlin.comparisons.maxOf' call

      var b_0 = k - 1 | 0;
      var tmp$ret$6 = Math.max(1, b_0);
      var wrong_0 = listOf_0([tmp_6, tmp_7, tmp_8, zh($this, tmp_9.ue(tmp$ret$6, '\u4E2A', 'g\xE8', 4).ae(Companion_getInstance_14().rd([to('\u4EBA', 'r\xE9n')])))]);
      var _destruct__k2r9zo_0 = choice($this, zh($this, sp_0), wrong_0);
      var opts_0 = _destruct__k2r9zo_0.cd();
      var ans_0 = _destruct__k2r9zo_0.dd();
      tmp = new NumQuestion(NumCat_UKURAN_getInstance(), 'Bagaimana mengatakan jumlah orang ini?', VOID, VOID, '' + k + ' orang', opts_0, ans_0, VOID, sp_0.je(), VOID, VOID, ex_0);
      break;
    case 2:
      var f = $this.wf_1.gb(1, 31);
      var sp_1 = ChineseNumber_getInstance().xd(f).ae(Companion_getInstance_14().rd([to('\u697C', 'l\xF3u')]));
      var ex_1 = 'Lantai = angka + \u697C. Untuk urutan seperti lantai, 2 tetap dibaca \u4E8C: \u4E8C\u697C, bukan \u4E24\u697C.';
      var tmp_10;
      if ($this.wf_1.ib()) {
        tmp_10 = new NumQuestion(NumCat_UKURAN_getInstance(), 'Lantai berapa?', sp_1.je(), sp_1.ke(), VOID, VOID, VOID, setOf('' + f), 'Lantai ' + f, VOID, VOID, ex_1);
      } else {
        var tmp_11 = f === 2 ? new NumOption('\u4E24\u697C', VOID, true) : zh($this, ChineseNumber_getInstance().xd(f + 1 | 0).ae(Companion_getInstance_14().rd([to('\u697C', 'l\xF3u')])));
        var tmp_12 = new NumOption('\u697C' + ChineseNumber_getInstance().xd(f).je(), VOID, true);
        var tmp_13 = zh($this, ChineseNumber_getInstance().xd((imul_0(f, 10) % 100 | 0) + 1 | 0).ae(Companion_getInstance_14().rd([to('\u697C', 'l\xF3u')])));
        var tmp_14 = ChineseNumber_getInstance();
        // Inline function 'kotlin.comparisons.maxOf' call
        var b_1 = f - 1 | 0;
        var tmp$ret$7 = Math.max(1, b_1);
        var wrong_1 = listOf_0([tmp_11, tmp_12, tmp_13, zh($this, tmp_14.xd(tmp$ret$7).ae(Companion_getInstance_14().rd([to('\u697C', 'l\xF3u')])))]);
        var _destruct__k2r9zo_1 = choice($this, zh($this, sp_1), wrong_1);
        var opts_1 = _destruct__k2r9zo_1.cd();
        var ans_1 = _destruct__k2r9zo_1.dd();
        tmp_10 = new NumQuestion(NumCat_UKURAN_getInstance(), 'Bagaimana mengatakan lantai ini?', VOID, VOID, 'Lantai ' + f, opts_1, ans_1, VOID, sp_1.je(), VOID, VOID, ex_1);
      }

      tmp = tmp_10;
      break;
    case 3:
      // Inline function 'kotlin.let' call

      // Inline function 'kotlin.contracts.contract' call

      // Inline function 'com.nesxha.readit.data.NumberQuiz.ukuran.<anonymous>' call

      var it = $this.wf_1.gb(140, 200);
      var cm = NumberQuiz_getInstance().wf_1.ib() ? imul_0(it / 10 | 0, 10) : it;
      var rest = cm - 100 | 0;
      var tmp_15 = Companion_getInstance_14().rd([to('\u4E00', 'y\xEC'), to('\u7C73', 'm\u01D0')]);
      // Inline function 'kotlin.text.map' call

      // Inline function 'kotlin.text.ifEmpty' call

      var this_2 = trimEnd(rest.toString(), charArrayOf([_Char___init__impl__6a9atx(48)]));
      // Inline function 'kotlin.contracts.contract' call

      var tmp_16;
      // Inline function 'kotlin.text.isEmpty' call

      if (charSequenceLength(this_2) === 0) {
        // Inline function 'com.nesxha.readit.data.NumberQuiz.ukuran.<anonymous>' call
        tmp_16 = '0';
      } else {
        tmp_16 = this_2;
      }

      var this_3 = tmp_16;
      // Inline function 'kotlin.text.mapTo' call

      var destination_0 = ArrayList.z(charSequenceLength(this_3));
      var inductionVariable_0 = 0;
      while (inductionVariable_0 < charSequenceLength(this_3)) {
        var item_0 = charSequenceGet(this_3, inductionVariable_0);
        inductionVariable_0 = inductionVariable_0 + 1 | 0;
        // Inline function 'com.nesxha.readit.data.NumberQuiz.ukuran.<anonymous>' call
        var tmp$ret$13 = ChineseNumber_getInstance().yd(Char__minus_impl_a2frrh(item_0, _Char___init__impl__6a9atx(48)));
        destination_0.h(tmp$ret$13);
      }

      var sp_2 = tmp_15.ae(new Spoken(destination_0));
      var m = cm / 100.0;
      var ex_2 = 'Tinggi badan diucapkan "meter + sisa": ' + sp_2.je() + ' = ' + ChineseNumber_getInstance().vf(m) + ' m. Digit setelah \u7C73 dibaca satu per satu.';
      var tmp_17;
      if ($this.wf_1.ib()) {
        tmp_17 = new NumQuestion(NumCat_UKURAN_getInstance(), 'Berapa tingginya (meter)?', sp_2.je(), sp_2.ke(), VOID, VOID, VOID, setOf(dec($this, m)), ChineseNumber_getInstance().vf(m) + ' m', Keys_DECIMAL_getInstance(), 'm', ex_2);
      } else {
        var wrongNum = !((rest % 10 | 0) === 0) ? Companion_getInstance_14().rd([to('\u4E00', 'y\xEC'), to('\u7C73', 'm\u01D0')]).ae(ChineseNumber_getInstance().xd(rest)) : Companion_getInstance_14().rd([to('\u4E00', 'y\xEC'), to('\u7C73', 'm\u01D0')]).ae(ChineseNumber_getInstance().xd(rest));
        // Inline function 'kotlin.takeIf' call
        var this_4 = wrongNum.je();
        // Inline function 'kotlin.contracts.contract' call
        var tmp_18;
        // Inline function 'com.nesxha.readit.data.NumberQuiz.ukuran.<anonymous>' call
        if (!(this_4 === sp_2.je())) {
          tmp_18 = this_4;
        } else {
          tmp_18 = null;
        }
        var tmp3_elvis_lhs = tmp_18;
        var tmp_19 = new NumOption(tmp3_elvis_lhs == null ? '\u4E00\u7C73' + ChineseNumber_getInstance().xd(rest / 10 | 0).je() + '\u5341' : tmp3_elvis_lhs, VOID, true);
        var tmp_20 = Companion_getInstance_14().rd([to('\u4E00', 'y\xEC'), to('\u7C73', 'm\u01D0')]);
        // Inline function 'kotlin.text.map' call
        var this_5 = trimEnd((rest + 5 | 0).toString(), charArrayOf([_Char___init__impl__6a9atx(48)]));
        // Inline function 'kotlin.text.mapTo' call
        var destination_1 = ArrayList.z(charSequenceLength(this_5));
        var inductionVariable_1 = 0;
        while (inductionVariable_1 < charSequenceLength(this_5)) {
          var item_1 = charSequenceGet(this_5, inductionVariable_1);
          inductionVariable_1 = inductionVariable_1 + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberQuiz.ukuran.<anonymous>' call
          var tmp$ret$18 = ChineseNumber_getInstance().yd(Char__minus_impl_a2frrh(item_1, _Char___init__impl__6a9atx(48)));
          destination_1.h(tmp$ret$18);
        }
        var tmp_21 = zh($this, tmp_20.ae(new Spoken(destination_1)));
        var tmp_22 = new NumOption(ChineseNumber_getInstance().xd(rest).je() + '\u7C73\u4E00', VOID, true);
        var tmp_23 = Companion_getInstance_14().rd([to('\u4E00', 'y\xEC'), to('\u7C73', 'm\u01D0')]);
        // Inline function 'kotlin.text.map' call
        // Inline function 'kotlin.text.reversed' call
        var this_6 = rest.toString();
        var tmp$ret$21 = toString_1(reversed(isCharSequence(this_6) ? this_6 : THROW_CCE()));
        var this_7 = trimEnd(tmp$ret$21, charArrayOf([_Char___init__impl__6a9atx(48)]));
        // Inline function 'kotlin.text.mapTo' call
        var destination_2 = ArrayList.z(charSequenceLength(this_7));
        var inductionVariable_2 = 0;
        while (inductionVariable_2 < charSequenceLength(this_7)) {
          var item_2 = charSequenceGet(this_7, inductionVariable_2);
          inductionVariable_2 = inductionVariable_2 + 1 | 0;
          // Inline function 'com.nesxha.readit.data.NumberQuiz.ukuran.<anonymous>' call
          var tmp$ret$22 = ChineseNumber_getInstance().yd(Char__minus_impl_a2frrh(item_2, _Char___init__impl__6a9atx(48)));
          destination_2.h(tmp$ret$22);
        }
        var wrong_2 = listOf_0([tmp_19, tmp_21, tmp_22, zh($this, tmp_23.ae(new Spoken(destination_2)))]);
        var _destruct__k2r9zo_2 = choice($this, zh($this, sp_2), wrong_2);
        var opts_2 = _destruct__k2r9zo_2.cd();
        var ans_2 = _destruct__k2r9zo_2.dd();
        tmp_17 = new NumQuestion(NumCat_UKURAN_getInstance(), 'Bagaimana tinggi ini diucapkan?', VOID, VOID, ChineseNumber_getInstance().vf(m) + ' m', opts_2, ans_2, VOID, sp_2.je(), VOID, VOID, ex_2);
      }

      tmp = tmp_17;
      break;
    case 4:
      var kg = $this.wf_1.gb(2, 120);
      var sp_3 = ChineseNumber_getInstance().ue(kg, '\u516C', 'g\u014Dng', 1).ae(Companion_getInstance_14().rd([to('\u65A4', 'j\u012Bn')]));
      var ex_3 = '\u516C\u65A4 = kilogram. ' + sp_3.je() + ' = ' + kg + ' kg.';
      tmp = new NumQuestion(NumCat_UKURAN_getInstance(), 'Berapa beratnya (kg)?', sp_3.je(), sp_3.ke(), VOID, VOID, VOID, setOf('' + kg), '' + kg + ' kg', VOID, 'kg', ex_3);
      break;
    default:
      var jin = $this.wf_1.gb(1, 21);
      var sp_4 = ChineseNumber_getInstance().ue(jin, '\u65A4', 'j\u012Bn', 1);
      var kg_0 = jin / 2.0;
      var tmp4_cat = NumCat_UKURAN_getInstance();
      var tmp5_hanzi = sp_4.je();
      var tmp6_pinyin = sp_4.ke();
      var tmp7_accepted = setOf(dec($this, kg_0));
      var tmp8_answerText = ChineseNumber_getInstance().vf(kg_0) + ' kg';
      var tmp9_keys = Keys_DECIMAL_getInstance();
      var tmp10_explain = '1 \u65A4 = 0,5 kg (500 gram). ' + sp_4.je() + ' = ' + jin + ' \xD7 0,5 = ' + ChineseNumber_getInstance().vf(kg_0) + ' kg.';
      tmp = new NumQuestion(tmp4_cat, 'Di pasar Tiongkok berat dihitung dengan \u65A4. Berapa kilogram ini?', tmp5_hanzi, tmp6_pinyin, VOID, VOID, VOID, tmp7_accepted, tmp8_answerText, tmp9_keys, 'kg', tmp10_explain, true);
      break;
  }
  return tmp;
}
function approxPool($this, d) {
  var ge = Companion_getInstance_14().rd([to('\u4E2A', 'g\xE8')]);
  var list = ArrayList.h1();
  // Inline function 'kotlin.collections.plusAssign' call
  var element = new Approx(Companion_getInstance_14().rd([to('\u51E0', 'j\u01D0')]).ae(ge), 'Beberapa (biasanya kurang dari 10)');
  list.h(element);
  // Inline function 'kotlin.collections.plusAssign' call
  var element_0 = new Approx(Companion_getInstance_14().rd([to('\u4E09', 's\u0101n'), to('\u56DB', 's\xEC')]).ae(ge), 'Tiga atau empat');
  list.h(element_0);
  // Inline function 'kotlin.collections.plusAssign' call
  var element_1 = new Approx(Companion_getInstance_14().rd([to('\u5341', 'sh\xED'), to('\u591A', 'du\u014D')]).ae(ge), 'Lebih dari 10 (antara 11\u201319)');
  list.h(element_1);
  // Inline function 'kotlin.collections.plusAssign' call
  var element_2 = new Approx(Companion_getInstance_14().rd([to('\u4E94', 'w\u01D4'), to('\u516D', 'li\xF9')]).ae(ge), 'Lima atau enam');
  list.h(element_2);
  // Inline function 'kotlin.collections.plusAssign' call
  var element_3 = new Approx(Companion_getInstance_14().rd([to('\u4E8C', '\xE8r'), to('\u5341', 'sh\xED'), to('\u591A', 'du\u014D')]).ae(ge), 'Dua puluh lebih');
  list.h(element_3);
  if (d >= 1) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_4 = new Approx(Companion_getInstance_14().rd([to('\u51E0', 'j\u01D0'), to('\u5341', 'sh\xED')]).ae(ge), 'Puluhan (beberapa puluh)');
    list.h(element_4);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_5 = new Approx(Companion_getInstance_14().rd([to('\u4E8C', '\xE8r'), to('\u4E09', 's\u0101n'), to('\u5341', 'sh\xED')]).ae(ge), 'Sekitar 20 sampai 30');
    list.h(element_5);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_6 = new Approx(ChineseNumber_getInstance().xd(100).ae(Companion_getInstance_14().rd([to('\u591A', 'du\u014D')])).ae(ge), 'Seratus lebih');
    list.h(element_6);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_7 = new Approx(Companion_getInstance_14().rd([to('\u4E03', 'q\u012B'), to('\u516B', 'b\u0101')]).ae(ge), 'Tujuh atau delapan');
    list.h(element_7);
  }
  if (d >= 2) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_8 = new Approx(Companion_getInstance_14().rd([to('\u51E0', 'j\u01D0'), to('\u767E', 'b\u01CEi')]).ae(ge), 'Ratusan');
    list.h(element_8);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_9 = new Approx(ChineseNumber_getInstance().xd(2000).ae(Companion_getInstance_14().rd([to('\u591A', 'du\u014D')])), 'Dua ribu lebih');
    list.h(element_9);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_10 = new Approx(Companion_getInstance_14().rd([to('\u4E94', 'w\u01D4'), to('\u516D', 'li\xF9'), to('\u5341', 'sh\xED')]).ae(ge), 'Sekitar 50 sampai 60');
    list.h(element_10);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_11 = new Approx(ChineseNumber_getInstance().xd(10000).ae(Companion_getInstance_14().rd([to('\u591A', 'du\u014D')])), 'Sepuluh ribu lebih');
    list.h(element_11);
    // Inline function 'kotlin.collections.plusAssign' call
    var element_12 = new Approx(Companion_getInstance_14().rd([to('\u51E0', 'j\u01D0'), to('\u4E07', 'w\xE0n')]), 'Puluhan ribu');
    list.h(element_12);
  }
  return list;
}
function kira($this, d) {
  var pool = approxPool($this, d);
  var a = random(pool, $this.wf_1);
  var ex = '\u591A setelah angka = "lebih" (\u5341\u591A = belasan, \u4E00\u767E\u591A = seratus lebih). \u51E0 = "beberapa" (\u51E0\u5341 = puluhan). Dua angka berurutan = perkiraan (\u4E09\u56DB = 3 atau 4, \u4E8C\u4E09\u5341 = 20\u201330).';
  var tmp;
  if ($this.wf_1.ib()) {
    var tmp_0 = new NumOption(a.og_1);
    // Inline function 'kotlin.collections.map' call
    var this_0 = minus(pool, a);
    // Inline function 'kotlin.collections.mapTo' call
    var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
    var tmp0_iterator = this_0.r();
    while (tmp0_iterator.s()) {
      var item = tmp0_iterator.t();
      // Inline function 'com.nesxha.readit.data.NumberQuiz.kira.<anonymous>' call
      var tmp$ret$0 = new NumOption(item.og_1);
      destination.h(tmp$ret$0);
    }
    var _destruct__k2r9zo = choice($this, tmp_0, destination);
    var opts = _destruct__k2r9zo.cd();
    var ans = _destruct__k2r9zo.dd();
    tmp = new NumQuestion(NumCat_KIRA_getInstance(), 'Apa artinya?', a.ng_1.je(), a.ng_1.ke(), VOID, opts, ans, VOID, a.og_1, VOID, VOID, ex);
  } else {
    var tmp_1 = zh($this, a.ng_1);
    // Inline function 'kotlin.collections.map' call
    var this_1 = minus(pool, a);
    // Inline function 'kotlin.collections.mapTo' call
    var destination_0 = ArrayList.z(collectionSizeOrDefault(this_1, 10));
    var tmp0_iterator_0 = this_1.r();
    while (tmp0_iterator_0.s()) {
      var item_0 = tmp0_iterator_0.t();
      // Inline function 'com.nesxha.readit.data.NumberQuiz.kira.<anonymous>' call
      var tmp$ret$3 = zh(NumberQuiz_getInstance(), item_0.ng_1);
      destination_0.h(tmp$ret$3);
    }
    var _destruct__k2r9zo_0 = choice($this, tmp_1, destination_0);
    var opts_0 = _destruct__k2r9zo_0.cd();
    var ans_0 = _destruct__k2r9zo_0.dd();
    var tmp_2 = NumCat_KIRA_getInstance();
    // Inline function 'kotlin.text.replaceFirstChar' call
    var this_2 = a.og_1;
    var tmp_3;
    // Inline function 'kotlin.text.isNotEmpty' call
    if (charSequenceLength(this_2) > 0) {
      // Inline function 'kotlin.text.plus' call
      // Inline function 'com.nesxha.readit.data.NumberQuiz.kira.<anonymous>' call
      // Inline function 'kotlin.text.lowercaseChar' call
      // Inline function 'kotlin.text.lowercase' call
      var this_3 = charSequenceGet(this_2, 0);
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      var tmp$ret$9 = toString(this_3).toLowerCase();
      var this_4 = charSequenceGet(tmp$ret$9, 0);
      // Inline function 'kotlin.text.substring' call
      // Inline function 'kotlin.js.asDynamic' call
      var other = this_2.substring(1);
      tmp_3 = toString(this_4) + other;
    } else {
      tmp_3 = this_2;
    }
    var tmp$ret$15 = tmp_3;
    tmp = new NumQuestion(tmp_2, 'Mana yang artinya "' + tmp$ret$15 + '"?', VOID, VOID, a.og_1, opts_0, ans_0, VOID, a.ng_1.je(), VOID, VOID, ex);
  }
  return tmp;
}
function NumberQuiz$norm$lambda(it) {
  return toInt(it).toString();
}
var NumberQuiz_instance;
function NumberQuiz_getInstance() {
  if (NumberQuiz_instance === VOID)
    new NumberQuiz();
  return NumberQuiz_instance;
}
function NumCat_UMUM_getInstance() {
  NumCat_initEntries();
  return NumCat_UMUM_instance;
}
function NumCat_HARGA_getInstance() {
  NumCat_initEntries();
  return NumCat_HARGA_instance;
}
function NumCat_DISKON_getInstance() {
  NumCat_initEntries();
  return NumCat_DISKON_instance;
}
function NumCat_PERSEN_getInstance() {
  NumCat_initEntries();
  return NumCat_PERSEN_instance;
}
function NumCat_JAM_getInstance() {
  NumCat_initEntries();
  return NumCat_JAM_instance;
}
function NumCat_TANGGAL_getInstance() {
  NumCat_initEntries();
  return NumCat_TANGGAL_instance;
}
function NumCat_TELEPON_getInstance() {
  NumCat_initEntries();
  return NumCat_TELEPON_instance;
}
function NumCat_UKURAN_getInstance() {
  NumCat_initEntries();
  return NumCat_UKURAN_instance;
}
function NumCat_KIRA_getInstance() {
  NumCat_initEntries();
  return NumCat_KIRA_instance;
}
function NumCat_CAMPUR_getInstance() {
  NumCat_initEntries();
  return NumCat_CAMPUR_instance;
}
function Keys_NUMBER_getInstance() {
  Keys_initEntries();
  return Keys_NUMBER_instance;
}
function Keys_DECIMAL_getInstance() {
  Keys_initEntries();
  return Keys_DECIMAL_instance;
}
function Keys_TIME_getInstance() {
  Keys_initEntries();
  return Keys_TIME_instance;
}
function Keys_DATE_getInstance() {
  Keys_initEntries();
  return Keys_DATE_instance;
}
function Keys_FRACTION_getInstance() {
  Keys_initEntries();
  return Keys_FRACTION_instance;
}
function Keys_PERCENT_getInstance() {
  Keys_initEntries();
  return Keys_PERCENT_instance;
}
var Companion_instance_14;
function Companion_getInstance_14() {
  if (Companion_instance_14 === VOID)
    new Companion_14();
  return Companion_instance_14;
}
function Spoken$_get_hanzi_$lambda_g0dx40(it) {
  return it.lg_1;
}
function Spoken$_get_pinyin_$lambda_wqxeyv(it) {
  return it.mg_1;
}
function section($this, v, leading, followedByBig, out) {
  // Inline function 'kotlin.intArrayOf' call
  var digits = new Int32Array([v / 1000 | 0, (v / 100 | 0) % 10 | 0, (v / 10 | 0) % 10 | 0, v % 10 | 0]);
  // Inline function 'kotlin.arrayOf' call
  // Inline function 'kotlin.js.unsafeCast' call
  // Inline function 'kotlin.js.asDynamic' call
  var units = [new Syl('\u5343', 'qi\u0101n'), new Syl('\u767E', 'b\u01CEi'), new Syl('\u5341', 'sh\xED'), null];
  var pendingZero = false;
  var any = false;
  var inductionVariable = 0;
  if (inductionVariable <= 3)
    $l$loop: do {
      var i = inductionVariable;
      inductionVariable = inductionVariable + 1 | 0;
      var d = digits[i];
      if (d === 0) {
        if (any)
          pendingZero = true;
        continue $l$loop;
      }
      if (pendingZero) {
        // Inline function 'kotlin.collections.plusAssign' call
        var element = $this.vd_1;
        out.h(element);
        pendingZero = false;
      }
      var unit = units[i];
      var first = !any;
      if (i !== 2 || d !== 1 || !first || !leading)
        if (d === 2 && i === 0) {
          // Inline function 'kotlin.collections.plusAssign' call
          var element_0 = $this.ud_1;
          out.h(element_0);
        } else if (d === 2 && i === 1 && first && leading) {
          // Inline function 'kotlin.collections.plusAssign' call
          var element_1 = $this.ud_1;
          out.h(element_1);
        } else if (d === 2 && i === 3 && first && followedByBig) {
          // Inline function 'kotlin.collections.plusAssign' call
          var element_2 = $this.ud_1;
          out.h(element_2);
        } else if (d === 1 && (i === 0 || i === 1)) {
          // Inline function 'kotlin.collections.plusAssign' call
          var element_3 = new Syl('\u4E00', 'y\xEC');
          out.h(element_3);
        } else {
          // Inline function 'kotlin.collections.plusAssign' call
          var element_4 = $this.yd(d);
          out.h(element_4);
        }
      if (!(unit == null)) {
        // Inline function 'kotlin.collections.plusAssign' call
        out.h(unit);
      }
      any = true;
    }
     while (inductionVariable <= 3);
}
function time$hour(x) {
  return (x === 2 ? new Spoken(listOf(ChineseNumber_getInstance().ud_1)) : ChineseNumber_getInstance().xd(x)).ae(Companion_getInstance_14().rd([to('\u70B9', 'di\u01CEn')]));
}
function steps$part(out, v, label, leading) {
  if (v.equals(new Long(0, 0))) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element = 'Kelompok ' + label + ' = 0000, dilewati (tidak dibaca).';
    out.h(element);
    return Unit_instance;
  }
  var sp = ChineseNumber_getInstance().he(v);
  var sec = v.h3();
  var digits = padStart(v.toString(), 4, _Char___init__impl__6a9atx(48));
  var notes = ArrayList.h1();
  if (!leading && sec < 1000) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_0 = 'diawali nol, jadi didahului \u96F6';
    notes.h(element_0);
  }
  if (contains_1(trimEnd(trimStart(digits, charArrayOf([_Char___init__impl__6a9atx(48)])), charArrayOf([_Char___init__impl__6a9atx(48)])), _Char___init__impl__6a9atx(48))) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_1 = 'ada nol di tengah, ditulis \u96F6 sekali';
    notes.h(element_1);
  }
  if ((sec % 10 | 0) === 0 && sec >= 10) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_2 = 'nol di akhir tidak dibaca';
    notes.h(element_2);
  }
  if (leading && (10 <= sec ? sec <= 19 : false)) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_3 = '10\u201319 di awal cukup \u5341, tanpa \u4E00';
    notes.h(element_3);
  }
  if (contains_2(sp.je(), '\u4E24')) {
    // Inline function 'kotlin.collections.plusAssign' call
    var element_4 = 'angka 2 di sini dibaca \u4E24';
    notes.h(element_4);
  }
  // Inline function 'kotlin.collections.plusAssign' call
  var tmp = 'Baca ' + label + ' ' + ChineseNumber_getInstance().dg(v) + ': ' + sp.je();
  var tmp_0;
  // Inline function 'kotlin.collections.isNotEmpty' call
  if (!notes.b1()) {
    tmp_0 = ' (' + joinToString_0(notes, '; ') + ')';
  } else {
    tmp_0 = '';
  }
  var element_5 = tmp + tmp_0 + '.';
  out.h(element_5);
}
function ChineseNumber$steps$lambda(_destruct__k2r9zo) {
  var g = _destruct__k2r9zo.cd();
  var nm = _destruct__k2r9zo.dd();
  return nm === 'sisa' ? g + ' (tanpa nama)' : g + ' \u2192 ' + nm;
}
var ChineseNumber_instance;
function ChineseNumber_getInstance() {
  if (ChineseNumber_instance === VOID)
    new ChineseNumber();
  return ChineseNumber_instance;
}
function base($this, c) {
  var tmp0_safe_receiver = $this.wg_1.m2(new Char(c));
  var tmp;
  if (tmp0_safe_receiver == null) {
    tmp = null;
  } else {
    var tmp_0 = tmp0_safe_receiver.ad_1;
    tmp = tmp_0 == null ? null : tmp_0.u1_1;
  }
  var tmp1_elvis_lhs = tmp;
  var tmp_1;
  var tmp_2 = tmp1_elvis_lhs;
  if ((tmp_2 == null ? null : new Char(tmp_2)) == null) {
    // Inline function 'kotlin.text.lowercaseChar' call
    // Inline function 'kotlin.text.lowercase' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp$ret$2 = toString(c).toLowerCase();
    tmp_1 = charSequenceGet(tmp$ret$2, 0);
  } else {
    tmp_1 = tmp1_elvis_lhs;
  }
  return tmp_1;
}
function isVowel($this, c) {
  return isLetter(c) && contains_1('aeiou\xFCv', base($this, c));
}
function hanziCount($this, h) {
  // Inline function 'kotlin.text.count' call
  var count = 0;
  var inductionVariable = 0;
  while (inductionVariable < charSequenceLength(h)) {
    var element = charSequenceGet(h, inductionVariable);
    inductionVariable = inductionVariable + 1 | 0;
    // Inline function 'com.nesxha.readit.util.Pinyin.hanziCount.<anonymous>' call
    if ((_Char___init__impl__6a9atx(19968) <= element ? element <= _Char___init__impl__6a9atx(40959) : false) || element === _Char___init__impl__6a9atx(12295)) {
      count = count + 1 | 0;
    }
  }
  return count;
}
var Pinyin_instance;
function Pinyin_getInstance() {
  if (Pinyin_instance === VOID)
    new Pinyin();
  return Pinyin_instance;
}
function numCats() {
  // Inline function 'kotlin.collections.map' call
  var this_0 = get_entries();
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.numCats.<anonymous>' call
    var tmp$ret$0 = obj([to('id', q(item.o2_1)), to('title', q(item.ye_1)), to('sub', q(item.ze_1)), to('emoji', q(item.af_1))]);
    destination.h(tmp$ret$0);
  }
  return arr(destination);
}
function numRanges() {
  // Inline function 'kotlin.collections.map' call
  var this_0 = NumberQuiz_getInstance().xf_1;
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.numRanges.<anonymous>' call
    var tmp$ret$0 = obj([to('label', q(item.ad_1)), to('lo', item.bd_1.fc_1.toString()), to('hi', item.bd_1.gc_1.toString())]);
    destination.h(tmp$ret$0);
  }
  return arr(destination);
}
function numBuild(catId, difficulty, n, lo, hi) {
  var range = lo >= 0 && hi >= lo ? numberToLong(lo).c3(numberToLong(hi)) : null;
  // Inline function 'kotlin.collections.map' call
  var this_0 = NumberQuiz_getInstance().pg(cat(catId), difficulty, n, range);
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.numBuild.<anonymous>' call
    var tmp$ret$0 = question(item);
    destination.h(tmp$ret$0);
  }
  return arr(destination);
}
function numNorm(raw) {
  return NumberQuiz_getInstance().cg(raw);
}
function lesson(catId) {
  // Inline function 'kotlin.collections.map' call
  var this_0 = NumberLessons_getInstance().ve(cat(catId));
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.lesson.<anonymous>' call
    var tmp = to('title', q(item.jd_1));
    var tmp_0 = to('body', q(item.kd_1));
    // Inline function 'kotlin.collections.map' call
    var this_1 = item.ld_1;
    // Inline function 'kotlin.collections.mapTo' call
    var destination_0 = ArrayList.z(collectionSizeOrDefault(this_1, 10));
    var tmp0_iterator_0 = this_1.r();
    while (tmp0_iterator_0.s()) {
      var item_0 = tmp0_iterator_0.t();
      // Inline function 'com.nesxha.readit.web.lesson.<anonymous>.<anonymous>' call
      var tmp$ret$0 = obj([to('s', spoken(item_0.ad_1)), to('m', q(item_0.bd_1))]);
      destination_0.h(tmp$ret$0);
    }
    var tmp_1 = to('examples', arr(destination_0));
    // Inline function 'kotlin.collections.map' call
    var this_2 = item.md_1;
    // Inline function 'kotlin.collections.mapTo' call
    var destination_1 = ArrayList.z(collectionSizeOrDefault(this_2, 10));
    var tmp0_iterator_1 = this_2.r();
    while (tmp0_iterator_1.s()) {
      var item_1 = tmp0_iterator_1.t();
      // Inline function 'com.nesxha.readit.web.lesson.<anonymous>.<anonymous>' call
      var tmp$ret$3 = obj([to('s', spoken(item_1.ad_1)), to('m', q(item_1.bd_1))]);
      destination_1.h(tmp$ret$3);
    }
    var tmp_2 = to('table', arr(destination_1));
    var tmp_3 = to('formula', q(item.nd_1));
    // Inline function 'kotlin.collections.map' call
    var this_3 = item.od_1;
    // Inline function 'kotlin.collections.mapTo' call
    var destination_2 = ArrayList.z(collectionSizeOrDefault(this_3, 10));
    var tmp0_iterator_2 = this_3.r();
    while (tmp0_iterator_2.s()) {
      var item_2 = tmp0_iterator_2.t();
      // Inline function 'com.nesxha.readit.web.lesson.<anonymous>.<anonymous>' call
      var tmp$ret$6 = q(item_2);
      destination_2.h(tmp$ret$6);
    }
    var tmp$ret$9 = obj([tmp, tmp_0, tmp_1, tmp_2, tmp_3, to('steps', arr(destination_2)), to('stepsTitle', q(item.pd_1))]);
    destination.h(tmp$ret$9);
  }
  return arr(destination);
}
function culture() {
  // Inline function 'kotlin.collections.map' call
  var this_0 = NumberLessons_getInstance().pe_1;
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.culture.<anonymous>' call
    var tmp$ret$0 = q(item);
    destination.h(tmp$ret$0);
  }
  return arr(destination);
}
function convert(n) {
  var v = numberToLong(n);
  var tmp = to('s', spoken(ChineseNumber_getInstance().he(v)));
  // Inline function 'kotlin.collections.map' call
  var this_0 = ChineseNumber_getInstance().ge(v);
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.convert.<anonymous>' call
    var tmp$ret$0 = q(item);
    destination.h(tmp$ret$0);
  }
  return obj([tmp, to('steps', arr(destination)), to('fmt', q(ChineseNumber_getInstance().dg(v)))]);
}
function fmt(n) {
  return ChineseNumber_getInstance().dg(numberToLong(n));
}
function pySegments(py, hanzi) {
  // Inline function 'kotlin.collections.map' call
  var this_0 = Pinyin_getInstance().ch(py, hanzi);
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.pySegments.<anonymous>' call
    var tmp$ret$0 = obj([to('t', q(item.sg_1)), to('tone', '' + item.tg_1)]);
    destination.h(tmp$ret$0);
  }
  return arr(destination);
}
function pyPlain(py) {
  return Pinyin_getInstance().yg(py);
}
function pyAligned(py, hanzi) {
  return Pinyin_getInstance().bh(py, hanzi);
}
function pySplit(py) {
  // Inline function 'kotlin.collections.map' call
  var this_0 = Pinyin_getInstance().ah(py);
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.pySplit.<anonymous>' call
    var tmp$ret$0 = q(item);
    destination.h(tmp$ret$0);
  }
  return arr(destination);
}
function pyWithTone(syllable, tone) {
  return Pinyin_getInstance().dh(syllable, tone);
}
function pyToneOf(syllable) {
  return Pinyin_getInstance().zg(syllable);
}
function q(s) {
  if (s == null)
    return 'null';
  var sb = StringBuilder.y9('"');
  var inductionVariable = 0;
  while (inductionVariable < charSequenceLength(s)) {
    var c = charSequenceGet(s, inductionVariable);
    inductionVariable = inductionVariable + 1 | 0;
    if (c === _Char___init__impl__6a9atx(34))
      sb.n8('\\"');
    else if (c === _Char___init__impl__6a9atx(92))
      sb.n8('\\\\');
    else if (c === _Char___init__impl__6a9atx(10))
      sb.n8('\\n');
    else if (c === _Char___init__impl__6a9atx(13))
      sb.n8('\\r');
    else if (c === _Char___init__impl__6a9atx(9))
      sb.n8('\\t');
    else {
      // Inline function 'kotlin.code' call
      if (Char__toInt_impl_vasixd(c) < 32) {
        // Inline function 'kotlin.code' call
        var tmp$ret$1 = Char__toInt_impl_vasixd(c);
        sb.n8('\\u' + padStart(toString_2(tmp$ret$1, 16), 4, _Char___init__impl__6a9atx(48)));
      } else {
        sb.o8(c);
      }
    }
  }
  return sb.o8(_Char___init__impl__6a9atx(34)).toString();
}
function arr(items) {
  return joinToString_0(items, ',', '[', ']');
}
function obj(kv) {
  return joinToString(kv, ',', '{', '}', VOID, VOID, obj$lambda);
}
function spoken(s) {
  var tmp = to('h', q(s.je()));
  var tmp_0 = to('p', q(s.ke()));
  // Inline function 'kotlin.collections.map' call
  var this_0 = s.zd_1;
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.spoken.<anonymous>' call
    var tmp$ret$0 = obj([to('h', q(item.lg_1)), to('p', q(item.mg_1))]);
    destination.h(tmp$ret$0);
  }
  return obj([tmp, tmp_0, to('syls', arr(destination))]);
}
function cat(id) {
  var tmp$ret$1;
  $l$block: {
    // Inline function 'kotlin.collections.firstOrNull' call
    var tmp0_iterator = get_entries().r();
    while (tmp0_iterator.s()) {
      var element = tmp0_iterator.t();
      // Inline function 'com.nesxha.readit.web.cat.<anonymous>' call
      if (element.o2_1 === id) {
        tmp$ret$1 = element;
        break $l$block;
      }
    }
    tmp$ret$1 = null;
  }
  var tmp0_elvis_lhs = tmp$ret$1;
  return tmp0_elvis_lhs == null ? NumCat_UMUM_getInstance() : tmp0_elvis_lhs;
}
function question(x) {
  var tmp = to('cat', q(x.hf_1.o2_1));
  var tmp_0 = to('instruction', q(x.if_1));
  var tmp_1 = to('hanzi', q(x.jf_1));
  var tmp_2 = to('pinyin', q(x.kf_1));
  var tmp_3 = to('display', q(x.lf_1));
  // Inline function 'kotlin.collections.map' call
  var this_0 = x.mf_1;
  // Inline function 'kotlin.collections.mapTo' call
  var destination = ArrayList.z(collectionSizeOrDefault(this_0, 10));
  var tmp0_iterator = this_0.r();
  while (tmp0_iterator.s()) {
    var item = tmp0_iterator.t();
    // Inline function 'com.nesxha.readit.web.question.<anonymous>' call
    var tmp$ret$0 = obj([to('text', q(item.ef_1)), to('pinyin', q(item.ff_1)), to('chinese', '' + item.gf_1)]);
    destination.h(tmp$ret$0);
  }
  var tmp_4 = to('options', arr(destination));
  var tmp_5 = to('answer', '' + x.nf_1);
  // Inline function 'kotlin.collections.map' call
  var this_1 = x.of_1;
  // Inline function 'kotlin.collections.mapTo' call
  var destination_0 = ArrayList.z(collectionSizeOrDefault(this_1, 10));
  var tmp0_iterator_0 = this_1.r();
  while (tmp0_iterator_0.s()) {
    var item_0 = tmp0_iterator_0.t();
    // Inline function 'com.nesxha.readit.web.question.<anonymous>' call
    var tmp$ret$3 = q(item_0);
    destination_0.h(tmp$ret$3);
  }
  return obj([tmp, tmp_0, tmp_1, tmp_2, tmp_3, tmp_4, tmp_5, to('accepted', arr(destination_0)), to('answerText', q(x.pf_1)), to('keys', q(x.qf_1.o2_1)), to('extra', q(x.qf_1.df_1)), to('unit', q(x.rf_1)), to('explain', q(x.sf_1)), to('story', '' + x.tf_1)]);
}
function obj$lambda(it) {
  return q(it.ad_1) + ':' + it.bd_1;
}
//region block: post-declaration
initMetadataForInterface(CharSequence, 'CharSequence');
initMetadataForClass(Number_0, 'Number');
initMetadataForObject(Unit, 'Unit');
initMetadataForClass(_no_name_provided__qut3iv);
initMetadataForCompanion(Companion);
initMetadataForClass(Char, 'Char');
initMetadataForInterface(Collection, 'Collection');
initMetadataForInterface(KtList, 'List', VOID, VOID, [Collection]);
initMetadataForInterface(KtSet, 'Set', VOID, VOID, [Collection]);
initMetadataForInterface(Entry, 'Entry');
initMetadataForInterface(KtMap, 'Map');
initMetadataForCompanion(Companion_0);
initMetadataForClass(Enum, 'Enum');
initMetadataForCompanion(Companion_1);
initMetadataForClass(Long, 'Long');
initMetadataForInterface(FunctionAdapter, 'FunctionAdapter');
initMetadataForClass(arrayIterator$1);
initMetadataForObject(Digit, 'Digit');
initMetadataForObject(Letter, 'Letter');
initMetadataForObject(OtherLowercase, 'OtherLowercase');
initMetadataForInterface(Comparator, 'Comparator');
initMetadataForClass(AbstractCollection, 'AbstractCollection', VOID, VOID, [Collection]);
initMetadataForClass(AbstractMutableCollection, 'AbstractMutableCollection', VOID, VOID, [AbstractCollection, Collection]);
initMetadataForClass(IteratorImpl, 'IteratorImpl');
initMetadataForClass(ListIteratorImpl, 'ListIteratorImpl');
initMetadataForClass(AbstractMutableList, 'AbstractMutableList', VOID, VOID, [AbstractMutableCollection, KtList, Collection]);
initMetadataForClass(AbstractMap, 'AbstractMap', VOID, VOID, [KtMap]);
initMetadataForClass(AbstractMutableMap, 'AbstractMutableMap', VOID, VOID, [AbstractMap, KtMap]);
initMetadataForClass(AbstractMutableSet, 'AbstractMutableSet', VOID, VOID, [AbstractMutableCollection, KtSet, Collection]);
initMetadataForCompanion(Companion_2);
initMetadataForInterface(RandomAccess, 'RandomAccess');
initMetadataForClass(ArrayList, 'ArrayList', ArrayList.h1, VOID, [AbstractMutableList, KtList, Collection, RandomAccess]);
initMetadataForClass(HashMap, 'HashMap', HashMap.g6, VOID, [AbstractMutableMap, KtMap]);
initMetadataForClass(HashMapEntrySetBase, 'HashMapEntrySetBase', VOID, VOID, [KtSet, Collection, AbstractMutableSet]);
initMetadataForClass(HashMapEntrySet, 'HashMapEntrySet');
initMetadataForClass(HashSet, 'HashSet', HashSet.f7, VOID, [AbstractMutableSet, KtSet, Collection]);
initMetadataForCompanion(Companion_3);
initMetadataForClass(Itr, 'Itr');
initMetadataForClass(KeysItr, 'KeysItr');
initMetadataForClass(EntriesItr, 'EntriesItr');
initMetadataForClass(EntryRef, 'EntryRef', VOID, VOID, [Entry]);
initMetadataForInterface(InternalMap, 'InternalMap');
protoOf(InternalHashMap).b7 = containsAllEntries;
initMetadataForClass(InternalHashMap, 'InternalHashMap', InternalHashMap.r6, VOID, [InternalMap]);
initMetadataForObject(EmptyHolder, 'EmptyHolder');
initMetadataForClass(LinkedHashMap, 'LinkedHashMap', LinkedHashMap.e9, VOID, [HashMap, KtMap]);
initMetadataForClass(LinkedHashSet, 'LinkedHashSet', LinkedHashSet.e1, VOID, [HashSet, KtSet, Collection]);
initMetadataForClass(Exception, 'Exception', Exception.g9);
initMetadataForClass(RuntimeException, 'RuntimeException', RuntimeException.k9);
initMetadataForClass(IllegalArgumentException, 'IllegalArgumentException', IllegalArgumentException.j9);
initMetadataForClass(IndexOutOfBoundsException, 'IndexOutOfBoundsException', IndexOutOfBoundsException.l9);
initMetadataForClass(IllegalStateException, 'IllegalStateException', IllegalStateException.m9);
initMetadataForClass(UnsupportedOperationException, 'UnsupportedOperationException', UnsupportedOperationException.y5);
initMetadataForClass(NoSuchElementException, 'NoSuchElementException', NoSuchElementException.r4);
initMetadataForClass(NumberFormatException, 'NumberFormatException', NumberFormatException.r9);
initMetadataForClass(ConcurrentModificationException, 'ConcurrentModificationException', ConcurrentModificationException.b8);
initMetadataForClass(ArithmeticException, 'ArithmeticException', ArithmeticException.w9);
initMetadataForClass(NullPointerException, 'NullPointerException', NullPointerException.t3);
initMetadataForClass(NoWhenBranchMatchedException, 'NoWhenBranchMatchedException', NoWhenBranchMatchedException.x3);
initMetadataForClass(ClassCastException, 'ClassCastException', ClassCastException.b4);
initMetadataForClass(StringBuilder, 'StringBuilder', StringBuilder.e, VOID, [CharSequence]);
initMetadataForCompanion(Companion_4);
initMetadataForClass(sam$kotlin_Comparator$0, 'sam$kotlin_Comparator$0', VOID, VOID, [Comparator, FunctionAdapter]);
initMetadataForClass(IteratorImpl_0, 'IteratorImpl');
initMetadataForClass(ListIteratorImpl_0, 'ListIteratorImpl');
initMetadataForCompanion(Companion_5);
initMetadataForClass(AbstractList, 'AbstractList', VOID, VOID, [AbstractCollection, KtList]);
initMetadataForCompanion(Companion_6);
initMetadataForCompanion(Companion_7);
initMetadataForObject(EmptyList, 'EmptyList', VOID, VOID, [KtList, RandomAccess]);
initMetadataForObject(EmptyIterator, 'EmptyIterator');
initMetadataForClass(ArrayAsCollection, 'ArrayAsCollection', VOID, VOID, [Collection]);
initMetadataForClass(IntIterator, 'IntIterator');
initMetadataForClass(LongIterator, 'LongIterator');
initMetadataForObject(EmptySet, 'EmptySet', VOID, VOID, [KtSet]);
initMetadataForClass(EnumEntriesList, 'EnumEntriesList', VOID, VOID, [KtList, AbstractList]);
initMetadataForClass(Random, 'Random');
initMetadataForObject(Default, 'Default');
initMetadataForCompanion(Companion_8);
initMetadataForClass(XorWowRandom, 'XorWowRandom');
initMetadataForCompanion(Companion_9);
initMetadataForClass(IntProgression, 'IntProgression');
initMetadataForClass(IntRange, 'IntRange');
initMetadataForCompanion(Companion_10);
initMetadataForClass(LongProgression, 'LongProgression');
initMetadataForClass(LongRange, 'LongRange');
initMetadataForClass(IntProgressionIterator, 'IntProgressionIterator');
initMetadataForClass(LongProgressionIterator, 'LongProgressionIterator');
initMetadataForCompanion(Companion_11);
initMetadataForCompanion(Companion_12);
initMetadataForClass(DelimitedRangesSequence$iterator$1);
initMetadataForClass(DelimitedRangesSequence, 'DelimitedRangesSequence');
initMetadataForCompanion(Companion_13);
initMetadataForClass(Failure, 'Failure');
initMetadataForClass(Pair, 'Pair');
initMetadataForClass(Triple, 'Triple');
initMetadataForClass(LessonRule, 'LessonRule');
initMetadataForObject(NumberLessons, 'NumberLessons');
initMetadataForClass(NumCat, 'NumCat');
initMetadataForClass(Keys, 'Keys');
initMetadataForClass(NumOption, 'NumOption');
initMetadataForClass(NumQuestion, 'NumQuestion');
initMetadataForClass(Item, 'Item');
initMetadataForClass(Approx, 'Approx');
initMetadataForObject(NumberQuiz, 'NumberQuiz');
initMetadataForClass(Syl, 'Syl');
initMetadataForCompanion(Companion_14);
initMetadataForClass(Spoken, 'Spoken');
initMetadataForObject(ChineseNumber, 'ChineseNumber');
initMetadataForClass(PySeg, 'PySeg');
initMetadataForObject(Pinyin, 'Pinyin');
//endregion
//region block: init
Unit_instance = new Unit();
Companion_instance_0 = new Companion_0();
Companion_instance_3 = new Companion_3();
Companion_instance_5 = new Companion_5();
Companion_instance_6 = new Companion_6();
Companion_instance_7 = new Companion_7();
EmptyIterator_instance = new EmptyIterator();
Companion_instance_11 = new Companion_11();
Companion_instance_12 = new Companion_12();
Companion_instance_13 = new Companion_13();
//endregion
//region block: exports
export {
  numCats as numCats,
  numRanges as numRanges,
  numBuild as numBuild,
  numNorm as numNorm,
  lesson as lesson,
  culture as culture,
  convert as convert,
  fmt as fmt,
  pySegments as pySegments,
  pyPlain as pyPlain,
  pyAligned as pyAligned,
  pySplit as pySplit,
  pyWithTone as pyWithTone,
  pyToneOf as pyToneOf,
};
//endregion
