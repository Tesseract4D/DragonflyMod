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

/**
 * Class to encapsulate behavior of the singleton instance {@code nil}
 * <p>
 * There will be one instance of this class, {@link org.luaj.vm2.LuaValue#NIL}, per Java
 * virtual machine. However, the {@link Varargs} instance {@link org.luaj.vm2.LuaValue#NONE}
 * which is the empty list, is also considered treated as a nil value by
 * default.
 * <p>
 * Although it is possible to test for nil using Java == operator, the
 * recommended approach is to use the method {@link org.luaj.vm2.LuaValue#isnil()} instead.
 * By using that any ambiguities between {@link org.luaj.vm2.LuaValue#NIL} and
 * {@link org.luaj.vm2.LuaValue#NONE} are avoided.
 *
 * @see org.luaj.vm2.LuaValue
 * @see org.luaj.vm2.LuaValue#NIL
 */
public class LuaNil extends org.luaj.vm2.LuaValue {

	static final LuaNil _NIL = new LuaNil();

	public static org.luaj.vm2.LuaValue s_metatable;

	LuaNil() {}

	@Override
	public int type() {
		return org.luaj.vm2.LuaValue.TNIL;
	}

	@Override
	public String toString() {
		return "nil";
	}

	@Override
	public String typename() {
		return "nil";
	}

	@Override
	public String tojstring() {
		return "nil";
	}

	@Override
	public org.luaj.vm2.LuaValue not() {
		return org.luaj.vm2.LuaValue.TRUE;
	}

	@Override
	public boolean toboolean() {
		return false;
	}

	@Override
	public boolean isnil() {
		return true;
	}

	@Override
	public org.luaj.vm2.LuaValue getmetatable() {
		return s_metatable;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof LuaNil;
	}

	@Override
	public org.luaj.vm2.LuaValue checknotnil() {
		return argerror("value");
	}

	@Override
	public boolean isvalidkey() {
		return false;
	}

	// optional argument conversions - nil alwas falls badk to default value
	@Override
	public boolean optboolean(boolean defval) { return defval; }

	@Override
	public org.luaj.vm2.LuaClosure optclosure(LuaClosure defval) { return defval; }

	@Override
	public double optdouble(double defval) { return defval; }

	@Override
	public org.luaj.vm2.LuaFunction optfunction(LuaFunction defval) { return defval; }

	@Override
	public int optint(int defval) { return defval; }

	@Override
	public LuaInteger optinteger(LuaInteger defval) { return defval; }

	@Override
	public long optlong(long defval) { return defval; }

	@Override
	public org.luaj.vm2.LuaNumber optnumber(LuaNumber defval) { return defval; }

	@Override
	public org.luaj.vm2.LuaTable opttable(LuaTable defval) { return defval; }

	@Override
	public org.luaj.vm2.LuaThread optthread(LuaThread defval) { return defval; }

	@Override
	public String optjstring(String defval) { return defval; }

	@Override
	public org.luaj.vm2.LuaString optstring(LuaString defval) { return defval; }

	@Override
	public Object optuserdata(Object defval) { return defval; }

	@Override
	public Object optuserdata(Class c, Object defval) { return defval; }

	@Override
	public org.luaj.vm2.LuaValue optvalue(LuaValue defval) { return defval; }
}
