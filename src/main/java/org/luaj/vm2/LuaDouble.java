/*******************************************************************************
* Copyright (c) 2009-2011 Luaj.org. All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
******************************************************************************/
package org.luaj.vm2;

import org.luaj.vm2.compat.JavaCompat;
import org.luaj.vm2.lib.MathLib;

/**
 * Extension of {@link org.luaj.vm2.LuaNumber} which can hold a Java double as its value.
 * <p>
 * These instance are not instantiated directly by clients, but indirectly
 * via the static functions {@link org.luaj.vm2.LuaValue#valueOf(int)} or {@link org.luaj.vm2.LuaValue#valueOf(double)}
 * functions.  This ensures that values which can be represented as int
 * are wrapped in {@link LuaInteger} instead of {@link LuaDouble}.
 * <p>
 * Almost all API's implemented in LuaDouble are defined and documented in {@link org.luaj.vm2.LuaValue}.
 * <p>
 * However the constants {@link #NAN}, {@link #NEGNAN}, {@link #POSINF}, {@link #NEGINF},
 * {@link #JSTR_NAN}, {@link #JSTR_NEGNAN}, {@link #JSTR_POSINF}, and {@link #JSTR_NEGINF} may be useful
 * when dealing with Nan or Infinite values.
 * <p>
 * LuaDouble also defines functions for handling the unique math rules of lua devision and modulo in
 * <ul>
 * <li>{@link #ddiv(double, double)}</li>
 * <li>{@link #ddiv_d(double, double)}</li>
 * <li>{@link #dmod(double, double)}</li>
 * <li>{@link #dmod_d(double, double)}</li>
 * </ul>
 * <p>
 * @see org.luaj.vm2.LuaValue
 * @see org.luaj.vm2.LuaNumber
 * @see LuaInteger
 * @see org.luaj.vm2.LuaValue#valueOf(int)
 * @see org.luaj.vm2.LuaValue#valueOf(double)
 */
public class LuaDouble extends org.luaj.vm2.LuaNumber {

	/** Constant LuaDouble representing NaN (not a number) */
	public static final LuaDouble NAN    = new LuaDouble( Double.NaN );

	/** Constant LuaDouble representing negative NaN (not a number) */
	public static final LuaDouble NEGNAN    = new LuaDouble( -Double.NaN );

	/** Constant LuaDouble representing positive infinity */
	public static final LuaDouble POSINF = new LuaDouble( Double.POSITIVE_INFINITY );

	/** Constant LuaDouble representing negative infinity */
	public static final LuaDouble NEGINF = new LuaDouble( Double.NEGATIVE_INFINITY );

	/** Constant String representation for NaN (not a number), "nan" */
	public static final String JSTR_NAN    = "nan";

	/** Constant String representation for negative NaN (not a number), "-nan" */
	public static final String JSTR_NEGNAN    = "-nan";

	/** Constant String representation for positive infinity, "inf" */
	public static final String JSTR_POSINF = "inf";

	/** Constant String representation for negative infinity, "-inf" */
	public static final String JSTR_NEGINF = "-inf";

	/** The value being held by this instance. */
	final double v;

	public static org.luaj.vm2.LuaNumber valueOf(double d) {
		int id = (int) d;
		return d==id? (org.luaj.vm2.LuaNumber) LuaInteger.valueOf(id): (org.luaj.vm2.LuaNumber) new LuaDouble(d);
	}

	/** Don't allow ints to be boxed by DoubleValues  */
	private LuaDouble(double d) {
		this.v = d;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(v + 1);
		return ((int)(l>>32)) + (int) l;
	}

	public boolean islong() {
		return v == (long) v;
	}

	public byte    tobyte()        { return (byte) (long) v; }
	public char    tochar()        { return (char) (long) v; }
	public double  todouble()      { return v; }
	public float   tofloat()       { return (float) v; }
	public int     toint()         { return (int) (long) v; }
	public long    tolong()        { return (long) v; }
	public short   toshort()       { return (short) (long) v; }

