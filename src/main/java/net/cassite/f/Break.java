package net.cassite.f;

class Break extends RuntimeException {
    final Object ins;

    Break(Object ins) {
        this.ins = ins;
    }

    Break() {
        this.ins = null;
    }
}
