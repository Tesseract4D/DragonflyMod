/**
 *
 */
package org.luaj.vm2.luajc;

import org.luaj.vm2.Lua;
import org.luaj.vm2.luajc.BasicBlock;
import org.luaj.vm2.luajc.ProtoInfo;
import org.luaj.vm2.luajc.VarInfo;

public class UpvalInfo {
	ProtoInfo pi;    // where defined
	int       slot;  // where defined
	int       nvars; // number of vars involved
	org.luaj.vm2.luajc.VarInfo var[]; // list of vars
	boolean   rw;    // read-write

	// Upval info representing the implied context containing only the environment.
	public UpvalInfo(ProtoInfo pi) {
		this.pi = pi;
		this.slot = 0;
		this.nvars = 1;
		this.var = new org.luaj.vm2.luajc.VarInfo[] { org.luaj.vm2.luajc.VarInfo.PARAM(0) };
		this.rw = false;
	}

	public UpvalInfo(ProtoInfo pi, int pc, int slot) {
		this.pi = pi;
		this.slot = slot;
		this.nvars = 0;
		this.var = null;
		includeVarAndPosteriorVars(pi.vars[slot][pc]);
		for (int i = 0; i < nvars; i++)
			var[i].allocupvalue = testIsAllocUpvalue(var[i]);
		this.rw = nvars > 1;
	}

	private boolean includeVarAndPosteriorVars(org.luaj.vm2.luajc.VarInfo var) {
		if (var == null || var == org.luaj.vm2.luajc.VarInfo.INVALID)
			return false;
		if (var.upvalue == this)
			return true;
		var.upvalue = this;
		appendVar(var);
		if (isLoopVariable(var))
			return false;
		boolean loopDetected = includePosteriorVarsCheckLoops(var);
		if (loopDetected)
			includePriorVarsIgnoreLoops(var);
		return loopDetected;
	}

	private boolean isLoopVariable(org.luaj.vm2.luajc.VarInfo var) {
		if (var.pc >= 0) {
			switch (Lua.GET_OPCODE(pi.prototype.code[var.pc])) {
			case Lua.OP_TFORLOOP:
			case Lua.OP_FORLOOP:
				return true;
			}
		}
		return false;
	}

	private boolean includePosteriorVarsCheckLoops(org.luaj.vm2.luajc.VarInfo prior) {
		boolean loopDetected = false;
		for (BasicBlock b : pi.blocklist) {
			org.luaj.vm2.luajc.VarInfo v = pi.vars[slot][b.pc1];
			if (v == prior) {
				for (int j = 0, m = b.next != null? b.next.length: 0; j < m; j++) {
					BasicBlock b1 = b.next[j];
					org.luaj.vm2.luajc.VarInfo v1 = pi.vars[slot][b1.pc0];
					if (v1 != prior) {
						loopDetected |= includeVarAndPosteriorVars(v1);
						if (v1.isPhiVar())
							includePriorVarsIgnoreLoops(v1);
					}
				}
			} else {
				for (int pc = b.pc1-1; pc >= b.pc0; pc--) {
					if (pi.vars[slot][pc] == prior) {
						loopDetected |= includeVarAndPosteriorVars(pi.vars[slot][pc+1]);
						break;
					}
				}
			}
		}
		return loopDetected;
	}

	private void includePriorVarsIgnoreLoops(org.luaj.vm2.luajc.VarInfo poster) {
		for (BasicBlock b : pi.blocklist) {
			org.luaj.vm2.luajc.VarInfo v = pi.vars[slot][b.pc0];
			if (v == poster) {
				for (int j = 0, m = b.prev != null? b.prev.length: 0; j < m; j++) {
					BasicBlock b0 = b.prev[j];
					org.luaj.vm2.luajc.VarInfo v0 = pi.vars[slot][b0.pc1];
					if (v0 != poster)
						includeVarAndPosteriorVars(v0);
				}
			} else {
				for (int pc = b.pc0+1; pc <= b.pc1; pc++) {
					if (pi.vars[slot][pc] == poster) {
						includeVarAndPosteriorVars(pi.vars[slot][pc-1]);
						break;
					}
				}
			}
		}
	}

	private void appendVar(org.luaj.vm2.luajc.VarInfo v) {
		if (nvars == 0) {
			var = new org.luaj.vm2.luajc.VarInfo[1];
		} else if (nvars+1 >= var.length) {
			org.luaj.vm2.luajc.VarInfo[] s = var;
			var = new org.luaj.vm2.luajc.VarInfo[nvars*2+1];
			System.arraycopy(s, 0, var, 0, nvars);
		}
		var[nvars++] = v;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(pi.name);
		for (int i = 0; i < nvars; i++) {
			sb.append(i > 0? ",": " ");
			sb.append(String.valueOf(var[i]));
		}
		if (rw)
			sb.append("(rw)");
		return sb.toString();
	}

	private boolean testIsAllocUpvalue(VarInfo v) {
		if (v.pc < 0)
			return true;
		BasicBlock b = pi.blocks[v.pc];
		if (v.pc > b.pc0)
			return pi.vars[slot][v.pc-1].upvalue != this;
		if (b.prev == null) {
			v = pi.params[slot];
			if (v != null && v.upvalue != this)
				return true;
		} else {
			for (BasicBlock element : b.prev) {
				v = pi.vars[slot][element.pc1];
				if (v != null && v.upvalue != this)
					return true;
			}
		}
		return false;
	}

}