	public double      optdouble(double defval)        { return v; }
	public int         optint(int defval)              { return (int) (long) v;  }
	public LuaInteger  optinteger(LuaInteger defval)   { return LuaInteger.valueOf((int) (long)v); }
	public long        optlong(long defval)            { return (long) v; }

	public LuaInteger  checkinteger()                  { return LuaInteger.valueOf( (int) (long) v ); }

	// unary operators
	public org.luaj.vm2.LuaValue neg() { return valueOf(-v); }

	// object equality, used for key comparison
	public boolean equals(Object o) { return o instanceof LuaDouble? ((LuaDouble)o).v == v: false; }

	// equality w/ metatable processing
	public org.luaj.vm2.LuaValue eq(org.luaj.vm2.LuaValue val )        { return val.raweq(v)? TRUE: FALSE; }
	public boolean eq_b( org.luaj.vm2.LuaValue val )       { return val.raweq(v); }

	// equality w/o metatable processing
	public boolean raweq( org.luaj.vm2.LuaValue val )      { return val.raweq(v); }
	public boolean raweq( double val )        { return v == val; }
	public boolean raweq( int val )           { return v == val; }

	// basic binary arithmetic
	public org.luaj.vm2.LuaValue add(org.luaj.vm2.LuaValue rhs )        { return rhs.add(v); }
	public org.luaj.vm2.LuaValue add(double lhs )     { return LuaDouble.valueOf(lhs + v); }
	public org.luaj.vm2.LuaValue sub(org.luaj.vm2.LuaValue rhs )        { return rhs.subFrom(v); }
	public org.luaj.vm2.LuaValue sub(double rhs )        { return LuaDouble.valueOf(v - rhs); }
	public org.luaj.vm2.LuaValue sub(int rhs )        { return LuaDouble.valueOf(v - rhs); }
	public org.luaj.vm2.LuaValue subFrom(double lhs )   { return LuaDouble.valueOf(lhs - v); }
	public org.luaj.vm2.LuaValue mul(org.luaj.vm2.LuaValue rhs )        { return rhs.mul(v); }
	public org.luaj.vm2.LuaValue mul(double lhs )   { return LuaDouble.valueOf(lhs * v); }
	public org.luaj.vm2.LuaValue mul(int lhs )      { return LuaDouble.valueOf(lhs * v); }
	public org.luaj.vm2.LuaValue pow(org.luaj.vm2.LuaValue rhs )        { return rhs.powWith(v); }
	public org.luaj.vm2.LuaValue pow(double rhs )        { return MathLib.dpow(v,rhs); }
	public org.luaj.vm2.LuaValue pow(int rhs )        { return MathLib.dpow(v,rhs); }
	public org.luaj.vm2.LuaValue powWith(double lhs )   { return MathLib.dpow(lhs,v); }
	public org.luaj.vm2.LuaValue powWith(int lhs )      { return MathLib.dpow(lhs,v); }
	public org.luaj.vm2.LuaValue div(org.luaj.vm2.LuaValue rhs )        { return rhs.divInto(v); }
	public org.luaj.vm2.LuaValue div(double rhs )        { return LuaDouble.ddiv(v,rhs); }
	public org.luaj.vm2.LuaValue div(int rhs )        { return LuaDouble.ddiv(v,rhs); }
	public org.luaj.vm2.LuaValue divInto(double lhs )   { return LuaDouble.ddiv(lhs,v); }
	public org.luaj.vm2.LuaValue mod(org.luaj.vm2.LuaValue rhs )        { return rhs.modFrom(v); }
	public org.luaj.vm2.LuaValue mod(double rhs )        { return LuaDouble.dmod(v,rhs); }
	public org.luaj.vm2.LuaValue mod(int rhs )        { return LuaDouble.dmod(v,rhs); }
	public org.luaj.vm2.LuaValue modFrom(double lhs )   { return LuaDouble.dmod(lhs,v); }


	/** Divide two double numbers according to lua math, and return a {@link org.luaj.vm2.LuaValue} result.
	 * @param lhs Left-hand-side of the division.
	 * @param rhs Right-hand-side of the division.
	 * @return {@link org.luaj.vm2.LuaValue} for the result of the division,
	 * taking into account positive and negiative infinity, and Nan
	 * @see #ddiv_d(double, double)
	 */
	public static org.luaj.vm2.LuaValue ddiv(double lhs, double rhs) {
		return rhs!=0? valueOf( lhs / rhs ): lhs>0? POSINF: lhs==0? NAN: NEGINF;
	}

	/** Divide two double numbers according to lua math, and return a double result.
	 * @param lhs Left-hand-side of the division.
	 * @param rhs Right-hand-side of the division.
	 * @return Value of the division, taking into account positive and negative infinity, and Nan
	 * @see #ddiv(double, double)
	 */
	public static double ddiv_d(double lhs, double rhs) {
		return rhs!=0? lhs / rhs: lhs>0? Double.POSITIVE_INFINITY: lhs==0? Double.NaN: Double.NEGATIVE_INFINITY;
	}

	/** Take modulo double numbers according to lua math, and return a {@link org.luaj.vm2.LuaValue} result.
	 * @param lhs Left-hand-side of the modulo.
	 * @param rhs Right-hand-side of the modulo.
	 * @return {@link org.luaj.vm2.LuaValue} for the result of the modulo,
	 * using lua's rules for modulo
	 * @see #dmod_d(double, double)
	 */
	public static org.luaj.vm2.LuaValue dmod(double lhs, double rhs) {
		if (rhs == 0 || lhs == Double.POSITIVE_INFINITY || lhs == Double.NEGATIVE_INFINITY) return NAN;
		if (rhs == Double.POSITIVE_INFINITY) {
			return lhs < 0 ? POSINF : valueOf(lhs);
		}
		if (rhs == Double.NEGATIVE_INFINITY) {
			return lhs > 0 ? NEGINF : valueOf(lhs);
		}
		return valueOf( lhs-rhs*Math.floor(lhs/rhs) );
	}

	/** Take modulo for double numbers according to lua math, and return a double result.
	 * @param lhs Left-hand-side of the modulo.
	 * @param rhs Right-hand-side of the modulo.
	 * @return double value for the result of the modulo,
	 * using lua's rules for modulo
	 * @see #dmod(double, double)
	 */
	public static double dmod_d(double lhs, double rhs) {
		if (rhs == 0 || lhs == Double.POSITIVE_INFINITY || lhs == Double.NEGATIVE_INFINITY) return Double.NaN;
		if (rhs == Double.POSITIVE_INFINITY) {
			return lhs < 0 ? Double.POSITIVE_INFINITY : lhs;
		}
		if (rhs == Double.NEGATIVE_INFINITY) {
			return lhs > 0 ? Double.NEGATIVE_INFINITY : lhs;
		}
		return lhs-rhs*Math.floor(lhs/rhs);
	}

	// relational operators
	public org.luaj.vm2.LuaValue lt(org.luaj.vm2.LuaValue rhs )         { return rhs instanceof org.luaj.vm2.LuaNumber ? (rhs.gt_b(v)? TRUE: FALSE) : super.lt(rhs); }
	public org.luaj.vm2.LuaValue lt(double rhs )      { return v < rhs? TRUE: FALSE; }
	public org.luaj.vm2.LuaValue lt(int rhs )         { return v < rhs? TRUE: FALSE; }
	public boolean lt_b( org.luaj.vm2.LuaValue rhs )       { return rhs instanceof org.luaj.vm2.LuaNumber ? rhs.gt_b(v) : super.lt_b(rhs); }
	public boolean lt_b( int rhs )         { return v < rhs; }
	public boolean lt_b( double rhs )      { return v < rhs; }
	public org.luaj.vm2.LuaValue lteq(org.luaj.vm2.LuaValue rhs )       { return rhs instanceof org.luaj.vm2.LuaNumber ? (rhs.gteq_b(v)? TRUE: FALSE) : super.lteq(rhs); }
	public org.luaj.vm2.LuaValue lteq(double rhs )    { return v <= rhs? TRUE: FALSE; }
	public org.luaj.vm2.LuaValue lteq(int rhs )       { return v <= rhs? TRUE: FALSE; }
	public boolean lteq_b( org.luaj.vm2.LuaValue rhs )     { return rhs instanceof org.luaj.vm2.LuaNumber ? rhs.gteq_b(v) : super.lteq_b(rhs); }
	public boolean lteq_b( int rhs )       { return v <= rhs; }
	public boolean lteq_b( double rhs )    { return v <= rhs; }
	public org.luaj.vm2.LuaValue gt(org.luaj.vm2.LuaValue rhs )         { return rhs instanceof org.luaj.vm2.LuaNumber ? (rhs.lt_b(v)? TRUE: FALSE) : super.gt(rhs); }
	public org.luaj.vm2.LuaValue gt(double rhs )      { return v > rhs? TRUE: FALSE; }
	public org.luaj.vm2.LuaValue gt(int rhs )         { return v > rhs? TRUE: FALSE; }
	public boolean gt_b( org.luaj.vm2.LuaValue rhs )       { return rhs instanceof org.luaj.vm2.LuaNumber ? rhs.lt_b(v) : super.gt_b(rhs); }
	public boolean gt_b( int rhs )         { return v > rhs; }
	public boolean gt_b( double rhs )      { return v > rhs; }
	public org.luaj.vm2.LuaValue gteq(org.luaj.vm2.LuaValue rhs )       { return rhs instanceof org.luaj.vm2.LuaNumber ? (rhs.lteq_b(v)? TRUE: FALSE) : super.gteq(rhs); }
	public org.luaj.vm2.LuaValue gteq(double rhs )    { return v >= rhs? TRUE: FALSE; }
	public org.luaj.vm2.LuaValue gteq(int rhs )       { return v >= rhs? TRUE: FALSE; }
	public boolean gteq_b( org.luaj.vm2.LuaValue rhs )     { return rhs instanceof org.luaj.vm2.LuaNumber ? rhs.lteq_b(v) : super.gteq_b(rhs); }
	public boolean gteq_b( int rhs )       { return v >= rhs; }
	public boolean gteq_b( double rhs )    { return v >= rhs; }

	// string comparison
	public int strcmp( org.luaj.vm2.LuaString rhs )      { typerror("attempt to compare number with string"); return 0; }

	public String tojstring() {
		if ( v == 0.0 ) // never occurs on J2ME
			return (JavaCompat.INSTANCE.doubleToRawLongBits(v)<0? "-0": "0");
		long l = (long) v;
		if ( l == v )
			return Long.toString(l);
		if ( Double.isNaN(v) )
			return (JavaCompat.INSTANCE.doubleToRawLongBits(v)<0? JSTR_NEGNAN: JSTR_NAN);
		if ( Double.isInfinite(v) )
			return (v<0? JSTR_NEGINF: JSTR_POSINF);
		return Float.toString((float)v);
	}

	public org.luaj.vm2.LuaString strvalue() {
		return org.luaj.vm2.LuaString.valueOf(tojstring());
	}

	public org.luaj.vm2.LuaString optstring(org.luaj.vm2.LuaString defval) {
		return org.luaj.vm2.LuaString.valueOf(tojstring());
	}

	public org.luaj.vm2.LuaValue tostring() {
		return org.luaj.vm2.LuaString.valueOf(tojstring());
	}

	public String optjstring(String defval) {
		return tojstring();
	}

	public org.luaj.vm2.LuaNumber optnumber(org.luaj.vm2.LuaNumber defval) {
		return this;
	}

	public boolean isnumber() {
		return true;
	}

	public boolean isstring() {
		return true;
	}

	public LuaValue tonumber() {
		return this;
	}
	public int checkint()                { return (int) (long) v; }
	public long checklong()              { return (long) v; }
	public LuaNumber checknumber()       { return this; }
	public double checkdouble()          { return v; }

	public String checkjstring() {
		return tojstring();
	}
	public org.luaj.vm2.LuaString checkstring() {
		return LuaString.valueOf(tojstring());
	}

	public boolean isvalidkey() {
		return !Double.isNaN(v);
	}
}